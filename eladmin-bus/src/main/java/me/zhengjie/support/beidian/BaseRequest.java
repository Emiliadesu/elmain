package me.zhengjie.support.beidian;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

public abstract class BaseRequest implements CommonApiParam {
    /**
     * 应用id
     */
    @JSONField(name = "app_id")
    private String appId;
    /**
     * 会话session
     */
    @JSONField(name = "session")
    private String session;
    /**
     * 时间戳
     */
    @JSONField(name = "timestamp")
    private String timestamp;

    /**
     * 签名
     */
    @JSONField(name = "sign")
    private String sign;
    /**
     * 接口版本号
     */
    @JSONField(name = "version")
    private String version="1.0";

    /**
     * 数据格式，可选xml和json 默认json
     */
    @JSONField(name = "format")
    private String format="json";

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
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

    public String getVersion() {
        return version;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
