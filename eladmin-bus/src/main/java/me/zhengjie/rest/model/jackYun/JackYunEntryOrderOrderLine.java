package me.zhengjie.rest.model.jackYun;

public class JackYunEntryOrderOrderLine {
    /**
     * 商品行
     */
    private String orderLineNo;
    /**
     * 商品货主编码
     */
    private String ownerCode;
    /**
     * 商品编码
     */
    private String itemCode;
    /**
     * 仓储货号
     */
    private String itemId;
    /**
     * 品名
     */
    private String itemName;
    /**
     * 应收数量
     */
    private String planQty;
    /**
     * 商品类型
     */
    private String inventoryType;
    /**
     * 生产日期
     */
    private String productDate;
    /**
     * 失效日期
     */
    private String expireDate;
    /**
     * 生产批号
     */
    private String produceCode;
    /**
     * 批次编号
     */
    private String batchCode;
    /**
     * 单位
     */
    private String unit;

    public String getOrderLineNo() {
        return orderLineNo;
    }

    public void setOrderLineNo(String orderLineNo) {
        this.orderLineNo = orderLineNo;
    }

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getPlanQty() {
        return planQty;
    }

    public void setPlanQty(String planQty) {
        this.planQty = planQty;
    }

    public String getInventoryType() {
        return inventoryType;
    }

    public void setInventoryType(String inventoryType) {
        this.inventoryType = inventoryType;
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

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
