package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

import java.util.List;

public class YuncSyncInventorySnapshot implements CommonApiParam {
    @Override
    public String getMethod() {
        return "yunc.syncInventorySnapshotV2";
    }

    @Override
    public String getKeyWord() {
        return this.idempotentNo;
    }

    /**
     * 幂等键
     */
    @JSONField(name = "idempotent_no")
    private String idempotentNo;

    /**
     * 总行数
     */
    @JSONField(name = "total_lines")
    private Integer totalLines;

    /**
     * 当前页，一次性回传的话，传1
     */
    @JSONField(name = "current_page")
    private Integer currentPage;

    /**
     * 当前页数量，一次性回传的话，值等于总行数
     */
    @JSONField(name = "page_size")
    private Integer pageSize;

    /**
     * 发生时间，秒级时间戳
     */
    @JSONField(name = "occur_time")
    private Long occurTime;

    /**
     * 仓code
     */
    @JSONField(name = "warehouse_code")
    private String warehouseCode;

    /**
     * 货主code
     */
    @JSONField(name = "owner_code")
    private String ownerCode;

    /**
     * 接口请求时间，秒级时间戳
     */
    @JSONField(name = "api_time")
    private Long apiTime;

    /**
     * 明细
     */
    @JSONField(name = "details")
    private List<Detail> details;

    @JSONField(name = "vendor_no")
    private String vendorNo;

    @JSONField(name = "shop_id_out")
    private String shopIdOut;

    public String getIdempotentNo() {
        return idempotentNo;
    }

    public void setIdempotentNo(String idempotentNo) {
        this.idempotentNo = idempotentNo;
    }

    public Integer getTotalLines() {
        return totalLines;
    }

    public void setTotalLines(Integer totalLines) {
        this.totalLines = totalLines;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getOccurTime() {
        return occurTime;
    }

    public void setOccurTime(Long occurTime) {
        this.occurTime = occurTime;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public Long getApiTime() {
        return apiTime;
    }

    public void setApiTime(Long apiTime) {
        this.apiTime = apiTime;
    }

    public List<Detail> getDetails() {
        return details;
    }

    public void setDetails(List<Detail> details) {
        this.details = details;
    }

    public String getVendorNo() {
        return vendorNo;
    }

    public void setVendorNo(String vendorNo) {
        this.vendorNo = vendorNo;
    }

    public String getShopIdOut() {
        return shopIdOut;
    }

    public void setShopIdOut(String shopIdOut) {
        this.shopIdOut = shopIdOut;
    }

    public static class Detail{
        /**
         * 货品code
         */
        @JSONField(name = "cargo_code")
        private String cargoCode;
        /**
         * 货品类型，默认1
         */
        @JSONField(name = "cargo_type")
        private Integer cargoType;

        /**
         * 库存类型，1正品；2次品
         */
        @JSONField(name = "inventory_type")
        private Integer inventoryType;

        /**
         * 库存状态，1正常；2质检；3冻结
         */
        @JSONField(name = "inventory_status")
        private Integer inventoryStatus;

        /**
         * scm入库单号
         */
        @JSONField(name = "scm_inbound_order")
        private String scmInboundOrder;

        /**
         * 供应商id
         */
        @JSONField(name = "supplier_id")
        private String supplierId;

        /**
         * 生产日期
         */
        @JSONField(name = "product_date")
        private Long productDate;

        /**
         * 失效日期
         */
        @JSONField(name = "expire_date")
        private Long expireDate;

        /**
         * 入库时间
         */
        @JSONField(name = "receipt_date")
        private Long receiptDate;

        /**
         * 批次号
         */
        @JSONField(name = "batch_number")
        private String batchNumber;

        /**
         * 期初库存
         */
        @JSONField(name = "opening_inventory")
        private Double openingInventory;

        /**
         * 期末库存
         */
        @JSONField(name = "ending_inventory")
        private Double endingInventory;

        public String getCargoCode() {
            return cargoCode;
        }

        public void setCargoCode(String cargoCode) {
            this.cargoCode = cargoCode;
        }

        public Integer getCargoType() {
            return cargoType;
        }

        public void setCargoType(Integer cargoType) {
            this.cargoType = cargoType;
        }

        public Integer getInventoryType() {
            return inventoryType;
        }

        public void setInventoryType(Integer inventoryType) {
            this.inventoryType = inventoryType;
        }

        public Integer getInventoryStatus() {
            return inventoryStatus;
        }

        public void setInventoryStatus(Integer inventoryStatus) {
            this.inventoryStatus = inventoryStatus;
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

        public String getBatchNumber() {
            return batchNumber;
        }

        public void setBatchNumber(String batchNumber) {
            this.batchNumber = batchNumber;
        }

        public Double getOpeningInventory() {
            return openingInventory;
        }

        public void setOpeningInventory(Double openingInventory) {
            this.openingInventory = openingInventory;
        }

        public Double getEndingInventory() {
            return endingInventory;
        }

        public void setEndingInventory(Double endingInventory) {
            this.endingInventory = endingInventory;
        }
    }
}
