package me.zhengjie.rest.model.meituan;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;

public class SkuBenefitDetail {
    /**
     * 单件商品sku参加活动优惠后的单价
     */
    @JSONField(name = "activityPrice")
    private BigDecimal activityPrice;

    /**
     * 商家后台的商品编码
     */
    @JSONField(name = "app_food_code")
    private String appFoodCode;

    /**
     *
     */
    @JSONField(name = "app_medicine_code")
    private String appMedicineCode;

    /**
     * 单件sku打包盒数量
     */
    private Integer boxNumber;

    /**
     * sku的打包费
     */
    private BigDecimal boxPrice;

    /**
     * sku购买数
     */
    private Integer count;

    /**
     * 商家后台品名
     */
    private String name;

    /**
     * 原价
     */
    private BigDecimal originPrice;

    /**
     * sku编码，唯一
     */
    @JSONField(name = "sku_id")
    private String skuId;

    /**
     * 商品的upc编码，唯一，用于库存维护
     */
    private String upc;

    /**
     * 商品优惠后总价
     */
    private BigDecimal totalActivityPrice;

    /**
     * 商品打包费
     */
    private BigDecimal totalBoxPrice;

    /**
     * 优惠总价(美团承担的金额)
     */
    private BigDecimal totalMtCharge;

    /**
     * 商品优惠前总价
     */
    private BigDecimal totalOriginPrice;

    /**
     * 优惠总价(商家承担的金额)
     */
    private BigDecimal totalPoiCharge;

    /**
     * 商品总优惠
     */
    private BigDecimal totalReducePrice;

    private String wmAppOrderActDetails;

    public BigDecimal getActivityPrice() {
        return activityPrice;
    }

    public void setActivityPrice(BigDecimal activityPrice) {
        this.activityPrice = activityPrice;
    }

    public String getAppFoodCode() {
        return appFoodCode;
    }

    public void setAppFoodCode(String appFoodCode) {
        this.appFoodCode = appFoodCode;
    }

    public String getAppMedicineCode() {
        return appMedicineCode;
    }

    public void setAppMedicineCode(String appMedicineCode) {
        this.appMedicineCode = appMedicineCode;
    }

    public Integer getBoxNumber() {
        return boxNumber;
    }

    public void setBoxNumber(Integer boxNumber) {
        this.boxNumber = boxNumber;
    }

    public BigDecimal getBoxPrice() {
        return boxPrice;
    }

    public void setBoxPrice(BigDecimal boxPrice) {
        this.boxPrice = boxPrice;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getOriginPrice() {
        return originPrice;
    }

    public void setOriginPrice(BigDecimal originPrice) {
        this.originPrice = originPrice;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public BigDecimal getTotalActivityPrice() {
        return totalActivityPrice;
    }

    public void setTotalActivityPrice(BigDecimal totalActivityPrice) {
        this.totalActivityPrice = totalActivityPrice;
    }

    public BigDecimal getTotalBoxPrice() {
        return totalBoxPrice;
    }

    public void setTotalBoxPrice(BigDecimal totalBoxPrice) {
        this.totalBoxPrice = totalBoxPrice;
    }

    public BigDecimal getTotalMtCharge() {
        return totalMtCharge;
    }

    public void setTotalMtCharge(BigDecimal totalMtCharge) {
        this.totalMtCharge = totalMtCharge;
    }

    public BigDecimal getTotalOriginPrice() {
        return totalOriginPrice;
    }

    public void setTotalOriginPrice(BigDecimal totalOriginPrice) {
        this.totalOriginPrice = totalOriginPrice;
    }

    public BigDecimal getTotalPoiCharge() {
        return totalPoiCharge;
    }

    public void setTotalPoiCharge(BigDecimal totalPoiCharge) {
        this.totalPoiCharge = totalPoiCharge;
    }

    public BigDecimal getTotalReducePrice() {
        return totalReducePrice;
    }

    public void setTotalReducePrice(BigDecimal totalReducePrice) {
        this.totalReducePrice = totalReducePrice;
    }

    public String getWmAppOrderActDetails() {
        return wmAppOrderActDetails;
    }

    public void setWmAppOrderActDetails(String wmAppOrderActDetails) {
        this.wmAppOrderActDetails = wmAppOrderActDetails;
    }
}
