package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

import java.math.BigDecimal;

public class CBOrderSorterGetSortRequest implements CommonApiParam {
    @Override
    public String getMethod() {
        return "/api/sorter/getSort";
    }

    @Override
    public String getKeyWord() {
        return this.getTrackingNO();
    }

    /**
     * 设备名称
     */
    @JSONField(name = "name")
    private String name;

    /**
     * 运单号
     */
    @JSONField(name = "trackingNO")
    private String trackingNO;

    /**
     * 单位：毫克
     */
    @JSONField(name = "weight")
    private long weight;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTrackingNO() {
        return trackingNO;
    }

    public void setTrackingNO(String trackingNO) {
        this.trackingNO = trackingNO;
    }

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }
}
