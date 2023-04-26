package me.zhengjie.support.moGuJie;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.domain.ShopToken;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.support.EmptyResponse;
import me.zhengjie.utils.JSONUtils;
import me.zhengjie.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
public class MoGuJieSupport {
    @Value("${moGuJie.appKey}")
    private String appKey;

    @Value("${moGuJie.appSecret}")
    private String appSecret;

    @Value("${moGuJie.url}")
    private String url;
//    private MogujieTradeGetResp request;


    /**
     * 获取token
     *
     * @param code
     * @param shopToken
     * @return
     */
    public void getToken(String code, ShopToken shopToken) {
        String redirectUrl = "http://erp.fl56.net:8000/api/mgj/callback";
        String url = "https://oauth.mogujie.com/token?code=" + code + "&grant_type=authorization_code&app_key=" + appKey + "&app_secret=" + appSecret + "&redirect_uri=" + redirectUrl + "state=" + shopToken.getId();
        String result = HttpUtil.get(url);
        MogujieToken token = JSONObject.parseObject(result, MogujieToken.class);
        if (token!=null){
            if (StringUtil.equals("0000000",token.getStatusCode())) {
                shopToken.setTokenTime(token.getAccessExpiresIn());
                shopToken.setRefreshTime(new Timestamp(token.getRefreshExpiresIn() * 1000));
                shopToken.setCode(code);
                shopToken.setCodeGetTime(new Timestamp(System.currentTimeMillis()));
                shopToken.setAccessToken(token.getAccessToken());
                shopToken.setRefreshToken(token.getRefreshToken());
                return;
            }
            else {
                throw new BadRequestException(token.getErrorMsg());
            }
        }throw new BadRequestException(result);
    }

