package me.zhengjie.rest.model.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NotifyAdjustResult {
    /**
     * 仓库编号
     */
    @JsonProperty(value = "warehouse_code")
    @JSONField(name = "warehouse_code")
    private String warehouseCode;

    /**
     * 调整单号（对应回传的幂等单号）
     */
    @JsonProperty(value = "inventory_adjust_no")
    @JSONField(name = "inventory_adjust_no")
    private String inventoryAdjustNo;

    /**
     * 调整原因类型/单据类型 1-库存调整，2-库存转移
     */
    @JsonProperty(value = "adjust_reason")
    @JSONField(name = "adjust_reason")
    private Integer adjustReason;

    /**
     * 审核结果 1-通过  2-驳回
     */
    @JsonProperty(value = "approve_result")
    @JSONField(name = "approve_result")
    private Integer approveResult;

    /**
     * 驳回原因
     */
    @JsonProperty(value = "reject_reason")
    @JSONField(name = "reject_reason")
    private String rejectReason;

    /**
     * 审核时间(秒级时间戳)
     */
    @JsonProperty(value = "approve_time")
    @JSONField(name = "approve_time")
    private Long approveTime;

    /**
     * 履约单号，基于该单据号调整库存
     */
    @JsonProperty(value = "order_no")
    @JSONField(name = "order_no")
    private String orderNo;

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getInventoryAdjustNo() {
        return inventoryAdjustNo;
    }

    public void setInventoryAdjustNo(String inventoryAdjustNo) {
        this.inventoryAdjustNo = inventoryAdjustNo;
    }

    public Integer getAdjustReason() {
        return adjustReason;
    }

    public void setAdjustReason(Integer adjustReason) {
        this.adjustReason = adjustReason;
    }

    public Integer getApproveResult() {
        return approveResult;
    }

    public void setApproveResult(Integer approveResult) {
        this.approveResult = approveResult;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public Long getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(Long approveTime) {
        this.approveTime = approveTime;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
