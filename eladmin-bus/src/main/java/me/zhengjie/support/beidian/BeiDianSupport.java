package me.zhengjie.support.beidian;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.support.EmptyResponse;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
public class BeiDianSupport {
    private String appSecret;
    private BaseRequest apiParam;
    public <T>BeiDianCommonResponse request(Class<T> clazz){
        apiParam.setTimestamp((System.currentTimeMillis()/1000)+"");
        //签名
        Map<String, Object> param = JSONObject.parseObject(JSON.toJSONString(apiParam),Map.class);
        param.put("method",apiParam.getMethod());
        apiParam.setSign(getSign(param));
        param.put("sign",apiParam.getSign());
        //请求
        String result = HttpRequest.post("http://api.open.beibei.com/outer_api/out_gateway/route.html").body(JSON.toJSONString(param)).execute().body();
        System.out.println(result);
        JSONObject respJs= JSONObject.parseObject(result);
        BeiDianCommonResponse<T>response=respJs.toJavaObject(BeiDianCommonResponse.class);
        if (!clazz.equals(EmptyResponse.class)){
            if (response.getData() != null&&respJs.getString("data").indexOf("{")==0) {
                response.setData(respJs.getJSONObject("data").getObject("data",clazz));
            }else if (response.getData() != null&&respJs.getString("data").indexOf("[")==0){
                response.setDataArray(respJs.getJSONArray("data").toJavaList(clazz));
            }else {
                response.setData(respJs.getObject("data",clazz));
            }
        }
        return response;
    }

    private String getSign(Map<String, Object> paramMap) {
        if (!(paramMap instanceof TreeMap)) {
            TreeMap<String, Object> tempMap = new TreeMap<>();
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                tempMap.put(entry.getKey(), entry.getValue());
            }
            paramMap = tempMap;
        }
        StringBuffer strBuf = new StringBuffer(appSecret);
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            strBuf.append(entry.getKey()).append(entry.getValue());
        }
        strBuf.append(appSecret);
        String sign = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(strBuf.toString().getBytes());
            sign = new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sign.toUpperCase();
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public BaseRequest getApiParam() {
        return apiParam;
    }

    public void setApiParam(BaseRequest apiParam) {
        this.apiParam = apiParam;
    }

    public static void main(String[] args) {
        BeiDianSupport support=new BeiDianSupport();
        BeiBeiOuterTradeOrderGetRequest request=new BeiBeiOuterTradeOrderGetRequest();
        request.setEndTime(System.currentTimeMillis()/1000);
        request.setStartTime(System.currentTimeMillis()/1000-24*3600);
        request.setPageNo(1);
        request.setPageSize(100);
        request.setStatus(1);
        request.setTimeRange("pay_time");
        request.setAppId("ixci");
        request.setSession("e8d8197d947177006116387ee7440");
        support.setApiParam(request);
        support.setAppSecret("e8714f77a26d20a09c3408388e3b0e07");
        BeiDianCommonResponse<BeiBeiOuterTradeOrderGetResponse>response=support.request(BeiBeiOuterTradeOrderGetResponse.class);
        System.out.println("1111");
    }
}
