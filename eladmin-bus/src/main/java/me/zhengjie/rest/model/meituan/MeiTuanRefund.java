package me.zhengjie.rest.model.meituan;

import com.alibaba.fastjson.annotation.JSONField;

public class MeiTuanRefund {
    /**
     * 订单号
     */
    @JSONField(name = "order_id")
    private Long orderId;

    /**
     * 全额退款通知类型
     */
    @JSONField(name = "notify_type")
    private String notifyType;

    /**
     * 退款id
     */
    @JSONField(name = "refund_id")
    private Long refundId;

    /**
     * 退款时间
     */
    @JSONField(name = "ctime")
    private Long ctime;

    /**
     * 退款类型
     */
    @JSONField(name = "res_type")
    private Integer resType;

    /**
     * 是否为用户发起的退款
     */
    @JSONField(name = "is_appeal")
    private Integer isAppeal;

    /**
     * 申请退款的原因
     */
    private String reason;

    /**
     * 退款服务类型
     */
    @JSONField(name = "service_type")
    private String serviceType;

    private String status;

    public String getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType;
    }

    public Long getRefundId() {
        return refundId;
    }

    public void setRefundId(Long refundId) {
        this.refundId = refundId;
    }

    public Long getCtime() {
        return ctime;
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }

    public Integer getResType() {
        return resType;
    }

    public void setResType(Integer resType) {
        this.resType = resType;
    }

    public Integer getIsAppeal() {
        return isAppeal;
    }

    public void setIsAppeal(Integer isAppeal) {
        this.isAppeal = isAppeal;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
