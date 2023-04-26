package me.zhengjie.support.meituan;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

public class MeiTuanOrderCancelRequest implements CommonApiParam {
    /**
     * 订单id
     */
    @JSONField(name = "order_id")
    private String orderId;

    /**
     * 取消原因
     */
    private String reason;

    /**
     * 取消原因代码 可选
     */
    @JSONField(name = "reason_code")
    private String reasonCode;

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

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    @Override
    public String getMethod() {
        return "/api/v1/order/cancel";
    }

    @Override
    public String getKeyWord() {
        return getOrderId();
    }
}
