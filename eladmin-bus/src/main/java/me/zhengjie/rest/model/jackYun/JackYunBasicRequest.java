package me.zhengjie.rest.model.jackYun;

public class JackYunBasicRequest {
    /**
     * 仓库编码
     */
    private String warehouseCode;
    /**
     * 货主编码
     */
    private String ownerCode;

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }
}
