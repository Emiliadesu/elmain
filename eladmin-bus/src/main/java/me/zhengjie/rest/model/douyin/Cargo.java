package me.zhengjie.rest.model.douyin;

import com.alibaba.fastjson.annotation.JSONField;

public class Cargo{
    /**
     * 店铺编码
     */
    @JSONField(name = "shop_id")

    private Long shopId;

    /**
     * 抖音货品编码
     */
    @JSONField(name = "cargo_code")
    private String cargoCode;

    /**
     * 服务商货品编码
     */
    @JSONField(name = "external_code")
    private String externalCode;

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getCargoCode() {
        return cargoCode;
    }

    public void setCargoCode(String cargoCode) {
        this.cargoCode = cargoCode;
    }

    public String getExternalCode() {
        return externalCode;
    }

    public void setExternalCode(String externalCode) {
        this.externalCode = externalCode;
    }
}
