/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.mq.CBOrderProducer;
import me.zhengjie.service.*;
import me.zhengjie.support.GZTOSupport;
import me.zhengjie.support.pdd.*;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.utils.constant.MsgType;
import me.zhengjie.utils.enums.BooleanEnum;
import me.zhengjie.utils.enums.CBOrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

import me.zhengjie.support.pdd.PddSyncTrackRequest.*;

/**
 * @website https://el-admin.vip
 * @description 服务实现
 * @author wangm
 * @date 2021-06-10
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class PddOrderServiceImpl implements PddOrderService {

    @Autowired
    private ShopTokenService shopTokenService;

    @Autowired
    private PlatformService platformService;

    @Autowired
    private PddMailMarkService pddMailMarkService;

    @Autowired
    private PullOrderLogService pullOrderLogService;

    @Autowired
    private PDDSupport pddSupport;

    @Autowired
    private GZTOSupport gztoSupport;

    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private CBOrderProducer cbOrderProducer;

    @Autowired
    private OrderLogService orderLogService;

    @Autowired
    private CombinationOrderService combinationOrderService;

    @Autowired
    private CombSplitService combSplitService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private ClearCompanyInfoService clearCompanyInfoService;

    @Value("${pdd.pddUserId}")
    private String pddUserId;

    @Autowired
    private PddCloudPrintDataService pddCloudPrintDataService;

    @Override
    public JSONObject printOrder(String dataJsonText) throws Exception {
        JSONObject dataJson = new JSONObject(dataJsonText);
        JSONArray trade_order_info_dtos = dataJson.getJSONArray("trade_order_info_dtos");
        if (trade_order_info_dtos == null) {
            String msg = "没有请求面单信息(No trade_order_info_dtos)";
            throw new BadRequestException(msg);
        }
        String userId = trade_order_info_dtos.getJSONObject(0).getStr("user_id");
        if (StringUtil.isEmpty(userId)) {
            throw new BadRequestException("请求面单信息的user_id是必填项");
        }
        PddGetMailNo2Request request=new PddGetMailNo2Request();
        request.setParam(dataJsonText);
        request.setShopCode(pddUserId);//富立物流在拼多多店铺的id
        try {
            PddCommonResponse<JSONObject> resp=pddSupport.request(JSONObject.class,request);
            if (resp.getCode()==200){
                return resp.getData();
            }
            throw new BadRequestException(resp.getMsg());
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void pddPullOrder() {
        // TODO: 2021/11/29 拼多多拉单
        Platform platform=platformService.findByCode("PDD");
        if (platform==null)
            return;
        List<ShopToken> shopTokenList = shopTokenService.queryEnableTokenByPlatId(platform.getId());
        for (int i = 0; i < shopTokenList.size(); i++) {
            ShopToken shopToken = shopTokenList.get(i);
            if (!StringUtil.equals("1", shopToken.getPullOrderAble())) {
                // 没开启拉单
                continue;
            }
            ShopInfo shopInfo=shopInfoService.findById(shopToken.getShopId());
            PullOrderLog pullTime = pullOrderLogService.getPullTime(shopToken.getShopId());
            if (pullTime == null) {
                // 新商家一次都还没拉单过，那么拉单时间设置到前一天开始
                // 注意，如果是从老系统切换过来的商家，那么数据库手动新增一条结束时间到当天16点05分拉单成功数据
                // 这样老商家就会从当天16点开始拉单，达到切换效果
                pullTime = new PullOrderLog();
                pullTime.setPageNo(1);
                pullTime.setPageSize(100);
                pullTime.setEndTime(new Timestamp(System.currentTimeMillis()));
                pullTime.setStartTime(new Timestamp(pullTime.getEndTime().getTime() - 3600*1000*24));
            }
            pullTime.setEndTime(new Timestamp(System.currentTimeMillis()-3600/2*1000L));//拉取半小时前的订单，给用户留下修改收货地址的时间
            if (pullTime.getPageNo()==0){
                pullTime.setPageNo(1);
            }
            try {
                pullOrder(shopToken, shopInfo, pullTime);
            } catch (Exception e) {
                e.printStackTrace();
                if (StringUtil.contains(e.getMessage(),"获取订单失败：业务服务错误"))
                    i--;
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void pullOrderByHours(Integer hours) {
        Platform platform=platformService.findByCode("PDD");
        if (platform==null)
            return;
        List<ShopToken> shopTokenList = shopTokenService.queryEnableTokenByPlatId(platform.getId());
        for (ShopToken shopToken : shopTokenList) {
            if (!StringUtil.equals("1", shopToken.getPullOrderAble())) {
                // 没开启拉单
                continue;
            }
            ShopInfo shopInfo=shopInfoService.findById(shopToken.getShopId());
            Date endTime=new Date(System.currentTimeMillis()-3600/2*1000L);
            Date startTime=new Date(endTime.getTime() - hours*3600*1000L);
            if (endTime.getTime()-startTime.getTime()>24*3600*1000L){
                //拼多多只能一次拉取24小时的待发货订单,超过24小时则需要分开拉单
                long day=(endTime.getTime()-startTime.getTime())%(24*3600*1000L)==0?
                        ((endTime.getTime()-startTime.getTime())/(24*3600*1000L)):
                        ((endTime.getTime()-startTime.getTime())/(24*3600*1000L)+1);
                for (int i = 1; i <= day; i++) {
                    Date endC;
                    if (i==day)
                        endC=endTime;
                    else
                        endC=new Date(startTime.getTime()+i*24*3600*1000L);
                    Date startC=new Date(startTime.getTime()+(i-1)*24*3600*1000L);
                    pullOrderByTimeRange(startC,endC,shopInfo);
                }
            }else
                pullOrderByTimeRange(startTime,endTime,shopInfo);
        }
    }

    private void pullOrder(ShopToken shopToken, ShopInfo shopInfo, PullOrderLog pullTime) throws Exception{
        //定义参数
        Date startTime = new Date(pullTime.getStartTime().getTime());
        Date endTime = new Date(pullTime.getEndTime().getTime());
        if (endTime.getTime()-startTime.getTime()>24*3600*1000L){
            //拼多多只能一次拉取24小时的待发货订单,超过24小时则需要分开拉单
            long day=(endTime.getTime()-startTime.getTime())%(24*3600*1000L)==0?
                    ((endTime.getTime()-startTime.getTime())/(24*3600*1000L)):
                    ((endTime.getTime()-startTime.getTime())/(24*3600*1000L)+1);
            for (int i = 1; i <= day; i++) {
                Date endC;
                if (i==day)
                    endC=endTime;
                else
                    endC=new Date(startTime.getTime()+i*24*3600*1000L);
                Date startC=new Date(startTime.getTime()+(i-1)*24*3600*1000L);
                pullOrderByTimeRange(startC,endC,shopInfo);
            }
        }else{
            int pageNo = pullTime.getPageNo();
            int pageSize = pullTime.getPageSize();
            PddPullOrderRequest request = new PddPullOrderRequest();
            //参数存入 request
            request.setEndTime(endTime);
            request.setStartTime(startTime);
            request.setPageNo(pageNo);
            request.setPageSize(pageSize);
            request.setShopCode(shopToken.getPlatformShopId());
            request.setlShopId(shopInfo.getId());
            request.setCustId(shopInfo.getCustId());
            Integer totalNum = null;
            try {
                log.info("拼多多拉单：请求参数：{}", request);
                // 第一次先查询本次有多少订单

                PddCommonResponse<PddPullOrderResponse> response = pddSupport.request(PddPullOrderResponse.class,request);
                log.info("拼多多拉单：返回：{}", response);
                if (response.isSuccess()) {
                    // 拉取成功
                    totalNum = response.getData().getTotalCount();
                    if (totalNum <= pageSize) {
                        //没有下一页
                        response.getData().setlShopId(shopInfo.getId());
                        response.getData().setCustId(shopInfo.getCustId());
                        PullOrderLog log = new PullOrderLog(
                                shopInfo.getId(),
                                pullTime.getStartTime(),
                                pullTime.getEndTime(),
                                pageNo,
                                pageSize,
                                "F",
                                totalNum.toString(),
                                "T",
                                "成功,"+new JSONObject(response).toString()
                        );
                        try {
                            pullOrderLogService.create(log);
                        }catch (Exception e){
                            log.setResMsg("成功");
                            pullOrderLogService.create(log);
                        }
                        handleOrder(response);
                    } else {
                        // 循环分页拉取数据
                        // 计算总页数
                        Integer totalPage = response.getData().getTotalPage();
                        for (int i = pageNo; i <= totalPage; i++) {
                            request.setPageNo(i);
                            request.setPageSize(pageSize);

                            // 发送MQ消息
                            String jsonString = JSON.toJSONString(request);
                            cbOrderProducer.send(
                                    MsgType.CB_ORDER_PULL_PDD,
                                    jsonString,
                                    null
                            );
                            //pullOrderByPage(jsonString);
                        }
                    }
                }
            } catch (Exception e) {
                // catch住异常信息并保存到日志表，继续循环下一个店铺
                PullOrderLog log = new PullOrderLog(
                        shopToken.getShopId(),
                        pullTime.getStartTime(),
                        pullTime.getEndTime(),
                        1,
                        0,
                        "E",
                        "0",
                        "F",
                        StringUtil.exceptionStackInfoToString(e)
                );
                pullOrderLogService.create(log);
                e.printStackTrace();
                throw e;
            }
        }
    }

    private synchronized void handleOrder(PddCommonResponse<PddPullOrderResponse> response) {
        log.info("拼多多订单分页拉单开始处理，参数：{}", response);
        List<PddOrder> list = response.getData().getOrderList();
        for (PddOrder pddOrder : list) {
            pddOrder.setlShopId(response.getData().getlShopId());
            pddOrder.setCustId(response.getData().getCustId());
            String jsonString = JSON.toJSONString(pddOrder);
            log.info("拼多多订单拉单单个订单发送消息队列：{}", jsonString);
            // 保存订单通知
            cbOrderProducer.send(
                    MsgType.CB_ORDER_200_PDD,
                    jsonString,
                    pddOrder.getOrderSn()
            );
            //createOrder(jsonString);
        }
        log.info("拼多多订单分页拉单开始处理完成");
    }


    @Override
    public synchronized void confirmOrder(CrossBorderOrder order) throws Exception {
        // TODO: 2021/11/29 拼多多接单回传
        log.info("拼多多开始回传接单：{}", order.getOrderNo());
        if (order.getStatus().intValue() != CBOrderStatusEnum.STATUS_200.getCode().intValue()) {
            throw new BadRequestException("当前状态不允许接单回传：" + order.getId());
        }

        try {
            // 开始回传接单
            ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());
            if (shopToken == null)
                throw new BadRequestException("店铺未配置授权信息，请先配置:" + order.getShopId());
            PddSyncTrackRequest request = new PddSyncTrackRequest();
            PddCommonResponse<PddSyncTrackResponse>response=syncRequest("CTRATE",order,request);
            if (response.isSuccess()&&StringUtil.equals("1",response.getData().getIsSuccess())) {
                // 回传接单成功,更改订单状态
                order.setStatus(CBOrderStatusEnum.STATUS_215.getCode());
                order.setReceivedBackTime(new Timestamp(System.currentTimeMillis()));
                crossBorderOrderService.update(order);
                // 保存订单日志
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_215.getCode()),
                        BooleanEnum.SUCCESS.getCode(),
                        BooleanEnum.SUCCESS.getDescription()
                );
                orderLog.setReqMsg(request.toString());
                orderLog.setResMsg(response.toString());
                orderLogService.create(orderLog);
                // 清关开始
                cbOrderProducer.send(
                        MsgType.CB_ORDER_220,
                        String.valueOf(String.valueOf(order.getId())),
                        order.getOrderNo()
                );
            }else {
                // 回传接单失败
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_215.getCode()),
                        BooleanEnum.FAIL.getCode(),
                        response.getMsg()
                );
                orderLog.setReqMsg(request.toString());
                orderLog.setResMsg(response.toString());
                orderLogService.create(orderLog);
                throw new BadRequestException(response.getMsg());
            }
        }catch (Exception e) {
            // 回传接单失败
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_215.getCode()),
                    BooleanEnum.FAIL.getCode(),
                    e.getMessage()
            );
            orderLogService.create(orderLog);
            e.printStackTrace();
            throw e;
        }
    }

    //工具重推接单，只重推，不改变任何订单数据
    @Override
    public void confirmOrderByTools(String orderNo) throws Exception {
        CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(orderNo);
        if (order == null)
            throw new BadRequestException("订单不存在：" + orderNo);
        try {
            // 开始回传接单
            ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());
            if (shopToken == null)
                throw new BadRequestException("店铺未配置授权信息，请先配置:" + order.getShopId());
            PddSyncTrackRequest request = new PddSyncTrackRequest();
            PddCommonResponse<PddSyncTrackResponse>response=syncRequest("CTRATE",order,request);
            if (!(response.isSuccess()&&StringUtil.equals(response.getData().getIsSuccess(),"1"))){
                throw new BadRequestException(response.getMsg());
            }
        }catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // 清关开始回传
    public synchronized void confirmClearStart(String orderId) throws Exception {
        CrossBorderOrder order = crossBorderOrderService.queryById(Long.valueOf(orderId));
        if (order == null) {
            throw new BadRequestException("订单不存在：" + orderId);
        }
    }

    @Override
    public synchronized void confirmClearStart(CrossBorderOrder order) throws Exception {
        // TODO: 2021/11/29 拼多多清关开始回传
        try {

            log.info("拼多多开始回传清关开始：{}", order.getOrderNo());
//            if (order.getClearStartBackTime() != null) {
//                throw new BadRequestException("订单已回传过清关开始：" + order.getOrderNo());
//            }

            ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());
            if (shopToken == null) {
                throw new BadRequestException("店铺未配置授权信息：" + order.getOrderNo());
            }
            PddSyncTrackRequest request = new PddSyncTrackRequest();
            PddCommonResponse<PddSyncTrackResponse>response=syncRequest("ORDER_DECLARE_START_SUCC",order,request);
            if (response.isSuccess()&&StringUtil.equals("1",response.getData().getIsSuccess())) {
                // 回传成功
                if (order.getStatus().intValue() == CBOrderStatusEnum.STATUS_220.getCode().intValue()) {
                    // 如果当前状态为清关开始，回传成功后则可以改状态为清关开始回传，其他状态则不能更改状态
                    order.setStatus(CBOrderStatusEnum.STATUS_225.getCode());
                }
                order.setClearStartBackTime(new Timestamp(System.currentTimeMillis()));
                crossBorderOrderService.update(order);
                // 保存订单日志
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_225.getCode()),
                        BooleanEnum.SUCCESS.getCode(),
                        BooleanEnum.SUCCESS.getDescription()
                );
                orderLog.setReqMsg(request.toString());
                orderLog.setResMsg(response.toString());
                orderLogService.create(orderLog);

            }else {
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_225.getCode()),
                        BooleanEnum.FAIL.getCode(),
                        response.getMsg()
                );
                orderLog.setReqMsg(request.toString());
                orderLog.setResMsg(response.toString());
                orderLogService.create(orderLog);
                // 出现状态缺失，重推一下缺失的状态
                if (StringUtil.contains(response.getMsg(), "当前状态是 支付单申报成功")) {
                    // 重推接单
                    confirmOrderByTools(order.getOrderNo());
                }
                throw new BadRequestException(response.getMsg());
            }

        }catch (Exception e) {
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_225.getCode()),
                    BooleanEnum.FAIL.getCode(),
                    e.getMessage()
            );
            orderLogService.create(orderLog);
            e.printStackTrace();
            throw e;
        }
    }

    private synchronized PddCommonResponse<PddSyncTrackResponse> syncRequest(String statusCode,CrossBorderOrder order,PddSyncTrackRequest request) throws Exception{
        TrackBody body=new TrackBody();
        body.setCreateTime(DateUtils.now());
        body.setLogisticsCode("ZTO");
        body.setLogisticsNo(order.getLogisticsNo());
        body.setPlatformOrderNo(order.getOrderNo());
        body.setStatusCode(statusCode);
        request.setOrderDeclare(body);

        log.info("拼多多回传接单,单号：{},请求参数：{}", order.getOrderNo(), new JSONObject(request).toString());
        PddCommonResponse<PddSyncTrackResponse>response=pddSupport.request(PddSyncTrackResponse.class,request);
        log.info("拼多多回传接单,单号：{},返回：{}", order.getOrderNo(), response);
        return response;
    }
    // 清关完成回传
    public synchronized void confirmClearSuccess(String orderId) throws Exception {
        CrossBorderOrder order = crossBorderOrderService.queryById(Long.valueOf(orderId));
        if (order == null) {
            throw new BadRequestException("订单不存在：" + orderId);
        }
        confirmClearSuccess(order);
    }

    @Override
    public synchronized void confirmClearSuccess(CrossBorderOrder order) throws Exception {
        // TODO: 2021/11/29 拼多多清关完成回传
        try {
            ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());
            if (shopToken == null) {
                throw new BadRequestException("店铺未配置授权信息：" + order.getOrderNo());
            }
            log.info("拼多多开始回传清关完成,单号：{}", order.getOrderNo());
            PddSyncTrackRequest request = new PddSyncTrackRequest();
            PddCommonResponse<PddSyncTrackResponse>response= syncRequest("ORDER_DECLARE_DOC_PASS",order,request);
            if (response.isSuccess()&&StringUtil.equals("1",response.getData().getIsSuccess())) {
                // 回传成功
                order.setClearSuccessBackTime(new Timestamp(System.currentTimeMillis()));
                order.setStatus(CBOrderStatusEnum.STATUS_235.getCode());
                crossBorderOrderService.update(order);
                // 保存订单日志
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_235.getCode()),
                        BooleanEnum.SUCCESS.getCode(),
                        BooleanEnum.SUCCESS.getDescription()
                );
                orderLog.setReqMsg(request.toString());
                orderLog.setResMsg(response.toString());
                orderLogService.create(orderLog);
            }else {
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_235.getCode()),
                        BooleanEnum.FAIL.getCode(),
                        response.getMsg()
                );
                orderLog.setReqMsg(request.toString());
                orderLog.setResMsg(response.toString());
                orderLogService.create(orderLog);
                // 出现状态缺失，重推一下缺失的状态
                if (StringUtil.contains(response.getMsg(), "当前状态是 服务商接单成功")) {
                    // 重推清关开始
                    confirmClearStart(order);
                }
                throw new BadRequestException(response.getMsg());
            }

        }catch (Exception e) {
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_235.getCode()),
                    BooleanEnum.FAIL.getCode(),
                    e.getMessage()
            );
            orderLogService.create(orderLog);
            e.printStackTrace();
            throw e;
        }
    }

    // 回传出库，同时回传打包，等跟富勒系统对接完成之后就能分开回传了
    @Override
    public synchronized void confirmDeliver(String orderId) throws Exception {
        CrossBorderOrder order = crossBorderOrderService.queryById(Long.valueOf(orderId));
        if (order==null)
            throw new BadRequestException("订单id:"+orderId+"不存在");
        for (int i = 0; i < 50; i++) {
            try {
                confirmDeliver(order);
                break;
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage(),e);
                if (!StringUtil.contains(e.getMessage(),"获取订单失败：业务服务错误")){
                    throw new BadRequestException(e.getMessage());
                }else if (i > 48)
                    throw new BadRequestException(e.getMessage());
            }
        }
    }

    @Override
    public synchronized void confirmDeliver(CrossBorderOrder order) throws Exception {
        // TODO: 2021/11/29 拼多多出库回传
        try {
            /*if (order.getStatus().intValue() != CBOrderStatusEnum.STATUS_240.getCode().intValue()) {
                throw new BadRequestException("此状态不能回传出库：" + order.getOrderNo());
            }*/
            ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());
            if (shopToken == null) {
                throw new BadRequestException("店铺未配置授权信息：" + order.getOrderNo());
            }
            if (StringUtil.isNotBlank(order.getLpCode())){
                String pddStatus = getOrderStatus(order.getOrderNo(),order.getPlatformShopId());
                if(StringUtils.contains(pddStatus,"t")){
                    //机器人报警，邮件容易被当成垃圾邮件
                    throw new BadRequestException("订单在退款流程");
                }else if (!StringUtil.equals("1",pddStatus)){
                    //平台已发货，目前无处理
                    order.setStatus(CBOrderStatusEnum.STATUS_245.getCode());
                    crossBorderOrderService.update(order);
                    return;
                }
            }
            log.info("拼多多开始回传出库,单号：{}", order.getOrderNo());
            PddLogisticsSendRequest sendRequest=new PddLogisticsSendRequest();
            PddOrder pddOrder=new PddOrder();
            pddOrder.setOrderSn(order.getOrderNo());
            pddOrder.setTrackingNumber(order.getLogisticsNo());
            if (StringUtil.isBlank(order.getLogisticsCode()))
                order.setLogisticsCode("ZTO");
            switch (order.getLogisticsCode()){
                case "330296T004":
                case "EMS":
                    pddOrder.setLogisticsId(240L);
                    break;
                case "11089609XE":
                case "JD":
                    pddOrder.setLogisticsId(120L);
                    break;
                default:
                    pddOrder.setLogisticsId(371L);//发货的快递公司代码
                    break;
            }
            sendRequest.setPddOrder(pddOrder);
            sendRequest.setShopCode(order.getPlatformShopId());
            PddCommonResponse<PddLogisticsSendResponse>sendResponse=pddSupport.request(PddLogisticsSendResponse.class,sendRequest);
            if (!sendResponse.isSuccess()){
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_245.getCode()),
                        BooleanEnum.FAIL.getCode(),
                        sendResponse.getMsg()
                );
                orderLog.setReqMsg(sendRequest.toString());
                orderLog.setResMsg(sendResponse.toString());
                orderLogService.create(orderLog);
                throw new BadRequestException(sendResponse.getMsg());
            }
            PddSyncTrackRequest request=new PddSyncTrackRequest();
            PddCommonResponse<PddSyncTrackResponse>response=syncRequest("WAREHOUSE_DEPARTURE",order,request);
            if (response.isSuccess()&&StringUtil.equals("1",response.getData().getIsSuccess())) {
                // 出库回传成功
                order.setDeliverTime(new Timestamp(System.currentTimeMillis()));
                order.setStatus(CBOrderStatusEnum.STATUS_245.getCode());
                crossBorderOrderService.update(order);
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_245.getCode()),
                        BooleanEnum.SUCCESS.getCode(),
                        BooleanEnum.SUCCESS.getDescription()
                );
                orderLog.setReqMsg(request.toString());
                orderLog.setResMsg(response.toString());
                orderLogService.create(orderLog);
            }else {
                // 出库回传失败
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_245.getCode()),
                        BooleanEnum.FAIL.getCode(),
                        response.getMsg()
                );
                orderLog.setReqMsg(request.toString());
                orderLog.setResMsg(response.toString());
                orderLogService.create(orderLog);
                throw new BadRequestException(response.getMsg());
            }
        }catch (Exception e) {
            // 出库回传失败
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_245.getCode()),
                    BooleanEnum.FAIL.getCode(),
                    e.getMessage()
            );
            orderLogService.create(orderLog);
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void pullOrderByPage(String body) throws Exception{
        // TODO: 2021/11/29 拼多多分页拉单(mq)
        PddPullOrderRequest request = JSON.parseObject(body, PddPullOrderRequest.class);

        log.info("拼多多拉单：请求参数：{}", request);
        PddCommonResponse<PddPullOrderResponse> res = pddSupport.request(PddPullOrderResponse.class,request);
        log.info("拼多多拉单：返回：{}", res);
        if (res.isSuccess()) {
            boolean resHasNext = res.getData().getOrderList().size()>=request.getPageSize();
            res.getData().setlShopId(request.getlShopId());
            res.getData().setCustId(request.getCustId());
            // 将客户、店铺信息插入实体，省得到下一步再去查数据库

            Integer totalNum = res.getData().getTotalCount();
            String nextPage = resHasNext?"T":"F";
            // 记录拉单日志
            PullOrderLog log = new PullOrderLog(
                    request.getlShopId(),
                    new Timestamp(request.getStartTime().getTime()),
                    new Timestamp(request.getEndTime().getTime()),
                    request.getPageNo(),
                    request.getPageSize(),
                    nextPage,
                    totalNum.toString(),
                    "T",
                    "成功,"+new JSONObject(res).toString()
            );
            pullOrderLogService.create(log);
            handleOrder(res);
        }else {
            PullOrderLog log = new PullOrderLog(
                    request.getlShopId(),
                    new Timestamp(request.getStartTime().getTime()),
                    new Timestamp(request.getEndTime().getTime()),
                    request.getPageNo(),
                    request.getPageSize(),
                    "E",
                    "0",
                    "F",
                    res.getMsg()
            );
            pullOrderLogService.create(log);
        }
    }

    // 保存订单
    @Override
    public synchronized void createOrder(String body) {
        // TODO: 2021/11/29 拼多多保存拉取到的订单
        PddOrder pddOrder = JSON.parseObject(body, PddOrder.class);
        try {
            // 查询确认是否已保存过
            CrossBorderOrder exists = crossBorderOrderService.queryByOrderNo(pddOrder.getOrderSn());
            if (exists != null) {
                // 已保存过
                return;
            }
            // 将订单保存进数据库
            CrossBorderOrder order = new CrossBorderOrder();
            ShopInfo shopInfo=shopInfoService.findById(pddOrder.getlShopId());
            order.setCreateTime(new Timestamp(System.currentTimeMillis()));
            order.setCreateBy("SYSTEM");
            order.setStatus(CBOrderStatusEnum.STATUS_200.getCode());
            order.setShopId(pddOrder.getlShopId());
            order.setPlatformShopId(pddOrder.getPddUserId());
            order.setCustomersId(pddOrder.getCustId());
            order.setUpStatus(pddOrder.getStatus());


            order.setOrderNo(pddOrder.getOrderSn());
            order.setCrossBorderNo(pddOrder.getInnerTransactionId());
            order.setOrderCreateTime(new Timestamp(DateUtils.parseDateTime(pddOrder.getCreatedTime()).getTime()));
            order.setEbpCode("3105961682");
            order.setEbpName("上海寻梦信息技术有限公司");

            // 这些数据一期时间紧先写死
            order.setPlatformCode("PDD");
            order.setOrderForm("1134");
            order.setDisAmount("0");
            order.setPostFee(pddOrder.getPostage().toString());
            order.setPayment(pddOrder.getPayAmount().toString());// 实际支付金额
            order.setBuyerAccount("null");
            order.setBuyerPhone(pddOrder.getReceiverPhone());
            order.setBuyerIdNum(pddOrder.getIdCardNum());
            order.setBuyerName(pddOrder.getIdCardName());
            order.setPayTime(new Timestamp(DateUtils.parseDateTime(pddOrder.getPayTime()).getTime()));
            order.setPaymentNo(pddOrder.getPayNo());
            order.setOrderSeqNo(pddOrder.getPayNo());
            order.setBooksNo(shopInfo.getBooksNo());
            if (StringUtil.isNotBlank(pddOrder.getPayType())){
                switch (pddOrder.getPayType()) {
                    case "QQ":
                    case "WEIXIN":
                        order.setPayCode("13");//财付通
                        break;
                    case "ALIPAY":
                        order.setPayCode("02");//支付宝
                        break;
                    case "LIANLIANPAY":
                        order.setPayCode("12");//连连支付
                        break;
                    case "DUODUOPAY":
                        order.setPayCode("64");//多多支付
                        break;
                }
            }else {
                order.setPayCode("-");//多多支付
            }
            order.setConsigneeName(StringUtil.isBlank(pddOrder.getReceiverName())?"-":pddOrder.getReceiverName());
            order.setProvince(StringUtil.isBlank(pddOrder.getProvince())?"-":pddOrder.getProvince());
            order.setCity(StringUtil.isBlank(pddOrder.getCity())?"-":pddOrder.getCity());
            order.setDistrict(StringUtil.isBlank(pddOrder.getTown())?"-":pddOrder.getTown());
            order.setConsigneeAddr(StringUtil.isBlank(pddOrder.getAddress())?"-":pddOrder.getAddress());
            order.setConsigneeTel(StringUtil.isBlank(pddOrder.getReceiverPhone())?"-":pddOrder.getReceiverPhone());

            // 是否需要冻结标记
            boolean needFreeze = false;
            String freezeReason = "";
            //平均价
            BigDecimal advPrice = pddOrder.getGoodsAmount().subtract(pddOrder.getDiscountAmount()).divide(new BigDecimal(pddOrder.getOrderItems().size()), 2, BigDecimal.ROUND_HALF_UP);//每种商品的平均支付价
            // 处理明细数据
            List<CrossBorderOrderDetails> list = new ArrayList<>();
            List<PddOrderItem> itemList = pddOrder.getOrderItems();
            StringBuilder detailBuild=new StringBuilder();
            if (CollectionUtils.isNotEmpty(itemList)) {
                for (PddOrderItem orderItem : itemList) {
                    String goodsNo = null;
                    if ((orderItem.getOuterId() != null && (orderItem.getOuterId().indexOf("P") == 0 || orderItem.getOuterId().indexOf("31") == 0)) || (orderItem.getOuterGoodsId() != null && (orderItem.getOuterGoodsId().indexOf("P") == 0 || orderItem.getOuterGoodsId().indexOf("31") == 0))) {
                        goodsNo = StringUtil.isNotEmpty(orderItem.getOuterId()) ? orderItem.getOuterId() : orderItem.getOuterGoodsId();
                    } else {
                        if (StringUtil.isNotEmpty(orderItem.getOuterId()) && orderItem.getOuterId().indexOf("FL_") == 0) {
                            goodsNo = orderItem.getOuterId();//商品维度外部编码，针对于单规格
                        } else if (StringUtil.isNotEmpty(orderItem.getOuterGoodsId()) && orderItem.getOuterGoodsId().indexOf("FL_") == 0) {
                            goodsNo = orderItem.getOuterGoodsId();//sku维度商家外部编码,针对于多规格
                        } else {
                            goodsNo = StringUtil.isEmpty(orderItem.getOuterId())?orderItem.getOuterGoodsId():orderItem.getOuterId();
                        }
                        goodsNo = goodsNo.replaceAll("^FL_", "");
                    }
                    if (StringUtil.isNotEmpty(goodsNo)){
                        // 有些商家瞎填货号有空格，直接去除
                        goodsNo=StringUtil.removeEscape(goodsNo);
                    }
                    CombinationOrder comb=combinationOrderService.queryByCombSku(goodsNo);
                    if (detailBuild.length()!=0){
                        detailBuild.append("，");
                    }

                    if ((comb!=null&& StringUtil.equals(shopInfo.getPushTo(),"0"))||(comb!=null && !StringUtil.equals(shopInfo.getPushTo(),"0")&&comb.getShopId().equals(shopInfo.getId()))){
                        //组合单
                        List<CombSplit>splitList=combSplitService.queryByCombId(comb.getId());
                        for (CombSplit combSplit : splitList) {
                            int num = combSplit.getQty() * orderItem.getGoodsCount();
                            BigDecimal payment = BigDecimal.ZERO;
                            if (splitList.size() == 1 && pddOrder.getOrderItems().size() == 1) {//如果拆出的商品只有一种sku，直接用该商品的平均支付价格
                                payment = advPrice;//商品价
                            } else {
                                payment = advPrice.
                                        multiply(new BigDecimal(combSplit.getQty())).
                                        divide(new BigDecimal(comb.getSplitQty()), 2, BigDecimal.ROUND_HALF_UP);
                            /*details.getPayment()*combSplit.getQty()/combinationOrder.getSplitQty()
                            (申报价+税费)*被拆出商品的数量/一共拆出的数量
                            */
                            }
                            CrossBorderOrderDetails details = new CrossBorderOrderDetails();
                            BaseSku baseSku=baseSkuService.queryByGoodsNo(combSplit.getSplitSkuId());
                            if (baseSku == null || !shopInfo.getCustId().equals(baseSku.getCustomersId())) {
                                needFreeze = true;
                                freezeReason = combSplit.getSplitSkuId() + "未创建货品";
                            }else {
                                details.setGoodsId(baseSku.getId());
                                details.setGoodsCode(baseSku.getGoodsCode());
                                details.setBarCode(baseSku.getBarCode());
                            }
                            detailBuild.append(String.format("%s%s %s %s件",
                                    details.getGoodsNo(),
                                    baseSku!=null?baseSku.getGoodsName():"Null",
                                    baseSku!=null?baseSku.getBarCode():"Null",
                                    details.getQty()));
                            details.setOrderNo(order.getOrderNo());
                            details.setGoodsNo(combSplit.getSplitSkuId());
                            details.setQty(num + "");
                            details.setPayment(payment + "");
                            list.add(details);
                        }
                    }else {
                        CrossBorderOrderDetails details = new CrossBorderOrderDetails();
                        BaseSku baseSku;
                        if (StringUtil.equals(shopInfo.getPushTo(),"0"))
                            //富勒
                            baseSku = baseSkuService.queryByGoodsNo(goodsNo);
                        else
                            //其他,例如菜鸟
                            baseSku = baseSkuService.queryByOutGoodsNo(goodsNo);
                        if (baseSku == null || !shopInfo.getCustId().equals(baseSku.getCustomersId())) {
                            needFreeze = true;
                            freezeReason = goodsNo + "未创建货品";
                        }else {
                            details.setGoodsId(baseSku.getId());
                            details.setGoodsCode(baseSku.getGoodsCode());
                            details.setBarCode(baseSku.getBarCode());
                        }
                        detailBuild.append(String.format("%s%s %s %s件",
                                details.getGoodsNo(),
                                baseSku!=null?baseSku.getGoodsName():"Null",
                                baseSku!=null?baseSku.getBarCode():"Null",
                                details.getQty()));
                        details.setOrderNo(order.getOrderNo());
                        details.setGoodsNo(baseSku!=null?baseSku.getGoodsNo():goodsNo);
                        details.setQty(orderItem.getGoodsCount() + "");
                        details.setPayment(advPrice + "");
                        list.add(details);
                    }

                }
            }
            order.setItemList(list);
            try {
                if (pddOrder.getProvinceId()!=0&&pddOrder.getCityId()!=0&&pddOrder.getTownId()!=0){
                    PddCloudPrintData pddCloudPrintData = getMailNo(pddOrder);
                    String mailNo=pddCloudPrintData.getMailNo();
                    order.setLogisticsNo(mailNo);
                    pddCloudPrintData.setShopCode(order.getPlatformShopId());
                    pddCloudPrintData.setSender(shopInfo.getName());
                    pddCloudPrintData.setSenderPhone(shopInfo.getContactPhone());
                    cbOrderProducer.send(
                            MsgType.CB_ORDER_PDD_SAVE_PRINTDATA,
                            new JSONObject(pddCloudPrintData).toString(),
                            order.getOrderNo()
                    );
                }
            }catch (Exception e){
                e.printStackTrace();
                OrderLog orderLog = new OrderLog(
                        0L,
                        pddOrder.getOrderSn(),
                        String.valueOf(CBOrderStatusEnum.STATUS_200.getCode()),
                        body,
                        "",
                        BooleanEnum.FAIL.getCode(),
                        e.getMessage(),
                        StringUtil.exceptionStackInfoToString(e)
                );
                orderLogService.create(orderLog);
                needFreeze=true;
                freezeReason+=",无运单号："+e.getMessage();
            }
            if (order.getCity().length() <= 1)
                order.setCity(order.getDistrict());
            if (StringUtil.contains(order.getCity(),"直辖县")){
                order.setCity(order.getDistrict());
            }
            //由于数据加密了，无法检查地址是否包含省市区
            /*if (!order.getConsigneeAddr().contains(order.getProvince())) {
                order.setConsigneeAddr(order.getProvince() + " " + order.getCity() + " " + order.getDistrict() + " " + order.getConsigneeAddr());
            }*/
            CrossBorderOrder orderDto = crossBorderOrderService.createWithDetail(order);
            // 保存订单日志
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_200.getCode()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderLogService.create(orderLog);
            if(needFreeze){
                if (StringUtil.isBlank(pddOrder.getInnerTransactionId())){
                    freezeReason+=",无XP单号";
                }
                if (StringUtil.isBlank(pddOrder.getPayNo())){
                    freezeReason+=",无支付单号";
                }
                if (pddOrder.getProvinceId()==0&&pddOrder.getCityId()==0&&pddOrder.getTownId()==0){
                    freezeReason+=",风控订单";
                }
            }else {
                if (StringUtil.isBlank(pddOrder.getInnerTransactionId())){
                    freezeReason+=",无XP单号";
                    needFreeze=true;
                }
                if (StringUtil.isBlank(pddOrder.getPayNo())){
                    freezeReason+=",无支付单号";
                    needFreeze=true;
                }
                if (pddOrder.getProvinceId()==0&&pddOrder.getCityId()==0&&pddOrder.getTownId()==0){
                    freezeReason+=",风控订单";
                }
            }
            // 冻结逻辑
            if (needFreeze) {
                order.setStatus(CBOrderStatusEnum.STATUS_999.getCode());
                order.setFreezeReason(freezeReason);
                crossBorderOrderService.update(order);
                OrderLog freezeLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_999.getCode()),
                        BooleanEnum.SUCCESS.getCode(),
                        freezeReason
                );
                orderLogService.create(freezeLog);
            }else {
                // 接单回传通知（预售单不往下走）
                cbOrderProducer.send(
                        MsgType.CB_ORDER_215,
                        String.valueOf(orderDto.getId()),
                        order.getOrderNo()
                );
            }
        }catch (Exception e) {
            e.printStackTrace();
            // 记录异常日志，继续循环
            OrderLog orderLog = new OrderLog(
                    0L,
                    pddOrder.getOrderSn(),
                    String.valueOf(CBOrderStatusEnum.STATUS_200.getCode()),
                    body,
                    "",
                    BooleanEnum.FAIL.getCode(),
                    e.getMessage(),
                    StringUtil.exceptionStackInfoToString(e)
            );
            orderLogService.create(orderLog);
            log.error("接单异常",e);
        }

    }

    @Override
    public void getMailNo(CrossBorderOrder order) throws Exception{
        // TODO: 2021/11/29 拼多多获取运单
        PddGetMailNoReplaceRequest request=new PddGetMailNoReplaceRequest();
        request.setShopCode(order.getPlatformShopId());
        PddOrderDeclare pddGetMailNo=new PddOrderDeclare();
        pddGetMailNo.setOrderSn(order.getOrderNo());
        pddGetMailNo.setProvince(order.getProvince());
        pddGetMailNo.setDistrict(order.getDistrict());
        pddGetMailNo.setCity(order.getCity());
        pddGetMailNo.setConsigneeAddr(order.getConsigneeAddr());
        pddGetMailNo.setConsignee(order.getConsigneeName());
        pddGetMailNo.setConsigneeTel(order.getConsigneeTel());
        pddGetMailNo.setBuyerIdNum(order.getBuyerIdNum());
        pddGetMailNo.setOrderNo(order.getCrossBorderNo());
        pddGetMailNo.setGrossWeight(order.getGrossWeight());
        pddGetMailNo.setNetWeight(order.getNetWeight());
        List<PddOrderDeclareDetail>itemList=new ArrayList<>();
        for (CrossBorderOrderDetails details : order.getItemList()) {
            PddOrderDeclareDetail item=new PddOrderDeclareDetail();
            item.setProductId(details.getGoodsNo());
            item.setGoodsName(details.getGoodsName());
            item.setPayment(details.getPayment());
            item.setQty(details.getQty());
            item.setMakeCountry(details.getMakeCountry());
            item.setUnit(details.getUnit());
            itemList.add(item);
        }
        pddGetMailNo.setDetails(itemList);
        request.setOrder(pddGetMailNo);

        PddCommonResponse<PddGetMailNoResponse>response=pddSupport.request(PddGetMailNoResponse.class,request);
        if (response.isSuccess()&&response.getData()!=null){
            if (StringUtil.isNotBlank(response.getData().getErrMsg()))
                throw new BadRequestException(response.getData().getErrMsg());
            order.setAddMark(response.getData().getAddMark());
            order.setLogisticsNo(response.getData().getMailNo());
            order.setCrossBorderNo(response.getData().getOrderNo());
        }else
            throw new BadRequestException("获取运单号失败："+response.getMsg());
    }

    @Override
    public String declare(CrossBorderOrder order) throws Exception {
        // TODO: 2021/11/29 拼多多清关申报
        PddOrderDeclareRequest request = new PddOrderDeclareReplaceRequest();
        return declare(order,request);
    }

    private String declare(CrossBorderOrder order, PddOrderDeclareRequest request) throws Exception {
        request.setShopCode(order.getPlatformShopId());
        PddOrderDeclare orderDeclare=new PddOrderDeclare();
        orderDeclare.setOrderNo(order.getCrossBorderNo());
        orderDeclare.setOrderSn(order.getOrderNo());
        orderDeclare.setPhone(order.getBuyerPhone());
        orderDeclare.setPaymentNo(order.getPaymentNo());
        orderDeclare.setBuyerIdNum(order.getBuyerIdNum());
        orderDeclare.setBuyerName(order.getBuyerName());
        orderDeclare.setProvince(order.getProvince());
        orderDeclare.setCity(order.getCity());
        orderDeclare.setDistrict(order.getDistrict());
        orderDeclare.setConsigneeAddr(order.getConsigneeAddr());
        orderDeclare.setConsignee(order.getConsigneeName());
        orderDeclare.setConsigneeTel(order.getConsigneeTel());
        ShopInfo shopInfo=shopInfoService.findById(order.getShopId());
        ClearCompanyInfo clearCompanyInfo=clearCompanyInfoService.findById(shopInfo.getServiceId());
        if (!StringUtil.equals(clearCompanyInfo.getCustomsCode(),"3302461510")){
            //多多云默认是3302461510的用户秘钥
            KeyPwdInfo keyPwdInfo=new KeyPwdInfo();
            keyPwdInfo.setWmsUser(SecureUtils.encryptDexHex(clearCompanyInfo.getKjgUser(),"80db75f13e0e4dce80117e6591666d99"));
            keyPwdInfo.setWmsPwd(SecureUtils.encryptDexHex(clearCompanyInfo.getKjgKey(),"80db75f13e0e4dce80117e6591666d99"));
            orderDeclare.setKeyPwd(keyPwdInfo);
            orderDeclare.setCustomerCode(clearCompanyInfo.getCustomsCode());
            orderDeclare.setCustomerName(clearCompanyInfo.getClearCompanyName());
        }else {
            orderDeclare.setCustomerCode("3302461510");
            orderDeclare.setCustomerName("宁波富立物流有限公司");
        }
        orderDeclare.setCreateTime(DateUtils.formatDateTime(order.getOrderCreateTime()));
        orderDeclare.setPostFee(order.getPostFee());
        orderDeclare.setAmount(order.getPayment());
        orderDeclare.setBuyerAccount(order.getConsigneeName());
        orderDeclare.setGrossWeight(order.getGrossWeight());
        orderDeclare.setNetWeight(order.getNetWeight());
        orderDeclare.setDisAmount(order.getDisAmount());
        orderDeclare.setTaxAmount(order.getTaxAmount());
        orderDeclare.setBooksNo(order.getBooksNo());
        orderDeclare.setPayTime(DateUtils.formatDateTime(order.getPayTime()));
        orderDeclare.setSource(order.getPayCode());
        orderDeclare.setMailNo(order.getLogisticsNo());
        orderDeclare.setLogisticsName("中通速递");
        orderDeclare.setAddMark(order.getAddMark());
        List<PddOrderDeclareDetail> detailList=new ArrayList<>();
        for (CrossBorderOrderDetails item : order.getItemList()) {
            PddOrderDeclareDetail detail=new PddOrderDeclareDetail();
            BaseSku baseSku=baseSkuService.queryByGoodsNo(item.getGoodsNo());
            if (baseSku!=null)
                detail.setBarcode(baseSku.getBarCode());
            else
                detail.setBarcode(item.getBarCode());
            detail.setProductId(item.getGoodsNo());
            detail.setGoodsName(item.getGoodsName());
            detail.setQty(item.getQty());
            detail.setUnit(item.getUnit());
            detail.setPrice(item.getDutiableValue());
            detail.setAmount(item.getDutiableTotalValue());
            detail.setMakeCountry(item.getMakeCountry());
            detail.setPayment(item.getPayment());
            detailList.add(detail);
        }
        orderDeclare.setDetails(detailList);
        request.setOrder(orderDeclare);

        log.info("拼多多报关请求:"+request.toString());
        PddCommonResponse<PddOrderDeclareResponse>response=pddSupport.request(PddOrderDeclareResponse.class,request);
        if (response.isSuccess()){
            if (StringUtil.isNotBlank(response.getData().getDeclareNo())){
                order.setAddMark(response.getData().getAddMark());
                order.setLogisticsNo(response.getData().getMailNo());
                order.setCrossBorderNo(response.getData().getOrderNo());
                return response.getData().getDeclareNo();
            }
            log.error(new JSONObject(response).toString());
            throw new BadRequestException(response.getData().getMsg());
        }
        log.error(new JSONObject(response).toString());
        throw new BadRequestException(response.getMsg());
    }

    @Override
    public String cancelDeclare(CrossBorderOrder order) throws Exception{
        // TODO: 2021/11/29 拼多多总署撤单
        ShopInfo shopInfo=shopInfoService.findById(order.getShopId());
        ClearCompanyInfo clearCompanyInfo=clearCompanyInfoService.findById(shopInfo.getServiceId());
        PddCancelOrderDeclareRequest request=new PddCancelOrderDeclareReplaceRequest();
        request.setOrderNo(order.getCrossBorderNo());
        request.setShopCode(order.getPlatformShopId());
        request.setOrderSn(order.getOrderNo());
        request.setLogisticsCode("3122480063");
        request.setCustomerCode(clearCompanyInfo.getCustomsCode());
        request.setCustomerName(clearCompanyInfo.getClearCompanyName());
        request.setMailNo(order.getLogisticsNo());
        request.setCusLogisticsName("上海大誉国际物流有限公司");
        request.setDeclareNo(order.getDeclareNo());
        request.setInvtNo(order.getInvtNo());
        request.setBuyerIdNum(order.getBuyerIdNum());
        request.setBuyerName(order.getBuyerName());
        request.setPhone(order.getBuyerPhone());

        PddCommonResponse<PddCancelOrderDeclareResponse>response=pddSupport.request(PddCancelOrderDeclareResponse.class,request);
        if (response.isSuccess())
            return response.getData().getResponse();
        throw new BadRequestException(response.getMsg());
    }

    @Override
    public void decryptMask(CrossBorderOrder order) throws Exception{
        // TODO: 2021/11/29 拼多多密文脱敏
        PddDecryptMaskRequest request=new PddDecryptMaskRequest();
        request.setOrderSn(order.getOrderNo());
        request.setPddUserId(order.getPlatformShopId());
        if (StringUtil.isBlank(order.getConsigneeName()))
            return;
        request.setReceiverName(order.getConsigneeName());
        request.setReceiverPhone(order.getConsigneeTel());
        request.setAddress(order.getConsigneeAddr());
        request.setPayNo(order.getPaymentNo());
        request.setIdCardName(order.getBuyerName());
        request.setIdCardNum(order.getBuyerIdNum());

        PddCommonResponse<PddDecryptMaskResponse>response=pddSupport.request(PddDecryptMaskResponse.class,request);
        if (!response.isSuccess())
            throw new BadRequestException("脱敏失败");
        order.setConsigneeName(response.getData().getReceiverName());
        order.setConsigneeTel(response.getData().getReceiverPhone());
        order.setConsigneeAddr(response.getData().getAddress());
        order.setPaymentNo(response.getData().getPayNo());
        order.setBuyerIdNum(response.getData().getIdCardNum());
        order.setBuyerName(response.getData().getIdCardName());
    }

    @Override
    public void pullOrderByTimeRange(Date startTime, Date endTime, ShopInfo shopInfo){
        // TODO: 2021/11/29 拼多多根据起止时间拉单
        ShopToken shopToken=shopTokenService.queryByShopId(shopInfo.getId());
        PullOrderLog pullTime = new PullOrderLog();
        pullTime.setPageNo(1);
        pullTime.setPageSize(100);
        pullTime.setEndTime(new Timestamp(endTime.getTime()));
        pullTime.setStartTime(new Timestamp(startTime.getTime()));
        //重试机制
        for (int i = 0; i < 50; i++){
            try {
                pullOrder(shopToken, shopInfo, pullTime);
                break;
            } catch (Exception e) {
                e.printStackTrace();
                if (!StringUtil.contains(e.getMessage(),"获取订单失败：业务服务错误"))
                    break;
            }
        }
    }

    @Override
    public long queryShopTotalOrder(String startTime, String endTime, Long shopId) throws Exception{
        try {
            ShopToken shopToken = shopTokenService.queryByShopId(shopId);
            int pageNo = 1;
            int pageSize = 1;

            PddPullOrderRequest request = new PddPullOrderRequest();
            request.setStartTime(DateUtils.parse(startTime, DatePattern.NORM_DATETIME_PATTERN));
            request.setEndTime(DateUtils.parse(endTime, DatePattern.NORM_DATETIME_PATTERN));
            request.setShopCode(shopToken.getPlatformShopId());
            request.setOrderStatus(5);
            request.setRefundStatus(5);
            request.setPageNo(pageNo);
            request.setPageSize(pageSize);

            // 第一次先查询本次有多少订单
            PddCommonResponse<PddPullOrderResponse> response = pddSupport.request(PddPullOrderResponse.class,request);
            if (response.isSuccess()) {
                return response.getData().getTotalCount();
            }else {
                throw new BadRequestException(response.getMsg());
            }
        }catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public PddOrder getOrderByOrderSn(String orderSn,String shopCode) throws Exception{
        PddPullOrderByOrderSnRequest request=new PddPullOrderByOrderSnRequest();
        request.setOrderSns(new String[]{orderSn});
        request.setShopCode(shopCode);
        PddCommonResponse<PddOrder>resp = pddSupport.request(PddOrder.class,request);
        if (!resp.isSuccess())
            throw new BadRequestException(resp.getMsg());
        if (CollectionUtils.isNotEmpty(resp.getDataArray()))
            return resp.getDataArray().get(0);
        return null;
    }

    @Override
    public String getOrderStatus(String orderSn,String shopCode) throws Exception {
        if (StringUtil.isEmpty(orderSn))
            return null;
        //testTokenOverdue(token);
        PddGetOrderStatusRequest request=new PddGetOrderStatusRequest();
        request.setOrderSns(orderSn);
        request.setShopCode(shopCode);

        PddCommonResponse<PddGetOrderStatusResponse>response=pddSupport.request(PddGetOrderStatusResponse.class,request);
        List<PddGetOrderStatusResponse>statuses= response.getDataArray();
        if (statuses.get(0).getRefundStatus() == 1) {
            switch (statuses.get(0).getOrderStatus()) {
                case 1:
                    return "1";//待发货
                case 2:
                    return "2";//已发货待签收
                case 3:
                    return "3";//已签收
            }
        }
        switch (statuses.get(0).getRefundStatus()) {
            case 2:
                return "t2";//售后处理中
            case 3:
                return "t3";//退款中
            case 4:
                return "t4";//退款成功
        }
        return "unknow error";
    }

    @Override
    public JSONArray getMailNo(String[] orderDatas, String shopId) {
        JSONArray array = new JSONArray();
        for (String orderData : orderDatas) {
            JSONObject obj = new JSONObject();
            JSONObject order = new JSONObject(orderData);
            String orderSn = order.getStr("order_sn");
            PddMailMark pddMailMark = pddMailMarkService.queryByOrderSn(orderSn);
            if (pddMailMark != null) {
                obj.putOnce("order_sn", orderSn);
                obj.putOnce("logistics_no", pddMailMark.getMailNo());
                obj.putOnce("add_mark", pddMailMark.getAddMark());
                array.add(obj);
                continue;
            }
            String innerTransactionId = order.getStr("inner_transaction_id");
            JSONArray orderItem = order.getJSONArray("order_item");
            String province = order.getStr("province");
            String city = order.getStr("city");
            String district = order.getStr("district");
            String detail = order.getStr("detail");
            String mobile = order.getStr("mobile");
            String name = order.getStr("name");
            String idCardNum = order.getStr("id_card_num");
            String grossWeight = order.getStr("gross_weight");
            String netWeight = order.getStr("net_weight");
            PddOrder pddOrder = order.toBean(PddOrder.class);
            pddOrder.setOrderSn(orderSn);
            pddOrder.setProvince(province);
            pddOrder.setCity(city);
            pddOrder.setTown(district);
            pddOrder.setAddress(detail);
            pddOrder.setReceiverName(name);
            pddOrder.setReceiverPhone(mobile);

            CrossBorderOrder cbOrder = new CrossBorderOrder();
            cbOrder.setOrderNo(innerTransactionId);
            cbOrder.setConsigneeName(name);
            cbOrder.setConsigneeTel(mobile);
            cbOrder.setProvince(province);
            cbOrder.setCity(city);
            cbOrder.setDistrict(district);
            cbOrder.setConsigneeAddr(detail);
            cbOrder.setBuyerIdNum(idCardNum);
            cbOrder.setEbpCode("3105961682");
            cbOrder.setEbpName("上海寻梦信息技术有限公司");
            cbOrder.setGrossWeight(grossWeight);
            cbOrder.setNetWeight(netWeight);
            List<CrossBorderOrderDetails> detailsList = new ArrayList<>();
            List<PddOrderItem> pddOrderItemList = new ArrayList<>();
            for (int i = 0; i < orderItem.size(); i++) {
                JSONObject item = orderItem.getJSONObject(i);
                String itemname = item.getStr("name");
                Integer count = item.getInt("count");
                String outerGoodsId = item.getStr("outer_goods_id");
                String makeCountry = item.getStr("make_country");
                String unit = item.getStr("unit");
                String payment = item.getStr("payment");
                PddOrderItem pddOrderItem = new PddOrderItem();
                pddOrderItem.setGoodsName(itemname);
                pddOrderItem.setGoodsCount(count);
                CrossBorderOrderDetails details = new CrossBorderOrderDetails();
                details.setGoodsName(itemname);
                details.setPayment(payment);
                details.setGrossWeight(grossWeight);
                details.setNetWeight(netWeight);
                details.setGoodsNo(outerGoodsId);
                details.setUnit(unit);
                details.setMakeCountry(makeCountry);
                pddOrderItemList.add(pddOrderItem);
                detailsList.add(details);
            }
            cbOrder.setItemList(detailsList);
            pddOrder.setOrderItems(pddOrderItemList);

            obj.putOnce("order_sn", orderSn);

            Map<String, String> map;
            String mailNo;
            try {
                PddCloudPrintData pddCloudPrintData = getMailNo(pddOrder);
                mailNo = pddCloudPrintData.getMailNo();
                cbOrderProducer.send(
                        MsgType.CB_ORDER_PDD_SAVE_PRINTDATA,
                        new cn.hutool.json.JSONObject(pddCloudPrintData).toString(),
                        pddOrder.getOrderSn()
                );
            } catch (Exception e) {
                e.printStackTrace();
                obj.putOnce("err_msg", e.getMessage());
                array.add(obj);
                continue;
            }
            if (StringUtil.isNotEmpty(mailNo)) {
                map = new HashMap<>();
                map.put("company_id", "PDD-NBFLWL132046");
                map.put("KEY", "D2D3AAA53F572EE73E");
                map.put("platformsource", "1438");
                pddOrder.setTrackingNumber(mailNo);
                cbOrder.setLogisticsNo(mailNo);
            } else {
                obj.putOnce("err_msg", "获取运单号失败");
                array.add(obj);
                continue;
            }
            try {
                gztoSupport.getMail(cbOrder,map);
                obj.putOnce("logistics_no", cbOrder.getLogisticsNo());
                obj.putOnce("add_mark", cbOrder.getAddMark());
                pddMailMark = new PddMailMark();
                pddMailMark.setAddMark(cbOrder.getAddMark() == null ? "" : cbOrder.getAddMark());
                pddMailMark.setMailNo(cbOrder.getLogisticsNo() == null ? "" : cbOrder.getLogisticsNo());
                pddMailMark.setOrderSn(orderSn);
                pddMailMark.setShopCode(shopId);
                pddMailMarkService.create(pddMailMark);
                array.add(obj);
            }catch (Exception e){
                obj.putOnce("err_msg", "获取运单失败" + e.getMessage());
                array.add(obj);
            }
        }
        return array;
    }

    @Override
    public void pullOrderByOrderSn(String[] orderSns, ShopToken shopToken) throws Exception {
        // TODO: 2021/11/29 拼多多根据订单号拉单
        //testTokenOverdue(shopToken);//检测token是否还有效，并且根据token情况延长token有效期或重新获取token
        //int i=0;
        PddPullOrderByOrderSnRequest request=new PddPullOrderByOrderSnRequest();
        request.setOrderSns(orderSns);
        request.setShopCode(shopToken.getPlatformShopId());

        PddCommonResponse<PddOrder>resp = pddSupport.request(PddOrder.class,request);
        if (!resp.isSuccess())
            throw new BadRequestException(resp.getMsg());
        for (PddOrder pddOrder : resp.getDataArray()) {
            CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(pddOrder.getOrderSn());
            if (order != null) {
                //拉取过的订单，跳过本次循环
                continue;
            }
            List<PddOrderItem> pddOrderItemList = pddOrder.getOrderItems();
            if (CollectionUtils.isNotEmpty(pddOrderItemList)) {
                ShopInfo shopInfo=shopInfoService.findById(shopToken.getShopId());
                pddOrder.setlShopId(shopToken.getShopId());
                pddOrder.setCustId(shopInfo.getCustId());
                try {
                    createOrder(new JSONObject(pddOrder).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private String getMailNoParam(PddOrder order) {
        List<Map<String, Object>> tradeOrderInfoDtos = new ArrayList<>();
        Map<String, Object> tradeOrderInfoDto = new HashMap();
        tradeOrderInfoDto.put("object_id", UUID.randomUUID().toString().replaceAll("-", ""));
        Map<String, Object> orderInfo = new HashMap<>();
        orderInfo.put("order_channels_type", "PDD");//订单来源平台
        List<String> tradeOrderList = new ArrayList<>();
        tradeOrderList.add(order.getOrderSn());
        orderInfo.put("trade_order_list", tradeOrderList);
        tradeOrderInfoDto.put("order_info", orderInfo);
        Map<String, Object> packageInfo = new HashMap<>();
        List<Map<String, Object>> items = new ArrayList<>();
        List<PddOrderItem> pddOrderItems = order.getOrderItems();
        for (PddOrderItem pddOrderItem : pddOrderItems) {
            Map<String, Object> item = new HashMap<>();
            item.put("count", pddOrderItem.getGoodsCount());
            item.put("name", pddOrderItem.getGoodsName());
            items.add(item);
        }
        packageInfo.put("items", items);
        tradeOrderInfoDto.put("package_info", packageInfo);
        Map<String, Object> recipient = new HashMap<>();
        Map<String, Object> address = new HashMap<>();
        if (StringUtil.isNotEmpty(order.getCity())) {
            address.put("city", order.getCity());
        }
        if (StringUtil.isNotEmpty(order.getCountry())) {
            address.put("country", order.getCountry());
        }
        if (StringUtil.isNotEmpty(order.getTown())) {
            address.put("district", order.getTown());
        }
        address.put("detail", order.getAddress());
        address.put("province", order.getProvince());
        recipient.put("address", address);
        recipient.put("mobile", order.getReceiverPhone());
        recipient.put("name", order.getReceiverName());
        tradeOrderInfoDto.put("recipient", recipient);
        tradeOrderInfoDto.put("template_url", "http://pinduoduoimg.yangkeduo.com/msfe/2019-0221/2bae9ebcf3c05fb50541760529abc837.xml");//面单模板URL
        tradeOrderInfoDto.put("user_id", Long.parseLong(pddUserId));
        tradeOrderInfoDtos.add(tradeOrderInfoDto);
        return "{\"trade_order_info_dtos\":" + com.alibaba.fastjson.JSONArray.toJSON(tradeOrderInfoDtos) + "}";
    }

    @Override
    public PddCloudPrintData getMailNo(PddOrder order) throws Exception {
        JSONObject printResult ;
        try {
            printResult=printOrder(getMailNoParam(order));
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
        JSONObject resp = printResult.getJSONObject("pdd_waybill_get_response");
        String mailNo = "";
        PddCloudPrintData pddCloudPrintData=new PddCloudPrintData();
        if (resp != null) {
            JSONArray modules = resp.getJSONArray("modules");
            if (modules != null) {
                JSONObject modeule = modules.getJSONObject(0);
                pddCloudPrintData.setCrossBorderOrderNo(order.getInnerTransactionId());
                pddCloudPrintData.setOrderNo(order.getOrderSn());
                if (modeule != null) {
                    String code = modeule.getStr("waybill_code");
                    if (StringUtil.isNotEmpty(code)) {
                        String printData = modeule.getStr("print_data");
                        pddCloudPrintData.setPrintData(StringUtil.isEmpty(printData) ? ("${-}"+printResult.toString()) : printData);
                        mailNo = code;
                    } else
                        throw new BadRequestException("运单号为空");
                }
            }
        } else if (printResult.getJSONObject("error_response") != null) {
            String errMsg = printResult.getJSONObject("error_response").getStr("error_msg");
            String subMsg = printResult.getJSONObject("error_response").getStr("sub_msg");
            throw new BadRequestException(errMsg + "," + subMsg);
        }
        pddCloudPrintData.setMailNo(mailNo);
        return pddCloudPrintData;
    }
}
