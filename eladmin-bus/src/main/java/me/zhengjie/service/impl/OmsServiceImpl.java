package me.zhengjie.service.impl;

import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson.JSON;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.mq.CBOrderProducer;
import me.zhengjie.service.*;
import me.zhengjie.support.oms.OmsSupport;
import me.zhengjie.support.oms.OrderChild;
import me.zhengjie.support.oms.OrderMain;
import me.zhengjie.support.oms.OrderStatusCheck;
import me.zhengjie.utils.DateUtils;
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.constant.MsgType;
import me.zhengjie.utils.enums.BooleanEnum;
import me.zhengjie.utils.enums.CBOrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author luob
 * @description
 * @date 2022/2/13
 */
@Validated
@Service
public class OmsServiceImpl implements OmsService {

    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private OrderLogService orderLogService;

    @Autowired
    private OmsSupport omsSupport;

    @Autowired
    private PlatformService platformService;

    @Autowired
    private CBOrderProducer cbOrderProducer;

    @Override
    public void pushOrder(@Valid OrderMain orderMain) {
        String orderNo = orderMain.getOrderNo();
        CrossBorderOrder exist1 = crossBorderOrderService.queryByOrderNo(orderNo);
        if (exist1 != null)
            throw new BadRequestException("订单号已存在：" + orderNo);
        String crossBorderNo = orderMain.getDeclareNo();
        CrossBorderOrder exist2 = crossBorderOrderService.queryByCrossBorderNo(crossBorderNo);
        if (exist2 != null)
            throw new BadRequestException("申报单号已存在：" + crossBorderNo);
        ShopInfo shopInfo = shopInfoService.queryByShopCode(orderMain.getShopCode());
        if (shopInfo == null)
            throw new BadRequestException("店铺code不存在：" + orderMain.getShopCode());
        CrossBorderOrder order = new CrossBorderOrder();
        order.setCustomersId(shopInfo.getCustId());
        order.setShopId(shopInfo.getId());
        order.setOrderNo(orderNo);
        order.setCrossBorderNo(crossBorderNo);
        Platform platform = platformService.findByCode(orderMain.getPlatformCode());
        if (platform == null)
            throw new BadRequestException("平台编码未配置");
        order.setPlatformCode(orderMain.getPlatformCode());
        order.setPlatformId(platform.getId());
        order.setOrderForm(platform.getOrderForm());
        order.setEbpName(platform.getEbpName());
        order.setEbpCode(platform.getEbpCode());
        order.setOrderCreateTime(new Timestamp(DateUtils.parse(orderMain.getCreateTime(), DatePattern.NORM_DATETIME_PATTERN).getTime()));

        order.setCreateBy("SYSTEM");
        order.setStatus(CBOrderStatusEnum.STATUS_200.getCode());
        order.setCreateTime(new Timestamp(System.currentTimeMillis()));

        order.setBuyerAccount(StringUtil.isBlank(orderMain.getBuyerAccount())?"null":orderMain.getBuyerAccount());
        order.setBuyerName(orderMain.getBuyerName());
        order.setBuyerPhone(orderMain.getBuyerPhone());
        order.setBuyerIdNum(orderMain.getBuyerIdNum());
        // 订单总金额是否还需要保存
        order.setPayment(String.valueOf(orderMain.getPayment()));
        order.setPostFee(String.valueOf(orderMain.getFee()));
        order.setPreSell(orderMain.getPreSell());
        if (orderMain.getExpectDeliverTime() != null) {
            order.setExpDeliverTime(new Timestamp(DateUtils.parse(orderMain.getExpectDeliverTime(), DatePattern.NORM_DATETIME_PATTERN).getTime()));
        }
        order.setTaxAmount(String.valueOf(orderMain.getTaxAmount()));
        order.setDisAmount(String.valueOf(orderMain.getDiscount()));
        order.setConsigneeName(orderMain.getConsigneeName());
        order.setConsigneeTel(orderMain.getConsigneePhone());
        order.setProvince(orderMain.getProvince());
        order.setCity(orderMain.getCity());
        order.setDistrict(orderMain.getDistrict());
        order.setConsigneeAddr(orderMain.getAddress());
        order.setPaymentNo(orderMain.getPaymentNo());
        order.setOrderSeqNo(orderMain.getPaymentNo());
        order.setPayTime(new Timestamp(DateUtils.parse(orderMain.getPayTime(), DatePattern.NORM_DATETIME_PATTERN).getTime()));
        switch (orderMain.getPayType()) {
            case "WEIXIN":
                order.setPayCode("13");//财付通
                break;
            case "ALIPAY":
                order.setPayCode("02");//支付宝
                break;
            default:
                order.setPayCode(orderMain.getPayType());
        }
        List<OrderChild> skuDetails = orderMain.getSkuDetails();
        List<CrossBorderOrderDetails> list = new ArrayList<>();
        for (OrderChild orderChild : skuDetails) {
            BaseSku baseSku = baseSkuService.queryByGoodsNo(orderChild.getGoodsNo());
            if (baseSku == null)
                throw new BadRequestException("货号不存在：" + orderChild.getGoodsNo());
            CrossBorderOrderDetails details = new CrossBorderOrderDetails();
            details.setGoodsId(baseSku.getId());
            details.setGoodsCode(baseSku.getGoodsCode());
            details.setBarCode(baseSku.getBarCode());

            details.setOrderNo(order.getOrderNo());
            details.setGoodsNo(orderChild.getGoodsNo());
            details.setQty(String.valueOf(orderChild.getQty()));
            details.setTaxAmount(String.valueOf(orderChild.getTax()));
            details.setDutiableValue(String.valueOf(orderChild.getDeclarePrice())); // 不含税(申报)单价
            details.setDutiableTotalValue(String.valueOf(orderChild.getDeclarePrice().multiply(new BigDecimal(orderChild.getQty()))));
            details.setPayment(new BigDecimal(details.getDutiableTotalValue()).add(new BigDecimal(details.getTaxAmount())).toString());// 商品支付总价
            list.add(details);
        }
        order.setItemList(list);

        CrossBorderOrder crossBorderOrder=crossBorderOrderService.createWithDetail(order);

        // 保存订单日志
        OrderLog orderLog = new OrderLog(
                order.getId(),
                order.getOrderNo(),
                String.valueOf(CBOrderStatusEnum.STATUS_200.getCode()),
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription()
        );
        orderLogService.create(orderLog);
        // 接单回传通知
        cbOrderProducer.send(
                MsgType.CB_ORDER_215,
                String.valueOf(crossBorderOrder.getId()),
                order.getOrderNo()
        );
    }

