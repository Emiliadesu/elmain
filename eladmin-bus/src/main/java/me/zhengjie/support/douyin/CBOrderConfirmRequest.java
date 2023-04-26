package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

public class CBOrderConfirmRequest implements CommonApiParam {
    @Override
    public String getMethod() {
        return "crossBorder.orderConfirm";
    }

    @Override
    public String getKeyWord() {
        return this.getOrderId();
    }

    /**
     * 订单id
     */
    @JSONField(name = "order_id")
    private String orderId;

    @JSONField(name = "vendor")
    private String vendor;

    @JSONField(name = "status")
    private String status;

    @JSONField(name = "occurrence_time")
    private String occurrenceTime;

    @JSONField(name = "error_info")
    private ErrorInfo error_info;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ErrorInfo getError_info() {
        return error_info;
    }

    public void setError_info(ErrorInfo error_info) {
        this.error_info = error_info;
    }

    public String getOccurrenceTime() {
        return occurrenceTime;
    }

    public void setOccurrenceTime(String occurrenceTime) {
        this.occurrenceTime = occurrenceTime;
    }

    @Override
    public String toString() {
        return "CBOrderConfirmRequest{" +
                "orderId='" + orderId + '\'' +
                ", vendor='" + vendor + '\'' +
                ", status='" + status + '\'' +
                ", occurrenceTime='" + occurrenceTime + '\'' +
                ", error_info=" + error_info +
                '}';
    }
}
