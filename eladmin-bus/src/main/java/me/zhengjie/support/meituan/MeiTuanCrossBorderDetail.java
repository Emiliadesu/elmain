package me.zhengjie.support.meituan;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

public class MeiTuanCrossBorderDetail {
    /**
     * 订单门店id
     */
    @JSONField(name = "app_poi_code")
    private String appPoiCode;
    /**
     * 订单号
     */
    @JSONField(name = "wm_order_id_view")
    private String wmOrderIdView;


    /**
     * 支付交易编号（交易流水号）
     */
    @JSONField(name = "pay_transaction_id")
    private String payTransactionId;

    /**
     * 支付人证件类型：1-身份证 2-其他
     */
    @JSONField(name = "payer_id_type")
    private Integer payerIdType;

    /**
     * 支付人证件号码：该字段需解密
     */
    @JSONField(name = "payer_id_number")
    private String payerIdNumber;

    /**
     * 支付人姓名：该字段需解密
     */
    @JSONField(name = "payer_name")
    private String payerName;

    /**
     * 支付币制 ：142-人民币
     */
    private String currency;

    /**
     * 支付时间：10位秒级时间戳
     */
    @JSONField(name = "pay_time")
    private Long payTime;

    /**
     * 订单类型：I-进口商品订单(大写字母i)；E-出口商品订单
     */
    @JSONField(name = "order_type")
    private String orderType;

    /**
     * 电商企业代码
     */
    @JSONField(name = "ebc_code")
    private String ebcCode;

    /**
     * 电商企业名称
     */
    @JSONField(name = "ebc_name")
    private String ebcName;

    /**
     * 订购人注册号：该字段需解密
     */
    @JSONField(name = "buyer_reg_no")
    private String buyerRegNo;

    /**
     * 订购人真实姓名：该字段需解密
     */
    @JSONField(name = "buyer_name")
    private String buyerName;

    /**
     * 订购人身份类型：1-身份证 2-其他
     */
    @JSONField(name = "buyer_id_type")
    private Integer buyerIdType;

    /**
     * 订购人身份证号：该字段需解密
     */
    @JSONField(name = "buyer_id_number")
    private String buyerIdNumber;

    /**
     * 代扣税款：单位元
     */
    @JSONField(name = "tax_total")
    private String taxTotal;

    /**
     * 电商平台代码
     */
    @JSONField(name = "ebp_code")
    private String ebpCode;

    /**
     * 电商平台名称
     */
    @JSONField(name = "ebp_name")
    private String ebpName;

    /**
     * 支付企业代码
     */
    @JSONField(name = "pay_code")
    private String payCode;

    /**
     * 支付企业名称
     */
    @JSONField(name = "pay_name")
    private String payName;

    public String getPayTransactionId() {
        return payTransactionId;
    }

    public String getAppPoiCode() {
        return appPoiCode;
    }

    public void setAppPoiCode(String appPoiCode) {
        this.appPoiCode = appPoiCode;
    }

    public String getWmOrderIdView() {
        return wmOrderIdView;
    }

    public void setWmOrderIdView(String wmOrderIdView) {
        this.wmOrderIdView = wmOrderIdView;
    }

    public void setPayTransactionId(String payTransactionId) {
        this.payTransactionId = payTransactionId;
    }

    public Integer getPayerIdType() {
        return payerIdType;
    }

    public void setPayerIdType(Integer payerIdType) {
        this.payerIdType = payerIdType;
    }

    public String getPayerIdNumber() {
        return payerIdNumber;
    }

    public void setPayerIdNumber(String payerIdNumber) {
        this.payerIdNumber = payerIdNumber;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getPayTime() {
        return payTime;
    }

    public void setPayTime(Long payTime) {
        this.payTime = payTime;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getEbcCode() {
        return ebcCode;
    }

    public void setEbcCode(String ebcCode) {
        this.ebcCode = ebcCode;
    }

    public String getEbcName() {
        return ebcName;
    }

    public void setEbcName(String ebcName) {
        this.ebcName = ebcName;
    }

    public String getBuyerRegNo() {
        return buyerRegNo;
    }

    public void setBuyerRegNo(String buyerRegNo) {
        this.buyerRegNo = buyerRegNo;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public Integer getBuyerIdType() {
        return buyerIdType;
    }

    public void setBuyerIdType(Integer buyerIdType) {
        this.buyerIdType = buyerIdType;
    }

    public String getBuyerIdNumber() {
        return buyerIdNumber;
    }

    public void setBuyerIdNumber(String buyerIdNumber) {
        this.buyerIdNumber = buyerIdNumber;
    }

    public String getTaxTotal() {
        return taxTotal;
    }

    public void setTaxTotal(String taxTotal) {
        this.taxTotal = taxTotal;
    }

    public String getEbpCode() {
        return ebpCode;
    }

    public void setEbpCode(String ebpCode) {
        this.ebpCode = ebpCode;
    }

    public String getEbpName() {
        return ebpName;
    }

    public void setEbpName(String ebpName) {
        this.ebpName = ebpName;
    }

    public String getPayCode() {
        return payCode;
    }

    public void setPayCode(String payCode) {
        this.payCode = payCode;
    }

    public String getPayName() {
        return payName;
    }

    public void setPayName(String payName) {
        this.payName = payName;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
