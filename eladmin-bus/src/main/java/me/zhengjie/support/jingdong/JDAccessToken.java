package me.zhengjie.support.jingdong;

import com.alibaba.fastjson.annotation.JSONField;

public class JDAccessToken {

    @JSONField(name = "access_token")
    private String accessToken;

    @JSONField(name = "expires_in")
    private Long expiresIn;    //令牌有效时间, 单位秒

    @JSONField(name = "refresh_token")
    private String refreshToken;

    @JSONField(name = "scope")
    private String scope;

    @JSONField(name = "open_id")
    private String openId;

    @JSONField(name = "item_code")
    private String itemCode;  //购买的收费项目编码

    private String msg;

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }
}
