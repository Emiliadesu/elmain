package me.zhengjie.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.mq.CBOrderProducer;
import me.zhengjie.rest.model.douyin.CargoInbound;
import me.zhengjie.rest.model.douyin.OrderInfoNotify;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.ShopInfoDto;
import me.zhengjie.support.WmsSupport;
import me.zhengjie.support.kjg.KJGResult;
import me.zhengjie.support.kjg.KJGSupport;
import me.zhengjie.utils.*;
import me.zhengjie.utils.constant.CustomsCodeConstant;
import me.zhengjie.utils.constant.KJGMsgType;
import me.zhengjie.utils.constant.MsgType;
import me.zhengjie.utils.constant.PlatformConstant;
import me.zhengjie.utils.enums.BooleanEnum;
import me.zhengjie.utils.enums.CBGoodsStatusEnum;
import me.zhengjie.utils.enums.CBOrderStatusEnum;
import me.zhengjie.utils.enums.CBReturnOrderStatusEnum;
import org.bouncycastle.util.Times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author luob
 * @description 跨境购服务实现类
 * @date 2021/11/15
 */
@Service
@Slf4j
public class KJGServiceImpl implements KJGService {

    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

    @Autowired
    private OrderLogService orderLogService;

    @Autowired
    private CBOrderProducer cbOrderProducer;

    @Autowired
    private OrderReturnService orderReturnService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private ClearCompanyInfoService clearCompanyInfoService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private SkuLogService skuLogService;

    @Autowired
    private PlatformService platformService;

    @Autowired
    private InboundOrderService inboundOrderService;

    @Autowired
    private KJGSupport kjgSupport;

    @Autowired
    private LogisticsInfoService logisticsInfoService;

    @Autowired
    private DouyinMailMarkService douyinMailMarkService;

    @Autowired
    private ShopTokenService shopTokenService;


    @Override
    public String msgPush(String msgtype, String msg) {
        if (StringUtils.isEmpty(msgtype))
            return KJGResult.getOrderResultErr(null, null, "msgtype为空");
        if (StringUtils.isEmpty(msg))
            return KJGResult.getOrderResultErr(null, null, "msg为空");
        String result;
        switch (msgtype) {
            case KJGMsgType.NINGBO_SKU:// 货品下发
                result = handleSku(msg);
                break;
            case KJGMsgType.NINGBO_WH_ORDER:// 订单
                result = handleOrder(msg);
                break;
            case KJGMsgType.NINGBO_WH_STATUS:// 订单状态状态下发
                result = handleStatus(msg);
                break;
            case KJGMsgType.NINGBO_WH_ORDER_CANCEL:// 订单取消
                result = handleOrderCancel(msg);
                break;
            case KJGMsgType.NINGBO_WH_IN:// 入库单下发
                result = handleIn(msg);
                break;
            case KJGMsgType.NINGBO_WH_IN_CANCEL:// 入库单取消
                result = handleInCancel(msg);
                break;
            default:
                result = KJGResult.getOrderResultSucc(null, null);
                break;
        }
        return result;
    }


