package me.zhengjie.rest.model.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Reconciliation {
    /**
     * 仓库编码
     */
    @JSONField(name = "warehouse_code")
    @SerializedName("warehouse_code")
    private String warehouseCode;

    /**
     * 货品列表
     */
    @JSONField(name = "cargo_list")
    @SerializedName("cargo_list")
    private List<Cargo> cargoList;

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public List<Cargo> getCargoList() {
        return cargoList;
    }

    public void setCargoList(List<Cargo> cargoList) {
        this.cargoList = cargoList;
    }
}
