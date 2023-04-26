package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

public class CommonTokenRefreshRequest implements CommonApiParam {
    @Override
    public String getMethod() {
        return "token.refresh";
    }

    @Override
    public String getKeyWord() {
        return this.getRefreshToken();
    }

    @JSONField(name = "grant_type")
    private String grantType = "refresh_token";

    @JSONField(name = "refresh_token")
    private String refreshToken;

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        return "CommonTokenRefreshRequest{" +
                "grantType='" + grantType + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}
