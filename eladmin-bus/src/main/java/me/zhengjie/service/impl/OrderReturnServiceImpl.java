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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DatePattern;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.mq.CBReturnProducer;
import me.zhengjie.repository.OrderReturnDetailsRepository;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.*;
import me.zhengjie.support.WmsSupport;
import me.zhengjie.support.douyin.CBOrderReturnChild;
import me.zhengjie.support.kjg.KJGSupport;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.OrderReturnRepository;
import me.zhengjie.service.mapstruct.OrderReturnMapper;
import me.zhengjie.utils.constant.MsgType;
import me.zhengjie.utils.constant.PlatformConstant;
import me.zhengjie.utils.enums.BooleanEnum;
import me.zhengjie.utils.enums.CBOrderStatusEnum;
import me.zhengjie.utils.enums.CBReturnOrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author luob
* @date 2021-04-14
**/
@Slf4j
@Service
@RequiredArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class OrderReturnServiceImpl implements OrderReturnService {

    private final OrderReturnRepository OrderReturnRepository;
    private final OrderReturnMapper OrderReturnMapper;

    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

    @Autowired
    private OrderReturnLogService orderReturnLogService;

    @Autowired
    private OrderReturnDetailsService orderReturnDetailsService;

    @Autowired
    private KJGSupport kjgSupport;

    @Autowired
    private CBReturnProducer cbReturnProducer;

    @Autowired
    private CustomerInfoService customerInfoService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private OrderReturnDetailsRepository orderReturnDetailsRepository;

    @Autowired
    private WmsSupport wmsSupport;

    @Autowired
    private DouyinService douyinService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Override
    public void declareBatch() {
        OrderReturnQueryCriteria criteria = new OrderReturnQueryCriteria();
        List<Integer> status = new ArrayList<>();
        status.add(CBReturnOrderStatusEnum.STATUS_317.getCode());
        criteria.setStatus(status);
        criteria.setIsBorder("1");
        criteria.setIsOverTime("0");
        criteria.setCheckResult("1");
        List<OrderReturn> all = OrderReturnRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        if (CollectionUtils.isNotEmpty(all)) {
            for (OrderReturn orderReturn : all) {
                cbReturnProducer.delaySend(
                        MsgType.CB_RETURN_325,
                        String.valueOf(orderReturn.getId()),
                        orderReturn.getOrderNo(),
                        2000
                );
            }
        }
    }

    // 强制申报
    @Override
    public void forceDec(Long id) throws Exception {
        OrderReturn orderReturn = queryById(Long.valueOf(id));
        if (orderReturn == null)
            throw new BadRequestException("订单不存在：" + id);
        if (orderReturn.getStatus().intValue() != CBReturnOrderStatusEnum.STATUS_317.getCode().intValue())
            throw new BadRequestException("质检未通过，不允许申报：" + id);
        orderReturn.setDecFlag("1");
        update(orderReturn);
        douyinService.confirmReturnByTools(orderReturn, "1");
        Thread.sleep(1000);
        douyinService.confirmReturnByTools(orderReturn, "2");
        declare(String.valueOf(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upload4Pl(List<Map<String, Object>> list) {
        if (CollectionUtils.isEmpty(list))
            throw new BadRequestException("数据为空");
        HashSet<String> orderNos = new HashSet<>();
        HashSet<String> orderNos1 = new HashSet<>();
        for (Map<String, Object> map : list) {
            String tradeReturnNo = map.get("销退单号") != null ?map.get("销退单号").toString() : null;
            String status = map.get("状态") != null ?map.get("状态").toString() : null;
            String rExpressNo = map.get("逆向运单号") != null ?map.get("逆向运单号").toString() : null;
            String orderNo = map.get("原订单号") != null ?map.get("原订单号").toString() : null;
            String isBorder = map.get("销退类型") != null ?map.get("销退类型").toString() : null;
            String checkType = map.get("质检结果") != null ?map.get("质检结果").toString() : null;
            String declareType = map.get("是否平台申报") != null ?map.get("是否平台申报").toString() : null;

            if (StringUtils.isEmpty(tradeReturnNo))
                throw new BadRequestException("销退单号不能为空");
            if (StringUtils.isEmpty(status))
                throw new BadRequestException("第" + tradeReturnNo + "行，状态不能为空");
            if (StringUtils.isEmpty(rExpressNo))
                throw new BadRequestException("第" + tradeReturnNo + "行，逆向运单号不能为空");
            if (StringUtils.isEmpty(orderNo))
                throw new BadRequestException("第" + tradeReturnNo + "行，原订单号不能为空");
            if (StringUtils.isEmpty(isBorder))
                throw new BadRequestException("第" + tradeReturnNo + "行，销退类型不能为空");
            if (StringUtils.isEmpty(checkType))
                throw new BadRequestException("第" + tradeReturnNo + "行，质检结果不能为空");

            tradeReturnNo = tradeReturnNo.replaceAll("\t", "");
            status = status.replaceAll("\t", "");
            rExpressNo = rExpressNo.replaceAll("\t", "");
            orderNo = orderNo.replaceAll("\t", "");
            isBorder = isBorder.replaceAll("\t", "");
            checkType = checkType.replaceAll("\t", "");

            if (!StringUtils.equals(status,"已收货") && !StringUtils.equals(status,"已申报"))
                throw new BadRequestException("第" + tradeReturnNo + "行，状态不是已收货和已申报不能导入");
            if (!StringUtils.equals(isBorder,"入区"))
                throw new BadRequestException("第" + tradeReturnNo + "行，销退类型不是入区不能导入");
            if (!StringUtils.equals(checkType,"成功"))
                throw new BadRequestException("第" + tradeReturnNo + "行，检结果不是成功不能导入");
            if (StringUtils.equals("平台申报", declareType) && !StringUtils.equals(status,"已申报"))
                throw new BadRequestException("第" + tradeReturnNo + "行，平台申报的单子，只有已申报状态才能导入");
            if (StringUtils.equals(status, "已申报")) {
                orderNos1.add(StringUtils.remove(orderNo, "\t"));
            }else {
                orderNos.add(StringUtils.remove(orderNo, "\t"));
            }


        }

        List<OrderReturn> orderReturnList = new ArrayList<>();
        for (String orderNo : orderNos) {
            OrderReturn exist = queryByOrderNo(orderNo);
            if (exist != null)
                throw new BadRequestException(orderNo + ":订单号已存在");
            CrossBorderOrder order = crossBorderOrderService.queryByOrderNoWithDetails(orderNo);
            if (order == null)
                throw new BadRequestException(orderNo + ":订单号不存在正向订单");
            if (!StringUtils.equals("1", order.getFourPl()))
                throw new BadRequestException(orderNo + ":订单不是4PL单不能导入");

            OrderReturn orderReturn = new OrderReturn();

            orderReturn.setIsWave("0");
            Timestamp start = order.getClearSuccessTime();
            Timestamp end = new Timestamp(System.currentTimeMillis());

            int days = DateUtils.differentDays(new Date(start.getTime()), new Date(end.getTime()));
            if (days > 30){
                //将是否入区字段改为0
                orderReturn.setIsBorder("0");
                orderReturn.setIsOverTime("1");
            }else {
                orderReturn.setIsOverTime("0");
                orderReturn.setIsBorder("1");
            }

            orderReturn.setFourPl(order.getFourPl());
            orderReturn.setOrderSource("2");// 手动生成
            orderReturn.setShopId(order.getShopId());
            orderReturn.setCustomersId(order.getCustomersId());

            orderReturn.setTradeReturnNo(genOrderNo());
            orderReturn.setLogisticsNo(orderReturn.getTradeReturnNo());

            orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_317.getCode());

            orderReturn.setOrderNo(order.getCrossBorderNo());
            orderReturn.setAfterSalesType("1");
            orderReturn.setCheckResult("1");
            orderReturn.setSalesCustomsTime(order.getClearSuccessTime());
            orderReturn.setSExpressNo(order.getLogisticsNo());
            orderReturn.setSExpressName(order.getLogisticsName());
            orderReturn.setRExpressNo(order.getLogisticsNo());
            orderReturn.setRExpressName(order.getLogisticsName());
            orderReturn.setReturnType("1");
            orderReturn.setCreateBy(SecurityUtils.getCurrentUsername());
            orderReturn.setCreateTime(new Timestamp(System.currentTimeMillis()));

            orderReturn.setSalesDeliverTime(order.getClearSuccessTime());
            orderReturn.setInvtNo(order.getInvtNo());
            orderReturn.setPlatformCode(order.getPlatformCode());

            List<OrderReturnDetails> detailsList = new ArrayList<>();
            for (CrossBorderOrderDetails child : order.getItemList()) {
                OrderReturnDetails details = new OrderReturnDetails();
                details.setLogisticsNo(orderReturn.getOrderNo());
                details.setGoodsNo(child.getGoodsNo());
                details.setFontGoodsName(child.getFontGoodsName());
                details.setQty(String.valueOf(child.getQty()));

                details.setGoodsId(child.getGoodsId());
                details.setGoodsName(child.getFontGoodsName());
                details.setGoodsCode(child.getGoodsCode());
                details.setHsCode(child.getHsCode());
                details.setBarCode(StringUtils.trim(child.getBarCode()));
                detailsList.add(details);
            }
            orderReturn.setItemList(detailsList);

            orderReturnList.add(orderReturn);

        }

        for (String orderNo : orderNos1) {
            OrderReturn exist = queryByOrderNo(orderNo);
            if (exist != null)
                throw new BadRequestException(orderNo + ":订单号已存在");
            CrossBorderOrder order = crossBorderOrderService.queryByOrderNoWithDetails(orderNo);
            if (order == null)
                throw new BadRequestException(orderNo + ":订单号不存在正向订单");
            if (!StringUtils.equals("1", order.getFourPl()))
                throw new BadRequestException(orderNo + ":订单不是4PL单不能导入");

            OrderReturn orderReturn = new OrderReturn();

            orderReturn.setIsWave("0");

            orderReturn.setIsBorder("1");

            orderReturn.setFourPl(order.getFourPl());
            orderReturn.setOrderSource("2");// 手动生成
            orderReturn.setShopId(order.getShopId());
            orderReturn.setCustomersId(order.getCustomersId());

            orderReturn.setTradeReturnNo(genOrderNo());
            orderReturn.setLogisticsNo(orderReturn.getTradeReturnNo());

            orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_337.getCode());

            orderReturn.setOrderNo(order.getCrossBorderNo());
            orderReturn.setAfterSalesType("1");
            orderReturn.setCheckResult("1");
            orderReturn.setSalesCustomsTime(order.getClearSuccessTime());
            orderReturn.setSExpressNo(order.getLogisticsNo());
            orderReturn.setSExpressName(order.getLogisticsName());
            orderReturn.setRExpressNo(order.getLogisticsNo());
            orderReturn.setRExpressName(order.getLogisticsName());
            orderReturn.setReturnType("1");
            orderReturn.setCreateBy(SecurityUtils.getCurrentUsername());
            orderReturn.setCreateTime(new Timestamp(System.currentTimeMillis()));

            orderReturn.setSalesDeliverTime(order.getClearSuccessTime());
            orderReturn.setInvtNo(order.getInvtNo());
            orderReturn.setPlatformCode(order.getPlatformCode());

            List<OrderReturnDetails> detailsList = new ArrayList<>();
            for (CrossBorderOrderDetails child : order.getItemList()) {
                OrderReturnDetails details = new OrderReturnDetails();
                details.setLogisticsNo(orderReturn.getOrderNo());
                details.setGoodsNo(child.getGoodsNo());
                details.setFontGoodsName(child.getFontGoodsName());
                details.setQty(String.valueOf(child.getQty()));

                details.setGoodsId(child.getGoodsId());
                details.setGoodsName(child.getFontGoodsName());
                details.setGoodsCode(child.getGoodsCode());
                details.setHsCode(child.getHsCode());
                details.setBarCode(StringUtils.trim(child.getBarCode()));
                detailsList.add(details);
            }
            orderReturn.setItemList(detailsList);

            orderReturnList.add(orderReturn);

        }
        createReturns(orderReturnList);
    }

    @Override
    public List<OrderReturn> querybyGatherNoAndShopId(String gatherNo, Long shopId) {
        List<OrderReturn> list = OrderReturnRepository.findAllByGatherNoAndShopId(gatherNo,shopId);
        if (CollectionUtils.isEmpty(list))
            return null;
        for (OrderReturn orderReturn : list) {
            List<OrderReturnDetails>detailList=orderReturnDetailsService.queryByReturnId(orderReturn.getId());
            orderReturn.setItemList(detailList);
        }
        return list;
    }

    @Override
    public void createReturns(List<OrderReturn> orderReturnList) {
        if (CollectionUtils.isEmpty(orderReturnList))
            throw new BadRequestException("数据为空");
        for (OrderReturn orderReturn : orderReturnList) {
            createWithDetail(orderReturn);

            if (orderReturn.getStatus().intValue() == CBReturnOrderStatusEnum.STATUS_317.getCode().intValue()) {
                cbReturnProducer.send(
                        MsgType.CB_RETURN_325,
                        String.valueOf(orderReturn.getId()),
                        orderReturn.getOrderNo()
                );
            }

            OrderReturnLog log = new OrderReturnLog(
                    orderReturn.getId(),
                    orderReturn.getOrderNo(),
                    String.valueOf(orderReturn.getStatus()),
                    BooleanEnum.SUCCESS.getCode(),
                    SecurityUtils.getCurrentUsername()
            );
            orderReturnLogService.create(log);
        }
    }

    @Override
    public void declare(String id) throws Exception {
        OrderReturn orderReturn = queryByIdWithDetails(Long.valueOf(id));
        if (orderReturn == null)
            throw new BadRequestException("订单不存在：" + id);
        if (orderReturn.getStatus().intValue() != CBReturnOrderStatusEnum.STATUS_317.getCode().intValue())
            throw new BadRequestException("质检未通过，不允许申报：" + id);
        CrossBorderOrder orderId = crossBorderOrderService.queryByCrossBorderNo(orderReturn.getOrderNo());
        CrossBorderOrder order = crossBorderOrderService.queryByIdWithDetails(orderId.getId());

        String res = kjgSupport.declareReturn(order);
        JSONObject resJSON = JSONUtil.xmlToJson(res);
        JSONObject resHeader = resJSON.getJSONObject("Message").getJSONObject("Header");
        if (StringUtil.equals("T", resHeader.getStr("Result"))) {
            // 申报成功
            orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_325.getCode());
            orderReturn.setDeclareStartTime(new Timestamp(System.currentTimeMillis()));
            update(orderReturn);
            OrderReturnLog log1 = new OrderReturnLog(
                    orderReturn.getId(),
                    orderReturn.getOrderNo(),
                    String.valueOf(orderReturn.getStatus()),
                    "",
                    res,
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderReturnLogService.create(log1);

            // 回传申报开始
            if (StringUtils.equals("1", orderReturn.getOrderSource())) {
                if (StringUtils.equals("0", orderReturn.getDecFlag())) {
                    douyinService.confirmReturnByTools(orderReturn, "1");
                    Thread.sleep(1000);
                    douyinService.confirmReturnByTools(orderReturn, "2");
                }

                cbReturnProducer.delaySend(
                        MsgType.CB_RETURN_330,
                        String.valueOf(orderReturn.getId()),
                        orderReturn.getOrderNo(),
                        2000
                );
            }else {
                orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_330.getCode());
                orderReturn.setDeclareStartBackTime(new Timestamp(System.currentTimeMillis()));
                update(orderReturn);
                OrderReturnLog log = new OrderReturnLog(
                        orderReturn.getId(),
                        orderReturn.getOrderNo(),
                        String.valueOf(orderReturn.getStatus()),
                        "",
                        "",
                        BooleanEnum.SUCCESS.getCode(),
                        BooleanEnum.SUCCESS.getDescription()
                );
                orderReturnLogService.create(log);
            }

        } else {
            if (StringUtils.contains(resHeader.getStr("ResultMsg"), "超期")) {
                // 清关申报失败
                orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_325.getCode());
                orderReturn.setDeclareMsg(resHeader.getStr("ResultMsg"));
                orderReturn.setIsOverTime("1");
                orderReturn.setIsBorder("0");

                if (StringUtils.equals("1", orderReturn.getOrderSource())) {
                    if (StringUtils.equals("0", orderReturn.getDecFlag())) {
                        douyinService.confirmReturnByTools(orderReturn, "1");
                        Thread.sleep(1000);
                        douyinService.confirmReturnByTools(orderReturn, "2");
                        Thread.sleep(1000);
                    }
                    // 先回传清关开始
                    douyinService.confirmReturnByTools(orderReturn, "4");
                }
                orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_340.getCode());// 清关异常
                update(orderReturn);
            }

            // 出现申报异常不改变状态，只记录日志
            OrderReturnLog log2 = new OrderReturnLog(
                    orderReturn.getId(),
                    orderReturn.getTradeReturnNo(),
                    String.valueOf(CBReturnOrderStatusEnum.STATUS_340.getCode()),
                    "",
                    "",
                    BooleanEnum.FAIL.getCode(),
                    resHeader.getStr("ResultMsg")
            );
            orderReturnLogService.create(log2);
            throw new BadRequestException(resHeader.getStr("ResultMsg"));
        }
    }

    //关单
    @Override
    public void closeOrder(Long id) {
        OrderReturn orderReturn = queryById(id);
        if (StringUtil.equals("1", orderReturn.getOrderSource())) {
            // 回传保税仓上架成功
            cbReturnProducer.send(
                    MsgType.CB_RETURN_355,
                    String.valueOf(orderReturn.getId()),
                    orderReturn.getOrderNo()
            );
        }else {
            orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_900.getCode());// 关单
            orderReturn.setCloseTime(new Timestamp(System.currentTimeMillis()));
            update(orderReturn);
            OrderReturnLog log = new OrderReturnLog(
                    orderReturn.getId(),
                    orderReturn.getOrderNo(),
                    String.valueOf(orderReturn.getStatus()),
                    "",
                    "",
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderReturnLogService.create(log);
        }
    }


    @Override
    public void ground(String id) throws Exception {
        OrderReturn orderReturn = queryByIdWithDetails(Long.valueOf(id));
        if (orderReturn == null)
            throw new BadRequestException("订单不存在：" + id);
        if (orderReturn.getStatus().intValue() != CBReturnOrderStatusEnum.STATUS_337.getCode().intValue())
            throw new BadRequestException("该状态不允许上架：" + id);
        // 更改状态
        if (StringUtil.equals("0", orderReturn.getIsBorder()))
            throw new BadRequestException("非入区不需手动上架，请联系技术");

        if (StringUtil.equals("1", orderReturn.getOrderSource())) {
            // 回传保税仓上架成功
            cbReturnProducer.send(
                    MsgType.CB_RETURN_355,
                    String.valueOf(orderReturn.getId()),
                    orderReturn.getOrderNo()
            );
        }else {
            orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_900.getCode());// 关单
            orderReturn.setCloseTime(new Timestamp(System.currentTimeMillis()));
            update(orderReturn);
            OrderReturnLog log = new OrderReturnLog(
                    orderReturn.getId(),
                    orderReturn.getOrderNo(),
                    String.valueOf(orderReturn.getStatus()),
                    "",
                    "",
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderReturnLogService.create(log);
        }

    }

    /**
     * 创建退货单
     * @param orderId
     * @param isBorder
     */
    @Override
    public void createReturn(Long orderId, String isBorder) {
        CrossBorderOrder order = crossBorderOrderService.queryByIdWithDetails(orderId);
        OrderReturn orderReturn = new OrderReturn();

        orderReturn.setIsWave("0");
        Timestamp start = order.getClearSuccessTime();
        Timestamp end = new Timestamp(System.currentTimeMillis());

        int days = DateUtils.differentDays(new Date(start.getTime()), new Date(end.getTime()));
        if (days > 30){
            //将是否入区字段改为0
            orderReturn.setIsBorder("0");
            orderReturn.setIsOverTime("1");
        }else {
            orderReturn.setIsOverTime("0");
            orderReturn.setIsBorder("1");
        }
        orderReturn.setFourPl(order.getFourPl());
        orderReturn.setOrderSource("2");// 手动生成
        orderReturn.setShopId(order.getShopId());
        orderReturn.setCustomersId(order.getCustomersId());

        orderReturn.setTradeReturnNo(genOrderNo());
        orderReturn.setLogisticsNo(orderReturn.getTradeReturnNo());
        orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_300.getCode());

        orderReturn.setOrderNo(order.getCrossBorderNo());
        orderReturn.setIsBorder(String.valueOf(isBorder));
        orderReturn.setAfterSalesType("1");
        orderReturn.setSalesCustomsTime(order.getClearSuccessTime());
        orderReturn.setSExpressNo(order.getLogisticsNo());
        orderReturn.setSExpressName(order.getLogisticsName());
        orderReturn.setRExpressNo(order.getLogisticsNo());
        orderReturn.setRExpressName(order.getLogisticsName());
        orderReturn.setReturnType("1");
        orderReturn.setCreateBy(SecurityUtils.getCurrentUsername());
        orderReturn.setCreateTime(new Timestamp(System.currentTimeMillis()));

        orderReturn.setSalesDeliverTime(order.getClearSuccessTime());
        orderReturn.setInvtNo(order.getInvtNo());
        orderReturn.setPlatformCode(order.getPlatformCode());

        List<OrderReturnDetails> detailsList = new ArrayList<>();
        for (CrossBorderOrderDetails child : order.getItemList()) {
            OrderReturnDetails details = new OrderReturnDetails();
            details.setLogisticsNo(orderReturn.getOrderNo());
            details.setGoodsNo(child.getGoodsNo());
            details.setFontGoodsName(child.getFontGoodsName());
            details.setQty(String.valueOf(child.getQty()));

            details.setGoodsId(child.getGoodsId());
            details.setGoodsName(child.getGoodsName());
            details.setGoodsCode(child.getGoodsCode());
            details.setHsCode(child.getHsCode());
            details.setBarCode(StringUtils.trim(child.getBarCode()));
            detailsList.add(details);
        }
        orderReturn.setItemList(detailsList);
        createWithDetail(orderReturn);

        OrderReturnLog log = new OrderReturnLog(
                orderReturn.getId(),
                orderReturn.getOrderNo(),
                String.valueOf(orderReturn.getStatus()),
                BooleanEnum.SUCCESS.getCode(),
                SecurityUtils.getCurrentUsername()
        );
        orderReturnLogService.create(log);

    }

    // 更新申报状态
    @Override
    public void updateDecStatus() {
        // 查询所有申报开始的订单
        OrderReturnQueryCriteria criteria = new OrderReturnQueryCriteria();
        List<Integer> status = new ArrayList<>();
        status.add(CBReturnOrderStatusEnum.STATUS_330.getCode());
        criteria.setStatus(status);
        List<OrderReturn> all = OrderReturnRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        if (all != null) {
            for (OrderReturn orderReturn : all) {
                try {
                    String res = kjgSupport.getReturnStatus(orderReturn);
                    JSONObject resJSON = JSONUtil.xmlToJson(res);
                    JSONObject resHeader = resJSON.getJSONObject("Message").getJSONObject("Header");
                    if (StringUtil.equals("T", resHeader.getStr("Result"))) {
                        JSONArray RejectedInfo = resJSON.getJSONObject("Message").getJSONObject("Body").getJSONArray("RejectedInfo");
                        for (int i = 0; i < RejectedInfo.size(); i++) {
                            JSONObject info = RejectedInfo.getJSONObject(i);
                            if (orderReturn.getDeclareStatus() == null || !info.getStr("Status").equals(orderReturn.getDeclareStatus())) {
                                // 清关状态为空，或者清关状态与当前状态不一致才更新
                                String Status = info.getStr("Status");
                                orderReturn.setDeclareStatus(Status);
                                orderReturn.setDeclareMsg(info.getStr("StatusDec"));
                                if ("20".equals(String.valueOf(Status)) || "70".equals(String.valueOf(Status))) {
                                    // 审核通过，改变单据状态
                                    confirmDecEnd(orderReturn.getId());
                                    continue;
                                }else if ("30".equals(String.valueOf(Status))) {
                                    // 审核不通过
                                    orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_340.getCode());
                                    orderReturn.setDeclareStatus(Status);
                                    orderReturn.setDeclareMsg("审核不通过");
                                    orderReturn.setDeclareEndBackTime(new Timestamp(System.currentTimeMillis()));
                                    update(orderReturn);
                                    OrderReturnLog log = new OrderReturnLog(
                                            orderReturn.getId(),
                                            orderReturn.getOrderNo(),
                                            String.valueOf(orderReturn.getStatus()),
                                            "",
                                            "",
                                            BooleanEnum.SUCCESS.getCode(),
                                            BooleanEnum.SUCCESS.getDescription()
                                    );
                                    orderReturnLogService.create(log);
                                }
                            }

                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public void confirmDecEnd(Long id) throws Exception {
        OrderReturn orderReturn = queryByIdWithDetails(id);
        if (StringUtils.equals(orderReturn.getOrderSource(), "1")) {
            // 回传申报完成
            orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_335.getCode());
            if (orderReturn.getDeclareEndTime() == null) {
                orderReturn.setDeclareEndTime(new Timestamp(System.currentTimeMillis()));
            }
            update(orderReturn);
            cbReturnProducer.delaySend(
                    MsgType.CB_RETURN_337,
                    String.valueOf(orderReturn.getId()),
                    orderReturn.getOrderNo(),
                    1000
            );
        }else {
            orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_337.getCode());
            orderReturn.setDeclareEndBackTime(new Timestamp(System.currentTimeMillis()));
            update(orderReturn);
//            if (StringUtils.equals("1", orderReturn.getIsWave())) {
//                // 推送wms退货入库单
//                wmsSupport.pushResturn(orderReturn);
//            }
            OrderReturnLog log = new OrderReturnLog(
                    orderReturn.getId(),
                    orderReturn.getOrderNo(),
                    String.valueOf(orderReturn.getStatus()),
                    "",
                    "",
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderReturnLogService.create(log);
        }
    }



    @Override
    public OrderReturn returnBook(String mailNo) throws Exception {
        // 控制状态
        OrderReturn orderReturn = queryByMailNo(mailNo);
        CrossBorderOrder order = crossBorderOrderService.queryByMailNo(mailNo);
        if (order == null)
            throw new BadRequestException("运单号不存在，请确认是否为原单退回");
        if (StringUtils.equals("1", order.getFourPl()))
            throw new BadRequestException("抖音4PL单，请先在抖音退货系统操作");
        if (order.getStatus().intValue() == CBReturnOrderStatusEnum.STATUS_888.getCode().intValue())
            throw new BadRequestException("正向订单已取消，请线下处理");
        if (order.getStatus().intValue() != CBOrderStatusEnum.STATUS_245.getCode().intValue())
            throw new BadRequestException("正向订单状态非出库，请确认是否为漏出库拦截单，状态：" + CBOrderStatusEnum.getDesc(order.getStatus()));
        if (order.getClearSuccessTime() == null)
            throw new BadRequestException("正向订单没有出库时间");
        Timestamp start = order.getClearSuccessTime();
        Timestamp end = new Timestamp(System.currentTimeMillis());

        int days = DateUtils.differentDays(new Date(start.getTime()), new Date(end.getTime()));

        if (orderReturn == null) {
            // 未查到对应退货单，通过运单号查询对应的正向订单
            orderReturn = new OrderReturn();
            if (days > 30){
                //将是否入区字段改为0
                orderReturn.setIsBorder("0");
                orderReturn.setIsOverTime("1");
            }else {
                orderReturn.setIsOverTime("0");
            }
            orderReturn.setOrder(order);
            return orderReturn;
        }

        if (orderReturn.getStatus().intValue() != CBReturnOrderStatusEnum.STATUS_300.getCode().intValue())
            throw new BadRequestException("当前退货单状态不允许到货登记：" + orderReturn.getStatus());
        if (days > 30){
            //将是否入区字段改为0
            orderReturn.setIsBorder("0");
            orderReturn.setIsOverTime("1");
        }else {
            orderReturn.setIsOverTime("0");
        }

        // 更改单据状态
        orderReturn.setDeclareNo(order.getDeclareNo());
        orderReturn.setInvtNo(order.getInvtNo());
        orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_305.getCode());
        orderReturn.setTakeTime(new Timestamp(System.currentTimeMillis()));
        update(orderReturn);

        OrderReturnLog log = new OrderReturnLog(
                orderReturn.getId(),
                orderReturn.getOrderNo(),
                String.valueOf(orderReturn.getStatus()),
                BooleanEnum.SUCCESS.getCode(),
                SecurityUtils.getCurrentUsername()
        );
        orderReturnLogService.create(log);

        returnBookBack(orderReturn);
        return orderReturn;
    }

    // 收货回传
    @Override
    public void returnBookBack(OrderReturn orderReturn) {

        if (StringUtils.equals("1", orderReturn.getOrderSource())) {
            if (!StringUtils.equals("0", orderReturn.getDecFlag())) {
                // 可申报
                cbReturnProducer.send(
                        MsgType.CB_RETURN_310,
                        String.valueOf(orderReturn.getId()),
                        orderReturn.getOrderNo()
                );
            }else {
                orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_310.getCode());
                orderReturn.setTakeBackTime(new Timestamp(System.currentTimeMillis()));
                update(orderReturn);
                OrderReturnLog log1 = new OrderReturnLog(
                        orderReturn.getId(),
                        orderReturn.getOrderNo(),
                        String.valueOf(orderReturn.getStatus()),
                        BooleanEnum.SUCCESS.getCode(),
                        BooleanEnum.SUCCESS.getDescription()
                );
                orderReturnLogService.create(log1);
            }
        }else {
            // 非抖音线上单状态直接变为收货完成回传
            orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_310.getCode());
            orderReturn.setTakeBackTime(new Timestamp(System.currentTimeMillis()));
            update(orderReturn);
            OrderReturnLog log1 = new OrderReturnLog(
                    orderReturn.getId(),
                    orderReturn.getOrderNo(),
                    String.valueOf(orderReturn.getStatus()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderReturnLogService.create(log1);
        }
    }


    @Override
    public OrderReturn queryByMailNo(String mailNo) {
        OrderReturn orderReturn = new  OrderReturn();
        orderReturn.setSExpressNo(mailNo);
        Example<OrderReturn> example = Example.of(orderReturn);
        Optional<OrderReturn> one = OrderReturnRepository.findOne(example);
        if (one.isPresent()) {
            OrderReturn orderReturn1 = one.get();
            List<OrderReturnDetails> orderReturnDetails = orderReturnDetailsService.queryByReturnId(orderReturn1.getId());
            for (OrderReturnDetails details : orderReturnDetails) {
                if (StringUtils.isBlank(details.getBarCode())) {
                    BaseSku baseSku = baseSkuService.queryByGoodsNo(details.getGoodsNo());
                    if (baseSku == null) {
                        baseSku = baseSkuService.queryByGoodsCodeAndWarehouseCode(details.getGoodsNo(), "FLBBC01");
                    }
                    details.setBarCode(baseSku.getBarCode());
                    details.setGoodsNo(baseSku.getGoodsNo());
                    orderReturnDetailsService.update(details);
                }
            }
            orderReturn1.setItemList(orderReturnDetails);
            return orderReturn1;
        }
        return null;
    }

    // 退货质检商品扫描
    @Override
    public void checkBarcode(String mailNo, String barcode) {
        OrderReturn orderReturn = queryByMailNo(mailNo);
        if (orderReturn == null)
            throw new BadRequestException("运单号不存在：" + mailNo);
        List<OrderReturnDetails> orderReturnDetails = orderReturnDetailsService.queryByReturnIdAndBarCode(orderReturn.getId(), barcode);
        if (CollectionUtils.isEmpty(orderReturnDetails))
            throw new BadRequestException("条码不存在：" + barcode);
    }

    // 提交质检
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void check(String checkTyp,String mailNo, List<OrderReturnDetails> itemList) throws Exception {
        if (CollectionUtils.isEmpty(itemList))
            throw new BadRequestException("质检明细为空");
        OrderReturn orderReturn = queryByMailNo(mailNo);
        if (orderReturn == null)
            throw new BadRequestException("运单号不存在：" + mailNo);
        orderReturn.setCheckType(checkTyp);
        orderReturn.setCheckResult(StringUtils.equals("1", checkTyp)?"1":"0");


        List<OrderReturnDetails> returnDetails = orderReturn.getItemList();
        for (OrderReturnDetails details : returnDetails) {
            for (OrderReturnDetails checkItem : itemList) {
                if (details.getId().intValue() == checkItem.getId().intValue()) {
                    if (!StringUtils.equals(String.format("%d",Double.valueOf(details.getQty()).intValue()),
                            String.format("%d",Double.valueOf(checkItem.getTotalNum()).intValue()))) {

                        throw new BadRequestException("实收数量与应收数量不一致");
                    }
                    details.setNormalNum(checkItem.getNormalNum());
                    details.setDamagedNum(checkItem.getDamagedNum());
                    details.setTotalNum(checkItem.getTotalNum());
                }
            }
        }
        orderReturnDetailsService.createAll(returnDetails);


        orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_315.getCode());
        orderReturn.setCheckTime(new Timestamp(System.currentTimeMillis()));
        update(orderReturn);

        OrderReturnLog log = new OrderReturnLog(
                orderReturn.getId(),
                orderReturn.getOrderNo(),
                String.valueOf(orderReturn.getStatus()),
                BooleanEnum.SUCCESS.getCode(),
                SecurityUtils.getCurrentUsername()
        );
        orderReturnLogService.create(log);

        checkBack(orderReturn);
    }

    //质检回传
    @Override
    public void checkBack(OrderReturn orderReturn) throws Exception {
        if (StringUtils.equals("1", orderReturn.getCheckResult())) {
            if (StringUtils.equals("1", orderReturn.getOrderSource()) ) {
                // 抖音下发
                if (StringUtils.equals("1", orderReturn.getIsBorder())) {
                    // 入区
                    if (!StringUtils.equals("0", orderReturn.getDecFlag())) {
                        // 抖音线上单，且可申报
                        cbReturnProducer.send(
                                MsgType.CB_RETURN_317,
                                String.valueOf(orderReturn.getId()),
                                orderReturn.getOrderNo()
                        );
                    }else {
                        orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_317.getCode());
                        orderReturn.setCheckBackTime(new Timestamp(System.currentTimeMillis()));
                        update(orderReturn);
                        OrderReturnLog log1 = new OrderReturnLog(
                                orderReturn.getId(),
                                orderReturn.getOrderNo(),
                                String.valueOf(orderReturn.getStatus()),
                                BooleanEnum.SUCCESS.getCode(),
                                SecurityUtils.getCurrentUsername()
                        );
                        orderReturnLogService.create(log1);
                    }
                }else {
                    if (StringUtils.equals("0", orderReturn.getDecFlag())) {
                        douyinService.confirmReturnByTools(orderReturn, "1");
                    }
                    // 非入区
                    // 回传质检不通过&退货仓上架
                    cbReturnProducer.delaySend(
                            MsgType.CB_RETURN_323,
                            String.valueOf(orderReturn.getId()),
                            orderReturn.getOrderNo(),
                            2000
                    );
                }
            }else {
                // 手动生成
                if (StringUtils.equals("1", orderReturn.getIsBorder())) {
                    // 入区
                    orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_317.getCode());
                    orderReturn.setCheckBackTime(new Timestamp(System.currentTimeMillis()));
                    update(orderReturn);
                    OrderReturnLog log1 = new OrderReturnLog(
                            orderReturn.getId(),
                            orderReturn.getOrderNo(),
                            String.valueOf(orderReturn.getStatus()),
                            BooleanEnum.SUCCESS.getCode(),
                            SecurityUtils.getCurrentUsername()
                    );
                    orderReturnLogService.create(log1);
                }else {
                    // 非入区
                    orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_900.getCode());
                    orderReturn.setCloseTime(new Timestamp(System.currentTimeMillis()));
                    update(orderReturn);
                    OrderReturnLog log1 = new OrderReturnLog(
                            orderReturn.getId(),
                            orderReturn.getOrderNo(),
                            String.valueOf(orderReturn.getStatus()),
                            BooleanEnum.SUCCESS.getCode(),
                            SecurityUtils.getCurrentUsername()
                    );
                    orderReturnLogService.create(log1);
                }
            }
        }else {
            if (StringUtils.equals("1", orderReturn.getOrderSource()) ) {
                if (StringUtils.equals("0", orderReturn.getDecFlag())) {
                    douyinService.confirmReturnByTools(orderReturn, "1");
                }
                // 回传质检不通过&退货仓上架
                cbReturnProducer.delaySend(
                        MsgType.CB_RETURN_323,
                        String.valueOf(orderReturn.getId()),
                        orderReturn.getOrderNo(),
                        2000
                );
            }else {
                // 质检不通过直接关单
                orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_900.getCode());
                orderReturn.setCloseTime(new Timestamp(System.currentTimeMillis()));
                update(orderReturn);
                OrderReturnLog log1 = new OrderReturnLog(
                        orderReturn.getId(),
                        orderReturn.getOrderNo(),
                        String.valueOf(orderReturn.getStatus()),
                        BooleanEnum.SUCCESS.getCode(),
                        SecurityUtils.getCurrentUsername()
                );
                orderReturnLogService.create(log1);
            }
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createWithDetail(OrderReturn orderReturn) {
        OrderReturn save = OrderReturnRepository.save(orderReturn);
        List<OrderReturnDetails> itemList = orderReturn.getItemList();
        if (CollectionUtils.isNotEmpty(itemList)) {
            for (OrderReturnDetails details : itemList) {
                details.setReturnId(save.getId());
            }
            orderReturnDetailsService.createAll(itemList);
        }

    }

    @Override
    public OrderReturn queryByLogisNo(String logisticsNo) {
        OrderReturn orderReturn = new  OrderReturn();
        orderReturn.setLogisticsNo(logisticsNo);
        Example<OrderReturn> example = Example.of(orderReturn);
        Optional<OrderReturn> one = OrderReturnRepository.findOne(example);
        return one.isPresent()?one.get():null;
    }

    @Override
    public OrderReturn queryByIdWithDetails(Long id) {
        OrderReturn orderReturn = OrderReturnRepository.findById(id).orElseGet(OrderReturn::new);
        List<OrderReturnDetails> detailsList = orderReturnDetailsService.queryByReturnId(orderReturn.getId());
        orderReturn.setItemList(detailsList);
        return orderReturn;
    }

    @Override
    public OrderReturn queryById(Long id) {
        return OrderReturnRepository.findById(id).orElseGet(OrderReturn::new);
    }

    @Override
    public OrderReturn queryByOrderNo(String orderNo) {
        return OrderReturnRepository.findByOrderNo(orderNo);
    }
    @Override
    public OrderReturn queryByOrderNoWithDetails(String orderNo) {
        OrderReturn orderReturn = OrderReturnRepository.findByOrderNo(orderNo);
        List<OrderReturnDetails> orderReturnDetails = orderReturnDetailsService.queryByReturnId(orderReturn.getId());
        orderReturn.setItemList(orderReturnDetails);
        return orderReturn;
    }


    @Override
    public OrderReturn queryDeclareNo(String declareNo) {
        return OrderReturnRepository.findByDeclareNo(declareNo);
    }

    // 更新WMS状态
    @Override
    public void updateWMsStatus() {
        // 查询所有申报完成回传的订单
        OrderReturnQueryCriteria criteria = new OrderReturnQueryCriteria();
        List<Integer> status = new ArrayList<>();
        status.add(CBReturnOrderStatusEnum.STATUS_337.getCode());
        criteria.setStatus(status);
        List<OrderReturn> all = OrderReturnRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        for (OrderReturn orderReturn : all) {
            updateWMsStatus(orderReturn.getId());
        }
    }

    @Override
    public void queryGatherByShop(Long shopId, Long gatherId, String gatherNo) {
        OrderReturnRepository.queryGatherByShop(shopId, gatherId, gatherNo);
    }

    @Override
    public List<OrderReturn> queryDecEnd() {
        OrderReturnQueryCriteria criteria = new OrderReturnQueryCriteria();
        List<Integer> status = new ArrayList<>();
        status.add(CBReturnOrderStatusEnum.STATUS_337.getCode());
        criteria.setStatus(status);
        List<OrderReturn> all = OrderReturnRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        for (OrderReturn orderReturn : all) {
            List<OrderReturnDetails> orderReturnDetails = orderReturnDetailsService.queryByReturnId(orderReturn.getId());
            orderReturn.setItemList(orderReturnDetails);
        }
        return all;
    }

    @Override
    public List<Map<String, Object>> queryWaitGather() {
        return OrderReturnRepository.queryWaitGather();
    }



    @Override
    public void updateWMsStatus(Long id) {
        try {
            OrderReturn orderReturn = queryById(id);
            JSONObject wmsOrder = wmsSupport.queryAsn2(orderReturn.getDeclareNo());
            if (wmsOrder == null)
                throw new BadRequestException("获取WMS状态失败:" + orderReturn.getOrderNo());
            if (StringUtil.isEmpty(orderReturn.getWmsNo())) {
                orderReturn.setWmsNo(wmsOrder.getJSONObject("header").getStr("asnno"));
                update(orderReturn);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Map<String,Object> queryAll(OrderReturnQueryCriteria criteria, Pageable pageable){
        Page<OrderReturn> page = OrderReturnRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(OrderReturnMapper::toDto));
    }

    @Override
    public List<OrderReturnDto> queryAll(OrderReturnQueryCriteria criteria){
        return OrderReturnMapper.toDto(OrderReturnRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public OrderReturnDto findById(Long id) {
        OrderReturn OrderReturn = OrderReturnRepository.findById(id).orElseGet(OrderReturn::new);
        ValidationUtil.isNull(OrderReturn.getId(),"OrderReturn","id",id);
        return OrderReturnMapper.toDto(OrderReturn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderReturnDto create(OrderReturn resources) {
        return OrderReturnMapper.toDto(OrderReturnRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(OrderReturn resources) {
        OrderReturn OrderReturn = OrderReturnRepository.findById(resources.getId()).orElseGet(OrderReturn::new);
        ValidationUtil.isNull( OrderReturn.getId(),"OrderReturn","id",resources.getId());
        OrderReturn.copy(resources);
        OrderReturnRepository.save(OrderReturn);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            OrderReturnRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<OrderReturnDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (OrderReturnDto OrderReturn : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("客户", customerInfoService.queryById(OrderReturn.getCustomersId()).getCustNickName());
            ShopInfoDto shopInfoDto = shopInfoService.queryById(OrderReturn.getShopId());
            map.put("店铺", shopInfoDto.getName());
            map.put("退货单号", OrderReturn.getTradeReturnNo());
            map.put("状态",  CBReturnOrderStatusEnum.getDesc(OrderReturn.getStatus()));
            map.put("物流订单号", OrderReturn.getLogisticsNo());
            map.put("原订单号", OrderReturn.getOrderNo());
            map.put("是否4PL", StringUtils.equals("1", OrderReturn.getFourPl())?"是":"否");
            String booksNo = shopInfoDto.getBooksNo();
            if (StringUtils.equals(PlatformConstant.DY, OrderReturn.getPlatformCode())) {
                booksNo = StringUtils.equals("1", OrderReturn.getFourPl())?"T3105W000185":"T3105W000159";
            }
            map.put("账册号", booksNo);
            map.put("申报单号", OrderReturn.getDeclareNo());
            map.put("总署清单编号", OrderReturn.getInvtNo());
            map.put("WMS单号", OrderReturn.getWmsNo());
            map.put("是否入区", StringUtil.equals("1",OrderReturn.getIsBorder())?"是":"否");
            map.put("是否超时",StringUtil.equals("1",OrderReturn.getIsOverTime())?"是":"否");
            map.put("产生波次",StringUtil.equals("1",OrderReturn.getIsWave())?"是":"否");
            map.put("单据来源",StringUtil.equals("1",OrderReturn.getOrderSource())?"抖音下发":"手动生成");
            map.put("订单放行时间", OrderReturn.getSalesDeliverTime());
            map.put("正向物流单号", OrderReturn.getSExpressNo());
            map.put("正向物流公司", OrderReturn.getSExpressName());
            map.put("质检结果", StringUtil.equals("1",OrderReturn.getCheckResult())?"通过":"不通过");
            map.put("收货时间", OrderReturn.getTakeTime());
            map.put("收货回传时间", OrderReturn.getTakeBackTime());
            map.put("质检通过时间", OrderReturn.getCheckTime());
            map.put("质检通过回传时间", OrderReturn.getCheckBackTime());
            map.put("申报开始时间", OrderReturn.getDeclareStartTime());
            map.put("申报开始回传时间", OrderReturn.getDeclareStartBackTime());
            map.put("申报完成时间", OrderReturn.getDeclareEndTime());
            map.put("申报完成回传时间", OrderReturn.getDeclareEndBackTime());
            map.put("关单时间", OrderReturn.getCloseTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void downloadDetails(Long id, HttpServletResponse response) throws IOException {
        OrderReturn orderReturn = queryByIdWithDetails(id);
        List<OrderReturnDetails> details = orderReturn.getItemList();
        List<Map<String, Object>> listOrder = new ArrayList<>();
        Map<String,Object> mapOrder = new LinkedHashMap<>();
        mapOrder.put("客户ID", customerInfoService.queryById(orderReturn.getCustomersId()).getCustNickName());
        mapOrder.put("店铺ID", shopInfoService.queryById(orderReturn.getShopId()).getName());
        mapOrder.put("退货单号", orderReturn.getTradeReturnNo());
        mapOrder.put("状态", orderReturn.getStatus());
        mapOrder.put("物流订单号", orderReturn.getLogisticsNo());
        mapOrder.put("原订单号", orderReturn.getOrderNo());
        mapOrder.put("申报单号", orderReturn.getDeclareNo());
        mapOrder.put("总税额", orderReturn.getTaxAmount());
        mapOrder.put("是否入区", orderReturn.getIsBorder());
        mapOrder.put("是否超时",orderReturn.getIsOverTime());
        mapOrder.put("售后类型", orderReturn.getAfterSalesType());
        mapOrder.put("售后单号", orderReturn.getAfterSalesNo());
        mapOrder.put("订单清关时间", orderReturn.getSalesCustomsTime());
        mapOrder.put("订单放行时间", orderReturn.getSalesDeliverTime());
        mapOrder.put("正向物流单号", orderReturn.getSExpressNo());
        mapOrder.put("正向物流公司", orderReturn.getSExpressName());
        mapOrder.put("逆向物流单号", orderReturn.getRExpressNo());
        mapOrder.put("逆向物流公司", orderReturn.getRExpressName());
        mapOrder.put("退货类型", orderReturn.getReturnType());
        mapOrder.put("质检结果", orderReturn.getCheckResult());
        mapOrder.put("清关状态", orderReturn.getDeclareStatus());
        mapOrder.put("清关信息", orderReturn.getDeclareMsg());
        mapOrder.put("收货时间", orderReturn.getTakeTime());
        mapOrder.put("收货回传时间", orderReturn.getTakeBackTime());
        mapOrder.put("质检完成时间", orderReturn.getCheckTime());
        mapOrder.put("质检完成回传时间", orderReturn.getCheckBackTime());
        mapOrder.put("申报开始时间", orderReturn.getDeclareStartTime());
        mapOrder.put("申报开始回传时间", orderReturn.getDeclareStartBackTime());
        mapOrder.put("申报完成时间", orderReturn.getDeclareEndTime());
        mapOrder.put("申报完成回传时间", orderReturn.getDeclareEndBackTime());
        mapOrder.put("保税仓上架时间", orderReturn.getBondedGroundTime());
        mapOrder.put("保税仓上架回传时间", orderReturn.getBondedGroundBackTime());
        mapOrder.put("理货完成时间", orderReturn.getTallyTime());
        mapOrder.put("理货完成回传时间", orderReturn.getTallyBackTime());
        mapOrder.put("退货仓上架时间", orderReturn.getReturnGroundTime());
        mapOrder.put("退货仓上架回传时间", orderReturn.getReturnGroundBackTime());
        listOrder.add(mapOrder);
        List<Map<String, Object>> listDetails = new ArrayList<>();
        if (details != null) {
            for (OrderReturnDetails detail : details) {
                Map<String,Object> mapDetails = new LinkedHashMap<>();
                mapDetails.put("退货单ID", detail.getReturnId());
                mapDetails.put("物流订单号", detail.getLogisticsNo());
                mapDetails.put("货品ID", detail.getGoodsId());
                mapDetails.put("货品编码", detail.getGoodsCode());
                mapDetails.put("海关货号", detail.getGoodsNo());
                mapDetails.put("条形码", detail.getBarCode());
                mapDetails.put("HS编码", detail.getHsCode());
                mapDetails.put("前端商品名称", detail.getFontGoodsName());
                mapDetails.put("商品名称", detail.getGoodsName());
                mapDetails.put("数量", detail.getQty());
                mapDetails.put("总税额", detail.getTaxAmount());
                mapDetails.put("正品数量", detail.getNormalNum());
                mapDetails.put("残品数量", detail.getDamagedNum());
                mapDetails.put("总数量", detail.getTotalNum());
                mapDetails.put("残次原因", detail.getDamagedReason());
                listDetails.add(mapDetails);
            }
        }
        FileUtil.downloadExcelDetails(listOrder, listDetails, response);
        }

    @Override
    public void downloadDetails(OrderReturnQueryCriteria criteria, HttpServletResponse response) throws IOException {
        List<Integer> status = criteria.getStatus();
        if (CollectionUtils.isEmpty(status))
            throw new BadRequestException("状态必填");
        List<OrderReturnDto> all = queryAll(criteria);
        if (CollectionUtils.isNotEmpty(status) && all.size() > 1000)
            throw new BadRequestException("数量不能超过1000");
        List<Map<String, Object>> list = new ArrayList<>();
        for (OrderReturnDto OrderReturn : all) {
            List<OrderReturnDetails> orderReturnDetails = orderReturnDetailsService.queryByReturnId(OrderReturn.getId());
            for (OrderReturnDetails details : orderReturnDetails) {
                Map<String,Object> map = new LinkedHashMap<>();
                map.put("客户", customerInfoService.queryById(OrderReturn.getCustomersId()).getCustNickName());
                map.put("店铺", shopInfoService.queryById(OrderReturn.getShopId()).getName());
                map.put("退货单号", OrderReturn.getTradeReturnNo());
                map.put("状态",  CBReturnOrderStatusEnum.getDesc(OrderReturn.getStatus()));
                map.put("物流订单号", OrderReturn.getLogisticsNo());
                map.put("原订单号", OrderReturn.getOrderNo());
                map.put("申报单号", OrderReturn.getDeclareNo());
                map.put("总署清单编号", OrderReturn.getInvtNo());
                map.put("WMS单号", OrderReturn.getWmsNo());
                map.put("是否入区", StringUtil.equals("1",OrderReturn.getIsBorder())?"是":"否");
                map.put("是否超时",StringUtil.equals("1",OrderReturn.getIsOverTime())?"是":"否");
                map.put("产生波次",StringUtil.equals("1",OrderReturn.getIsWave())?"是":"否");
                map.put("单据来源",StringUtil.equals("1",OrderReturn.getOrderSource())?"抖音下发":"手动生成");
                map.put("订单放行时间", OrderReturn.getSalesDeliverTime());
                map.put("正向物流单号", OrderReturn.getSExpressNo());
                map.put("正向物流公司", OrderReturn.getSExpressName());
                map.put("质检结果", StringUtil.equals("1",OrderReturn.getCheckResult())?"通过":"不通过");
                map.put("收货时间", OrderReturn.getTakeTime());
                map.put("收货回传时间", OrderReturn.getTakeBackTime());
                map.put("质检通过时间", OrderReturn.getCheckTime());
                map.put("质检通过回传时间", OrderReturn.getCheckBackTime());
                map.put("申报开始时间", OrderReturn.getDeclareStartTime());
                map.put("申报开始回传时间", OrderReturn.getDeclareStartBackTime());
                map.put("申报完成时间", OrderReturn.getDeclareEndTime());
                map.put("申报完成回传时间", OrderReturn.getDeclareEndBackTime());
                map.put("关单时间", OrderReturn.getCloseTime());

                map.put("海关货号", details.getGoodsNo());
                map.put("商品条码", details.getBarCode());
                map.put("商品名称", details.getGoodsName());
                map.put("应收数量", details.getQty());
                map.put("实收数量", details.getTotalNum());
                map.put("正品数量", details.getNormalNum());
                map.put("产品数量", details.getDamagedNum());
                list.add(map);
            }
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public synchronized String genOrderNo() {
        int tryCount = 0;
        String qz = "TH";
        String time = String.valueOf(System.currentTimeMillis());
        String result = qz + time;
        Random random = new Random();
        for (int i=0;i<4; i++) {
            result += random.nextInt(10);
        }
        OrderReturn order = queryByLogisNo(result);
        if (order != null && tryCount < 4) {
            genOrderNo();
            tryCount++;
        }
        return result;
    }

    private int getOrderServiceStatus(CrossBorderOrder order) {
        try {
            if (StringUtils.equals(PlatformConstant.DY, order.getPlatformCode())) {
                Integer status = douyinService.getStatus(order);
                if (status.intValue() != 4 && status.intValue() != 21 && status.intValue() != 17 && status.intValue() != 5) {
                    return 1;// 未退款
                }else{
                    return 2;// 已退款
                }
            }else {
                return 2;// 已退款
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 2;// 已退款
    }

    @Override
    public Map<String, Object> queryOrders(OrderReturnQueryCriteria criteria, Pageable pageable) {
        Page<OrderReturn> page = OrderReturnRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        List<OrderReturn> content = page.getContent();
        List<OrderReturnOutDto> results = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(content)) {
            for (OrderReturn order : content) {

                OrderReturnOutDto orderReturnOutDto = new OrderReturnOutDto();
                BeanUtil.copyProperties(order, orderReturnOutDto, CopyOptions.create().setIgnoreNullValue(true));

                List<OrderReturnDetails> list = orderReturnDetailsService.queryByReturnId(order.getId());
                List<OrderReturnDetailsOutDto> detailsDTOS = new ArrayList<>();
                for (OrderReturnDetails details : list) {
                    OrderReturnDetailsOutDto detailsDTO = new OrderReturnDetailsOutDto();
                    BeanUtil.copyProperties(details, detailsDTO, CopyOptions.create().setIgnoreNullValue(true));
                    detailsDTOS.add(detailsDTO);
                }
                ShopInfoDto shopInfoDto = shopInfoService.queryById(order.getShopId());
                orderReturnOutDto.setShopCode(shopInfoDto.getCode());
                orderReturnOutDto.setShopName(shopInfoDto.getName());
                orderReturnOutDto.setItemList(detailsDTOS);
                results.add(orderReturnOutDto);
            }
        }
        Page<OrderReturnOutDto> pageDTO = new PageImpl<>(results, pageable, page.getTotalElements());
        return PageUtil.toPage(pageDTO);
    }

}

