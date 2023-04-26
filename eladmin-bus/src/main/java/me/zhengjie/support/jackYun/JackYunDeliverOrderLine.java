package me.zhengjie.support.jackYun;

import java.util.List;

public class JackYunDeliverOrderLine {
    /**
     * 单据行号
     * F
     */
    private String orderLineNo;
    /**
     * 平台交易订单号
     */
    private String orderSourceCode;
    /**
     * 交易子单号
     */
    private String subSourceCode;
    /**
     * 商品编码
     */
    private String itemCode;
    /**
     * 商品仓储编码
     */
    private String itemId;
    /**
     * 库存类型
     */
    private String inventoryType;
    /**
     * 货主编码
     */
    private String ownerCode;
    /**
     * 品名
     */
    private String itemName;
    /**
     * 交易平台商品编码
     */
    private String extCode;
    /**
     * 应发数量
     */
    private Integer planQty;
    /**
     * 实发数量
     */
    private Integer actualQty;
    /**
     * 批次编号
     */
    private String batchCode;
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
     * 批次列表
     */
    private List<JackYunDeliverBatch>batchs;
    /**
     * 商品二维码
     */
    private String qrCode;
    /**
     * 商品SN码
     */
    private String snCode;
    /**
     * SN列表
     */
    private JackYunDeliverSnList snList;

    public String getOrderLineNo() {
        return orderLineNo;
    }

    public void setOrderLineNo(String orderLineNo) {
        this.orderLineNo = orderLineNo;
    }

    public String getOrderSourceCode() {
        return orderSourceCode;
    }

    public void setOrderSourceCode(String orderSourceCode) {
        this.orderSourceCode = orderSourceCode;
    }

    public String getSubSourceCode() {
        return subSourceCode;
    }

    public void setSubSourceCode(String subSourceCode) {
        this.subSourceCode = subSourceCode;
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

    public String getInventoryType() {
        return inventoryType;
    }

    public void setInventoryType(String inventoryType) {
        this.inventoryType = inventoryType;
    }

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getExtCode() {
        return extCode;
    }

    public void setExtCode(String extCode) {
        this.extCode = extCode;
    }

    public Integer getPlanQty() {
        return planQty;
    }

    public void setPlanQty(Integer planQty) {
        this.planQty = planQty;
    }

    public Integer getActualQty() {
        return actualQty;
    }

    public void setActualQty(Integer actualQty) {
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

    public String getProduceCode() {
        return produceCode;
    }

    public void setProduceCode(String produceCode) {
        this.produceCode = produceCode;
    }

    public List<JackYunDeliverBatch> getBatchs() {
        return batchs;
    }

    public void setBatchs(List<JackYunDeliverBatch> batchs) {
        this.batchs = batchs;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getSnCode() {
        return snCode;
    }

    public void setSnCode(String snCode) {
        this.snCode = snCode;
    }

    public JackYunDeliverSnList getSnList() {
        return snList;
    }

    public void setSnList(JackYunDeliverSnList snList) {
        this.snList = snList;
    }
}
