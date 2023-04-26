package me.zhengjie.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.mq.CBOrderProducer;
import me.zhengjie.rest.model.jackYun.*;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.ReceivingTransaction;
import me.zhengjie.support.WmsSupport;
import me.zhengjie.support.fuliPre.ActAllocationDetails;
import me.zhengjie.support.jackYun.*;
import me.zhengjie.utils.DateUtils;
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.StringUtils;
import me.zhengjie.utils.constant.MsgType;
import me.zhengjie.utils.enums.BooleanEnum;
import me.zhengjie.utils.enums.CBOrderStatusEnum;
import me.zhengjie.utils.enums.InBoundStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class JackYunServiceImpl implements JackYunService {
    @Autowired
    private CrossBorderOrderService crossBorderOrderService;
    @Autowired
    private BaseSkuService baseSkuService;
    @Autowired
    private ShopInfoService shopInfoService;
    @Autowired
    private ClearCompanyInfoService clearCompanyInfoService;
    @Autowired
    private PlatformService platformService;
    @Autowired
    private OrderLogService orderLogService;
    @Autowired
    private CBOrderProducer cbOrderProducer;
    @Autowired
    private InboundOrderService inboundOrderService;
    @Autowired
    private OutboundOrderService outboundOrderService;
    @Autowired
    private JackYunSupport jackYunSupport;
    @Autowired
    private WmsSupport wmsSupport;
    @Autowired
    private PaymentInfoService paymentInfoService;
    @Override
    public JackYunDeliveryOrderCreateResponse createOrder(JackYunDeliveryOrderCreateRequest request,String customerId) {
        JackYunDeliveryOrderCreateResponse response=new JackYunDeliveryOrderCreateResponse();
        response.returnSucc();
        try {
            JackYunDeliveryOrder deliveryOrder=request.getDeliveryOrder();
            if (deliveryOrder==null)
                throw new BadRequestException("deliveryOrder为空");
            CrossBorderOrder order=crossBorderOrderService.queryByOrderNo(deliveryOrder.getDeliveryOrderCode());
            if (order!=null){
                response.setWarehouseCode("3302461510");
                response.setDeliveryOrderId(order.getOrderNo());
                response.setLogisticsCode(order.getLogisticsCode());
                response.setCreateTime(DateUtils.formatDateTime(order.getCreateTime()));
            }else {
                order=new CrossBorderOrder();
                boolean needFreeze=false;
                String freezeReason="";
                order.setOrderNo(deliveryOrder.getDeliveryOrderCode());
                order.setCrossBorderNo(order.getOrderNo());
                order.setStatus(CBOrderStatusEnum.STATUS_200.getCode());
                ShopInfo shopInfo=shopInfoService.queryByKjgCode(request.getDeliveryOrder().getWarehouseCode());
                if (shopInfo==null)
                    throw new BadRequestException("未知的货主编码"+customerId);
                order.setCustomersId(shopInfo.getCustId());
                order.setShopId(shopInfo.getId());
                order.setPlatformShopId(shopInfo.getKjgCode());
                order.setClearCompanyId(shopInfo.getServiceId());
                order.setPlatformCode(deliveryOrder.getSourcePlatformCode()+"-JCY");
                order.setCrossBorderNo(deliveryOrder.getDeclareOrderNo());
                Platform platform=platformService.findByCode(deliveryOrder.getSourcePlatformCode()+"-JCY");
                if (platform==null){
                    needFreeze=true;
                    freezeReason+="平台代码未维护|";
                }else {
                    order.setEbpCode(platform.getEbpCode());
                    order.setEbpName(platform.getEbpName());
                    order.setOrderForm(platform.getOrderForm());
                }
                order.setOrderCreateTime(DateUtils.parseDateTime(deliveryOrder.getPlaceOrderTime()));
                order.setPayment(deliveryOrder.getTotalAmount());
                order.setPostFee(deliveryOrder.getFreight());
                order.setBuyerAccount(StringUtil.isBlank(deliveryOrder.getBuyerNick())?"Null":deliveryOrder.getBuyerNick());
                order.setTaxAmount(deliveryOrder.getTaxAmount());
                if (deliveryOrder.getReceiverInfo()==null)
                    throw new BadRequestException("收件人信息为空");
                order.setBuyerPhone(deliveryOrder.getReceiverInfo().getMobile());
                order.setBuyerIdNum(deliveryOrder.getIdcard());
                order.setBuyerName(deliveryOrder.getBuyerName());
                order.setBooksNo(shopInfo.getBooksNo());
                PaymentInfo paymentInfo=paymentInfoService.queryByCustomerCode(deliveryOrder.getPayCompanyCustomsCode());
                if (paymentInfo==null) {
                    needFreeze=true;
                    order.setPayCode(deliveryOrder.getPayCompanyCustomsName()+":"+deliveryOrder.getPayCompanyCustomsCode());
                    freezeReason+="支付公司代码不存在";
                }else
                    order.setPayCode(paymentInfo.getPayCode());
                order.setPayTime(DateUtils.parseDateTime(deliveryOrder.getPayTime()));
                order.setPaymentNo(deliveryOrder.getPayNo());
                order.setOrderSeqNo(deliveryOrder.getPayNo());
                order.setProvince(deliveryOrder.getReceiverInfo().getProvince());
                order.setCity(deliveryOrder.getReceiverInfo().getCity());
                order.setDistrict(deliveryOrder.getReceiverInfo().getArea());
                order.setConsigneeAddr(String.format("%s %s %s %s",
                        deliveryOrder.getReceiverInfo().getProvince(),
                        deliveryOrder.getReceiverInfo().getCity(),
                        deliveryOrder.getReceiverInfo().getArea(),
                        deliveryOrder.getReceiverInfo().getDetailAddress()));
                order.setConsigneeName(deliveryOrder.getReceiverInfo().getName());
                order.setConsigneeTel(deliveryOrder.getReceiverInfo().getMobile());
                List<CrossBorderOrderDetails>details=new ArrayList<>();
                BigDecimal disAmountTotal=new BigDecimal(deliveryOrder.getDiscountAmount());
                if (request.getOrderLines()!=null){
                    for (JackYunDeliveryOrderOrderLine orderLine : request.getOrderLines()) {
                        CrossBorderOrderDetails detail = new CrossBorderOrderDetails();
                        BaseSku baseSku=baseSkuService.queryByCode(orderLine.getItemCode());
                        String goodsNo=null;
                        if (baseSku == null) {
                            if (StringUtil.isNotBlank(orderLine.getItemId()))
                                baseSku=baseSkuService.queryByGoodsNo(orderLine.getItemId());
                            if (baseSku==null){
                                needFreeze = true;
                                freezeReason = "商品编码:"+(StringUtil.isBlank(orderLine.getItemId())?orderLine.getItemCode():orderLine.getItemId()) + "未创建货品";
                            }else
                                goodsNo=baseSku.getGoodsNo();
                        }else {
                            goodsNo=baseSku.getGoodsNo();
                            detail.setGoodsId(baseSku.getId());
                            detail.setGoodsCode(baseSku.getGoodsCode());
                            detail.setBarCode(baseSku.getBarCode());
                        }
                        detail.setOrderNo(order.getOrderNo());
                        detail.setGoodsNo(goodsNo);
                        detail.setQty(orderLine.getPlanQty()+"");
                        BigDecimal disAmount=new BigDecimal(orderLine.getDiscountAmount());
                        disAmountTotal=disAmountTotal.add(disAmount);
                        BigDecimal dutiableTotalValue=new BigDecimal(orderLine.getActualPrice()).multiply(new BigDecimal(orderLine.getPlanQty())).add(disAmount);
                        BigDecimal payment=dutiableTotalValue.multiply(new BigDecimal("1.091")).setScale(2,RoundingMode.HALF_UP);
                        detail.setDutiableValue(dutiableTotalValue.divide(new BigDecimal(orderLine.getPlanQty()),2, RoundingMode.HALF_UP).toString());
                        detail.setDutiableTotalValue(dutiableTotalValue.toString());
                        detail.setPayment(payment.toString());
                        detail.setTaxAmount(payment.subtract(new BigDecimal(orderLine.getActualPrice())).toString());
                        details.add(detail);
                    }
                }
                //order.setDisAmount(deliveryOrder.getDiscountAmount());
                order.setDisAmount(disAmountTotal.toString());
                order.setItemList(details);
                order.setCreateBy("JackYun");
                order.setCreateTime(new Timestamp(System.currentTimeMillis()));
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
                }else {
                    // 接单
                    cbOrderProducer.send(
                            MsgType.CB_ORDER_215,
                            String.valueOf(orderDto.getId()),
                            order.getOrderNo()
                    );
                    //pushPayOrder(order);
                }
                response.setWarehouseCode("3302461510");
                response.setDeliveryOrderId(order.getOrderNo());
                response.setLogisticsCode(order.getLogisticsCode());
                response.setCreateTime(DateUtils.formatDateTime(order.getCreateTime()));
            }
        }catch (Exception e){
            e.printStackTrace();
            response.returnFail(e.getMessage());
        }
        return response;
    }

    @Override
    public JackYunSingleitemSynchronizeResponse syncGoodsInfo(JackYunSingleItemSynchronizeRequest request) {
        JackYunSingleitemSynchronizeResponse response=new JackYunSingleitemSynchronizeResponse();
        BaseSku baseSku;
        JackYunSingleItem item=request.getItem();
        response.returnSucc();
        try {
            if (StringUtil.equals("add",request.getActionType())){
                baseSku=baseSkuService.queryByCode(item.getItemCode());
                if (baseSku!=null){
                    response.setItemId(baseSku.getGoodsNo());
                }else {
                    ShopInfo shopInfo=shopInfoService.queryByKjgCode(request.getWarehouseCode());
                    if (shopInfo!=null){
                        //如果店铺不为空，先查询该店铺是否有同条码的商品，有就修改商品编码
                        baseSku=baseSkuService.queryByBarCodeAndShopId(request.getItem().getBarCode(),shopInfo.getId());
                        if (baseSku!=null){
                            baseSku.setGoodsCode(request.getItem().getItemCode());
                            response.setItemId(baseSku.getGoodsNo());
                            baseSkuService.update(baseSku);
                            return response;
                        }else {
                            baseSku=new BaseSku();
                            baseSku.setShopId(shopInfo.getId());
                            baseSku.setCustomersId(shopInfo.getCustId());
                        }
                    }else
                        throw new BadRequestException("未知的仓");
                    baseSku.setGoodsCode(item.getItemCode());
                    baseSku.setGoodsName(item.getItemName());
                    baseSku.setGoodsNameC(item.getItemName());
                    baseSku.setGoodsNameE(item.getEnglishName());
                    baseSku.setBarCode(item.getBarCode());
                    baseSku.setStockUnit(item.getStockUnit());
                    baseSku.setBrand(item.getBrandName());
                    baseSku.setSnControl(StringUtil.equals(item.getIsSNMgmt(),"Y")?"1":"0");
                    baseSku.setStatus(100);
                    baseSkuService.create(baseSku);
                    response.setItemId(baseSku.getGoodsCode());
                }
            }else if (StringUtil.equals("update",request.getActionType())){
                baseSku=baseSkuService.queryByCode(item.getGoodsCode());
                if (baseSku==null)
                    throw new BadRequestException("商品信息不存在，无法update");
                if (baseSku.getStatus()!=100)
                    throw new BadRequestException("商品状态不是已创建，无法update");
                baseSku.setGoodsCode(item.getGoodsCode());
                baseSku.setGoodsName(item.getItemName());
                baseSku.setGoodsNameC(item.getItemName());
                baseSku.setGoodsNameE(item.getEnglishName());
                baseSku.setBarCode(item.getBarCode());
                baseSku.setStockUnit(item.getStockUnit());
                baseSku.setBrand(item.getBrandName());
                baseSku.setSnControl(StringUtil.equals(item.getIsSNMgmt(),"Y")?"1":"0");
                baseSkuService.update(baseSku);
                response.setItemId(baseSku.getGoodsNo());
            }else
                throw new BadRequestException("不支持的操作："+request.getActionType());
        }catch (Exception e){
            e.printStackTrace();
            response.returnFail(e.getMessage());
        }
        return response;
    }

    @Override
    public JackYunBasicResponse cancelOrder(JackYunCancelOrderRequest request){
        JackYunBasicResponse response=new JackYunBasicResponse();
        try {
            switch (request.getOrderType()){
                case "JYCK":
                    CrossBorderOrder order=crossBorderOrderService.queryByOrderNo(request.getOrderCode());
                    if (order!=null){
                        crossBorderOrderService.cancel(order.getId());
                        order.setCrossBorderNo(order.getCrossBorderNo()+DateUtils.now());
                        order.setLogisticsNo(order.getLogisticsNo()+DateUtils.now());
                        crossBorderOrderService.update(order);
                    }
                    break;
                case "HHCK":
                case "BFCK":
                case "PTCK":
                case "DBCK":
                case "B2BCK":
                case "CGTH":
                case "QTCK":
                    outboundOrderCancel(request.getOrderCode());
                    break;
                case "B2BRK":
                case "SCRK":
                case "LYRK":
                case "CCRK":
                case "CGRK":
                case "DBRK":
                case "QTRK":
                case "XTRK":
                case "THRK":
                case "HHRK":
                    inboundOrderCancel(request.getOrderCode());
                    break;
            }
            response.returnSucc();
        }catch (Exception e){
            e.printStackTrace();
            response.returnFail(e.getMessage()==null?"Null":e.getMessage());
        }
        return response;
    }

    private void inboundOrderCancel(String orderCode) throws Exception{
        InboundOrder inboundOrder=inboundOrderService.queryByOutNo(orderCode);
        if (inboundOrder==null)
            throw new BadRequestException("入库单据不存在");
        if (inboundOrder.getStatus()== 655)
            throw new BadRequestException("入库单已入库");
        inboundOrder.setOutNo(inboundOrder.getOutNo()+"-QX"+DateUtils.format(new Date(),"yyyyMMddHHmm"));
        inboundOrderService.update(inboundOrder);
        inboundOrderService.cancel(inboundOrder.getId());
    }

    private void outboundOrderCancel(String orderCode) throws Exception{
        OutboundOrder outboundOrder=outboundOrderService.queryByOutNo(orderCode);
        if (outboundOrder==null)
            throw new BadRequestException("出库单据不存在");
        if (outboundOrder.getStatus()== 755)
            throw new BadRequestException("出库单已出库");
        outboundOrder.setOutNo(outboundOrder.getOutNo()+"-QX"+DateUtils.format(new Date(),"yyyyMMddHHmm"));
        outboundOrderService.update(outboundOrder);
        outboundOrderService.dyConfirmCancel(outboundOrder.getId());
    }

    @Override
    public JackYunBasicResponse msgPush(String method, String content, String customerid) {
        JackYunBasicResponse response;
        if (StringUtil.isBlank(content)){
            response=new JackYunBasicResponse();
            response.returnFail("content为空");
            return response;
        }
        JSONObject contentObj=new JSONObject(content);
        switch (method){
            case "entryorder.create":
                //入库单创建
                response=createInboundOrder(contentObj.toBean(JackYunEntryOrderCreateRequest.class));
                break;
            case "stockout.create":
                //出库单创建
                response=createOutboundOrder(contentObj.toBean(JackYunStockOutCreateRequest.class));
                break;
            case"deliveryorder.create":
                //发货单创建
                response=createOrder(new JSONObject(content).toBean(JackYunDeliveryOrderCreateRequest.class),customerid);
                break;
            case "order.cancel":
                //单据取消
                response=cancelOrder(new JSONObject(content).toBean(JackYunCancelOrderRequest.class));
                break;
            case "singleitem.synchronize":
                //商品同步
                syncGoodsInfo(new JSONObject(content).toBean(JackYunSingleItemSynchronizeRequest.class));
                response=new JackYunBasicResponse();
                response.returnSucc();
                break;
            default:
                response=new JackYunBasicResponse();
                response.returnFail("未处理");
                break;
        }
        return response;
    }

    @Override
    public void confirmUp(InboundOrder inboundOrder, InboundOrderLog log) {
        JackYunEntryOrderConfirmRequest request=new JackYunEntryOrderConfirmRequest();
        JackYunEntryOrderConfirm confirm=new JackYunEntryOrderConfirm();
        confirm.setEntryOrderCode(inboundOrder.getOutNo());
        ShopInfo shopInfo=shopInfoService.findById(inboundOrder.getShopId());
        confirm.setOwnerCode("FL56");
        confirm.setWarehouseCode(shopInfo.getKjgCode());
        confirm.setOutBizCode(IdUtil.simpleUUID());
        confirm.setStatus("FULFILLED");
        request.setEntryOrder(confirm);
        List<JackYunDeliverOrderLine>orderLines=new ArrayList<>();
        if (StringUtil.isBlank(inboundOrder.getWmsNo())){
            for (InboundOrderDetails detail : inboundOrder.getDetails()) {
                JackYunDeliverOrderLine orderLine=new JackYunDeliverOrderLine();
                orderLine.setOwnerCode("FL56");
                orderLine.setOrderLineNo(detail.getGoodsLineNo());
                orderLine.setItemCode(detail.getGoodsCode());
                orderLine.setPlanQty(detail.getExpectNum());
                orderLine.setInventoryType(detail.getDefault01());
                orderLine.setActualQty(detail.getTakeNum());
                BaseSku baseSku=baseSkuService.queryByGoodsNo(detail.getGoodsNo());
                if (baseSku.getLifecycle()!=null&&detail.getExpireDate()!=null){
                    orderLine.setProductDate(DateUtils.formatDate(DateUtils.subtract(detail.getExpireDate(),baseSku.getLifecycle(),DateUtils.DAY)));
                    orderLine.setExpireDate(DateUtils.formatDate(detail.getExpireDate()));
                }
                orderLine.setBatchCode(detail.getDefault02());
                List<JackYunDeliverBatch>batches=new ArrayList<>();
                JackYunDeliverBatch batch=new JackYunDeliverBatch();
                batch.setBatchCode(orderLine.getBatchCode());
                batch.setProductDate(orderLine.getProductDate());
                batch.setExpireDate(orderLine.getExpireDate());
                batch.setInventoryType(orderLine.getInventoryType());
                batch.setActualQty(orderLine.getActualQty());
                batches.add(batch);
                orderLine.setBatchs(batches);
                orderLines.add(orderLine);
            }
        }else {
            List<ReceivingTransaction>detailList=wmsSupport.getDocAsnOrderByAsnNo(inboundOrder.getWmsNo());
            for (ReceivingTransaction detail : detailList) {
                JackYunDeliverOrderLine orderLine=new JackYunDeliverOrderLine();
                BaseSku baseSku=baseSkuService.queryByGoodsNo(detail.getSku());
                orderLine.setOwnerCode("FL56");
                orderLine.setOrderLineNo(String.format("%.0f",detail.getAsnlineno()));
                orderLine.setItemCode(baseSku.getGoodsCode());
                /*for (InboundOrderDetails inboundOrderDetail : inboundOrder.getDetails()) {
                    if (StringUtil.equals(inboundOrderDetail.getGoodsLineNo(),orderLine.getOrderLineNo())){
                        orderLine.setPlanQty(inboundOrderDetail.getExpectNum());
                        break;
                    }
                }*/
                orderLine.setActualQty(Integer.parseInt(String.format("%.0f",detail.getReceivedqty())));
                orderLine.setPlanQty(orderLine.getActualQty());
                if (StringUtil.equals(detail.getLotatt08(),"良品"))
                    orderLine.setInventoryType("ZP");
                else
                    orderLine.setInventoryType("CC");
                orderLine.setActualQty(Integer.parseInt(String.format("%.0f",detail.getReceivedqty())));
                if (baseSku.getLifecycle()!=null&&detail.getLotatt02()!=null){
                    orderLine.setProductDate(DateUtils.formatDate(DateUtils.subtract(DateUtils.parseDate(detail.getLotatt02()),baseSku.getLifecycle(),DateUtils.DAY)));
                    orderLine.setExpireDate(detail.getLotatt02());
                }
                orderLine.setBatchCode(detail.getLotnum());
                List<JackYunDeliverBatch>batches=new ArrayList<>();
                JackYunDeliverBatch batch=new JackYunDeliverBatch();
                batch.setBatchCode(orderLine.getBatchCode());
                batch.setProductDate(orderLine.getProductDate());
                batch.setExpireDate(orderLine.getExpireDate());
                batch.setInventoryType(orderLine.getInventoryType());
                batch.setActualQty(orderLine.getActualQty());
                batches.add(batch);
                orderLine.setBatchs(batches);
                orderLines.add(orderLine);
            }
        }
        request.setOrderLines(orderLines);
        JackYunBasicResponse response=jackYunSupport.confirmUp(request);
        log.setReqMsg(new JSONObject(request).toString());
        log.setResMsg(new JSONObject(response).toString());
        log.setSuccess("0".equals(response.getCode())?"1":"0");
        log.setMsg(response.getMessage()==null?"":response.getMessage());
        if (StringUtil.equals(log.getSuccess(),"0"))
            throw new BadRequestException(log.getMsg());
    }

    @Override
    public void confirmOut(OutboundOrder outboundOrder, OutboundOrderLog log) {
        JackYunDeliver2bRequest request=new JackYunDeliver2bRequest();
        ShopInfo shopInfo=shopInfoService.findById(outboundOrder.getShopId());
        JackYunDeliverOrder deliveryOrder=new JackYunDeliverOrder();
        deliveryOrder.setDeliveryOrderCode(outboundOrder.getOutNo());
        deliveryOrder.setDeliveryOrderId(outboundOrder.getOrderNo());
        deliveryOrder.setWarehouseCode(shopInfo.getKjgCode());
        deliveryOrder.setOrderType(outboundOrder.getDefault01());
        deliveryOrder.setOutBizCode(IdUtil.simpleUUID());
        deliveryOrder.setOrderConfirmTime(DateUtils.now());
        List<JackYunDeliverOrderLine>orderLines=new ArrayList<>();
        boolean flag=false;
        if (StringUtil.isBlank(outboundOrder.getWmsNo())) {
            for (OutboundOrderDetails detail : outboundOrder.getDetails()) {
                if (detail.getDeliverNum()==null||detail.getDeliverNum()==0) {
                    flag=true;
                    continue;
                }else if (!detail.getDeliverNum().equals(detail.getExpectNum())){
                    flag=true;
                }
                JackYunDeliverOrderLine orderLine=new JackYunDeliverOrderLine();
                BaseSku baseSku=baseSkuService.queryByGoodsNo(detail.getGoodsNo());
                if (baseSku==null)
                    throw new BadRequestException("货号"+detail.getGoodsNo()+"找不到");
                orderLine.setOrderLineNo(detail.getGoodsLineNo());
                orderLine.setItemCode(baseSku.getGoodsCode());
                orderLine.setActualQty(detail.getDeliverNum());
                orderLine.setInventoryType(detail.getDefault01());
                orderLine.setBatchCode(detail.getDefault02());
                if (baseSku.getLifecycle()!=null){
                    orderLine.setExpireDate(DateUtils.formatDate(detail.getExpireDate()));
                    orderLine.setProductDate(DateUtils.formatDate(DateUtils.subtract(detail.getExpireDate(),baseSku.getLifecycle(),DateUtils.DAY)));
                }
                List<JackYunDeliverBatch>batches=new ArrayList<>();
                JackYunDeliverBatch batch=new JackYunDeliverBatch();
                batch.setProductDate(orderLine.getProductDate());
                batch.setExpireDate(orderLine.getExpireDate());
                batch.setActualQty(orderLine.getActualQty());
                batch.setBatchCode(orderLine.getBatchCode());
                batch.setInventoryType(orderLine.getInventoryType());
                batches.add(batch);
                orderLine.setBatchs(batches);
                orderLines.add(orderLine);
            }
        }else {
            List<ActAllocationDetails>detailsList=wmsSupport.getActAllocationBySoNo(outboundOrder.getWmsNo());
            if (CollectionUtil.isEmpty(detailsList))
                throw new BadRequestException("富勒出库单明细为空");
            for (ActAllocationDetails detail : detailsList) {
                JackYunDeliverOrderLine orderLine=new JackYunDeliverOrderLine();
                BaseSku baseSku=baseSkuService.queryByGoodsNo(detail.getSku());
                if (baseSku==null)
                    throw new BadRequestException("货号"+detail.getSku()+"找不到");
                orderLine.setOrderLineNo(detail.getOrderlineno()+"");
                orderLine.setItemCode(baseSku.getGoodsCode());
                orderLine.setActualQty(Integer.parseInt(String.format("%.0f",detail.getQty())));
                orderLine.setInventoryType(StringUtil.equals("良品",detail.getLotAtt08())?"ZP":"CC");
                orderLine.setBatchCode(detail.getLotnum());
                if (baseSku.getLifecycle()!=null){
                    orderLine.setExpireDate(detail.getLotAtt02());
                    orderLine.setProductDate(DateUtils.formatDate(DateUtils.subtract(DateUtils.parseDate(detail.getLotAtt02()),baseSku.getLifecycle(),DateUtils.DAY)));
                }
                List<JackYunDeliverBatch>batches=new ArrayList<>();
                JackYunDeliverBatch batch=new JackYunDeliverBatch();
                batch.setProductDate(orderLine.getProductDate());
                batch.setExpireDate(orderLine.getExpireDate());
                batch.setActualQty(orderLine.getActualQty());
                batch.setBatchCode(orderLine.getBatchCode());
                batch.setInventoryType(orderLine.getInventoryType());
                batches.add(batch);
                orderLine.setBatchs(batches);
                orderLines.add(orderLine);
            }
        }
        deliveryOrder.setStatus(flag?"PARTDELIVERED":"DELIVERED");
        request.setOrderLines(orderLines);
        request.setDeliveryOrder(deliveryOrder);
        JackYunBasicResponse response=jackYunSupport.confirmOut(request,"FL56");
        log.setReqMsg(new JSONObject(request).toString());
        log.setResMsg(new JSONObject(response).toString());
        log.setSuccess(StringUtil.equals(response.getCode(),"0")?"1":"0");
        log.setMsg(response.getMessage()==null?"":response.getMessage());
    }

    @Override
    public void deliver(CrossBorderOrder crossBorderOrder) {
        JackYunDeliverRequest request=new JackYunDeliverRequest();
        ShopInfo shopInfo=shopInfoService.findById(crossBorderOrder.getShopId());
        JackYunDeliverOrder deliverOrder=new JackYunDeliverOrder();
        deliverOrder.setDeliveryOrderCode(crossBorderOrder.getOrderNo());
        deliverOrder.setWarehouseCode(shopInfo.getKjgCode());
        deliverOrder.setOrderType("JYCK");
        deliverOrder.setStatus("DELIVERED");
        deliverOrder.setOutBizCode(IdUtil.simpleUUID());
        deliverOrder.setOrderConfirmTime(DateUtils.now());
        List<JackYunDeliverOrderLine>orderLines=new ArrayList<>();
        List<JackYunDeliverPackage>packageList=new ArrayList<>();
        JackYunDeliverPackage _package=new JackYunDeliverPackage();
        _package.setLogisticsCode("ZTO");
        _package.setExpressCode(crossBorderOrder.getLogisticsNo());
        packageList.add(_package);
        request.setDeliveryOrder(deliverOrder);
        request.setPackages(packageList);
        for (CrossBorderOrderDetails detail : crossBorderOrder.getItemList()) {
            JackYunDeliverOrderLine orderLine=new JackYunDeliverOrderLine();
            BaseSku baseSku=baseSkuService.queryByGoodsNo(detail.getGoodsNo());
            if (baseSku==null)
                throw new BadRequestException("货号"+detail.getGoodsNo()+"找不到");
            orderLine.setItemCode(baseSku.getGoodsCode());
            orderLine.setActualQty(Integer.parseInt(detail.getQty()));
            orderLines.add(orderLine);
        }
        request.setOrderLines(orderLines);
        JackYunBasicResponse response = jackYunSupport.deliver(request,"FL56");
        if (!StringUtil.equals(response.getCode(),"0")){
            OrderLog orderLog = new OrderLog(
                    crossBorderOrder.getId(),
                    crossBorderOrder.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_245.getCode()),
                    BooleanEnum.FAIL.getCode(),
                    response.getMessage()
            );
            orderLog.setReqMsg(com.alibaba.fastjson.JSONObject.toJSONString(request));
            orderLog.setResMsg(com.alibaba.fastjson.JSONObject.toJSONString(response));
            orderLogService.create(orderLog);
            throw new BadRequestException("发货失败");
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
        orderLog.setReqMsg(JSON.toJSONString(request));
        orderLog.setResMsg(JSON.toJSONString(response));
        orderLogService.create(orderLog);
    }

    private JackYunStockOutCreateResponse createOutboundOrder(JackYunStockOutCreateRequest request) {
        JackYunStockOutCreateResponse response=new JackYunStockOutCreateResponse();
        try {
            JackYunStockOutOrder deliveryOrder=request.getDeliveryOrder();
            if (deliveryOrder.getDeliveryOrderCode()==null)
                throw new BadRequestException("出库单号为空");
            OutboundOrder outboundOrder = outboundOrderService.queryByOutNo(deliveryOrder.getDeliveryOrderCode());
            if (outboundOrder!=null){
                response.returnSucc();
                response.setDeliveryOrderId(outboundOrder.getOrderNo());
            }
            else {
                outboundOrder=new OutboundOrder();
                outboundOrder.setOutNo(deliveryOrder.getDeliveryOrderCode());
                outboundOrder.setDefault01(deliveryOrder.getOrderType());
                outboundOrder.setExpectDeliverTime(new Timestamp(System.currentTimeMillis()+72*3600* 1000L));
                switch (deliveryOrder.getOrderType()){
                    case "DBCK"://调拨出库
                        outboundOrder.setOrderType("0");
                        break;
                    case "PTCK"://普通出库
                    case "B2BCK"://B2B出库
                    case "QTCK"://其他出库
                    case "CGTH"://采购退货出库单
                    case "XNCK"://虚拟出库单
                    case "LYCK"://领用出库
                    case "JITCK"://唯品出库
                    case "PKCK"://盘亏出库
                        outboundOrder.setOrderType("11");//其他出库
                        break;
                }
                if (CollectionUtil.isEmpty(request.getOrderLines()))
                    throw new BadRequestException("出库单明细为空");
                List<OutboundOrderDetails>details=new ArrayList<>();
                long shopId=-1;
                long custId=-1;
                for (JackYunEntryOrderOrderLine orderLine : request.getOrderLines()) {
                    BaseSku baseSku=baseSkuService.queryByCode(orderLine.getItemCode());
                    if (baseSku==null)
                        throw new BadRequestException("商品编码:\""+orderLine.getItemCode()+"\"不存在");
                    OutboundOrderDetails detail=new OutboundOrderDetails();
                    detail.setGoodsNo(baseSku.getGoodsNo());
                    detail.setGoodsCode(orderLine.getItemCode());
                    if (!StringUtils.isNumeric(orderLine.getPlanQty()))
                        throw new BadRequestException("入库单明细-计划数量:"+orderLine.getPlanQty()+" 不是一个有效的数字");
                    detail.setExpectNum(Integer.valueOf(orderLine.getPlanQty()));
                    detail.setGoodsLineNo(orderLine.getOrderLineNo());
                    detail.setDefault01(orderLine.getInventoryType());
                    detail.setDefault02(orderLine.getBatchCode());
                    details.add(detail);
                    shopId=baseSku.getShopId();
                    custId=baseSku.getCustomersId();
                }
                outboundOrder.setDetails(details);
                outboundOrder.setIsOnline("1");
                outboundOrder.setOnlineSrc("JackYun");
                outboundOrder.setShopId(shopId);
                outboundOrder.setCustomersId(custId);
                outboundOrder.setCreateBy("JackYun");
                outboundOrderService.create(outboundOrder);
                response.returnSucc();
                response.setDeliveryOrderId(outboundOrder.getOrderNo());
            }
        }catch (Exception e){
            e.printStackTrace();
            response.returnFail(e.getMessage());
        }
        return response;
    }

    private JackYunEntryOrderCreateResponse createInboundOrder(JackYunEntryOrderCreateRequest request) {
        JackYunEntryOrderCreateResponse response=new JackYunEntryOrderCreateResponse();
        try {
            JackYunEntryOrder entryOrder=request.getEntryOrder();
            if (entryOrder==null)
                throw new BadRequestException("entryOrder为null");
            if (entryOrder.getEntryOrderCode()==null)
                throw new BadRequestException("入库单号必填");
            InboundOrder inboundOrder=inboundOrderService.queryByOutNo(entryOrder.getEntryOrderCode());
            if (inboundOrder!=null) {
                response.returnSucc();
                response.setEntryOrderId(inboundOrder.getOrderNo());
            }else {
                inboundOrder=new InboundOrder();
                inboundOrder.setOutNo(entryOrder.getEntryOrderCode());
                switch (entryOrder.getOrderType()){
                    case "DBRK":
                        //调拨入库
                        inboundOrder.setOrderType("1");
                        break;
                    case "CGRK":
                        //采购入库
                        inboundOrder.setOrderType("0");
                        inboundOrder.setDeclareNo(entryOrder.getPurchaseOrderCode());
                        break;
                    case "CCRK":
                        //残次入库
                        inboundOrder.setOrderType("2");//销退入库
                    case "SCRK"://生产入库
                    case "LYRK"://领用入库
                    case "QTRK"://其它入库
                    case "B2BRK"://B2B入库
                    case "XNRK"://虚拟入库
                    case "PYRK"://盘盈入库
                        break;
                    default:
                        throw new BadRequestException("不支持的业务");
                }
                if (CollectionUtil.isEmpty(request.getOrderLines()))
                    throw new BadRequestException("入库单明细为空");
                List<InboundOrderDetails>details=new ArrayList<>();
                long shopId=-1;
                long custId=-1;
                inboundOrder.setDefault01(entryOrder.getOrderType());
                StringBuilder errMsg=new StringBuilder();
                for (JackYunEntryOrderOrderLine orderLine : request.getOrderLines()) {
                    BaseSku baseSku=baseSkuService.queryByCode(orderLine.getItemCode());
                    if (baseSku==null){
                        errMsg.append("商品编码\"").append(orderLine.getItemCode()).append("\"不存在,");
                        continue;
                    }
                    if (baseSku.getGoodsNo()==null){
                        errMsg.append("商品编码\"").append(orderLine.getItemCode()).append("\"未完成备案,");
                        continue;
                    }
                    InboundOrderDetails detail=new InboundOrderDetails();
                    detail.setGoodsLineNo(orderLine.getOrderLineNo());
                    detail.setGoodsNo(baseSku.getGoodsNo());
                    if (!StringUtils.isNumeric(orderLine.getPlanQty()))
                        throw new BadRequestException("入库单明细-计划数量:"+orderLine.getPlanQty()+" 不是一个有效的数字");
                    detail.setExpectNum(Integer.valueOf(orderLine.getPlanQty()));
                    detail.setGoodsCode(orderLine.getItemCode());
                    detail.setDefault01(orderLine.getInventoryType());
                    detail.setBarCode(baseSku.getBarCode());
                    details.add(detail);
                    shopId=baseSku.getShopId();
                    custId=baseSku.getCustomersId();
                }
                if (StringUtil.isNotBlank(errMsg)){
                    throw new BadRequestException(errMsg.toString());
                }
                inboundOrder.setDetails(details);
                if (StringUtil.isNotBlank(request.getEntryOrder().getExpectStartTime()))
                    inboundOrder.setExpectArriveTime(DateUtils.parseDateTime(request.getEntryOrder().getExpectStartTime()));
                else
                    inboundOrder.setExpectArriveTime(new Timestamp(System.currentTimeMillis()+72*3600* 1000L));
                inboundOrder.setIsOnline("1");
                inboundOrder.setOnlineSrc("JackYun");
                inboundOrder.setShopId(shopId);
                inboundOrder.setCustomersId(custId);
                inboundOrder.setCreateBy("JackYun");
                inboundOrderService.create(inboundOrder);
                response.returnSucc();
                response.setEntryOrderId(inboundOrder.getOrderNo());
            }
        }catch (Exception e){
            e.printStackTrace();
            response.returnFail(e.getMessage());
        }
        return response;
    }
}
