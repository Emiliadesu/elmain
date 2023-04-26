package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

import java.util.List;

public class StockTransformRequest implements CommonApiParam {
    @Override
    public String getMethod() {
        return "crossborder.stockTransform";
    }

    @Override
    public String getKeyWord() {
        return String.valueOf(this.getShopId());
    }
    /**
     * 库存变动类型
     * 1良转残，2残转良
     */
    @JSONField(name = "transform_type")
    private Short transformType;

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
     */
    @JSONField(name = "shop_id")
    private Long shopId;

    /**
     * 库存变动时间
     */
    @JSONField(name = "occurrence_time")
    private Long occurrenceTime;

    /**
     * 备注
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
     * 调整类型
     * 201 库内商品正转残
     * 202 库内商品残转正
     * 203 库存批次属性转移
     * 204 库内商品过期转残
     */
    @JSONField(name = "adjust_biz_type")
    private String adjustBizType;

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
     * 货品类型变动信息
     */
    @JSONField(name = "stock_transform_details")
    private List<StockTransformDetail> stockTransformDetails;

    public Short getTransformType() {
        return transformType;
    }

    public void setTransformType(Short transformType) {
        this.transformType = transformType;
    }

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

    public String getIdempotentNo() {
        return idempotentNo;
    }

    public void setIdempotentNo(String idempotentNo) {
        this.idempotentNo = idempotentNo;
    }

    public List<StockTransformDetail> getStockTransformDetails() {
        return stockTransformDetails;
    }

    public void setStockTransformDetails(List<StockTransformDetail> stockTransformDetails) {
        this.stockTransformDetails = stockTransformDetails;
    }

    public String getAdjustBizType() {
        return adjustBizType;
    }

    public void setAdjustBizType(String adjustBizType) {
        this.adjustBizType = adjustBizType;
    }
}
