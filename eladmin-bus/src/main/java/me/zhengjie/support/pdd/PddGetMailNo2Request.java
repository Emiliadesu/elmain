package me.zhengjie.support.pdd;


public class PddGetMailNo2Request implements PddCommonRequest{
    private String shopCode;

    private String param;

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    @Override
    public String getApiPath() {
        return "/api/pdd-erp/get-mail-no";
    }
}
