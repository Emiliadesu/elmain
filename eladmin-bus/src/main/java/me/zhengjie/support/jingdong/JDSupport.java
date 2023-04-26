package me.zhengjie.support.jingdong;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.shade.com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.ShopToken;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.utils.Md5Utils;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JDSupport {

    private String v = "2.0";

    private String apiUrl = "https://api.jd.com/routerjson";

    private String appKey="817093B122E9A4A8DC3505ED4364077D";

    private String appSecret="38ab5c4ce7604456af6301564547a527";

    public static void main(String[] args) throws Exception{
        JDSupport support=new JDSupport();
//        ShopToken shopToken=new ShopToken();
        //support.getToken(shopToken)
        //System.out.println(DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss.sssZ"));
        ShopToken shopToken=new ShopToken();
        shopToken.setClientId(support.appKey);
        shopToken.setClientSecret(support.appSecret);
        shopToken.setCode("qxZCvP");
        shopToken.setTokenTime(1637385560439L);
        shopToken.setShopId(9999L);
        shopToken.setAccessToken("effcf787ebb44dc2b83900a7c6b4dd6boddl");
        shopToken.setRefreshToken("d66c9c1229474d2bb4b6d1f624b2de45zljm");
        shopToken.setPlatformShopId("-");
        /*下单取号的测试字段
        String jsonStr="{\"salePlat\":\"0030001\",\"customerCode\":\"021K1421608\",\"orderId\":\"4815144468999899028\",\"thrOrderId\":\"4779501617730733431\",\"senderName\":\"富立物流\",\"senderAddress\":\"浙江省宁波市北仑区保税东区兴业四路二号\",\"senderTel\":\"0666169082\",\"senderMobile\":\"18888888888\",\"senderPostcode\":\"315800\",\"receiveName\":\"郑沈洁\",\"receiveAddress\":\"北京市市辖区大兴区德贤路239号润枫·锦尚7-2-903\",\"province\":\"北京市\",\"city\":\"市辖区\",\"county\":\"大兴区\",\"siteName\":\"无\",\"receiveMobile\":\"13693091932\",\"packageCount\":\"1\",\"weight\":\"0.55\",\"vloumn\":\"110.32\",\"description\":\"芳珂（FANCL） 无添加 高保湿化妆水 30ml\",\"signReturn\":\"1\",\"remark\":\"5554412\",\"idNumber\":\"110108198102172718\",\"freight\":\"0\"}";
        JDLdopWaybillReceiveResponse response=support.requests("360buy_param_json",jsonStr,shopToken, DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss.sssZ"),"jingdong.ldop.waybill.receive");
         */

        /*
        //运单轨迹测试字段
        String jsonStr = "{\"customerCode\":\"021K1421608\",\"waybillCode\":\"7711233424232323\"}";
        JDCommonParam commonParam = new JDCommonParam();
        commonParam.setJs("360buy_param_json");
        commonParam.setJson(jsonStr);
        commonParam.setShopToken(shopToken);
        commonParam.setMethod("jingdong.ldop.receive.trace.get");
        commonParam.setTimestamp( DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss.sssZ"));
        JDLdopReceiveTraceGetResponse response = support.requests(commonParam);
         */
        System.out.println();
    }

    /**
     * 查询物流消息
     */
    public JDLdopReceiveTraceGetResponse requests(JDCommonParam commonParam)throws Exception{
        ShopToken shopToken = commonParam.getShopToken();
        String accessToken = shopToken.getAccessToken();
        String js = commonParam.getJs();
        String json = commonParam.getJson();
        String method = commonParam.getMethod();
        String timestamp = commonParam.getTimestamp();
        String sign = sign(js,json,method,accessToken,timestamp);
        Map<String,Object> map = new HashMap<>();
        map.put(js,json);
        map.put("access_token",accessToken);
        map.put("app_key",appKey);
        map.put("method",method);
        map.put("timestamp",timestamp);
        map.put("v",v);
        map.put("sign",sign);
        String resp = null;
        try {
            HttpRequest request = HttpRequest.get(apiUrl).form(map).contentType(ContentType.FORM_URLENCODED.getValue());
            resp = request.execute().body();
            log.info("京东物流查询物流消息请求:"+request.getUrl());
        } catch (HttpException e) {
            throw new BadRequestException("京东物流查询物流消息请求时失败:"+e.getMessage());
        }
        String str = JSONObject.parseObject(resp).getString("jingdong_ldop_receive_trace_get_responce");
        String res = JSONObject.parseObject(str).getString("querytrace_result");
        log.info("京东物流查询物流消息响应:"+res);
        JDLdopReceiveTraceGetResponse response = JSONObject.parseObject(res,JDLdopReceiveTraceGetResponse.class);
        return response;
    }


    /**
     *下单取号
     */
    public JingdongLdopWaybillReceiveResponce requests(String js, String json, ShopToken shopToken, String timestamp, String method) throws Exception {
        //用shopToken去获取code与accessToken
        //JDAccessToken token = getToken(shopToken);
        String accessToken = shopToken.getAccessToken();
        String sign =sign(js,json,method,accessToken,timestamp);
        Map<String,Object> map=new HashMap<>();
        map.put(js,json);
        map.put("access_token",accessToken);
        map.put("app_key",appKey);
        map.put("method",method);
        map.put("timestamp",timestamp);
        map.put("v",v);
        map.put("sign",sign);
        String resp = null;
        try {
            HttpRequest request=HttpRequest.get(apiUrl).form(map).contentType(ContentType.FORM_URLENCODED.getValue()); //ContentType的form这个用法默认用标准编码(UTF-8)
            resp = request.execute().body();
            log.info("京东物流下单取号请求"+request.getUrl());
            //System.out.println(request.getUrl());
        } catch (Exception e) {
            throw new BadRequestException("京东物流下单取号请求时失败:"+e.getMessage());
        }
//        String str = JSONObject.parseObject(resp).getString("jingdong_ldop_waybill_receive_responce");
//        String res = JSONObject.parseObject(str).getString("receiveorderinfo_result");
        log.info("京东物流下单取号响应:"+resp);
        JingdongLdopWaybillReceiveResponce response = JSONObject.parseObject(resp, JingdongLdopWaybillReceiveResponce.class);
        return response;
    }


    /**
     *运单申报
     */
    public JDEclpOrderAddDeclareCustsResponse request(String js,String json,ShopToken shopToken,String timestamp,String method) throws NoSuchAlgorithmException {
        //用shopToken去获取code与accessToken
        //JDAccessToken token = getToken(shopToken);
        String accessToken = shopToken.getAccessToken();
        String sign =sign(js,json,method,accessToken,timestamp);
        Map<String,Object> map=new HashMap<>();
        map.put(js,json);
        map.put("access_token",accessToken);
        map.put("app_key",appKey);
        map.put("method",method);
        map.put("timestamp",timestamp);
        map.put("v",v);
        map.put("sign",sign);
        String resp = null;
        try {
            HttpRequest request=HttpRequest.get(apiUrl).form(map).contentType(ContentType.FORM_URLENCODED.getValue());
            resp = request.execute().body();
            log.info("京东物流申报请求"+request.getUrl());
        } catch (Exception e) {
            throw new BadRequestException("京东物流申报请求时失败:"+e.getMessage());
        }
//        String str = JSONObject.parseObject(resp).getString("jingdong_eclp_order_addDeclareOrderCustoms_responce");
//        String res = JSONObject.parseObject(str).getString("declaredOrderCustoms_result");
        log.info("京东物流申报响应:"+resp);
        JDEclpOrderAddDeclareCustsResponse response = JSON.parseObject(resp,JDEclpOrderAddDeclareCustsResponse.class);
        return response;
    }

    /**
     *签名
     */
    public String sign(String js,String json,String method,String accessToken,String timestamp) throws NoSuchAlgorithmException {
        String str = appSecret+js+json+"access_token"+accessToken+"app_key"+appKey+"method"+method+"timestamp"+timestamp+"v"+v+appSecret;
        String sign = Md5Utils.md5Hex(str);  //常用md5加密转16进制
        return sign.toUpperCase();  //字母转大写
    }

    /**
     * 获取token
     *
     * @param shopToken
     * @return
     */
    public void getToken(ShopToken shopToken){
        String url = "https://open-oauth.jd.com/oauth2/access_token?app_key="+appKey+"&app_secret="+appSecret
                +"&grant_type=authorization_code&code="+shopToken.getCode();
        String result = HttpUtil.get(url);
        JDAccessToken token = JSONObject.parseObject(result,JDAccessToken.class);
        System.out.println(result);
        if (token != null){
            shopToken.setAccessToken(token.getAccessToken());
            shopToken.setRefreshToken(token.getRefreshToken());
            shopToken.setRefreshTime(new Timestamp(System.currentTimeMillis()+token.getExpiresIn()*1000));
            shopToken.setTokenTime(System.currentTimeMillis()+token.getExpiresIn()*1000);
        }else{
            throw new BadRequestException(result);
        }
    }

    /**
     * 刷新token
     *
     * @param shopToken
     * @return
     */
    public void refreshToken(ShopToken shopToken){
        String url = "https://open-oauth.jd.com/oauth2/refresh_token?app_key="+appKey+"&app_secret="
                +appSecret+"&grant_type=refresh_token&refresh_token="+shopToken.getRefreshToken();
        String result = HttpUtil.post(url,"");
        JDAccessToken token = JSONObject.parseObject(result,JDAccessToken.class);
        if (token != null){
            shopToken.setAccessToken(token.getAccessToken());
            shopToken.setRefreshToken(token.getRefreshToken());
            shopToken.setTokenTime(System.currentTimeMillis()/1000+token.getExpiresIn());
            shopToken.setRefreshTime(new Timestamp(token.getExpiresIn()*1000+System.currentTimeMillis()));
        }else{
            throw new BadRequestException(token.getMsg());
        }
    }




    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
}
