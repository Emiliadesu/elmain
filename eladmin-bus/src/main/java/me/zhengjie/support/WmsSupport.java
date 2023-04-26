package me.zhengjie.support;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.rest.model.Flux.DocOrderHeader;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.*;
import me.zhengjie.support.fuliPre.ActAllocationDetails;
import me.zhengjie.support.fuliPre.BaseSkuPackInfo;
import me.zhengjie.support.fuliPre.DocOrderPackingSummary;
import me.zhengjie.utils.*;
import me.zhengjie.utils.constant.PlatformConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 与WMS交互的支持类
 */
@Slf4j
public class WmsSupport {

    @Value("${wms.data-hub.app-key}")
    private String appkey;

    @Value("${wms.data-hub.app-secret}")
    private String appSecret;

    @Value("${wms.data-hub.app-secret}")
    private String appToken;

    public static String warehouseId = "3302461510";

    private static String clientDb = "FLUXWMSDB_V4";

    private static String clientCustomerId = "FLUXWMSJSON_V4";

    @Value("${wms.data-hub.url}")
    private String url;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private ClearCompanyInfoService clearCompanyInfoService;

    @Autowired
    private LogisticsInfoService logisticsInfoService;

    @Autowired
    private SkuLogService skuLogService;

    @Autowired
    private GiftInfoService giftInfoService;

    @Autowired
    private PlatformService platformService;

    @Autowired
    private ConfigService configService;

    public void pushSku(BaseSku baseSkuDto) throws Exception {
        JSONObject request = new JSONObject();
        JSONObject xmldata = new JSONObject();
        JSONArray headers = new JSONArray();
        JSONObject header = new JSONObject();
        header.putOnce("warehouseId", warehouseId);
        ShopInfoDto shopInfoDto = shopInfoService.queryById(baseSkuDto.getShopId());
        ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.findById(shopInfoDto.getServiceId());
        header.putOnce("customerId", clearCompanyInfo.getCustomsCode());
        header.putOnce("sku", baseSkuDto.getGoodsNo());
        header.putOnce("skuDescr1", baseSkuDto.getGoodsName());
        header.putOnce("skuDescr2", shopInfoDto.getName());
        header.putOnce("itemCode", baseSkuDto.getBarCode());
        header.putOnce("skuBarcode", baseSkuDto.getBarCode());
        header.putOnce("AllocationRule", "STANDARD");

        headers.add(header);
        xmldata.putOnce("header", headers);
        request.putOnce("xmldata", xmldata);
        log.info("推送富勒SKU请求参数：{}", request.toString());
        Map<String, Object> pubParam = getPubParam(request.toString(), "SKU", "putSKU");
        String res = HttpRequest.post(url).form(pubParam).execute().body();

        JSONObject response = JSONUtil.parseObj(res).getJSONObject("Response");
        if (StringUtil.equals("0000", response.getJSONObject("return").getStr("returnCode"))) {
            log.info("{}推送富勒SKU成功", baseSkuDto.getGoodsNo());
        } else {
            log.error("{}推送富勒SKU失败：{}", baseSkuDto.getGoodsNo(), response.getJSONObject("return").getStr("returnDesc"));
            throw new BadRequestException(response.getJSONObject("return").getStr("returnDesc"));
        }
    }

    /**
     * 推送退货提总单
     * @param returnGather
     */
    public void pushReturnGather(ReturnGather returnGather) throws UnsupportedEncodingException {
        JSONObject request = new JSONObject();
        JSONObject xmldata = new JSONObject();
        JSONArray headers = new JSONArray();
        JSONObject header = new JSONObject();
        header.putOnce("warehouseId", warehouseId);
        ShopInfoDto shopInfoDto = shopInfoService.queryById(returnGather.getShopId());
        ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.findById(shopInfoDto.getServiceId());
        header.putOnce("customerId", clearCompanyInfo.getCustomsCode());
        header.putOnce("asnType", "20");
        header.putOnce("docNo", returnGather.getGatherNo());// 唯一单号
        header.putOnce("asnReferenceA", returnGather.getGatherNo());
        header.putOnce("asnReferenceC", shopInfoDto.getName());
        header.putOnce("asnCreationTime", DateUtils.formatDateTime(new Timestamp(System.currentTimeMillis())));
        JSONArray details = new JSONArray();
        List<ReturnGatherDetail> returnGatherDetails = returnGather.getItemList();
        for (ReturnGatherDetail returnGatherDetail : returnGatherDetails) {
            JSONObject detail = new JSONObject();
            detail.putOnce("lineNo", returnGatherDetail.getId());//商品行号
            detail.putOnce("sku", returnGatherDetail.getGoodsNo());//货号-仓库
            detail.putOnce("expectedQty", returnGatherDetail.getQty());
            detail.putOnce("userDefine1", returnGatherDetail.getGoodsName());
            details.add(detail);
        }
        header.putOnce("details", details);
        headers.add(header);
        xmldata.putOnce("header", headers);
        request.putOnce("xmldata", xmldata);
        log.info("推送富勒入库单请求参数：{}", request.toString());
        Map<String, Object> pubParam = getPubParam(request.toString(), "ASN", "putASN");
        String res = HttpRequest.post(url).form(pubParam).execute().body();
        JSONObject response = JSONUtil.parseObj(res).getJSONObject("Response");
        if (StringUtil.equals("0000", response.getJSONObject("return").getStr("returnCode"))) {
            log.info("{}推送富勒入库单成功", returnGather.getGatherNo());
        } else {
            log.error("{}推送富勒入库单失败：{}", returnGather.getGatherNo(), response.getJSONObject("return").getStr("returnDesc"));
            throw new BadRequestException(response.getJSONObject("return").getStr("returnDesc"));
        }
    }

