package me.zhengjie.rest.model.douyin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.zhengjie.support.douyin.CBOrderListMain;
import me.zhengjie.utils.StringUtil;

import java.math.BigDecimal;
import java.util.List;

public class TakeLogisticsInfoPush {

    @JsonProperty(value = "order_id")
    @JSONField(name = "order_id")
    private String orderId;

    @JsonProperty(value = "shop_id")
    @JSONField(name = "shop_id")
    private Long shopId;

    //收货人姓名
    @JsonProperty(value = "consignee")
    @JSONField(name = "consignee")
    private String consignee;

    //收货人电话
    @JsonProperty(value = "consignee_telephone")
    @JSONField(name = "consignee_telephone")
    private String consigneeTelephone;

    //订购人证件类型   1-身份证  2-其他
    @JsonProperty(value = "buyer_id_type")
    @JSONField(name = "buyer_id_type")
    private String buyerIdType;

    //订购人证件号码
    @JsonProperty(value = "buyer_id_number")
    @JSONField(name = "buyer_id_number")
    private String buyerIdNumber;

    //进出口标志   i-进口,e-出口
    @JsonProperty(value = "ie_flag")
    @JSONField(name = "ie_flag")
    private String ieFlag;

    //0-存量  1-报税备货  2-海外集货  3-海外备货
    @JsonProperty(value = "wh_type")
    @JSONField(name = "wh_type")
    private Integer whType;

    //电商平台代码(海关备案编号)
    @JsonProperty(value = "ebp_code")
    @JSONField(name = "ebp_code")
    private String ebpCode;

    //电商平台名称(海关备案名称)
    @JsonProperty(value = "ebp_name")
    @JSONField(name = "ebp_name")
    private String ebpName;

    //关区代码
    @JsonProperty(value = "port_code")
    @JSONField(name = "port_code")
    private String portCode;

    //服务商仓库编码
    @JsonProperty(value = "scsp_warehouse_code")
    @JSONField(name = "scsp_warehouse_code")
    private String scspWarehouseCode;

    //明细信息
    @JsonProperty(value = "goodsDetails")
    @JSONField(name = "goodsDetails")
    private List<GoodsDetails> details;

    @JsonProperty(value = "consignee_address")
    @JSONField(name = "consignee_address")
    private String consigneeAddress;

    /**
     * 快递资源编码
     */
    @JsonProperty(value = "carrier_code")
    @JSONField(name = "carrier_code")
    private String carrierCode;

    @JsonProperty(value = "extend")
    @JSONField(name = "extend")
    private String extend;

    public static class Extend {

        @JsonProperty(value = "4PL")
        @JSONField(name = "4PL")
        private String fourPL;

        public String getFourPL() {
            return fourPL;
        }

        public void setFourPL(String fourPL) {
            this.fourPL = fourPL;
        }
    }


    private Address address;

    public static class Address {
        /**
         * 省
         */
        private AddressNameId province;
        /**
         * 市
         */
        private AddressNameId city;
        /**
         * 区
         */
        private AddressNameId town;
        /**
         * 街道
         */
        private AddressNameId street;
        /**
         * 详细地址
         */
        private String detail;

        public AddressNameId getProvince() {
            return province;
        }

        public void setProvince(AddressNameId province) {
            this.province = province;
        }

        public AddressNameId getCity() {
            return city;
        }

        public void setCity(AddressNameId city) {
            this.city = city;
        }

        public AddressNameId getTown() {
            return town;
        }

        public void setTown(AddressNameId town) {
            this.town = town;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public AddressNameId getStreet() {
            return street;
        }

        public void setStreet(AddressNameId street) {
            this.street = street;
        }
    }

    public static class AddressNameId{
        @JSONField(name = "id")
        private String id;
        @JSONField(name = "name")
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getConsigneeTelephone() {
        return consigneeTelephone;
    }

    public void setConsigneeTelephone(String consigneeTelephone) {
        this.consigneeTelephone = consigneeTelephone;
    }

    public String getBuyerIdType() {
        return buyerIdType;
    }

    public void setBuyerIdType(String buyerIdType) {
        this.buyerIdType = buyerIdType;
    }

    public String getBuyerIdNumber() {
        return buyerIdNumber;
    }

    public void setBuyerIdNumber(String buyerIdNumber) {
        this.buyerIdNumber = buyerIdNumber;
    }

    public String getIeFlag() {
        return ieFlag;
    }

    public void setIeFlag(String ieFlag) {
        this.ieFlag = ieFlag;
    }

    public Integer getWhType() {
        return whType;
    }

    public void setWhType(Integer whType) {
        this.whType = whType;
    }

    public String getEbpCode() {
        return ebpCode;
    }

    public void setEbpCode(String ebpCode) {
        this.ebpCode = ebpCode;
    }

    public String getEbpName() {
        return ebpName;
    }

    public void setEbpName(String ebpName) {
        this.ebpName = ebpName;
    }

    public String getPortCode() {
        return portCode;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public String getScspWarehouseCode() {
        return scspWarehouseCode;
    }

    public void setScspWarehouseCode(String scspWarehouseCode) {
        this.scspWarehouseCode = scspWarehouseCode;
    }

    public String getConsigneeAddress() {
        return consigneeAddress;
    }

    public void setConsigneeAddress(String consigneeAddress) {
        if (StringUtil.isNotEmpty(consigneeAddress))
            this.address = JSON.parseObject(StringUtil.filterEmoji(consigneeAddress), Address.class);
        this.consigneeAddress = consigneeAddress;
    }

    public String getCarrierCode() {
        return carrierCode;
    }

    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public List<GoodsDetails> getDetails() {
        return details;
    }

    public void setDetails(List<GoodsDetails> details) {
        this.details = details;
    }

    public static class GoodsDetails {
        //商品id 1
        @JsonProperty(value = "item_no")
        @JSONField(name = "item_no")
        private String itemNo;

        //2
        @JsonProperty(value = "bar_code")
        @JSONField(name = "bar_code")
        private String barCode;

        //3
        @JsonProperty(value = "item_name")
        @JSONField(name = "item_name")
        private String itemName;

        //4
        @JsonProperty(value = "price")
        @JSONField(name = "price")
        private BigDecimal price;

        //5
        @JsonProperty(value = "qty")
        @JSONField(name = "qty")
        private Integer qty;

        //    货币类型  6
        @JsonProperty(value = "currency")
        @JSONField(name = "currency")
        private String currency;

        //毛重   8
        @JsonProperty(value = "weight")
        @JSONField(name = "weight")
        private String weight;

        //    净重  7
        @JsonProperty(value = "net_weight_qty")
        @JSONField(name = "net_weight_qty")
        private String netWeightQty;

        @JsonProperty(value = "mark_id")
        @JSONField(name = "mark_id")
        private Long markId;

        @JsonProperty(value = "record_name")
        @JSONField(name = "record_name")
        private String recordName;

        public Long getMarkId() {
            return markId;
        }

        public void setMarkId(Long markId) {
            this.markId = markId;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getNetWeightQty() {
            return netWeightQty;
        }

        public void setNetWeightQty(String netWeightQty) {
            this.netWeightQty = netWeightQty;
        }

        public String getItemNo() {
            return itemNo;
        }

        public void setItemNo(String itemNo) {
            this.itemNo = itemNo;
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

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public Integer getQty() {
            return qty;
        }

        public void setQty(Integer qty) {
            this.qty = qty;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getRecordName() {
            return recordName;
        }

        public void setRecordName(String recordName) {
            this.recordName = recordName;
        }
    }
}