    /**
     * 货品下发
     * @param msg
     * @return
     */
    @Override
    public String handleSku(String msg) {
        JSONObject msgJSON = JSONUtil.xmlToJson(msg);
        JSONObject kjSkuRequest = msgJSON.getJSONObject("kjSkuRequest");
        String storer = kjSkuRequest.getStr("storer");
        String skuKey = kjSkuRequest.getStr("skuKey");//货号
        // 先查询ERP中是否存在该货品，如果存在，那么不做任何操作
        // 如果不存在，那么查询判断这个货品的货主、店铺，保存进ERP，状态为申报完成
        BaseSku exist = baseSkuService.queryByGoodsNo(skuKey);
        if (exist != null)
            return KJGResult.getSkuResultSucc(storer, skuKey);

        String goodsName = kjSkuRequest.getStr("sku");
        String uom = kjSkuRequest.getStr("uom");
        BigDecimal swt = kjSkuRequest.getBigDecimal("swt");
        String hsCode = kjSkuRequest.getStr("hsNumber");
        String property = kjSkuRequest.getStr("property");
        String gproduction = kjSkuRequest.getStr("gproduction");
        String brand = kjSkuRequest.getStr("brand");
        String guse = kjSkuRequest.getStr("guse");
        String gcomposition = kjSkuRequest.getStr("gcomposition");
        String gfunction = kjSkuRequest.getStr("gfunction");
        String dsCode = kjSkuRequest.getStr("dsCode");
        String dsSku = kjSkuRequest.getStr("dsSku");

        BaseSku sku = new BaseSku();
        sku.setStatus(CBGoodsStatusEnum.STATUS_115.getCode());
        ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.queryByCustomsCode(storer);
        if (clearCompanyInfo != null) {
            ShopInfo shopInfo = shopInfoService.queryByServiceId(clearCompanyInfo.getClearCompanyId());
            if (shopInfo != null) {
                sku.setCustomersId(shopInfo.getCustId());
                sku.setShopId(shopInfo.getId());
                sku.setRegisterType(shopInfo.getRegisterType());
            }
        }
        sku.setOuterGoodsNo(dsSku);
        sku.setGoodsName(goodsName);
        sku.setGoodsCode(skuKey);
        sku.setGoodsNo(skuKey);
        sku.setBarCode(dsCode);
        sku.setGoodsNameC(goodsName);
        sku.setHsCode(hsCode);
        sku.setNetWeight(swt);
        sku.setGrossWeight(swt);
        sku.setProperty(property);
        sku.setBrand(brand);
        sku.setUnit(uom);
        sku.setGuse(guse);
        sku.setGfunction(gfunction);
        sku.setGcomposition(gcomposition);
        sku.setMakeContry(gproduction);
        // 法一单位、法二单位从维护好的数据中取
        sku.setSnControl(BooleanEnum.FAIL.getCode());
        sku.setIsNew("1");
        sku.setIsGift("0");
        baseSkuService.create(sku);
        SkuLog log = new SkuLog(
                sku.getId(),
                String.valueOf(sku.getStatus()),
                "",
                "",
                "KJG"
        );
        skuLogService.create(log);
        return KJGResult.getSkuResultSucc(storer, skuKey);
    }

