package me.zhengjie.support.beidian;

import com.alibaba.fastjson.annotation.JSONField;

public class BeiBeiOuterTradeOrderGetRequest extends BaseRequest{

    @Override
    public String getMethod() {
        return "beibei.outer.trade.order.get";
    }

    @Override
    public String getKeyWord() {
        return "";
    }

    /**
     *查询时间方式
     * modified_time根据修改时间查询
     * pay_time 支付时间
     */
    @JSONField(name = "time_range")
    private String timeRange;

    /**
     * 起始时间 秒
     */
    @JSONField(name = "start_time")
    private Long startTime;

    /**
     * 终止时间 秒
     */
    @JSONField(name = "end_time")
    private Long endTime;

    /**
     * 订单状态 不填则返回所有状态订单
     * 1待发货，2已发货，3已完成，4已关闭
     */
    @JSONField(name = "status")
    private Integer status;

    /**
     * 页码
     */
    @JSONField(name = "page_no")
    private Integer pageNo;

    /**
     * 页大小
     */
    @JSONField(name = "pageSize")
    private Integer pageSize;

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
