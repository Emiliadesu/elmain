package me.zhengjie.support.ruoYuChen.request;

import java.util.List;

public class RuoYuChenInboundOrderBackDetail {
    /**
     * 单据行号
     * N
     */
    private String orderLineNo;
    /**
     *商品编码
     * Y
     */
    private String itemCode;
    /**
     * 商品名称
     * N
     */
    private String itemName;
    /**
     * 应收数量
     * N
     */
    private String planQty;
    /**
     * 库存类型
     * (ZP=正品;CC=残次;
     * JS=机损;XS=箱损;默认为ZP)
     * 有batchs节点，此值可为空
     * Y
     */
    private String inventoryType;
    /**
     *实收数量
     * Y
     */
    private String actualQty;
    /**
     * 批次编码 有batchs节点，此值可为空
     * N
     */
    private String batchCode;
    /**
     * 生产日期 有batchs节点，此值可为空
     * N
     */
    private String productDate;
    /**
     * 失效日期 有batchs节点，此值可为空
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
    /**
     * 批次列表
     */
    private List<RuoYuChenOrderBackBatch> batchs;

    public String getOrderLineNo() {
        return orderLineNo;
    }

    public void setOrderLineNo(String orderLineNo) {
        this.orderLineNo = orderLineNo;
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

    public String getActualQty() {
        return actualQty;
    }

    public void setActualQty(String actualQty) {
        this.actualQty = actualQty;
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

    public List<RuoYuChenOrderBackBatch> getBatchs() {
        return batchs;
    }

    public void setBatchs(List<RuoYuChenOrderBackBatch> batchs) {
        this.batchs = batchs;
    }
}
