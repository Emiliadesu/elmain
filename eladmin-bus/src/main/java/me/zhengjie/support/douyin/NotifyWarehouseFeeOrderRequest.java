package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

import java.util.List;

public class NotifyWarehouseFeeOrderRequest implements CommonApiParam {

    @Override
    public String getMethod() {
        return "crossBorder.notifyWarehouseFeeOrder";
    }

    @Override
    public String getKeyWord() {
        return this.wsStoreNo;
    }

    /**
     * 仓租单号
     */
    @JSONField(name = "ws_store_no")
    private String wsStoreNo;


    /**
     * 仓库编码
     */
    @JSONField(name = "warehouse_code")
    private String warehouseCode;

    /**
     * 计费日期。 十位秒级时间戳，需要截断时分秒，否则会被校验住报错。 如 1640966400 表示2022-01-01 00:00:00
     */
    @JSONField(name = "fee_date")
    private Long feeDate;

    /**
     * 是否需要计费
     */
    @JSONField(name = "need_charge")
    private Boolean needCharge;

    /**
     * 服务商编码
     */
    private String vendor;

    /**
     * 当仓租单需要计费时必传
     */
    @JSONField(name = "detail_list")
    private List<Detail>detailList;

    public String getWsStoreNo() {
        return wsStoreNo;
    }

    public void setWsStoreNo(String wsStoreNo) {
        this.wsStoreNo = wsStoreNo;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public Long getFeeDate() {
        return feeDate;
    }

    public void setFeeDate(Long feeDate) {
        this.feeDate = feeDate;
    }

    public Boolean getNeedCharge() {
        return needCharge;
    }

    public void setNeedCharge(Boolean needCharge) {
        this.needCharge = needCharge;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public List<Detail> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<Detail> detailList) {
        this.detailList = detailList;
    }

    public static class Detail{
        /**
         * 货品编码
         */
        @JSONField(name = "cargo_code")
        private String cargoCode;

        /**
         * 订单号
         */
        @JSONField(name = "order_no")
        private String orderNo;

        /**
         * 订单类型 采购入库：PURCHASE_IN; 销退入库：SALE_RETURN_IN; 库存调整（盘盈）：INVENTORY_ADJUST
         */
        @JSONField(name = "order_type")
        private String orderType;

        /**
         * 结算库存数（T-1日期末库存）
         */
        @JSONField(name = "stock_num")
        private Integer stockNum;

        /**
         * 库区类型： 1-普通区 2-恒温区
         */
        @JSONField(name = "warehouse_area_type")
        private Integer warehouseAreaType;

        /**
         * 库龄（单位是天），10天
         */
        @JSONField(name = "storage_age")
        private Integer storageAge;

        /**
         * 货品条码
         * 非必填
         */
        @JSONField(name = "bar_code")
        private String barCode;

        /**
         * 长（单位 mm）
         */
        private Integer length;

        /**
         * 宽（单位 mm）
         */
        private Integer width;

        /**
         * 高（单位 mm）
         */
        private Integer height;

        public String getCargoCode() {
            return cargoCode;
        }

        public void setCargoCode(String cargoCode) {
            this.cargoCode = cargoCode;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public String getOrderType() {
            return orderType;
        }

        public void setOrderType(String orderType) {
            this.orderType = orderType;
        }

        public Integer getStockNum() {
            return stockNum;
        }

        public void setStockNum(Integer stockNum) {
            this.stockNum = stockNum;
        }

        public Integer getWarehouseAreaType() {
            return warehouseAreaType;
        }

        public void setWarehouseAreaType(Integer warehouseAreaType) {
            this.warehouseAreaType = warehouseAreaType;
        }

        public Integer getStorageAge() {
            return storageAge;
        }

        public void setStorageAge(Integer storageAge) {
            this.storageAge = storageAge;
        }

        public String getBarCode() {
            return barCode;
        }

        public void setBarCode(String barCode) {
            this.barCode = barCode;
        }

        public Integer getLength() {
            return length;
        }

        public void setLength(Integer length) {
            this.length = length;
        }

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }
    }
}
