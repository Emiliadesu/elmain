package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.domain.ShopToken;
import me.zhengjie.support.CommonApiParam;
import me.zhengjie.utils.StringUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class CommonTokenCreateRequest implements CommonApiParam {
    @Override
    public String getMethod() {
        return "token.create";
    }

    @Override
    public String getKeyWord() {
        return this.getCode();
    }

    @JSONField(name = "code")
    private String code;

    @JSONField(name = "grant_type")
    private String grantType = "authorization_code";

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    @Override
    public String toString() {
        return "CommonTokenRequest{" +
                "code='" + code + '\'' +
                ", grantType='" + grantType + '\'' +
                '}';
    }
}
