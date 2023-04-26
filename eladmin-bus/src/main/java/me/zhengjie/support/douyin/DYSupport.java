package me.zhengjie.support.douyin;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.PropertyPreFilters;
import com.pdd.pop.ext.apache.http.protocol.HTTP;
import com.youzan.cloud.open.security.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.Config;
import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.service.BusinessLogService;
import me.zhengjie.service.ConfigService;
import me.zhengjie.support.CommonApiParam;
import me.zhengjie.support.EmptyResponse;
import me.zhengjie.utils.DateUtils;
import me.zhengjie.utils.JSONUtils;
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.StringUtils;
import me.zhengjie.utils.enums.BusTypeEnum;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 抖音请求支持类
 */
@Slf4j
@Service
@Scope(value = "prototype")// 框架每次使用bean都new一个实例，保证线程安全
public class DYSupport {

    @Value("${douyin.app-key}")
    public String appKey;
    @Value("${douyin.app-secret}")
    public String appSecret;
    @Value("${douyin.url}")
    private String url;
    @Value("${spring.profiles.active}")
    private String active;
    private String format = "yyyy-MM-dd HH:mm:ss";
    private String accessToken;
    private Map<String, String> headerMap;

    //API的参数
    private CommonApiParam apiParam;

    @Autowired
    private BusinessLogService businessLogService;

    @Autowired
    private ConfigService configService;

    // 抖音请求
    public <T> DYCommonResponse request(Class<T> clazz) throws Exception {
        long start = System.currentTimeMillis();
        String method = apiParam.getMethod();
        String timestamp = DateUtils.format(new Date(), format);
        //1.签名
        String sign = getSign(method, timestamp);

        Map<String,String>headMap=new HashMap<>();
        Config config = configService.queryByK("DY_TEST_TOKEN");
        if (StringUtils.equals(this.accessToken, config.getV())){
            //抖音测试的token
            config =configService.queryByK("DY_REQ_HEAD");
            if (config!=null){
                JSONObject obj = JSONObject.parseObject(config.getV());
                for (String key : obj.keySet()) {
                    headMap.put(key,obj.getString(key));
                }
            }
        }
        String resp;
        String param;
        if (StringUtils.equals("crossborder.warehouseInOutboundEvent", method)) {
            param = getPostParam();
            String reqUrl = url + method.replaceAll("\\.", "/") ;
            reqUrl = reqUrl + "?method=" + URLEncoder.encode(method, "UTF-8") +
                    "&app_key=" + URLEncoder.encode(appKey, "UTF-8") +
                    "&timestamp=" + URLEncoder.encode(timestamp, "UTF-8") +
                    "&v=" + URLEncoder.encode("2", "UTF-8") +
                    "&sign=" + URLEncoder.encode(sign, "UTF-8");
            if (StringUtils.isNotEmpty(accessToken)) {
                reqUrl = reqUrl + "&access_token=" + URLEncoder.encode(accessToken, "UTF-8");
            }
            resp = HttpRequest.post(reqUrl).body(param).headerMap(headMap,true).execute().body();
        }else if(StringUtils.equals("crossBorder.notifyWarehouseFeeOrder", method)){
            Map<String,Object>paramMap = getParamMap(method,timestamp,sign);
            paramMap.remove("access_token");//该接口无需授权
            param = getParam(method,timestamp,sign);
            resp = HttpRequest.post(url + method.replaceAll("\\.", "/")).headerMap(headMap,true).form(paramMap).execute().body();
        }else {
            param = getParam(method, timestamp, sign);
            String reqUrl = url + method.replaceAll("\\.", "/") + param;
            resp = HttpRequest.get(reqUrl).headerMap(headMap,true).execute().body();
        }

        // 保存日志
        businessLogService.saveLog(BusTypeEnum.DOUYIN_OUT, method, url, apiParam.getKeyWord(),  param, resp, (System.currentTimeMillis() - start));
        resp = StringUtil.filterEmoji(resp);
        JSONObject respJson = JSONObject.parseObject(resp);
        DYCommonResponse<T> response = JSON.parseObject(resp, DYCommonResponse.class);

        if (!clazz.equals(EmptyResponse.class)) {
            if (response.getData() != null) {
                if (JSONUtils.isObj(respJson, "data")) {
                    response.setData(respJson.getObject("data", clazz));
                } else {
                    List<T> array = JSONArray.parseArray(respJson.getString("data"), clazz);
                    response.setDataArray(array);
                }
            }
        }
        return response;
    }

