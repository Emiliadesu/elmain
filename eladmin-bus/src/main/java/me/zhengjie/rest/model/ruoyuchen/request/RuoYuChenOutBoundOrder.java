package me.zhengjie.rest.model.ruoyuchen.request;

import java.util.List;

public class RuoYuChenOutBoundOrder {
    /**
     * 货主编码
     * Y
     */
    private String ownerCode;
    /**
     * 出库单号
     * Y
     */
    private String deliveryOrderCode;
    /**
     * 业务类型 (PTCK=普通出库单;DBCK=调拨出库;
     * B2BCK=B2B出库;QTCK=其他出库; CGTH=采购退货出库单;XNCK=虚拟出库单,
     * LYCK=领用出库;JITCK=唯品出库;
     * PKCK=盘亏出库);
     * Y
     */
    private String orderType;
    /**
     * 业务子类型 (ZLBG=质量变更;XQTZ=效期调整; KCPD=库存盘点;QTLX=其他类型;
     * LPBF=报废出库;只传英文编码)
     * N
     */
    private String orderSubType;
    /**
     *单据类型
     * Y
     * (ZC=正常;NB=内部;QT=其他。
     * 只传英文编码)
     */
    private String billType;
    /**
     * 需求到货日期 yyyy-MM-dd
     * N
     */
    private String askTime;
    /**
     * 出库单创建时间 yyyy-MM-dd HH:mm:ss
     * Y
     */
    private String createTime;
    /**
     * 物流公司编码
     * N
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
     * N
     */
    private String logisticsName;
    /**
     * 供应商编码
     * N
     */
    private String supplierCode;
    /**
     * 供应商名称
     * N
     */
    private String supplierName;
    /**
     * 来源单号
     * N
     */
    private String sourceOrderCode;
    /**
     * 备注
     * N
     */
    private String remark;
    /**
     * 订单信息
     */
    private List<RuoYuChenOutBoundOrderDetail>orderLines;
    /**
     *仓库发货地址
     * N
     */
    private RuoYuChenOutBoundOrderSenderInfo senderInfo;
    /**
     *收货信息 供应商
     */
    private RuoYuChenOutBoundOrderSenderInfo receiverInfo;

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public String getDeliveryOrderCode() {
        return deliveryOrderCode;
    }

    public void setDeliveryOrderCode(String deliveryOrderCode) {
        this.deliveryOrderCode = deliveryOrderCode;
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

    public String getAskTime() {
        return askTime;
    }

    public void setAskTime(String askTime) {
        this.askTime = askTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
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

    public List<RuoYuChenOutBoundOrderDetail> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<RuoYuChenOutBoundOrderDetail> orderLines) {
        this.orderLines = orderLines;
    }

    public RuoYuChenOutBoundOrderSenderInfo getSenderInfo() {
        return senderInfo;
    }

    public void setSenderInfo(RuoYuChenOutBoundOrderSenderInfo senderInfo) {
        this.senderInfo = senderInfo;
    }

    public RuoYuChenOutBoundOrderSenderInfo getReceiverInfo() {
        return receiverInfo;
    }

    public void setReceiverInfo(RuoYuChenOutBoundOrderSenderInfo receiverInfo) {
        this.receiverInfo = receiverInfo;
    }
}
