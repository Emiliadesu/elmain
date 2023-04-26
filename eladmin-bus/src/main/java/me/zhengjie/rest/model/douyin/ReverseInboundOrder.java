package me.zhengjie.rest.model.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class ReverseInboundOrder {
    @JsonProperty(value = "Inbound_plan_no",required = true)
    @NotNull
    @JSONField(name = "Inbound_plan_no")
    private String inboundPlanNo;

    @JsonProperty(value = "operate_type",required = true)
    @NotNull
    @JSONField(name = "operate_type")
    private Integer operateType;

    @JsonProperty(value = "cancel_reason",required = true)
    @NotNull
    @JSONField(name = "cancel_reason")
    private String cancelReason;

    @JsonProperty(value = "owner_id",required = true)
    @JSONField(name = "owner_id")
    private String ownerId;

    @JsonProperty(value = "shop_id",required = true)
    @JSONField(name = "shop_id")
    private Long shopId;

    public String getInboundPlanNo() {
        return inboundPlanNo;
    }

    public void setInboundPlanNo(String inboundPlanNo) {
        this.inboundPlanNo = inboundPlanNo;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }
}
