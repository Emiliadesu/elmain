package me.zhengjie.support.meituan;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

public class MeiTuanViewStatusRequest implements CommonApiParam {
    @Override
    public String getMethod() {
        return "/api/v1/order/viewstatus";
    }

    @Override
    public String getKeyWord() {
        return getOrderId();
    }

    @JSONField(name = "order_id")
    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
