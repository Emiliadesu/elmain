package me.zhengjie.rest.model.jackYun;

import me.zhengjie.support.jackYun.JackYunBasicResponse;

public class JackYunStockOutCreateResponse extends JackYunBasicResponse {
    private String deliveryOrderId;

    public String getDeliveryOrderId() {
        return deliveryOrderId;
    }

    public void setDeliveryOrderId(String deliveryOrderId) {
        this.deliveryOrderId = deliveryOrderId;
    }
}
