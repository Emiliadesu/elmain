package me.zhengjie.rest.model.jackYun;

import java.util.List;

public class JackYunStockOutOrder extends JackYunBasicRequest{
    private String deliveryOrderCode;
    private String orderType;
    private List<JackYunRelatedOrder> relatedOrders;
    private String createTime;
    private String scheduleDate;
    private JackYunStockOutPickerInfo pickerInfo;
    private String remark;

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

    public List<JackYunRelatedOrder> getRelatedOrders() {
        return relatedOrders;
    }

    public void setRelatedOrders(List<JackYunRelatedOrder> relatedOrders) {
        this.relatedOrders = relatedOrders;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public JackYunStockOutPickerInfo getPickerInfo() {
        return pickerInfo;
    }

    public void setPickerInfo(JackYunStockOutPickerInfo pickerInfo) {
        this.pickerInfo = pickerInfo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
