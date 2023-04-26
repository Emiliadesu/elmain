package me.zhengjie.support.ymatou;

import com.alibaba.fastjson.annotation.JSONField;

public class YmatouOrderStatus {
    @JSONField(name = "order_id")
    private Long orderId;
    @JSONField(name = "order_status")
    private Integer orderStatus;
    @JSONField(name = "last_update_time")
    private String lastUpdateTime;
    @JSONField(name = "return_status")
    private boolean returnStatus;
    @JSONField(name = "order_items_status")
    private YmatouOrderItemStatus[] orderItemsStatus;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public boolean isReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(boolean returnStatus) {
        this.returnStatus = returnStatus;
    }

    public YmatouOrderItemStatus[] getOrderItemsStatus() {
        return orderItemsStatus;
    }

    public void setOrderItemsStatus(YmatouOrderItemStatus[] orderItemsStatus) {
        this.orderItemsStatus = orderItemsStatus;
    }
}
