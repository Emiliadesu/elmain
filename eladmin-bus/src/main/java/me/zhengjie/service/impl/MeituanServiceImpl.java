package me.zhengjie.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.mq.CBOrderProducer;
import me.zhengjie.rest.model.meituan.*;
import me.zhengjie.service.*;
import me.zhengjie.support.meituan.*;
import me.zhengjie.utils.AESUtils;
import me.zhengjie.utils.CollectionUtils;
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.constant.MsgType;
import me.zhengjie.utils.enums.BooleanEnum;
import me.zhengjie.utils.enums.CBOrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MeituanServiceImpl implements MeituanService {
    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

    @Autowired
    private CombinationOrderService combinationOrderService;

    @Autowired
    private CombSplitService combSplitService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private ShopTokenService shopTokenService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private OrderLogService orderLogService;

    @Autowired
    private CBOrderProducer cbOrderProducer;

    @Autowired
    private MeiTuanSupport meiTuanSupport;

    @Override
    public void createOrder(MeiTuanOrder meiTuanOrder) throws Exception {
        try {
            // 查询确认是否已保存过
            CrossBorderOrder exists = crossBorderOrderService.queryByOrderNo(meiTuanOrder.getWmOrderIdView().toString());
            if (exists != null) {
                // 已保存过
                return;
            }
            ShopToken shopToken = shopTokenService.queryByPaltShopId(meiTuanOrder.getAppPoiCode());
            ShopInfo shopInfo;
            if (shopToken == null) {
                shopInfo = shopInfoService.queryByShopCode(meiTuanOrder.getAppPoiCode());
            } else {
                shopInfo = shopInfoService.findById(shopToken.getShopId());
            }
            if (shopInfo == null)
                throw new BadRequestException("没有登记这个店铺编码:" + meiTuanOrder.getAppPoiCode());
            // 将订单保存进数据库
            CrossBorderOrder order = new CrossBorderOrder();

            order.setCreateTime(new Timestamp(System.currentTimeMillis()));
            order.setCreateBy("SYSTEM");
            order.setStatus(CBOrderStatusEnum.STATUS_200.getCode());
            order.setShopId(shopInfo.getId());
            order.setPlatformShopId(meiTuanOrder.getAppPoiCode());
            order.setCustomersId(shopInfo.getCustId());
            order.setUpStatus(meiTuanOrder.getStatus().toString());


            order.setOrderNo(meiTuanOrder.getWmOrderIdView().toString());
            order.setCrossBorderNo(meiTuanOrder.getWmOrderIdView().toString());
            order.setOrderCreateTime(new Timestamp(meiTuanOrder.getCtime() * 1000L));

            // 这些数据一期时间紧先写死
            order.setPlatformCode("MeiTuan");
            order.setOrderForm("1524");
            order.setPostFee(meiTuanOrder.getShippingFee().toString());
            order.setPayment(meiTuanOrder.getTotal().toString());// 实际支付金额
            order.setBuyerAccount("null");
            order.setBuyerPhone(meiTuanOrder.getRecipientPhone());
            order.setPayTime(new Timestamp(meiTuanOrder.getCtime() * 1000L));
            order.setBooksNo(shopInfo.getBooksNo());
            order.setPayCode("63");
            RecipientAddressDetail address = JSONObject.parseObject(meiTuanOrder.getRecipientAddressDetail(), RecipientAddressDetail.class);
            order.setProvince(address.getProvince());
            order.setCity(address.getCity());
            order.setDistrict(address.getArea());
            order.setConsigneeAddr(address.getProvince() + " " + address.getCity() + " " + address.getArea() + " " + address.getTown() + " " + address.getDetailAddress());
            order.setConsigneeTel(meiTuanOrder.getRecipientPhone());
            order.setConsigneeName(meiTuanOrder.getRecipientName());

            // 是否需要冻结标记
            boolean needFreeze = false;
            String freezeReason = "";
            BigDecimal amount = BigDecimal.ZERO;
            BigDecimal disAmountTotal = BigDecimal.ZERO;
            // 处理明细数据
            List<CrossBorderOrderDetails> list = new ArrayList<>();
            List<SkuBenefitDetail> meiTuanDetails = JSONArray.parseArray(meiTuanOrder.getSkuBenefitDetail(), SkuBenefitDetail.class);
            if (CollectionUtils.isNotEmpty(meiTuanDetails)) {
                for (SkuBenefitDetail meiTuanDetail : meiTuanDetails) {
                    String goodsNo = meiTuanDetail.getAppFoodCode();
                    disAmountTotal=disAmountTotal.add(meiTuanDetail.getTotalReducePrice());
                    BigDecimal manjian=BigDecimal.ZERO;
                    List<WmAppOrderActDetail>wmDetail=JSONArray.parseArray(meiTuanDetail.getWmAppOrderActDetails(),WmAppOrderActDetail.class);
                    if (CollectionUtils.isNotEmpty(wmDetail)){
                        for (WmAppOrderActDetail wmAppOrderActDetail : wmDetail) {
                            if (wmAppOrderActDetail.getType().toString().matches("2|7|16|17|20|25|26|27|30|31|34|35|40|43|55|56")){
                                //满减的优惠
                                manjian=manjian.add(wmAppOrderActDetail.getMtCharge()).add(wmAppOrderActDetail.getPoiCharge());
                            }
                        }
                    }
                    disAmountTotal=disAmountTotal.subtract(manjian);
                    BigDecimal localAmount=meiTuanDetail.getOriginPrice().multiply(new BigDecimal(meiTuanDetail.getCount())).subtract(manjian);
                    amount = amount.add(localAmount);
                    CombinationOrder combinationOrder = combinationOrderService.queryByCombSku(goodsNo);
                    if (combinationOrder != null) {
                        //组合单
                        List<CombSplit> splitList = combSplitService.queryByCombId(combinationOrder.getId());
                        for (CombSplit combSplit : splitList) {
                            BigDecimal payment;
                            if (splitList.size() == 1 && meiTuanDetails.size() == 1) {//如果拆出的商品只有一种sku，直接用有赞的支付价格
                                payment = localAmount;
                            } else {
                                payment = localAmount.
                                        multiply(new BigDecimal(combSplit.getQty())).
                                        divide(new BigDecimal(combinationOrder.getSplitQty()), 2, BigDecimal.ROUND_HALF_UP);
                            /*details.getPayment()*combSplit.getQty()/combinationOrder.getSplitQty()
                            组合包实付价*被拆出商品的数量/一共拆出的数量
                            */
                            }

                            CrossBorderOrderDetails detail = new CrossBorderOrderDetails();
                            BaseSku baseSku = baseSkuService.queryByGoodsNo(combSplit.getSplitSkuId());
                            if (baseSku == null) {
                                needFreeze = true;
                                freezeReason = combSplit.getSplitSkuId() + "未创建货品";
                            } else {
                                detail.setGoodsId(baseSku.getId());
                                detail.setGoodsCode(baseSku.getGoodsCode());
                                detail.setBarCode(baseSku.getBarCode());
                            }
                            detail.setOrderNo(order.getOrderNo());
                            detail.setGoodsNo(combSplit.getSplitSkuId());
                            detail.setQty((combSplit.getQty() * meiTuanDetail.getCount()) + "");
                            detail.setPayment(payment.toString());
                            list.add(detail);
                        }
                    } else {
                        CrossBorderOrderDetails details = new CrossBorderOrderDetails();
                        BaseSku baseSku = baseSkuService.queryByGoodsNo(goodsNo);
                        if (baseSku == null) {
                            needFreeze = true;
                            freezeReason = goodsNo + "未创建货品";
                        } else {
                            details.setGoodsId(baseSku.getId());
                            details.setGoodsCode(baseSku.getGoodsCode());
                            details.setBarCode(baseSku.getBarCode());
                        }
                        details.setOrderNo(order.getOrderNo());
                        details.setGoodsNo(goodsNo);
                        details.setQty(meiTuanDetail.getCount() + "");
                        details.setPayment(localAmount.toString());
                        list.add(details);
                    }
                }
            }else {
                //无优惠
                List<MeiTuanOrderDetail> orderDetailList = JSONArray.parseArray(meiTuanOrder.getDetail(), MeiTuanOrderDetail.class);
                for (MeiTuanOrderDetail meiTuanDetail : orderDetailList) {
                    String goodsNo = meiTuanDetail.getAppFoodCode();
                    BigDecimal localAmount=meiTuanDetail.getPrice().multiply(new BigDecimal(meiTuanDetail.getQuantity()));
                    amount = amount.add(localAmount);
                    CombinationOrder combinationOrder = combinationOrderService.queryByCombSku(goodsNo);
                    if (combinationOrder != null) {
                        //组合单
                        List<CombSplit> splitList = combSplitService.queryByCombId(combinationOrder.getId());
                        for (CombSplit combSplit : splitList) {
                            BigDecimal payment;
                            if (splitList.size() == 1 && meiTuanDetails.size() == 1) {//如果拆出的商品只有一种sku，直接用有赞的支付价格
                                payment = localAmount;
                            } else {
                                payment = localAmount.
                                        multiply(new BigDecimal(combSplit.getQty())).
                                        divide(new BigDecimal(combinationOrder.getSplitQty()), 2, BigDecimal.ROUND_HALF_UP);
                            /*details.getPayment()*combSplit.getQty()/combinationOrder.getSplitQty()
                            组合包实付价*被拆出商品的数量/一共拆出的数量
                            */
                            }

                            CrossBorderOrderDetails detail = new CrossBorderOrderDetails();
                            BaseSku baseSku = baseSkuService.queryByGoodsNo(combSplit.getSplitSkuId());
                            if (baseSku == null) {
                                needFreeze = true;
                                freezeReason = combSplit.getSplitSkuId() + "未创建货品";
                            } else {
                                detail.setGoodsId(baseSku.getId());
                                detail.setGoodsCode(baseSku.getGoodsCode());
                                detail.setBarCode(baseSku.getBarCode());
                            }
                            detail.setOrderNo(order.getOrderNo());
                            detail.setGoodsNo(combSplit.getSplitSkuId());
                            detail.setQty((combSplit.getQty() * meiTuanDetail.getQuantity()) + "");
                            detail.setPayment(payment.toString());
                            list.add(detail);
                        }
                    } else {
                        CrossBorderOrderDetails details = new CrossBorderOrderDetails();
                        BaseSku baseSku = baseSkuService.queryByGoodsNo(goodsNo);
                        if (baseSku == null) {
                            needFreeze = true;
                            freezeReason = goodsNo + "未创建货品";
                        } else {
                            details.setGoodsId(baseSku.getId());
                            details.setGoodsCode(baseSku.getGoodsCode());
                            details.setBarCode(baseSku.getBarCode());
                        }
                        details.setOrderNo(order.getOrderNo());
                        details.setGoodsNo(goodsNo);
                        details.setQty(meiTuanDetail.getQuantity() + "");
                        details.setPayment(localAmount.toString());
                        list.add(details);
                    }
                }
            }
            order.setDisAmount(disAmountTotal.toString());
            order.setItemList(list);
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
            } else {
                // 不接单，直接等美团推送支付信息的节点再去接单
                /*cbOrderProducer.send(
                        MsgType.CB_ORDER_215,
                        String.valueOf(orderDto.getId()),
                        order.getOrderNo()
                );*/
                //confirmOrder(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 记录异常日志，继续循环
            OrderLog orderLog = new OrderLog(
                    0L,
                    meiTuanOrder.getWmOrderIdView() + "",
                    String.valueOf(CBOrderStatusEnum.STATUS_200.getCode()),
                    JSONObject.toJSONString(meiTuanOrder),
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
    public void cancelOrder(MeiTuanCancel cancel) {
        CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(cancel.getOrderId() + "");
        if (order != null) {
            ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());
            if (order.getStatus() > 200 && order.getStatus() < 240) {
                //已接单的，需要取消订单
                try {
                    crossBorderOrderService.cancel(order.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                    if (StringUtil.equals(e.getMessage(), "当前状态不允许取消发货")) {
                        //拒绝取消
                        MeiTuanOrderRefundRejectRequest request = new MeiTuanOrderRefundRejectRequest();
                        request.setOrderId(cancel.getOrderId() + "");
                        request.setReason("订单已称重或已出库");
                        /*Map<String,Object>map=new HashMap<>();
                        map.put("accessToken",shopToken.getAccessToken());
                        map.put("request",request);
                        cbOrderProducer.send(
                                MsgType.MEITUAN_REFUND_REJECT,
                                JSONObject.toJSONString(request),
                                order.getOrderNo());*/
                        MeiTuanBaseResponse response=meiTuanSupport.refundReject(request, shopToken.getAccessToken());
                        if (!StringUtil.equals(response.getData(),"ok")){
                            OrderLog orderLog = new OrderLog(
                                    order.getId(),
                                    order.getOrderNo(),
                                    String.valueOf(CBOrderStatusEnum.STATUS_888.getCode()),
                                    BooleanEnum.FAIL.getCode(),
                                    response.toString()
                            );
                            orderLogService.create(orderLog);
                            throw new BadRequestException("错误代码"+response.getError().getCode()+":"+response.getError().getMsg());
                        }
                    }
                }
            } else if (order.getStatus()>=240&&order.getStatus()<=245){
                //拒绝
                MeiTuanOrderRefundRejectRequest request = new MeiTuanOrderRefundRejectRequest();
                request.setOrderId(cancel.getOrderId() + "");
                request.setReason("订单已称重或已出库");
                        /*Map<String,Object>map=new HashMap<>();
                        map.put("accessToken",shopToken.getAccessToken());
                        map.put("request",request);
                        cbOrderProducer.send(
                                MsgType.MEITUAN_REFUND_REJECT,
                                JSONObject.toJSONString(request),
                                order.getOrderNo());*/
                MeiTuanBaseResponse response=meiTuanSupport.refundReject(request, shopToken.getAccessToken());
                if (!StringUtil.equals(response.getData(),"ok")){
                    OrderLog orderLog = new OrderLog(
                            order.getId(),
                            order.getOrderNo(),
                            String.valueOf(CBOrderStatusEnum.STATUS_888.getCode()),
                            BooleanEnum.FAIL.getCode(),
                            response.toString()
                    );
                    orderLogService.create(orderLog);
                    throw new BadRequestException("错误代码"+response.getError().getCode()+":"+response.getError().getMsg());
                }
            }
            else {
                //同意取消
                MeiTuanOrderCancelRequest request = new MeiTuanOrderCancelRequest();
                request.setOrderId(cancel.getOrderId() + "");
                /*Map<String,Object>map=new HashMap<>();
                        map.put("accessToken",shopToken.getAccessToken());
                        map.put("request",request);
                        cbOrderProducer.send(
                                MsgType.MEITUAN_CANCEL,
                                JSONObject.toJSONString(request),
                                order.getOrderNo());*/
                MeiTuanBaseResponse response=meiTuanSupport.confirmCancel(request, shopToken.getAccessToken());
                if (!StringUtil.equals(response.getData(),"ok")){
                    OrderLog orderLog = new OrderLog(
                            order.getId(),
                            order.getOrderNo(),
                            String.valueOf(CBOrderStatusEnum.STATUS_888.getCode()),
                            BooleanEnum.FAIL.getCode(),
                            response.toString()
                    );
                    orderLogService.create(orderLog);
                    order.setStatus(CBOrderStatusEnum.STATUS_999.getCode());
                    order.setFreezeReason("订单已退款，回传取消订单失败");
                    throw new BadRequestException("错误代码"+response.getError().getCode()+":"+response.getError().getMsg());
                }
                order.setStatus(CBOrderStatusEnum.STATUS_888.getCode());
                crossBorderOrderService.update(order);
            }
        }
        //找不到订单的，因为不知道该订单是哪个商家的，所以不推送退款结果
    }

    @Override
    public void refundReject(String body) throws Exception {
        JSONObject object = JSONObject.parseObject(body);
        MeiTuanOrderRefundRejectRequest request = object.getObject("request", MeiTuanOrderRefundRejectRequest.class);
        meiTuanSupport.refundReject(request, object.getString("accessToken"));
    }

    @Override
    public void confirmOrder(String body) throws Exception {
        CrossBorderOrder order = crossBorderOrderService.queryById(Long.parseLong(body));
        if (order == null)
            return;
        confirmOrder(order);
    }

    @Override
    public void getCrossBorderInfo(String body) throws Exception {
        CrossBorderOrder order = crossBorderOrderService.queryById(Long.valueOf(body));
        getCrossBorderInfo(order);
    }

    @Override
    public void refundAgree(String body) throws Exception {
        JSONObject object = JSONObject.parseObject(body);
        MeiTuanOrderRefundAgreeRequest request = object.getObject("request", MeiTuanOrderRefundAgreeRequest.class);
        meiTuanSupport.confirmRefund(request, object.getString("accessToken"));
    }

    @Override
    public void refundOrder(MeiTuanRefund refund) {
        CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(refund.getOrderId() + "");
        if (order != null) {
            if (StringUtil.equals(refund.getStatus(),"1")){
                order.setIsLock("1");
                order.setRefundReason(refund.getReason());
                crossBorderOrderService.update(order);
            }else if (StringUtil.equals(refund.getStatus(),"30")){
                order.setIsLock("0");
                order.setRefundReason(refund.getReason());
                crossBorderOrderService.update(order);
            }
        }
        //找不到订单的，因为不知道该订单是哪个商家的，所以不推送退款结果
    }

    @Override
    public void cancelOrder(String body) {
        JSONObject obj = JSONObject.parseObject(body);
        MeiTuanOrderCancelRequest request = obj.getObject("request", MeiTuanOrderCancelRequest.class);
        meiTuanSupport.confirmCancel(request, obj.getString("accessToken"));
    }

    @Override
    public void confirmOrder(CrossBorderOrder order) {
        MeiTuanOrderConfirmRequest request = new MeiTuanOrderConfirmRequest();
        request.setOrderId(order.getOrderNo());
        ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());
        try {
            shopTokenService.testTokenOverdue(shopToken);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
        MeiTuanBaseResponse response = meiTuanSupport.confirmOrder(request, shopToken.getAccessToken());
        if (!StringUtil.equals(response.getData(), "ok")) {
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_215.getCode()),
                    BooleanEnum.FAIL.getCode(),
                    response.toString()
            );
            orderLogService.create(orderLog);
            if (StringUtil.contains(response.getError().getMsg(),"订单已经确认过了")){
                order.setStatus(CBOrderStatusEnum.STATUS_215.getCode());
                crossBorderOrderService.update(order);
            }else
                throw new BadRequestException("错误代码" + response.getError().getCode() + ":" + response.getError().getMsg());
        }
        OrderLog orderLog = new OrderLog(
                order.getId(),
                order.getOrderNo(),
                String.valueOf(CBOrderStatusEnum.STATUS_215.getCode()),
                BooleanEnum.SUCCESS.getCode(),
                response.toString()
        );
        orderLogService.create(orderLog);
        order.setStatus(CBOrderStatusEnum.STATUS_215.getCode());
        crossBorderOrderService.update(order);
        cbOrderProducer.send(
                MsgType.CB_ORDER_220,
                JSONObject.toJSONString(request),
                order.getOrderNo());
    }

    @Override
    public void getCrossBorderInfo(CrossBorderOrder order) {
        ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());
        MeiTuanGetCrossBorderDetailRequest request = new MeiTuanGetCrossBorderDetailRequest();
        request.setWmOrderIdView(order.getCrossBorderNo());
        request.setAppPoiCode(shopToken.getPlatformShopId());
        MeiTuanGetCrossBorderDetailResponse response = meiTuanSupport.getCrossBorderDetail(request, shopToken.getAccessToken());
        if (StringUtil.indexOf(response.getData(), "{") == 0) {
            MeiTuanCrossBorderDetail detail = JSONObject.parseObject(response.getData(), MeiTuanCrossBorderDetail.class);
            order.setBuyerIdNum(detail.getBuyerIdNumber());
            order.setBuyerName(detail.getBuyerName());
            order.setEbpCode(detail.getEbpCode());
            order.setEbpName(detail.getEbpName());
            order.setPaymentNo(detail.getPayTransactionId());
            order.setOrderSeqNo(detail.getPayTransactionId());
            //order.setTaxAmount(detail.getTaxTotal());
        }
    }

    @Override
    public void setDeclareInfo(MeiTuanCrossBorderDetail detail) throws Exception {
        CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(detail.getWmOrderIdView());
        if (order == null)
            throw new BadRequestException("找不到该订单");
        detail.setPayerIdNumber(AESUtils.decrypt(detail.getPayerIdNumber(), meiTuanSupport.getAppSecret().substring(0, 16), meiTuanSupport.getAppSecret().substring(0, 16)));
        detail.setPayerName(AESUtils.decrypt(detail.getPayerName(), meiTuanSupport.getAppSecret().substring(0, 16), meiTuanSupport.getAppSecret().substring(0, 16)));
        detail.setBuyerIdNumber(AESUtils.decrypt(detail.getBuyerIdNumber(), meiTuanSupport.getAppSecret().substring(0, 16), meiTuanSupport.getAppSecret().substring(0, 16)));
        detail.setBuyerName(AESUtils.decrypt(detail.getBuyerName(), meiTuanSupport.getAppSecret().substring(0, 16), meiTuanSupport.getAppSecret().substring(0, 16)));
        order.setBuyerIdNum(detail.getBuyerIdNumber());
        order.setBuyerName(detail.getBuyerName());
        order.setEbpCode(detail.getEbpCode());
        order.setEbpName(detail.getEbpName());
        order.setPaymentNo(detail.getPayTransactionId());
        order.setOrderSeqNo(detail.getPayTransactionId());
        crossBorderOrderService.update(order);
        cbOrderProducer.delaySend(MsgType.CB_ORDER_215,order.getId()+"",order.getOrderNo(),3000);
    }

    @Override
    public void setDeclareInfo(String body) throws Exception {
        if (StringUtil.isBlank(body))
            return;
        MeiTuanCrossBorderDetail detail = JSONObject.parseObject(body, MeiTuanCrossBorderDetail.class);
        setDeclareInfo(detail);
    }

    @Override
    public Integer getOrderStatus(String orderNo, Long shopId) {
        MeiTuanViewStatusRequest request=new MeiTuanViewStatusRequest();
        ShopToken shopToken=shopTokenService.queryByShopId(shopId);
        request.setOrderId(orderNo);
        MeiTuanViewStatusResponse response=meiTuanSupport.getStatus(request,shopToken.getAccessToken());
        return response.getStatus();
    }

    @Override
    public void confirmDeliver(CrossBorderOrder order) throws Exception {
        ShopToken shopToken=shopTokenService.queryByShopId(order.getShopId());
        MeiTuanLogisticsRequest request = new MeiTuanLogisticsRequest();
        request.setOrderId(order.getOrderNo());
        request.setLogisticsCode(order.getLogisticsNo());
        request.setLogisticsProviderCode("10021");
        MeiTuanBaseResponse response = meiTuanSupport.logistics(request,shopToken.getAccessToken());
        if (!StringUtil.equals(response.getData(),"ok")){
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_245.getCode()),
                    BooleanEnum.FAIL.getCode(),
                    "错误代码"+response.getError().getCode()+":"+response.getError().getMsg()
            );
            orderLogService.create(orderLog);
            throw new BadRequestException(orderLog.getMsg());
        }
        order.setDeliverTime(new Timestamp(System.currentTimeMillis()));
        order.setStatus(CBOrderStatusEnum.STATUS_245.getCode());
        crossBorderOrderService.update(order);
        OrderLog orderLog = new OrderLog(
                order.getId(),
                order.getOrderNo(),
                String.valueOf(CBOrderStatusEnum.STATUS_245.getCode()),
                BooleanEnum.SUCCESS.getCode(),
                response.toString()
        );
        orderLogService.create(orderLog);
    }

    @Override
    public void refundOrder(String body) throws Exception {
        MeiTuanRefund refund=JSONObject.parseObject(body,MeiTuanRefund.class);
        refundOrder(refund);
    }

    @Override
    public void createOrder(String body) throws Exception {
        createOrder(JSONObject.parseObject(body,MeiTuanOrder.class));
    }
}
