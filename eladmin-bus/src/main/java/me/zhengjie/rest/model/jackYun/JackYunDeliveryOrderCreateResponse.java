package me.zhengjie.rest.model.jackYun;

import me.zhengjie.support.jackYun.JackYunBasicResponse;

public class JackYunDeliveryOrderCreateResponse extends JackYunBasicResponse {
    /**
     * 订单创建时间
     */
    private String createTime;
    /**
     * 仓储系统的出库单号
     */
    private String deliveryOrderId;
    /**
     * 仓库编码
     */
    private String warehouseCode;
    /**
     * 物流公司编码
     */
    private String logisticsCode;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDeliveryOrderId() {
        return deliveryOrderId;
    }

    public void setDeliveryOrderId(String deliveryOrderId) {
        this.deliveryOrderId = deliveryOrderId;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getLogisticsCode() {
        return logisticsCode;
    }

    public void setLogisticsCode(String logisticsCode) {
        this.logisticsCode = logisticsCode;
    }
}
