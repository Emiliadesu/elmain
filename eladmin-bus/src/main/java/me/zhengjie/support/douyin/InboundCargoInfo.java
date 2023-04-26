package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;

public class InboundCargoInfo {
    /**
     * 货品编码
     */
    @JSONField(name = "cargo_code")
    private String cargoCode;

    /**
     * 实际上架数量
     */
    @JSONField(name = "actual_stack_count")
    private Integer actualStackCount;

    /**
     * 良品数量
     */
    @JSONField(name = "good_cargo_count")
    private Integer goodCargoCount;

    /**
     * 次品数量
     */
    @JSONField(name = "defective_cargo_count")
    private Integer defectiveCargoCount;

    /**
     * 失效日期
     * yyyy-MM-dd HH:mm:ss
     */
    @JSONField(name = "expiry_date")
    private String expiryDate;

    public String getCargoCode() {
        return cargoCode;
    }

    public void setCargoCode(String cargoCode) {
        this.cargoCode = cargoCode;
    }

    public Integer getActualStackCount() {
        return actualStackCount;
    }

    public void setActualStackCount(Integer actualStackCount) {
        this.actualStackCount = actualStackCount;
    }

    public Integer getGoodCargoCount() {
        return goodCargoCount;
    }

    public void setGoodCargoCount(Integer goodCargoCount) {
        this.goodCargoCount = goodCargoCount;
    }

    public Integer getDefectiveCargoCount() {
        return defectiveCargoCount;
    }

    public void setDefectiveCargoCount(Integer defectiveCargoCount) {
        this.defectiveCargoCount = defectiveCargoCount;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}
