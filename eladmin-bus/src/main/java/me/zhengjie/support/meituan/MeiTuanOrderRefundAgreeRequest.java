package me.zhengjie.support.meituan;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

public class MeiTuanOrderRefundAgreeRequest implements CommonApiParam {
    /**
     * 订单id
     */
    @JSONField(name = "order_id")
    private String orderId;

    /**
     * 取消原因
     */
    private String reason;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String getMethod() {
        return "/api/v1/order/refund/agree";
    }

    @Override
    public String getKeyWord() {
        return getOrderId();
    }
}
