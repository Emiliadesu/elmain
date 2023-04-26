package me.zhengjie.rest.model.jackYun;

import me.zhengjie.support.jackYun.JackYunDeliverOrderLine;

public class JackYunDeliveryOrderOrderLine extends JackYunDeliverOrderLine {
    private String retailPrice;
    private String actualPrice;
    private String discountAmount;

    public String getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(String retailPrice) {
        this.retailPrice = retailPrice;
    }

    public String getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(String actualPrice) {
        this.actualPrice = actualPrice;
    }

    public String getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(String discountAmount) {
        this.discountAmount = discountAmount;
    }
}
