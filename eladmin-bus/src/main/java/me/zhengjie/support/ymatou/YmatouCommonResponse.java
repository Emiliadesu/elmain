package me.zhengjie.support.ymatou;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.utils.StringUtil;

public class YmatouCommonResponse<T>  {
    @JSONField(name = "code")
    private String code;

    @JSONField(name = "message")
    private String message;

    @JSONField(name = "content")
    private T content;

    @JSONField(name = "l_shop_id")
    private Long lShopId;

    @JSONField(name = "cust_id")
    private Long custId;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public boolean isSuccess() {
        return this.code != null && StringUtil.equals(this.code, "0000");
    }

    public Long getLShopId() {
        return lShopId;
    }

    public void setLShopId(Long lShopId) {
        this.lShopId = lShopId;
    }

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
