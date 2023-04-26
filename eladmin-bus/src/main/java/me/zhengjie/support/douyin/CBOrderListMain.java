package me.zhengjie.support.douyin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.utils.StringUtil;

import java.math.BigDecimal;
import java.util.List;

public class CBOrderListMain {

    @JSONField(name = "cust_id")
    private Long custId; // 本系统货主ID

    @JSONField(name = "l_shop_id")
    private Long lShopId;// 本系统店铺ID

    /**
     * 订单编号
     */
    @JSONField(name = "order_id")
    private String orderId;

    @JSONField(name = "create_time")
    private String createTime;

    /**
     * 电商平台代码
     */
    @JSONField(name = "ebp_code")
    private String ebpCode;
    /**
     * 电商平台名
     */
    @JSONField(name = "ebp_name")
    private String ebpName;
    /**
     * 电商企业代码
     */
    @JSONField(name = "ebc_code")
    private String ebcCode;
    /**
     * 电商企业名
     */
    @JSONField(name = "ebc_name")
    private String ebcName;
    /**
     * 店铺在电商平台的id
     */
    @JSONField(name = "shop_id")
    private String shopId;
    /**
     * 店铺在电商平台的名称
     */
    @JSONField(name = "shop_name")
    private String shopName;
    /**
     * 进出口标识
     */
    @JSONField(name = "is_flag")
    private String ieFlag;
    /**
     * 通关模式
     */
    @JSONField(name = "customs_clear_type")
    private String customsClearType;
    /**
     * 申报海关代码
     */
    @JSONField(name = "customs_code")
    private String customsCode;
    /**
     * 口岸海关代码
     */
    @JSONField(name = "port_code")
    private String portCode;
    /**
     * 商家仓库编码
     */
    @JSONField(name = "warehouse_code")
    private String warehouseCode;
    /**
     * 商品实际成交价， 含非现金抵扣金额（商品不含税价*商品数量）单位是分，数据库要求元
     */
    @JSONField(name = "goods_value")
    private BigDecimal goodsValue;
    /**
     * 运杂费（含物流保费）免邮传0，单位是分，数据库要求元
     */
    @JSONField(name = "freight")
    private BigDecimal freight;
    /**
     * 非现金抵扣金额（不含支付满减）使用积分等非现金支付金额，无则填写 "0" 单位是分，数据库要求元
     */
    @JSONField(name = "discount")
    private BigDecimal discount;
    /**
     * 代扣税款  企业预先代扣的税款金额，无则填写“0” 单位是分,数据库要求元
     */
    @JSONField(name = "tax_total")
    private BigDecimal taxTotal;
    /**
     * 实际支付金额（商品价格+运杂费+代扣税款- 非现金抵扣金额）单位是分,数据库要求元
     */
    @JSONField(name = "actural_paid")
    private BigDecimal acturalPaid;
    /**
     * 物流保费（物流保价费）  一般传0  单位是分,数据库要求元
     */
    @JSONField(name = "insured_fee")
    private BigDecimal insuredFee;
    /**
     * 币制 限定为人民币，填写“142”
     */
    @JSONField(name = "currency")
    private String currency;
    /**
     * 订购人注册号
     */
    @JSONField(name = "buyer_reg_no")
    private String buyerRegNo;
    /**
     * 订购人姓名
     */
    @JSONField(name = "buyer_name")
    private String buyerName;
    /**
     * 订购人电话
     */
    @JSONField(name = "buyer_telephone")
    private String buyerTelephone;
    /**
     * 订购人证件类型
     */
    @JSONField(name = "buyer_id_type")
    private String buyerIdType;
    /**
     * 订购人证件号码
     */
    @JSONField(name = "buyer_id_number")
    private String buyerIdNumber;
    /**
     * 收货人
     */
    @JSONField(name = "consignee")
    private String consignee;
    /**
     * 收货人电话
     */
    @JSONField(name = "consignee_telephone")
    private String consigneeTelephone;
    /**
     * 收货地址(JSON字符串)
     */
    @JSONField(name = "consignee_address")
    private String consigneeAddress;
    /**
     * 支付企业的海关注册登记编号
     */
    @JSONField(name = "pay_code")
    private String payCode;
    /**
     * 支付企业在海关注册登记的企业名称
     */
    @JSONField(name = "pay_name")
    private String payName;
    /**
     * 支付时间
     */
    @JSONField(name = "pay_time")
    private String payTime;
    /**
     * 支付流水号
     */
    @JSONField(name = "pay_transaction_id")
    private String payTransactionId;

    @JSONField(name = "pre_sale_type")
    private String preSaleType;

    @JSONField(name = "exp_ship_time")
    private String expShipTime;

    @JSONField(name = "order_declare")
    private Integer orderDeclare;

    @JSONField(name = "order_detail_list")
    private List<CBOrderListChild> itemList;

    /**
     * 状态
     */
    private Integer status;

    private Address address;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public String getEbcCode() {
        return ebcCode;
    }

    public void setEbcCode(String ebcCode) {
        this.ebcCode = ebcCode;
    }

    public String getEbcName() {
        return ebcName;
    }

