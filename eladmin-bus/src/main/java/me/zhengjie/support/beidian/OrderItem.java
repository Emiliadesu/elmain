package me.zhengjie.support.beidian;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;

public class OrderItem {
    /**
     * 商品skuId
     */
    @JSONField(name = "sku_id")
    private String skuId;
    /**
     * 商品Id
     */
    @JSONField(name = "iid")
    private Integer iid;
    /**
     * 商品编码
     */
    @JSONField(name = "outer_id")
    private String outerId;
    /**
     * 商品货号
     */
    @JSONField(name = "goods_num")
    private String goodsNum;
    /**
     * 商品价格
     */
    @JSONField(name = "price")
    private BigDecimal price;
    /**
     * 商品原价
     */
    @JSONField(name = "origin_price")
    private BigDecimal originPrice;
    /**
     * 购买该商品的数量
     */
    @JSONField(name = "num")
    private Integer num;
    /**
     * 小计item.price*item.num-平摊优惠(贝贝现金券+积分)
     */
    @JSONField(name = "subtotal")
    private BigDecimal subtotal;
    /**
     * 该商品的商家实际所得（经过优惠平摊）
     */
    @JSONField(name = "total_fee")
    private BigDecimal totalFee;
    /**
     * 申报价
     */
    @JSONField(name = "declare_amount")
    private BigDecimal declareAmount;
    /**
     * 关税
     */
    @JSONField(name = "tariff_fee")
    private BigDecimal tariffFee;
    /**
     * 增值税
     */
    @JSONField(name = "addedvalue_fee")
    private BigDecimal addedvalueFee;
    /**
     * 消费税
     */
    @JSONField(name = "consump_fee")
    private BigDecimal consumpFee;

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public Integer getIid() {
        return iid;
    }

    public void setIid(Integer iid) {
        this.iid = iid;
    }

    public String getOuterId() {
        return outerId;
    }

    public void setOuterId(String outerId) {
        this.outerId = outerId;
    }

    public String getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(String goodsNum) {
        this.goodsNum = goodsNum;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getOriginPrice() {
        return originPrice;
    }

    public void setOriginPrice(BigDecimal originPrice) {
        this.originPrice = originPrice;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public BigDecimal getTariffFee() {
        return tariffFee;
    }

    public void setTariffFee(BigDecimal tariffFee) {
        this.tariffFee = tariffFee;
    }

    public BigDecimal getAddedvalueFee() {
        return addedvalueFee;
    }

    public void setAddedvalueFee(BigDecimal addedvalueFee) {
        this.addedvalueFee = addedvalueFee;
    }

    public BigDecimal getConsumpFee() {
        return consumpFee;
    }

    public void setConsumpFee(BigDecimal consumpFee) {
        this.consumpFee = consumpFee;
    }

    public BigDecimal getDeclareAmount() {
        return declareAmount;
    }

    public void setDeclareAmount(BigDecimal declareAmount) {
        this.declareAmount = declareAmount;
    }
}
