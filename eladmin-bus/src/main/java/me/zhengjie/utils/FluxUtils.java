package me.zhengjie.utils;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.dto.CustomerKeyDto;
import me.zhengjie.service.dto.InvLotLocIdAtt;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.*;

public class FluxUtils {
    public static String appkey = "80AC1A3F-F949-492C-A024-7044B28C8025";
    public static String appSecret = "1234567890";
    public static String appToken = "80AC1A3F-F949-492C-A024-7044B28C8025";
    public static String warehouseId = "3302461510";
    private static String clientDb = "FLUXWMSDB_V4";
    private static String clientCustomerId = "FLUXWMSJSON_V4";
    private static String testUrl = "http://120.27.230.145:19192/datahub/FluxWmsJsonApi";
    private static String onlineUrl = "http://kjgapi.fl56.net:19192/datahub/FluxWmsJsonApi";
    private static String url = onlineUrl;
    private static final String FLIURL = "http://pre.fl56.net/";

    public static String getSign(String data) {
        String Md5EncText = Md5Utils.md5Hex(appSecret + data + appSecret);
        String base64Text = Base64Encoder.encode(Md5EncText.getBytes());
        String urlEncode;
        try {
            urlEncode = URLEncoder.encode(base64Text.toUpperCase(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new BadRequestException("不支持的编码:" + e.getMessage());
        }
        return urlEncode;
    }

    public static void main(String[] args) {
        Date now = new Date(System.currentTimeMillis() + 5 * 3600 * 1000);
        int hourse = DateUtils.hour(now, true);
        if (hourse > 16) {
            now = new Date(System.currentTimeMillis() + 24 * 3600 * 1000);
        }
        int year = DateUtils.year(now);
        int month = DateUtils.month(now) + 1;
        int day = DateUtils.dayOfMonth(now);
        Date end = DateUtils.parseDateTime(String.format("%d-%02d-%02d 16:00:00", year, month, day));
        Date start = new Date(end.getTime() - 24 * 3600 * 1000);
        System.out.println("开始时间：" + DateUtils.formatDateTime(start));
        System.out.println("结束时间：" + DateUtils.formatDateTime(end));
    }

    public static class FluxAsnDetail {
        private String lineNo;
        private String sku;
        private Integer expectedQty;
        private String userDefine1;
        private String lotAtt04;

        public String getLineNo() {
            return lineNo;
        }

        public void setLineNo(String lineNo) {
            this.lineNo = lineNo;
        }

        public String getSku() {
            return sku;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }

        public Integer getExpectedQty() {
            return expectedQty;
        }

        public void setExpectedQty(Integer expectedQty) {
            this.expectedQty = expectedQty;
        }

        public String getUserDefine1() {
            return userDefine1;
        }

        public void setUserDefine1(String userDefine1) {
            this.userDefine1 = userDefine1;
        }

        public String getLotAtt04() {
            return lotAtt04;
        }

        public void setLotAtt04(String lotAtt04) {
            this.lotAtt04 = lotAtt04;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FluxAsnDetail that = (FluxAsnDetail) o;
            return Objects.equals(sku, that.sku);
        }
    }

    /**
     * 调拨入库通知单
     *
     * @param wmsInstock
     */
    public static void pushWmsInstock(WmsInstock wmsInstock, ThirdOrderLog thirdOrderLog) {
        JSONObject xmldata = new JSONObject();
        JSONArray headers = new JSONArray();
        JSONObject header = new JSONObject();
        header.putOnce("warehouseId", warehouseId);

        //header.put("customerId",wmsInstock.getShopInfo().getClearCompanyInfo().getCustomsCode());
        header.putOnce("customerId", wmsInstock.getShopInfo().getClearCompanyInfo().getCustomsCode());
        header.putOnce("asnType", "07");
        header.putOnce("docNo", wmsInstock.getPoNo());
        header.putOnce("asnReferenceA",wmsInstock.getInOrderSn());
        header.putOnce("asnCreationTime", DateUtils.formatDateTime(wmsInstock.getCreateTime()));
        header.putOnce("expectedArriveTime1", wmsInstock.getExpectArriveTime());
        header.putOnce("notes", wmsInstock.getRemark());
        List<FluxAsnDetail> details = new ArrayList<>();
        for (WmsInstockItem wmsInstockItem : wmsInstock.getItemList()) {
            FluxAsnDetail detail = new FluxAsnDetail();
            detail.setSku(wmsInstockItem.getGoodsNo());//货号-仓库
            if (details.indexOf(detail) != -1) {
                detail = details.get(details.indexOf(detail));
                detail.setExpectedQty(detail.getExpectedQty() + wmsInstockItem.getQty());
            } else {
                detail.setLineNo(wmsInstockItem.getGoodsLineNo());//商品行号
                detail.setExpectedQty(wmsInstockItem.getQty());
                detail.setUserDefine1(wmsInstockItem.getGoodsName());
                detail.setLotAtt04(wmsInstock.getPoNo());
                details.add(detail);
            }
        }
        header.putOnce("details", details);
        headers.add(header);
        xmldata.putOnce("header", headers);
        JSONObject obj = new JSONObject();
        obj.putOnce("xmldata", xmldata);
        String dataStr = obj.toString();
        String messageid = "ASN";
        String method = "putASN";
        Map<String, Object> pubParam = getPubParam(dataStr, messageid, method);
        String resp = HttpRequest.post(url).form(pubParam).execute().body();
        thirdOrderLog.setResponse(resp);
        thirdOrderLog.setRequest(new JSONObject(pubParam).toString());
        thirdOrderLog.setPlatformCode("FluxDataHub");
        thirdOrderLog.setMethodName("com.fuli.szfl.common.utils.FluxUtils.pushWmsInstock()");
        thirdOrderLog.setShopId(wmsInstock.getCreateCustomer());
        thirdOrderLog.setCreateTime(new Timestamp(System.currentTimeMillis()));
        JSONObject response = new JSONObject(resp).getJSONObject("Response");
        if (response == null) {
            thirdOrderLog.setErrMsg("无法解析响应");
            thirdOrderLog.setIsSuccess("0");
            thirdOrderLog.setCode(500);
            throw new BadRequestException(thirdOrderLog.getErrMsg());
        }
        String isSucc = response.getJSONObject("return").getStr("returnFlag");
        if ("1".equalsIgnoreCase(isSucc)) {
            thirdOrderLog.setIsSuccess("1");
            thirdOrderLog.setCode(200);
            return;
        }
        thirdOrderLog.setErrMsg(response.getJSONObject("return").getStr("returnDesc"));
        thirdOrderLog.setIsSuccess("0");
        thirdOrderLog.setCode(500);
        throw new BadRequestException(thirdOrderLog.getErrMsg());
    }

    private static Map<String, Object> getPubParam(String dataStr, String messageid, String method) {
        String sign = getSign(dataStr);
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

    /**
     * 调拨出库通知单
     *
     * @param wmsOutstock
     */
    public static void pushWmsOutStock(WmsOutstock wmsOutstock, ThirdOrderLog thirdOrderLog) {
        JSONObject xmldata = new JSONObject();
        JSONArray headers = new JSONArray();
        JSONObject header = new JSONObject();
        header.put("warehouseId", "3302461510");
        //header.put("customerId",wmsOutstock.getShopInfo().getClearCompanyInfo().getCustomsCode());
        header.put("customerId", wmsOutstock.getShopInfo().getClearCompanyInfo().getCustomsCode());
        header.put("orderType", "07");//出库单类型
        header.put("docNo", wmsOutstock.getOutOrderSn());//出库单号
        header.put("consigneeId", StringUtil.isEmpty(wmsOutstock.getReceiver()) ? "-" : wmsOutstock.getReceiver());//出库联系人
        header.put("consigneeName", wmsOutstock.getReceiver());//出库联系人
        header.put("consigneeTel1", wmsOutstock.getPhone());//出库联系电话
        header.put("consigneeAddress1", wmsOutstock.getAddress());//配送地址
        header.put("channel", wmsOutstock.getChannel());//渠道-乐其虚拟仓业务代码
        header.put("orderTime", DateUtils.formatDateTime(wmsOutstock.getCreateTime()));//通知单下发时间
        header.put("expectedShipmentTime1", wmsOutstock.getExpectShipTime());//预期发货时间
        if ("0".equalsIgnoreCase(wmsOutstock.getOutOrderType())) {
            header.put("carrierId", "73698071-8");
            header.put("carrierName", "中通速递");
        }
        JSONArray details = new JSONArray();
        for (WmsOutstockItem wmsOutstockItem : wmsOutstock.getItemList()) {
            JSONObject detail = new JSONObject();
            detail.put("lineNo", wmsOutstockItem.getGoodsLineNo());//商品行号
            detail.put("sku", wmsOutstockItem.getGoodsNo());//仓库用货号
            detail.put("qtyOrdered", wmsOutstockItem.getQty());//通知数量
            details.add(detail);
        }
        header.put("details", details);
        headers.add(header);
        xmldata.put("header", headers);
        JSONObject obj = new JSONObject();
        obj.put("xmldata", xmldata);
        String dataStr = obj.toString();
        String messageid = "SO";
        String method = "putSalesOrder";
        Map<String, Object> pubParam = getPubParam(dataStr, messageid, method);
        String resp = HttpRequest.post(url).form(pubParam).execute().body();
        thirdOrderLog.setResponse(resp);
        thirdOrderLog.setRequest(new JSONObject(pubParam).toString());
        thirdOrderLog.setPlatformCode("FluxDataHub");
        thirdOrderLog.setMethodName("com.fuli.szfl.common.utils.FluxUtils.pushWmsOutStock()");
        thirdOrderLog.setShopId(wmsOutstock.getCreateCustomer());
        thirdOrderLog.setCreateTime(new Timestamp(System.currentTimeMillis()));
        JSONObject response = new JSONObject(resp).getJSONObject("Response");
        if (response == null) {
            thirdOrderLog.setErrMsg("无法解析响应");
            thirdOrderLog.setIsSuccess("0");
            thirdOrderLog.setCode(500);
            throw new BadRequestException(thirdOrderLog.getErrMsg());
        }
        String isSucc = response.getJSONObject("return").getStr("returnFlag");
        if ("1".equalsIgnoreCase(isSucc)) {
            thirdOrderLog.setIsSuccess("1");
            thirdOrderLog.setCode(200);
            return;
        }
        thirdOrderLog.setErrMsg("");
        thirdOrderLog.setIsSuccess("0");
        thirdOrderLog.setCode(500);
        throw new BadRequestException(thirdOrderLog.getErrMsg());
    }

    public static JSONObject queryStock(int pageNo, int pageSize, List<String> productCodes, CustomerKeyDto customerKey) {
        JSONObject body = new JSONObject();
        body.putOnce("pageNo", pageNo);
        body.putOnce("pageSize", pageSize);
        body.putOnce("productCodes", productCodes);
        String encData = SecureUtils.encryptDexHex(body.toString(), customerKey.getSignKey());
        String resp = null;
        try {
            resp = HttpRequest.post(FLIURL + "api/el-admin/query-stock?customerCode=" + customerKey.getCode() + "&data=" + URLEncoder.encode(encData, "UTF-8")).execute().body();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
        JSONObject respJs = new JSONObject(resp);
        if (respJs.getBool("success")) {
            String desc = SecureUtils.decryptDesHex(respJs.getJSONObject("data").getStr("encryptData"), customerKey.getSignKey());
            return new JSONObject(desc);
        }
        throw new BadRequestException("请求仓库错误：" + resp);
    }

    public static JSONArray sapQueryStock(CustomerKeyDto customerKey) {
        String encData = SecureUtils.encryptDexHex("{\"customerCode\":\"3302461509\"}", customerKey.getSignKey());
        String resp = null;
        try {
            resp = HttpRequest.post(FLIURL + "api/el-admin/sap-query-stock?customerCode=" + customerKey.getCode() + "&data=" + URLEncoder.encode(encData, "UTF-8")).execute().body();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
        JSONObject respJs = new JSONObject(resp);
        if (respJs.getBool("success")) {
            String desc = SecureUtils.decryptDesHex(respJs.getJSONObject("data").getStr("encryptData"), customerKey.getSignKey());
            return new JSONArray(desc);
        }
        throw new BadRequestException("请求仓库错误：" + resp);
    }

    public static String getFluxSoNo(String outOrderSn, CustomerKeyDto customerKey) {
        String encData = SecureUtils.encryptDexHex(outOrderSn, customerKey.getSignKey());
        String resp = null;
        try {
            resp = HttpRequest.post(FLIURL + "api/el-admin/get-flux-so-no?customerCode=" + customerKey.getCode() + "&data=" + URLEncoder.encode(encData, "UTF-8")).execute().body();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
        JSONObject respJs = new JSONObject(resp);
        if (respJs.getBool("success")) {
            String desc = SecureUtils.decryptDesHex(respJs.getJSONObject("data").getStr("encryptData"), customerKey.getSignKey());
            JSONObject data = new JSONObject(desc);
            return data.getStr("soNo") == null ? "-" : data.getStr("soNo");
        }
        return "-";
    }

    public static String getFluxAsnNo(String poNo, CustomerKeyDto customerKeyDto) {
        String encData = SecureUtils.encryptDexHex(poNo, customerKeyDto.getSignKey());
        String resp = null;
        try {
            resp = HttpRequest.post(FLIURL + "api/el-admin/get-flux-asn-no?customerCode=" + customerKeyDto.getCode() + "&data=" + URLEncoder.encode(encData, "UTF-8")).execute().body();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
        JSONObject respJs = new JSONObject(resp);
        if (respJs.getBool("success")) {
            String desc = SecureUtils.decryptDesHex(respJs.getJSONObject("data").getStr("encryptData"), customerKeyDto.getSignKey());
            JSONObject data = new JSONObject(desc);
            return data.getStr("asnNo") == null ? "-" : data.getStr("asnNo");
        }
        return "-";
    }

    public static void getWmsBatchNo(TrsDetail trsDetail, CustomerKeyDto customerKeyDto) {
        Map<String, Object> req = new JSONObject();
        req.put("sku", trsDetail.getGoodsNo());
        if (trsDetail.getExpiredTime()!=null)
            req.put("expireDate", DateUtils.formatDate(DateUtils.parse(trsDetail.getExpiredTime(), "yyyyMMdd")));
        req.put("inOrderSn", trsDetail.getPoNo());
        req.put("avaOrDef", StringUtil.equals(trsDetail.getIsDamaged(), "0") ? "良品" : "非良品");
        Map<String, Object> map = new HashMap<>();
        map.put("customerCode", customerKeyDto.getCode());
        map.put("data", StringUtil.removeEscape(SecureUtils.encryptDexHex(new JSONObject(req).toString(), customerKeyDto.getSignKey())));
        String resp = HttpRequest.post(FLIURL + "api/el-admin/get-batch-info-in")
                .contentType(ContentType.FORM_URLENCODED.getValue()).form(map).execute().body();
        if (StringUtil.indexOf(resp,"{")!=0)
            throw new BadRequestException("不是JSON字符串:"+resp);
        JSONObject respJs = new JSONObject(resp);
        if (respJs.getBool("success")) {
            String enc = respJs.getJSONObject("data").getStr("encryptData");
            String dec = SecureUtils.decryptDesHex(enc, customerKeyDto.getSignKey());
            if(StringUtil.equalsIgnoreCase("null",dec)){
                throw new BadRequestException("富勒找不到收货的批次号：货号："+trsDetail.getGoodsNo()+",效期："+req.get("expireDate")+",好坏品属性："+req.get("avaOrDef")+",报检号："+trsDetail.getPoNo());
            }
            try {
                InvLotLocIdAtt invLotLocIdAtt = JSONUtil.toBean(dec, InvLotLocIdAtt.class);
                trsDetail.setBatchNo(invLotLocIdAtt.getLotNum());
            }catch (Exception e){
                throw new BadRequestException("JSON序列化失败："+dec);
            }
        } else {
            throw new BadRequestException("错误:" + respJs.getStr("msg"));
        }
    }

    public static com.alibaba.fastjson.JSONObject getWmsOutStockInfo(String soNo, String barCode) {
        String response = HttpRequest.post(FLIURL + "api/el-admin/get-batch-info?soNo=" + soNo + "&barCode=" + barCode).execute().body();
        return com.alibaba.fastjson.JSONObject.parseObject(response);
    }
}