    public void setEbcName(String ebcName) {
        this.ebcName = ebcName;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getIeFlag() {
        return ieFlag;
    }

    public void setIeFlag(String ieFlag) {
        this.ieFlag = ieFlag;
    }

    public String getCustomsClearType() {
        return customsClearType;
    }

    public void setCustomsClearType(String customsClearType) {
        this.customsClearType = customsClearType;
    }

    public String getCustomsCode() {
        return customsCode;
    }

    public void setCustomsCode(String customsCode) {
        this.customsCode = customsCode;
    }

    public String getPortCode() {
        return portCode;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public BigDecimal getGoodsValue() {
        return goodsValue;
    }

    public void setGoodsValue(BigDecimal goodsValue) {
        this.goodsValue = goodsValue;
    }

    public BigDecimal getFreight() {
        return freight;
    }

    public void setFreight(BigDecimal freight) {
        this.freight = freight;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getTaxTotal() {
        return taxTotal;
    }

    public void setTaxTotal(BigDecimal taxTotal) {
        this.taxTotal = taxTotal;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public BigDecimal getActuralPaid() {
        return acturalPaid;
    }

    public void setActuralPaid(BigDecimal acturalPaid) {
        this.acturalPaid = acturalPaid;
    }

    public BigDecimal getInsuredFee() {
        return insuredFee;
    }

    public void setInsuredFee(BigDecimal insuredFee) {
        this.insuredFee = insuredFee;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getBuyerRegNo() {
        return buyerRegNo;
    }

    public void setBuyerRegNo(String buyerRegNo) {
        this.buyerRegNo = buyerRegNo;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerTelephone() {
        return buyerTelephone;
    }

    public void setBuyerTelephone(String buyerTelephone) {
        this.buyerTelephone = buyerTelephone;
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

    public String getConsigneeAddress() {
        return consigneeAddress;
    }

    public void setConsigneeAddress(String consigneeAddress) {
        if (StringUtil.isNotEmpty(consigneeAddress))
            this.address = JSON.parseObject(consigneeAddress, Address.class);
        this.consigneeAddress = consigneeAddress;
    }

    public String getPayCode() {
        return payCode;
    }

    public void setPayCode(String payCode) {
        this.payCode = payCode;
    }

    public String getPayName() {
        return payName;
    }

    public void setPayName(String payName) {
        this.payName = payName;
    }

    public String getPayTransactionId() {
        return payTransactionId;
    }

    public void setPayTransactionId(String payTransactionId) {
        this.payTransactionId = payTransactionId;
    }

    public List<CBOrderListChild> getItemList() {
        return itemList;
    }

    public void setItemList(List<CBOrderListChild> itemList) {
        this.itemList = itemList;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    public Long getlShopId() {
        return lShopId;
    }

    public void setlShopId(Long lShopId) {
        this.lShopId = lShopId;
    }

    public String getPreSaleType() {
        return preSaleType;
    }

    public void setPreSaleType(String preSaleType) {
        this.preSaleType = preSaleType;
    }

    public String getExpShipTime() {
        return expShipTime;
    }

    public void setExpShipTime(String expShipTime) {
        this.expShipTime = expShipTime;
    }

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

    public Integer getOrderDeclare() {
        return orderDeclare;
    }

    public void setOrderDeclare(Integer orderDeclare) {
        this.orderDeclare = orderDeclare;
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

    @Override
    public String toString() {
        return "CBOrderListMain{" +
                "custId=" + custId +
                ", lShopId=" + lShopId +
                ", orderId='" + orderId + '\'' +
                ", createTime='" + createTime + '\'' +
                ", ebpCode='" + ebpCode + '\'' +
                ", ebpName='" + ebpName + '\'' +
                ", ebcCode='" + ebcCode + '\'' +
                ", ebcName='" + ebcName + '\'' +
                ", shopId='" + shopId + '\'' +
                ", shopName='" + shopName + '\'' +
                ", ieFlag='" + ieFlag + '\'' +
                ", customsClearType='" + customsClearType + '\'' +
                ", customsCode='" + customsCode + '\'' +
                ", portCode='" + portCode + '\'' +
                ", warehouseCode='" + warehouseCode + '\'' +
                ", goodsValue=" + goodsValue +
                ", freight=" + freight +
                ", discount=" + discount +
                ", taxTotal=" + taxTotal +
                ", acturalPaid=" + acturalPaid +
                ", insuredFee=" + insuredFee +
                ", currency='" + currency + '\'' +
                ", buyerRegNo='" + buyerRegNo + '\'' +
                ", buyerName='" + buyerName + '\'' +
                ", buyerTelephone='" + buyerTelephone + '\'' +
                ", buyerIdType='" + buyerIdType + '\'' +
                ", buyerIdNumber='" + buyerIdNumber + '\'' +
                ", consignee='" + consignee + '\'' +
                ", consigneeTelephone='" + consigneeTelephone + '\'' +
                ", consigneeAddress='" + consigneeAddress + '\'' +
                ", payCode='" + payCode + '\'' +
                ", payName='" + payName + '\'' +
                ", payTime='" + payTime + '\'' +
                ", payTransactionId='" + payTransactionId + '\'' +
                ", preSaleType='" + preSaleType + '\'' +
                ", expShipTime='" + expShipTime + '\'' +
                ", itemList=" + itemList +
                ", status=" + status +
                ", address=" + address +
                '}';
    }
}
