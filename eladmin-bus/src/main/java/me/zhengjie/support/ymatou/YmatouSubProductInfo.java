package me.zhengjie.support.ymatou;

import com.alibaba.fastjson.annotation.JSONField;

public class YmatouSubProductInfo {
    /**
     * skuId
     */
    @JSONField(name = "sku_id")
    private String skuId;
    /**
     * 子商品SkuId
     */
    @JSONField(name = "sub_sku_id")
    private String subSkuId;
    /**
     * 子商品编码
     */
    @JSONField(name = "sub_outer_sku_id")
    private String subOuterSkuId;
    /**
     * 子商品名称
     */
    @JSONField(name = "sub_title")
    private String subTitle;
    /**
     * 子商品价格
     */
    private Double price;
    /**
     * 子商品数量
     */
    private Integer num;

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getSubSkuId() {
        return subSkuId;
    }

    public void setSubSkuId(String subSkuId) {
        this.subSkuId = subSkuId;
    }

    public String getSubOuterSkuId() {
        return subOuterSkuId;
    }

    public void setSubOuterSkuId(String subOuterSkuId) {
        this.subOuterSkuId = subOuterSkuId;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
