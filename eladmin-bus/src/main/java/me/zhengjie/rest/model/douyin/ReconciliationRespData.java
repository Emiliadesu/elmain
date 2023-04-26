package me.zhengjie.rest.model.douyin;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class ReconciliationRespData {
    @JSONField(name = "cargo_inventory_list")
    List<CargoInventory>cargoInventoryList;

    public List<CargoInventory> getCargoInventoryList() {
        return cargoInventoryList;
    }

    public void setCargoInventoryList(List<CargoInventory> cargoInventoryList) {
        this.cargoInventoryList = cargoInventoryList;
    }
}
