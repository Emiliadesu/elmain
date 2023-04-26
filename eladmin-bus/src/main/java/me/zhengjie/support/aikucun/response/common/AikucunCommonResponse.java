package me.zhengjie.support.aikucun.response.common;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonResponse;

public class AikucunCommonResponse<T> implements CommonResponse {
    @JSONField(name = "success")
    private Boolean success;

    @JSONField(name = "message")
    private String message;

    @JSONField(name = "data")
    private T data;

    @JSONField(name = "code")
    private String code;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean isSuccess() {
        return this.success;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
