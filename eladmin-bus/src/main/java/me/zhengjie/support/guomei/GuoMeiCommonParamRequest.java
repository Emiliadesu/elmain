package me.zhengjie.support.guomei;

import cn.hutool.json.JSONObject;
import io.swagger.models.auth.In;

public class GuoMeiCommonParamRequest {

    private String traceId;

    private String timestamp;

    private String orderNo;

    private String statusReason;

    private Integer status;

    private String operator;

    private String operateTime;

    private String operateRemark;

    private Integer isBuyerInfoError;

    private String customsCode;

    private String customsName;

    private String logisticsName;

    private String logisticsCode;

    private String wayBillNo;

    private String deliveryTime;

    private String vendor;

    public String getLogisticsName() {
        return logisticsName;
    }

    public void setLogisticsName(String logisticsName) {
        this.logisticsName = logisticsName;
    }

    public String getLogisticsCode() {
        return logisticsCode;
    }

    public void setLogisticsCode(String logisticsCode) {
        this.logisticsCode = logisticsCode;
    }

    public String getWayBillNo() {
        return wayBillNo;
    }

    public void setWayBillNo(String wayBillNo) {
        this.wayBillNo = wayBillNo;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getCustomsName() {
        return customsName;
    }

    public void setCustomsName(String customsName) {
        this.customsName = customsName;
    }

    public Integer getIsBuyerInfoError() {
        return isBuyerInfoError;
    }

    public void setIsBuyerInfoError(Integer isBuyerInfoError) {
        this.isBuyerInfoError = isBuyerInfoError;
    }

    public String getCustomsCode() {
        return customsCode;
    }

    public void setCustomsCode(String customsCode) {
        this.customsCode = customsCode;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public String getOperateRemark() {
        return operateRemark;
    }

    public void setOperateRemark(String operateRemark) {
        this.operateRemark = operateRemark;
    }


    @Override
    public String toString() {
        return "GuoMeiCommonParamRequest{" +
                "traceId='" + traceId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", statusReason='" + statusReason + '\'' +
                ", status=" + status +
                ", operator='" + operator + '\'' +
                ", operateTime='" + operateTime + '\'' +
                ", operateRemark='" + operateRemark + '\'' +
                ", isBuyerInfoError=" + isBuyerInfoError +
                ", customsCode='" + customsCode + '\'' +
                ", customsName='" + customsName + '\'' +
                ", logisticsName='" + logisticsName + '\'' +
                ", logisticsCode='" + logisticsCode + '\'' +
                ", wayBillNo='" + wayBillNo + '\'' +
                ", deliveryTime='" + deliveryTime + '\'' +
                ", vendor='" + vendor + '\'' +
                '}';
    }
}
