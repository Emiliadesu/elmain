package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

public class CBOrderCustomClearanceRequest implements CommonApiParam {

    @Override
    public String getMethod() {
        return "crossBorder.orderCustomClearance";
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

    @JSONField(name = "customs_code")
    private String customsCode;

    @JSONField(name = "customs_name")
    private String customsName;

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

    public String getOccurrenceTime() {
        return occurrenceTime;
    }

    public void setOccurrenceTime(String occurrenceTime) {
        this.occurrenceTime = occurrenceTime;
    }

    public String getCustomsCode() {
        return customsCode;
    }

    public void setCustomsCode(String customsCode) {
        this.customsCode = customsCode;
    }

    public String getCustomsName() {
        return customsName;
    }

    public void setCustomsName(String customsName) {
        this.customsName = customsName;
    }

    public ErrorInfo getError_info() {
        return error_info;
    }

    public void setError_info(ErrorInfo error_info) {
        this.error_info = error_info;
    }

    @Override
    public String toString() {
        return "CBOrderCustomClearanceRequest{" +
                "orderId='" + orderId + '\'' +
                ", vendor='" + vendor + '\'' +
                ", status='" + status + '\'' +
                ", occurrenceTime='" + occurrenceTime + '\'' +
                ", customsCode='" + customsCode + '\'' +
                ", customsName='" + customsName + '\'' +
                ", error_info=" + error_info +
                '}';
    }
}