    @Override
    public void cancelOrder(String orderNo, String remark, Long customerId) throws Exception{
        CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(orderNo);
        if (order == null)
            throw new BadRequestException("订单号不存在");
        if (!order.getCustomersId().equals(customerId))
            throw new BadRequestException("订单号不属于当前签名的客户");
        if (order.getStatus().equals(CBOrderStatusEnum.STATUS_888.getCode()))
            return;
        //开始取消订单
        crossBorderOrderService.cancel(order.getId());
        order.setRefundReason(remark);
        crossBorderOrderService.update(order);
    }

    @Override
    public void confirmOrder(CrossBorderOrder crossBorderOrder) {
        OrderStatusCheck orderStatusCheck = new OrderStatusCheck();
        orderStatusCheck.setOrderNo(crossBorderOrder.getOrderNo());
        orderStatusCheck.setOperateTime(DateUtils.now());
        orderStatusCheck.setStatus(CBOrderStatusEnum.STATUS_215.getCode());
        statusCheck(orderStatusCheck,crossBorderOrder);
        cbOrderProducer.send(
                MsgType.CB_ORDER_220,
                String.valueOf(crossBorderOrder.getId()),
                crossBorderOrder.getOrderNo()
        );
    }

    private void statusCheck(OrderStatusCheck orderStatusCheck,CrossBorderOrder crossBorderOrder){
        String resp;
        try {
            resp=omsSupport.statusCheck(orderStatusCheck,crossBorderOrder.getCustomersId());
        } catch (Exception e) {
            e.printStackTrace();
            // 保存订单日志
            OrderLog orderLog = new OrderLog(
                    crossBorderOrder.getId(),
                    crossBorderOrder.getOrderNo(),
                    String.valueOf(orderStatusCheck.getStatus()),
                    BooleanEnum.FAIL.getCode(),
                    e.getMessage(),
                    StringUtil.exceptionStackInfoToString(e)
            );
            orderLog.setReqMsg(JSON.toJSONString(orderStatusCheck));
            orderLogService.create(orderLog);
            return;
        }
        crossBorderOrder.setStatus(orderStatusCheck.getStatus());
        if (orderStatusCheck.getStatus()==245){
            crossBorderOrder.setDeliverTime(new Timestamp(System.currentTimeMillis()));
        }
        crossBorderOrderService.update(crossBorderOrder);
        // 保存订单日志
        OrderLog orderLog = new OrderLog(
                crossBorderOrder.getId(),
                crossBorderOrder.getOrderNo(),
                String.valueOf(orderStatusCheck.getStatus()),
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription()
        );
        orderLog.setReqMsg(JSON.toJSONString(orderStatusCheck));
        orderLog.setResMsg(JSON.toJSONString(resp));
        orderLogService.create(orderLog);
    }

