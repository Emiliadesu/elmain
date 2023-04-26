package me.zhengjie.rest.model.meituan;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;

public class WmAppOrderActDetail {
    @JSONField(name = "act_id")
    private Long actId;

    private Integer count;

    private BigDecimal mtCharge;

    private BigDecimal poiCharge;

    private String remark;

    @JSONField(name = "sku_act_id")
    private Long skuActId;

    private Integer type;

    public Long getActId() {
        return actId;
    }

    public void setActId(Long actId) {
        this.actId = actId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public BigDecimal getMtCharge() {
        return mtCharge;
    }

    public void setMtCharge(BigDecimal mtCharge) {
        this.mtCharge = mtCharge;
    }

    public BigDecimal getPoiCharge() {
        return poiCharge;
    }

    public void setPoiCharge(BigDecimal poiCharge) {
        this.poiCharge = poiCharge;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getSkuActId() {
        return skuActId;
    }

    public void setSkuActId(Long skuActId) {
        this.skuActId = skuActId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
