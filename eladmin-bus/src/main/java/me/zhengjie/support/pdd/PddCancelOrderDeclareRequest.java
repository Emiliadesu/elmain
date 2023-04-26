package me.zhengjie.support.pdd;

public class PddCancelOrderDeclareRequest implements PddCommonRequest{
    /**
     * 拼多多跨境单号
     */
    private String orderNo;
    /**
     * 拼多多订单号
     */
    private String orderSn;
    /**
     * 拼多多店铺code
     */
    private String shopCode;
    /**
     * 推单抬头Code
     */
    private String customerCode;
    /**
     * 推单抬头名
     */
    private String customerName;
    /**
     * 运单号
     */
    private String mailNo;
    /**
     * 快递公司编码
     */
    private String logisticsCode;
    /**
     * 快递在海关的备案名
     */
    private String cusLogisticsName;
    /**
     * 申报单号
     */
    private String declareNo;
    /**
     * 总署单号
     */
    private String invtNo;
    /**
     * 身份证号码(拼多多密文)
     */
    private String buyerIdNum;
    /**
     * 身份证姓名(拼多多密文)
     */
    private String buyerName;
    /**
     * 支付人手机号(拼多多密文)
     */
    private String phone;

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

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
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

    public String getMailNo() {
        return mailNo;
    }

    public void setMailNo(String mailNo) {
        this.mailNo = mailNo;
    }

    public String getCusLogisticsName() {
        return cusLogisticsName;
    }

    public void setCusLogisticsName(String cusLogisticsName) {
        this.cusLogisticsName = cusLogisticsName;
    }

    public String getLogisticsCode() {
        return logisticsCode;
    }

    public void setLogisticsCode(String logisticsCode) {
        this.logisticsCode = logisticsCode;
    }

    public String getDeclareNo() {
        return declareNo;
    }

    public void setDeclareNo(String declareNo) {
        this.declareNo = declareNo;
    }

    public String getInvtNo() {
        return invtNo;
    }

    public void setInvtNo(String invtNo) {
        this.invtNo = invtNo;
    }

    public String getBuyerIdNum() {
        return buyerIdNum;
    }

    public void setBuyerIdNum(String buyerIdNum) {
        this.buyerIdNum = buyerIdNum;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String getApiPath() {
        return "/api/pdd-erp/cancel-order-declare";
    }
}
