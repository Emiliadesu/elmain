package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

import java.util.List;

public class YuncSyncInventoryLogFlow implements CommonApiParam {
    @Override
    public String getMethod() {
        return "yunc.syncInventoryLogFlowV2";
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
     * 操作时间/发生时间 yyyy-MM-dd 对应库存快照业务时间,精确到天
     */
    @JSONField(name = "snapshot_time")
    private String snapshotTime;

    /**
     * 接口请求时间，秒级时间戳
     */
    @JSONField(name = "api_time")
    private Long apiTime;

    /**
     * 库存流水明细
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

    public String getSnapshotTime() {
        return snapshotTime;
    }

    public void setSnapshotTime(String snapshotTime) {
        this.snapshotTime = snapshotTime;
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
         * 业务发生时间,秒级
         */
        @JSONField(name = "occur_time")
        private Long occurTime;

        /**
         * 货品编码
         */
        @JSONField(name = "cargo_code")
        private String cargoCode;

        /**
         * 1-普通货品 2-包材 3-耗材
         */
        @JSONField(name = "cargo_type")
        private Integer cargoType;

        /**
         * 库存类型 1-正品 2-残品
         */
        @JSONField(name = "inventory_type")
        private Integer inventoryType;

        /**
         * 库存状态 1-正常 2-质检 3-冻结
         */
        @JSONField(name = "inventory_status")
        private Integer inventoryStatus;

        /**
         * 批次号
         */
        @JSONField(name = "lot_no")
        private String lotNo;

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
         * 供应商
         */
        @JSONField(name = "supplier_id")
        private Long supplierId;

        /**
         * 入库单号
         */
        @JSONField(name = "scm_inbound_order")
        private String scmInboundOrder;
        /**
         * 订单类型
         * 采购入库:PURCHASE_IN;
         * 销退入库:SALE_RETURN_IN;
         * 调拨入库:TRANSFER_IN;
         * 其他入库:OTHER_IN;
         * 销售出库:SALE_OUT;
         * 退仓出库:RTV_OUT;
         * 调拨出库:TRANSFER_OUT;
         * 加工出库:PROCESS_OUT;
         * 加工入库:PROCESS_IN;
         * 转移入库:TRANSFER_PLUS;
         * 转移出库:TRANSFER_REDUCE;
         * 库存调整:INVENTORY_ADJUST
         */
        @JSONField(name = "order_type")
        private String orderType;
        /**
         * 本流水对应的WMS端的入库/出库/库调单号
         */
        @JSONField(name = "wms_order_no")
        private String wmsOrderNo;

        /**
         * 本流水对应的上游的单号,入库单号(erp_order_no)、出库单号(erp_order_no)
         */
        @JSONField(name = "erp_order_no")
        private String erpOrderNo;

        /**
         * 变更数量 可负
         */
        @JSONField(name = "change_qty")
        private Integer changeQty;

        /**
         * 单位, 现均默认"件"
         */
        @JSONField(name = "unit")
        private String unit;

        public Long getOccurTime() {
            return occurTime;
        }

        public void setOccurTime(Long occurTime) {
            this.occurTime = occurTime;
        }

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

        public String getLotNo() {
            return lotNo;
        }

        public void setLotNo(String lotNo) {
            this.lotNo = lotNo;
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

        public Long getSupplierId() {
            return supplierId;
        }

        public void setSupplierId(Long supplierId) {
            this.supplierId = supplierId;
        }

        public String getScmInboundOrder() {
            return scmInboundOrder;
        }

        public void setScmInboundOrder(String scmInboundOrder) {
            this.scmInboundOrder = scmInboundOrder;
        }

        public String getOrderType() {
            return orderType;
        }

        public void setOrderType(String orderType) {
            this.orderType = orderType;
        }

        public String getWmsOrderNo() {
            return wmsOrderNo;
        }

        public void setWmsOrderNo(String wmsOrderNo) {
            this.wmsOrderNo = wmsOrderNo;
        }

        public String getErpOrderNo() {
            return erpOrderNo;
        }

        public void setErpOrderNo(String erpOrderNo) {
            this.erpOrderNo = erpOrderNo;
        }

        public Integer getChangeQty() {
            return changeQty;
        }

        public void setChangeQty(Integer changeQty) {
            this.changeQty = changeQty;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }
    }
}
