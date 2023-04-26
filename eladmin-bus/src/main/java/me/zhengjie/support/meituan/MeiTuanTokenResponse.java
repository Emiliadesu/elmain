package me.zhengjie.support.meituan;

import com.alibaba.fastjson.annotation.JSONField;

public class MeiTuanTokenResponse {

    private Integer status;
    @JSONField(name = "access_token")
    private String accessToken;
    @JSONField(name = "expires_in")
    private Long expireIn;
    @JSONField(name = "refresh_token")
    private String refreshToken;
    @JSONField(name = "re_expires_in")
    private Long reExpireIn;
    private String state;
    private String message;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

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

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getReExpireIn() {
        return reExpireIn;
    }

    public void setReExpireIn(Long reExpireIn) {
        this.reExpireIn = reExpireIn;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MeiTuanTokenResponse{" +
                "status=" + status +
                ", accessToken='" + accessToken + '\'' +
                ", expireIn=" + expireIn +
                ", refreshToken='" + refreshToken + '\'' +
                ", reExpireIn=" + reExpireIn +
                ", state='" + state + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
