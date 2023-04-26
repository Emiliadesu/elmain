package me.zhengjie.support.pdd;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class PddPullOrderByOrderSnRequest implements PddCommonRequest{
    @JSONField(name = "order_sns")
    private String [] orderSns;
    @JSONField(name = "shop_code")
    private String shopCode;

    public String[] getOrderSns() {
        return orderSns;
    }

    public void setOrderSns(String[] orderSns) {
        this.orderSns = orderSns;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    @Override
    public String getApiPath() {
        return "/api/pdd-erp/get-order-list-by-sn";
    }
}
