package me.zhengjie.rest.model.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author luob
 * @description 抖音销退明细
 * @date 2023/1/7
 */
public class SaleReturnOrderDetail {

    /**
     * 平台商品sku编号
     */
    @JsonProperty(value = "sku_id")
    @JSONField(name = "sku_id")
    private Long skuId;

    /**
     * 行号
     */
    @JsonProperty(value = "line_no")
    @JSONField(name = "line_no")
    private String lineNo;

    /**
     * 外部服务商货品编码
     */
    @JsonProperty(value = "item_no")
    @JSONField(name = "item_no")
    private String itemNo;

    /**
     * 货品id，不同行货品id有可能相同
     */
    @JsonProperty(value = "item_id")
    @JSONField(name = "item_id")
    private String itemId;

    /**
     * 条形码
     */
    @JsonProperty(value = "bar_code")
    @JSONField(name = "bar_code")
    private String barCode;

    /**
     * 商品名称
     */
    @JsonProperty(value = "item_name")
    @JSONField(name = "item_name")
    private String itemName;

    /**
     * 实际数量
     */
    @JsonProperty(value = "quantity")
    @JSONField(name = "quantity")
    private Long quantity;

    /**
     * 商品名称
     */
    @JsonProperty(value = "inventory_type")
    @JSONField(name = "inventory_type")
    private String inventoryType;

    /**
     * 批次号，按照2C出库回告数据下发，否则为空
     */
    @JsonProperty(value = "batch_number")
    @JSONField(name = "batch_number")
    private String batchNumber;

    /**
     * 生产日期，按照2C出库回告数据下发，否则为空
     */
    @JsonProperty(value = "product_date")
    @JSONField(name = "product_date")
    private Long productDate;

    /**
     * 失效日期，按照2C出库回告数据下发，否则为空
     */
    @JsonProperty(value = "expire_date")
    @JSONField(name = "expire_date")
    private Long expireDate;

    /**
     * 入库日期，按照2C出库回告数据下发，否则为空
     */
    @JsonProperty(value = "receipt_date")
    @JSONField(name = "receipt_date")
    private Long receiptDate;

    /**
     * SCM入库单号，按照2C出库回告数据下发，否则为空
     */
    @JsonProperty(value = "scm_inbound_order")
    @JSONField(name = "scm_inbound_order")
    private String scmInboundOrder;

    /**
     * 供应商id，保税备货为空
     */
    @JsonProperty(value = "supplier_id")
    @JSONField(name = "supplier_id")
    private String supplierId;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public String getLineNo() {
        return lineNo;
    }

    public void setLineNo(String lineNo) {
        this.lineNo = lineNo;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getInventoryType() {
        return inventoryType;
    }

    public void setInventoryType(String inventoryType) {
        this.inventoryType = inventoryType;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public Long getProductDate() {
        return productDate;
    }

    public void setProductDate(Long productDate) {
        this.productDate = productDate;
    }

    public Long getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Long expireDate) {
        this.expireDate = expireDate;
    }

    public Long getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(Long receiptDate) {
        this.receiptDate = receiptDate;
    }

    public String getScmInboundOrder() {
        return scmInboundOrder;
    }

    public void setScmInboundOrder(String scmInboundOrder) {
        this.scmInboundOrder = scmInboundOrder;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }
}
