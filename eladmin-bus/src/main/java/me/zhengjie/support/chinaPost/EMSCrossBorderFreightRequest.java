package me.zhengjie.support.chinaPost;

import org.apache.poi.ss.formula.functions.T;

import java.math.BigDecimal;

public class EMSCrossBorderFreightRequest {

    private String appType; //企业报送类型。1-新增 2-变更 3-删除。默认为1。

    private String appTime;  //报送时间

    private String appStatus;  //业务状态:1-暂存,2-申报,默认为2。

    private String logisticsCode;  //物流企业的海关注册登记编号。

    private String logisticsName;  //物流企业在海关注册登记的名称。

    private String logisticsNo;

    private String billNo;  //提运单号

    private BigDecimal freight; //运费

    private BigDecimal insuredFee;  //保价费

    private String currency;  //币制    限定为人民币，填写“142”。

    private BigDecimal weight;  //毛重

    private Integer packNo;  //件数

    private String goodsInfo; //货物信息

    private String consignee;

    private String consigneeAddress;

    private String consigneeTelephone;

    private String note;

    private String orderNo;  //订单号

    private String ebpCode;  //电商平台代码

    private KzInfo KzInfo;

    public static class KzInfo{

        private String shipper;  //发货人名称

        private String shipperAddress;

        private String shipperTelephone;

        private String consigneeProvince;  //收件人省

        private String consigneeCity;

        private String consigneeCounty;

        private String mailType;  //1：标准快递 9：快递包裹 0：BS号段

        private String ebpName;

        private String ebcCode;

        private String ebcName;

        private String isNotDeclSend;  //是否发送海关报关	N:不发送; Y:发送

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

        public String getConsigneeCounty() {
            return consigneeCounty;
        }

        public void setConsigneeCounty(String consigneeCounty) {
            this.consigneeCounty = consigneeCounty;
        }

        public String getMailType() {
            return mailType;
        }

        public void setMailType(String mailType) {
            this.mailType = mailType;
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

        public String getIsNotDeclSend() {
            return isNotDeclSend;
        }

        public void setIsNotDeclSend(String isNotDeclSend) {
            this.isNotDeclSend = isNotDeclSend;
        }
    }

    public KzInfo getKzInfo() {
        return KzInfo;
    }

    public void setKzInfo(KzInfo kzInfo) {
        this.KzInfo = kzInfo;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getAppTime() {
        return appTime;
    }

    public void setAppTime(String appTime) {
        this.appTime = appTime;
    }

    public String getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(String appStatus) {
        this.appStatus = appStatus;
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

    public String getLogisticsNo() {
        return logisticsNo;
    }

    public void setLogisticsNo(String logisticsNo) {
        this.logisticsNo = logisticsNo;
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Integer getPackNo() {
        return packNo;
    }

    public void setPackNo(Integer packNo) {
        this.packNo = packNo;
    }

    public String getGoodsInfo() {
        return goodsInfo;
    }

    public void setGoodsInfo(String goodsInfo) {
        this.goodsInfo = goodsInfo;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getEbpCode() {
        return ebpCode;
    }

    public void setEbpCode(String ebpCode) {
        this.ebpCode = ebpCode;
    }
}