    /**
     * 刷新token
     *
     * @param shopToken
     * @return
     */
    public void refreshToken(ShopToken shopToken) {
        String url = "https://oauth.mogujie.com/token?app_key=" + appKey + "&app_secret=" + appSecret + "&grant_type=refresh_token&refresh_token=" + shopToken.getRefreshToken() + "&state=" + shopToken.getId();
        String result = HttpUtil.post(url,"");
        MogujieToken token = JSONObject.parseObject(result, MogujieToken.class);
        if (token!=null){
            if (StringUtil.equals("0000000",token.getStatusCode())){
                shopToken.setAccessToken(token.getAccessToken());
                shopToken.setRefreshToken(token.getRefreshToken());
                shopToken.setRefreshTime(new Timestamp(token.getRefreshExpiresIn()*1000));
                shopToken.setTokenTime(token.getAccessExpiresIn());
            }
            else {
                throw new BadRequestException(token.getErrorMsg());
            }
        }
    }
    /**
     * 拉取订单
     *
     * @param token
     * @param request
     * @return
     */
    public MogujieTradeSoldGetResp pullOrder(String token,MogujiePullOrderRequest request) {
        Map<String, Object> paramMap = new TreeMap<>();
        JSONObject param = new JSONObject();
        param.put("startCreated", request.getStartCreated());
        param.put("endCreated", request.getEndCreated());
        if (request.getStartOrderId()!=null&&request.getStartOrderId()!=0L){
            param.put("startOrderId", request.getStartOrderId());
        }else {
            param.put("page", request.getPage());
        }
        param.put("orderBy", request.getOrderBy());
        param.put("pageSize", request.getPageSize());
        if (StringUtil.isNotBlank(request.getOrderStatus())){
            param.put("orderStatus", request.getOrderStatus());
        }
        paramMap.put("app_key", appKey);
        paramMap.put("method", request.getMethod());
        paramMap.put("access_token", token);
        paramMap.put("format", "json");
        paramMap.put("timestamp", System.currentTimeMillis());
        StringBuilder strBuf = new StringBuilder();
        int mapSize = paramMap.size();
        System.out.println("mapSize:" + mapSize);
        paramMap.put("Params", JSONObject.toJSONString(param));
        String sign = getSign(paramMap);
        paramMap.put("sign", sign);
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            try {
                strBuf.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue() + "", "UTF-8"));
                System.out.println(entry.getKey() + "=" + entry.getValue());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                throw new BadRequestException("URL编码异常" + e.getMessage());
            }
            if (mapSize >= 0) {
                strBuf.append("&");
            }
            mapSize--;
        }
        System.out.println("mapSize:" + mapSize);
        System.out.println(sign);
        String result;
        int i=0;
        while (true){
            result=HttpRequest.post(this.url + "?" + strBuf).execute().body();
            if (!StringUtil.contains(result,"Sign错误")){
                break;
            }
            i++;
            if (i>100){
                throw new BadRequestException(result);
            }
        }
        System.out.println(result);
        if (StringUtil.isNotEmpty(result)) {
            return JSONObject.parseObject(result, MogujieTradeSoldGetResp.class);
        }
        throw new BadRequestException(result);
    }

    /**
     * 根据店铺级订单号查询
     *
     * @param token
     * @param shopOrderNo
     * @return
     */
    public MogujieTradeGetResp queryByShopOrderNo(String token, String shopOrderNo) {
        Map<String, Object> paramMap = new TreeMap<>();
        JSONObject param = new JSONObject();
        param.put("shopOrderId", shopOrderNo);
        paramMap.put("openApiOrderDetailReqDto", param);
        paramMap.put("app_key", appKey);
        paramMap.put("method", "xiaodian.trade.get");
        paramMap.put("access_token", token);
        paramMap.put("timestamp", System.currentTimeMillis());
        paramMap.put("format", "json");
        String sign = getSign(paramMap);
        paramMap.put("sign", sign);
        StringBuilder strBuf = new StringBuilder();
        int mapSize = paramMap.size();
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            try {
                strBuf.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue() + "", "UTF-8"));
                System.out.println(entry.getKey() + "=" + entry.getValue());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                throw new BadRequestException("URL编码异常" + e.getMessage());
            }
            if (mapSize >= 0) {
                strBuf.append("&");
            }
            mapSize--;
        }
        String result = HttpRequest.post(this.url + "?" + strBuf).execute().body();
        System.out.println(result);
        return JSONObject.parseObject(result, MogujieTradeGetResp.class);
    }

    /**
     * 蘑菇街发货接口
     *
     * @param token
     * @param request
     * @return
     */
    public MogujieSendResp send(String token, MogujieLogisticsSendRequest request) {
        Map<String, Object> map = new TreeMap<>();
        map.put("app_key", appKey);
        map.put("method", request.getMethod());
        map.put("access_token", token);
        map.put("timestamp", System.currentTimeMillis());
        map.put("format", "json");
        JSONObject param = new JSONObject();
        param.put("shipType", request.getShipType());
        param.put("shopOrderId", request.getShopOrderId());
        param.put("expressCode", request.getExpressCode());
        param.put("expressId", request.getExpressId());
        param.put("needSplit", request.getNeedSplit());
        map.put("openAPIOrderConsignReqDTO", param);
        String sign = getSign(map);
        map.put("sign", sign);
        StringBuilder strBuf = new StringBuilder();
        int mapSize = map.size();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            try {
                strBuf.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue() + "", "UTF-8"));
                System.out.println(entry.getKey() + "=" + entry.getValue());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                throw new BadRequestException("URL编码异常" + e.getMessage());
            }
            if (mapSize >= 0) {
                strBuf.append("&");
            }
            mapSize--;
        }
        String resultText = HttpRequest.get(this.url + "?" + strBuf).execute().body();
        System.out.println(resultText);
        return JSONObject.parseObject(resultText, MogujieSendResp.class);
    }

    public static String translationStatusCode(String status) {
        if (status==null){
            return "null";
        }
        switch (status) {
            case "ORDER_CANCELLED":
                status = "订单取消";
                break;
            case "ORDER_PAID":
                status = "待发货";
                break;
            case "ORDER_SHIPPED":
                status = "已发货";
                break;
            case "ORDER_RECEIVED":
                status = "已收货";
                break;
            case "ORDER_COMPLETED":
                status = "订单完成";
                break;
            case "ORDER_CLOSED":
                status = "已关闭";
                break;
            case "REFUND_REQUESTED":
                status = "退款中";
                break;
            case "REFUND_OR_RETURN_DEAL_AGREED":
                status = "退款协商一致";
                break;
            case "REFUND_COMPLETED":
                status = "退款完成";
                break;
        }
        return status;
    }

    public static Integer translationToDYStatus(String mgjStatusCode) throws Exception{
        int statusCode;
        switch (mgjStatusCode){
            case "ORDER_PAID":
                statusCode=2;
                break;
            case "ORDER_SHIPPED":
                statusCode=3;
                break;
            case "ORDER_RECEIVED":
                statusCode=5;
                break;
            case "REFUND_REQUESTED":
                //throw new BadRequestException("等待商家处理售后");//拼多多部分商家要求同意退款后才取消订单(目前不清楚有哪些商家，先注释掉看看情况)
                statusCode=16;
                break;
            case "REFUND_OR_RETURN_DEAL_AGREED":
                statusCode=17;
                break;
            case "REFUND_COMPLETED":
            case "ORDER_CANCELLED":
                statusCode=21;
                break;
            default:
                throw new Exception("未知异常:"+mgjStatusCode);
        }
        return statusCode;
    }

    private String getSign(Map<String, Object> paramMap) {
        if (!(paramMap instanceof TreeMap)) {
            TreeMap<String, Object> tempMap = new TreeMap<>();
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                tempMap.put(entry.getKey(), entry.getValue());
            }
            paramMap = tempMap;
        }
        StringBuilder strBuf = new StringBuilder(appSecret);
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            strBuf.append(entry.getKey()).append(entry.getValue());
        }
        strBuf.append(appSecret);
        String sign;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(strBuf.toString().getBytes());
            sign = new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new BadRequestException(e.getMessage());
        }
        return sign.toUpperCase();
    }

}
