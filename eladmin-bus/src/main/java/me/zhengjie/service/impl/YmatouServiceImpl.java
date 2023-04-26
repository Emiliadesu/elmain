package me.zhengjie.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.mq.CBOrderProducer;
import me.zhengjie.service.*;
import me.zhengjie.support.ymatou.*;
import me.zhengjie.utils.DateUtils;
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.constant.MsgType;
import me.zhengjie.utils.enums.BooleanEnum;
import me.zhengjie.utils.enums.CBOrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class YmatouServiceImpl implements YmatouService {

    @Autowired
    private ShopTokenService shopTokenService;

    @Autowired
    private YmatouSupport ymatouSupport;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private PlatformService platformService;

    @Autowired
    private PullOrderLogService pullOrderLogService;

    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

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

    @Override
    public void ymatouPullOrder() {
        Platform platform=platformService.findByCode("Ymatou");
        if (platform==null)
            return;
        List<ShopToken> shopTokenList = shopTokenService.queryEnableTokenByPlatId(platform.getId());
        for (ShopToken shopToken : shopTokenList) {
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
                pullTime.setEndTime(new Timestamp(System.currentTimeMillis()));
                pullTime.setStartTime(new Timestamp(System.currentTimeMillis() - 3600*1000*24));
            }
            if (pullTime.getPageNo()==0){
                pullTime.setPageNo(1);
            }
            pullOrder(shopToken, shopInfo, pullTime);
        }
    }

    private void pullOrder(ShopToken shopToken, ShopInfo shopInfo, PullOrderLog pullTime) {
        Date startTime = new Date(pullTime.getStartTime().getTime());
        Date endTime = new Date(pullTime.getEndTime().getTime());
        int pageNo = pullTime.getPageNo();
        int pageSize = 100;
        YmatouOrderListAPIRequest request = new YmatouOrderListAPIRequest();
        request.setEndDate(endTime);
        request.setStartDate(startTime);
        request.setPageNo(pageNo);
        request.setPageRows(pageSize);
        request.setOrderStatus("17");
        request.setDateType(1);
        request.setSortType(1);
        request.setLShopId(shopInfo.getId());
        request.setCustId(shopInfo.getCustId());
        ymatouSupport.setAuthCode(shopToken.getAccessToken());
        ymatouSupport.setAppId(shopToken.getClientId());
        ymatouSupport.setAppSecret(shopToken.getClientSecret());
        ymatouSupport.setApiParam(request);
        Integer totalNum;
        try {
            log.info("洋码头拉单：请求参数：{}", JSON.toJSONString(request));
            // 第一次先查询本次有多少订单
            YmatouCommonResponse<YmatouOrderListResponse> response = ymatouSupport.request(YmatouOrderListResponse.class);
            log.info("洋码头拉单：返回：{}", JSON.toJSONString(response));
            if (response.isSuccess()) {
                // 拉取成功
                totalNum = response.getContent().getTotal();
                if (totalNum < pageSize) {
                    //没有下一页
                    response.setLShopId(shopInfo.getId());
                    response.setCustId(shopInfo.getCustId());
                    PullOrderLog log = new PullOrderLog(
                            shopInfo.getId(),
                            pullTime.getStartTime(),
                            pullTime.getEndTime(),
                            pageNo,
                            pageSize,
                            "F",
                            totalNum.toString(),
                            "T",
                            "成功,"+ JSON.toJSONString(response)
                    );
                    pullOrderLogService.create(log);
                    handleOrder(response,shopToken.getPlatformShopId());
                } else {
                    // 循环分页拉取数据
                    // 计算总页数
                    int totalPage = response.getContent().getTotal()%request.getPageRows()==0?
                            response.getContent().getTotal()/request.getPageRows()
                            :response.getContent().getTotal()/request.getPageRows()+1;
                    for (int i = pageNo; i <= totalPage; i++) {
                        request.setPageNo(i);
                        request.setPageRows(pageSize);
                        String jsonString=JSON.toJSONString(request);
                        // 发送MQ消息
                        cbOrderProducer.send(
                                MsgType.CB_ORDER_PULL_YZ,
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
        }
    }

    @Override
    public void pullOrderByHours(Integer hours) {
        Platform platform=platformService.findByCode("Ymatou");
        if (platform==null)
            return;
        List<ShopToken> shopTokenList = shopTokenService.queryEnableTokenByPlatId(platform.getId());
        for (ShopToken shopToken : shopTokenList) {
            if (!StringUtil.equals("1", shopToken.getPullOrderAble())) {
                // 没开启拉单
                continue;
            }
            ShopInfo shopInfo=shopInfoService.findById(shopToken.getShopId());
            PullOrderLog pullTime = new PullOrderLog();
            pullTime.setPageNo(1);
            pullTime.setEndTime(new Timestamp(System.currentTimeMillis()));
            pullTime.setStartTime(new Timestamp(System.currentTimeMillis() - 3600*1000*hours));
            pullTime.setPageNo(1);
            pullOrder(shopToken, shopInfo, pullTime);
        }
    }

    @Override
    public void pullOrderByTimeRange(Date startTime, Date endTime, ShopInfo shopInfo) throws Exception {
        ShopToken shopToken=shopTokenService.queryByShopId(shopInfo.getId());
        PullOrderLog pullTime = new PullOrderLog();
        pullTime.setPageNo(1);
        pullTime.setEndTime(new Timestamp(endTime.getTime()));
        pullTime.setStartTime(new Timestamp(startTime.getTime()));
        pullTime.setPageNo(1);
        pullOrder(shopToken, shopInfo, pullTime);
    }

    @Override
    public void pullOrderByOrderNo(String[] orderNos, ShopToken shopToken) throws Exception {
        if (ArrayUtil.isEmpty(orderNos))
            throw new BadRequestException("单号为空");
        for (String orderNo : orderNos) {
            YmatouOrderDetailGetAPIRequest request=new YmatouOrderDetailGetAPIRequest();
            request.setNeedsDeliveryInfo(false);
            request.setOrderId(Long.parseLong(orderNo));
            ymatouSupport.setApiParam(request);
            ymatouSupport.setAppSecret(shopToken.getClientSecret());
            ymatouSupport.setAppId(shopToken.getClientId());
            ymatouSupport.setAuthCode(shopToken.getAccessToken());
            YmatouCommonResponse<YmatouOrderInfo> result=ymatouSupport.request(YmatouOrderInfo.class);
            if (!result.isSuccess())
                continue;
            ShopInfo shopInfo=shopInfoService.findById(shopToken.getShopId());
            result.getContent().setlShopId(shopInfo.getId());
            result.getContent().setCustId(shopInfo.getCustId());
            result.getContent().setShopId(shopToken.getPlatformShopId());
            try {
                createOrder(new JSONObject(result.getContent()).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Integer getOrderStatus(String orderNo, String shopCode) throws Exception {
        if (StringUtil.isEmpty(orderNo))
            return null;
        ShopToken shopToken=shopTokenService.queryByPaltShopId(shopCode);
        //shopTokenService.testTokenOverdue(shopToken);
        YmatouOrdersStatusGetAPIRequest request=new YmatouOrdersStatusGetAPIRequest();
        request.setOrder_ids(new Long[]{Long.parseLong(orderNo)});
        ymatouSupport.setAuthCode(shopToken.getAccessToken());
        ymatouSupport.setAppId(shopToken.getClientId());
        ymatouSupport.setAppSecret(shopToken.getClientSecret());
        ymatouSupport.setApiParam(request);
        YmatouCommonResponse<YmatouOrdersStatusGetResponse>response=ymatouSupport.request(YmatouOrdersStatusGetResponse.class);
        if (response.isSuccess()){
            if (response.getContent().getOrdersStatus()[0]==null)
                throw new BadRequestException("没有查询到状态");
            return response.getContent().getOrdersStatus()[0].getOrderStatus();
        }
        throw new BadRequestException("查询状态失败:"+JSON.toJSONString(response));
    }

    @Override
    public void confirmDeliver(String orderId) throws Exception {
        CrossBorderOrder order=crossBorderOrderService.queryById(Long.parseLong(orderId));
        if (order==null)
            throw new BadRequestException("订单id:"+orderId+"不存在");
        confirmDeliver(order);
    }

    @Override
    public void confirmDeliver(CrossBorderOrder order) throws Exception {
        try {
            if (order.getStatus().intValue() != CBOrderStatusEnum.STATUS_240.getCode().intValue()) {
                throw new BadRequestException("此状态不能回传出库：" + order.getOrderNo());
            }
            ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());
            if (shopToken == null) {
                throw new BadRequestException("店铺未配置授权信息：" + order.getOrderNo());
            }
            log.info("洋码头开始回传出库,单号：{}", order.getOrderNo());
            YmatouOrderDeliverAPIRequest request=new YmatouOrderDeliverAPIRequest();
            YmatouDeliverOrder deliverOrder=new YmatouDeliverOrder();
            deliverOrder.setOrderId(Long.parseLong(order.getOrderNo()));
            if (StringUtil.isBlank(order.getLogisticsCode()))
                order.setLogisticsCode("ZTO");
            switch (order.getLogisticsCode()){
                case "330296T004":
                case "EMS":
                    deliverOrder.setLogisticsCompanyId("Y013");
                    break;
                case "11089609XE":
                case "JD":
                    deliverOrder.setLogisticsCompanyId("Y190");
                    break;
                default:
                    deliverOrder.setLogisticsCompanyId("Y023");
                    break;
            }
            deliverOrder.setTrackingNumber(order.getLogisticsNo());
            request.setDeliverOrders(new YmatouDeliverOrder[]{deliverOrder});
            ymatouSupport.setApiParam(request);
            ymatouSupport.setAppId(shopToken.getClientId());
            ymatouSupport.setAppSecret(shopToken.getClientSecret());
            ymatouSupport.setAuthCode(shopToken.getAccessToken());
            YmatouCommonResponse<YmatouOrderDeliverResponse> response=ymatouSupport.request(YmatouOrderDeliverResponse.class);
            if (!response.isSuccess()){
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_245.getCode()),
                        BooleanEnum.FAIL.getCode(),
                        response.getMessage()
                );
                orderLog.setReqMsg(com.alibaba.fastjson.JSONObject.toJSONString(request));
                orderLog.setResMsg(com.alibaba.fastjson.JSONObject.toJSONString(response));
                orderLogService.create(orderLog);
                throw new BadRequestException(response.getMessage());
            }
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
            orderLog.setReqMsg(JSON.toJSONString(request));
            orderLog.setResMsg(JSON.toJSONString(response));
            orderLogService.create(orderLog);
        }catch (Exception e) {
            // 出库回传失败
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_245.getCode()),
                    BooleanEnum.FAIL.getCode(),
                    e.getMessage(),
                    StringUtil.exceptionStackInfoToString(e)
            );
            orderLogService.create(orderLog);
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void pullOrderByPage(String body) throws Exception {
        YmatouOrderListAPIRequest request=JSON.parseObject(body,YmatouOrderListAPIRequest.class);
        ShopToken shopToken=shopTokenService.queryByShopId(request.getLShopId());
        //shopTokenService.testTokenOverdue(shopToken);
        log.info("洋码头拉单：请求参数：{}", JSON.toJSONString(request));
        YmatouCommonResponse<YmatouOrderListResponse> response=ymatouSupport.request(YmatouOrderListResponse.class);
        log.info("洋码头拉单：返回：{}", JSON.toJSONString(response));
        if (response.isSuccess()) {
            boolean resHasNext = response.getContent().getOrdersInfo().length>=request.getPageRows();
            response.setLShopId(request.getLShopId());
            response.setCustId(request.getCustId());
            // 将客户、店铺信息插入实体，省得到下一步再去查数据库

            int totalNum = response.getContent().getTotal();
            String nextPage = resHasNext?"T":"F";
            // 记录拉单日志
            PullOrderLog log = new PullOrderLog(
                    request.getLShopId(),
                    new Timestamp(request.getStartDate().getTime()),
                    new Timestamp(request.getEndDate().getTime()),
                    request.getPageNo(),
                    request.getPageRows(),
                    nextPage,
                    totalNum+"",
                    "T",
                    "成功,"+JSON.toJSONString(response)
            );
            pullOrderLogService.create(log);
            handleOrder(response,shopToken.getPlatformShopId());
        }else {
            PullOrderLog log = new PullOrderLog(
                    request.getLShopId(),
                    new Timestamp(request.getStartDate().getTime()),
                    new Timestamp(request.getEndDate().getTime()),
                    request.getPageNo(),
                    request.getPageRows(),
                    "E",
                    "0",
                    "F",
                    response.getMessage()+","+JSON.toJSONString(response)
            );
            pullOrderLogService.create(log);
        }
    }

    @Override
    public void createOrder(String body) throws Exception {
        YmatouOrderInfo ymatouOrderInfo = JSON.parseObject(body,YmatouOrderInfo.class);
        try {
            // 查询确认是否已保存过
            CrossBorderOrder exists = crossBorderOrderService.queryByOrderNo(ymatouOrderInfo.getOrderId()+"");
            if (exists != null) {
                // 已保存过
                return;
            }
            ShopInfo shopInfo=shopInfoService.findById(ymatouOrderInfo.getlShopId());
            // 将订单保存进数据库
            CrossBorderOrder order = new CrossBorderOrder();

            order.setCreateTime(new Timestamp(System.currentTimeMillis()));
            order.setCreateBy("SYSTEM");
            order.setStatus(CBOrderStatusEnum.STATUS_200.getCode());
            order.setShopId(ymatouOrderInfo.getlShopId());
            order.setPlatformShopId(ymatouOrderInfo.getShopId());
            order.setCustomersId(ymatouOrderInfo.getCustId());
            order.setUpStatus(ymatouOrderInfo.getOrderStatus()+"");


            order.setOrderNo(ymatouOrderInfo.getOrderId()+"");
            order.setCrossBorderNo(ymatouOrderInfo.getOrderId()+"");
            order.setOrderCreateTime(new Timestamp(DateUtils.parseDateTime(ymatouOrderInfo.getOrderTime()).getTime()));
            order.setEbpCode("3106960576");
            order.setEbpName("上海洋码头网络技术有限公司");

            // 这些数据一期时间紧先写死
            order.setPlatformCode("Ymatou");
            order.setOrderForm("1407");
            order.setDisAmount("0.0");
            order.setPostFee(ymatouOrderInfo.getShippingFee()==null?"0":ymatouOrderInfo.getShippingFee().toString());
            order.setPayment(ymatouOrderInfo.getPayment().toString());// 实际支付金额
            order.setBuyerAccount("null");
            order.setBuyerPhone(ymatouOrderInfo.getReceiverPhone());
            if (ArrayUtil.isNotEmpty(ymatouOrderInfo.getIdCards())){
                order.setBuyerIdNum(ymatouOrderInfo.getIdCards()[0].getReceiverIdNo());
                order.setBuyerName(ymatouOrderInfo.getReceiverName());
            }
            order.setPayTime(new Timestamp(DateUtils.parseDateTime(ymatouOrderInfo.getPaidTime()).getTime()));
            order.setPaymentNo(null);
            order.setOrderSeqNo(null);
            order.setBooksNo(shopInfo.getBooksNo());
            switch (ymatouOrderInfo.getPayType()) {
                case "Weixin":
                    order.setPayCode("13");//财付通
                    break;
                case "AliPayHb":
                case "Alipay":
                    order.setPayCode("02");//支付宝
                    break;
                default:
                    order.setPayCode(ymatouOrderInfo.getPayType());
            }
            String[] addressArray = ymatouOrderInfo.getReceiverAddress().split("[,，]");
            if (addressArray.length >= 4) {
                order.setProvince(addressArray[0]);
                order.setCity(addressArray[1]);
                order.setDistrict(addressArray[2]);
                order.setConsigneeAddr(ymatouOrderInfo.getReceiverAddress());
            }
            order.setConsigneeTel(ymatouOrderInfo.getReceiverPhone());
            order.setConsigneeName(ymatouOrderInfo.getReceiverName());
            // 是否需要冻结标记
            boolean needFreeze = false;
            String freezeReason = "";
            // 处理明细数据
            List<CrossBorderOrderDetails> list = new ArrayList<>();
            YmatouOrderItemInfo[] ymtOrderDetails = ymatouOrderInfo.getOrderItemsInfo();
            if (ArrayUtil.isNotEmpty(ymtOrderDetails)) {
                for (YmatouOrderItemInfo ymtOrderDetail : ymtOrderDetails) {
                    CrossBorderOrderDetails details;
                    CombinationOrder combinationOrder = combinationOrderService.queryByCombSku(ymtOrderDetail.getOuterSkuId());
                    if (combinationOrder != null) {
                        //组合单
                        List<CombSplit>splitList=combSplitService.queryByCombId(combinationOrder.getId());
                        for (CombSplit combSplit : splitList) {
                            details= new CrossBorderOrderDetails();
                            int num = combSplit.getQty() * ymtOrderDetail.getNum();
                            BigDecimal payment;
                            if (splitList.size() == 1) {
                                payment = ymtOrderDetail.getPayment();
                                //买家实际支付价=实付价+优惠
                            } else {
                                payment = ymtOrderDetail.getPayment().multiply(new BigDecimal(combSplit.getQty() + ""))
                                        .divide(new BigDecimal(combinationOrder.getSplitQty()), 2, BigDecimal.ROUND_HALF_UP);
                                /**
                                 * 支付价/拆包数
                                 */
                            }
                            BaseSku baseSku=baseSkuService.queryByGoodsNo(combSplit.getSplitSkuId());
                            if (baseSku == null) {
                                needFreeze = true;
                                freezeReason = combSplit.getSplitSkuId() + "未创建货品";
                            }else {
                                details.setGoodsId(baseSku.getId());
                                details.setGoodsCode(baseSku.getGoodsCode());
                                details.setBarCode(baseSku.getBarCode());
                            }
                            details.setOrderNo(order.getOrderNo());
                            details.setGoodsNo(combSplit.getSplitSkuId());
                            details.setQty(num + "");
                            details.setPayment(payment + "");
                            list.add(details);
                        }
                    } else {
                        details= new CrossBorderOrderDetails();
                        BaseSku baseSku=baseSkuService.queryByGoodsNo(ymtOrderDetail.getOuterSkuId());
                        if (baseSku == null) {
                            needFreeze = true;
                            freezeReason = ymtOrderDetail.getOuterSkuId() + "未创建货品";
                        }else {
                            details.setGoodsId(baseSku.getId());
                            details.setGoodsCode(baseSku.getGoodsCode());
                            details.setBarCode(baseSku.getBarCode());
                        }
                        details.setOrderNo(order.getOrderNo());
                        details.setGoodsNo(ymtOrderDetail.getOuterSkuId());//供应商sku
                        details.setQty(ymtOrderDetail.getNum() + "");
                        details.setPayment(ymatouOrderInfo.getPayment().toString());
                        list.add(details);
                    }
                }
            }
            order.setItemList(list);
            if (order.getCity().length() <= 1)
                order.setCity(order.getDistrict());
            if (StringUtil.contains(order.getCity(),"直辖县")){
                order.setCity(order.getDistrict());
            }
            if(needFreeze){
                if (ArrayUtil.isEmpty(ymatouOrderInfo.getIdCards())||ymatouOrderInfo.getIdCards()[0]==null){
                    freezeReason+=",无跨境身份信息";
                }
            }else {
                if (ArrayUtil.isEmpty(ymatouOrderInfo.getIdCards())||ymatouOrderInfo.getIdCards()[0]==null){
                    freezeReason+=",无跨境身份信息";
                    needFreeze=true;
                }
            }
            CrossBorderOrder orderDto = crossBorderOrderService.createWithDetail(order);
            // 保存订单日志
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_215.getCode()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderLogService.create(orderLog);
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
                // 开始推送支付单通知（预售单不往下走）
                cbOrderProducer.send(
                        MsgType.CB_ORDER_205,
                        String.valueOf(orderDto.getId()),
                        order.getOrderNo()
                );
            }
        }catch (Exception e) {
            e.printStackTrace();
            // 记录异常日志，继续循环
            OrderLog orderLog = new OrderLog(
                    0L,
                    ymatouOrderInfo.getOrderId()+"",
                    String.valueOf(CBOrderStatusEnum.STATUS_200.getCode()),
                    body,
                    "",
                    BooleanEnum.FAIL.getCode(),
                    e.getMessage(),
                    StringUtil.exceptionStackInfoToString(e)
            );
            orderLogService.create(orderLog);
            e.printStackTrace();
        }

    }

    @Override
    public void pushPayOrder(CrossBorderOrder order) throws Exception {
        if (!CBOrderStatusEnum.STATUS_200.getCode().equals(order.getStatus())){
            throw new BadRequestException("非接单状态不可推送支付单");
        }
        ShopToken shopToken=shopTokenService.queryByShopId(order.getShopId());
        if (shopToken==null&&StringUtil.isNotBlank(order.getPaymentNo()))
            return;
        else if (shopToken==null)
            throw new BadRequestException("店铺id:"+order.getShopId()+"没有店铺授权信息且支付单号为空，推送支付单失败");
        YmatouPaymentPushAPIRequest request = new YmatouPaymentPushAPIRequest();
        request.setOrderId(Long.parseLong(order.getOrderNo()));
        request.setCustoms("NINGBO");
        ymatouSupport.setAuthCode(shopToken.getAccessToken());
        ymatouSupport.setAppId(shopToken.getClientId());
        ymatouSupport.setAppSecret(shopToken.getClientSecret());
        ymatouSupport.setApiParam(request);
        YmatouCommonResponse<YmatouPaymentPushResponse> response;
        try {
            response=ymatouSupport.request(YmatouPaymentPushResponse.class);
        }catch (Exception e){
            log.error("支付单推送失败");
            log.error(e.getMessage(),e);
            e.printStackTrace();
            throw new BadRequestException("支付单推送失败：" + e.getMessage());
        }
        if (response.isSuccess()){
            YmatouPaymentPushResponse content = response.getContent();
            YmatouPaymentPushResult result = content.getResult();
            if (result.getExecSuccess()) {
                order.setPaymentNo(result.getPayTransactionId());
                order.setOrderSeqNo(result.getPayTransactionId());
                order.setCrossBorderNo(result.getOrderNo());
                order.setStatus(CBOrderStatusEnum.STATUS_205.getCode());
                crossBorderOrderService.update(order);
                cbOrderProducer.send(
                        MsgType.CB_ORDER_215,
                        String.valueOf(order.getId()),
                        order.getOrderNo()
                );
            } else {
                log.error("支付单推送成功但未成功执行"+result.getMsg());
                throw new BadRequestException("支付单推送成功但未成功执行：" + result.getMsg());
            }
        }else {
            log.error("支付单推送成功但未成功执行"+response.getMessage());
            throw new BadRequestException("支付单推送失败：" + response.getMessage());
        }
    }

    @Override
    public void confirmOrder(CrossBorderOrder order) {
        order.setStatus(CBOrderStatusEnum.STATUS_215.getCode());
        crossBorderOrderService.update(order);
        cbOrderProducer.send(
                MsgType.CB_ORDER_220,
                String.valueOf(String.valueOf(order.getId())),
                order.getOrderNo()
        );
    }

    @Override
    public long queryShopTotalOrder(String startTime, String endTime, Long shopId) throws Exception {
        try {
            ShopToken shopToken = shopTokenService.queryByShopId(shopId);
            int pageNo = 1;
            int pageSize = 1;

            YmatouOrderListAPIRequest request = new YmatouOrderListAPIRequest();
            request.setStartDate(DateUtils.parse(startTime, DatePattern.NORM_DATETIME_PATTERN));
            request.setEndDate(DateUtils.parse(endTime, DatePattern.NORM_DATETIME_PATTERN));
            request.setDateType(1);
            request.setSortType(1);
            request.setPageNo(pageNo);
            request.setPageRows(pageSize);
            ymatouSupport.setAuthCode(shopToken.getAccessToken());
            ymatouSupport.setAppId(shopToken.getClientId());
            ymatouSupport.setAppSecret(shopToken.getClientSecret());
            ymatouSupport.setApiParam(request);
            // 第一次先查询本次有多少订单
            YmatouCommonResponse<YmatouOrderListResponse> response = ymatouSupport.request(YmatouOrderListResponse.class);
            if (response.isSuccess()) {
                return response.getContent().getTotal();
            }else {
                throw new BadRequestException(response.getMessage());
            }
        }catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public String[] rePushPayOrder(CrossBorderOrder order) throws Exception {
        ShopToken shopToken=shopTokenService.queryByShopId(order.getShopId());
        if (shopToken==null)
            throw new BadRequestException("店铺id:"+order.getShopId()+"没有店铺授权信息且支付单号为空，推送支付单失败");
        YmatouPaymentPushAPIRequest request = new YmatouPaymentPushAPIRequest();
        request.setOrderId(Long.parseLong(order.getOrderNo()));
        request.setCustoms("NINGBO");
        ymatouSupport.setAuthCode(shopToken.getAccessToken());
        ymatouSupport.setAppId(shopToken.getClientId());
        ymatouSupport.setAppSecret(shopToken.getClientSecret());
        ymatouSupport.setApiParam(request);
        YmatouCommonResponse<YmatouPaymentPushResponse> response;
        try {
            response=ymatouSupport.request(YmatouPaymentPushResponse.class);
        }catch (Exception e){
            log.error("支付单推送失败");
            log.error(e.getMessage(),e);
            e.printStackTrace();
            throw new BadRequestException("支付单推送失败：" + e.getMessage());
        }
        if (response.isSuccess()){
            YmatouPaymentPushResponse content = response.getContent();
            YmatouPaymentPushResult result = content.getResult();
            if (result.getExecSuccess()) {
                String[]orderNos=new String[2];
                orderNos[0]=result.getPayTransactionId();
                orderNos[1]=result.getOrderNo();
                return orderNos;
            } else {
                log.error("支付单推送成功但未成功执行"+result.getMsg());
                throw new BadRequestException("支付单推送成功但未成功执行：" + result.getMsg());
            }
        }else {
            log.error("支付单推送成功但未成功执行"+response.getMessage());
            throw new BadRequestException("支付单推送失败：" + response.getMessage());
        }
    }

    private void handleOrder(YmatouCommonResponse<YmatouOrderListResponse> response,String shopCode) throws Exception{
        log.info("洋码头订单分页拉单开始处理，参数：{}", response);
        YmatouOrderInfo[] orderInfos = response.getContent().getOrdersInfo();
        if (ArrayUtil.isEmpty(orderInfos))
            return;
        for (YmatouOrderInfo orderInfo : orderInfos) {
            orderInfo.setCustId(response.getCustId());
            orderInfo.setlShopId(response.getLShopId());
            orderInfo.setShopId(shopCode);
            String jsonString = JSON.toJSONString(orderInfo);
            log.info("洋码头订单拉单单个订单发送消息队列：{}", jsonString);
            // 保存订单通知
            cbOrderProducer.send(
                    MsgType.CB_ORDER_200_YMT,
                    jsonString,
                    orderInfo.getOrderId()+""
            );
            //createOrder(jsonString);
        }
        log.info("洋码头订单分页拉单开始处理完成");
    }
}
