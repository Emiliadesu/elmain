package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

import java.util.List;
import com.alibaba.fastjson.JSONObject;

/**
 * 盘点信息回传
 */
public class StockTakingRequest implements CommonApiParam {
    @Override
    public String getMethod() {
        return "crossborder.stockTaking";
    }

    @Override
    public String getKeyWord() {
        return String.valueOf(this.getShopId());
    }

    /**
     * 服务商编码
     */
    private String vendor;

    /**
     * 仓库编码
     */
    @JSONField(name = "warehouse_code")
    private String warehouseCode;

    /**
     * 店铺id
     * 选填
     */
    @JSONField(name = "shop_id")
    private Long shopId;

    /**
     * 盘点类型
     * 1盘盈，2盘亏
     */
    @JSONField(name = "taking_type")
    private Integer takingType;

    /**
     * 盘点完成时间
     */
    @JSONField(name = "occurrence_time")
    private Long occurrenceTime;

    /**
     * 备注
     * 选填
     */
    private String remark;

    /**
     * 状态
     * 抖音新模式必传
     * 2-待审核 1-已完成 5-已取消
     */
    @JSONField(name = "status")
    private Integer status;

    /**
     * 预约到货单号
     * 抖音新模式必传
     */
    @JSONField(name = "inbound_plan_no")
    private String inboundPlanNo;

    /**
     * 幂等号(转移号
     * 抖音新模式必传
     */
    @JSONField(name = "idempotent_no")
    private String idempotentNo;

    /**
     * 调整类型
     * 101 库存盘点
     * 102 包耗材盘点
     * 103 包耗材出库
     * 104 库存初始化
     * 105 销退入库
     * 106 系统异常
     */
    @JSONField(name = "adjust_biz_type")
    private String adjustBizType;

    /**
     * 盘点信息
     */
    @JSONField(name = "stock_taking_details")
    List<StockTakingDetail> stockTakingDetails;

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Integer getTakingType() {
        return takingType;
    }

    public void setTakingType(Integer takingType) {
        this.takingType = takingType;
    }

    public Long getOccurrenceTime() {
        return occurrenceTime;
    }

    public void setOccurrenceTime(Long occurrenceTime) {
        this.occurrenceTime = occurrenceTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getInboundPlanNo() {
        return inboundPlanNo;
    }

    public void setInboundPlanNo(String inboundPlanNo) {
        this.inboundPlanNo = inboundPlanNo;
    }

    public List<StockTakingDetail> getStockTakingDetails() {
        return stockTakingDetails;
    }

    public void setStockTakingDetails(List<StockTakingDetail> stockTakingDetails) {
        this.stockTakingDetails = stockTakingDetails;
    }

    public String getIdempotentNo() {
        return idempotentNo;
    }

    public void setIdempotentNo(String idempotentNo) {
        this.idempotentNo = idempotentNo;
    }

    public String getAdjustBizType() {
        return adjustBizType;
    }

    public void setAdjustBizType(String adjustBizType) {
        this.adjustBizType = adjustBizType;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
