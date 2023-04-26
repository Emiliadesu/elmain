package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

public class CBReturnOrderStatusRequest implements CommonApiParam {

    @Override
    public String getMethod() {
        return "storage.notifySaleReturnStatus";
    }

    @Override
    public String getKeyWord() {
        return this.getLogisticsNo();
    }

    /**
     * 订单id
     */
    @JSONField(name = "logistics_no")
    private String logisticsNo;

    @JSONField(name = "vendor")
    private String vendor;

    @JSONField(name = "status")
    private String status;

    @JSONField(name = "warehouse_code")
    private String warehouseCode;

    @JSONField(name = "logistics_fulfil_no")
    private String logisticsFulfilNo;

    @JSONField(name = "occurrence_time")
    private String occurrenceTime;

    @JSONField(name = "reason")
    private String reason;


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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getLogisticsNo() {
        return logisticsNo;
    }

    public void setLogisticsNo(String logisticsNo) {
        this.logisticsNo = logisticsNo;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getLogisticsFulfilNo() {
        return logisticsFulfilNo;
    }

    public void setLogisticsFulfilNo(String logisticsFulfilNo) {
        this.logisticsFulfilNo = logisticsFulfilNo;
    }

    @Override
    public String toString() {
        return "CBReturnOrderStatusRequest{" +
                "logistics_no='" + logisticsNo + '\'' +
                ", vendor='" + vendor + '\'' +
                ", status='" + status + '\'' +
                ", occurrenceTime='" + occurrenceTime + '\'' +
                ", reason=" + reason +
                '}';
    }
}
