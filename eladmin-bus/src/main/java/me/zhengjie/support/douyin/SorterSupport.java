package me.zhengjie.support.douyin;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.support.CommonApiParam;
import me.zhengjie.utils.DateUtils;
import me.zhengjie.utils.SecureUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author luob
 * @description 抖音流水线
 * @date 2023/3/20
 */
@Slf4j
@Service
@Scope(value = "prototype")// 框架每次使用bean都new一个实例，保证线程安全
public class SorterSupport {

    @Value("${douyin.sorter-access-key}")
    public String accessKey;
    @Value("${douyin.sorter-secret-key}")
    public String secretKey;
    @Value("${douyin.sorter-url}")
    private String url;

    private static final  String SORT_NAME = "fl_sort_01";


    /**
     * 注册
     */
    public String register() throws Exception {
        CBOrderSorterRegisterRequest request = new CBOrderSorterRegisterRequest();
        request.setName(SORT_NAME);
        String result = request(request);
        return result;
    }

    /**
     * 登录
     */
    public String login() throws Exception {
        CBOrderSorterLoginRequest request = new CBOrderSorterLoginRequest();
        request.setName(SORT_NAME);
        String result = request(request);
        return result;
    }

    /**
     * 登出
     */
    public String logout() throws Exception {
        CBOrderSorterLogoutRequest request = new CBOrderSorterLogoutRequest();
        request.setName(SORT_NAME);
        String result = request(request);
        return result;
    }

    /**
     * 心跳保活
     */
    public String heartBeat() throws Exception {
        CBOrderSorterHeartBeatRequest request = new CBOrderSorterHeartBeatRequest();
        request.setName(SORT_NAME);
        String result = request(request);
        return result;
    }

    /**
     * 获取分拣格口
     */
    public String getSort(String mailNo, BigDecimal weight) throws Exception {
        login();
        CBOrderSorterGetSortRequest request = new CBOrderSorterGetSortRequest();
        request.setName(SORT_NAME);
        request.setTrackingNO(mailNo);
        request.setWeight(weight.multiply(new BigDecimal(1000000)).longValue()); //千克转毫克
        String result = request(request);
        log.info(mailNo + ": 获取分拣结果返回：" + result);
        JSONObject jsonObject = JSON.parseObject(result);
        String code = jsonObject.getString("data");
        int resultCode = jsonObject.getIntValue("code");
//        if (jsonObject.getIntValue("code") != 0)
//            throw new BadRequestException(jsonObject.getString("msg"));
//        int code = jsonObject.getIntValue("data");
        sortEvent((resultCode == 0 ? 1 : 2) ,mailNo, code);
        return String.valueOf(code);
    }

    /**
     * 分拣结果上报
     */
    public void sortEvent(int resultCode, String mailNo, String code) throws Exception {
        CBOrderSorterEventRequest request = new CBOrderSorterEventRequest();
        request.setDeviceName(SORT_NAME);
        request.setTrackingNO(mailNo);
        request.setReqTime(System.currentTimeMillis());
        request.setRespTime(System.currentTimeMillis());
        request.setStatus(resultCode);
        request.setResult(code);
        String result = requestPost(request);
        log.info(mailNo + ": 分拣结果上报返回：" + result);
    }


    public String request(CommonApiParam apiParam) throws Exception {
        String requestUrl = this.url + apiParam.getMethod();
        String param = getParam(apiParam);
        String sign = getSign(param);

        Map<String,String> headMap=new HashMap<>();
        headMap.put("S-Auth",sign);

        String result = HttpRequest.get(requestUrl + "?" + param).headerMap(headMap, true).execute().body();
        return result;
    }

    public String requestPost(CommonApiParam apiParam) throws Exception {
        String requestUrl = this.url + apiParam.getMethod();
        String param = JSON.toJSONString(apiParam);
        String paramSign = "accessKey=" + this.accessKey + "&paramBody=" + param;
        String sign = getSign(paramSign);

        Map<String,String> headMap=new HashMap<>();
        headMap.put("S-Auth",sign);

        String result = HttpRequest.post(requestUrl + "?accessKey=" + this.accessKey ).body(param).headerMap(headMap, true).execute().body();
        return result;
    }


    public String getParam(CommonApiParam apiParam) {
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(apiParam));
        String param = "";
        for (Map.Entry<String, Object> entry:  jsonObject.entrySet()) {
            param += entry.getKey() + "=" + entry.getValue() + "&";
         }
        param += "accessKey=" + this.accessKey;
        return param;
    }

    public String getSign(String param) throws Exception {
        String sign = SecureUtils.hmacSHA256(param, this.secretKey);
        return sign;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
