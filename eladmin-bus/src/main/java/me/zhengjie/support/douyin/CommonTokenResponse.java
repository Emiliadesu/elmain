package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;

public class CommonTokenResponse {

    @JSONField(name = "access_token")
    private String accessToken;
    @JSONField(name = "expires_in")
    private Long expireIn;
    private String scope;
    @JSONField(name = "shop_id")
    private String shopId;
    @JSONField(name = "shop_name")
    private String shopName;
    @JSONField(name = "refresh_token")
    private String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getExpireIn() {
        return expireIn;
    }

    public void setExpireIn(Long expireIn) {
        this.expireIn = expireIn;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }


    @Override
    public String toString() {
        return "CommonTokenResponse{" +
                "accessToken='" + accessToken + '\'' +
                ", expireIn=" + expireIn +
                ", scope='" + scope + '\'' +
                ", shopId='" + shopId + '\'' +
                ", shopName='" + shopName + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}
