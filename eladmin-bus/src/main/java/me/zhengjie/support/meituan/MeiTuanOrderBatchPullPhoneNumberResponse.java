package me.zhengjie.support.meituan;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

public class MeiTuanOrderBatchPullPhoneNumberResponse {
    /**
     * 订单id
     */
    @JSONField(name = "order_id")
    private Long orderId;

    /**
     * APP方门店id
     */
    @JSONField(name = "app_poi_code")
    private Integer appPoiCode;

    /**
     * 订单展示ID
     */
    @JSONField(name = "wm_order_id_view")
    private Integer wmOrderIdView;

    /**
     * 订单流水号
     */
    @JSONField(name = "day_seq")
    private Integer daySeq;

    /**
     * 收件人真实手机号
     */
    @JSONField(name = "real_phone_number")
    private String realPhoneNumber;

    /**
     * 预订人手机号
     */
    @JSONField(name = "real_order_phone_number")
    private String realOrderPhoneNumber;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getAppPoiCode() {
        return appPoiCode;
    }

    public void setAppPoiCode(Integer appPoiCode) {
        this.appPoiCode = appPoiCode;
    }

    public Integer getWmOrderIdView() {
        return wmOrderIdView;
    }

    public void setWmOrderIdView(Integer wmOrderIdView) {
        this.wmOrderIdView = wmOrderIdView;
    }

    public Integer getDaySeq() {
        return daySeq;
    }

    public void setDaySeq(Integer daySeq) {
        this.daySeq = daySeq;
    }

    public String getRealPhoneNumber() {
        return realPhoneNumber;
    }

    public void setRealPhoneNumber(String realPhoneNumber) {
        this.realPhoneNumber = realPhoneNumber;
    }

    public String getRealOrderPhoneNumber() {
        return realOrderPhoneNumber;
    }

    public void setRealOrderPhoneNumber(String realOrderPhoneNumber) {
        this.realOrderPhoneNumber = realOrderPhoneNumber;
    }
}
