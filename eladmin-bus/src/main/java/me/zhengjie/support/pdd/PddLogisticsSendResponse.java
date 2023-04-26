package me.zhengjie.support.pdd;

import com.alibaba.fastjson.annotation.JSONField;

public class PddLogisticsSendResponse{

    @JSONField(name = "is_success")
    private Boolean isSuccess;

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }
}
