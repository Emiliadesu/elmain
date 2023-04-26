package me.zhengjie.support.queenshop;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.utils.StringUtil;

import java.util.List;

public class QSCommonResponse<T> {

    @JSONField(name = "success")
    private Boolean success;

    @JSONField(name = "code")
    private Integer code;

    @JSONField(name = "message")
    private String message;

    @JSONField(name = "nowTime")
    private Integer nowTime;

    @JSONField(name = "result")
    private T result;

    @JSONField(serialize = false)
    private List<T> dataArray;


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

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getNowTime() {
        return nowTime;
    }

    public void setNowTime(Integer nowTime) {
        this.nowTime = nowTime;
    }

    @Override
    public String toString() {
        return "QSCommonResponse{" +
                "success=" + success +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", nowTime=" + nowTime +
                ", result=" + result +
                '}';
    }
    public List<T> getDataArray() {
        return dataArray;
    }
}
