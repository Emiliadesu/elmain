package me.zhengjie.support.pdd;


import java.util.List;

public class PddOrderDeclare {
    /**
     * 跨境单号，XP单号
     */
    private String orderNo;
    /**
     * 拼多多订单号
     */
    private String orderSn;
    /**
     * 支付单号
     */
    private String paymentNo;
    /**
     * 支付人联系电话
     */
    private String phone;
    /**
     * 申报单号
     */
    private String declareNo;
    /**
     * 支付人身份证
     */
    private String buyerIdNum;
    /**
     * 支付人姓名
     */
    private String buyerName;
    /**
     * 收货地址 省
     */
    private String province;
    /**
     * 收货地址 市
     */
    private String city;
    /**
     * 收货地址 区
     */
    private String district;
    /**
     * 收货详细地址 拼接省市区
     */
    private String consigneeAddr;
    /**
     * 收货人
     */
    private String consignee;
    /**
     * 收货人联系电话
     */
    private String consigneeTel;
    /**
     * 推单抬头编码
     */
    private String customerCode;
    /**
     * 推单抬头名
     */
    private String customerName;
    /**
     * 订单创建时间
     */
    private String createTime;
    /**
     * 运费
     */
    private String postFee;
    /**
     * 支付金额
     */
    private String amount;
    /**
     * 支付人平台账号名
     */
    private String buyerAccount;
    /**
     * 订单总毛重
     */
    private String grossWeight;
    /**
     * 订单总净重
     */
    private String netWeight;
    /**
     * 订单优惠费
     */
    private String disAmount;
    /**
     * 税费
     */
    private String taxAmount;
    /**
     * 账册编码
     */
    private String booksNo;
    /**
     * 支付时间
     */
    private String payTime;
    /**
     * 支付方式
     */
    private String source;
    /**
     * 运单号
     */
    private String mailNo;
    /**
     * 快递公司名
     */
    private String logisticsName;
    /**
     * 大头笔
     */
    private String addMark;
    /**
     * 商品详情
     */
    private List<PddOrderDeclareDetail> details;
    /**
     * 推单抬头账户秘钥
     */
    private KeyPwdInfo keyPwd;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public String getPaymentNo() {
        return paymentNo;
    }

    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBuyerIdNum() {
        return buyerIdNum;
    }

    public void setBuyerIdNum(String buyerIdNum) {
        this.buyerIdNum = buyerIdNum;
    }

    public String getDeclareNo() {
        return declareNo;
    }

    public void setDeclareNo(String declareNo) {
        this.declareNo = declareNo;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getConsigneeAddr() {
        return consigneeAddr;
    }

    public void setConsigneeAddr(String consigneeAddr) {
        this.consigneeAddr = consigneeAddr;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getConsigneeTel() {
        return consigneeTel;
    }

    public void setConsigneeTel(String consigneeTel) {
        this.consigneeTel = consigneeTel;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPostFee() {
        return postFee;
    }

    public void setPostFee(String postFee) {
        this.postFee = postFee;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBuyerAccount() {
        return buyerAccount;
    }

    public void setBuyerAccount(String buyerAccount) {
        this.buyerAccount = buyerAccount;
    }

    public String getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(String grossWeight) {
        this.grossWeight = grossWeight;
    }

    public String getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(String netWeight) {
        this.netWeight = netWeight;
    }

    public String getDisAmount() {
        return disAmount;
    }

    public void setDisAmount(String disAmount) {
        this.disAmount = disAmount;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getBooksNo() {
        return booksNo;
    }

    public void setBooksNo(String booksNo) {
        this.booksNo = booksNo;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMailNo() {
        return mailNo;
    }

    public void setMailNo(String mailNo) {
        this.mailNo = mailNo;
    }

    public String getLogisticsName() {
        return logisticsName;
    }

    public void setLogisticsName(String logisticsName) {
        this.logisticsName = logisticsName;
    }

    public String getAddMark() {
        return addMark;
    }

    public void setAddMark(String addMark) {
        this.addMark = addMark;
    }

    public List<PddOrderDeclareDetail> getDetails() {
        return details;
    }

    public void setDetails(List<PddOrderDeclareDetail> details) {
        this.details = details;
    }

    public KeyPwdInfo getKeyPwd() {
        return keyPwd;
    }

    public void setKeyPwd(KeyPwdInfo keyPwd) {
        this.keyPwd = keyPwd;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    @Override
    public String toString() {
        return "PddOrderDeclare{" +
                "orderNo='" + orderNo + '\'' +
                ", orderSn='" + orderSn + '\'' +
                ", paymentNo='" + paymentNo + '\'' +
                ", phone='" + phone + '\'' +
                ", declareNo='" + declareNo + '\'' +
                ", buyerIdNum='" + buyerIdNum + '\'' +
                ", buyerName='" + buyerName + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", consigneeAddr='" + consigneeAddr + '\'' +
                ", consignee='" + consignee + '\'' +
                ", consigneeTel='" + consigneeTel + '\'' +
                ", customerCode='" + customerCode + '\'' +
                ", customerName='" + customerName + '\'' +
                ", createTime='" + createTime + '\'' +
                ", postFee='" + postFee + '\'' +
                ", amount='" + amount + '\'' +
                ", buyerAccount='" + buyerAccount + '\'' +
                ", grossWeight='" + grossWeight + '\'' +
                ", netWeight='" + netWeight + '\'' +
                ", disAmount='" + disAmount + '\'' +
                ", taxAmount='" + taxAmount + '\'' +
                ", booksNo='" + booksNo + '\'' +
                ", payTime='" + payTime + '\'' +
                ", source='" + source + '\'' +
                ", mailNo='" + mailNo + '\'' +
                ", logisticsName='" + logisticsName + '\'' +
                ", addMark='" + addMark + '\'' +
                ", details=" + details +
                ", keyPwd=" + keyPwd +
                '}';
    }
}
