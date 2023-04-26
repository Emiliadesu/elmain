package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

import java.math.BigDecimal;

public class CBOrderSorterEventRequest implements CommonApiParam {
    @Override
    public String getMethod() {
        return "/api/sorter/event";
    }

    @Override
    public String getKeyWord() {
        return this.getTrackingNO();
    }

    /**
     * 设备名称
     */
    @JSONField(name = "deviceName")
    private String deviceName;

    /**
     * 运单号
     */
    @JSONField(name = "trackingNO")
    private String trackingNO;

    /**
     * 请求时间
     */
    @JSONField(name = "reqTime")
    private long reqTime;

    /**
     * 响应时间，毫秒时间戳
     */
    @JSONField(name = "respTime")
    private long respTime;

    /**
     * 分拣状态：1-成功，2-失败
     */
    @JSONField(name = "status")
    private int status;

    /**
     * 结果：成功时赋值格口号，失败时赋值失败原因
     */
    @JSONField(name = "result")
    private String result;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getTrackingNO() {
        return trackingNO;
    }

    public void setTrackingNO(String trackingNO) {
        this.trackingNO = trackingNO;
    }

    public long getReqTime() {
        return reqTime;
    }

    public void setReqTime(long reqTime) {
        this.reqTime = reqTime;
    }

    public long getRespTime() {
        return respTime;
    }

    public void setRespTime(long respTime) {
        this.respTime = respTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
