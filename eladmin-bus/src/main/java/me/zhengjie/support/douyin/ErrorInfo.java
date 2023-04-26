package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;

public class ErrorInfo {

    @JSONField(name = "error_code")
    private String errorCode;

    @JSONField(name = "error_msg")
    private String errorMsg;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "ErrorInfo{" +
                "errorCode='" + errorCode + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }
}