    private Map<String, Object> getParamMap(String method, String timestamp, String sign) {
        Map<String,Object>paramMap = new HashMap<>();
        String[] excludeProperties = {"access_token", "cust_id", "shop_id"};
        PropertyPreFilters filters = new PropertyPreFilters();
        PropertyPreFilters.MySimplePropertyPreFilter excludefilter = filters.addFilter();
        excludefilter.addExcludes(excludeProperties);
        // 异常返回上层
        paramMap.put("param_json",JSON.toJSONString(this.apiParam, excludefilter));
        paramMap.put("method",method);
        paramMap.put("app_key",this.appKey);
        paramMap.put("timestamp",timestamp);
        paramMap.put("v",2);
        paramMap.put("sign",sign);
        if (StringUtils.isNotEmpty(this.accessToken)) {
            paramMap.put("access_token",this.accessToken);
        }
        return paramMap;
    }

    private String getPostParam() throws Exception {
        String[] excludeProperties = {"access_token", "cust_id", "shop_id"};
        PropertyPreFilters filters = new PropertyPreFilters();
        PropertyPreFilters.MySimplePropertyPreFilter excludefilter = filters.addFilter();
        excludefilter.addExcludes(excludeProperties);
        // 异常返回上层
        String param;
        param =  JSON.toJSONString(this.apiParam, excludefilter);
        return param;
    }


    // 返回请求参数
    private String getParam(String method, String timestamp, String sign) throws Exception {
        String[] excludeProperties = {"access_token", "cust_id", "shop_id"};
        PropertyPreFilters filters = new PropertyPreFilters();
        PropertyPreFilters.MySimplePropertyPreFilter excludefilter = filters.addFilter();
        excludefilter.addExcludes(excludeProperties);
        // 异常返回上层
        String param;
        param =  "?param_json=" + URLEncoder.encode(JSON.toJSONString(this.apiParam, excludefilter), "UTF-8") +
                "&method=" + URLEncoder.encode(method, "UTF-8") +
                "&app_key=" + URLEncoder.encode(appKey, "UTF-8") +
                "&timestamp=" + URLEncoder.encode(timestamp, "UTF-8") +
                "&v=" + URLEncoder.encode("2", "UTF-8") +
                "&sign=" + URLEncoder.encode(sign, "UTF-8");
        if (StringUtils.isNotEmpty(accessToken)) {
            param = param + "&access_token=" + URLEncoder.encode(accessToken, "UTF-8");
        }
        return param;
    }


    // 签名
    public String getSign(String method, String timestamp) {
        String[] excludeProperties = {"access_token", "cust_id", "shop_id"};
        PropertyPreFilters filters = new PropertyPreFilters();
        PropertyPreFilters.MySimplePropertyPreFilter excludefilter = filters.addFilter();
        excludefilter.addExcludes(excludeProperties);
        String str = appSecret + "app_key" + appKey +
                "method" + method +
                "param_json" + JSON.toJSONString(this.apiParam, excludefilter) +
                "timestamp" + timestamp +
                "v" + 2 + appSecret;
        str=str.replaceAll("&","\\\\u0026").replaceAll("<","\\\\u003c")
                .replaceAll(">","\\\\u003e");
        return DigestUtils.md5Hex(str);
    }

    // SPI签名
    public String getSpiSign(Object param, String timestamp) {

        String str = appSecret + "app_key" + appKey +
                "param_json" + JSON.toJSONString(param) +
                "timestamp" + timestamp +
                appSecret;
        str=str.replaceAll("&","\\\\u0026").replaceAll("<","\\\\u003c")
                .replaceAll(">","\\\\u003e");
        return DigestUtils.md5Hex(str);
    }

    public CommonApiParam getApiParam() {
        return apiParam;
    }

    public void setApiParam(CommonApiParam apiParam) {
        this.apiParam = apiParam;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }


    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }
}


