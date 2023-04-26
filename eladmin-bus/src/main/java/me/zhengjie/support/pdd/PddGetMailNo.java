package me.zhengjie.support.pdd;

import java.util.List;

public class PddGetMailNo {
    private String orderSn;
    private String city;
    private String country;
    private String town;
    private String address;
    private String province;
    private String receiverPhone;
    private String receiverName;
    private String innerTransactionId;
    private String buyerIdNum;
    private String grossWeight;
    private String netWeight;
    private String mailNo;
    private List<PddGetMailNoOrderItem>orderItems;

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getInnerTransactionId() {
        return innerTransactionId;
    }

    public String getMailNo() {
        return mailNo;
    }

    public void setMailNo(String mailNo) {
        this.mailNo = mailNo;
    }

    public void setInnerTransactionId(String innerTransactionId) {
        this.innerTransactionId = innerTransactionId;
    }

    public String getBuyerIdNum() {
        return buyerIdNum;
    }

    public void setBuyerIdNum(String buyerIdNum) {
        this.buyerIdNum = buyerIdNum;
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

    public List<PddGetMailNoOrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<PddGetMailNoOrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
