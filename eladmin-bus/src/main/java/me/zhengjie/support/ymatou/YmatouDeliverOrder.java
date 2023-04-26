package me.zhengjie.support.ymatou;

import com.alibaba.fastjson.annotation.JSONField;

public class YmatouDeliverOrder {
    /**
     * 代发货的订单编号
     */
    @JSONField(name = "order_id")
    private Long orderId;
    /**
     * 平台物流公司标识
     */
    @JSONField(name = "logistics_company_id")
    private String logisticsCompanyId;
    /**
     * 物流面单号
     */
    @JSONField(name = "tracking_number")
    private String trackingNumber;
    /**
     * 是否国内段发货
     */
    @JSONField(name = "is_domestic_delivery")
    private String isDomesticDelivery;
    /**
     * 是否国内转运发货
     */
    @JSONField(name = "tran_ship")
    private String tran_ship;
    /**
     * 是否香港转运发货
     */
    @JSONField(name = "ship_by_hk")
    private String shipByHk;
    /**
     * 收件人号码
     */
    @JSONField(name = "mobile")
    private String mobile;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getLogisticsCompanyId() {
        return logisticsCompanyId;
    }

    public void setLogisticsCompanyId(String logisticsCompanyId) {
        this.logisticsCompanyId = logisticsCompanyId;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getIsDomesticDelivery() {
        return isDomesticDelivery;
    }

    public void setIsDomesticDelivery(String isDomesticDelivery) {
        this.isDomesticDelivery = isDomesticDelivery;
    }

    public String getTran_ship() {
        return tran_ship;
    }

    public void setTran_ship(String tran_ship) {
        this.tran_ship = tran_ship;
    }

    public String getShipByHk() {
        return shipByHk;
    }

    public void setShipByHk(String shipByHk) {
        this.shipByHk = shipByHk;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
