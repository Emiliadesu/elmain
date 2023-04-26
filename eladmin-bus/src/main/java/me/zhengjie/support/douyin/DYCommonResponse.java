package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.StringUtils;

import java.util.List;

public class DYCommonResponse<T> {

    @JSONField(name = "message")
    private String message;

    @JSONField(name = "err_no")
    private String statusCode;

    @JSONField(name = "code")
    private Integer code;

    @JSONField(name = "data")
    private T data;

    @JSONField(serialize = false)
    private List<T> dataArray;

    @JSONField(name = "log_id")
    private String logId;

    @JSONField(name = "msg")
    private String msg;

    @JSONField(name = "sub_msg")
    private String subMsg;

    public String getMessage() {
        return this.subMsg;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatusCode() {
        if (StringUtils.isNotBlank(this.statusCode))
            return statusCode;
        if (this.code.intValue() == 10000)
            return "0";
        return "-1";
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<T> getDataArray() {
        return dataArray;
    }

    public void setDataArray(List<T> dataArray) {
        this.dataArray = dataArray;
    }

    public boolean isSuccess() {
        return this.code.intValue() == 10000;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getSubMsg() {
        return subMsg;
    }

    public void setSubMsg(String subMsg) {
        this.subMsg = subMsg;
    }

    @Override
    public String toString() {
        return "DYCommonResponse{" +
                "message='" + message + '\'' +
                ", statusCode='" + statusCode + '\'' +
                ", data=" + data +
                ", dataArray=" + dataArray +
                ", logId='" + logId + '\'' +
                '}';
    }
}
