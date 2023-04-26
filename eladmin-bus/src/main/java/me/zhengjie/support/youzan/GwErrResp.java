package me.zhengjie.support.youzan;

import com.alibaba.fastjson.annotation.JSONField;

public class GwErrResp {
    @JSONField(name = "err_code")
    private Integer errCode;

    @JSONField(name = "err_msg")
    private String errMsg;

    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
