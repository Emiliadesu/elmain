package me.zhengjie.support.queenshop;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;

public class QSOrderListChild {

    @JSONField(name = "subOrderNo")
    private String subOrderNo;

    @JSONField(name = "goodsId")
    private String goodsId;

    @JSONField(name = "supSkuNo")
    private String supSkuNo;

    @JSONField(name = "goodsName")
    private String goodsName;

    @JSONField(name = "skuName")
    private String skuName;

    @JSONField(name = "wmsId")
    private String wmsId;

    @JSONField(name = "wmsName")
    private String wmsName;

    @JSONField(name = "brand")
    private String brand;

    @JSONField(name = "settlePrice")
    private Double settlePrice;

    @JSONField(name = "num")
    private Integer num;

    @JSONField(name = "price")
    private Double price;

    @JSONField(name = "refundStatus")
    private String refundStatus;

    public String getSubOrderNo() {
        return subOrderNo;
    }

    public void setSubOrderNo(String subOrderNo) {
        this.subOrderNo = subOrderNo;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getSupSkuNo() {
        return supSkuNo;
    }

    public void setSupSkuNo(String supSkuNo) {
        this.supSkuNo = supSkuNo;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getWmsId() {
        return wmsId;
    }

    public void setWmsId(String wmsId) {
        this.wmsId = wmsId;
    }

    public String getWmsName() {
        return wmsName;
    }

    public void setWmsName(String wmsName) {
        this.wmsName = wmsName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Double getSettlePrice() {
        return settlePrice;
    }

    public void setSettlePrice(Double settlePrice) {
        this.settlePrice = settlePrice;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }
}