    /**
     * 推送退货单
     * @param orderReturn
     */
    public void pushResturn(OrderReturn orderReturn) throws UnsupportedEncodingException {
        throw new BadRequestException("此接口已弃用");
//        JSONObject request = new JSONObject();
//        JSONObject xmldata = new JSONObject();
//        JSONArray headers = new JSONArray();
//        JSONObject header = new JSONObject();
//        header.putOnce("warehouseId", warehouseId);
//        ShopInfoDto shopInfoDto = shopInfoService.queryById(orderReturn.getShopId());
//        ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.findById(shopInfoDto.getServiceId());
//        header.putOnce("customerId", clearCompanyInfo.getCustomsCode());
//        header.putOnce("asnType", "20");
//        header.putOnce("docNo", orderReturn.getOrderNo());// 唯一单号
//        header.putOnce("asnReferenceA", orderReturn.getDeclareNo());
//        header.putOnce("asnReferenceC", shopInfoDto.getName());
//        header.putOnce("asnCreationTime", DateUtils.formatDateTime(new Timestamp(System.currentTimeMillis())));
//        JSONArray details = new JSONArray();
//        List<OrderReturnDetails> orderDetailsList = orderReturn.getItemList();
//        for (OrderReturnDetails orderReturnDetails : orderDetailsList) {
//            JSONObject detail = new JSONObject();
//            detail.putOnce("lineNo", orderReturnDetails.getId());//商品行号
//            detail.putOnce("sku", orderReturnDetails.getGoodsNo());//货号-仓库
//            detail.putOnce("expectedQty", orderReturnDetails.getQty());
//            detail.putOnce("userDefine1", orderReturnDetails.getGoodsName());
//            details.add(detail);
//        }
//        header.putOnce("details", details);
//        headers.add(header);
//        xmldata.putOnce("header", headers);
//        request.putOnce("xmldata", xmldata);
//        log.info("推送富勒入库单请求参数：{}", request.toString());
//        Map<String, Object> pubParam = getPubParam(request.toString(), "ASN", "putASN");
//        String res = HttpRequest.post(url).form(pubParam).execute().body();
//        JSONObject response = JSONUtil.parseObj(res).getJSONObject("Response");
//        if (StringUtil.equals("0000", response.getJSONObject("return").getStr("returnCode"))) {
//            log.info("{}推送富勒入库单成功", orderReturn.getOrderNo());
//        } else {
//            log.error("{}推送富勒入库单失败：{}", orderReturn.getOrderNo(), response.getJSONObject("return").getStr("returnDesc"));
//            throw new BadRequestException(response.getJSONObject("return").getStr("returnDesc"));
//        }
    }


    /**
     * 推送入库单到WMS
     *
     * @param order
     */
    public void pushInboundOrder(InboundOrder order) throws Exception {
        JSONObject request = new JSONObject();
        JSONObject xmldata = new JSONObject();
        JSONArray headers = new JSONArray();
        JSONObject header = new JSONObject();
        header.putOnce("warehouseId", warehouseId);
        ShopInfoDto shopInfoDto = shopInfoService.queryById(order.getShopId());
        ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.findById(shopInfoDto.getServiceId());
        header.putOnce("customerId", clearCompanyInfo.getCustomsCode());
        header.putOnce("asnType", "1");
        header.putOnce("docNo", order.getOrderNo());
        header.putOnce("asnReferenceA", StringUtils.isEmpty(order.getInspectNo()) ? order.getOrderNo() : order.getInspectNo());
        header.putOnce("asnReferenceB", StringUtils.isEmpty(order.getDeclareNo()) ? order.getOrderNo() :order.getDeclareNo());
        header.putOnce("asnReferenceC", shopInfoDto.getName());
        header.putOnce("asnCreationTime", DateUtils.formatDateTime(order.getCreateTime()));
        header.putOnce("expectedArriveTime1", DateUtils.formatDateTime(order.getExpectArriveTime()));
        JSONArray details = new JSONArray();
        List<InboundOrderDetails> orderDetailsList = order.getDetails();
        for (InboundOrderDetails inboundOrderDetails : orderDetailsList) {
            JSONObject detail = new JSONObject();
            detail.putOnce("lineNo", inboundOrderDetails.getId());//商品行号
            detail.putOnce("sku", inboundOrderDetails.getGoodsNo());//货号-仓库
            detail.putOnce("expectedQty", inboundOrderDetails.getExpectNum());
            detail.putOnce("userDefine1", inboundOrderDetails.getGoodsName());
            detail.putOnce("lotAtt04", order.getInspectNo());
            detail.putOnce("packUom", "EA");//单位件
            details.add(detail);
        }
        header.putOnce("details", details);
        headers.add(header);
        xmldata.putOnce("header", headers);
        request.putOnce("xmldata", xmldata);
        log.info("推送富勒入库单请求参数：{}", request.toString());
        Map<String, Object> pubParam = getPubParam(request.toString(), "ASN", "putASN");
        String res = HttpRequest.post(url).form(pubParam).execute().body();
        JSONObject response = JSONUtil.parseObj(res).getJSONObject("Response");
        if (StringUtil.equals("0000", response.getJSONObject("return").getStr("returnCode"))) {
            log.info("{}推送富勒入库单成功", order.getOrderNo());
        } else {
            log.error("{}推送富勒入库单失败：{}", order.getOrderNo(), response.getJSONObject("return").getStr("returnDesc"));
            throw new BadRequestException(response.getJSONObject("return").getStr("returnDesc"));
        }
    }

