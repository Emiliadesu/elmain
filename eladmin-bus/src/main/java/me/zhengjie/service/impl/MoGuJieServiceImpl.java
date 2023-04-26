package me.zhengjie.service.impl;

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.mq.CBOrderProducer;
import me.zhengjie.service.*;
import me.zhengjie.support.moGuJie.*;
import me.zhengjie.utils.BigDecimalUtils;
import me.zhengjie.utils.CollectionUtils;
import me.zhengjie.utils.DateUtils;
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.enums.BooleanEnum;
import me.zhengjie.utils.enums.CBOrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import me.zhengjie.utils.constant.MsgType;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MoGuJieServiceImpl implements MoGuJieService {

    @Autowired
    private PlatformService platformService;

    @Autowired
    private ShopTokenService shopTokenService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private PullOrderLogService pullOrderLogService;


    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

    @Autowired
    private MoGuJieSupport moGuJieSupport;

    @Autowired
    private OrderLogService orderLogService;

    @Autowired
    private CBOrderProducer cbOrderProducer;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private CombinationOrderService combinationOrderService;

    @Autowired
    private CombSplitService combSplitService;


    /**
     * 拉单
     */
    @Override
    public void moGuJiePullOrder() {
        Platform platform = platformService.findByCode("MGJ");
        if (platform == null)
            return;
        List<ShopToken> shopTokenList = shopTokenService.queryEnableTokenByPlatId(platform.getId());
        for (ShopToken shopToken : shopTokenList) {
            //是否允许拉单操作   是1 表示开启拉单
            try {
                shopTokenService.testTokenOverdue(shopToken);
            }catch (Exception e){
                e.printStackTrace();
            }
            if (!StringUtil.equals("1", shopToken.getPullOrderAble())) {
                // 没开启拉单
                continue;
            }
            // shopToken.getShopId() 是shopToken类的店铺ID(Long)
            ShopInfo shopInfo = shopInfoService.findById(shopToken.getShopId());
            PullOrderLog pullTime = pullOrderLogService.getPullTime(shopToken.getShopId());
            if (pullTime == null) {
                // 新商家一次都还没拉单过，那么拉单时间设置到前一天开始
                // 注意，如果是从老系统切换过来的商家，那么数据库手动新增一条结束时间到当天16点05分拉单成功数据
                // 这样老商家就会从当天16点开始拉单，达到切换效果
                pullTime = new PullOrderLog();
                pullTime.setPageNo(1);
                pullTime.setEndTime(new Timestamp(System.currentTimeMillis()));
                pullTime.setStartTime(new Timestamp(System.currentTimeMillis() - 3600 * 1000 * 24));
            }
            pullTime.setPageSize(50);//蘑菇街最大值50
            if (pullTime.getPageNo() == 0) {
                pullTime.setPageNo(1);
            }
            pullOrder(shopToken, shopInfo, pullTime);
        }

    }

    /**
     * 24小时拉单
     */
    @Override
    public void pullOrderByHours(Integer hours) {
        Platform platform = platformService.findByCode("MGJ");
        if (platform == null)
            return;
        List<ShopToken> shopTokenList = shopTokenService.queryEnableTokenByPlatId(platform.getId());
        for (ShopToken shopToken : shopTokenList) {
            if (!StringUtil.equals("1", shopToken.getPullOrderAble())) {
                // 没开启拉单
                continue;
            }
            ShopInfo shopInfo = shopInfoService.findById(shopToken.getShopId());
            PullOrderLog pullTime = new PullOrderLog();
            pullTime.setPageNo(1);
            pullTime.setPageSize(50);//蘑菇街最大值50
            pullTime.setEndTime(new Timestamp(System.currentTimeMillis()));
            pullTime.setStartTime(new Timestamp(System.currentTimeMillis() - hours*3600 * 1000));
            pullOrder(shopToken, shopInfo, pullTime);
        }

    }

    private void pullOrder(ShopToken shopToken, ShopInfo shopInfo, PullOrderLog pullTime) {
        try {
            //定义参数
            String token = shopToken.getAccessToken();
            MogujiePullOrderRequest request=new MogujiePullOrderRequest();
            request.setStartCreated(DateUtils.formatDateTime(pullTime.getStartTime()));
            request.setEndCreated(DateUtils.formatDateTime(pullTime.getEndTime()));
            request.setOrderStatus("ORDER_PAID");
            request.setPage(pullTime.getPageNo());
            request.setPageSize(pullTime.getPageSize());
            request.setLShopId(shopInfo.getId());
            request.setCustId(shopInfo.getCustId());
            log.info("蘑菇街拉单：请求参数："+JSONObject.toJSONString(request));
            MogujieTradeSoldGetResp res = moGuJieSupport.pullOrder(token, request);
            log.info("蘑菇街拉单：返回：{}", JSONObject.toJSONString(res));
            Long totalNum;
            if (res.getSuccess()) {
                // 拉取成功
                totalNum = res.getResult().getData().getTotalNum().longValue();
                if (totalNum < pullTime.getPageSize()) {
                    //没有下一页
                    res.setLShopId(shopInfo.getId());
                    res.setCustId(shopInfo.getCustId());
                    PullOrderLog log = new PullOrderLog(
                            shopInfo.getId(),
                            pullTime.getStartTime(),
                            pullTime.getEndTime(),
                            pullTime.getPageNo(),
                            pullTime.getPageSize(),
                            "F",
                            totalNum.toString(),
                            "T",
                            "成功," + JSON.toJSONString(res)
                    );
                    pullOrderLogService.create(log);
                    handleOrder(res);
                } else {
                    // 循环分页拉取数据
                    // 计算总页数
                    long totalPage = res.getResult().getData().getTotalNum() % pullTime.getPageSize() == 0 ?
                            res.getResult().getData().getTotalNum() / pullTime.getPageSize()
                            : res.getResult().getData().getTotalNum() / pullTime.getPageSize() + 1;
                    for (int i = pullTime.getPageNo(); i <= totalPage; i++) {
                        request.setPage(i);
                        String jsonString = JSON.toJSONString(request);
                        // 发送MQ消息
                        cbOrderProducer.send(
                                MsgType.CB_ORDER_PULL_MGJ,
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

    private void handleOrder(MogujieTradeSoldGetResp response) throws Exception{
        log.info("蘑菇街订单分页拉单开始处理，参数：{}", response);
        List<MogujieTradeSoldGetResp.OrderData> dataList = response.getResult().getData().getOpenApiOrderDetailResDtos();
        if (CollectionUtils.isEmpty(dataList))
            return;
        for (MogujieTradeSoldGetResp.OrderData data : dataList) {
            data.setCustId(response.getCustId());
            data.setLShopId(response.getLShopId());
            String jsonString = JSON.toJSONString(data);
            log.info("蘑菇街订单拉单单个订单发送消息队列：{}", jsonString);
            // 保存订单通知
            cbOrderProducer.send(
                    MsgType.CB_ORDER_200_MGJ,
                    jsonString,
                    data.getShipExpressId()
            );
            //createOrder(jsonString);
        }
        log.info("蘑菇街订单分页拉单开始处理完成");
    }

    /**
     * 查询订单在平台的状态
     *
     * @param orderNo  要查询订单的状态
     * @param shopId
     * @return
     * @throws Exception
     */
    @Override
    public String getOrderStatus(String orderNo, Long shopId) throws Exception {
        if (StringUtil.isEmpty(orderNo))
            return null;
        ShopToken shopToken = shopTokenService.queryByShopId(shopId);
        shopTokenService.testTokenOverdue(shopToken);
        MogujieTradeGetResp resp = null;
        for (int i = 0; i < 100; i++) {
            resp=moGuJieSupport.queryByShopOrderNo(shopToken.getAccessToken(), orderNo);
            if (StringUtil.contains(resp.getStatus().getMsg(),"Sign")){
                if (i>98){
                    throw new BadRequestException(resp.getStatus().getMsg());
                }
            }
            else
                break;
        }
        for (MogujieTradeSoldGetResp.OrderDataDetail orderInfo : resp.getResult().getData().getItemOrderInfos()) {
            String refundStatus = "REFUND_REQUESTED,REFUND_OR_RETURN_DEAL_AGREED,REFUND_COMPLETED";
            if (StringUtil.isNotEmpty(orderInfo.getRefundStatus().toUpperCase()) && StringUtil.contains(refundStatus, orderInfo.getRefundStatus().toUpperCase()) && !StringUtil.equals("ORDER_CANCELLED", resp.getResult().getData().getOrderStatus().toUpperCase())) {
                //商品处于退款退货状态,应该退整个订单
                if (StringUtil.equals(orderInfo.getRefundStatus().toUpperCase(), "REFUND_COMPLETED")) {
                    //退款完成
                    return "ORDER_CANCELLED";
                }
                return orderInfo.getRefundStatus().toUpperCase();
            }
        }
        return resp.getResult().getData().getOrderStatus();
    }

    /**
     * 根据订单号拉单
     *
     * @param orderNos
     * @param shopToken
     * @throws Exception
     */
    @Override
    public void pullOrderByOrderNo(String[] orderNos, ShopToken shopToken) throws Exception {
        if (ArrayUtil.isEmpty(orderNos))
            throw new BadRequestException("单号为空");
        for (String orderNo : orderNos) {
            MogujieTradeGetResp resp=moGuJieSupport.queryByShopOrderNo(shopToken.getAccessToken(),orderNo);
            if (!resp.getSuccess())
                continue;
            ShopInfo shopInfo=shopInfoService.findById(shopToken.getShopId());
            resp.getResult().getData().setLShopId(shopToken.getShopId());
            resp.getResult().getData().setCustId(shopInfo.getCustId());
            createOrder(JSONObject.toJSONString(resp.getResult().getData()));
        }
    }

    /**
     * 发货
     *
     * @param orderId 跨境订单id
     * @throws Exception
     */
    @Override
    public void confirmDeliver(String orderId) throws Exception {
        CrossBorderOrder order = crossBorderOrderService.queryById(Long.parseLong(orderId));
        if (order == null)
            throw new BadRequestException("订单id:" + orderId + "不存在");
        confirmDeliver(order);
    }

    /**
     * 发货
     *
     * @param crossBorderOrder 跨境订单
     * @throws Exception
     */
    @Override
    public void confirmDeliver(CrossBorderOrder crossBorderOrder) throws Exception {
        try {
            if (crossBorderOrder.getStatus().intValue() != CBOrderStatusEnum.STATUS_240.getCode().intValue()) {
                throw new BadRequestException("此状态不能回传出库：" + crossBorderOrder.getOrderNo());
            }
            ShopToken shopToken = shopTokenService.queryByShopId(crossBorderOrder.getShopId());
            if (shopToken == null) {
                throw new BadRequestException("店铺未配置授权信息：" + crossBorderOrder.getOrderNo());
            }
            MogujieLogisticsSendRequest request=new MogujieLogisticsSendRequest();
            request.setExpressId(crossBorderOrder.getLogisticsNo());
            request.setShopOrderId(Long.parseLong(crossBorderOrder.getOrderNo()));
            if (StringUtil.isBlank(crossBorderOrder.getLogisticsCode()))
                crossBorderOrder.setLogisticsCode("ZTO");
            switch (crossBorderOrder.getLogisticsCode()){
                case "330296T004":
                case "EMS":
                    request.setExpressCode("ems");
                    break;
                case "11089609XE":
                case "JD":
                    request.setExpressCode("jd");
                    break;
            }
            log.info("蘑菇街开始回传出库,请求：{}", JSONObject.toJSONString(request));
            MogujieSendResp resp = moGuJieSupport.send(shopToken.getAccessToken(), request);
            log.info("蘑菇街开始回传出库,响应：{}", JSONObject.toJSONString(resp));
            if (resp.getSuccess()==null||!resp.getSuccess()) {
                OrderLog orderLog = new OrderLog(
                        crossBorderOrder.getId(),
                        crossBorderOrder.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_245.getCode()),
                        JSONObject.toJSONString(request),
                        JSONObject.toJSONString(resp),
                        BooleanEnum.FAIL.getCode(),
                        resp.getStatus().getMsg()
                );
                orderLogService.create(orderLog);
                throw new BadRequestException(resp.getStatus().getMsg());
            }
            crossBorderOrder.setDeliverTime(new Timestamp(System.currentTimeMillis()));
            crossBorderOrder.setStatus(CBOrderStatusEnum.STATUS_245.getCode());
            crossBorderOrderService.update(crossBorderOrder);
            OrderLog orderLog = new OrderLog(
                    crossBorderOrder.getId(),
                    crossBorderOrder.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_245.getCode()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderLog.setReqMsg(JSON.toJSONString(shopToken));
            orderLog.setResMsg(JSON.toJSONString(resp));
            orderLogService.create(orderLog);

        } catch (Exception e) {
            // 出库回传失败
            OrderLog orderLog = new OrderLog(
                    crossBorderOrder.getId(),
                    crossBorderOrder.getOrderNo(),
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

    /**
     * 根据页码拉单
     *
     * @param body
     * @throws Exception
     */
    @Override
    public void pullOrderByPage(String body) throws Exception {
        //MogujieTradeSoldGetResp params = JSON.parseObject(body, MogujieTradeSoldGetResp.class);
        MogujiePullOrderRequest request=JSONObject.parseObject(body,MogujiePullOrderRequest.class);
        log.info("蘑菇街拉单：请求参数：{}", body);
        ShopToken shopToken = shopTokenService.queryByShopId(request.getLShopId());
        String token = shopToken.getAccessToken();
        MogujieTradeSoldGetResp result = moGuJieSupport.pullOrder(token, request);
        log.info("蘑菇街拉单：返回：{}", JSON.toJSONString(result));
        if (result.getSuccess()) {
            boolean resHasNext = result.getResult().getData().getHasNext();
            result.setLShopId(request.getLShopId());
            result.setCustId(request.getCustId());
            // 将客户、店铺信息插入实体，省得到下一步再去查数据库

            Long totalNum = Long.valueOf(result.getResult().getData().getTotalNum());
            String nextPage = resHasNext ? "T" : "F";
            // 记录拉单日志
            PullOrderLog log = new PullOrderLog(
                    request.getLShopId(),
                    new Timestamp(DateUtils.parseDateTime(request.getStartCreated()).getTime()),
                    new Timestamp(DateUtils.parseDateTime(request.getEndCreated()).getTime()),
                    request.getPage(),
                    request.getPageSize(),
                    nextPage,
                    totalNum.toString(),
                    "T",
                    "成功," + JSON.toJSONString(result)
            );
            pullOrderLogService.create(log);
            handleOrder(result);
        } else {
            PullOrderLog log = new PullOrderLog(
                    request.getLShopId(),
                    new Timestamp(DateUtils.parseDateTime(request.getStartCreated()).getTime()),
                    new Timestamp(DateUtils.parseDateTime(request.getEndCreated()).getTime()),
                    request.getPage(),
                    request.getPageSize(),
                    "E",
                    "0",
                    "F",
                    result.getStatus() + "," + JSON.toJSONString(result)
            );
            pullOrderLogService.create(log);
        }


    }

    /**
     * 保存订单
     *
     * @param body 订单正体json字符串
     * @throws Exception
     */
    @Override
    public void createOrder(String body) throws Exception {
        MogujieTradeSoldGetResp.OrderData orderData = JSON.parseObject(body, MogujieTradeSoldGetResp.OrderData.class);
        try {
            String orderId = orderData.getShopOrderId();

            // 查询确认是否已保存过
            CrossBorderOrder exists = crossBorderOrderService.queryByOrderNo(orderId);
            if (exists != null) {
                // 已保存过
                return;
            }
            // 将订单保存进数据库
            CrossBorderOrder order = new CrossBorderOrder();
            //Long shopId = Long.valueOf(orderData.getShopOrderId()); //暂写, 蘑菇街类没有shopId
            order.setShopId(orderData.getLShopId());
            ShopInfo shopInfo=shopInfoService.findById(orderData.getLShopId());
            order.setCustomersId(shopInfo.getCustId());
            order.setCreateTime(new Timestamp(System.currentTimeMillis()));
            order.setOrderCreateTime(new Timestamp(DateUtils.parseDateTime(orderData.getCreatedStr()).getTime()));
            order.setCreateBy("SYSTEM");
            order.setStatus(CBOrderStatusEnum.STATUS_200.getCode());
            order.setUpStatus(orderData.getOrderStatus());

            order.setOrderNo(orderData.getShopOrderId());
            order.setCrossBorderNo(orderData.getShopOrderId());
            order.setOrderCreateTime(Timestamp.valueOf(orderData.getCreatedStr()));
            order.setEbpCode("3301964M86");
            order.setEbpName("杭州卷瓜网络有限公司");

            // 这些数据一期时间紧先写死
            order.setPlatformCode("MGJ");
            order.setOrderForm("1145");
            order.setDisAmount(new BigDecimal(orderData.getPlatformPromotionAmount()+"").divide(BigDecimal.TEN.multiply(BigDecimal.TEN),2,BigDecimal.ROUND_HALF_UP).toString());
            order.setIsWave("0");
            order.setIsPrint("0");
            order.setSendPickFlag("0");
            //order.setPlatformStatus(2);

            order.setBooksNo(shopInfo.getBooksNo());
            order.setPostFee(orderData.getShipExpense()/100 + "");
            order.setBuyerAccount(orderData.getBuyerAccountId());
            order.setBuyerPhone(orderData.getReceiverMobile());
            order.setBuyerIdNum(orderData.getExtraMap().getIdCardCode());
            order.setBuyerName(orderData.getExtraMap().getIdCardName());
            order.setPayTime(Timestamp.valueOf(orderData.getPayTimeStr()));
            order.setPayment((new BigDecimal(orderData.getShopOrderPrice()+"").divide(BigDecimalUtils.ONEHUNDRED,2,BigDecimal.ROUND_HALF_UP)
                    .subtract(new BigDecimal(orderData.getPlatformPromotionAmount()+"").divide(BigDecimalUtils.ONEHUNDRED,2,BigDecimal.ROUND_HALF_UP))) + "");
            order.setPaymentNo(orderData.getExtraMap().getDeclareNo());
            order.setOrderSeqNo(orderData.getExtraMap().getDeclareNo());
            if (StringUtil.equals(orderData.getExtraMap().getPayChannelCode(), "wechat")) {
                order.setPayCode("13");
            } else {
                order.setPayCode("02");
            }
            order.setProvince(orderData.getReceiverProvince());
            order.setCity(orderData.getReceiverCity());
            order.setDistrict(orderData.getReceiverArea());
            order.setConsigneeAddr(orderData.getReceiverAddress());
            order.setConsigneeTel(orderData.getReceiverPhone());
            order.setConsigneeName(orderData.getReceiverName());

            // 是否需要冻结标记
            boolean needFreeze = false;
            String freezeReason = "";

            List<CrossBorderOrderDetails> list = new ArrayList<>();
            List<MogujieTradeSoldGetResp.OrderDataDetail> itemLists = orderData.getItemOrderInfos();
            if (CollectionUtils.isNotEmpty(itemLists)) {
                for (MogujieTradeSoldGetResp.OrderDataDetail item : itemLists) {
                    CrossBorderOrderDetails details;
                    CombinationOrder combinationOrder = combinationOrderService.queryByCombSku(item.getSkuCode());
                    if (combinationOrder != null) {
                        //组合单
                        List<CombSplit>splitList=combSplitService.queryByCombId(combinationOrder.getId());
                        for (CombSplit combSplit : splitList) {
                            details = new CrossBorderOrderDetails();
                            int num = combSplit.getQty() * item.getNumber().intValue();
                            BigDecimal payment;
                            if (splitList.size() == 1) {
                                payment = new BigDecimal(item.getSellerFinal() + "").divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
                                //买家实际支付价=实付价+优惠
                            } else {
                                payment = new BigDecimal(item.getSellerFinal() + "").divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP)
                                        .multiply(new BigDecimal(combSplit.getQty() + ""))
                                        .divide(new BigDecimal(combinationOrder.getSplitQty()), 2, BigDecimal.ROUND_HALF_UP);
                                /**
                                 * 支付价/拆包数
                                 */
                            }
                            BaseSku baseSku = baseSkuService.queryByGoodsNo(combSplit.getSplitSkuId());
                            if (baseSku == null) {
                                needFreeze = true;
                                freezeReason = combSplit.getSplitSkuId() + "未创建货品";
                            } else {
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
                        details = new CrossBorderOrderDetails();
                        BaseSku baseSku = baseSkuService.queryByGoodsNo(item.getSkuCode());
                        if (baseSku == null) {
                            needFreeze = true;
                            freezeReason = item.getSkuCode() + "未创建货品";
                        } else {
                            details.setGoodsId(baseSku.getId());
                            details.setGoodsCode(baseSku.getGoodsCode());
                            details.setBarCode(baseSku.getBarCode());
                        }
                        details.setOrderNo(order.getOrderNo());
                        details.setGoodsNo(item.getSkuCode());//供应商sku
                        details.setQty(item.getNumber() + "");
                        details.setPayment(new BigDecimal(item.getSellerFinal() + "").divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).toString());
                        list.add(details);
                    }
                }
                order.setItemList(list);

                if(needFreeze){
                    if (StringUtil.isBlank(order.getPaymentNo())){
                        freezeReason+=",无支付单号";
                    }
                }else {
                    if (StringUtil.isBlank(order.getPaymentNo())){
                        freezeReason+=",无支付单号";
                        needFreeze=true;
                    }
                }

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
                } else {
                    // 接单回传通知（预售单不往下走）
                    cbOrderProducer.send(
                            MsgType.CB_ORDER_215,
                            String.valueOf(orderDto.getId()),
                            order.getOrderNo()
                    );
                    //confirmOrder(order);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 记录异常日志，继续循环
            OrderLog orderLog = new OrderLog(
                    0L,
                    String.valueOf(orderData.getShopOrderId()),
                    String.valueOf(CBOrderStatusEnum.STATUS_200.getCode()),
                    body,
                    "",
                    BooleanEnum.FAIL.getCode(),
                    e.getMessage()
            );
            orderLogService.create(orderLog);
            e.printStackTrace();
        }


    }

    @Override
    public void pullOrderByTimeRange(Date startTime, Date endTime, ShopInfo shopInfo) throws Exception{
        ShopToken shopToken=shopTokenService.queryByShopId(shopInfo.getId());
        shopTokenService.testTokenOverdue(shopToken);
        PullOrderLog pullTime = new PullOrderLog();
        pullTime.setPageNo(1);
        pullTime.setPageSize(50);//蘑菇街最大值50
        pullTime.setEndTime(new Timestamp(endTime.getTime()));
        pullTime.setStartTime(new Timestamp(startTime.getTime()));
        pullTime.setPageNo(1);
        pullOrder(shopToken, shopInfo, pullTime);
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
    public long queryShopTotalOrder(String startTime, String endTime, Long shopId) {
        try {
            ShopToken shopToken = shopTokenService.queryByShopId(shopId);
            int pageNo = 1;
            int pageSize = 1;

            MogujiePullOrderRequest request = new MogujiePullOrderRequest();
            request.setStartCreated(startTime);
            request.setEndCreated(endTime);
            request.setPage(pageNo);
            request.setPageSize(pageSize);
            // 第一次先查询本次有多少订单
            MogujieTradeSoldGetResp response = moGuJieSupport.pullOrder(shopToken.getAccessToken(),request);
            if (response.getSuccess()) {
                return response.getResult().getData().getTotalNum();
            }else {
                throw new BadRequestException(response.getStatus().getMsg());
            }
        }catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void refreshToken(ShopToken shopToken) throws Exception {
        moGuJieSupport.refreshToken(shopToken);
    }
}
