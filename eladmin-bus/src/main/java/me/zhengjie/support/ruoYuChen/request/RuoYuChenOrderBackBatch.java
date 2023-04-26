package me.zhengjie.support.ruoYuChen.request;

public class RuoYuChenOrderBackBatch {
    /**
     * 批次编号
     * N
     */
    private String batchCode;
    /**
     * 生产日期 yyyy-MM-dd
     * N
     */
    private String productDate;
    /**
     * 失效日期 yyyy-MM-dd
     */
    private String expireDate;
    /**
     * 库存类型
     * N
     * (ZP=正品;CC=残次;
     * JS=机损;XS=箱损;
     * ZT=在途库存;
     */
    private String inventoryType;
    /**
     * 实收数量
     */
    private String actualQty;

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getProductDate() {
        return productDate;
    }

    public void setProductDate(String productDate) {
        this.productDate = productDate;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getInventoryType() {
        return inventoryType;
    }

    public void setInventoryType(String inventoryType) {
        this.inventoryType = inventoryType;
    }

    public String getActualQty() {
        return actualQty;
    }

    public void setActualQty(String actualQty) {
        this.actualQty = actualQty;
    }
}
