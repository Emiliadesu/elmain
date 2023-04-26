package me.zhengjie.support.pdd;

import com.alibaba.fastjson.annotation.JSONField;

public class PddGetOrderStatusResponse{
    /**
     * 要查询状态的订单号，多个订单号请用英文逗号隔开
     */
    @JSONField(name = "orderSn")
    private String orderSn;
    @JSONField(name = "order_status")
    private Integer orderStatus;
    @JSONField(name = "refund_status")
    private Integer refundStatus;

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(Integer refundStatus) {
        this.refundStatus = refundStatus;
    }
}
