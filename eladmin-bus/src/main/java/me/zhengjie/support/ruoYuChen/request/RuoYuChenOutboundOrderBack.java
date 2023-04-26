package me.zhengjie.support.ruoYuChen.request;

import java.util.List;

public class RuoYuChenOutboundOrderBack extends RuoYuChenRequestAbs{
    /**
     * 仓储系统出库单号
     * Y
     */
    private String deliveryOrderId;
    /**
     * 出库单号
     * Y
     */
    private String deliveryOrderCode;
    /**
     * 出库单类型
     * (PTCK=普通出库单;DBCK=调拨出库;
     * B2BCK=B2B出库;QTCK=其他出库; CGTH=采购退货出库单;XNCK=虚拟出库单,
     * LYCK=领用出库;JITCK=唯品出库;
     * PKCK=盘亏出库);
     * Y
     */
    private String orderType;
    /**
     * 单据类型
     * (ZLBG=质量变更;XQTZ=效期调整; KCPD=库存盘点;QTLX=其他类型;
     * 只传英文编码)
     * N
     */
    private String orderSubType;
    /**
     * 出库单状态
     * Y
     */
    private String status;
    /**
     * 外部业务编码(用于消息去重)
     * N
     */
    private String outBizCode;
    /**
     * 物流公司编码
     * N
     */
    private String logisticsCode;
    /**
     * 运单号
     * N
     */
    private String expressCode;
    /**
     * 操作时间(yyyy-MM-dd HH:mm:ss)
     * Y
     */
    private String operateTime;
    /**
     * 预计到货日期
     * N
     */
    private String calculateTime;
    /**
     * 回传模式 0：标准模式、1：合并模式；
     * 默认为：0标准模式
     * N
     */
    private String modeType;
    /**
     * 备注
     * N
     */
    private String remark;
    /**
     * 订单信息
     * Y
     */
    private List<RuoYuChenOutboundOrderBackDetail> orderLines;

    /**
     * 包裹信息
     * N
     */
    private List<RuoYuChenOutBoundOrderBackPackage>packages;

    public String getDeliveryOrderId() {
        return deliveryOrderId;
    }

    public void setDeliveryOrderId(String deliveryOrderId) {
        this.deliveryOrderId = deliveryOrderId;
    }

    public String getDeliveryOrderCode() {
        return deliveryOrderCode;
    }

    public void setDeliveryOrderCode(String deliveryOrderCode) {
        this.deliveryOrderCode = deliveryOrderCode;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderSubType() {
        return orderSubType;
    }

    public void setOrderSubType(String orderSubType) {
        this.orderSubType = orderSubType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOutBizCode() {
        return outBizCode;
    }

    public void setOutBizCode(String outBizCode) {
        this.outBizCode = outBizCode;
    }

    public String getLogisticsCode() {
        return logisticsCode;
    }

    public void setLogisticsCode(String logisticsCode) {
        this.logisticsCode = logisticsCode;
    }

    public String getExpressCode() {
        return expressCode;
    }

    public void setExpressCode(String expressCode) {
        this.expressCode = expressCode;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public String getCalculateTime() {
        return calculateTime;
    }

    public void setCalculateTime(String calculateTime) {
        this.calculateTime = calculateTime;
    }

    public String getModeType() {
        return modeType;
    }

    public void setModeType(String modeType) {
        this.modeType = modeType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<RuoYuChenOutboundOrderBackDetail> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<RuoYuChenOutboundOrderBackDetail> orderLines) {
        this.orderLines = orderLines;
    }

    public List<RuoYuChenOutBoundOrderBackPackage> getPackages() {
        return packages;
    }

    public void setPackages(List<RuoYuChenOutBoundOrderBackPackage> packages) {
        this.packages = packages;
    }

    @Override
    public String getMethod() {
        return "StockoutBack";
    }
}
