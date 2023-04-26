package me.zhengjie.support.jackYun;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.XML;
import com.alibaba.fastjson.JSONObject;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.support.CommonApiParam;
import me.zhengjie.utils.DateUtils;
import me.zhengjie.utils.JSONUtils;
import me.zhengjie.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class JackYunSupport {
    @Value("${jackYun.appId}")
    private String appId;

    @Value("${jackYun.secret}")
    private String secret;

    @Value("${jackYun.url}")
    private String url;

    public static void main(String[] args) {

    }
    public String getSign(String method,String content,String customerid,String timestamp){
        //签名参数集合
        SortedMap<String, String> sortedMap = new TreeMap<>();
        sortedMap.put("method", method);
        sortedMap.put("format","json");
        sortedMap.put("content", content);
        sortedMap.put("timestamp", StringUtil.isBlank(timestamp)?DateUtils.now():timestamp);
        sortedMap.put("customerid", customerid);
        //构建待签名的字符串
        StringBuilder sbSignData = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
            sbSignData.append(entry.getKey()).append(entry.getValue());
        }
        // 生成签名 md5 后转大写
        try {
            return encrypt(sbSignData.toString(),"UTF-8") . toUpperCase ();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public String encrypt(String text, String encoding) throws Exception {
        if (encoding == null || encoding.isEmpty()) {
            encoding = "UTF-8";
        }
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[]
                resultByte = text.getBytes(encoding);
        byte[]
                md5Bytes = md5.digest(resultByte);
        StringBuilder hexValue = new StringBuilder();
        for (byte md5Byte : md5Bytes) {
            int val = (md5Byte) & 0xff;
            if (val< 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
    public JackYunBasicResponse confirmUp(JackYunEntryOrderConfirmRequest request){
        String content=JSONObject.toJSONString(request);
        String urlParam;
        try {
            urlParam=url+
                    "?method="+URLEncoder.encode(request.getMethod(), StandardCharsets.UTF_8.toString())+
                    "&format=json" +
                    "&timestamp="+URLEncoder.encode(DateUtils.now(), StandardCharsets.UTF_8.toString())+
                    "&customerid="+URLEncoder.encode(request.getEntryOrder().getOwnerCode(), StandardCharsets.UTF_8.toString())+
                    "&sign="+URLEncoder.encode(getSign(request.getMethod(),content,request.getEntryOrder().getOwnerCode(),null), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new BadRequestException("编码异常");
        }
        String resp=HttpRequest.post(urlParam).body(content).contentType(ContentType.JSON.getValue()).execute().body();
        if (JSONUtils.isXml(resp)){
           return XML.toJSONObject(resp).toBean(JackYunBasicResponse.class);
        }
        return JSONObject.parseObject(resp,JackYunBasicResponse.class);
    }

    public JackYunBasicResponse confirmOut(JackYunDeliverRequest request,String customerId){
        String content=JSONObject.toJSONString(request);
        String urlParam;
        try {
            urlParam=url+
                    "?method="+URLEncoder.encode(request.getMethod(), StandardCharsets.UTF_8.toString())+
                    "&format=json" +
                    "&timestamp="+URLEncoder.encode(DateUtils.now(), StandardCharsets.UTF_8.toString())+
                    "&customerid="+URLEncoder.encode(customerId, StandardCharsets.UTF_8.toString())+
                    "&sign="+URLEncoder.encode(getSign(request.getMethod(),content,customerId,null), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new BadRequestException("编码异常");
        }
        String resp=HttpRequest.post(urlParam).body(content).contentType(ContentType.JSON.getValue()).execute().body();
        if (JSONUtils.isXml(resp)){
            return XML.toJSONObject(resp).toBean(JackYunBasicResponse.class);
        }
        return JSONObject.parseObject(resp,JackYunBasicResponse.class);
    }


    public JackYunBasicResponse deliver(JackYunDeliverRequest request,String customerId){
        String content=JSONObject.toJSONString(request);
        String urlParam;
        try {
            urlParam=url+
                    "?method="+URLEncoder.encode(request.getMethod(), StandardCharsets.UTF_8.toString())+
                    "&format=json" +
                    "&timestamp="+URLEncoder.encode(DateUtils.now(), StandardCharsets.UTF_8.toString())+
                    "&customerid="+URLEncoder.encode(customerId, StandardCharsets.UTF_8.toString())+
                    "&sign="+URLEncoder.encode(getSign(request.getMethod(),content,customerId,null), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new BadRequestException("编码异常");
        }
        String resp=HttpRequest.post(urlParam).body(content).contentType(ContentType.JSON.getValue()).execute().body();
        if (JSONUtils.isXml(resp)){
            return XML.toJSONObject(resp).toBean(JackYunBasicResponse.class);
        }
        return JSONObject.parseObject(resp,JackYunBasicResponse.class);
    }
}
