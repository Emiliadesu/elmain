package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;

public class CBOrderListChild {

    @JSONField(name = "gnum")
    private Long gnum;

    @JSONField(name = "sku_id")
    private String skuId;

    @JSONField(name = "item_no")
    private String itemNo;

    @JSONField(name = "item_name")
    private String itemName;

    @JSONField(name = "g_model")
    private String gModel;

    @JSONField(name = "item_describe")
    private String itemDescribe;

    @JSONField(name = "bar_code")
    private String barCode;

    @JSONField(name = "unit")
    private String unit;

    @JSONField(name = "qty")
    private Integer qty;

    @JSONField(name = "price")
    private Long price;

    @JSONField(name = "total_price")
    private Long totalPrice;

    @JSONField(name = "currency")
    private String currency;

    @JSONField(name = "country")
    private String country;

    @JSONField(name = "hs_code")
    private String hsCode;

    @JSONField(name = "first_measure_qty")
    private BigDecimal firstMeasureQty;

    @JSONField(name = "second_measure_qty")
    private BigDecimal secondMeasureQty;

    @JSONField(name = "first_measure_unit")
    private String firstMeasureUnit;

    @JSONField(name = "second_measure_unit")
    private String secondMeasureUnit;

    @JSONField(name = "note")
    private String note;

    public Long getGnum() {
        return gnum;
    }

    public void setGnum(Long gnum) {
        this.gnum = gnum;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getgModel() {
        return gModel;
    }

    public void setgModel(String gModel) {
        this.gModel = gModel;
    }

    public String getItemDescribe() {
        return itemDescribe;
    }

    public void setItemDescribe(String itemDescribe) {
        this.itemDescribe = itemDescribe;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHsCode() {
        return hsCode;
    }

    public void setHsCode(String hsCode) {
        this.hsCode = hsCode;
    }

    public BigDecimal getFirstMeasureQty() {
        return firstMeasureQty;
    }

    public void setFirstMeasureQty(BigDecimal firstMeasureQty) {
        this.firstMeasureQty = firstMeasureQty;
    }

    public BigDecimal getSecondMeasureQty() {
        return secondMeasureQty;
    }

    public void setSecondMeasureQty(BigDecimal secondMeasureQty) {
        this.secondMeasureQty = secondMeasureQty;
    }

    public String getFirstMeasureUnit() {
        return firstMeasureUnit;
    }

    public void setFirstMeasureUnit(String firstMeasureUnit) {
        this.firstMeasureUnit = firstMeasureUnit;
    }

    public String getSecondMeasureUnit() {
        return secondMeasureUnit;
    }

    public void setSecondMeasureUnit(String secondMeasureUnit) {
        this.secondMeasureUnit = secondMeasureUnit;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
