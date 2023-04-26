package me.zhengjie.support.pdd;

import com.alibaba.fastjson.annotation.JSONField;

public class PddLogisticsSendRequest implements PddCommonRequest{

    @JSONField(name = "shop_code")
    private String shopCode;

    @JSONField(name = "pdd_order")
    private PddOrder pddOrder;

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public PddOrder getPddOrder() {
        return pddOrder;
    }

    public void setPddOrder(PddOrder pddOrder) {
        this.pddOrder = pddOrder;
    }

    @Override
    public String getApiPath() {
        return "/api/pdd-erp/send-order";
    }
}
