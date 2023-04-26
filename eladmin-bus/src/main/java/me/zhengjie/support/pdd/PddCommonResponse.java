package me.zhengjie.support.pdd;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class PddCommonResponse<T> {
    private Integer code;

    private boolean success;

    private String msg;

    private String errStatckTrack;

    public String getErrStatckTrack() {
        return errStatckTrack;
    }

    public void setErrStatckTrack(String errStatckTrack) {
        this.errStatckTrack = errStatckTrack;
    }

    private T data;

    @JSONField(serialize = false)
    private List<T> dataArray;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
