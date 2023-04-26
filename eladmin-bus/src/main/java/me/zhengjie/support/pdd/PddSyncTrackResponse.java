package me.zhengjie.support.pdd;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

import me.zhengjie.support.pdd.PddSyncTrackRequest.TrackBody;

public class PddSyncTrackResponse{

    @JSONField(name = "order_declare")
    private TrackBody orderDeclare;

    private Integer code;

    private Date requestTime;

    private String isSuccess;

    private String msg;

    public TrackBody getOrderDeclare() {
        return orderDeclare;
    }

    public void setOrderDeclare(TrackBody orderDeclare) {
        this.orderDeclare = orderDeclare;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public String getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
