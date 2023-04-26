package me.zhengjie.support.pdd;

public class PddEncryptRequest implements PddCommonRequest{
    private String shopId;
    private String orderSn;
    private String encrypt;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public String getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(String encrypt) {
        this.encrypt = encrypt;
    }

    @Override
    public String getApiPath() {
        return "/api/pdd-erp/decrypt";
    }
}
