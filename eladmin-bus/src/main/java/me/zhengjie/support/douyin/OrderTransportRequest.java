package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.zhengjie.support.CommonApiParam;

public class OrderTransportRequest implements CommonApiParam {

    @Override
    public String getKeyWord() {
        return this.getOrderId();
    }

    @JsonProperty(value = "order_id")
    @JSONField(name = "order_id")
    private String orderId;

    //服务商编码
    @JsonProperty(value = "vendor")
    @JSONField(name = "vendor")
    private String vendor;

    //物流公司code
    @JsonProperty(value = "domestic_carrier")
    @JSONField(name = "domestic_carrier")
    private String domesticCarrier;

    //物流单号
    @JsonProperty(value = "domestic_trans_no")
    @JSONField(name = "domestic_trans_no")
    private String domesticTransNo;

    //物流企业代码,四字中文编码 (宁波必填)
    @JsonProperty(value = "express_code")
    @JSONField(name = "express_code")
    private String expressCode;

    //物流企业海关编码
    @JsonProperty(value = "express_cop_code")
    @JSONField(name = "express_cop_code")
    private String expressCopCode;

    //物流企业名称
    @JsonProperty(value = "express_cop_name")
    @JSONField(name = "express_cop_name")
    private String expressCopName;

    //三段码
    @JsonProperty(value = "sort_code")
    @JSONField(name = "sort_code")
    private String sortCode;

    //物流备用字段
    @JsonProperty(value = "remark01")
    @JSONField(name = "remark01")
    private String remark01;

    @JsonProperty(value = "remark02")
    @JSONField(name = "remark02")
    private String remark02;

    @JsonProperty(value = "remark03")
    @JSONField(name = "remark03")
    private String remark03;

    @JsonProperty(value = "remark04")
    @JSONField(name = "remark04")
    private String remark04;

    @JsonProperty(value = "remark05")
    @JSONField(name = "remark05")
    private String remark05;

    @JsonProperty(value = "remark06")
    @JSONField(name = "remark06")
    private String remark06;

    @JsonProperty(value = "remark07")
    @JSONField(name = "remark07")
    private String remark07;

    @JsonProperty(value = "remark08")
    @JSONField(name = "remark08")
    private String remark08;

    @JsonProperty(value = "remark09")
    @JSONField(name = "remark09")
    private String remark09;

    @JsonProperty(value = "remark10")
    @JSONField(name = "remark10")
    private String remark10;


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDomesticCarrier() {
        return domesticCarrier;
    }

    public void setDomesticCarrier(String domesticCarrier) {
        this.domesticCarrier = domesticCarrier;
    }

    public String getDomesticTransNo() {
        return domesticTransNo;
    }

    public void setDomesticTransNo(String domesticTransNo) {
        this.domesticTransNo = domesticTransNo;
    }

    public String getExpressCode() {
        return expressCode;
    }

    public void setExpressCode(String expressCode) {
        this.expressCode = expressCode;
    }

    public String getExpressCopCode() {
        return expressCopCode;
    }

    public void setExpressCopCode(String expressCopCode) {
        this.expressCopCode = expressCopCode;
    }

    public String getExpressCopName() {
        return expressCopName;
    }

    public void setExpressCopName(String expressCopName) {
        this.expressCopName = expressCopName;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getRemark01() {
        return remark01;
    }

    public void setRemark01(String remark01) {
        this.remark01 = remark01;
    }

    public String getRemark02() {
        return remark02;
    }

    public void setRemark02(String remark02) {
        this.remark02 = remark02;
    }

    public String getRemark03() {
        return remark03;
    }

    public void setRemark03(String remark03) {
        this.remark03 = remark03;
    }

    public String getRemark04() {
        return remark04;
    }

    public void setRemark04(String remark04) {
        this.remark04 = remark04;
    }

    public String getRemark05() {
        return remark05;
    }

    public void setRemark05(String remark05) {
        this.remark05 = remark05;
    }

    public String getRemark06() {
        return remark06;
    }

    public void setRemark06(String remark06) {
        this.remark06 = remark06;
    }

    public String getRemark07() {
        return remark07;
    }

    public void setRemark07(String remark07) {
        this.remark07 = remark07;
    }

    public String getRemark08() {
        return remark08;
    }

    public void setRemark08(String remark08) {
        this.remark08 = remark08;
    }

    public String getRemark09() {
        return remark09;
    }

    public void setRemark09(String remark09) {
        this.remark09 = remark09;
    }

    public String getRemark10() {
        return remark10;
    }

    public void setRemark10(String remark10) {
        this.remark10 = remark10;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    @Override
    public String toString() {
        return "OrderTransportRequest{" +
                "orderId='" + orderId + '\'' +
                ", domesticCarrier='" + domesticCarrier + '\'' +
                ", domesticTransNo='" + domesticTransNo + '\'' +
                ", expressCode='" + expressCode + '\'' +
                ", expressCopCode='" + expressCopCode + '\'' +
                ", expressCopName='" + expressCopName + '\'' +
                ", sortCode='" + sortCode + '\'' +
                ", remark01='" + remark01 + '\'' +
                ", remark02='" + remark02 + '\'' +
                ", remark03='" + remark03 + '\'' +
                ", remark04='" + remark04 + '\'' +
                ", remark05='" + remark05 + '\'' +
                ", remark06='" + remark06 + '\'' +
                ", remark07='" + remark07 + '\'' +
                ", remark08='" + remark08 + '\'' +
                ", remark09='" + remark09 + '\'' +
                ", remark10='" + remark10 + '\'' +
                '}';
    }

    @Override
    public String getMethod() {
        return "crossborder.takingLogisticsInfo";
    }
}
