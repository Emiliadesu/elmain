package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;

public class OutboundCargoInfo {
    /**
     * 货品编码
     */
    @JSONField(name = "cargo_code")
    private String cargoCode;

    /**
     * 质量等级
     */
    @JSONField(name = "quality_grade")
    private Short qualityGrade;

    /**
     * 货品数量
     */
    private Integer quantity;

    public String getCargoCode() {
        return cargoCode;
    }

    public void setCargoCode(String cargoCode) {
        this.cargoCode = cargoCode;
    }

    public Short getQualityGrade() {
        return qualityGrade;
    }

    public void setQualityGrade(Short qualityGrade) {
        this.qualityGrade = qualityGrade;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
