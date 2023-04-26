package me.zhengjie.support.queenshop;

import com.alibaba.fastjson.annotation.JSONField;

public class QSCommonParam {

    @JSONField(name = "method")
    private String method;
    @JSONField(name = "app_key")
    private String appKey;
    @JSONField(name = "access_token")
    private String accessToken;
    @JSONField(name = "param_json")
    private String paramJson;
    @JSONField(name = "timestamp")
    private String timestamp;
    @JSONField(name = "v")
    private String v;
    @JSONField(name = "sign")
    private String sign;
    @JSONField(name = "sign_method")
    private String signMethod;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getParamJson() {
        return paramJson;
    }

    public void setParamJson(String paramJson) {
        this.paramJson = paramJson;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSignMethod() {
        return signMethod;
    }

    public void setSignMethod(String signMethod) {
        this.signMethod = signMethod;
    }
}
