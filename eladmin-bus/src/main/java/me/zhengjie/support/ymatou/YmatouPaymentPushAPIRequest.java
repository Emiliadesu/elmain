package me.zhengjie.support.ymatou;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

public class YmatouPaymentPushAPIRequest implements CommonApiParam {
    @JSONField(name = "order_id")
    private Long orderId;
    @JSONField(name = "customs")
    private String customs;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getCustoms() {
        return customs;
    }

    public void setCustoms(String customs) {
        this.customs = customs;
    }

    @Override
    public String getMethod() {
        return "ymatou.payment.push";
    }

    @Override
    public String getKeyWord() {
        return String.valueOf(getOrderId());
    }
}
