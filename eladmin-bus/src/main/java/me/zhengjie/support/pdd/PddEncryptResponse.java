package me.zhengjie.support.pdd;

public class PddEncryptResponse{
    private String address;
    private String receiverName;
    private String receiverPhone;
    private String buyerIdNum;
    private String buyerName;
    private String payNo;
    private String innerTransactionId;


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getPayNo() {
        return payNo;
    }

    public void setPayNo(String payNo) {
        this.payNo = payNo;
    }

    public String getInnerTransactionId() {
        return innerTransactionId;
    }

    public void setInnerTransactionId(String innerTransactionId) {
        this.innerTransactionId = innerTransactionId;
    }
}
