package me.zhengjie.rest.model.jackYun;

import java.util.List;

public class JackYunEntryOrder extends JackYunBasicRequest{
    private String entryOrderCode;
    private String purchaseOrderCode;
    private String orderCreateTime;
    private String orderType;
    private List<JackYunRelatedOrder>relatedOrders;
    private String expectStartTime;
    private String expectEndTime;
    private String supplierCode;
    private String supplierName;

    public String getEntryOrderCode() {
        return entryOrderCode;
    }

    public void setEntryOrderCode(String entryOrderCode) {
        this.entryOrderCode = entryOrderCode;
    }

    public String getPurchaseOrderCode() {
        return purchaseOrderCode;
    }

    public void setPurchaseOrderCode(String purchaseOrderCode) {
        this.purchaseOrderCode = purchaseOrderCode;
    }

    public String getOrderCreateTime() {
        return orderCreateTime;
    }

    public void setOrderCreateTime(String orderCreateTime) {
        this.orderCreateTime = orderCreateTime;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public List<JackYunRelatedOrder> getRelatedOrders() {
        return relatedOrders;
    }

    public void setRelatedOrders(List<JackYunRelatedOrder> relatedOrders) {
        this.relatedOrders = relatedOrders;
    }

    public String getExpectStartTime() {
        return expectStartTime;
    }

    public void setExpectStartTime(String expectStartTime) {
        this.expectStartTime = expectStartTime;
    }

    public String getExpectEndTime() {
        return expectEndTime;
    }

    public void setExpectEndTime(String expectEndTime) {
        this.expectEndTime = expectEndTime;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
}
