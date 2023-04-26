package me.zhengjie.rest.model.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

public class InboundBillPush {
    /**
     * 预约到货单id
     */
    @JsonProperty(value = "inbound_plan_no")
    @JSONField(name = "inbound_plan_no")
    private String inboundPlanNo;

    /**
     * 仓库id
     */
    @JsonProperty(value = "warehouse_no")
    @JSONField(name = "warehouse_no")
    private String warehouseNo;

    /**
     * 商家id
     */
    @JsonProperty(value = "shop_id")
    @JSONField(name = "shop_id")
    private Long shopId;

    /**
     * 预约类型
     * 1：保税-跨境海外发货 2：保税-区间区内结转
     */
    @JsonProperty(value = "inbound_from_type")
    @JSONField(name = "inbound_from_type")
    private Short inboundFromType;

    /**
     * 转出关区名称
     * 若预约类型是2.保税-区间区内结转，必传
     */
    @JsonProperty(value = "out_area_name")
    @JSONField(name = "out_area_name")
    private String outAreaName;

    /**
     * 转出关区代码
     * 若预约类型是2.保税-区间区内结转，必传
     */
    @JsonProperty(value = "out_area_code")
    @JSONField(name = "out_area_code")
    private String outAreaCode;

    /**
     * 转出仓账册号
     * 若预约类型是2.保税-区间区内结转，必传
     */
    @JsonProperty(value = "inbound_book")
    @JSONField(name = "inbound_book")
    private String inboundBook;

    /**
     * 发货地区
     */
    @JsonProperty(value = "shipped_from")
    @JSONField(name = "shipped_from")
    private String shippedFrom;

    /**
     * 运输方式
     * 1:空运 2:海运 3:陆运
     */
    @JsonProperty(value = "shipment_mode")
    @JSONField(name = "shipment_mode")
    private Short shipmentMode;

    /**
     * 发货时间
     * 精确到日
     */
    @JsonProperty(value = "shipment_date")
    @JSONField(name = "shipment_date",format = "yyyy-MM-dd HH:mm:ss")
    private Date shipmentDate;

    /**
     * 到货时间
     * 精确到日
     */
    @JsonProperty(value = "arrival_date")
    @JSONField(name = "arrival_date",format = "yyyy-MM-dd HH:mm:ss")
    private Date arrivalDate;

    /**
     * 发货人姓名
     */
    @JsonProperty(value = "shipper_name")
    @JSONField(name = "shipper_name")
    private String shipperName;

    /**
     * 发货人电话
     */
    @JsonProperty(value = "shipper_phone")
    @JSONField(name = "shipper_phone")
    private String shipperPhone;

    /**
     * 发货人邮箱
     */
    @JsonProperty(value = "shipper_email")
    @JSONField(name = "shipper_email")
    private String shipperEmail;

    /**
     * 备注
     */
    private String remark;

    /**
     * 货品消息列表
     */
    @JsonProperty(value = "cargo_inbound_list")
    @JSONField(name = "cargo_inbound_list")
    private List<CargoInbound>cargoInboundList;

    /**
     * 报关信息列表
     */
    @JsonProperty(value = "declare_detail_list")
    @JSONField(name = "declare_detail_list")
    private List<DeclareDetail>declareDetailList;

    /**
     * 提单号，非必填
     */
    @JSONField(name = "lading_bill_no")
    private String ladingBillNo;

    /**
     * 扩展信息
     */
    private String extend;

    public String getInboundPlanNo() {
        return inboundPlanNo;
    }

    public void setInboundPlanNo(String inboundPlanNo) {
        this.inboundPlanNo = inboundPlanNo;
    }

    public String getWarehouseNo() {
        return warehouseNo;
    }

    public void setWarehouseNo(String warehouseNo) {
        this.warehouseNo = warehouseNo;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Short getInboundFromType() {
        return inboundFromType;
    }

    public void setInboundFromType(Short inboundFromType) {
        this.inboundFromType = inboundFromType;
    }

    public String getOutAreaName() {
        return outAreaName;
    }

    public void setOutAreaName(String outAreaName) {
        this.outAreaName = outAreaName;
    }

    public String getOutAreaCode() {
        return outAreaCode;
    }

    public void setOutAreaCode(String outAreaCode) {
        this.outAreaCode = outAreaCode;
    }

    public String getInboundBook() {
        return inboundBook;
    }

    public void setInboundBook(String inboundBook) {
        this.inboundBook = inboundBook;
    }

    public String getShippedFrom() {
        return shippedFrom;
    }

    public void setShippedFrom(String shippedFrom) {
        this.shippedFrom = shippedFrom;
    }

    public Short getShipmentMode() {
        return shipmentMode;
    }

    public void setShipmentMode(Short shipmentMode) {
        this.shipmentMode = shipmentMode;
    }

    public Date getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(Date shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public String getShipperName() {
        return shipperName;
    }

    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }

    public String getShipperPhone() {
        return shipperPhone;
    }

    public void setShipperPhone(String shipperPhone) {
        this.shipperPhone = shipperPhone;
    }

    public String getShipperEmail() {
        return shipperEmail;
    }

    public void setShipperEmail(String shipperEmail) {
        this.shipperEmail = shipperEmail;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<CargoInbound> getCargoInboundList() {
        return cargoInboundList;
    }

    public void setCargoInboundList(List<CargoInbound> cargoInboundList) {
        this.cargoInboundList = cargoInboundList;
    }

    public List<DeclareDetail> getDeclareDetailList() {
        return declareDetailList;
    }

    public void setDeclareDetailList(List<DeclareDetail> declareDetailList) {
        this.declareDetailList = declareDetailList;
    }

    public String getLadingBillNo() {
        return ladingBillNo;
    }

    public void setLadingBillNo(String ladingBillNo) {
        this.ladingBillNo = ladingBillNo;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }
}
