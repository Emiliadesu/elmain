package me.zhengjie.support.meituan;

import com.alibaba.fastjson.annotation.JSONField;

public class MeiTuanMedicineData {
    /**
     *APP方药品id，即商家中台系统里药品的编码（spu_code值）
     */
    @JSONField(name = "app_medicine_code")
    private String appMedicineCode;

    /**
     * 库存数
     */
    private Integer stock;

    /**
     * 商家仓库编码
     */
    @JSONField(name = "warehouse_code")
    private String warehouseCode;

    public String getAppMedicineCode() {
        return appMedicineCode;
    }

    public void setAppMedicineCode(String appMedicineCode) {
        this.appMedicineCode = appMedicineCode;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }
}