    /**
     * 推送2B出库单到WMS
     * @param outboundOrder
     * @throws Exception
     */
    public void pushOutBoundOrder(OutboundOrder outboundOrder) throws Exception {
        JSONObject xmldata = new JSONObject();
        JSONArray headers = new JSONArray();
        JSONObject header = new JSONObject();
        header.putOnce("warehouseId", warehouseId);
        ShopInfoDto shopInfoDto = shopInfoService.queryById(outboundOrder.getShopId());
        ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.findById(shopInfoDto.getServiceId());
        header.putOnce("customerId", clearCompanyInfo.getCustomsCode());
        header.putOnce("orderType", "DB");//出库单类型
        header.putOnce("docNo", outboundOrder.getOrderNo());//出库单号
        header.putOnce("soReferenceA", outboundOrder.getOrderNo());
        header.putOnce("consigneeId", "-");//出库联系人
        header.putOnce("consigneeName", "-");//出库联系人
        header.putOnce("consigneeTel1", "-");//出库联系电话
        header.putOnce("consigneeAddress1", "-");//配送地址
        header.putOnce("orderTime", DateUtils.formatDateTime(outboundOrder.getCreateTime()));//通知单下发时间
        header.putOnce("expectedShipmentTime1", DateUtils.formatDateTime(outboundOrder.getExpectDeliverTime()));//预期发货时间
        JSONArray details = new JSONArray();
        for (OutboundOrderDetails detailItem : outboundOrder.getDetails()) {
            JSONObject detail = new JSONObject();
            detail.putOnce("lineNo", detailItem.getId());//商品行号
            detail.putOnce("sku", detailItem.getGoodsNo());//货号
            detail.putOnce("qtyOrdered", detailItem.getExpectNum());//数量
            detail.putOnce("packUom", "EA");//单位件
            details.add(detail);
        }
        header.putOnce("details", details);
        headers.add(header);
        xmldata.putOnce("header", headers);
        JSONObject obj = new JSONObject();
        obj.putOnce("xmldata", xmldata);
        String dataStr = obj.toString();
        String messageid = "SO";
        String method = "putSalesOrder";
        Map<String, Object> pubParam = getPubParam(dataStr, messageid, method);
        log.info("推送出库单请求参数:{}" + dataStr);
        String resp = HttpRequest.post(url).form(pubParam).execute().body();
        JSONObject response = new JSONObject(resp).getJSONObject("Response");
        if (response == null) {
            log.error("{}推送富勒出库单失败,未成功解析：{}", outboundOrder.getOrderNo(), resp);
            throw new BadRequestException("未能解析响应");
        }
        String isSucc = response.getJSONObject("return").getStr("returnFlag");
        if ("1".equalsIgnoreCase(isSucc)) {
            log.info("{}推送富勒出库单成功", outboundOrder.getOrderNo());
            return;
        }
        log.error("{}推送富勒入库单失败：{}", outboundOrder.getOrderNo(), response.getJSONObject("return").getStr("returnDesc"));
        throw new BadRequestException(response.getJSONObject("return").getStr("returnDesc"));
    }

