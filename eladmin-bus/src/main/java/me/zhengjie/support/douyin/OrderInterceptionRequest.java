package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

public class OrderInterceptionRequest implements CommonApiParam {
    @Override
    public String getMethod() {
        return "crossborder.OrderInterception";
    }

    @Override
    public String getKeyWord() {
        return this.getOrderId();
    }

    /**
     * 服务商编码
     */
    private String vendor;

    /**
     * 交易单号
     */
    @JSONField(name = "order_id")
    private String orderId;

    /**
     * 节点发生时间yyyy-MM-dd HH:mm:ss
     */
    @JSONField(name = "occurrence_time")
    private String occurrenceTime;

    /**
     * 锁单结果
     */
    private Integer status;

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
