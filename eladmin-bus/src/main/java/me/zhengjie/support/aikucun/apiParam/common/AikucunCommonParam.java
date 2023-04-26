package me.zhengjie.support.aikucun.apiParam.common;

import com.alibaba.fastjson.annotation.JSONField;

public class AikucunCommonParam {
    @JSONField(name = "noncestr")
    private String nonceStr;

    @JSONField(name = "timestamp")
    private Long timestamp;

    @JSONField(name = "version")
    private String version = "3.0";

    @JSONField(name = "format")
    private String format = "json";

    @JSONField(name = "sign")
    private String sign;

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
