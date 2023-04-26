package me.zhengjie.support.queenshop;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

public class QSOrderClearErrRequest implements CommonApiParam {

    @Override
    public String getMethod() {
        return "qs.pop.trade.declarefailure";
    }

    @Override
    public String getKeyWord() {
        return getOrderNo();
    }

    @JSONField(name = "orderNo")
    private String orderNo;

    @JSONField(name = "reason")
    private String reason;

    @JSONField(name = "notifyTime")
    private String notifyTime;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(String notifyTime) {
        this.notifyTime = notifyTime;
    }

    @Override
    public String toString() {
        return "QSOrderClearErrRequest{" +
                "orderNo='" + orderNo + '\'' +
                ", reason='" + reason + '\'' +
                ", notifyTime='" + notifyTime + '\'' +
                '}';
    }
}
