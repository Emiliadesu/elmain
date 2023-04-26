package me.zhengjie.rest.model.ruoyuchen.request;

public class RuoYuChenOutBoundOrderDetail {
    /**
     * 单据行号
     * N
     */
    private String orderLineNo;
    /**
     * 交易单号
     * N
     */
    private String sourceOrderCode;
    /**
     * 商品编码
     * Y
     */
    private String itemCode;
    /**
     * 商品名称
     * N
     */
    private String itemName;
    /**
     * 应退商品数量
     * Y
     */
    private String planQty;
    /**
     * 库存类型
     * Y
     */
    private String inventoryType;
    /**
     * 批次编码
     * N
     */
    private String batchCode;
    /**
     * 生产日期YYYY-MM-dd
     * N
     */
    private String productDate;
    /**
     * 失效日期YYYY-MM-dd
     * N
     */
    private String expireDate;
    /**
     * 单位
     * N
     */
    private String unit;
    /**
     * 备注
     * N
     */
    private String remark;

    public String getOrderLineNo() {
        return orderLineNo;
    }

    public void setOrderLineNo(String orderLineNo) {
        this.orderLineNo = orderLineNo;
    }

    public String getSourceOrderCode() {
        return sourceOrderCode;
    }

    public void setSourceOrderCode(String sourceOrderCode) {
        this.sourceOrderCode = sourceOrderCode;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
