package me.zhengjie.support.aikucun.apiParam;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;


import java.util.Date;
import java.util.List;

public class AikucunDeliveryOrderSendApiParam implements CommonApiParam {
    /**
     * 发货列表
     */
    @JSONField(name = "adOrderList")
    private List<AdOrder>adOrderList;

    public List<AdOrder> getAdOrderList() {
        return adOrderList;
    }

    public void setAdOrderList(List<AdOrder> adOrderList) {
        this.adOrderList = adOrderList;
    }
    public static class AdOrder{
        /**
         * 发货订单号，AD单号
         */
        @JSONField(name = "adOrderId")
        private String adOrderId;
        /**
         * 发货包裹列表
         */
        @JSONField(name = "deliveryList")
        private List<DeliveryInfo>deliveryList;

        public List<DeliveryInfo> getDeliveryList() {
            return deliveryList;
        }

        public void setAdOrderId(String adOrderId) {
            this.adOrderId = adOrderId;
        }

        public String getAdOrderId() {
            return adOrderId;
        }

        public void setDeliveryList(List<DeliveryInfo> deliveryList) {
            this.deliveryList = deliveryList;
        }

    }
    public static class DeliveryInfo{
        /**
         * 运单号
         */
        @JSONField(name = "deliveryNo")
        private String deliveryNo;
        /**
         * 快递公司编码
         */
        @JSONField(name = "carrierId")
        private String carrierId;
        /**
         * 快递公司名称
         */
        @JSONField(name = "carrierName")
        private String carrierName;
        /**
         * 发货时间
         */
        @JSONField(name = "shippedTime",format = "yyyy-MM-dd-HH:mm:ss")
        private Date shippedTime;
        /**
         * 发货人信息
         */
        @JSONField(name = "shipInfo")
        private ShipInfo shipInfo;
        /**
         * 发货明细
         */
        @JSONField(name = "shipmentList")
        private List<BackSalesOrderDetailsDto>shipmentList;

        public String getDeliveryNo() {
            return deliveryNo;
        }

        public void setDeliveryNo(String deliveryNo) {
            this.deliveryNo = deliveryNo;
        }

        public String getCarrierId() {
            return carrierId;
        }

        public void setCarrierId(String carrierId) {
            this.carrierId = carrierId;
        }

        public String getCarrierName() {
            return carrierName;
        }

        public void setCarrierName(String carrierName) {
            this.carrierName = carrierName;
        }

        public Date getShippedTime() {
            return shippedTime;
        }

        public void setShippedTime(Date shippedTime) {
            this.shippedTime = shippedTime;
        }

        public ShipInfo getShipInfo() {
            return shipInfo;
        }

        public void setShipInfo(ShipInfo shipInfo) {
            this.shipInfo = shipInfo;
        }

        public List<BackSalesOrderDetailsDto> getShipmentList() {
            return shipmentList;
        }

        public void setShipmentList(List<BackSalesOrderDetailsDto> shipmentList) {
            this.shipmentList = shipmentList;
        }
    }
    public static class ShipInfo{
        /**
         * 发货人电话
         */
        @JSONField(name = "shipFromTel")
        private String shipFromTel;
        /**
         * 发货人名称
         */
        @JSONField(name = "shipFromName")
        private String shipFromName;
        /**
         * 发货人详细地址，不包括省市区
         */
        @JSONField(name = "shipFromAddress")
        private String shipFromAddress;
        /**
         * 发货人省份
         */
        @JSONField(name = "shipFromProvinceName")
        private String shipFromProvinceName;
        /**
         * 发货人 市
         */
        @JSONField(name = "shipFromCityName")
        private String shipFromCityName;
        /**
         * 发货人 区
         */
        @JSONField(name = "shipFromAreaName")
        private String shipFromAreaName;

        public String getShipFromTel() {
            return shipFromTel;
        }

        public void setShipFromTel(String shipFromTel) {
            this.shipFromTel = shipFromTel;
        }

        public String getShipFromName() {
            return shipFromName;
        }

        public void setShipFromName(String shipFromName) {
            this.shipFromName = shipFromName;
        }

        public String getShipFromAddress() {
            return shipFromAddress;
        }

        public void setShipFromAddress(String shipFromAddress) {
            this.shipFromAddress = shipFromAddress;
        }

        public String getShipFromProvinceName() {
            return shipFromProvinceName;
        }

        public void setShipFromProvinceName(String shipFromProvinceName) {
            this.shipFromProvinceName = shipFromProvinceName;
        }

        public String getShipFromCityName() {
            return shipFromCityName;
        }

        public void setShipFromCityName(String shipFromCityName) {
            this.shipFromCityName = shipFromCityName;
        }

        public String getShipFromAreaName() {
            return shipFromAreaName;
        }

        public void setShipFromAreaName(String shipFromAreaName) {
            this.shipFromAreaName = shipFromAreaName;
        }
    }
    public static class BackSalesOrderDetailsDto{
        /**
         * 平台skuId
         */
        @JSONField(name = "skuId")
        private String skuId;
        /**
         * 需发货数量，订购数量
         */
        @JSONField(name = "qty")
        private Integer qty;
        /**
         * 实发数
         */
        @JSONField(name = "realQty")
        private Integer realQty;
        /**
         * 商品条码
         */
        @JSONField(name = "barcode")
        private String barcode;
        /**
         * 批次号
         */
        @JSONField(name = "lotNo")
        private String lotNo;
        /**
         * 质量等级
         */
        @JSONField(name = "quality")
        private String quality;

        public String getSkuId() {
            return skuId;
        }

        public void setSkuId(String skuId) {
            this.skuId = skuId;
        }

        public Integer getQty() {
            return qty;
        }

        public void setQty(Integer qty) {
            this.qty = qty;
        }

        public Integer getRealQty() {
            return realQty;
        }

        public void setRealQty(Integer realQty) {
            this.realQty = realQty;
        }

        public String getBarcode() {
            return barcode;
        }

        public void setBarcode(String barcode) {
            this.barcode = barcode;
        }

        public String getLotNo() {
            return lotNo;
        }

        public void setLotNo(String lotNo) {
            this.lotNo = lotNo;
        }

        public String getQuality() {
            return quality;
        }

        public void setQuality(String quality) {
            this.quality = quality;
        }
    }
    @Override
    public String getMethod() {
        return "aikucun.delivery.oversea.order.send";
    }

    @Override
    public String getKeyWord() {
        return "";
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
