package me.zhengjie.support.jingdong;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class JDEclpOrderAddDeclareCustsRequest {

    private String platformId;  //三方平台编号(由JD运营或者销售支持反馈给商家字段信息表中提供。)

    private String platformName; //三方平台名称

    private String appType;  //申报类型，1-新增; 2-变更并重推;3-重推

    private String logisticsNo; //物流运单编号

    private String billSerialNo;  //运单流水号

    private String billNo; //直购进口为海运提单或空运总单

    private BigDecimal freight;  //运费

    private BigDecimal insuredFee;  //保价费用

    private BigDecimal netWeight;//净重(单位为千克)

    private BigDecimal weight;  //毛重(单位为千克)

    private Integer packNo;   //件数(包裹数,固定传1)

    private BigDecimal worth;  //价值

    private String goodsName;  //主要商品名称(赤道需要,必填)

    private String orderNo;  //外单订单号

    private String shipper;//发货人信息

    private String shipperAddress;

    private String shipperTelephone;

    private String shipperCountry;  //发货人所在国国别代码

    private String consigneeCountry;

    private String consigneeProvince;

    private String consigneeCity;

    private String consigneeDistrict;

    private String consingee;

    private String consigneeAddress;

    private String consigneeTelephone;

    private String buyerIdType;  //收货人证件类型，1-身份证;2-其它

    private String buyerIdNumber;

    private String customsId;  //保税区编码（保税区在京东系统中的编码，由JD运营或者销售支持反馈给商家字段信息表中提供。

    private String customsCode;  //海关关区编码

    private String deptNo;  //事业部编码

    private String isvSource;  //ISV来源编号（JD运营或者销售支持反馈给商家字段信息表中提供，用于标识ISV软件服务商,京东内部事业部编号,可查）,否则拒单

    private String pattern;  //跨境业务模式(保税备货=beihuo，保税直邮=zhiyou，个人快件=grkuaijian，邮政=youzheng

    private String isvUUID;  //商家销售订单号（作为商家订单生产出库的唯一标识，同商家进行支付时调支付企业的唯一订单标识；如果存在拆单则传子订单号

    private Integer platformType;  //销售平台类型，请赋值数字2

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Timestamp salesPlatformCreateTime;  //订单创建时间(yyyy-MM-dd HH:mm:ss),必须是消费者真实下单时间

    private String postType;  //申报订单类型，I-进口商品订单；E-出口商品订单,长度不超过1

    private Integer istax;  //是否包税，0-包税；1-不包税

    private String logisticsCode;  //物流企业代码

    private String logisticsName;  //物流企业名称

    private Integer isDelivery;  //	是否货到付款，1-是；0-否

    private String ebpCode;  //电商平台编码

    private  String ebpName;  //电商平台名称

    private String ebcCode;  //电商企业编码

    private String ebcName;  //电商企业名称

    private String ebpCiqCode;  //电商平台国检编码

    private String ebpCiqName;  //电商平台国检名称

    private String ebcCiqCode;  //电商企业国检档案编号

    private String ebcCiqName;  //电商企业国检档案名称

    private String spSoNo;  //销售平台单号,1:销售平台来源京东（salePlatformSource=1）时必填,会验证该单号是否是京东平台订单,如果不是拒单;2:全球购订单（orderMark：18位）时必填.

    public Integer getPackNo() {
        return packNo;
    }

    public void setPackNo(Integer packNo) {
        this.packNo = packNo;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getLogisticsNo() {
        return logisticsNo;
    }

    public void setLogisticsNo(String logisticsNo) {
        this.logisticsNo = logisticsNo;
    }

    public String getBillSerialNo() {
        return billSerialNo;
    }

    public void setBillSerialNo(String billSerialNo) {
        this.billSerialNo = billSerialNo;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public BigDecimal getFreight() {
        return freight;
    }

    public void setFreight(BigDecimal freight) {
        this.freight = freight;
    }

    public BigDecimal getInsuredFee() {
        return insuredFee;
    }

    public void setInsuredFee(BigDecimal insuredFee) {
        this.insuredFee = insuredFee;
    }

    public BigDecimal getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(BigDecimal netWeight) {
        this.netWeight = netWeight;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getWorth() {
        return worth;
    }

    public void setWorth(BigDecimal worth) {
        this.worth = worth;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getShipper() {
        return shipper;
    }

    public void setShipper(String shipper) {
        this.shipper = shipper;
    }

    public String getShipperAddress() {
        return shipperAddress;
    }

    public void setShipperAddress(String shipperAddress) {
        this.shipperAddress = shipperAddress;
    }

    public String getShipperTelephone() {
        return shipperTelephone;
    }

    public void setShipperTelephone(String shipperTelephone) {
        this.shipperTelephone = shipperTelephone;
    }

    public String getShipperCountry() {
        return shipperCountry;
    }

    public void setShipperCountry(String shipperCountry) {
        this.shipperCountry = shipperCountry;
    }

    public String getConsigneeCountry() {
        return consigneeCountry;
    }

    public void setConsigneeCountry(String consigneeCountry) {
        this.consigneeCountry = consigneeCountry;
    }

    public String getConsigneeProvince() {
        return consigneeProvince;
    }

    public void setConsigneeProvince(String consigneeProvince) {
        this.consigneeProvince = consigneeProvince;
    }

    public String getConsigneeCity() {
        return consigneeCity;
    }

    public void setConsigneeCity(String consigneeCity) {
        this.consigneeCity = consigneeCity;
    }

    public String getConsigneeDistrict() {
        return consigneeDistrict;
    }

    public void setConsigneeDistrict(String consigneeDistrict) {
        this.consigneeDistrict = consigneeDistrict;
    }

    public String getConsingee() {
        return consingee;
    }

    public void setConsingee(String consingee) {
        this.consingee = consingee;
    }

    public String getConsigneeAddress() {
        return consigneeAddress;
    }

    public void setConsigneeAddress(String consigneeAddress) {
        this.consigneeAddress = consigneeAddress;
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

    public String getCustomsId() {
        return customsId;
    }

    public void setCustomsId(String customsId) {
        this.customsId = customsId;
    }

    public String getCustomsCode() {
        return customsCode;
    }

    public void setCustomsCode(String customsCode) {
        this.customsCode = customsCode;
    }

    public String getDeptNo() {
        return deptNo;
    }

    public void setDeptNo(String deptNo) {
        this.deptNo = deptNo;
    }

    public String getIsvSource() {
        return isvSource;
    }

    public void setIsvSource(String isvSource) {
        this.isvSource = isvSource;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getIsvUUID() {
        return isvUUID;
    }

    public void setIsvUUID(String isvUUID) {
        this.isvUUID = isvUUID;
    }

    public Integer getPlatformType() {
        return platformType;
    }

    public void setPlatformType(Integer platformType) {
        this.platformType = platformType;
    }

    public Timestamp getSalesPlatformCreateTime() {
        return salesPlatformCreateTime;
    }

    public void setSalesPlatformCreateTime(Timestamp salesPlatformCreateTime) {
        this.salesPlatformCreateTime = salesPlatformCreateTime;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public Integer getIstax() {
        return istax;
    }

    public void setIstax(Integer istax) {
        this.istax = istax;
    }

    public String getLogisticsCode() {
        return logisticsCode;
    }

    public void setLogisticsCode(String logisticsCode) {
        this.logisticsCode = logisticsCode;
    }

    public String getLogisticsName() {
        return logisticsName;
    }

    public void setLogisticsName(String logisticsName) {
        this.logisticsName = logisticsName;
    }

    public Integer getIsDelivery() {
        return isDelivery;
    }

    public void setIsDelivery(Integer isDelivery) {
        this.isDelivery = isDelivery;
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

    public String getEbpCiqCode() {
        return ebpCiqCode;
    }

    public void setEbpCiqCode(String ebpCiqCode) {
        this.ebpCiqCode = ebpCiqCode;
    }

    public String getEbpCiqName() {
        return ebpCiqName;
    }

    public void setEbpCiqName(String ebpCiqName) {
        this.ebpCiqName = ebpCiqName;
    }

    public String getEbcCiqCode() {
        return ebcCiqCode;
    }

    public void setEbcCiqCode(String ebcCiqCode) {
        this.ebcCiqCode = ebcCiqCode;
    }

    public String getEbcCiqName() {
        return ebcCiqName;
    }

    public void setEbcCiqName(String ebcCiqName) {
        this.ebcCiqName = ebcCiqName;
    }

    public String getSpSoNo() {
        return spSoNo;
    }

    public void setSpSoNo(String spSoNo) {
        this.spSoNo = spSoNo;
    }
}
