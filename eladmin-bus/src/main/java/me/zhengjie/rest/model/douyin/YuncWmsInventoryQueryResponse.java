package me.zhengjie.rest.model.douyin;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;
import java.util.Objects;

public class YuncWmsInventoryQueryResponse {
    /**
     * 总数量
     */
    @JSONField(name = "total")
    private Long total;

    /**
     * 当前页
     */
    @JSONField(name = "page")
    private Integer page;

    /**
     * 偏移量
     */
    @JSONField(name = "page_size")
    private Integer pageSize;

    /**
     * 列表
     */
    @JSONField(name = "list")
    private List<InventoryData>list;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<InventoryData> getList() {
        return list;
    }

    public void setList(List<InventoryData> list) {
        this.list = list;
    }
    public static class InventoryData{
        /**
         * 仓code
         */
        @JSONField(name = "warehouse_code")
        private String warehouseCode;

        /**
         * 货主编码
         */
        @JSONField(name = "owner_code")
        private String ownerCode;

        /**
         * 货品编码
         */
        @JSONField(name = "cargo_code")
        private String cargoCode;

        /**
         * 良品库存数量
         */
        @JSONField(name = "good_qty")
        private Integer goodQty;

        /**
         * 次品库存数量
         */
        @JSONField(name = "defective_qty")
        private Integer defectiveQty;

        /**
         * 接口时间
         */
        @JSONField(name = "api_time")
        private Long apiTime;

        /**
         * 库存明细
         */
        @JSONField(name = "details")
        private List<Detail>details;

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

        public String getCargoCode() {
            return cargoCode;
        }

        public void setCargoCode(String cargoCode) {
            this.cargoCode = cargoCode;
        }

        public Integer getGoodQty() {
            return goodQty;
        }

        public void setGoodQty(Integer goodQty) {
            this.goodQty = goodQty;
        }

        public Integer getDefectiveQty() {
            return defectiveQty;
        }

        public void setDefectiveQty(Integer defectiveQty) {
            this.defectiveQty = defectiveQty;
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
    }
    public static class Detail{
        /**
         * 库存数量
         */
        @JSONField(name = "qty")
        private Integer qty;

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
         * 批次号
         */
        @JSONField(name = "batch_number")
        private String batchNumber;

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
         * 入库日期
         */
        @JSONField(name = "receipt_date")
        private Long receiptDate;

        /**
         * 供应商id
         */
        @JSONField(name = "supplier_id")
        private String supplierId;

        /**
         * 入库单号
         */
        @JSONField(name = "scm_inbound_order")
        private String scmInboundOrder;

        public Integer getQty() {
            return qty;
        }

        public void setQty(Integer qty) {
            this.qty = qty;
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

        public String getSupplierId() {
            return supplierId;
        }

        public void setSupplierId(String supplierId) {
            this.supplierId = supplierId;
        }

        public String getScmInboundOrder() {
            return scmInboundOrder;
        }

        public void setScmInboundOrder(String scmInboundOrder) {
            this.scmInboundOrder = scmInboundOrder;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Detail detail = (Detail) o;
            return inventoryType.equals(detail.inventoryType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(inventoryType);
        }
    }
}
