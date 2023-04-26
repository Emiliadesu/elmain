package me.zhengjie.support.pdd;

public class PddOrderDeclareRequest implements PddCommonRequest{
    public String shopCode;
    public PddOrderDeclare order;

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public PddOrderDeclare getOrder() {
        return order;
    }

    public void setOrder(PddOrderDeclare order) {
        this.order = order;
    }

    @Override
    public String getApiPath() {
        return "/api/pdd-erp/order-declare";
    }

    @Override
    public String toString() {
        return "PddOrderDeclareRequest{" +
                "shopCode='" + shopCode + '\'' +
                ", order=" + order +
                '}';
    }
}
