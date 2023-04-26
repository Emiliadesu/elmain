package me.zhengjie.support.pdd;

public class PddGetMailNoRequest implements PddCommonRequest{
    private String shopCode;
    private PddGetMailNo order;

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public PddGetMailNo getOrder() {
        return order;
    }

    public void setOrder(PddGetMailNo order) {
        this.order = order;
    }

    @Override
    public String getApiPath() {
        return "/api/pdd-erp/get-mail-no-2";
    }
}
