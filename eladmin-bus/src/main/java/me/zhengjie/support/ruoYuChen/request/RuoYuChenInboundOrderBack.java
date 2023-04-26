package me.zhengjie.support.ruoYuChen.request;

import java.util.List;

public class RuoYuChenInboundOrderBack extends RuoYuChenRequestAbs {
    /**
     * 仓库的单号
     * Y
     */
    private String entryOrderId;
    /**
     * 入库单号
     * Y
     */
    private String entryOrderCode;
    /**
     * 采购单号(当orderType=CGRK时使用)
     * N
     */
    private String purchaseOrderCode;
    /**
     * 入库单类型
     * Y
     */
    private String entryOrderType;
    /**
     * 单据类型
     * N
     */
    private String orderSubType;
    /**
     * 外部业务编码(用于消息去重)
     * N
     */
    private String outBizCode;
    /**
     * 入库单状态
     * Y
     */
    private String status;
    /**
     * 操作时间(yyyy-MM-dd HH:mm:ss)
     * Y
     */
    private String operateTime;
    /**
     * 店铺名称
     * N
     */
    private String shopNick;
    /**
     * 店铺编码
     * N
     */
    private String shopCode;
    /**
     * 回传模式 0：标准模式、1：合并模式；
     * 默认为：0标准模式
     * N
     */
    private String modeType;
    /**
     * 备注
     * N
     */
    private String remark;
    /**
     * 订单信息
     */
    private List<RuoYuChenInboundOrderBackDetail> orderLines;

    public String getEntryOrderId() {
        return entryOrderId;
    }

    public void setEntryOrderId(String entryOrderId) {
        this.entryOrderId = entryOrderId;
    }

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

    public String getEntryOrderType() {
        return entryOrderType;
    }

    public void setEntryOrderType(String entryOrderType) {
        this.entryOrderType = entryOrderType;
    }

    public String getOrderSubType() {
        return orderSubType;
    }

    public void setOrderSubType(String orderSubType) {
        this.orderSubType = orderSubType;
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

    public String getShopNick() {
        return shopNick;
    }

    public void setShopNick(String shopNick) {
        this.shopNick = shopNick;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getModeType() {
        return modeType;
    }

    public void setModeType(String modeType) {
        this.modeType = modeType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<RuoYuChenInboundOrderBackDetail> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<RuoYuChenInboundOrderBackDetail> orderLines) {
        this.orderLines = orderLines;
    }

    @Override
    public String getMethod() {
        return "EntryorderBack";
    }
}
