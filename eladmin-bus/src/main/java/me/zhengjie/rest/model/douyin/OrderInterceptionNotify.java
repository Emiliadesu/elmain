package me.zhengjie.rest.model.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author luob
 * @description
 * @date 2022/1/22
 */
public class OrderInterceptionNotify {

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
     * 1：拦截指令 2：取消拦截指令
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
