package me.zhengjie.support.pdd;

import com.alibaba.fastjson.annotation.JSONField;

public class PddGetOrderStatusRequest implements PddCommonRequest{
    /**
     * 要查询状态的订单号，多个订单号请用英文逗号隔开
     */
    @JSONField(name = "order_sns")
    private String orderSns;
    @JSONField(name = "shop_code")
    private String shopCode;

    public String getOrderSns() {
        return orderSns;
    }

    public void setOrderSns(String orderSn) {
        this.orderSns = orderSn;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    @Override
    public String getApiPath() {
        return "/api/pdd-erp/get-order-status";
    }
}
