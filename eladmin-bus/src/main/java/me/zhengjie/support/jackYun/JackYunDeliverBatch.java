package me.zhengjie.support.jackYun;

public class JackYunDeliverBatch {
    /**
     * 批次编号
     */
    private String batchCode;
    /**
     * 生产日期
     */
    private String  productDate;
    /**
     * 失效日期
     */
    private String expireDate;
    /**
     * 生产批号
     */
    private String produceCode;
    /**
     * 库存类型
     */
    private String inventoryType;
    /**
     * 实发数量
     */
    private Integer actualQty;
    /**
     * SN号
     */
    private String snCode;

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

    public String getProduceCode() {
        return produceCode;
    }

    public void setProduceCode(String produceCode) {
        this.produceCode = produceCode;
    }

    public String getInventoryType() {
        return inventoryType;
    }

    public void setInventoryType(String inventoryType) {
        this.inventoryType = inventoryType;
    }

    public Integer getActualQty() {
        return actualQty;
    }

    public void setActualQty(Integer actualQty) {
        this.actualQty = actualQty;
    }

    public String getSnCode() {
        return snCode;
    }

    public void setSnCode(String snCode) {
        this.snCode = snCode;
    }
}
