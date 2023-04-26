package me.zhengjie.rest.model.meituan;

import com.alibaba.fastjson.annotation.JSONField;

public class RecipientAddressDetail {
    private String area;
    private String city;

    @JSONField(name = "detail_address")
    private String detailAddress;

    private String province;

    private String town;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }
}