    /**
     * 推送订单到WMS
     * @param order
     */
    public void pushOrder(CrossBorderOrder order) throws UnsupportedEncodingException {
        JSONObject xmldata = new JSONObject();
        JSONArray headers = new JSONArray();
        JSONObject header = new JSONObject();
        header.putOnce("warehouseId", warehouseId);
        ShopInfoDto shopInfoDto = shopInfoService.queryById(order.getShopId());
        Config config = configService.queryByK("DO_NOT_PUSH_WMS");
        if (config != null) {
            String v = config.getV();
            String[] shopIds = v.split(",");
            for (int i = 0; i < shopIds.length; i++) {
                if (StringUtils.equals(shopIds[i], String.valueOf(shopInfoDto.getId()))) {
                    return;
                }
            }
        }
        if (shopInfoDto != null) {
            ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.findById(shopInfoDto.getServiceId());
            header.putOnce("customerId", clearCompanyInfo.getCustomsCode());
            header.putOnce("hedi07", shopInfoDto.getContactPhone());//联系电话
            header.putOnce("hedi08", shopInfoDto.getName());//店铺名称
        }else {
            header.putOnce("customerId", order.getDefault01());
        }
        if (StringUtils.equals(PlatformConstant.DY, order.getPlatformCode())) {
            if (StringUtils.equals("1", order.getFourPl())) {
                header.putOnce("orderType", "08");//抖音订单-4PL
            }else {
                header.putOnce("orderType", "05");//抖音订单
            }

        }else {
            header.putOnce("orderType", "01");//出库单类型
        }
        header.putOnce("soReferenceA", order.getCrossBorderNo());//订单号
        header.putOnce("docNo", order.getDeclareNo());//申报单号
        header.putOnce("soReferenceD", order.getLogisticsNo());// 运单号
        if (StringUtils.isBlank(order.getAddMark()))
            throw new BadRequestException("三段码为空");
        header.putOnce("hedi02", order.getAddMark());
        LogisticsInfo logisticsInfo = logisticsInfoService.queryById(order.getSupplierId());
        String kjgCode;
        if (logisticsInfo==null)
            kjgCode = order.getLogisticsCode();
        else if (StringUtils.equals("73698071-8-1", logisticsInfo.getKjgCode()))
            kjgCode = "73698071-8";
        else
            kjgCode = logisticsInfo.getKjgCode();
        header.putOnce("carrierId", kjgCode);// 快递编码

        header.putOnce("consigneeId", "XN");//出库联系人
        if (StringUtils.equals(PlatformConstant.PDD, order.getPlatformCode())
            || StringUtils.equals(PlatformConstant.YZ, order.getPlatformCode())) {
            JSONObject msgJSON = JSONUtil.xmlToJson(order.getOrderMsg());
            JSONObject kjsoOrderRequest = msgJSON.getJSONObject("kjsoOrderRequest");
            String provinceName = kjsoOrderRequest.getStr("provinceName");
            String cityName = kjsoOrderRequest.getStr("cityName");
            String regionName = kjsoOrderRequest.getStr("regionName");
            String shipToAddr = kjsoOrderRequest.getStr("shipToAddr");
            String shipToName = kjsoOrderRequest.getStr("shipToName");
            String shipToPhone = kjsoOrderRequest.getStr("shipToPhone");

            header.putOnce("consigneeName", shipToName);//出库联系人
            header.putOnce("consigneeTel1", shipToPhone);//联系电话
            header.putOnce("consigneeProvince", provinceName);// 省
            header.putOnce("consigneeCity", cityName);// 市
            header.putOnce("consigneeAddress2", regionName);// 区
            header.putOnce("consigneeAddress1", shipToAddr);//详细地址
        }else {
            header.putOnce("consigneeName", order.getConsigneeName());//出库联系人
            header.putOnce("consigneeTel1", order.getConsigneeTel());//联系电话
            header.putOnce("consigneeProvince", order.getProvince());// 省
            header.putOnce("consigneeCity", order.getCity());// 市
            header.putOnce("consigneeAddress2", order.getDistrict());// 区
            header.putOnce("consigneeAddress1", order.getConsigneeAddr());//详细地址
        }
        header.putOnce("orderTime", DateUtils.formatDateTime(order.getOrderCreateTime()));//通知单下发时间
        header.putOnce("expectedShipmentTime1", DateUtils.formatDateTime(order.getCreateTime()));// 创建时间
        header.putOnce("userDefine2", "海关审核放行");//海关放行标记，只推送审核成功的
        header.putOnce("userDefine3", "2-2");//海关放行标记，只推送审核成功的
        header.putOnce("route", "2-2");//海关放行标记，只推送审核成功的

        Platform platform = platformService.queryByPlafCode(order.getPlatformCode());
        header.putOnce("userDefine5", platform.getPlafNickName());//跨境购平台名称
        JSONArray details = new JSONArray();
        for (CrossBorderOrderDetails detailItem : order.getItemList()) {
            // 查询赠品信息
//            if (!hasSku(details, detailItem.getGoodsNo())) {
//                List<GiftInfo> giftInfos = giftInfoService.queryByShopIdAndSkuId(shopInfoDto.getId(), detailItem.getGoodsId());
//                if (CollectionUtils.isNotEmpty(giftInfos)) {
//                    for (GiftInfo giftInfo : giftInfos) {
//                        Date now = new Date();
//                        Date start = new Date(giftInfo.getOpenTime().getTime());
//                        Date end = new Date(giftInfo.getEndTime().getTime());
//                        if (DateUtils.compare(now, start) > 0 && DateUtils.compare(now, end) < 0) {
//                            JSONObject detail = new JSONObject();
//                            detail.putOnce("lineNo", detailItem.getId());//商品行号
//                            detail.putOnce("sku", giftInfo.getGiftNo());//货号
//                            detail.putOnce("qtyOrdered", giftInfo.getPlaceCounts());//数量
//                            details.add(detail);
//                        }
//                    }
//                }
//            }
            JSONObject detail = new JSONObject();
            detail.putOnce("lineNo", detailItem.getId());//商品行号
            detail.putOnce("sku", detailItem.getGoodsNo());//货号
            detail.putOnce("qtyOrdered", detailItem.getQty());//数量
            details.add(detail);
        }
//        List<GiftInfo> giftInfos = giftInfoService.queryByShopId(shopInfoDto.getId());
//        if (CollectionUtils.isNotEmpty(giftInfos)) {
//            for (GiftInfo giftInfo : giftInfos) {
//                Date now = new Date();
//                Date start = new Date(giftInfo.getOpenTime().getTime());
//                Date end = new Date(giftInfo.getEndTime().getTime());
//                if (DateUtils.compare(now, start) > 0 && DateUtils.compare(now, end) < 0) {
//                    JSONObject detail = new JSONObject();
//                    detail.putOnce("sku", giftInfo.getGiftNo());//货号
//                    detail.putOnce("qtyOrdered", giftInfo.getPlaceCounts());//数量
//                    details.add(detail);
//                }
//            }
//        }
        header.putOnce("details", details);
        headers.add(header);
        xmldata.putOnce("header", headers);
        JSONObject obj = new JSONObject();
        obj.putOnce("xmldata", xmldata);
        String dataStr = obj.toString();
        String messageid = "SO";
        String method = "putSalesOrder";
        Map<String, Object> pubParam = getPubParam(dataStr, messageid, method);
        log.info("推送出库单请求参数:{}" + dataStr);
        String resp = HttpRequest.post(url).form(pubParam).execute().body();
        JSONObject response = new JSONObject(resp).getJSONObject("Response");
        if (response == null) {
            log.error("{}推送富勒出库单失败,未成功解析：{}", order.getOrderNo(), resp);
            throw new BadRequestException("未能解析响应");
        }
        String isSucc = response.getJSONObject("return").getStr("returnFlag");
        if ("1".equalsIgnoreCase(isSucc)) {
            log.info("{}推送富勒出库单成功", order.getOrderNo());
            return;
        }
        log.error("{}推送富勒入库单失败：{}", order.getOrderNo(), response.getJSONObject("return").getStr("returnDesc"));
        throw new BadRequestException(response.getJSONObject("return").getStr("returnDesc"));
    }

