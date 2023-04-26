package me.zhengjie.support.ymatou;

import com.alibaba.fastjson.annotation.JSONField;

public class YmatouLogisticsCompany {
    @JSONField(name = "delivery_types")
    private Integer[]deliveryTypes;
    @JSONField(name = "package_delivery_type")
    private Integer packageDeliveryType;
    @JSONField(name = "company_code")
    private String companyCode;
    @JSONField(name = "short_name")
    private String shortName;
    @JSONField(name = "company_name")
    private String companyName;

    public Integer[] getDeliveryTypes() {
        return deliveryTypes;
    }

    public void setDeliveryTypes(Integer[] deliveryTypes) {
        this.deliveryTypes = deliveryTypes;
    }

    public Integer getPackageDeliveryType() {
        return packageDeliveryType;
    }

    public void setPackageDeliveryType(Integer packageDeliveryType) {
        this.packageDeliveryType = packageDeliveryType;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
