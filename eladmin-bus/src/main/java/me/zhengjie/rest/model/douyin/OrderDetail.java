package me.zhengjie.rest.model.douyin;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class OrderDetail {

    /**
     * 订单明细序号
     */
    @JsonProperty(value = "gnum")
    @JSONField(name = "gnum")
    private Long gnum;

    /**
     * 字节平台商品sku id
     */
    @JsonProperty(value = "sku_id")
    @JSONField(name = "sku_id")
    private String skuId;

    /**
     * 货品id
     */
    @JsonProperty(value = "item_id")
    @JSONField(name = "item_id")
    private String itemId;

    /**
     * 货品编码
     */
    @JsonProperty(value = "item_no")
    @JSONField(name = "item_no")
    private String itemNo;

    /**
     * 平台商品名称 （商家在电商平台创建的名称）
     */
    @JsonProperty(value = "item_name")
    @JSONField(name = "item_name")
    private String itemName;

    /**
     * 平台商品描述 （商家在电商平台创建的描述 ）
     */
    @JsonProperty(value = "item_describe")
    @JSONField(name = "item_describe")
    private String itemDescribe;

    /**
     * 商品规格型号
     */
    @JsonProperty(value = "g_model")
    @JSONField(name = "g_model")
    private String gModel;

    /**
     * SKU条形码
     */
    @JsonProperty(value = "bar_code")
    @JSONField(name = "bar_code")
    private String barCode;

    /**
     * 法定计量单位
     */
    @JsonProperty(value = "unit")
    @JSONField(name = "unit")
    private String unit;

    /**
     * 商品实际数量
     */
    @JsonProperty(value = "qty")
    @JSONField(name = "qty")
    private Integer qty;

    /**
     * 单价（商品不含税价，不含运费保费）  单位是分 BBC/BC：单价；CC：实付单价
     */
    @JsonProperty(value = "price")
    @JSONField(name = "price")
    private Long price;

    /**
     * 总价  单位是分 BBC/BC总价（商品不含税价，不含保费运费）；CC：实付总价
     */
    @JsonProperty(value = "total_price")
    @JSONField(name = "total_price")
    private Long totalPrice;

    /**
     * 币制
     */
    @JsonProperty(value = "currency")
    @JSONField(name = "currency")
    private String currency;

    /**
     * 原产国
     */
    @JsonProperty(value = "country")
    @JSONField(name = "country")
    private String country;

    /**
     * 跨境商品HSCODE税号编码（CC商品无该信息）
     */
    @JsonProperty(value = "hs_code")
    @JSONField(name = "hs_code")
    private String hsCode;

    /**
     * 法一数量 （第一法定计量单位数量）
     */
    @JsonProperty(value = "first_measure_qty")
    @JSONField(name = "first_measure_qty")
    private BigDecimal firstMeasureQty;

    /**
     * 法二数量 （第二法定计量单位数量）
     */
    @JsonProperty(value = "second_measure_qty")
    @JSONField(name = "second_measure_qty")
    private BigDecimal secondMeasureQty;

    /**
     * 法一计量单位
     */
    @JsonProperty(value = "first_measure_unit")
    @JSONField(name = "first_measure_unit")
    private String firstMeasureUnit;

    /**
     * 法二计量单位
     */
    @JsonProperty(value = "second_measure_unit")
    @JSONField(name = "second_measure_unit")
    private String secondMeasureUnit;

    /**
     * 商品净重
     */
    @JsonProperty(value = "net_weight_qty")
    @JSONField(name = "net_weight_qty")
    private String netWeightQty;

    /**
     * 商品毛重
     */
    @JsonProperty(value = "gross_weight_qty")
    @JSONField(name = "gross_weight_qty")
    private String grossWeightQty;

    /**
     * 商品备案品名
     */
    @JsonProperty(value = "record_name")
    @JSONField(name = "record_name")
    private String recordName;

    /**
     * 品牌名称
     */
    @JsonProperty(value = "brand_name")
    @JSONField(name = "brand_name")
    private String brandName;

    /**
     * (仅cc使用)行邮税号
     */
    @JsonProperty(value = "postal_code")
    @JSONField(name = "postal_code")
    private String postalCode;

    /**
     * (仅cc使用)英文品名
     */
    @JsonProperty(value = "record_name_en")
    @JSONField(name = "record_name_en")
    private String recordNameEn;

    /**
     * (仅cc使用)是否香港OTC，1香港OFC，0非香港OTC
     */
    @JsonProperty(value = "is_otc")
    @JSONField(name = "is_otc")
    private Integer isOtc;

    /**
     * (仅cc使用)本地注册编码，如果为香港OTC，需要同时申报此字段
     */
    @JsonProperty(value = "otc_reg_no")
    @JSONField(name = "otc_reg_no")
    private String otcRegNo;

    /**
     * 备案料号
     */
    @JsonProperty(value = "record_part_no")
    @JSONField(name = "record_part_no")
    private String recordPartNo;

    /**
     * 配料及成分说明
     */
    @JsonProperty(value = "ingredient_desc")
    @JSONField(name = "ingredient_desc")
    private String ingredientDesc;

    /**
     * 规格型号
     */
    @JsonProperty(value = "specification")
    @JSONField(name = "specification")
    private String specification;

    /**
     * 申报计量单位，海关申报计量单位
     */
    @JsonProperty(value = "unit_declare")
    @JSONField(name = "unit_declare")
    private String unitDeclare;

    /**
     * 备注 （扩展预留）
     */
    @JsonProperty(value = "note")
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

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
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

    public String getItemDescribe() {
        return itemDescribe;
    }

    public void setItemDescribe(String itemDescribe) {
        this.itemDescribe = itemDescribe;
    }

    public String getgModel() {
        return gModel;
    }

    public void setgModel(String gModel) {
        this.gModel = gModel;
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

    public String getNetWeightQty() {
        return netWeightQty;
    }

    public void setNetWeightQty(String netWeightQty) {
        this.netWeightQty = netWeightQty;
    }

    public String getGrossWeightQty() {
        return grossWeightQty;
    }

    public void setGrossWeightQty(String grossWeightQty) {
        this.grossWeightQty = grossWeightQty;
    }

    public String getRecordName() {
        return recordName;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getRecordNameEn() {
        return recordNameEn;
    }

    public void setRecordNameEn(String recordNameEn) {
        this.recordNameEn = recordNameEn;
    }

    public Integer getIsOtc() {
        return isOtc;
    }

    public void setIsOtc(Integer isOtc) {
        this.isOtc = isOtc;
    }

    public String getOtcRegNo() {
        return otcRegNo;
    }

    public void setOtcRegNo(String otcRegNo) {
        this.otcRegNo = otcRegNo;
    }

    public String getIngredientDesc() {
        return ingredientDesc;
    }

    public void setIngredientDesc(String ingredientDesc) {
        this.ingredientDesc = ingredientDesc;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getUnitDeclare() {
        return unitDeclare;
    }

    public void setUnitDeclare(String unitDeclare) {
        this.unitDeclare = unitDeclare;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getRecordPartNo() {
        return recordPartNo;
    }

    public void setRecordPartNo(String recordPartNo) {
        this.recordPartNo = recordPartNo;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
