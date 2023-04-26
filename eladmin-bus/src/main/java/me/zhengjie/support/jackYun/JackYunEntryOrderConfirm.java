package me.zhengjie.support.jackYun;

import me.zhengjie.rest.model.jackYun.JackYunBasicRequest;

public class JackYunEntryOrderConfirm extends JackYunBasicRequest {
    private String entryOrderCode;
    private String ownerCode;
    private String purchaseOrderCode;
    private String warehouseCode;
    private String entryOrderId;
    private String entryOrderType;
    private String outBizCode;
    private String status;
    private String operateTime;

    public String getEntryOrderCode() {
        return entryOrderCode;
    }

    public void setEntryOrderCode(String entryOrderCode) {
        this.entryOrderCode = entryOrderCode;
    }

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public String getPurchaseOrderCode() {
        return purchaseOrderCode;
    }

    public void setPurchaseOrderCode(String purchaseOrderCode) {
        this.purchaseOrderCode = purchaseOrderCode;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getEntryOrderId() {
        return entryOrderId;
    }

    public void setEntryOrderId(String entryOrderId) {
        this.entryOrderId = entryOrderId;
    }

    public String getEntryOrderType() {
        return entryOrderType;
    }

    public void setEntryOrderType(String entryOrderType) {
        this.entryOrderType = entryOrderType;
    }

    public String getOutBizCode() {
        return outBizCode;
    }

    public void setOutBizCode(String outBizCode) {
        this.outBizCode = outBizCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }
}
