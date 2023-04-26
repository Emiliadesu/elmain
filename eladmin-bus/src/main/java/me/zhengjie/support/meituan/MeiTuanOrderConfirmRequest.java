package me.zhengjie.support.meituan;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

public class MeiTuanOrderConfirmRequest implements CommonApiParam {
    /**
     * 订单id
     */
    @JSONField(name = "order_id")
    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public String getMethod() {
        return "/api/v1/order/confirm";
    }

    @Override
    public String getKeyWord() {
        return getOrderId();
    }
}
