package me.zhengjie.support.moGuJie;

import me.zhengjie.support.CommonApiParam;

public class MogujieLogisticsSendRequest implements CommonApiParam {
    /**
     * 发货类型（物流发货：0，虚拟发货：1）
     */
    private Integer shipType=0;
    /**
     * 店铺订单ID
     */
    private Long shopOrderId;
    /**
     * 快递编码
     */
    private String expressCode="zhongtong";
    /**
     * 运单号
     */
    private String expressId;
    /**
     * 是否拆单
     */
    private Boolean needSplit=false;

    public Integer getShipType() {
        return shipType;
    }

    public void setShipType(Integer shipType) {
        this.shipType = shipType;
    }

    public Long getShopOrderId() {
        return shopOrderId;
    }

    public void setShopOrderId(Long shopOrderId) {
        this.shopOrderId = shopOrderId;
    }

    public String getExpressCode() {
        return expressCode;
    }

    public void setExpressCode(String expressCode) {
        this.expressCode = expressCode;
    }

    public String getExpressId() {
        return expressId;
    }

    public void setExpressId(String expressId) {
        this.expressId = expressId;
    }

    public Boolean getNeedSplit() {
        return needSplit;
    }

    public void setNeedSplit(Boolean needSplit) {
        this.needSplit = needSplit;
    }

    @Override
    public String getMethod() {
        return "xiaodian.logistics.online.send";
    }

    @Override
    public String getKeyWord() {
        return String.valueOf(getShopOrderId());
    }
}
