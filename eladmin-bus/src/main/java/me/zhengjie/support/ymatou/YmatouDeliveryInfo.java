package me.zhengjie.support.ymatou;

public class YmatouDeliveryInfo {
    private String logisticsCompanyCode;

    private String trackingNumber;

    private String deliveryTime;

    private int logisticsType;

    public String getLogisticsCompanyCode() {
        return logisticsCompanyCode;
    }

    public void setLogisticsCompanyCode(String logisticsCompanyCode) {
        this.logisticsCompanyCode = logisticsCompanyCode;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public int getLogisticsType() {
        return logisticsType;
    }

    public void setLogisticsType(int logisticsType) {
        this.logisticsType = logisticsType;
    }
}
