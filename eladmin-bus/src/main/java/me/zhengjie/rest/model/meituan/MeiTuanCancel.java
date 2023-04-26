package me.zhengjie.rest.model.meituan;

import com.alibaba.fastjson.annotation.JSONField;

public class MeiTuanCancel {
    /**
     * 订单号
     */
    @JSONField(name = "order_id")
    private Long orderId;

    /**
     * 订单取消原因Code
     */
    @JSONField(name = "reason_code")
    private Integer reasonCode;

    /**
     * 订单取消的原因
     */
    private String reason;

    /**
     * 订单取消操作人
     * 1-用户
     * 2-商家端
     * 3-客服
     * 4-BD
     * 5-系统
     * 6-开放平台
     */
    @JSONField(name = "deal_op_type")
    private String dealOpType;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(Integer reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDealOpType() {
        return dealOpType;
    }

    public void setDealOpType(String dealOpType) {
        this.dealOpType = dealOpType;
    }
}
