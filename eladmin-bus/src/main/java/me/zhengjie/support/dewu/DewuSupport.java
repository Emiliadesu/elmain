package me.zhengjie.support.dewu;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.type.ReqLogType;
import me.zhengjie.support.HttpRequestSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
@Slf4j
public class DewuSupport {

    @Value("${dewu.app-key}")
    private String appkey;

    @Value("${dewu.app-secret}")
    private String appSecret;

    @Value("${dewu.base-url}")
    private String baseUrl;

    public void reqPackInfo(String mailNo, BigDecimal weight) {
        JSONObject boby = new JSONObject();
        boby.put("expressCode", mailNo);
        boby.put("weight", weight);

        String reqUrl = baseUrl + "/api/v1/app/pink/api/weight/packageWeight";
        log.info("得物重量回传开始请求：{}", reqUrl);
        String data = boby.toString();
        log.info("得物参数：{}", data);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String format = "appkey=%s&expressCode=%s&timestamp=%s&weight=%s&key=%s";
        String paramStr =  String.format(format,appkey,mailNo, timestamp,weight, appSecret);
        System.out.println(paramStr);
        String sign = encoderByMd5(paramStr);
        Map<String, List<String>> headers = new HashMap<>();
        List<String> appkeyHeader = new ArrayList<>();
        appkeyHeader.add(appkey);
        List<String> timestampHeader = new ArrayList<>();
        timestampHeader.add(timestamp);
        List<String> signHeader = new ArrayList<>();
        signHeader.add(sign);
        headers.put("appkey", appkeyHeader);
        headers.put("timestamp", timestampHeader);
        headers.put("sign", signHeader);
        log.info("timestamp:{}", timestamp);
        log.info("sign:{}", sign);
//        String res = httpRequestSupport.doPost(ReqLogType.DEWU_PACK_INO, reqUrl, data, headers);
        String res = HttpRequest.post(reqUrl)
                .header(headers)
                .body(data).execute()
                .body();
        log.info("得物返回：{}", res);
    }

    public String autoHandover(String mailNo, BigDecimal weight, String boxCode) {
        String repositoryCode = "NB01";
        String deviceNum = "pd";
        JSONObject boby = new JSONObject();
        boby.put("expressCode", mailNo);
        boby.put("weight", weight);
        boby.put("boxCode", boxCode);
        boby.put("repositoryCode", repositoryCode);
        boby.put("deviceNum", deviceNum);

        String reqUrl = baseUrl + "/api/v1/app/pink/api/weight/autoHandover";
        log.info("得物重量回传开始请求：{}", reqUrl);
        String data = boby.toString();
        log.info("得物参数：{}", data);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String format = "appkey=%s&boxCode=%s&deviceNum=%s&expressCode=%s&repositoryCode=%s&timestamp=%s&weight=%s&key=%s";
        String paramStr =  String.format(format, appkey, boxCode, deviceNum, mailNo, repositoryCode, timestamp,weight, appSecret);
        System.out.println(paramStr);
        String sign = encoderByMd5(paramStr);
        Map<String, List<String>> headers = new HashMap<>();
        List<String> appkeyHeader = new ArrayList<>();
        appkeyHeader.add(appkey);
        List<String> timestampHeader = new ArrayList<>();
        timestampHeader.add(timestamp);
        List<String> signHeader = new ArrayList<>();
        signHeader.add(sign);
        headers.put("appkey", appkeyHeader);
        headers.put("timestamp", timestampHeader);
        headers.put("sign", signHeader);
        log.info("timestamp:{}", timestamp);
        log.info("sign:{}", sign);
        String res = HttpRequest.post(reqUrl)
                .header(headers)
                .body(data).execute()
                .body();
        log.info("得物返回：{}", res);
        return res;
    }


    public static String encoderByMd5(String str){
        if (str == null || str.length() == 0) {
            throw new IllegalArgumentException("String to encript cannot be null or zero length");
        }
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] hash = md.digest();
            for (byte b : hash) {
                if ((0xff & b) < 0x10) {
                    hexString.append("0").append(Integer.toHexString((0xFF & b)));
                } else {
                    hexString.append(Integer.toHexString(0xFF & b));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException", e);
        }
        return hexString.toString();
    }


    public static void main(String[] args) {
//        String str = "appkey=PXDRNkie1CtY1TW9&expressCode=SF1020082244120&timestamp=1619775019680&weight=1000&key=8m92ZmhT0j0uF4KRtZJycP6guZJMm6";
//        String s = encoderByMd5(str);
//        System.out.println(s);

//        String timestamp = String.valueOf(System.currentTimeMillis());
//        System.out.println(timestamp);
        String timestamp = "1663221320702";
        String format = "appkey=%s&expressCode=%s&inBoxes=%s&outBox=%s&timestamp=%s&key=%s";
        String appKey = "PXDRNkie1CtY1TW9";
        String appSecret = "8m92ZmhT0j0uF4KRtZJycP6guZJMm6"; //测试服的数据appSecret
        String expressCode = "SF9999999999";
//        String inBoxes = "[{\"code\":\"QZ020000\",\"name\":\"包装推荐名内包材气柱袋大-内包材-气柱袋（大）\"}]";
        String inBoxes = null;
//        String outbox = "{ \"code\": \"0001\", \"name\": \"纸箱\" }";
        String outbox = "{\"code\": \"0001\",\"name\": \"纸箱\"}";
        String paramStr =  String.format(format, appKey, expressCode, inBoxes, outbox, timestamp, appSecret);
        String sign = encoderByMd5(paramStr);
        System.out.println(sign);
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
