package me.zhengjie.support.jingdong;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.domain.ShopToken;

public class JDCommonParam {

    @JSONField(name = "method")
    private String method;

    @JSONField(name = "app_key")
    private String appKey;

    @JSONField(name = "format")
    private String format;

    @JSONField(name = "timestamp")
    private String timestamp;

    @JSONField(name = "v")
    private String v;

    @JSONField(name = "sign")
    private String sign;

    private String js;

    private String json;

    private ShopToken shopToken;

    public ShopToken getShopToken() {
        return shopToken;
    }

    public void setShopToken(ShopToken shopToken) {
        this.shopToken = shopToken;
    }

    public String getJs() {
        return js;
    }

    public void setJs(String js) {
        this.js = js;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

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

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
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
}