    // 推送国内订单到富勒
    public void pushDmOrder(DomesticOrder order) throws UnsupportedEncodingException {
        JSONObject xmldata = new JSONObject();
        JSONArray headers = new JSONArray();
        JSONObject header = new JSONObject();
        header.putOnce("warehouseId", warehouseId);
        ShopInfoDto shopInfoDto = shopInfoService.queryById(order.getShopId());
        ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.findById(shopInfoDto.getServiceId());
        header.putOnce("customerId", clearCompanyInfo.getCustomsCode());
        header.putOnce("hedi07", shopInfoDto.getContactPhone());//联系电话
        header.putOnce("hedi08", shopInfoDto.getName());//店铺名称

        if (StringUtils.equals("2C", order.getOrderType())) {
            header.putOnce("orderType", "04");//抖音订单
        }else {
            header.putOnce("orderType", "03");//出库单类型
        }
        header.putOnce("soReferenceA", order.getOrderNo());//订单号
        header.putOnce("docNo", order.getOrderNo());//申报单号
        header.putOnce("soReferenceD", order.getLogisticsNo());// 运单号
        header.putOnce("hedi02", order.getAddMark());
        LogisticsInfo logisticsInfo = logisticsInfoService.queryById(order.getSupplierId());
        header.putOnce("carrierId", logisticsInfo == null ? "73698071-8" : logisticsInfo.getDefault04());// 快递编码

        header.putOnce("consigneeId", "XN");//出库联系人
        header.putOnce("consigneeName", order.getConsigneeName());//出库联系人
        header.putOnce("consigneeTel1", order.getConsigneeTel());//联系电话
        header.putOnce("consigneeProvince", order.getProvince());// 省
        header.putOnce("consigneeCity", order.getCity());// 市
        header.putOnce("consigneeAddress2", order.getDistrict());// 区
        header.putOnce("consigneeAddress1", order.getConsigneeAddr());//详细地址

        header.putOnce("orderTime", DateUtils.formatDateTime(order.getOrderCreateTime()));//通知单下发时间
        header.putOnce("expectedShipmentTime1", DateUtils.formatDateTime(order.getOrderCreateTime()));//预期发货时间
        header.putOnce("userDefine2", "海关审核放行");//海关放行标记，只推送审核成功的
        header.putOnce("userDefine3", "2-2");//海关放行标记，只推送审核成功的
        header.putOnce("route", "2-2");//海关放行标记，只推送审核成功的

        JSONArray details = new JSONArray();
        for (DomesticOrderDetails detailItem : order.getItems()) {
            // 查询赠品信息
            JSONObject detail = new JSONObject();
            detail.putOnce("lineNo", detailItem.getId());//商品行号
            detail.putOnce("sku", detailItem.getGoodsNo());//货号
            detail.putOnce("qtyOrdered", detailItem.getQty());//数量
            details.add(detail);
        }
        header.putOnce("details", details);
        headers.add(header);
        xmldata.putOnce("header", headers);
        JSONObject obj = new JSONObject();
        obj.putOnce("xmldata", xmldata);
        String dataStr = obj.toString();
        String messageid = "SO";
        String method = "putSalesOrder";
        Map<String, Object> pubParam = getPubParam(dataStr, messageid, method);
        log.info("推送出库单请求参数:{}" + dataStr);
        String resp = HttpRequest.post(url).form(pubParam).execute().body();
        JSONObject response = new JSONObject(resp).getJSONObject("Response");
        if (response == null) {
            log.error("{}推送富勒出库单失败,未成功解析：{}", order.getOrderNo(), resp);
            throw new BadRequestException("未能解析响应");
        }
        String isSucc = response.getJSONObject("return").getStr("returnFlag");
        if ("1".equalsIgnoreCase(isSucc)) {
            log.info("{}推送富勒出库单成功", order.getOrderNo());
            return;
        }
        log.error("{}推送富勒入库单失败：{}", order.getOrderNo(), response.getJSONObject("return").getStr("returnDesc"));
        throw new BadRequestException(response.getJSONObject("return").getStr("returnDesc"));
    }