    @Override
    public void confirmClearStart(CrossBorderOrder crossBorderOrder) {
        OrderStatusCheck orderStatusCheck = new OrderStatusCheck();
        orderStatusCheck.setOrderNo(crossBorderOrder.getOrderNo());
        orderStatusCheck.setOperateTime(DateUtils.now());
        orderStatusCheck.setStatus(CBOrderStatusEnum.STATUS_225.getCode());
        statusCheck(orderStatusCheck,crossBorderOrder);
    }

    @Override
    public void confirmClearSuccess(CrossBorderOrder crossBorderOrder) {
        OrderStatusCheck orderStatusCheck = new OrderStatusCheck();
        orderStatusCheck.setOrderNo(crossBorderOrder.getOrderNo());
        orderStatusCheck.setOperateTime(DateUtils.now());
        orderStatusCheck.setStatus(CBOrderStatusEnum.STATUS_235.getCode());
        statusCheck(orderStatusCheck,crossBorderOrder);
    }

    @Override
    public void confirmDeliver(CrossBorderOrder crossBorderOrder) {
        OrderStatusCheck orderStatusCheck = new OrderStatusCheck();
        orderStatusCheck.setOrderNo(crossBorderOrder.getOrderNo());
        orderStatusCheck.setOperateTime(DateUtils.now());
        orderStatusCheck.setStatus(CBOrderStatusEnum.STATUS_245.getCode());
        orderStatusCheck.setLogisticsName(crossBorderOrder.getLogisticsName());
        orderStatusCheck.setLogisticsNo(crossBorderOrder.getLogisticsNo());
        statusCheck(orderStatusCheck,crossBorderOrder);
    }

    @Override
    public void confirmCleanErr(CrossBorderOrder crossBorderOrder) {
        OrderStatusCheck orderStatusCheck = new OrderStatusCheck();
        orderStatusCheck.setOrderNo(crossBorderOrder.getOrderNo());
        orderStatusCheck.setOperateTime(DateUtils.now());
        orderStatusCheck.setStatus(CBOrderStatusEnum.STATUS_227.getCode());
        orderStatusCheck.setMsg(crossBorderOrder.getDeclareMsg());
        statusCheck(orderStatusCheck,crossBorderOrder);
    }
}
