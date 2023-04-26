package me.zhengjie.rest.model.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

public class LadingBillPush {
    /**
     * 提货单id
     */
    @JsonProperty(value = "outbound_plan_no")
    @JSONField(name = "outbound_plan_no")
    private String outboundPlanNo;

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
     * 提货出库类型
     */
    @JsonProperty(value = "outbound_type")
    @JSONField(name = "outbound_type")
    private Short outboundType;

    /**
     * 提货日期
     * 精确到日
     */
    @JsonProperty(value = "outbound_date")
    @JSONField(name = "outbound_date",format = "yyyy-MM-dd HH:mm:ss")
    private Date outboundDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 货品信息列表
     */
    @JsonProperty(value = "cargo_lading_list")
    @JSONField(name = "cargo_lading_list")
    private List<CargoLading> cargoLadingList;

    /**
     * 货主id
     */
    @JSONField(name = "owner_id")
    private Long ownerId;

    /**
     * 转入关区名称
     */

    @JSONField(name = "from_customs_name")
    private String fromCustomsName;

    /**
     * 转入关区代码
     */
    @JSONField(name = "from_customs_code")
    private String fromCustomsCode;

    /**
     * 转入仓账册
     */
    @JSONField(name = "from_warehouse_volume")
    private String fromWarehouseVolume;

    /**
     * 扩展信息
     */
    private String extend;

    public String getOutboundPlanNo() {
        return outboundPlanNo;
    }

    public void setOutboundPlanNo(String outboundPlanNo) {
        this.outboundPlanNo = outboundPlanNo;
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

    public Short getOutboundType() {
        return outboundType;
    }

    public void setOutboundType(Short outboundType) {
        this.outboundType = outboundType;
    }

    public Date getOutboundDate() {
        return outboundDate;
    }

    public void setOutboundDate(Date outboundDate) {
        this.outboundDate = outboundDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<CargoLading> getCargoLadingList() {
        return cargoLadingList;
    }

    public void setCargoLadingList(List<CargoLading> cargoLadingList) {
        this.cargoLadingList = cargoLadingList;
    }

    public String getFromCustomsName() {
        return fromCustomsName;
    }

    public void setFromCustomsName(String fromCustomsName) {
        this.fromCustomsName = fromCustomsName;
    }

    public String getFromCustomsCode() {
        return fromCustomsCode;
    }

    public void setFromCustomsCode(String fromCustomsCode) {
        this.fromCustomsCode = fromCustomsCode;
    }

    public String getFromWarehouseVolume() {
        return fromWarehouseVolume;
    }

    public void setFromWarehouseVolume(String fromWarehouseVolume) {
        this.fromWarehouseVolume = fromWarehouseVolume;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }
}
