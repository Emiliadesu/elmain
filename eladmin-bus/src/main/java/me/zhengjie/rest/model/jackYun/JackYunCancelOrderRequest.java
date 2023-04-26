package me.zhengjie.rest.model.jackYun;

public class JackYunCancelOrderRequest extends JackYunBasicRequest{
    /**
     * 单据编码
     */
    private String orderCode;
    /**
     * 仓储单号
     */
    private String orderId;
    /**
     * 订单类型
     */
    private String orderType;
    /**
     * 取消原因
     */
    private String cancelReason;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }
}
