package me.zhengjie.support.jackYun;

import java.util.List;

public class JackYunDeliverOrder {
    /**
     * 出库单号
     * T
     */
    private String deliveryOrderCode;
    /**
     * 仓储系统的出库单号
     * F
     */
    private String deliveryOrderId;
    /**
     * 仓库编码
     * T
     */
    private String warehouseCode;
    /**
     * 出库单类型
     * T
     */
    private String orderType;
    /**
     * 状态
     * F
     */
    private String status;
    /**
     * 外部消息编码
     * F
     */
    private String outBizCode;
    /**
     * 出库单出库最终状态
     * F
     */
    private String confirmType;
    /**
     * 订单完成时间
     * F
     */
    private String orderConfirmTime;
    /**
     * 当前操作员编码
     * F
     */
    private String operatorCode;
    /**
     * 操作员姓名
     * F
     */
    private String operatorName;
    /**
     * 操作时间
     * F
     */
    private String operateTime;
    /**
     *仓储费
     * F
     */
    private String storageFee;
    /**
     * 发票信息
     */
    private List<JackYunDeliverInvoice> invoices;

    public String getDeliveryOrderCode() {
        return deliveryOrderCode;
    }

    public void setDeliveryOrderCode(String deliveryOrderCode) {
        this.deliveryOrderCode = deliveryOrderCode;
    }

    public String getDeliveryOrderId() {
        return deliveryOrderId;
    }

    public void setDeliveryOrderId(String deliveryOrderId) {
        this.deliveryOrderId = deliveryOrderId;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOutBizCode() {
        return outBizCode;
    }

    public void setOutBizCode(String outBizCode) {
        this.outBizCode = outBizCode;
    }

    public String getConfirmType() {
        return confirmType;
    }

    public void setConfirmType(String confirmType) {
        this.confirmType = confirmType;
    }

    public String getOrderConfirmTime() {
        return orderConfirmTime;
    }

    public void setOrderConfirmTime(String orderConfirmTime) {
        this.orderConfirmTime = orderConfirmTime;
    }

    public String getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public String getStorageFee() {
        return storageFee;
    }

    public void setStorageFee(String storageFee) {
        this.storageFee = storageFee;
    }

    public List<JackYunDeliverInvoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<JackYunDeliverInvoice> invoices) {
        this.invoices = invoices;
    }
}
