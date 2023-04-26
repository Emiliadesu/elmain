package me.zhengjie.support.aikucun;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.support.CommonApiParam;
import me.zhengjie.support.CommonClient;
import me.zhengjie.support.CommonResponse;
import me.zhengjie.support.aikucun.apiParam.common.AikucunCommonParam;
import me.zhengjie.support.aikucun.response.common.AikucunCommonResponse;
import me.zhengjie.utils.StringUtil;
import org.springframework.context.annotation.Scope;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Scope(value = "prototype")// 框架每次使用bean都new一个实例，保证线程安全
public class AikucunSupport implements CommonClient {
    private String appId;
    private String appSecret;
    private String url = "https://openapi.aikucun.com/route/rest";
    private String method;

    public static Integer translationToDYStatus(int AiKuCunStatusCode) throws Exception{
        int statusCode;
        switch (AiKuCunStatusCode){
            case 60:
            case 70:
                statusCode=2;
                break;
            case 80:
                statusCode=3;
                break;
            case 90:
                statusCode=5;
                break;
            case 100:
                statusCode=21;
                break;
            default:
                throw new Exception("未知异常:"+AiKuCunStatusCode);
        }
        return statusCode;
    }

    public static String translationStatusCode(Integer statusCode) {
        if (statusCode==null)
            throw new BadRequestException("状态码为空");
        switch (statusCode){
            case 60:
                return "待发货";
            case 70:
                return "发货中";
            case 80:
                return "已发货";
            case 90:
                return "订单完成";
            case 100:
                return "订单取消";
            default:
                return "未知的状态"+statusCode;
        }
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public AikucunSupport() {
    }

    /**
     * Non-Implement
     * @param clazz
     * @param <T>
     * @return
     */
    @Override
    public <T> CommonResponse request(Class<T> clazz) {
        return null;
    }

    @Override
    public <T> AikucunCommonResponse request(Class<T> clazz,CommonApiParam apiParam) {
        AikucunCommonParam commonParam = new AikucunCommonParam();
        commonParam.setNonceStr("c9215e2d711b45ea9dbd6db8f8d657b1");
        commonParam.setTimestamp(System.currentTimeMillis()/1000);
        //签名
        String[] sign = getSigns(commonParam,apiParam);
        //String[] sign = AikucunUtils.genSign("1000","114514","c9215e2d711b45ea9dbd6db8f8d657b1","3.0","1598930402","json","delivery.haitao.order.list",null,"{\"endDate\":\"2020-09-01 11:20:02\",\"fields\":\"*\",\"page\":1,\"pageSize\":20,\"startDate\":\"2020-08-31 11:20:02\",\"status\":60}");
        commonParam.setSign(sign[0]);
        for (String s : sign) {
            System.err.println(s);
        }
        String resp = HttpUtil.post(url + "?" + sign[1],sign[2]);
        JSONObject respJson = JSONObject.parseObject(resp);
        AikucunCommonResponse<T> response = JSON.parseObject(resp, AikucunCommonResponse.class);
        response.setData(respJson.getObject("data", clazz));
        System.out.println(resp);
        return response;
    }

    /**
     * Non-Implement
     * @return
     */
    @Override
    public String getSign() {
        return null;
    }

    private String[] getSigns(AikucunCommonParam commonParam,CommonApiParam apiParam) {
        Map<String, Object> signParams = new HashMap<>();
        Map<String, Object> postParams = new HashMap<>();
        Map<String, Object> queryParam = new HashMap<>();
        Map<String, Object> comonsMap = new HashMap<>();
        String jsonStr = "";
        comonsMap.put("appid", appId);
        comonsMap.put("noncestr", commonParam.getNonceStr());
        comonsMap.put("timestamp", commonParam.getTimestamp());
        comonsMap.put("version", commonParam.getVersion());
        comonsMap.put("appsecret", appSecret);
        comonsMap.put("format", commonParam.getFormat());
        comonsMap.put("interfaceName", apiParam.getMethod());
        //全部进计算参数中
        signParams.putAll(comonsMap);
        jsonStr = JSON.toJSONString(apiParam);
        if (StringUtil.equalsIgnoreCase(method, "post")) {
            postParams.put("body", jsonStr);
            signParams.putAll(postParams);
        } else if (StringUtil.equalsIgnoreCase(method, "get")) {
            HashMap<String, Object> map = JSON.parseObject(jsonStr, HashMap.class);
            signParams.putAll(map);
        } else {
            throw new BadRequestException("只能用GET和POST请求");
        }
        //取出所有的key
        Object[] arKey = signParams.keySet().toArray();
        //对所有的key进行按Ascii码升序排
        Arrays.sort(arKey);
        StringBuffer buf = new StringBuffer();
        for (Object key : arKey) {
            if (buf.length() > 0) {
                buf.append("&");
            }
            //对原值进行串接
            buf.append(key).append("=").append(signParams.get(key));
        }
        System.out.println("签名前的内容是:" + buf.toString());
        //生成SHA1签名
        String sign = sha1(buf.toString());
        //剔除appsecret
        comonsMap.remove("appsecret");
        //加入自签名
        queryParam.put("sign", sign);
        //加入全局参数
        queryParam.putAll(comonsMap);
        return new String[]{sign, map2urlencode(queryParam), jsonStr};
    }

    private static String map2urlencode(Map<String, Object> dataMap) {
        StringBuffer buf = new StringBuffer();
        Set<String> namesSet = dataMap.keySet();
        for (String name : namesSet) {
            if (buf.length() > 0) {
                buf.append("&");
            }
            try {
                buf.append(name).append("=").append(URLEncoder.encode(dataMap.get(name).toString(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                throw new BadRequestException("不支持的编码方式");
            }
        }
        return buf.toString();
    }

    private static String sha1(String text) {
        MessageDigest md = null;
        String outStr = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(text.getBytes("UTF-8"));
            outStr = byteToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e1) {
            throw new RuntimeException(e1);
        }
        return outStr;
    }

    private static String byteToString(byte[] digest) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            String tempStr = Integer.toHexString(digest[i] & 0xff);
            if (tempStr.length() == 1) {
                buf.append("0").append(tempStr);
            } else {
                buf.append(tempStr);
            }
        }
        return buf.toString().toLowerCase();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
