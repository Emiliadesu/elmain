package me.zhengjie.support.meituan;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.sankuai.meituan.shangou.open.sdk.exception.SgOpenException;
import com.sankuai.meituan.shangou.open.sdk.util.SignUtil;
import me.zhengjie.domain.ShopToken;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.rest.model.meituan.MeiTuanCancel;
import me.zhengjie.rest.model.meituan.MeiTuanOrder;
import me.zhengjie.utils.AESUtils;
import me.zhengjie.utils.Md5Utils;
import me.zhengjie.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import sun.reflect.generics.tree.Tree;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MeiTuanSupport {
    @Value("${meiTuan.appId}")
    private String appId;
    @Value("${meiTuan.appSecret}")
    private String appSecret;
    private final String URL="https://waimaiopen.meituan.com";

    public static Integer translationToDYStatus(int meiTuanStatusCode) {
        switch (meiTuanStatusCode){
            case 4:
                meiTuanStatusCode=2;
                break;
            case 8:
                meiTuanStatusCode=5;
                break;
            case 9:
                meiTuanStatusCode=21;
                break;
        }
        return meiTuanStatusCode;
    }

    public static String translationStatusCode(Integer statusCode) {
        switch (statusCode){
            case 1:
                return "已下单";
            case 2:
                return "未接单";
            case 4:
                return "已接单";
            case 8:
                return "订单完成";
            case 9:
                return "已取消";
            default:
                return null;
        }
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public static void main(String[] args) throws Exception{
        ShopToken shopToken=new ShopToken();
        shopToken.setPlatformShopId("12726219");
        shopToken.setCode("code_j_DGIQT3HPDhXfDhmz5BUA");
        shopToken.setAccessToken("token_nBxs5_6mfVdhlj4QbXND_A");
        shopToken.setRefreshToken("refresh_token_m0gDK_RUOhLt3KDhkXxCiA");
        shopToken.setCodeGetTime(new Timestamp(1636353892000L));
        shopToken.setTokenTime(1638945892L);
        shopToken.setRefreshTime(new Timestamp(1651030939058L));
        test();
        //getStatus("26946230892148856",shopToken);
        //refundAgree(shopToken,"26946230892148856","填错收货信息");
        //logistics(shopToken,"26946232743986492");
        //review(shopToken,"26946231297394243","1",null,null);
        //cancelOrder(shopToken,"26946231297394243","超卖");
        //getCrossBordInfo(shopToken,"127262191258064190");
        //pullPhone(shopToken);
        //updateStock(shopToken,"0101",10,"FL56");
        //support.refundReject(request,shopToken.getAccessToken());
        //support.getCode(shopToken);
        //support.getAccessToken(shopToken);
    }
    private static void test(){
        String jsonStr="{\n" +
                "\t\"app_id\": \"7335\",\n" +
                "\t\"app_poi_code\": \"12726219\",\n" +
                "\t\"buyer_id_number\": \"2dF0gzzmD_btVOL3cq6yrrgHxOdloerzL_FmBt9u6w8\",\n" +
                "\t\"buyer_id_type\": \"1\",\n" +
                "\t\"buyer_name\": \"d7ft3eYzyBjNyZmzQ_SJlQ\",\n" +
                "\t\"buyer_reg_no\": \"z0Z0pEseqlAYyJr6_2ccgQ\",\n" +
                "\t\"currency\": \"142\",\n" +
                "\t\"ebp_code\": \"4403961ETL\",\n" +
                "\t\"ebp_name\": \"深圳百寿健康信息技术有限公司\",\n" +
                "\t\"order_type\": \"I\",\n" +
                "\t\"pay_code\": \"11089609G4\",\n" +
                "\t\"pay_name\": \"北京钱袋宝支付技术有限公司\",\n" +
                "\t\"pay_time\": \"1636427997\",\n" +
                "\t\"pay_transaction_id\": \"21110911100300030003099907969630\",\n" +
                "\t\"payer_id_number\": \"2dF0gzzmD_btVOL3cq6yrrgHxOdloerzL_FmBt9u6w8\",\n" +
                "\t\"payer_id_type\": \"1\",\n" +
                "\t\"payer_name\": \"d7ft3eYzyBjNyZmzQ_SJlQ\",\n" +
                "\t\"sig\": \"981707ce9ad1bd201f16ef13c1b942d2\",\n" +
                "\t\"tax_total\": \"0\",\n" +
                "\t\"timestamp\": \"1636428000\",\n" +
                "\t\"wm_order_id_view\": \"127262192329201389\"\n" +
                "}";
        JSONObject object=JSONObject.parseObject(jsonStr);
        String appId=object.getString("app_id");
        String timestamp=object.getString("timestamp");
        String sig=object.getString("sig");
        urlDecodeJson(object);
        System.out.println(appId);
        System.out.println(timestamp);
        System.out.println(sig);
        System.out.println(object.toJSONString());
        object.remove("sig");
        Map<String,String>map=object.toJavaObject(Map.class);
        TreeMap<String,String>treeMap=mapToTreeMap(map);
        MeiTuanSupport support=new MeiTuanSupport();
        String sign=support.getSignByIsv(treeMap,"/api/meituan/order",support.URL);
        System.out.println(sign);
        System.out.println(sig.equals(sign));
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();

    }

    public static void urlDecodeJson(JSONObject object) {
        if (object==null||object.size()==0)
            return;
        for (String key : object.keySet()) {
            object.put(key,URLUtil.decode(object.getString(key)));
        }
    }

    private static TreeMap<String, String> mapToTreeMap(Map<String, String> map) {
        TreeMap<String,String>treeMap=new TreeMap<>();
        if (CollectionUtil.isEmpty(map))
            return null;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            treeMap.put(entry.getKey(),entry.getValue());
        }
        return treeMap;
    }

    public static void confirmOrder(ShopToken shopToken,String orderId){
        MeiTuanOrderConfirmRequest request=new MeiTuanOrderConfirmRequest();
        request.setOrderId(orderId);
        MeiTuanSupport support=new MeiTuanSupport();
        support.confirmOrder(request,shopToken.getAccessToken());
    }
    public static void cancelOrder(ShopToken shopToken,String orderId,String reason){
        MeiTuanSupport support=new MeiTuanSupport();
        MeiTuanViewStatusRequest statusRequest=new MeiTuanViewStatusRequest();
        statusRequest.setOrderId(orderId);
        MeiTuanViewStatusResponse response=support.getStatus(statusRequest,shopToken.getAccessToken());
        if (response.getStatus()==9){
            System.out.println("订单已退款");
            return;
        }
        MeiTuanOrderCancelRequest request=new MeiTuanOrderCancelRequest();
        request.setOrderId(orderId);
        request.setReason(reason);
        support.confirmCancel(request,shopToken.getAccessToken());
    }
    public static void refundAgree(ShopToken shopToken,String orderId,String reason){
        MeiTuanOrderRefundAgreeRequest request=new MeiTuanOrderRefundAgreeRequest();
        request.setOrderId(orderId);
        request.setReason(reason);
        MeiTuanSupport support=new MeiTuanSupport();
        support.confirmRefund(request,shopToken.getAccessToken());
        MeiTuanOrderCancelRequest cancel=new MeiTuanOrderCancelRequest();
        cancel.setOrderId(request.getOrderId());
        cancel.setReason(request.getReason());
        support.confirmCancel(cancel,shopToken.getAccessToken());
    }
    public static void refundReject(ShopToken shopToken,String orderId){
        MeiTuanOrderRefundRejectRequest request = new MeiTuanOrderRefundRejectRequest();
        request.setOrderId(orderId);
        request.setReason("订单已经称重完成或者已出库");
        MeiTuanSupport support=new MeiTuanSupport();
        support.refundReject(request,shopToken.getAccessToken());
    }
    public static void logistics(ShopToken shopToken,String orderId){
        MeiTuanLogisticsRequest request = new MeiTuanLogisticsRequest();
        request.setOrderId(orderId);
        request.setLogisticsCode("77121099809233");
        request.setLogisticsProviderCode("10021");
        MeiTuanSupport support=new MeiTuanSupport();
        support.logistics(request,shopToken.getAccessToken());
    }
    public static void pullPhone(ShopToken shopToken){
        MeiTuanOrderBatchPullPhoneNumberRequest request=new MeiTuanOrderBatchPullPhoneNumberRequest();
        request.setAppPoiCode(shopToken.getPlatformShopId());
        request.setLimit("100");
        request.setOffset("0");
        MeiTuanSupport support=new MeiTuanSupport();
        List<MeiTuanOrderBatchPullPhoneNumberResponse>list=support.batchPullPhoneNumber(request,shopToken.getAccessToken());
        if (CollectionUtil.isEmpty(list))
            return;
        for (MeiTuanOrderBatchPullPhoneNumberResponse response : list) {
            System.out.println("订单号："+response.getOrderId());
            System.out.println("门店id："+response.getAppPoiCode());
            System.out.println("订单展示id："+response.getWmOrderIdView());
            System.out.println("订单流水号："+response.getDaySeq());
            System.out.println("收货人号码："+response.getRealPhoneNumber());
            System.out.println("收货人预留号码："+response.getRealOrderPhoneNumber());
            System.out.println();
        }
    }
    public static void getCrossBordInfo(ShopToken shopToken,String orderId) throws Exception{
        MeiTuanGetCrossBorderDetailRequest request=new MeiTuanGetCrossBorderDetailRequest();
        request.setAppPoiCode(shopToken.getPlatformShopId());
        request.setWmOrderIdView(orderId);
        MeiTuanSupport support=new MeiTuanSupport();
        MeiTuanGetCrossBorderDetailResponse response=support.getCrossBorderDetail(request,shopToken.getAccessToken());
        if (StringUtil.indexOf(response.getData(),"{")==0){
            MeiTuanCrossBorderDetail detail=JSONObject.parseObject(response.getData(),MeiTuanCrossBorderDetail.class);
            detail.setPayerIdNumber(AESUtils.decrypt(detail.getPayerIdNumber(),support.appSecret.substring(0,16),support.appSecret.substring(0,16)));
            detail.setPayerName(AESUtils.decrypt(detail.getPayerName(),support.appSecret.substring(0,16),support.appSecret.substring(0,16)));
            detail.setBuyerIdNumber(AESUtils.decrypt(detail.getBuyerIdNumber(),support.appSecret.substring(0,16),support.appSecret.substring(0,16)));
            detail.setBuyerName(AESUtils.decrypt(detail.getBuyerName(),support.appSecret.substring(0,16),support.appSecret.substring(0,16)));
            System.out.println();
        }
        System.out.println();
    }
    public static void getStatus(String orderId,ShopToken shopToken){
        MeiTuanSupport support=new MeiTuanSupport();
        MeiTuanViewStatusRequest request=new MeiTuanViewStatusRequest();
        request.setOrderId(orderId);
        MeiTuanViewStatusResponse response=support.getStatus(request,shopToken.getAccessToken());
        String status=getStatusText(response.getStatus());
        System.out.println(status);
    }

    public static String getStatusText(Integer status) {
        if (status==null)
            return null;
        String statusText=null;
        switch (status){
            case 1:
                statusText="用户已提交订单";
                break;
            case 2:
                statusText="向商家推送订单";
                break;
            case 4:
                statusText="已接单";
                break;
            case 8:
                statusText="订单完成";
                break;
            case 9:
                statusText="订单取消";
                break;
        }
        return statusText;
    }

    public static void review(ShopToken shopToken,String orderId,String reviewCode,String rejectCode,String rejectOther){
        MeiTuanOrderReviewAfterSalesRequest request=new MeiTuanOrderReviewAfterSalesRequest();
        request.setWmOrderIdView(orderId);
        request.setReviewType(reviewCode);
        if (StringUtil.equals(reviewCode,"2")){
            request.setRejectReasonCode(rejectCode);
            if (StringUtil.equals(rejectCode,"-1")){
                request.setRejectOtherReason(rejectOther);
            }
        }
        MeiTuanSupport support=new MeiTuanSupport();
        support.reviewAfterSales(request,shopToken.getAccessToken());
    }
    public static void updateStock(ShopToken shopToken,String outGoodsNo,int stock,String warehouseCode){
        MeiTuanUpdateStockRequest request=new MeiTuanUpdateStockRequest();
        request.setAppPoiCode(shopToken.getPlatformShopId());
        MeiTuanMedicineData data=new MeiTuanMedicineData();
        data.setAppMedicineCode(outGoodsNo);
        data.setStock(stock);
        data.setWarehouseCode(warehouseCode);
        List<MeiTuanMedicineData>list=new ArrayList<>();
        list.add(data);
        request.setMedicineData(JSONObject.toJSONString(list));
        MeiTuanSupport support=new MeiTuanSupport();
        support.stockUpdate(request,shopToken.getAccessToken());
    }
    public void getCode(ShopToken shopToken){
        TreeMap<String,String>map=new TreeMap<>();
        long timestamp=System.currentTimeMillis()/1000L;
        map.put("app_id",appId);
        map.put("timestamp",timestamp+"");
        map.put("app_poi_code",shopToken.getPlatformShopId());
        map.put("response_type","code");
        String sign=getSignByIsv(map,"/api/v1/oauth/authorize",URL);
        String url=URL+"/api/v1/oauth/authorize?app_id="+appId +
                "&app_poi_code="+shopToken.getPlatformShopId()+"&response_type=code&sig="+sign+"&timestamp="+timestamp;
        String resp= HttpUtil.get(url);
        MeiTuanGetCodeResponse response=JSONObject.parseObject(resp,MeiTuanGetCodeResponse.class);
        if (response.getStatus()==0){
            shopToken.setCode(response.getCode());
            shopToken.setCodeGetTime(new Timestamp(System.currentTimeMillis()));
        }else
            throw new BadRequestException(response.getMessage());
    }

    public void getAccessToken(ShopToken shopToken){
        TreeMap<String,String>map=new TreeMap<>();
        long timestamp=System.currentTimeMillis()/1000L;
        map.put("app_id",appId);
        map.put("grant_type","authorization_code");
        map.put("timestamp",timestamp+"");
        map.put("code",shopToken.getCode());
        String sign=getSignByIsv(map,"/api/v1/oauth/token",URL);
        String url=URL+"/api/v1/oauth/token?app_id="+appId +
                "&grant_type=authorization_code&code="+shopToken.getCode()+"&sig="+sign+"&timestamp="+timestamp;
        String resp= HttpUtil.post(url,"");
        MeiTuanTokenResponse response=JSONObject.parseObject(resp,MeiTuanTokenResponse.class);
        if (response.getStatus()==0){
            shopToken.setAccessToken(response.getAccessToken());
            shopToken.setRefreshToken(response.getRefreshToken());
            shopToken.setTokenTime(response.getExpireIn()+(System.currentTimeMillis()/1000));
            shopToken.setRefreshTime(new Timestamp(System.currentTimeMillis()+response.getReExpireIn()*1000L));
            shopToken.setCodeGetTime(new Timestamp(System.currentTimeMillis()));
        }else
            throw new BadRequestException(response.getMessage());
    }

    public void quickGetAccessToken(ShopToken shopToken){
        TreeMap<String,String>map=new TreeMap<>();
        map.put("app_id",appId);
        map.put("app_poi_code",shopToken.getPlatformShopId());
        map.put("response_type","token");
        String sign=getSignByIsv(map,"/api/v1/oauth/token",URL);
        String url=URL+"/api/v1/oauth/token?app_id="+appId +
                "&response_type=token&app_poi_code="+shopToken.getPlatformShopId()+"&sign="+sign;
        String resp= HttpUtil.get(url);
        MeiTuanTokenResponse response=JSONObject.parseObject(resp,MeiTuanTokenResponse.class);
        if (response.getStatus()==0){
            shopToken.setAccessToken(response.getAccessToken());
            shopToken.setRefreshToken(response.getRefreshToken());
            shopToken.setTokenTime(response.getExpireIn()+(System.currentTimeMillis()/1000));
            shopToken.setRefreshTime(new Timestamp(System.currentTimeMillis()+response.getReExpireIn()*1000L));
            shopToken.setCodeGetTime(new Timestamp(System.currentTimeMillis()));
        }else
            throw new BadRequestException(response.getMessage());
    }

    public void refreshAccessToken(ShopToken shopToken){
        long timestamp=System.currentTimeMillis()/1000L;
        TreeMap<String,String>map=new TreeMap<>();
        map.put("app_id",appId);
        map.put("grant_type","refresh_token");
        map.put("refresh_token",shopToken.getRefreshToken());
        map.put("timestamp",timestamp+"");
        String sign=getSignByIsv(map,"/api/v1/oauth/token",URL);
        String url=URL+"/api/v1/oauth/token";
        String resp= HttpRequest.post(url).form("app_id",appId)
                .form("grant_type","refresh_token")
                .form("refresh_token",shopToken.getRefreshToken())
                .form("timestamp",timestamp)
                .form("sig",sign)
                .contentType(ContentType.FORM_URLENCODED.getValue())
                .execute().body();
        MeiTuanTokenResponse response=JSONObject.parseObject(resp,MeiTuanTokenResponse.class);
        if (response.getStatus()==0){
            shopToken.setAccessToken(response.getAccessToken());
            shopToken.setRefreshToken(response.getRefreshToken());
            shopToken.setTokenTime(response.getExpireIn()+(System.currentTimeMillis()/1000));
            shopToken.setRefreshTime(new Timestamp(System.currentTimeMillis()+180*24*3600*1000L));
            shopToken.setCodeGetTime(new Timestamp(System.currentTimeMillis()));
        }else
            throw new BadRequestException(response.getMessage());
    }

    /**
     * 接单
     * @param request 请求参数体
     * @param accessToken 令牌
     */
    public MeiTuanBaseResponse confirmOrder(MeiTuanOrderConfirmRequest request,String accessToken){
        long timestamp=System.currentTimeMillis()/1000;
        TreeMap<String,String>map=JSONObject.parseObject(JSONObject.toJSONString(request),TreeMap.class);
        map.put("timestamp",timestamp+"");
        map.put("access_token",accessToken);
        map.put("app_id",appId);
        String sig=getSignByIsv(map,request.getMethod(),URL);
        HttpRequest httpRequest=HttpRequest.get(URL+request.getMethod()+"?access_token="+accessToken+"&app_id="+appId+"&sig="+sig+"&timestamp="+timestamp);
        map.remove("timestamp");
        map.remove("access_token");
        map.remove("app_id");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            httpRequest.form(entry.getKey(),entry.getValue());
        }
        String resp=httpRequest.execute().body();
        System.out.println(resp);
        return JSONObject.parseObject(resp,MeiTuanBaseResponse.class);


    }

    /**
     * 获取跨境信息
     * @param request 请求参数体
     * @param accessToken 令牌
     */
    public MeiTuanGetCrossBorderDetailResponse getCrossBorderDetail(MeiTuanGetCrossBorderDetailRequest request,String accessToken){
        long timestamp=System.currentTimeMillis()/1000;
        TreeMap<String,String>map=JSONObject.parseObject(JSONObject.toJSONString(request),TreeMap.class);
        map.put("timestamp",timestamp+"");
        map.put("access_token",accessToken);
        map.put("app_id",appId);
        String sig=getSignByIsv(map,request.getMethod(),URL);
        HttpRequest httpRequest=HttpRequest.get(URL+request.getMethod()+"?access_token="+accessToken+"&app_id="+appId+"&sig="+sig+"&timestamp="+timestamp);
        map.remove("timestamp");
        map.remove("access_token");
        map.remove("app_id");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            httpRequest.form(entry.getKey(),entry.getValue());
        }
        String resp=httpRequest.execute().body();
        System.out.println(resp);
        return JSONObject.parseObject(resp,MeiTuanGetCrossBorderDetailResponse.class);
    }



    /**
     * 确认取消订单
     * @param request 请求参数体
     * @param accessToken 令牌
     */
    public MeiTuanBaseResponse confirmCancel(MeiTuanOrderCancelRequest request,String accessToken){
        long timestamp=System.currentTimeMillis()/1000;
        TreeMap<String,String>map=JSONObject.parseObject(JSONObject.toJSONString(request),TreeMap.class);
        map.put("timestamp",timestamp+"");
        map.put("access_token",accessToken);
        map.put("app_id",appId);
        String sig=getSignByIsv(map,request.getMethod(),URL);
        HttpRequest httpRequest=HttpRequest.get(URL+request.getMethod()+"?access_token="+accessToken+"&app_id="+appId+"&sig="+sig+"&timestamp="+timestamp);
        map.remove("timestamp");
        map.remove("access_token");
        map.remove("app_id");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            httpRequest.form(entry.getKey(),entry.getValue());
        }
        String resp=httpRequest.execute().body();
        System.out.println(resp);
        return JSONObject.parseObject(resp,MeiTuanBaseResponse.class);
    }

    /**
     * 确认退款请求
     * @param request 请求参数体
     * @param accessToken 令牌
     */
    public MeiTuanBaseResponse confirmRefund(MeiTuanOrderRefundAgreeRequest request,String accessToken){
        long timestamp=System.currentTimeMillis()/1000;
        TreeMap<String,String>map=JSONObject.parseObject(JSONObject.toJSONString(request),TreeMap.class);
        map.put("timestamp",timestamp+"");
        map.put("access_token",accessToken);
        map.put("app_id",appId);
        String sig=getSignByIsv(map,request.getMethod(),URL);
        HttpRequest httpRequest=HttpRequest.post(URL+request.getMethod()+"?access_token="+accessToken+"&app_id="+appId+"&sig="+sig+"&timestamp="+timestamp);
        map.remove("timestamp");
        map.remove("access_token");
        map.remove("app_id");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            httpRequest.form(entry.getKey(),entry.getValue());
        }
        System.out.println(JSONObject.toJSONString(request));
        String resp=httpRequest.execute().body();
        System.out.println(resp);
        return JSONObject.parseObject(resp,MeiTuanBaseResponse.class);
    }

    /**
     * 驳回退款请求
     * @param request 请求参数体
     * @param accessToken 令牌
     */
    public MeiTuanBaseResponse refundReject(MeiTuanOrderRefundRejectRequest request,String accessToken){
        long timestamp=System.currentTimeMillis()/1000;
        TreeMap<String,String>map=JSONObject.parseObject(JSONObject.toJSONString(request),TreeMap.class);
        map.put("timestamp",timestamp+"");
        map.put("access_token",accessToken);
        map.put("app_id",appId);
        String sig=getSignByIsv(map,request.getMethod(),URL);
        HttpRequest httpRequest=HttpRequest.post(URL+request.getMethod()+"?access_token="+accessToken+"&app_id="+appId+"&sig="+sig+"&timestamp="+timestamp);
        map.remove("timestamp");
        map.remove("access_token");
        map.remove("app_id");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            httpRequest.form(entry.getKey(),entry.getValue());
        }
        String resp=httpRequest.execute().body();
        System.out.println(resp);
        return JSONObject.parseObject(resp,MeiTuanBaseResponse.class);
    }

    /**
     * 批量拉取用户真实手机号
     * @param request 请求参数体
     * @param accessToken 令牌
     */
    public List<MeiTuanOrderBatchPullPhoneNumberResponse> batchPullPhoneNumber(MeiTuanOrderBatchPullPhoneNumberRequest request, String accessToken){
        long timestamp=System.currentTimeMillis()/1000;
        TreeMap<String,String>map=JSONObject.parseObject(JSONObject.toJSONString(request),TreeMap.class);
        map.put("timestamp",timestamp+"");
        map.put("access_token",accessToken);
        map.put("app_id",appId);
        String sig=getSignByIsv(map,request.getMethod(),URL);
        HttpRequest httpRequest=HttpRequest.post(URL+request.getMethod()+"?access_token="+accessToken+"&app_id="+appId+"&sig="+sig+"&timestamp="+timestamp);
        map.remove("timestamp");
        map.remove("access_token");
        map.remove("app_id");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            httpRequest.form(entry.getKey(),entry.getValue());
        }
        String resp=httpRequest.execute().body();
        System.out.println(resp);
        JSONObject response=JSONObject.parseObject(resp);
        if (response.getString("data").indexOf("[")!=0){
            //非数组
            if (!StringUtil.equals(response.getString("data"),"ok")){
                JSONObject error=response.getJSONObject("error");
                throw new BadRequestException("错误代码"+error.getInteger("code")+":"+error.getString("msg"));
            }
        }
        return response.getJSONArray("data").toJavaList(MeiTuanOrderBatchPullPhoneNumberResponse.class);
    }

    /**
     * 售后处理
     * @param request 请求参数体
     * @param accessToken 令牌
     */
    public void reviewAfterSales(MeiTuanOrderReviewAfterSalesRequest request,String accessToken){
        long timestamp=System.currentTimeMillis()/1000;
        TreeMap<String,String>map=JSONObject.parseObject(JSONObject.toJSONString(request),TreeMap.class);
        map.put("timestamp",timestamp+"");
        map.put("access_token",accessToken);
        map.put("app_id",appId);
        String sig=getSignByIsv(map,request.getMethod(),URL);
        HttpRequest httpRequest=HttpRequest.get(URL+request.getMethod()+"?access_token="+accessToken+"&app_id="+appId+"&sig="+sig+"&timestamp="+timestamp);
        map.remove("timestamp");
        map.remove("access_token");
        map.remove("app_id");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            httpRequest.form(entry.getKey(),entry.getValue());
        }
        String resp=httpRequest.execute().body();
        System.out.println(resp);
        JSONObject response=JSONObject.parseObject(resp);
        if (!StringUtil.equals(response.getString("data"),"ok")){
            JSONObject error=response.getJSONObject("error");
            throw new BadRequestException("错误代码"+error.getInteger("code")+":"+error.getString("msg"));
        }
    }

    /**
     * 发货
     * @param request
     * @param accessToken
     */
    public MeiTuanBaseResponse logistics(MeiTuanLogisticsRequest request,String accessToken){
        long timestamp=System.currentTimeMillis()/1000;
        TreeMap<String,String>map=JSONObject.parseObject(JSONObject.toJSONString(request),TreeMap.class);
        map.put("timestamp",timestamp+"");
        map.put("access_token",accessToken);
        map.put("app_id",appId);
        String sig=getSignByIsv(map,request.getMethod(),URL);
        HttpRequest httpRequest=HttpRequest.post(URL+request.getMethod()+"?access_token="+accessToken+"&app_id="+appId+"&sig="+sig+"&timestamp="+timestamp);
        map.remove("timestamp");
        map.remove("access_token");
        map.remove("app_id");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            httpRequest.form(entry.getKey(),entry.getValue());
        }
        String resp=httpRequest.execute().body();
        System.out.println(resp);
        return JSONObject.parseObject(resp,MeiTuanBaseResponse.class);
    }

    /**
     * 查询状态
     * @param request
     * @param accessToken
     */
    public MeiTuanViewStatusResponse getStatus(MeiTuanViewStatusRequest request,String accessToken){
        long timestamp=System.currentTimeMillis()/1000;
        TreeMap<String,String>map=JSONObject.parseObject(JSONObject.toJSONString(request),TreeMap.class);
        map.put("timestamp",timestamp+"");
        map.put("access_token",accessToken);
        map.put("app_id",appId);
        String sig=getSignByIsv(map,request.getMethod(),URL);
        HttpRequest httpRequest=HttpRequest.get(URL+request.getMethod()+"?access_token="+accessToken+"&app_id="+appId+"&sig="+sig+"&timestamp="+timestamp);
        map.remove("timestamp");
        map.remove("access_token");
        map.remove("app_id");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            httpRequest.form(entry.getKey(),entry.getValue());
        }
        String resp=httpRequest.execute().body();
        System.out.println(resp);
        JSONObject response=JSONObject.parseObject(resp);
        if (response.getString("data").indexOf("{")!=0){
            //非对象
            if (!StringUtil.equals(response.getString("data"),"ok")){
                JSONObject error=response.getJSONObject("error");
                throw new BadRequestException("错误代码"+error.getInteger("code")+":"+error.getString("msg"));
            }
        }
        return response.getObject("data",MeiTuanViewStatusResponse.class);
    }

    public void stockUpdate(MeiTuanUpdateStockRequest request,String accessToken){
        long timestamp=System.currentTimeMillis()/1000;
        TreeMap<String,String>map=JSONObject.parseObject(JSONObject.toJSONString(request),TreeMap.class);
        map.put("timestamp",timestamp+"");
        map.put("access_token",accessToken);
        map.put("app_id",appId);
        String sig=getSignByIsv(map,request.getMethod(),URL);
        HttpRequest httpRequest=HttpRequest.post(URL+request.getMethod()+"?access_token="+accessToken+"&app_id="+appId+"&sig="+sig+"&timestamp="+timestamp);
        map.remove("timestamp");
        map.remove("access_token");
        map.remove("app_id");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            httpRequest.form(entry.getKey(),entry.getValue());
        }
        String resp=httpRequest.execute().body();
        System.out.println(resp);
        JSONObject response=JSONObject.parseObject(resp);
        if (!StringUtil.equals(response.getString("data"),"ok")){
            JSONObject error=response.getJSONObject("error");
            throw new BadRequestException("错误代码"+error.getInteger("code")+":"+error.getString("msg"));
        }
    }


    public String getSignByIsv(TreeMap<String,String> map, String path,String url){
        StringBuilder builder=new StringBuilder();
        int i=0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey()+":"+entry.getValue());
            builder.append(entry.getKey()).append("=").append(entry.getValue());
            if (i<(map.size()-1)){
                builder.append("&");
            }
            i++;
        }
        url=url+path;
        url+=("?"+builder+appSecret);
        try {
            return SignUtil.genSig(url);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("签名失败："+e.getMessage());
        }
    }
}
