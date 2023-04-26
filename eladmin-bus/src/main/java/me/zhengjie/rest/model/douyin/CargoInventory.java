package me.zhengjie.rest.model.douyin;

import com.alibaba.fastjson.annotation.JSONField;

public class CargoInventory {
    /**
     * 商家编码
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

    /**
     * 货品良品结余库存
     */
    @JSONField(name = "good_inventory_qty")
    private Integer goodInventoryQty;

    /**
     * 货品次品结余库存
     */
    @JSONField(name = "defective_good_qty")
    private Integer defectiveGoodQty;

    /**
     * 实际对账时间，秒级
     */
    @JSONField(name = "actualReconciliationTime")
    private Long actualReconciliationTime;

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

    public Integer getGoodInventoryQty() {
        return goodInventoryQty;
    }

    public void setGoodInventoryQty(Integer goodInventoryQty) {
        this.goodInventoryQty = goodInventoryQty;
    }

    public Integer getDefectiveGoodQty() {
        return defectiveGoodQty;
    }

    public void setDefectiveGoodQty(Integer defectiveGoodQty) {
        this.defectiveGoodQty = defectiveGoodQty;
    }

    public Long getActualReconciliationTime() {
        return actualReconciliationTime;
    }

    public void setActualReconciliationTime(Long actualReconciliationTime) {
        this.actualReconciliationTime = actualReconciliationTime;
    }
}
