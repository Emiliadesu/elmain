package me.zhengjie.rest.model.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CargoInbound {
    /**
     * 货品编码
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
     * 发货数量
     */
    private Integer quantity;

    /**
     * 条形码
     */
    @JsonProperty(value = "bar_code")
    @JSONField(name = "bar_code")
    private String barCode;

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
}