    private boolean hasSku(JSONArray details, String sku) {
        for (int i = 0; i < details.size(); i++) {
            if (StringUtils.equals(sku, details.getJSONObject(i).getStr("sku"))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 取消订单
     * @param declareNo
     * @param customerId
     * @return
     */
    public String cancelOrder(String declareNo , String customerId) {
        try {
            JSONObject request = new JSONObject();
            JSONObject xmldata = new JSONObject();
            JSONObject data = new JSONObject();
            JSONObject header = new JSONObject();
            header.putOnce("warehouseId", warehouseId);
            header.putOnce("customerId", customerId);
            header.putOnce("docNo", declareNo);

            data.putOnce("header", header);
            xmldata.putOnce("data", data);
            request.putOnce("xmldata", xmldata);
            Map<String, Object> pubParam = getPubParam(request.toString(), "SOC", "cancel");
            String res = HttpRequest.post(url).form(pubParam).execute().body();
            return res;
        } catch (Exception e) {
            // 为了不影响主业务的顺畅，报任何异常都catch
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * 恢复订单
     * @param soNo
     * @return
     */
    public String recover(String soNo) {
        try {
            JSONObject request = new JSONObject();
            JSONObject xmldata = new JSONObject();
            JSONObject data = new JSONObject();
            JSONObject header = new JSONObject();
            header.putOnce("warehouseId", warehouseId);
            header.putOnce("orderNo", soNo);

            data.putOnce("header", header);
            xmldata.putOnce("data", data);
            request.putOnce("xmldata", xmldata);
            Map<String, Object> pubParam = getPubParam(request.toString(), "SOR", "recover");
            String res = HttpRequest.post(url).form(pubParam).execute().body();
            return res;
        } catch (Exception e) {
            // 为了不影响主业务的顺畅，报任何异常都catch
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * 出库
     * @param soNo
     * @return
     */
    public void deliver(String soNo) throws Exception {
        JSONObject request = new JSONObject();
        JSONObject xmldata = new JSONObject();
        JSONObject data = new JSONObject();
        JSONObject header = new JSONObject();
        header.putOnce("warehouseId", warehouseId);
        header.putOnce("orderNo", soNo);

        data.putOnce("header", header);
        xmldata.putOnce("data", data);
        request.putOnce("xmldata", xmldata);
        Map<String, Object> pubParam = getPubParam(request.toString(), "SOO", "deliver");
        String res = HttpRequest.post(url).form(pubParam).execute().body();
        log.info("请求富勒出库返回：" + res);
        JSONObject resObject = JSONUtil.parseObj(res);
        JSONObject response = resObject.getJSONObject("Response").getJSONObject("return");
        if (StringUtil.equals("413", response.getStr("returnCode"))) {
            // 当是413时，查询富勒订单状态，如果是出库就正常放行，否则就报错
            DocOrderHeader docOrderHead = getDocOrderHead(soNo);
            if (docOrderHead == null)
                throw new BadRequestException("请求富勒出库失败,富勒订单未查到");
            if (StringUtils.equals(docOrderHead.getSostatus(), "99"))
                return;
            throw new BadRequestException("请求富勒出库失败,富勒订单状态异常");
        }
        if (StringUtil.equals("0000", response.getStr("returnCode"))
//            || StringUtil.equals("413", response.getStr("returnCode"))
            || StringUtil.equals("445", response.getStr("returnCode"))) {
            return;
        } else if (StringUtil.equals("900", response.getStr("returnCode"))){
            throw new BadRequestException("请求富勒出库失败,富勒订单处于创建状态");
        } else if (StringUtil.equals("910", response.getStr("returnCode"))){
            throw new BadRequestException("请求富勒出库失败,富勒订单处于分配完成状态");
        } else if (StringUtil.equals("920", response.getStr("returnCode"))){
            throw new BadRequestException("请求富勒出库失败,富勒订单处于取消");
        }else {
            throw new BadRequestException("请求富勒出库失败" + response.getStr("returnCode"));
        }

    }

    /**
     * 根据货号查询库存
     *
     * @param sku
     * @return
     */
    public Integer queryInventoryBySku(String sku, String customerId) {
        try {
            JSONObject request = new JSONObject();
            JSONObject xmldata = new JSONObject();
            JSONObject data = new JSONObject();
            JSONObject header = new JSONObject();
            header.putOnce("warehouseId", warehouseId);
            header.putOnce("customerId", customerId);
            header.putOnce("sku", sku);
            header.putOnce("pageSize", 1);

            data.putOnce("header", header);
            xmldata.putOnce("data", data);
            request.putOnce("xmldata", xmldata);
            Map<String, Object> pubParam = getPubParam(request.toString(), "INVQ", "queryInventory");
            String res = HttpRequest.post(url).form(pubParam).execute().body();
            JSONObject resObject = JSONUtil.parseObj(res);
            if (StringUtil.isBlank(resObject.getStr("Response"))) {
                log.error(sku + "无库存");
                return 0;
            }
            JSONObject response = resObject.getJSONObject("Response");
            if (StringUtil.equals("0000", response.getStr("returnCode"))) {
                JSONArray items = resObject.getJSONObject("items").getJSONArray("item");
                if (items != null) {
                    JSONObject item = items.getJSONObject(0);
                    Integer qty = item.getInt("qty");
                    return qty;
                }
            } else {
                log.error(sku + "获取库存失败" + response.getStr("returnDesc"));
            }
            return 1;
        } catch (Exception e) {
            // 为了不影响主业务的顺畅，报任何异常都返回有库存
            e.printStackTrace();
            return 1;
        }
    }


    private Map<String, Object> getPubParam(String dataStr, String messageid, String method) throws UnsupportedEncodingException {
        String sign = sign(dataStr);
        Map<String, Object> pubParam = new HashMap<>();
        pubParam.put("method", method);
        pubParam.put("client_customerid", clientCustomerId);
        pubParam.put("messageid", messageid);
        pubParam.put("client_db", clientDb);
        pubParam.put("appkey", appkey);
        pubParam.put("apptoken", appToken);
        pubParam.put("timestamp", DateUtils.now());
        pubParam.put("sign", sign);
        pubParam.put("data", dataStr);
        return pubParam;
    }

    public String sign(String data) throws UnsupportedEncodingException {
        String Md5EncText = Md5Utils.md5Hex(this.appSecret + data + this.appSecret);
        String base64Text = Base64Encoder.encode(Md5EncText.getBytes());
        String urlEncode = URLEncoder.encode(base64Text.toUpperCase(), "UTF-8");
        return urlEncode;
    }

    /**
     * 查询WMS库存
     *
     * @param goodsNos
     */
    public JSONArray querySumStock(List<String> goodsNos) {
        String url = "http://pre.fl56.net/api/el-admin/get-sum-stock";
        JSONObject data = new JSONObject();
        data.putOnce("sku", goodsNos.toArray());
        String decData = reqPre(url, data.toString());
        return JSONUtil.parseArray(decData);

    }

    public JSONArray queryDetailsStock(List<String> goodsNos) {
        String url = "http://pre.fl56.net/api/el-admin/get-stock";
        JSONObject data = new JSONObject();
        data.putOnce("sku", goodsNos.toArray());
        return JSONUtil.parseArray(reqPre(url, data.toString()));
    }

    public JSONArray queryDetailsStockByLocation(String location) {
        String url = "http://pre.fl56.net/api/el-admin/get-stock-by-location";
        return JSONUtil.parseArray(reqPre(url, location));
    }

    public JSONObject queryAsn(String orderNo) {
        JSONObject data = new JSONObject();
        data.putOnce("ASNREFERENCE1", orderNo);
        String url = "http://pre.fl56.net/api/el-admin/get-asn-data";
        String decData = reqPre(url, data.toString());
        return JSONUtil.parseObj(decData);
    }

    public JSONObject queryAsn2(String orderNo) {
        JSONObject data = new JSONObject();
        data.putOnce("ASNREFERENCE2", orderNo);
        String url = "http://pre.fl56.net/api/el-admin/get-asn-data-2";
        String decData = reqPre(url, data.toString());
        return JSONUtil.parseObj(decData);
    }

    public JSONObject querySo(String orderNo, String decNo) {
        JSONObject data = new JSONObject();
        data.putOnce("SOREFERENCE2", orderNo);
        data.putOnce("SOREFERENCE1", decNo);
        String url = "http://pre.fl56.net/api/el-admin/get-so-data";
        String decData = reqPre(url, data.toString());
        return JSONUtil.parseObj(decData);
    }

    public JSONObject queryDailyStock(String dayTime, int pageNo, int pageSize) {
        JSONObject data = new JSONObject();
        data.putOnce("current", pageNo);
        data.putOnce("size", pageSize);
        JSONObject condition = new JSONObject();
        condition.putOnce("OTHER", dayTime);
        data.putOnce("condition", condition);
        String url = "http://pre.fl56.net/api/el-admin/query-tmp-inv-log";
        System.out.println(data.toString());
        String decData = reqPre(url, data.toString());
        return JSONUtil.parseObj(decData);
    }


    public void updateOrderTime(String orderNo, Timestamp orderCreateTime) {
        JSONObject data = new JSONObject();
        data.putOnce("SOREFERENCE2", orderNo);
        data.putOnce("EXPECTEDSHIPMENTTIME1", DateUtils.formatDateTime(orderCreateTime));
        String url = "http://pre.fl56.net/api/el-admin/update-expShipTime-by-so2";
        reqPre(url, data.toString());
    }

    // 请求Pre数据
    private String reqPre(String url, String data) {
//        CustomerKeyDto custCode = customerKeyService.findByCustCode("3302461510");
//        String encData= SecureUtils.encryptDexHex(data.toString(), custCode.getSignKey());
        String encData = SecureUtils.encryptDexHex(data.toString(), "8cbcbce121f94415a97398b0c408bb40");
        Map<String, Object> params = new HashMap<>();
        params.put("customerCode", "3302461510");
        params.put("data", encData);
        String post = HttpUtil.post(url, params);
        JSONObject resObject = JSONUtil.parseObj(post);
        if (resObject.getBool("success")) {
            String encryptData = resObject.getJSONObject("data").getStr("encryptData");
//            String decData = SecureUtils.decryptDesHex(encryptData, custCode.getSignKey());
            if (StringUtils.isEmpty(encryptData))
                return null;
            String decData = SecureUtils.decryptDesHex(encryptData, "8cbcbce121f94415a97398b0c408bb40");
            return decData;
        } else {
            throw new BadRequestException(resObject.getStr("msg"));
        }
    }

    /**
     * 获取富勒出库单分配明细
     * @param soNo
     * @return
     */
    public List<ActAllocationDetails> getActAllocationBySoNo(String soNo) {
        String url="http://pre.fl56.net/api/el-admin/get-allocation-by-orderNo";
        String data=reqPre(url,soNo);
        return new JSONArray(data).toList(ActAllocationDetails.class);
    }

    /**
     * 获取富勒库存最新的批次号
     * @param skuNos
     * @return
     */
    public List<InvLotLocIdAtt> getLotNum(List<String>skuNos) {
        String url="http://pre.fl56.net/api/el-admin/get-lotNum-by-sku";
        String data=reqPre(url,JSONUtil.toJsonStr(skuNos));
        return new JSONArray(data).toList(InvLotLocIdAtt.class);
    }

    public List<ReceivingTransaction> getDocAsnOrderByAsnNo(String asnNo) {
        String url="http://pre.fl56.net/api/el-admin/get-docAsnOrder-by-asnNo";
        String data=reqPre(url,asnNo);
        return new JSONArray(data).toList(ReceivingTransaction.class);
    }


    public void setUrl(String url) {
        this.url = url;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }


    public DocAsnHeader getAsnOrder(String wmsNo) {
        String url="http://pre.fl56.net/api/el-admin/get-asnOrder-by-asnNo";
        String data=reqPre(url,"{\"asnNo\":\""+wmsNo+"\"}");
        return new JSONObject(data).toBean(DocAsnHeader.class);
    }

    public DocOrderHeader getDocOrderHead(String wmsNo) {
        String url="http://pre.fl56.net/api/el-admin/get-docOrderHeader-by-soNo";
        String data=reqPre(url,"{\"soNo\":\""+wmsNo+"\"}");
        return new JSONObject(data).toBean(DocOrderHeader.class);
    }

    public DocOrderPackingSummary getDocOrderPackingSummary(String orderNo) {
        String url="http://pre.fl56.net/api/el-admin/get-docOrderPackingSummary-by-orderNo";
        String data=reqPre(url,"{\"orderNo\":\""+orderNo+"\"}");
        return new JSONObject(data).toBean(DocOrderPackingSummary.class);
    }

    public BaseSkuPackInfo getBaseSkuPackInfo(String goodsNo){
        String url="http://pre.fl56.net/api/el-admin/get-sku-package-info";
        String data=reqPre(url,"{\"goodsNo\":\""+goodsNo+"\"}");
        return new JSONObject(data).toBean(BaseSkuPackInfo.class);
    }

    public void updateDocGoodLineNo(String data) {
        String url="http://pre.fl56.net/api/el-admin/update-doc-good-line-no";
        reqPre(url,data);
    }

    public List<DocOrderHeader> queryOrderByWaveNo(String waveNo) {
        String url="http://pre.fl56.net/api/el-admin/query-order-by-wave-no";
        String data=reqPre(url,"{\"waveNo\":\""+waveNo+"\"}");
        return new JSONArray(data).toList(DocOrderHeader.class);
    }

    public List<ActTransactionLogSoAsnInvLotAtt> queryTranscationLogByDyApi(List<String>goodsNos,String startTime,String endTime){
        String url="http://pre.fl56.net/api/el-admin/query-transcation-log-by-dy-api";
        JSONObject req=new JSONObject();
        req.putOnce("goodsNos",goodsNos).putOnce("startTime",startTime).putOnce("endTime",endTime);
        String data=reqPre(url,req.toString());
        return new JSONArray(data).toList(ActTransactionLogSoAsnInvLotAtt.class);
    }

    public List<ActTransactionLogSoAsnInvLotAtt> queryTranscationMoveLogByM3Api(List<String>goodsNos,String startTime,String endTime) {
        String url="http://pre.fl56.net/api/el-admin/query-transcation-move-log-by-keyan-api";
        JSONObject req=new JSONObject();
        req.putOnce("goodsNos",goodsNos).putOnce("startTime",startTime).putOnce("endTime",endTime);
        String data=reqPre(url,req.toString());
        return new JSONArray(data).toList(ActTransactionLogSoAsnInvLotAtt.class);
    }

    public JSONArray trackDocNoByLotNumAndLocation(String lotNum,String location) {
        String url="http://pre.fl56.net/api/el-admin/track-docNo-by-lotNum";
        JSONObject req=new JSONObject();
        req.putOnce("lotNum",lotNum);
        req.putOnce("location",location);
        String data=reqPre(url,req.toString());
        return new JSONArray(data);
    }

    public List<ActTransactionLogSoAsnInvLotAtt> getTransRecords(String goodsNo, String fmGrade, String toGrade) {
        String url="http://pre.fl56.net/api/el-admin/get-trans-records";
        JSONObject req=new JSONObject();
        req.putOnce("goodsNo",goodsNo);
        req.putOnce("fmGrade",StringUtil.equals("1",fmGrade)?"良品":"非良品");
        req.putOnce("toGrade",StringUtil.equals("1",toGrade)?"良品":"非良品");
        String data=reqPre(url,req.toString());
        return new JSONArray(data).toList(ActTransactionLogSoAsnInvLotAtt.class);
    }
}
