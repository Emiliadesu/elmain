package me.zhengjie.support.fuliPre;

import java.math.BigDecimal;

public class BaseSkuPackInfo {
    private String goodsNo;
    private String barcode;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal weight;
    private BigDecimal packLength;
    private BigDecimal packWidth;
    private BigDecimal packHeight;
    private BigDecimal packWeight;
    private Integer packNum;

    public String getGoodsNo() {
        return goodsNo;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo;
    }

    public BigDecimal getLength() {
        return length;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }

    public BigDecimal getWidth() {
        return width;
    }

    public void setWidth(BigDecimal width) {
        this.width = width;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getPackLength() {
        return packLength;
    }

    public void setPackLength(BigDecimal packLength) {
        this.packLength = packLength;
    }

    public BigDecimal getPackWidth() {
        return packWidth;
    }

    public void setPackWidth(BigDecimal packWidth) {
        this.packWidth = packWidth;
    }

    public BigDecimal getPackHeight() {
        return packHeight;
    }

    public void setPackHeight(BigDecimal packHeight) {
        this.packHeight = packHeight;
    }

    public BigDecimal getPackWeight() {
        return packWeight;
    }

    public void setPackWeight(BigDecimal packWeight) {
        this.packWeight = packWeight;
    }

    public Integer getPackNum() {
        return packNum;
    }

    public void setPackNum(Integer packNum) {
        this.packNum = packNum;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
