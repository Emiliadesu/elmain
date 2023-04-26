package me.zhengjie.support.pdd;


import java.util.Date;

public class PddShopToken{

    /**
     * 系统id
     */
    private String id;
    /**
     * 店铺名
     */
    private String shopName;
    /**
     * 店铺id
     */
    private String shopId;
    /**
     * 应用id
     */
    private String clientId;
    /**
     * 应用secret
     */
    private String clientSecret;
    /**
     * 授权码
     */
    private String code;
    /**
     * 令牌
     */
    private String accessToken;
    /**
     * 刷新令牌
     */
    private String refreshToken;
    /**
     * 授权码获取时间
     */
    private Date codeGetTime;
    /**
     * 令牌刷新时间
     */
    private Date refreshTime;
    /**
     * token有效期
     */
    private Long tokenTime;
    /**
     * 电商平台代码
     */
    private String platform;
    private String pubKey;
    private String priKey;
    /**
     * 是否允许拉单操作
     */
    private String pullOrderAble;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Date getCodeGetTime() {
        return codeGetTime;
    }

    public void setCodeGetTime(Date codeGetTime) {
        this.codeGetTime = codeGetTime;
    }

    public Date getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(Date refreshTime) {
        this.refreshTime = refreshTime;
    }

    public Long getTokenTime() {
        return tokenTime;
    }

    public void setTokenTime(Long tokenTime) {
        this.tokenTime = tokenTime;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public String getPriKey() {
        return priKey;
    }

    public void setPriKey(String priKey) {
        this.priKey = priKey;
    }

    public String getPullOrderAble() {
        return pullOrderAble;
    }

    public void setPullOrderAble(String pullOrderAble) {
        this.pullOrderAble = pullOrderAble;
    }
}