    /**
     * 入库单处理
     * @param msg
     * @return
     */
    @Override
    public String handleIn(String msg) {
        JSONObject msgJSON = JSONUtil.xmlToJson(msg);
        JSONObject kjAsnRequest = msgJSON.getJSONObject("kjAsnRequest");
        String storer = kjAsnRequest.getStr("storer");
        String receipType = kjAsnRequest.getStr("receipType");
        String externalNo = kjAsnRequest.getStr("externalNo");
        String externalNo2 = kjAsnRequest.getStr("externalNo2");
        if (StringUtils.equals("1", receipType)) {
            // 采购单
            InboundOrder exist = inboundOrderService.queryByOutNo(externalNo);
            if (exist != null)
                return KJGResult.getAsnResultSucc(storer, externalNo);
            InboundOrder inboundOrder = new InboundOrder();
            inboundOrder.setOrderNo(externalNo);
            inboundOrder.setOutNo(externalNo2);

            inboundOrder.setOrderType("0");
            inboundOrder.setExpectArriveTime(new Timestamp(System.currentTimeMillis()));
            inboundOrder.setTallyWay("0");
            inboundOrder.setIsOnline("1");
            inboundOrder.setOnlineSrc("KJG");
            List<InboundOrderDetails>details=new ArrayList<>();
            JSONArray itemArray = new JSONArray();
            try {
                itemArray = kjAsnRequest.getJSONArray("item");
            }catch (Exception e) {
                JSONObject itemObject = kjAsnRequest.getJSONObject("item");
                itemArray.add(itemObject);
            }
            for (int i = 0; i < itemArray.size(); i++) {
                String sku = itemArray.getJSONObject(i).getStr("sku");
                String qty = itemArray.getJSONObject(i).getStr("qty");
                InboundOrderDetails detail=new InboundOrderDetails();
                detail.setGoodsNo(sku);
                detail.setExpectNum(Integer.valueOf(qty));
                BaseSku baseSku = baseSkuService.queryByGoodsNo(sku);
                if (baseSku != null) {
                    detail.setGoodsId(baseSku.getId());
                    detail.setGoodsName(baseSku.getGoodsName());
                    detail.setBarCode(baseSku.getBarCode());
                    inboundOrder.setCustomersId(baseSku.getCustomersId());
                    inboundOrder.setShopId(baseSku.getShopId());
                }
                details.add(detail);
            }

            inboundOrder.setDetails(details);
            inboundOrder.setCreateBy("KJG");
            inboundOrder.setIsFourPl("0");
            inboundOrderService.create(inboundOrder);
        }else if (StringUtils.equals("20", receipType)) {
            // 退货入库,改wms状态
            OrderReturn exist = orderReturnService.queryDeclareNo(externalNo2);
            if (exist != null)
                return KJGResult.getAsnResultSucc(storer, externalNo);
            // 非富立和云联抬头的，保存进数数据库，富立抬头的不做任何更改
            if (StringUtils.equals("3302461510", storer) || StringUtils.equals("33024609V0", storer))
                return KJGResult.getAsnResultSucc(storer, externalNo);

            OrderReturn orderReturn = new OrderReturn();

            orderReturn.setIsWave("0");
            orderReturn.setIsOverTime("0");
            orderReturn.setIsBorder("1");
            orderReturn.setOrderSource("2");// 手动生成

            orderReturn.setTradeReturnNo(orderReturnService.genOrderNo());
            orderReturn.setLogisticsNo(orderReturn.getTradeReturnNo());
            orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_337.getCode());

            orderReturn.setOrderNo(externalNo);
            orderReturn.setDeclareNo(externalNo2);
            orderReturn.setIsBorder("1");
            orderReturn.setAfterSalesType("1");
            orderReturn.setSalesCustomsTime(new Timestamp(System.currentTimeMillis()));
            orderReturn.setSExpressNo(orderReturn.getTradeReturnNo());
            orderReturn.setSExpressName("中通速递");
            orderReturn.setRExpressNo(orderReturn.getTradeReturnNo());
            orderReturn.setRExpressName("中通速递");
            orderReturn.setReturnType("1");
            orderReturn.setCreateBy("SYSTEM");
            orderReturn.setCreateTime(new Timestamp(System.currentTimeMillis()));

            orderReturn.setSalesDeliverTime(new Timestamp(System.currentTimeMillis()));

            List<OrderReturnDetails> detailsList = new ArrayList<>();
            JSONArray itemArray = new JSONArray();
            try {
                itemArray = kjAsnRequest.getJSONArray("item");
            }catch (Exception e) {
                JSONObject itemObject = kjAsnRequest.getJSONObject("item");
                itemArray.add(itemObject);
            }
            for (int i = 0; i < itemArray.size(); i++) {
                String sku = itemArray.getJSONObject(i).getStr("sku");
                String qty = itemArray.getJSONObject(i).getStr("qty");


                OrderReturnDetails details = new OrderReturnDetails();
                BaseSku baseSku = baseSkuService.queryByGoodsNo(sku);

                details.setLogisticsNo(orderReturn.getOrderNo());
                details.setGoodsNo(baseSku.getGoodsNo());
                details.setFontGoodsName(baseSku.getGoodsName());
                details.setQty(new BigDecimal(qty).stripTrailingZeros().toPlainString());

                details.setGoodsId(baseSku.getId());
                details.setGoodsName(baseSku.getGoodsName());
                details.setGoodsCode(baseSku.getGoodsCode());
                details.setHsCode(baseSku.getHsCode());
                details.setBarCode(StringUtils.trim(baseSku.getBarCode()));
                orderReturn.setCustomersId(baseSku.getCustomersId());
                orderReturn.setShopId(baseSku.getShopId());
                detailsList.add(details);
            }
            orderReturn.setItemList(detailsList);
            orderReturnService.createWithDetail(orderReturn);
        }
        String result = KJGResult.getAsnResultSucc(storer, externalNo);
        return result;
    }

    /**
     * 入库单取消
     * @param msg
     * @return
     */
    @Override
    public String handleInCancel(String msg) {
        JSONObject msgJSON = JSONUtil.xmlToJson(msg);
        JSONObject kjAsnCancelRequest = msgJSON.getJSONObject("kjAsnCancelRequest");
        String storer = kjAsnCancelRequest.getStr("storer");
        String externalNo2 = kjAsnCancelRequest.getStr("externalNo2");
        String externalNo = kjAsnCancelRequest.getStr("externalNo");
        InboundOrder inboundOrder = inboundOrderService.queryByOutNo(externalNo2);
        if (inboundOrder == null)
            return KJGResult.getAsnCancelResultSucc(storer, externalNo);
        inboundOrderService.cancel(inboundOrder.getId());
        return KJGResult.getAsnCancelResultSucc(storer, externalNo);
    }

    /**
     * 更新订单状态
     * @param msg
     * @return
     */
    @Override
    public String handleStatus(String msg) {
        JSONObject msgJSON = JSONUtil.xmlToJson(msg);
        JSONObject mftVerifyRequest = msgJSON.getJSONObject("mftVerifyRequest");
        String externalNo = mftVerifyRequest.getStr("externalNo");
        String externalNo2 = mftVerifyRequest.getStr("externalNo2");
        String flag = mftVerifyRequest.getStr("flag");// 状态
        CrossBorderOrder order = crossBorderOrderService.queryByCrossBorderNoAndDeclareNo(externalNo2, externalNo);
        if (order != null) {
            if (StringUtils.equals("2", flag) && order.getStatus().intValue() == CBOrderStatusEnum.STATUS_225.getCode().intValue()) {
                if (!StringUtils.equals("2", order.getDefault02())) {
                    // 抖音申报的单子不做处理
                    // 单证放行
                    order.setDeclareStatus("22");
                    order.setClearSuccessTime(new Timestamp(System.currentTimeMillis()));
                    order.setStatus(CBOrderStatusEnum.STATUS_230.getCode());
                    crossBorderOrderService.update(order);
                    OrderLog orderLog = new OrderLog(
                            order.getId(),
                            order.getOrderNo(),
                            String.valueOf(CBOrderStatusEnum.STATUS_230.getCode()),
                            BooleanEnum.SUCCESS.getCode(),
                            BooleanEnum.SUCCESS.getDescription()
                    );
                    orderLogService.create(orderLog);
                    cbOrderProducer.send(
                            MsgType.CB_ORDER_235,
                            String.valueOf(order.getId()),
                            order.getOrderNo()
                    );
                }
            }else if (StringUtils.equals("4", flag) ) {
                if (!StringUtils.equals(PlatformConstant.DY, order.getPlatformCode())) {
                    // 非抖音货物放行的单子，直接请求富勒出库
                    crossBorderOrderService.deliverWms(order.getId());
                }
            }
        }

        String result = KJGResult.getMftVerResultSucc(externalNo);
        return result;
    }

    /**
     * 出库订单处理
     * @param msg
     * @return
     */
    @Override
    public String handleOrder(String msg) {
        JSONObject msgJSON = JSONUtil.xmlToJson(msg);
        JSONObject kjsoOrderRequest = msgJSON.getJSONObject("kjsoOrderRequest");
        String storer = kjsoOrderRequest.getStr("storer");
        String externalNo = kjsoOrderRequest.getStr("externalNo");
        String externalNo2 = kjsoOrderRequest.getStr("externalNo2");
        String shipToName = kjsoOrderRequest.getStr("shipToName");
        String shipToPhone = kjsoOrderRequest.getStr("shipToPhone");
        String billDate = kjsoOrderRequest.getStr("billDate");
        String paymentDateTime = kjsoOrderRequest.getStr("paymentDateTime");
        String provinceName = kjsoOrderRequest.getStr("provinceName");
        String cityName = kjsoOrderRequest.getStr("cityName");
        String regionName = kjsoOrderRequest.getStr("regionName");
        String shipToAddr = kjsoOrderRequest.getStr("shipToAddr");
        String shipToPassCode = kjsoOrderRequest.getStr("shipToPassCode");
        String dsPlatform = kjsoOrderRequest.getStr("dsPlatform");
        String carrierKey = kjsoOrderRequest.getStr("carrierKey");
        String expressID = kjsoOrderRequest.getStr("expressID");

        CrossBorderOrder exist = crossBorderOrderService.queryByCrossBorderNoWithDetails(externalNo2);

        if (exist != null) {
//            if (StringUtils.equals(PlatformConstant.DY, exist.getPlatformCode())) {
//                if (StringUtils.equals("2", exist.getDefault02())) {
//                    // 抖音订单平台申报的单子，需要更新订单相关的信息
//                    if (CollectionUtils.isEmpty(exist.getItemList())) {
//                        exist.setStatus(CBOrderStatusEnum.STATUS_225.getCode());
//                        exist.setDeclareNo(externalNo);// 申报单号
//                        exist.setConsigneeName(shipToName);
//                        exist.setConsigneeTel(shipToPhone);
//                        exist.setProvince(provinceName);
//                        exist.setCity(cityName);
//                        exist.setDistrict(regionName);
//                        exist.setConsigneeAddr(shipToAddr);
//                        exist.setOrderMsg(msg);
//                        // 将订单状态变为清关开始回传
//                        JSONArray itemArray = new JSONArray();
//                        try {
//                            itemArray = kjsoOrderRequest.getJSONArray("item");
//                        }catch (Exception e) {
//                            JSONObject itemObject = kjsoOrderRequest.getJSONObject("item");
//                            itemArray.add(itemObject);
//                        }
//
//                        List<CrossBorderOrderDetails> detailsList = new ArrayList<>();
//                        for (int i = 0; i < itemArray.size(); i++) {
//                            CrossBorderOrderDetails details = new CrossBorderOrderDetails();
//                            String sku = itemArray.getJSONObject(i).getStr("sku");
//                            String expectedQty = itemArray.getJSONObject(i).getStr("expectedQty");
//                            details.setOrderNo(exist.getOrderNo());
//                            details.setGoodsNo(sku);
//                            details.setQty(expectedQty);
//                            detailsList.add(details);
//                        }
//                        exist.setItemList(detailsList);
//                        crossBorderOrderService.createWithDetail(exist);
//                        // 将订单变为清关开始回传
//                        //清关开始，延迟三秒发送清关开始回传消息
//                        cbOrderProducer.delaySend(
//                                MsgType.CB_ORDER_225,
//                                String.valueOf(exist.getId()),
//                                exist.getOrderNo(),
//                                3000
//                        );
//                    }
//                }
//            }else {
//                // 非抖音的
//                exist.setOrderMsg(msg);
//                crossBorderOrderService.update(exist);
//            }
            if (!StringUtils.equals(PlatformConstant.DY, exist.getPlatformCode())) {
                exist.setOrderMsg(msg);
                crossBorderOrderService.update(exist);
            }
        }else {
            // erp系统中无此订单
            Platform platform = platformService.queryByPlafNickName(dsPlatform);
            CrossBorderOrder order = new CrossBorderOrder();
            if (platform != null) {
                order.setPlatformCode(platform.getPlafCode());
            }
            if (StringUtils.equals("格物致品", dsPlatform)) {
                // 抖音不保存订单
                return KJGResult.getOrderResultSucc(storer, externalNo);
            }
            if (StringUtils.equals(PlatformConstant.DY, order.getPlatformCode())) {
                // 抖音不保存订单
                return KJGResult.getOrderResultSucc(storer, externalNo);
            }
            LogisticsInfo logisticsInfo = logisticsInfoService.queryByKjgCode(carrierKey);
            if (logisticsInfo != null) {
                order.setSupplierId(logisticsInfo.getId());
            }
            order.setStatus(CBOrderStatusEnum.STATUS_225.getCode());
            order.setDefault01(storer);// 货主代码
            order.setOrderNo(externalNo2);// 订单号
            order.setDeclareNo(externalNo);// 申报单号
            order.setCrossBorderNo(externalNo2);
            order.setConsigneeName(shipToName);
            order.setConsigneeTel(shipToPhone);
            order.setOrderCreateTime(new Timestamp(System.currentTimeMillis()));
            order.setPayTime(new Timestamp(DateUtils.parse(paymentDateTime, "YYYYMMDD HH:mm:ss").getTime()));
            order.setProvince(provinceName);
            order.setCity(cityName);
            order.setDistrict(regionName);
            order.setConsigneeAddr(shipToAddr);
            order.setDefault04(dsPlatform);
            order.setLogisticsCode(carrierKey);
            order.setLogisticsNo(expressID);
            order.setAddMark(shipToPassCode);
            order.setCreateTime(new Timestamp(System.currentTimeMillis()));
            order.setCreateBy("SYSTEM");
            if (!StringUtils.equals(PlatformConstant.DY, order.getPlatformCode())) {
                order.setDefault05("2");// 跨境购下发
            }else {
                order.setDefault05("1");// 抖音下发
                order.setDefault02("2");// 平台申报
                DouyinMailMark douyinMailMark = douyinMailMarkService.queryByOrderNo(order.getOrderNo());
                if (douyinMailMark != null) {
                    order.setSupplierId(Long.valueOf(douyinMailMark.getSupplierId()));
                    ShopToken shopToken = shopTokenService.queryByPaltShopId(douyinMailMark.getShopId());
                    if (shopToken != null) {
                        order.setShopId(shopToken.getShopId());
                        ShopInfoDto shopInfoDto = shopInfoService.queryById(shopToken.getShopId());
                        if (shopInfoDto != null) {
                            order.setCustomersId(shopInfoDto.getCustId());
                        }
                    }
                }
            }

            order.setOrderMsg(msg);

            JSONArray itemArray = new JSONArray();
            try {
                itemArray = kjsoOrderRequest.getJSONArray("item");
            }catch (Exception e) {
                JSONObject itemObject = kjsoOrderRequest.getJSONObject("item");
                itemArray.add(itemObject);
            }
            log.info("推送明细：" + itemArray.toStringPretty());
            List<CrossBorderOrderDetails> detailsList = new ArrayList<>();
            for (int i = 0; i < itemArray.size(); i++) {
                CrossBorderOrderDetails details = new CrossBorderOrderDetails();
                String sku = itemArray.getJSONObject(i).getStr("sku");
                String expectedQty = itemArray.getJSONObject(i).getStr("expectedQty");
                BaseSku baseSku = baseSkuService.queryByGoodsNo(sku);
                if (baseSku != null) {
                    if (!StringUtils.equals(PlatformConstant.DY, order.getPlatformCode())) {
                        // 抖音不更新店铺和客户ID，等抖音推单了再更新
                        order.setShopId(baseSku.getShopId());
                        order.setCustomersId(baseSku.getCustomersId());
                    }
                    details.setGoodsId(baseSku.getId());
                    details.setGoodsCode(baseSku.getGoodsCode());
                    details.setGoodsName(baseSku.getGoodsName());
                }
                details.setOrderNo(order.getOrderNo());
                details.setGoodsNo(sku);
                details.setQty(new BigDecimal(expectedQty).stripTrailingZeros().toPlainString());
                detailsList.add(details);

            }
            order.setItemList(detailsList);
            crossBorderOrderService.createWithDetail(order);
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(order.getStatus()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderLogService.create(orderLog);

        }
        return KJGResult.getOrderResultSucc(storer, externalNo);
    }


    /**
     * 订单取消
     * @param msg
     * @return
     */
    @Override
    public String handleOrderCancel(String msg) {
        JSONObject msgJSON = JSONUtil.xmlToJson(msg);
        JSONObject wmsOrderCancelRequest = msgJSON.getJSONObject("wmsOrderCancelRequest");
        String externalNo = wmsOrderCancelRequest.getStr("externalNo");
        String externalNo2 = wmsOrderCancelRequest.getStr("externalNo2");
        CrossBorderOrder order = crossBorderOrderService.queryByCrossBorderNo(externalNo2);
        if (order != null) {
            if (!StringUtils.equals(PlatformConstant.DY, order.getPlatformCode())) {
//                if (StringUtils.equals("2", order.getDefault05())) {
                // 非抖音的单子走这里取消富勒
                crossBorderOrderService.cancelWmsOrder(order.getId());
            }
        }
        return KJGResult.getOrderCancelResultSucc();
    }

    /**
     * 回传入库完成
     * @param order
     */
    @Override
    public void confirmInSucc(InboundOrder order) {
        JSONObject result = new JSONObject();
        JSONObject wmsAsnReceiveRequest = new JSONObject();
        wmsAsnReceiveRequest.putOnce("wmwhseid", "3302461510");
        wmsAsnReceiveRequest.putOnce("externalNo", order.getOrderNo());
        wmsAsnReceiveRequest.putOnce("externalNo2", order.getOutNo());
        wmsAsnReceiveRequest.putOnce("billDate", new Date());
        wmsAsnReceiveRequest.putOnce("receiveDate", new Date());
        ShopInfoDto shopInfoDto = shopInfoService.queryById(order.getShopId());
        ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.findById(shopInfoDto.getServiceId());
        wmsAsnReceiveRequest.putOnce("storer", clearCompanyInfo.getCustomsCode());
        wmsAsnReceiveRequest.putOnce("tdq", order.getDetails().size());

        List<InboundOrderDetails> details = order.getDetails();
        JSONArray items = new JSONArray();
        for (InboundOrderDetails detail : details) {
            JSONObject item = new JSONObject();
            item.putOnce("sku", detail.getGoodsNo());
            item.putOnce("qtyQp", detail.getNormalNum());
            item.putOnce("qtyDef", detail.getDamagedNum());
            items.add(item);
        }
        wmsAsnReceiveRequest.putOnce("item", items);
        String xml = JSONUtils.toXml(result);
        kjgSupport.reqCKKJG(KJGMsgType.NINGBO_WH_IN_CONFIRM, xml);
    }
}
