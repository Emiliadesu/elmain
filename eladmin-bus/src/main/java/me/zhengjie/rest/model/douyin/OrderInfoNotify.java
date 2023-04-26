package me.zhengjie.rest.model.douyin;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OrderInfoNotify {
    /**
     * 交易单号
     */
    @JsonProperty(value = "order_id")
    @JSONField(name = "order_id")
    private String orderId;

    /**
     * 商家店铺id
     */
    @JsonProperty(value = "shop_id")
    @JSONField(name = "shop_id")
    private Long shopId;

    /**
     * 跨境履约单创建时间（UnixTime 以ms为单位）
     */
    @JsonProperty(value = "create_time")
    @JSONField(name = "create_time")
    private Long createTime;

    /**
     * 进出口标志 I-进口,E-出口
     */
    @JsonProperty(value = "ie_flag")
    @JSONField(name = "ie_flag")
    private String ieFlag;

    /**
     * 通关模式 1BBC 2BC 3CC
     */
    @JsonProperty(value = "customs_clear_type")
    @JSONField(name = "customs_clear_type")
    private Integer customsClearType;

    /**
     * 申报海关代码 （海关关区代码）目前业务和portCode保持一致
     */
    @JsonProperty(value = "customs_code")
    @JSONField(name = "customs_code")
    private String customsCode;

    /**
     * 口岸海关代码 商品实际进出我国关境口岸海关 的关区代码 （海关关区代码）
     */
    @JsonProperty(value = "port_code")
    @JSONField(name = "port_code")
    private String portCode;

    /**
     * 仓库编码（商家自定义商家仓code）
     */
    @JsonProperty(value = "warehouse_code")
    @JSONField(name = "warehouse_code")
    private String warehouseCode;

    /**
     * 服务商仓库编码（KA商家无需使用）
     */
    @JsonProperty(value = "scsp_warehouse_code")
    @JSONField(name = "scsp_warehouse_code")
    private String scspWarehouseCode;

    /**
     * 0存量, 1保税备货, 2海外集货, 3海外备货；
     */
    @JsonProperty(value = "wh_type")
    @JSONField(name = "wh_type")
    private Integer whType;

    /**
     * BBC/BC 商品实际成交价， 含非现金抵扣金额 （商品不含税价*商品数量）单位是分
     */
    @JsonProperty(value = "goods_value")
    @JSONField(name = "goods_value")
    private Long goodsValue;

    /**
     * BBC/BC 运杂费（含物流保费） 免邮传0 单位是分
     */
    @JsonProperty(value = "freight")
    @JSONField(name = "freight")
    private Long freight;

    /**
     * BBC/BC 非现金抵扣金额（不含支付满减） 使用积分等非现金支付金额，无则填写 "0" 单位是分
     */
    @JsonProperty(value = "discount")
    @JSONField(name = "discount")
    private Long discount;

    /**
     * BBC/BC 代扣税款 企业预先代扣的税款金额，无则填写“0” 单位是分
     */
    @JsonProperty(value = "tax_total")
    @JSONField(name = "tax_total")
    private Long taxTotal;

    /**
     * BBC/BC 实际支付金额（商品价格+运杂费+代扣税款- 非现金抵扣金额）单位是分
     */
    @JsonProperty(value = "actual_paid")
    @JSONField(name = "actual_paid")
    private Long actualPaid;

    /**
     * BBC/BC 物流保费（物流保价费） 一般传0 单位是分
     */
    @JsonProperty(value = "insured_fee")
    @JSONField(name = "insured_fee")
    private Long insuredFee;

    /**
     * 币制 限定为人民币，填写“142”
     */
    @JsonProperty(value = "currency")
    @JSONField(name = "currency")
    private String currency;

    /**
     * 订购人注册号
     */
    @JsonProperty(value = "buyer_reg_no")
    @JSONField(name = "buyer_reg_no")
    private String buyerRegNo;

    /**
     *  订购人姓名
     */
    @JsonProperty(value = "buyer_name")
    @JSONField(name = "buyer_name")
    private String buyerName;

    /**
     * 订购人电话
     */
    @JsonProperty(value = "buyer_telephone")
    @JSONField(name = "buyer_telephone")
    private String buyerTelephone;

    /**
     * 订购人证件类型 1-身份证,2-其它。 限定为身份证，填 1
     */
    @JsonProperty(value = "buyer_id_type")
    @JSONField(name = "buyer_id_type")
    private Integer buyerIdType;

    /**
     * 订购人证件号码
     */
    @JsonProperty(value = "buyer_id_number")
    @JSONField(name = "buyer_id_number")
    private String buyerIdNumber;

    /**
     * 收货人姓名
     */
    @JsonProperty(value = "consignee")
    @JSONField(name = "consignee")
    private String consignee;

    /**
     * 收货人电话
     */
    @JsonProperty(value = "consignee_telephone")
    @JSONField(name = "consignee_telephone")
    private String consigneeTelephone;

    /**
     * 收货地址,JSON字符串
     */
    @JsonProperty(value = "consignee_address")
    @JSONField(name = "consignee_address")
    private String consigneeAddress;

    /**
     * 支付企业编码（BC/BBC下发）
     */
    @JsonProperty(value = "pay_code")
    @JSONField(name = "pay_code")
    private String payCode;

    /**
     * 支付企业在海关注册登记的企业名称
     */
    @JsonProperty(value = "pay_name")
    @JSONField(name = "pay_name")
    private String payName;

    /**
     * 支付企业唯一的支付流水号
     */
    @JsonProperty(value = "pay_transaction_id")
    @JSONField(name = "pay_transaction_id")
    private String payTransactionId;

    /**
     * 预售类型 0: 现货发货模式，1: 全款预售发货模式
     */
    @JsonProperty(value = "pre_sale_type")
    @JSONField(name = "pre_sale_type")
    private Integer preSaleType;

    /**
     * 预计发货时间，发货超时时间，时间戳,单位s
     */
    @JsonProperty(value = "exp_ship_time")
    @JSONField(name = "exp_ship_time")
    private Long expShipTime;

    /**
     * 快递资源编码
     */
    @JsonProperty(value = "carrier_code")
    @JSONField(name = "carrier_code")
    private String carrierCode;

    /**
     * 件数 件数为包裹数量， 限定为“1”
     */
    @JsonProperty(value = "pack_no")
    @JSONField(name = "pack_no")
    private Integer packNo;

    /**
     * 订单申报备注（扩展字段预留）
     */
    @JsonProperty(value = "note")
    @JSONField(name = "note")
    private String note;

    /**
     * 运单号
     */
    @JsonProperty(value = "trans_no")
    @JSONField(name = "trans_no")
    private String transNo;

    /**
     * 三段码
     */
    @JsonProperty(value = "sort_code")
    @JSONField(name = "sort_code")
    private String sortCode;

    @JsonProperty(value = "short_address_code")
    @JSONField(name = "short_address_code")
    private String shortAddressCode;

    @JsonProperty(value = "gross_weight")
    @JSONField(name = "gross_weight")
    private String grossWeight;

    @JsonProperty(value = "net_weight")
    @JSONField(name = "net_weight")
    private String netWeight;

    /**
     * 订单详情
     */
    @JsonProperty(value = "order_detail_list")
    @JSONField(name = "order_detail_list")
    private List<OrderDetail>orderDetailList;

    /**
     * 扩展字段
     * 示例值：
     * {
     *      "orderDeclare":"T",// 订单申报类型，F服务商申报，T平台申报
     *      "logisticsDeclare":"F",// 运单申报类型，F服务商申报，T平台申报
     *      "inventoryDeclare":"F", // 清单申报类型，F服务商申报，T平台申报
     *      "VASPack":"F", // 增值包装，F无增值包装，T有增值包装
     *      "4PL":"F" // 4PL标识，F非4PL单，T是4PL单
     *      "tms_seperate":"T", // 仓配分拆标识，F非仓配分拆，T是仓配分拆
     * }
     */
    @JsonProperty(value = "extend")
    @JSONField(name = "extend")
    private String extend;

    public static class Extend {
        @JsonProperty(value = "orderDeclare")
        @JSONField(name = "orderDeclare")
        private String orderDeclare;

        @JsonProperty(value = "logisticsDeclare")
        @JSONField(name = "logisticsDeclare")
        private String logisticsDeclare;

        @JsonProperty(value = "inventoryDeclare")
        @JSONField(name = "inventoryDeclare")
        private String inventoryDeclare;

        @JsonProperty(value = "VASPack")
        @JSONField(name = "VASPack")
        private String vasPack;

        @JsonProperty(value = "4PL")
        @JSONField(name = "4PL")
        private String fourPL;

        @JsonProperty(value = "tms_seperate")
        @JSONField(name = "tms_seperate")
        private String tmsSeperate;

        @JsonProperty(value = "subLadingBillNo")
        @JSONField(name = "subLadingBillNo")
        private String subLadingBillNo;

        @JsonProperty(value = "transactionMethod")
        @JSONField(name = "transactionMethod")
        private Integer transactionMethod;

        @JsonProperty(value = "portCustomType")
        @JSONField(name = "portCustomType")
        private Integer portCustomType;

        @JsonProperty(value = "tmsType")
        @JSONField(name = "tmsType")
        private Integer tmsType;

        @JsonProperty(value = "sendWarehouseAddr")
        @JSONField(name = "sendWarehouseAddr")
        private String sendWarehouseAddr;

        @JsonProperty(value = "targetWarehouseAddr")
        @JSONField(name = "targetWarehouseAddr")
        private String targetWarehouseAddr;

        @JsonProperty(value = "receiverName")
        @JSONField(name = "receiverName")
        private String receiverName;

        @JsonProperty(value = "receiverPhone")
        @JSONField(name = "receiverPhone")
        private String receiverPhone;

        public String getTmsSeperate() {
            return tmsSeperate;
        }

        public void setTmsSeperate(String tmsSeperate) {
            this.tmsSeperate = tmsSeperate;
        }

        public String getOrderDeclare() {
            return orderDeclare;
        }

        public void setOrderDeclare(String orderDeclare) {
            this.orderDeclare = orderDeclare;
        }

        public String getLogisticsDeclare() {
            return logisticsDeclare;
        }

        public void setLogisticsDeclare(String logisticsDeclare) {
            this.logisticsDeclare = logisticsDeclare;
        }

        public String getInventoryDeclare() {
            return inventoryDeclare;
        }

        public void setInventoryDeclare(String inventoryDeclare) {
            this.inventoryDeclare = inventoryDeclare;
        }

        public String getVasPack() {
            return vasPack;
        }

        public void setVasPack(String vasPack) {
            this.vasPack = vasPack;
        }

        public String getFourPL() {
            return fourPL;
        }

        public void setFourPL(String fourPL) {
            this.fourPL = fourPL;
        }

        public String getSubLadingBillNo() {
            return subLadingBillNo;
        }

        public void setSubLadingBillNo(String subLadingBillNo) {
            this.subLadingBillNo = subLadingBillNo;
        }

        public Integer getTransactionMethod() {
            return transactionMethod;
        }

        public void setTransactionMethod(Integer transactionMethod) {
            this.transactionMethod = transactionMethod;
        }

        public Integer getPortCustomType() {
            return portCustomType;
        }

        public void setPortCustomType(Integer portCustomType) {
            this.portCustomType = portCustomType;
        }

        public Integer getTmsType() {
            return tmsType;
        }

        public void setTmsType(Integer tmsType) {
            this.tmsType = tmsType;
        }

        public String getSendWarehouseAddr() {
            return sendWarehouseAddr;
        }

        public void setSendWarehouseAddr(String sendWarehouseAddr) {
            this.sendWarehouseAddr = sendWarehouseAddr;
        }

        public String getTargetWarehouseAddr() {
            return targetWarehouseAddr;
        }

        public void setTargetWarehouseAddr(String targetWarehouseAddr) {
            this.targetWarehouseAddr = targetWarehouseAddr;
        }

        public String getReceiverName() {
            return receiverName;
        }

        public void setReceiverName(String receiverName) {
            this.receiverName = receiverName;
        }

        public String getReceiverPhone() {
            return receiverPhone;
        }

        public void setReceiverPhone(String receiverPhone) {
            this.receiverPhone = receiverPhone;
        }
    }

    /**
     * 支付完成时间（UnixTime 以ms为单位）
     */
    @JsonProperty(value = "pay_time")
    @JSONField(name = "pay_time")
    private Long payTime;

    /**
     * 电商平台代码（电商平台海关备案编码）
     */
    @JsonProperty(value = "ebp_code")
    @JSONField(name = "ebp_code")
    private String ebpCode;

    /**
     * 电商平台名称 （电商平台海关备案名称）
     */
    @JsonProperty(value = "ebp_name")
    @JSONField(name = "ebp_name")
    private String ebpName;

    /**
     * 货主id
     */
    @JsonProperty(value = "ownerId")
    @JSONField(name = "ownerId")
    private String ownerId;

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

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getIeFlag() {
        return ieFlag;
    }

    public void setIeFlag(String ieFlag) {
        this.ieFlag = ieFlag;
    }

    public Integer getCustomsClearType() {
        return customsClearType;
    }

    public void setCustomsClearType(Integer customsClearType) {
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

    public String getScspWarehouseCode() {
        return scspWarehouseCode;
    }

    public void setScspWarehouseCode(String scspWarehouseCode) {
        this.scspWarehouseCode = scspWarehouseCode;
    }

    public Integer getWhType() {
        return whType;
    }

    public void setWhType(Integer whType) {
        this.whType = whType;
    }

    public Long getGoodsValue() {
        return goodsValue;
    }

    public void setGoodsValue(Long goodsValue) {
        this.goodsValue = goodsValue;
    }

    public Long getFreight() {
        return freight;
    }

    public void setFreight(Long freight) {
        this.freight = freight;
    }

    public Long getDiscount() {
        return discount;
    }

    public void setDiscount(Long discount) {
        this.discount = discount;
    }

    public Long getTaxTotal() {
        return taxTotal;
    }

    public void setTaxTotal(Long taxTotal) {
        this.taxTotal = taxTotal;
    }

    public Long getActualPaid() {
        return actualPaid;
    }

    public void setActualPaid(Long actualPaid) {
        this.actualPaid = actualPaid;
    }

    public Long getInsuredFee() {
        return insuredFee;
    }

    public void setInsuredFee(Long insuredFee) {
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

    public Integer getBuyerIdType() {
        return buyerIdType;
    }

    public void setBuyerIdType(Integer buyerIdType) {
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

    public Integer getPreSaleType() {
        return preSaleType;
    }

    public void setPreSaleType(Integer preSaleType) {
        this.preSaleType = preSaleType;
    }

    public Long getExpShipTime() {
        return expShipTime;
    }

    public void setExpShipTime(Long expShipTime) {
        this.expShipTime = expShipTime;
    }

    public String getCarrierCode() {
        return carrierCode;
    }

    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    public Integer getPackNo() {
        return packNo;
    }

    public void setPackNo(Integer packNo) {
        this.packNo = packNo;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<OrderDetail> getOrderDetailList() {
        return orderDetailList;
    }

    public void setOrderDetailList(List<OrderDetail> orderDetailList) {
        this.orderDetailList = orderDetailList;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public Long getPayTime() {
        return payTime;
    }

    public void setPayTime(Long payTime) {
        this.payTime = payTime;
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

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    public String getTransNo() {
        return transNo;
    }

    public void setTransNo(String transNo) {
        this.transNo = transNo;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(String grossWeight) {
        this.grossWeight = grossWeight;
    }

    public String getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(String netWeight) {
        this.netWeight = netWeight;
    }

    public String getShortAddressCode() {
        return shortAddressCode;
    }

    public void setShortAddressCode(String shortAddressCode) {
        this.shortAddressCode = shortAddressCode;
    }
}
