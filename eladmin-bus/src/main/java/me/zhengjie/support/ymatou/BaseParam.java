package me.zhengjie.support.ymatou;

import com.alibaba.fastjson.annotation.JSONField;

public class BaseParam {
    @JSONField(name = "auth_code")
    private String authCode;
    @JSONField(name = "sign_method")
    private String signMethod="MD5";
    @JSONField(name = "timestamp")
    private String timestamp;
    @JSONField(name = "sign")
    private String sign;
    @JSONField(name = "nonce_str")
    private String nonceStr;
    @JSONField(name = "biz_content")
    private String bizContent;

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getSignMethod() {
        return signMethod;
    }

    public void setSignMethod(String signMethod) {
        this.signMethod = signMethod;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getBizContent() {
        return bizContent;
    }

    public void setBizContent(String bizContent) {
        this.bizContent = bizContent;
    }
}
