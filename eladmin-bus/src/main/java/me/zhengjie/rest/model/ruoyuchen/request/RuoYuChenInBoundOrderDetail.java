package me.zhengjie.rest.model.ruoyuchen.request;

public class RuoYuChenInBoundOrderDetail {
    /**
     * 单据行号
     */
    private String orderLineNo;
    /**
     * 交易单号
     */
    private String sourceOrderCode;
    /**
     * 商品编码
     */
    private String itemCode;
    /**
     * 商品名称
     */
    private String itemName;
    /**
     * 应收商品数量
     */
    private String planQty;
    /**
     * 商品属性
     */
    private String skuProperty;
    /**
     * 采购价
     */
    private String purchasePrice;
    /**
     * 零售价
     */
    private String retailPrice;
    /**
     * 库存类型
     */
    private String inventoryType;
    /**
     * 批次编码
     */
    private String batchCode;
    /**
     * 生产日期YYYY-MM-dd
     */
    private String productDate;
    /**
     * 失效日期YYYY-MM-dd
     */
    private String expireDate;
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

    public String getSkuProperty() {
        return skuProperty;
    }

    public void setSkuProperty(String skuProperty) {
        this.skuProperty = skuProperty;
    }

    public String getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(String purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(String retailPrice) {
        this.retailPrice = retailPrice;
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
}
