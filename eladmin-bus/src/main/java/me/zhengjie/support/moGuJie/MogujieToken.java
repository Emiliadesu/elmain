package me.zhengjie.support.moGuJie;

import com.alibaba.fastjson.annotation.JSONField;

public class MogujieToken {
    @JSONField(name = "errorMsg")
    private String errorMsg;
    @JSONField(name = "state")
    private String state;
    @JSONField(name = "token_type")
    private String tokenType;
    @JSONField(name = "userId")
    private String userId;
    @JSONField(name = "statusCode")
    private String statusCode;
    @JSONField(name = "access_token")
    private String accessToken;
    @JSONField(name = "access_expires_in")
    private Long accessExpiresIn;
    @JSONField(name = "refresh_token")
    private String refreshToken;
    @JSONField(name = "refresh_expires_in")
    private Long refreshExpiresIn;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
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

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setAccessExpiresIn(Long accessExpiresIn) {
        this.accessExpiresIn = accessExpiresIn;
    }

    public Long getRefreshExpiresIn() {
        return refreshExpiresIn;
    }

    public void setRefreshExpiresIn(Long refreshExpiresIn) {
        this.refreshExpiresIn = refreshExpiresIn;
    }

    public Long getAccessExpiresIn() {
        return accessExpiresIn;
    }
}
