package me.zhengjie.support.pdd;

public class PddDecryptMaskResponse{
    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 店铺code
     */
    private String pddUserId;

    /**
     * 支付单号密文
     */
    private String payNo;

    /**
     * 收货人姓名 密文
     */
    private String receiverName;

    /**
     * 收货人电话 密文
     */
    private String receiverPhone;

    /**
     * 收货人地址 密文
     */
    private String address;

    /**
     * 买家身份证号码 密文
     */
    private String idCardNum;

    /**
     * 买家身份证姓名 密文
     */
    private String idCardName;

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public String getPddUserId() {
        return pddUserId;
    }

    public void setPddUserId(String pddUserId) {
        this.pddUserId = pddUserId;
    }

    public String getPayNo() {
        return payNo;
    }

    public void setPayNo(String payNo) {
        this.payNo = payNo;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdCardNum() {
        return idCardNum;
    }

    public void setIdCardNum(String idCardNum) {
        this.idCardNum = idCardNum;
    }

    public String getIdCardName() {
        return idCardName;
    }

    public void setIdCardName(String idCardName) {
        this.idCardName = idCardName;
    }
}
