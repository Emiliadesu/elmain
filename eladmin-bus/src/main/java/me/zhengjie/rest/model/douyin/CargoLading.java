package me.zhengjie.rest.model.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CargoLading {
    /**
     * 货品id
     */
    @JsonProperty(value = "cargo_code")
    @JSONField(name = "cargo_code")
    private String cargoCode;

    /**
     * 货品名称
     */
    @JsonProperty(value = "cargo_name")
    @JSONField(name = "cargo_name")
    private String cargoName;

    /**
     * 服务商货品编码
     */
    @JsonProperty(value = "external_code")
    @JSONField(name = "external_code")
    private String externalCode;

    /**
     * 质量等级
     */
    @JsonProperty(value = "quality_grade")
    @JSONField(name = "quality_grade")
    private Short qualityGrade;

    /**
     * 提货数量
     */
    private Integer quantity;

    /**
     * 条形码
     */
    @JsonProperty(value = "bar_code")
    @JSONField(name = "bar_code")
    private String barCode;

    /**
     * 备案记录id
     */
    @JsonProperty(value = "filing_record_id")
    @JSONField(name = "filing_record_id")
    private String filingRecordId;

    /**
     * 商品效期备注
     */
    @JsonProperty(value = "expiration_time_desc")
    @JSONField(name = "expiration_time_desc")
    private String expirationTimeDesc;

    /**
     * 商品批次
     */
    @JsonProperty(value = "batch_no")
    @JSONField(name = "batch_no")
    private String batchNo;

    /**
     * 单价
     */
    private String price;

    /**
     * 币种
     */
    private String currency;

    public String getCargoCode() {
        return cargoCode;
    }

    public void setCargoCode(String cargoCode) {
        this.cargoCode = cargoCode;
    }

    public String getCargoName() {
        return cargoName;
    }

    public void setCargoName(String cargoName) {
        this.cargoName = cargoName;
    }

    public String getExternalCode() {
        return externalCode;
    }

    public void setExternalCode(String externalCode) {
        this.externalCode = externalCode;
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

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getFilingRecordId() {
        return filingRecordId;
    }

    public void setFilingRecordId(String filingRecordId) {
        this.filingRecordId = filingRecordId;
    }

    public String getExpirationTimeDesc() {
        return expirationTimeDesc;
    }

    public void setExpirationTimeDesc(String expirationTimeDesc) {
        this.expirationTimeDesc = expirationTimeDesc;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
