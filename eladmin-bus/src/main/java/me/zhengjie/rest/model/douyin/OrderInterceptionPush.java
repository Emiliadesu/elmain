package me.zhengjie.rest.model.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class OrderInterceptionPush {
    /**
     * 交易单号
     */
    @JsonProperty(value = "order_id")
    @JSONField(name = "order_id")
    private String orderId;

    /**
     * 指令产生时间
     */
    @JsonProperty(value = "occurrence_time")
    @JSONField(name = "occurrence_time",format = "yyyy-MM-dd HH:mm:ss")
    private String occurrenceTime;

    /**
     * 指令类型
     * 1：锁单 2：拦截 3：取消锁单
     */
    @JsonProperty(value = "type")
    @JSONField(name = "type")
    private Short type;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOccurrenceTime() {
        return occurrenceTime;
    }

    public void setOccurrenceTime(String occurrenceTime) {
        this.occurrenceTime = occurrenceTime;
    }

    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }
}
