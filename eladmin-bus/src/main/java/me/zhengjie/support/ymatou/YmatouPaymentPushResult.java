package me.zhengjie.support.ymatou;

import com.alibaba.fastjson.annotation.JSONField;

public class YmatouPaymentPushResult {
    @JSONField(name = "order_id")
    private Long orderId;
    @JSONField(name = "declare_no")
    private String declareNo;
    @JSONField(name = "order_no")
    private String orderNo;
    @JSONField(name = "pay_transaction_id")
    private String payTransactionId;
    @JSONField(name = "exec_success")
    private Boolean execSuccess;
    @JSONField(name = "identity_check")
    private Boolean identityCheck;
    private String msg;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getDeclareNo() {
        return declareNo;
    }

    public void setDeclareNo(String declareNo) {
        this.declareNo = declareNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPayTransactionId() {
        return payTransactionId;
    }

    public void setPayTransactionId(String payTransactionId) {
        this.payTransactionId = payTransactionId;
    }

    public Boolean getExecSuccess() {
        return execSuccess;
    }

    public void setExecSuccess(Boolean execSuccess) {
        this.execSuccess = execSuccess;
    }

    public Boolean getIdentityCheck() {
        return identityCheck;
    }

    public void setIdentityCheck(Boolean identityCheck) {
        this.identityCheck = identityCheck;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
