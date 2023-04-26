package me.zhengjie.rest.model.ruoyuchen.request;

public class RuoYuChenOutBoundOrderSenderInfo {
    /**
     * 发件人名
     */
    private String name;
    /**
     * 移动电话
     */
    private String senderInfo;
    /**
     * 电子邮箱
     */
    private String email;
    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 区
     */
    private String area;
    /**
     * 详细地址
     */
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSenderInfo() {
        return senderInfo;
    }

    public void setSenderInfo(String senderInfo) {
        this.senderInfo = senderInfo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
