package me.zhengjie.support.pdd;

public class PddGetMailNoOrderItem {
    private String productId;
    private String goodsName;
    private String payment;
    private Integer qty;
    private String makeCountry;
    private String unit;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getMakeCountry() {
        return makeCountry;
    }

    public void setMakeCountry(String makeCountry) {
        this.makeCountry = makeCountry;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
