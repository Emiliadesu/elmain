package me.zhengjie.rest.model.meituan;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;

public class MeiTuanOrderDetail {
    /**
     * 商品现价，单位是元
     */
    @JSONField(name = "actual_price")
    private BigDecimal actualPrice;

    /**
     *商品原价，单位是元
     */
    @JSONField(name = "original_price")
    private BigDecimal originalPrice;

    /**
     *APP方商品id，即商家中台系统里商品的编码(spu_code值)：
     * (1)不同门店之间商品id可以重复，同一门店内商品id不允许重复。
     * (2)字段信息限定长度不超过128个字符。
     * (3)如此字段信息推送的是商品名称或信息为空，则表示商家没有维护商品编码，请商家自行维护。
     */
    @JSONField(name = "app_food_code")
    private String appFoodCode;

    /**
     * 商品名称
     */
    @JSONField(name = "food_name")
    private String foodName;

    /**
     *SKU码(商家的规格编码)，是商品sku唯一标识码。
     */
    @JSONField(name = "sku_id")
    private String skuId;

    /**
     * 订单中此商品sku的购买数量
     */
    private Integer quantity;

    /**
     * 商品单价，单位是元。
     * 此字段信息默认推送活动折扣后价格，商家如有需求将价格替换为原价，可在开发者中心->基础设置->订单订阅字段 页面开启订阅字段“18 替换菜品折扣价格为原价”。
     */
    private BigDecimal price;

    public BigDecimal getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(BigDecimal actualPrice) {
        this.actualPrice = actualPrice;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getAppFoodCode() {
        return appFoodCode;
    }

    public void setAppFoodCode(String appFoodCode) {
        this.appFoodCode = appFoodCode;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
