package me.zhengjie.support.meituan;

import com.alibaba.fastjson.annotation.JSONField;

public class MeiTuanGetCodeResponse {

    private Integer status;
    private String code;
    private Long state;
    private String message;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getState() {
        return state;
    }

    public void setState(Long state) {
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
        return "MeiTuanGetCodeResponse{" +
                "status=" + status +
                ", code='" + code + '\'' +
                ", state=" + state +
                ", message='" + message + '\'' +
                '}';
    }
}
