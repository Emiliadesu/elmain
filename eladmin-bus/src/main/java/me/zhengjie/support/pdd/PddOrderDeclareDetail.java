package me.zhengjie.support.pdd;

public class PddOrderDeclareDetail {
    /**
     * 货号
     */
    private String productId;
    /**
     * 条码
     */
    private String barcode;
    /**
     * 品名
     */
    private String goodsName;
    /**
     * 申报数量
     */
    private String qty;
    /**
     * 计量单位
     */
    private String unit;
    /**
     * 单价
     */
    private String price;
    /**
     * 总价
     */
    private String amount;

    private String payment;

    private String makeCountry;

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getMakeCountry() {
        return makeCountry;
    }

    public void setMakeCountry(String makeCountry) {
        this.makeCountry = makeCountry;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
