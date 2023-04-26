package me.zhengjie.rest.model.jackYun;

import me.zhengjie.support.jackYun.JackYunDeliverDetail;

public class JackYunDeliveryOrder extends JackYunBasicRequest{
    /**
     * 订单号
     */
    private String deliveryOrderCode;
    /**
     * 报关号
     */
    private String declareOrderNo;
    /**
     * 税款
     */
    private String taxAmount;
    /**
     * 支付海关备案代码
     */
    private String payCompanyCustomsCode;
    /**
     * 支付海关备案名称
     */
    private String payCompanyCustomsName;
    /**
     * 支付方式
     */
    private String payType;
    /**
     * 证件号
     */
    private String idcard;
    /**
     * 证件类型
     */
    private String idcardtype;
    /**
     * 买家姓名
     */
    private String buyerName;
    /**
     * 购物网站备案代码
     */
    private String sellPlatRecordCode;
    /**
     * 订单来源代码
     */
    private String sourcePlatformCode;
    /**
     * 订单来源名称
     */
    private String sourcePlatformName;
    /**
     * 订单创建时间
     */
    private String placeOrderTime;
    /**
     *支付时间
     */
    private String payTime;
    /**
     * 支付单号
     */
    private String payNo;
    /**
     * 买家昵称
     */
    private String buyerNick;
    /**
     * 订单总金额
     */
    private String totalAmount;
    /**
     * 商品总金额
     */
    private String itemAmount;
    /**
     * 优惠金额
     */
    private String discountAmount;
    /**
     * 快递费
     */
    private String freight;
    /**
     * 快递编码
     */
    private String logisticsCode;
    /**
     * 快递名
     */
    private String logisticsName;
    /**
     * 运单号
     */
    private String expressCode;
    /**
     * 快递区域编码
     */
    private String logisticsAreaCode;
    /**
     * 发件人地址
     */
    private JackYunAddress senderInfo;
    /**
     * 收件人地址
     */
    private JackYunAddress receiverInfo;
    /**
     * 商品列表
     */
    private JackYunDeliverDetail detail;

    public String getDeliveryOrderCode() {
        return deliveryOrderCode;
    }

    public void setDeliveryOrderCode(String deliveryOrderCode) {
        this.deliveryOrderCode = deliveryOrderCode;
    }

    public String getDeclareOrderNo() {
        return declareOrderNo;
    }

    public void setDeclareOrderNo(String declareOrderNo) {
        this.declareOrderNo = declareOrderNo;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getPayCompanyCustomsCode() {
        return payCompanyCustomsCode;
    }

    public void setPayCompanyCustomsCode(String payCompanyCustomsCode) {
        this.payCompanyCustomsCode = payCompanyCustomsCode;
    }

    public String getPayCompanyCustomsName() {
        return payCompanyCustomsName;
    }

    public void setPayCompanyCustomsName(String payCompanyCustomsName) {
        this.payCompanyCustomsName = payCompanyCustomsName;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getIdcardtype() {
        return idcardtype;
    }

    public void setIdcardtype(String idcardtype) {
        this.idcardtype = idcardtype;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getSellPlatRecordCode() {
        return sellPlatRecordCode;
    }

    public void setSellPlatRecordCode(String sellPlatRecordCode) {
        this.sellPlatRecordCode = sellPlatRecordCode;
    }

    public String getSourcePlatformCode() {
        return sourcePlatformCode;
    }

    public void setSourcePlatformCode(String sourcePlatformCode) {
        this.sourcePlatformCode = sourcePlatformCode;
    }

    public String getSourcePlatformName() {
        return sourcePlatformName;
    }

    public void setSourcePlatformName(String sourcePlatformName) {
        this.sourcePlatformName = sourcePlatformName;
    }

    public String getPlaceOrderTime() {
        return placeOrderTime;
    }

    public void setPlaceOrderTime(String placeOrderTime) {
        this.placeOrderTime = placeOrderTime;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getPayNo() {
        return payNo;
    }

    public void setPayNo(String payNo) {
        this.payNo = payNo;
    }

    public String getBuyerNick() {
        return buyerNick;
    }

    public void setBuyerNick(String buyerNick) {
        this.buyerNick = buyerNick;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(String itemAmount) {
        this.itemAmount = itemAmount;
    }

    public String getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(String discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getFreight() {
        return freight;
    }

    public void setFreight(String freight) {
        this.freight = freight;
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

    public String getLogisticsAreaCode() {
        return logisticsAreaCode;
    }

    public void setLogisticsAreaCode(String logisticsAreaCode) {
        this.logisticsAreaCode = logisticsAreaCode;
    }

    public JackYunAddress getSenderInfo() {
        return senderInfo;
    }

    public void setSenderInfo(JackYunAddress senderInfo) {
        this.senderInfo = senderInfo;
    }

    public JackYunAddress getReceiverInfo() {
        return receiverInfo;
    }

    public void setReceiverInfo(JackYunAddress receiverInfo) {
        this.receiverInfo = receiverInfo;
    }

    public JackYunDeliverDetail getDetail() {
        return detail;
    }

    public void setDetail(JackYunDeliverDetail detail) {
        this.detail = detail;
    }
}
