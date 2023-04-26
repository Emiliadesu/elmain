package me.zhengjie.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.mq.CBOrderProducer;
import me.zhengjie.service.*;
import me.zhengjie.support.guomei.*;
import me.zhengjie.support.kjg.KJGSupport;
import me.zhengjie.utils.BigDecimalUtils;
import me.zhengjie.utils.DateUtils;
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.StringUtils;
import me.zhengjie.utils.constant.MsgType;
import me.zhengjie.utils.enums.BooleanEnum;
import me.zhengjie.utils.enums.CBOrderStatusEnum;
import org.apache.logging.log4j.core.util.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

@Service
@Slf4j
public class GuoMeiServiceImpl implements GuoMeiService {


    @Autowired
    private CrossBorderOrderDetailsService crossBorderOrderDetailsService;

    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

    @Autowired
    private ShopTokenService shopTokenService;

    @Autowired
    private GuomeiSupport guomeiSupport;

    @Autowired
    private OrderLogService orderLogService;

    @Autowired
    private CBOrderProducer cbOrderProducer;

    @Autowired
    private DepositService depositService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private KJGSupport kjgSupport;

    @Autowired
    private PlatformService platformService;

    @Autowired
    private LogisticsInfoService logisticsInfoService;

    //国美订单下发接收
    @Override
    public void createOrder(JSONObject object) {
        String orderNo = object.getString("orderNo");
        if (StringUtils.isEmpty(orderNo))
            throw new BadRequestException("没有orderNo");
        CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(orderNo);
        if (order != null)
            return;
        String ebpName = object.getString("ebpName");
        if (StringUtils.isEmpty(ebpName))
            throw new BadRequestException("没有ebpName");
        String buyerTelephone = object.getString("buyerTelephone");
        if (StringUtils.isEmpty(buyerTelephone))
            throw new BadRequestException("没有buyerTelephone");
        String city = object.getString("city");
        if (StringUtils.isEmpty(city))
            throw new BadRequestException("没有city");
        //单位 分转换为 元
        BigDecimal freight = object.getBigDecimal("freight");
        if (freight == null)
            throw new BadRequestException("没有freight");
        BigDecimal taxTotal = object.getBigDecimal("taxTotal");
        if (taxTotal == null)
            throw new BadRequestException("没有taxTotal");
        BigDecimal discount = object.getBigDecimal("discount");
        if (discount == null)
            throw new BadRequestException("没有discount");
        BigDecimal goodsValue = object.getBigDecimal("goodsValue");
        if (goodsValue == null)
            throw new BadRequestException("没有goodsValue");
        BigDecimal acturalPaid=object.getBigDecimal("acturalPaid");
        if (acturalPaid == null)
            throw new BadRequestException("没有acturalPaid");
        String consigneeTelephone = object.getString("consigneeTelephone");
        if (StringUtils.isEmpty(consigneeTelephone))
            throw new BadRequestException("没有consigneeTelephone");
        String payTransactionId = object.getString("payTransactionId");
        if (StringUtils.isEmpty(payTransactionId))
            throw new BadRequestException("没有payTransactionId");
        String buyerIdNumber = object.getString("buyerIdNumber");
        if (StringUtils.isEmpty(buyerIdNumber))
            throw new BadRequestException("没有buyerIdNumber");
        String province = object.getString("province");
        if (StringUtils.isEmpty(province))
            throw new BadRequestException("没有province");
        String ebpCode = object.getString("ebpCode");
        if (StringUtils.isEmpty(ebpCode))
            throw new BadRequestException("没有ebpCode");
        String shopId = object.getString("shopId");
        if (StringUtils.isEmpty(shopId))
            throw new BadRequestException("没有shopId");
        String consigneeAddress = object.getString("consigneeAddress");
        if (StringUtils.isEmpty(consigneeAddress))
            throw new BadRequestException("没有consigneeAddress");

        String consignee = object.getString("consignee");
        if (StringUtils.isEmpty(consignee))
            throw new BadRequestException("没有consignee");
        String buyerName = object.getString("buyerName");
        if (StringUtils.isEmpty(buyerName))
            throw new BadRequestException("没有buyerName");

        //国美的新增字段 但不保存
        String ebcName = object.getString("ebcName");  //电商企业名称
        if (StringUtils.isEmpty(ebcName))
            throw new BadRequestException("没有ebcName");
        String ebcCustomsCode = object.getString("ebcCustomsCode");  //电商企业编码
        if (StringUtils.isEmpty(ebcCustomsCode))
            throw new BadRequestException("没有ebcCustomsCode");

        order = new CrossBorderOrder();
        order.setEbpName(ebpName);
        order.setBuyerPhone(buyerTelephone);
        order.setCity(city);
        order.setPostFee(freight.divide(new BigDecimal(100), BigDecimal.ROUND_HALF_DOWN, 2) + "");
        order.setTaxAmount(taxTotal.divide(new BigDecimal(100), BigDecimal.ROUND_HALF_DOWN, 2) + "");
        order.setDisAmount(discount.divide(new BigDecimal(100), BigDecimal.ROUND_HALF_DOWN, 2) + "");
        order.setPayment(acturalPaid.divide(new BigDecimal(100), BigDecimal.ROUND_HALF_DOWN, 2) + "");
        order.setConsigneeTel(consigneeTelephone);
        order.setPaymentNo(payTransactionId);//支付流水号
        order.setBuyerIdNum(buyerIdNumber);
        order.setProvince(province);
        order.setEbpCode(ebpCode);
        order.setPlatformShopId(shopId);
        order.setConsigneeAddr(consigneeAddress);
        order.setOrderNo(orderNo);
        order.setConsigneeName(consignee);
        order.setBuyerName(buyerName);
        ShopInfo shopInfo = shopInfoService.queryByKjgCode(shopId);
        if (shopInfo == null)
            throw new BadRequestException("店铺未维护");
        order.setShopId(shopInfo.getId());
        order.setCustomersId(shopInfo.getCustId());
        order.setClearCompanyId(shopInfo.getServiceId());
        Platform platform=platformService.queryByPlafCode("GM");
        if (platform!=null)
            order.setOrderForm(platform.getOrderForm());
        order.setBooksNo(shopInfo.getBooksNo());
        order.setBuyerAccount("null");
        JSONArray orderDetailList = object.getJSONArray("orderDetailList");
        List<CrossBorderOrderDetails> details = new ArrayList<>();
        boolean needFreeze = false;
        String freezeReason = "";
        for (int i = 0; i < orderDetailList.size(); i++) {
            GuoMeiOrderDetailList guoMeiOrderDetailList = orderDetailList.getObject(i, GuoMeiOrderDetailList.class);
            String goodsNo = guoMeiOrderDetailList.getItemNo();
            CrossBorderOrderDetails detail = new CrossBorderOrderDetails();
            BaseSku baseSku = baseSkuService.queryByGoodsNo(goodsNo);
            if (baseSku == null || !shopInfo.getCustId().equals(baseSku.getCustomersId())) {
                needFreeze = true;
                freezeReason = goodsNo + "未创建货品";
            } else {
                detail.setGoodsId(baseSku.getId());
                detail.setGoodsCode(baseSku.getGoodsCode());
                detail.setBarCode(baseSku.getBarCode());
            }
            detail.setOrderNo(order.getOrderNo());
            detail.setGoodsNo(goodsNo);
            BigDecimal price=guoMeiOrderDetailList.getPrice().divide(BigDecimalUtils.ONEHUNDRED, BigDecimal.ROUND_HALF_DOWN, 2);
            BigDecimal totalPrice=guoMeiOrderDetailList.getTotalPrice().divide(BigDecimalUtils.ONEHUNDRED, BigDecimal.ROUND_HALF_DOWN, 2);
            BigDecimal detailTax=totalPrice.multiply(new BigDecimal("0.091"));
            detail.setPayment(totalPrice.add(detailTax).setScale(2,BigDecimal.ROUND_HALF_DOWN) + "");
            detail.setTaxAmount(detailTax.setScale(2,BigDecimal.ROUND_HALF_DOWN) +"");
            detail.setDutiableValue(price.setScale(2,BigDecimal.ROUND_HALF_DOWN)  + "");
            detail.setDutiableTotalValue(totalPrice.setScale(2,BigDecimal.ROUND_HALF_DOWN)  + "");
            detail.setBarCode(guoMeiOrderDetailList.getBarCode());
            detail.setGoodsName(guoMeiOrderDetailList.getItemName());
            detail.setUnit(guoMeiOrderDetailList.getUnit());
            detail.setMakeCountry(guoMeiOrderDetailList.getAssemCountry());
            detail.setHsCode(guoMeiOrderDetailList.getHsCode());
            detail.setQty(guoMeiOrderDetailList.getQty() + "");
            detail.setNetWeight(guoMeiOrderDetailList.getFirstMeasureQty() + "");
            details.add(detail);
        }
        order.setCreateBy("SYSTEM");
        order.setCreateTime(new Timestamp(System.currentTimeMillis()));
        order.setStatus(CBOrderStatusEnum.STATUS_200.getCode());
        String district = object.getString("district");
        if (StringUtils.isEmpty(district))
            throw new BadRequestException("没有district");
        order.setDistrict(district);
        String payCode = object.getString("payCode");
        if (StringUtils.isEmpty(payCode))
            throw new BadRequestException("payCode必填");
        //国美的支付code  暂时为银盈通,今后可能会枚举
        order.setPayCode("16"); //银盈通支付
        //seqNo  crossBroNo 填写
        order.setOrderSeqNo(payTransactionId);
        order.setCrossBorderNo(orderNo);
        String createTime = object.getString("orderDate");
        if (StringUtils.isEmpty(createTime))
            throw new BadRequestException("没有orderDate");
        order.setOrderCreateTime(DateUtils.parseDateTime(createTime));
        String payTime = object.getString("payTime");
        if (StringUtils.isEmpty(payTime))
            throw new BadRequestException("没有payTime");
        order.setPayTime(DateUtils.parseDateTime(payTime));
        //测试先写死国美 电商平台
        order.setPlatformCode("GM");

        order.setItemList(details);
        CrossBorderOrder orderDto = crossBorderOrderService.createWithDetail(order);
        //接单成功后续   保存日志
        OrderLog orderLog = new OrderLog(
                order.getId() == null ? 0L : order.getId(),
                order.getOrderNo(),
                order.getId() == null ? (order.getStatus() + "") : (CBOrderStatusEnum.STATUS_200.getCode() + ""),
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription()
        );
        orderLog.setResMsg(object.toString());
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
            if (!StringUtil.equals("1", orderDto.getPreSell())) {
                cbOrderProducer.send(
                        MsgType.CB_ORDER_215,
                        String.valueOf(orderDto.getId()),
                        order.getOrderNo()
                );
            }
        }

    }

    @Override
    // 确认接单回传
    public void confirmOrder(CrossBorderOrder order) {
        if (order.getStatus().intValue() != CBOrderStatusEnum.STATUS_200.getCode().intValue()) {
            throw new BadRequestException("当前状态不允许接单回传：" + order.getId());
        }
        GuoMeiCommonParamRequest request = new GuoMeiCommonParamRequest();
        request.setTraceId(IdUtil.simpleUUID());
        request.setOrderNo(order.getOrderNo());
        request.setOperator(order.getCreateBy());
        request.setOperateTime(DateUtils.format(order.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        request.setOperateRemark("无");
        request.setStatusReason("无");
        //确认接单回传状态
        request.setStatus(0);
        request.setVendor(guomeiSupport.getVendor());
        //回传
        try {
            String reqUrl = "/border/callback/orderConfirm";
            String json = JSONObject.toJSONString(request);
            GuoMeiCommonParamResponse response = guomeiSupport.request(json, reqUrl);
            if (StringUtil.equals(response.getCode(), "200")) {
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
                orderLog.setKeyWord(response.getCode());
                orderLogService.create(orderLog);


                // 清关开始
//                cbOrderProducer.send(
//                        MsgType.CB_ORDER_220,
//                        String.valueOf(String.valueOf(order.getId())),
//                        order.getOrderNo()
//                );
            } else {
                throw new BadRequestException("code: " + response.getCode() + ",错误信息: " + response.getMessage());
            }
        } catch (Exception e) {
            throw new BadRequestException("确认接单回传失败" + e.getMessage());
        }

    }

    @Override
    // 接单异常回传
    public void confirmOrderErr(CrossBorderOrder order) {
        if (order.getStatus().intValue() != CBOrderStatusEnum.STATUS_999.getCode().intValue())
            throw new BadRequestException("此状态不能回传接单异常：" + order.getOrderNo());
//        if (StringUtils.equals("1", order.getPreSell()))
//            throw new BadRequestException("预售订单不能回传接单异常：" + order.getOrderNo());
        if (StringUtils.isEmpty(order.getFreezeReason()))
            throw new BadRequestException("冻结原因为空不能回传接单异常：" + order.getOrderNo());

        GuoMeiCommonParamRequest request = new GuoMeiCommonParamRequest();
        request.setTraceId(IdUtil.simpleUUID());
        request.setOrderNo(order.getOrderNo());
        request.setOperator(order.getCreateBy());
        request.setOperateTime(DateUtils.format(order.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        request.setOperateRemark("");
        request.setStatusReason("接单异常");
        //接单异常回传状态
        request.setStatus(10);
        request.setVendor(guomeiSupport.getVendor());
        //回传
        try {
            String reqUrl = "/border/callback/orderConfirm";
            String json = JSONObject.toJSONString(request);
            GuoMeiCommonParamResponse response = guomeiSupport.request(json, reqUrl);
            if (StringUtil.equals(response.getCode(), "200")) {
                // 回传接单异常,更改订单状态
                order.setStatus(CBOrderStatusEnum.STATUS_201.getCode());
                order.setReceivedBackTime(new Timestamp(System.currentTimeMillis()));
                crossBorderOrderService.update(order);
                // 保存订单日志
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_201.getCode()),
                        BooleanEnum.SUCCESS.getCode(),
                        BooleanEnum.SUCCESS.getDescription()
                );
                orderLog.setReqMsg(request.toString());
                orderLog.setResMsg(response.toString());
                orderLog.setKeyWord(response.getCode());
                orderLogService.create(orderLog);
            } else {
                throw new BadRequestException("code: " + response.getCode() + ",错误信息: " + response.getMessage());
            }
        } catch (Exception e) {
            throw new BadRequestException("确认接单回传失败" + e.getMessage());
        }
    }

    @Override
    //清关作业状态回传开始
    public void confirmClearStart(CrossBorderOrder order) {
        if (order.getStatus().intValue() != CBOrderStatusEnum.STATUS_220.getCode().intValue()) {
            throw new BadRequestException("当前状态不允许回传清关开始：" + order.getId());
        }
        GuoMeiCommonParamRequest request = new GuoMeiCommonParamRequest();
        request.setTraceId(IdUtil.simpleUUID());
        request.setOrderNo(order.getOrderNo());
        request.setOperateTime(DateUtils.format(new Date(), DatePattern.NORM_DATETIME_FORMAT));
        request.setCustomsCode("3105");
        request.setCustomsName("甬保税区");
        request.setOperator(order.getCreateBy());
        request.setIsBuyerInfoError(0);  //暂时写死  这个是国美的 判断是否是因为订购人信息有误

        request.setStatusReason("无");
        request.setOperateRemark("无");
        //传清关开始状态
        request.setStatus(10);
        request.setVendor(guomeiSupport.getVendor());
        try {
            String reqUrl = "/border/callback/orderCustomClearance";
            String json = JSONObject.toJSONString(request);
            GuoMeiCommonParamResponse response = guomeiSupport.request(json, reqUrl);
            log.info("国美回传清关开始,单号：{},返回：{}", order.getOrderNo(), response);
            //取消状态 0 不取消：商品正常出库. 30 撤销取消：商品正常出库
            if (StringUtil.equals(response.getCode(), "200")) {
                GuoMeiOrderConfimResponse res = new cn.hutool.json.JSONObject(response).toBean(GuoMeiOrderConfimResponse.class);
                if (res.getData()!=null){
                    Integer cancelStatus = res.getData().getCancelStatus();
                    if (cancelStatus != 0) {
                        //取消申请：进行仓拦截，订单挂起
                        orderCancelBySys(order, cancelStatus);
                    }
                }

                //正常清关后续的流程
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
            } else {
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_225.getCode()),
                        BooleanEnum.FAIL.getCode(),
                        response.getMessage()
                );
                orderLog.setReqMsg(request.toString());
                orderLog.setResMsg(response.toString());
                orderLogService.create(orderLog);
                throw new BadRequestException(response.getMessage());
            }
        } catch (Exception e) {
            throw new BadRequestException("清关回传失败" + e.getMessage());
        }
    }

    //清关回传异常
    @Override
    public void confirmClearErr(String orderNo) throws Exception {
        log.info("开始回传国美清关异常,单号：{}", orderNo);
        CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(orderNo);
        if (order.getStatus().intValue() != CBOrderStatusEnum.STATUS_225.getCode().intValue()) {
            throw new BadRequestException("此状态不能回传清关异常：" + order.getOrderNo());
        }
        GuoMeiCommonParamRequest request = new GuoMeiCommonParamRequest();
        request.setTraceId(IdUtil.simpleUUID());
        request.setOrderNo(order.getOrderNo());
        request.setOperateTime(DateUtils.format(new Date(), DatePattern.NORM_DATETIME_FORMAT));
        request.setCustomsCode("3105");
        request.setCustomsName("甬保税区");
        request.setOperator(order.getCreateBy());

//        String msg = order.getDeclareMsg();
//        if (StringUtils.contains(msg,"Code:1327") ||
//                StringUtils.contains(msg,"Code:1300")){
//            request.setStatusReason(msg);
//            request.setIsBuyerInfoError(1);
//        }else {
//            request.setIsBuyerInfoError(0);
//        }
        request.setIsBuyerInfoError(1);  //测试暂时写死  1 为是订购人信息问题   0为无
        request.setStatusReason("订购人信息错误");

        //传清关失败信息
        request.setStatus(30);
        request.setVendor(guomeiSupport.getVendor());
        try {
            String reqUrl = "/border/callback/orderCustomClearance";
            String json = JSONObject.toJSONString(request);
            GuoMeiCommonParamResponse response = guomeiSupport.request(json, reqUrl);
            log.info("国美回传清关失败开始,单号：{},返回：{}", order.getOrderNo(), response);
            if (StringUtil.equals(response.getCode(), "200")) {
                // 回传成功
                order.setStatus(CBOrderStatusEnum.STATUS_227.getCode());
                crossBorderOrderService.update(order);
                // 保存订单日志
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_227.getCode()),
                        BooleanEnum.SUCCESS.getCode(),
                        BooleanEnum.SUCCESS.getDescription()
                );
                orderLog.setReqMsg(request.toString());
                orderLog.setResMsg(response.toString());
                orderLog.setKeyWord(response.getCode());
                orderLogService.create(orderLog);
            } else {
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_227.getCode()),
                        BooleanEnum.FAIL.getCode(),
                        response.getMessage()
                );
                orderLog.setReqMsg(request.toString());
                orderLog.setResMsg(response.toString());
                orderLogService.create(orderLog);
                throw new BadRequestException(response.getMessage());
            }
        } catch (Exception e) {
            throw new BadRequestException("回传国美清关异常失败");
        }
    }

    //清关回传成功
    @Override
    public void confirmClearSuccess(CrossBorderOrder order) throws Exception {
        if (order.getStatus().intValue() != CBOrderStatusEnum.STATUS_230.getCode().intValue()
                || StringUtil.equals(order.getIsLock(), "1")) {
            //锁单中不能回传清关完成
            throw new BadRequestException("此状态不能回传清关完成：" + order.getOrderNo());
        }
        log.info("开始回传国美清关异常,单号：{}", order.getOrderNo());
        GuoMeiCommonParamRequest request = new GuoMeiCommonParamRequest();
        request.setTraceId(IdUtil.simpleUUID());
        request.setOrderNo(order.getOrderNo());
        request.setOperateTime(DateUtils.format(new Date(), DatePattern.NORM_DATETIME_FORMAT));
        request.setCustomsCode("3105");
        request.setCustomsName("甬保税区");
        request.setOperator(order.getCreateBy());
        request.setIsBuyerInfoError(0);  //暂时写死
        //传清关成功信息
        request.setStatus(20);
        request.setVendor(guomeiSupport.getVendor());

        String reqUrl = "/border/callback/orderCustomClearance";
        String json = JSONObject.toJSONString(request);
        GuoMeiCommonParamResponse response = guomeiSupport.request(json, reqUrl);
        log.info("国美回传清关成功开始,单号：{},返回：{}", order.getOrderNo(), response);
        if (StringUtil.equals(response.getCode(), "200")) {
            GuoMeiOrderConfimResponse res = new cn.hutool.json.JSONObject(response).toBean(GuoMeiOrderConfimResponse.class);
            if (res.getData()!=null){
                Integer cancelStatus = res.getData().getCancelStatus();
                if (cancelStatus != 0) {
                    //取消申请：进行仓拦截，订单挂起
                    orderCancelBySys(order, cancelStatus);
                }
            }

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
            orderLog.setKeyWord(response.getCode());
            orderLogService.create(orderLog);
        } else {
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_235.getCode()),
                    BooleanEnum.FAIL.getCode(),
                    response.getMessage()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getCode());
            orderLogService.create(orderLog);
            throw new BadRequestException(response.getMessage());
        }

    }

    @Override
    //保税仓作业状态拣货回传   拣货开始
    public void confirmPickStart(CrossBorderOrder order) {
        if (StringUtil.equals(order.getIsLock(), "1"))
            throw new BadRequestException("锁单中。不能回传拣货开始");
        log.info("国美开始回传拣货开始,单号：{}", order.getOrderNo());
        GuoMeiCommonParamRequest request = new GuoMeiCommonParamRequest();
        request.setTraceId(IdUtil.simpleUUID());
        request.setOrderNo(order.getOrderNo());
        request.setOperateTime(DateUtils.format(new Date(), DatePattern.NORM_DATETIME_FORMAT));
        request.setOperator(order.getCreateBy());
        //回传开始拣货
        request.setStatus(10);
        request.setVendor(guomeiSupport.getVendor());
        try {
            String reqUrl = "/border/callback/orderOperate";
            String json = JSONObject.toJSONString(request);
            GuoMeiCommonParamResponse response = guomeiSupport.request(json, reqUrl);
            log.info("国美回传拣货开始,单号：{},返回：{}", order.getOrderNo(), response);
            if (StringUtil.equals(response.getCode(), "200")) {
                GuoMeiOrderConfimResponse res = new cn.hutool.json.JSONObject(response).toBean(GuoMeiOrderConfimResponse.class);
                if (res.getData()!=null){
                    Integer cancelStatus = res.getData().getCancelStatus();
                    if (cancelStatus != 0) {
                        //取消申请：进行仓拦截，订单挂起
                        orderCancelBySys(order, cancelStatus);
                    }

                }

                //正常拣货回传成功
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_236.getCode()),
                        BooleanEnum.SUCCESS.getCode(),
                        BooleanEnum.SUCCESS.getDescription()
                );
                orderLog.setReqMsg(request.toString());
                orderLog.setResMsg(response.toString());
                orderLog.setKeyWord(response.getCode());
                orderLogService.create(orderLog);

                // 3秒后回传拣货完成
//                cbOrderProducer.delaySend(
//                        MsgType.CB_ORDER_2361,
//                        String.valueOf(order.getId()),
//                        order.getOrderNo(),
//                        180000
//                );

            } else {
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_236.getCode()),
                        BooleanEnum.FAIL.getCode(),
                        response.getMessage()
                );
                orderLog.setReqMsg(request.toString());
                orderLog.setResMsg(response.toString());
                orderLogService.create(orderLog);
                throw new BadRequestException(response.getMessage());
            }
        } catch (Exception e) {
            throw new BadRequestException("拣货开始回传失败" + e.getMessage());
        }

    }

    // 回传拣货完成
    @Override
    public void confirmPickEnd(CrossBorderOrder order) {
        if (StringUtil.equals(order.getIsLock(), "1")) {
            throw new BadRequestException("锁单中，不能回传拣货结束");
        }
        log.info("国美开始回传拣货完成,单号：{}", order.getOrderNo());
        GuoMeiCommonParamRequest request = new GuoMeiCommonParamRequest();
        request.setTraceId(IdUtil.simpleUUID());
        request.setOrderNo(order.getOrderNo());
        request.setOperateTime(DateUtils.format(new Date(), DatePattern.NORM_DATETIME_FORMAT));
        request.setOperator(order.getCreateBy());
        //回传拣货完成
        request.setStatus(20);
        request.setVendor(guomeiSupport.getVendor());
        try {
            String reqUrl = "/border/callback/orderOperate";
            String json = JSONObject.toJSONString(request);
            GuoMeiCommonParamResponse response = guomeiSupport.request(json, reqUrl);
            log.info("国美回传拣货完成开始,单号：{},返回：{}", order.getOrderNo(), response);
            if (StringUtil.equals(response.getCode(), "200")) {
                GuoMeiOrderConfimResponse res = new cn.hutool.json.JSONObject(response).toBean(GuoMeiOrderConfimResponse.class);
                if (res.getData()!=null){
                    Integer cancelStatus = res.getData().getCancelStatus();
                    if (cancelStatus != 0) {
                        //取消申请：进行仓拦截，订单挂起
                        orderCancelBySys(order, cancelStatus);
                    }
                }

                //正常拣货完成回传成功
                order.setSendPickFlag("1");
                crossBorderOrderService.update(order);

                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_2361.getCode()),
                        BooleanEnum.SUCCESS.getCode(),
                        BooleanEnum.SUCCESS.getDescription()
                );
                orderLog.setReqMsg(request.toString());
                orderLog.setResMsg(response.toString());
                orderLog.setKeyWord(response.getCode());
                orderLogService.create(orderLog);

            } else {
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_2361.getCode()),
                        BooleanEnum.FAIL.getCode(),
                        response.getMessage()
                );
                orderLog.setReqMsg(request.toString());
                orderLog.setResMsg(response.toString());
                orderLog.setKeyWord(response.getCode());
                orderLogService.create(orderLog);

                throw new BadRequestException(response.getMessage());
            }
        } catch (Exception e) {
            throw new BadRequestException("拣货完成回传失败" + e.getMessage());
        }

    }

    // 回传打包
    @Override
    public void confirmPack(CrossBorderOrder order) {
        if (order.getStatus().intValue() != CBOrderStatusEnum.STATUS_240.getCode().intValue()
                || StringUtil.equals(order.getIsLock(), "1")) {
            throw new BadRequestException("此状态不能回传打包：" + order.getOrderNo());
        }
        if (!StringUtil.equals("1",order.getSendPickFlag()))
            throw new BadRequestException("请先回传拣货");
        log.info("国美开始回传打包,单号：{}", order.getOrderNo());
        GuoMeiCommonParamRequest request = new GuoMeiCommonParamRequest();
        request.setTraceId(IdUtil.simpleUUID());
        request.setOrderNo(order.getOrderNo());
        request.setOperateTime(DateUtils.format(new Date(), DatePattern.NORM_DATETIME_FORMAT));
        request.setOperator(order.getCreateBy());
        //回传打包完成
        request.setStatus(30);
        request.setVendor(guomeiSupport.getVendor());
        try {
            String reqUrl = "/border/callback/orderOperate";
            String json = JSONObject.toJSONString(request);
            GuoMeiCommonParamResponse response = guomeiSupport.request(json, reqUrl);
            log.info("国美回传打包完成开始,单号：{},返回：{}", order.getOrderNo(), response);
            if (StringUtil.equals(response.getCode(), "200")) {
                GuoMeiOrderConfimResponse res = new cn.hutool.json.JSONObject(response).toBean(GuoMeiOrderConfimResponse.class);
                if (res.getData()!=null){
                    Integer cancelStatus = res.getData().getCancelStatus();
                    if (cancelStatus != 0) {
                        //取消申请：进行仓拦截，订单挂起
                        orderCancelBySys(order, cancelStatus);
                    }
                }
                //正常回传打包成功
                order.setPackBackTime(new Timestamp(System.currentTimeMillis()));
                crossBorderOrderService.update(order);

                // 500ms后回传出库
//                cbOrderProducer.delaySend(
//                        MsgType.CB_ORDER_245,
//                        String.valueOf(order.getId()),
//                        order.getOrderNo(),
//                        500
//                );

                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_237.getCode()),
                        BooleanEnum.SUCCESS.getCode(),
                        BooleanEnum.SUCCESS.getDescription()
                );
                orderLog.setReqMsg(request.toString());
                orderLog.setResMsg(response.toString());
                orderLog.setKeyWord(response.getCode());
                orderLogService.create(orderLog);
            } else {
                // 打包回传失败
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_237.getCode()),
                        BooleanEnum.FAIL.getCode(),
                        response.getMessage()
                );
                orderLog.setReqMsg(request.toString());
                orderLog.setResMsg(response.toString());
                orderLog.setKeyWord(response.getCode());
                orderLogService.create(orderLog);
                throw new BadRequestException(response.getMessage());
            }
        } catch (Exception e) {
            throw new BadRequestException("打包完成回传失败" + e.getMessage());
        }
    }

    //出库节点回传
    @Override
    public void confirmDeliver(CrossBorderOrder order) {
        if (order.getStatus().intValue() != CBOrderStatusEnum.STATUS_240.getCode().intValue()
                || StringUtil.equals(order.getIsLock(), "1")) {
            throw new BadRequestException("此状态不能回传出库：" + order.getOrderNo());
        }
        if (order.getPackBackTime()==null)
            throw new BadRequestException("未回传打包");
        log.info("国美开始回传出库,单号：{}", order.getOrderNo());
        GuoMeiCommonParamRequest request = new GuoMeiCommonParamRequest();
        request.setTraceId(IdUtil.simpleUUID());
        request.setOrderNo(order.getOrderNo());
        //后续看是否写死物流
//        LogisticsInfo logisticsInfo = logisticsInfoService.queryById(order.getSupplierId());
//        request.setLogisticsCode(logisticsInfo == null?"GZTO":logisticsInfo.getDefault03());
//        request.setLogisticsName(logisticsInfo == null?"中通国际":logisticsInfo.getName());
//        request.setWayBillNo(order.getLogisticsNo());
//        request.setDeliveryTime(order.getExpDeliverTime()+"");

        //之前测试写死
        request.setLogisticsCode("EMS");
        request.setLogisticsName("邮政快递");
        request.setWayBillNo("E"+IdUtil.simpleUUID().substring(15));
        request.setDeliveryTime("2022-07-28 10:04:33");

        request.setVendor(guomeiSupport.getVendor());
        try {
            String reqUrl = "/border/callback/logisticsInfo";
            String json = JSONObject.toJSONString(request);
            GuoMeiCommonParamResponse response = guomeiSupport.request(json, reqUrl);
            log.info("国美回传物流信息开始,单号：{},返回：{}", order.getOrderNo(), response);
            if (StringUtil.equals(response.getCode(), "200")) {
                GuoMeiOrderConfimResponse res = new cn.hutool.json.JSONObject(response).toBean(GuoMeiOrderConfimResponse.class);
                if (res.getData() != null){
                    if (res.getData().getCancelStatus() != 0) {
                        //取消申请：进行仓拦截，订单挂起
                        Integer cancelStatus = res.getData().getCancelStatus();
                        orderCancelBySys(order, cancelStatus);
                    }
                }

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
                orderLog.setKeyWord(response.getCode());
                orderLogService.create(orderLog);

                // 扣除保证金
//                depositService.change(order);

//                cbOrderProducer.delaySend(
//                        MsgType.CB_ORDER_WMS,
//                        String.valueOf(order.getId()),
//                        order.getOrderNo(),
//                        30000
//                );
            } else {
                // 出库回传失败
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_245.getCode()),
                        BooleanEnum.FAIL.getCode(),
                        response.getMessage()
                );
                orderLog.setReqMsg(request.toString());
                orderLog.setResMsg(response.toString());
                orderLog.setKeyWord(response.getCode());
                orderLogService.create(orderLog);
                throw new BadRequestException("请求国美出库失败：" + response.getMessage());
            }
        } catch (Exception e) {
            throw new BadRequestException("国美出库回传失败" + e.getMessage());
        }

    }

    //更改订购人信息
    @Override
    public void updateOrder(JSONObject object) {
        String orderNo = object.getString("orderNo");
        if (StringUtils.isEmpty(orderNo))
            throw new BadRequestException("缺少orderNo");
        CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(orderNo);
        if (order == null)
            throw new BadRequestException("查询不到该订单号");
        String buyerIdNumber = object.getString("buyerIdNumber");
        if (StringUtils.isEmpty(buyerIdNumber))
            throw new BadRequestException("缺少buyerIdNumber");
        order.setBuyerIdNum(buyerIdNumber);
        String ebpName = object.getString("ebpName");
        if (StringUtils.isEmpty(ebpName))
            throw new BadRequestException("缺少ebpName");
        order.setEbpName(ebpName);
        String ebpCode = object.getString("ebpCode");
        if (StringUtils.isEmpty(ebpCode))
            throw new BadRequestException("缺少ebpCode");
        order.setEbpCode(ebpCode);
        String buyerName = object.getString("buyerName");
        if (StringUtils.isEmpty(buyerName))
            throw new BadRequestException("缺少buyerName");
        order.setBuyerName(buyerName);

//        String ebcName = object.getString("ebcName");  //电商企业名称
//        if (StringUtils.isEmpty(ebcName))
//            throw new BadRequestException("没有ebcName");
//        String ebcCustomsCode = object.getString("ebcCustomsCode");  //电商企业编码
//        if (StringUtils.isEmpty(ebcCustomsCode))
//            throw new BadRequestException("没有ebcCustomsCode");

        crossBorderOrderService.update(order);
        OrderLog orderLog = new OrderLog(
                order.getId(),
                order.getOrderNo(),
                String.valueOf(order.getStatus()),
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription()
        );
        orderLogService.create(orderLog);
        //国美这边的流程,走这个方法后  重推清关
//        try {
//            crossBorderOrderService.cancelDec(order.getId());
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new BadRequestException("取消申报失败 : " + e.getMessage());
//        }
//
//        try {
//            kjgSupport.declare(order);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new BadRequestException("重新申报失败 : " + e.getMessage());
//        }

    }


    //订单取消接收后回传
    @Override
    public void orderCancel(JSONObject object) {
        String orderNo = object.getString("orderNo");
        if (StringUtils.isEmpty(orderNo))
            throw new BadRequestException("缺少orderNo");
        CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(orderNo);
        if (order == null)
            throw new BadRequestException("查询不到该订单号");
        Integer type = object.getInteger("type");
        if (type == null)
            throw new BadRequestException("缺少type");
        if (type != 0) {

            Map<String, Object> map = new HashMap<>();
            map.put("id", order.getId().toString());
            map.put("type", type);

//            cbOrderProducer.delaySend(
//                    MsgType.CB_ORDER_GM_CANCEL,
//                    JSONObject.toJSONString(map),
//                    orderNo,
//                    3000
//            );

            //测试时使用
//            test1(order,type);
        }
        /*try {
            GuoMeiCommonParamRequest request = new GuoMeiCommonParamRequest();
            if (!(order.getStatus().equals(CBOrderStatusEnum.STATUS_245.getCode()))||!order.getStatus().equals(CBOrderStatusEnum.STATUS_240.getCode())){
                if (type==20){
                    //订单取消
                    //回传取消订单状态
                    request.setStatus(20);
                } else if (type==10){
                    //取消申请：进行仓拦截，订单挂起

                    //回传订单临时挂起状态
                    request.setStatus(10);
                }else if (type==30){
                    //需求撤销取消状态 正常出库
                    //回传订单撤销取消的消息
                    request.setStatus(30);
                }
            }else {
                //因为包裹已出库,所以不能取消订单
                //回传取消订单失败
                request.setStatus(15);
            }
            request.setOrderNo(order.getOrderNo());
            request.setVendor(guomeiSupport.getVendor());
            request.setTraceId(IdUtil.simpleUUID());
            String reqUrl = "/border/callback/orderCancel";
            String json = JSONObject.toJSONString(request);
            GuoMeiCommonParamResponse response =guomeiSupport.request(json,reqUrl);
            if (StringUtil.equals(response.getCode(), "200")){
                //成功
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_884.getCode()),
                        BooleanEnum.SUCCESS.getCode(),
                        BooleanEnum.SUCCESS.getDescription()
                );
                orderLog.setReqMsg(request.toString());
                orderLog.setResMsg(response.toString());
                orderLog.setKeyWord(response.getCode());
                orderLogService.create(orderLog);
            }else {
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_884.getCode()),
                        BooleanEnum.FAIL.getCode(),
                        response.getMessage()
                );
                orderLog.setReqMsg(request.toString());
                orderLog.setResMsg(response.toString());
                orderLog.setKeyWord(response.getCode());
                orderLogService.create(orderLog);
                throw new BadRequestException(response.getMessage());
            }
        }catch (Exception e){
            throw new BadRequestException("国美订单状态查询失败: " + e.getMessage());
        }*/

    }

    //这边主动订单取消回传给国美
    @Override
    public Integer guomeiOrderCancel(CrossBorderOrder order) {
        GuoMeiCommonParamRequest request = new GuoMeiCommonParamRequest();
        request.setOrderNo(order.getOrderNo());
        request.setVendor(guomeiSupport.getVendor());

        String reqUrl = "/border/getCancelStatus";
        String json = JSONObject.toJSONString(request);
        GuoMeiCommonParamResponse response = guomeiSupport.request(json, reqUrl);
        if (StringUtil.equals(response.getCode(), "200")) {
            GuoMeiOrderConfimResponse res = new cn.hutool.json.JSONObject(response).toBean(GuoMeiOrderConfimResponse.class);
            return res.getData().getCancelStatus();
        }
        throw new BadRequestException("获取订单取消状态错误：code" + response.getCode() + ";msg" + response.getMessage());
    }

    /**
     * mq处理国美订单取消
     *
     * @param body
     */
    @Override
    public void orderCancelByMq(String body) {
        JSONObject obj = JSONObject.parseObject(body);
        CrossBorderOrder order = crossBorderOrderService.queryById(obj.getLong("id"));
        orderCancelBySys(order, obj.getInteger("type"));
    }

    /**
     * 内部调用取消订单
     *
     * @param order
     * @throws Exception
     */
    @Override
    public synchronized void orderCancelBySys(CrossBorderOrder order, Integer cancelStatus) {
        try {
//            if (cancelStatus == 10) {
//                //冻结   (申请取消)
//                crossBorderOrderService.lockOrder(order);
//            } else if (cancelStatus == 20) {
//                //取消订单
//                crossBorderOrderService.cancel(order.getId());
//            } else {
//                //取消锁单
//                crossBorderOrderService.cancelLockOrder(order);
//            }
            // 拦截成功
            confirmInterceptionSucc(order, cancelStatus);
        } catch (Exception e) {
            // 拦截失败
            confirmInterceptionErr(order, cancelStatus);
        }
    }

    //拦截成功
    @Override
    public void confirmInterceptionSucc(CrossBorderOrder order, Integer cancelStatus) {
        // TODO: 2022/4/11 国美拦截回调接口
        log.info("国美拦截成功回传,单号：{}", order.getOrderNo());
        GuoMeiCommonParamRequest request = new GuoMeiCommonParamRequest();
        request.setStatus(cancelStatus);
        request.setOrderNo(order.getOrderNo());
        request.setVendor(guomeiSupport.getVendor());
        request.setTraceId(IdUtil.simpleUUID());
        request.setVendor(guomeiSupport.getVendor());
        try {
            String reqUrl = "/border/callback/orderCancel";
            String json = JSONObject.toJSONString(request);
            GuoMeiCommonParamResponse response = guomeiSupport.request(json, reqUrl);
            log.info("国美回传拦截信息开始,单号：{},返回：{}", order.getOrderNo(), response);
            if (StringUtil.equals(response.getCode(), "200")) {
                //走拦截成功后的流程

                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(order.getStatus()),
                        BooleanEnum.SUCCESS.getCode(),
                        BooleanEnum.SUCCESS.getDescription()
                );
                orderLog.setReqMsg(request.toString());
                orderLog.setResMsg(response.toString());
                orderLog.setKeyWord(response.getCode());
                orderLogService.create(orderLog);
            } else {
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(order.getStatus()),
                        BooleanEnum.FAIL.getCode(),
                        response.getMessage()
                );
                orderLog.setReqMsg(request.toString());
                orderLog.setResMsg(response.toString());
                orderLog.setKeyWord(response.getCode());
                orderLogService.create(orderLog);
            }
        } catch (Exception e) {
            throw new BadRequestException("国美拦截成功回传失败" + e.getMessage());
        }
    }

    //拦截失败
    @Override
    public void confirmInterceptionErr(CrossBorderOrder order, Integer cancelStatus) {
        // TODO: 2022/4/11 国美回传拦截失败
        log.info("国美回传拦截失败开始,单号：{}", order.getOrderNo());
        GuoMeiCommonParamRequest request = new GuoMeiCommonParamRequest();
        request.setOrderNo(order.getOrderNo());
        request.setVendor(guomeiSupport.getVendor());
        request.setTraceId(IdUtil.simpleUUID());
        //回传拦截失败
        if (cancelStatus == 10) {
            //回传订单临时挂起状态
            request.setStatus(15);
        } else if (cancelStatus == 20) {
            //回传订单取消状态
            request.setStatus(25);
        } else if (cancelStatus == 30) {
            //回传订单取消状态
            request.setStatus(35);
        }
        request.setVendor(guomeiSupport.getVendor());
        try {
            String reqUrl = "/border/callback/orderCancel";
            String json = JSONObject.toJSONString(request);
            GuoMeiCommonParamResponse response = guomeiSupport.request(json, reqUrl);
            log.info("国美回传拦截失败信息开始,单号：{},返回：{}", order.getOrderNo(), response);
            if (StringUtil.equals(response.getCode(), "200")) {
                //走拦截失败后的流程

                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(order.getStatus()),
                        BooleanEnum.SUCCESS.getCode(),
                        BooleanEnum.SUCCESS.getDescription()
                );
                orderLog.setReqMsg(request.toString());
                orderLog.setResMsg(response.toString());
                orderLog.setKeyWord(response.getCode());
                orderLogService.create(orderLog);
            } else {
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(order.getStatus()),
                        BooleanEnum.FAIL.getCode(),
                        response.getMessage()
                );
                orderLog.setReqMsg(request.toString());
                orderLog.setResMsg(response.toString());
                orderLog.setKeyWord(response.getCode());
                orderLogService.create(orderLog);
            }
        } catch (Exception e) {
            throw new BadRequestException("国美拦截回传失败" + e.getMessage());
        }
    }

}



