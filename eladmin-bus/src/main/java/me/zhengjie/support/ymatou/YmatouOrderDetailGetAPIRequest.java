package me.zhengjie.support.ymatou;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;


public class YmatouOrderDetailGetAPIRequest implements CommonApiParam {
    @JSONField(name = "order_id")
    private Long orderId;
    @JSONField(name = "needs_delivery_info")
    private Boolean needsDeliveryInfo;

    @Override
    public String getMethod() {
        return "ymatou.order.detail.get";
    }

    @Override
    public String getKeyWord() {
        return String.valueOf(getOrderId());
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Boolean getNeedsDeliveryInfo() {
        return needsDeliveryInfo;
    }

    public void setNeedsDeliveryInfo(Boolean needsDeliveryInfo) {
        this.needsDeliveryInfo = needsDeliveryInfo;
    }
}
