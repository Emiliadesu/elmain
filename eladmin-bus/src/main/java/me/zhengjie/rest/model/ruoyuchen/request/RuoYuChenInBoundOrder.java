package me.zhengjie.rest.model.ruoyuchen.request;

import java.util.List;

public class RuoYuChenInBoundOrder {
    /**
     * 货主编码
     */
    private String ownerCode;
    /**
     * 入库单号
     */
    private String entryOrderCode;
    /**
     * 采购单号
     */
    private String purchaseOrderCode;
    /**
     * 订单创建时间(YYYY-MM-dd HH:mm:ss)
     */
    private String orderCreateTime;
    /**
     * 业务类型
     * (SCRK=生产入库;LYRK=领用入库; CCRK=残次品入库;CGRK=采购入库;
     * DBRK=调拨入库;QTRK=其他入库;
     * B2BRK=B2B入 库;XNRK=虚拟入库;
     * PYRK=盘盈入库
     * 只传英文编码)
     */
    private String orderType;
    /**
     * 业务子类型
     * (ZLBG=质量变更;XQTZ=效期调整; KCPD=库存盘点;QTLX=其他类型;
     * 只传英文编码)
     */
    private String orderSubType;
    /**
     * 单据类型
     * (ZC=正常;NB=内部;QT=其他。
     * 只传英文编码)
     */
    private String billType;
    /**
     * 物流公司编码
     * (SF=顺丰、EMS=标准快递、
     * EYB=经济快件、ZJS=宅急送、
     * YTO=圆通、ZTO=中通(ZTO)、
     * HTKY=百世汇通、 UC=优速、
     * STO=申通、TTKDEX=天天快递、
     * QFKD=全峰、FAST=快捷、
     * POSTB=邮政小包、GTO=国通、
     * YUNDA=韵达、JD=京东配送、
     * DD=当当宅配、 AMAZON=亚马逊物流、
     * OTHER=其他;只传英文编码)
     */
    private String logisticsCode;
    /**
     * 物流公司名称
     */
    private String logisticsName;
    /**
     * 运单号
     */
    private String expressCode;
    /**
     * 供应商编码
     */
    private String supplierCode;
    /**
     * 供应商名称
     */
    private String supplierName;
    /**
     * 来源单号
     */
    private String sourceOrderCode;
    /**
     * 备注
     */
    private String remark;

    /**
     * 订单信息
     */
    private List<RuoYuChenInBoundOrderDetail>orderLines;

    private RuoYuChenInBoundOrderSenderInfo senderInfo;

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
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

    public String getOrderSubType() {
        return orderSubType;
    }

    public void setOrderSubType(String orderSubType) {
        this.orderSubType = orderSubType;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public String getLogisticsCode() {
        return logisticsCode;
    }

    public void setLogisticsCode(String logisticsCode) {
        this.logisticsCode = logisticsCode;
    }

    public String getLogisticsName() {
        return logisticsName;
    }

    public void setLogisticsName(String logisticsName) {
        this.logisticsName = logisticsName;
    }

    public String getExpressCode() {
        return expressCode;
    }

    public void setExpressCode(String expressCode) {
        this.expressCode = expressCode;
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

    public String getSourceOrderCode() {
        return sourceOrderCode;
    }

    public void setSourceOrderCode(String sourceOrderCode) {
        this.sourceOrderCode = sourceOrderCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<RuoYuChenInBoundOrderDetail> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<RuoYuChenInBoundOrderDetail> orderLines) {
        this.orderLines = orderLines;
    }

    public RuoYuChenInBoundOrderSenderInfo getSenderInfo() {
        return senderInfo;
    }

    public void setSenderInfo(RuoYuChenInBoundOrderSenderInfo senderInfo) {
        this.senderInfo = senderInfo;
    }
}
