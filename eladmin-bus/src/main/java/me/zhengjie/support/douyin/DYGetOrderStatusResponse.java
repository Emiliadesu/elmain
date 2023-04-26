package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;

public class DYGetOrderStatusResponse {
    /**
     * 订单状态
     */
    @JSONField(name = "order_status")
    private Integer orderStatus;

    /**
     * 售后状态
     */
    @JSONField(name = "final_status")
    private Integer finalStatus;

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getFinalStatus() {
        return finalStatus;
    }

    public void setFinalStatus(Integer finalStatus) {
        this.finalStatus = finalStatus;
    }
}
