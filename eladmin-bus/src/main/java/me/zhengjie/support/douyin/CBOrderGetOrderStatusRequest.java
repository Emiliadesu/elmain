package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

public class CBOrderGetOrderStatusRequest implements CommonApiParam {

    @Override
    public String getMethod() {
        return "crossBorder.getOrderStatus";
    }

    @Override
    public String getKeyWord() {
        return this.getOrderId();
    }

    /**
     * 入驻字节平台的商家店铺ID <开放平台提供>
     */
    @JSONField(name = "open_shop_id")
    private String openShopId;

    /**
     * 开放平台为入驻分配的授权的id <开放平台提供>
     */
    @JSONField(name = "open_app_key")
    private String openAppKey;

    @JSONField(name = "vendor")
    private String vendor;
    /**
     * 时间类型 必填，create_time订单创建时间，update_time订单更新时间
     */
    @JSONField(name = "order_id")
    private String orderId;

    public String getOpenShopId() {
        return openShopId;
    }

    public void setOpenShopId(String openShopId) {
        this.openShopId = openShopId;
    }

    public String getOpenAppKey() {
        return openAppKey;
    }

    public void setOpenAppKey(String openAppKey) {
        this.openAppKey = openAppKey;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "CBOrderGetOrderStatusRequest{" +
                "openShopId='" + openShopId + '\'' +
                ", openAppKey='" + openAppKey + '\'' +
                ", vendor='" + vendor + '\'' +
                ", orderId='" + orderId + '\'' +
                '}';
    }
}
