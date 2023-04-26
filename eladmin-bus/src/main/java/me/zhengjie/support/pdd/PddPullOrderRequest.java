package me.zhengjie.support.pdd;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class PddPullOrderRequest implements PddCommonRequest{
    @JSONField(name = "page_no")
    private Integer pageNo;
    @JSONField(name = "page_size")
    private Integer pageSize;
    @JSONField(name = "start_time")
    private Date startTime;
    @JSONField(name = "end_time")
    private Date endTime;
    @JSONField(name = "shop_code")
    private String shopCode;

    /**
     * 要查询的订单状态，可选
     * 1：待发货，2：已发货待签收，3：已签收 5：全部
     * 默认值 1
     */
    @JSONField(name = "order_status")
    private Integer orderStatus;

    /**
     * 售后状态 可选
     * 1：无售后或售后关闭，2：售后处理中，3：退款中，4： 退款成功 5：全部
     * 默认值 1
     */
    @JSONField(name = "refund_status")
    private Integer refundStatus;

    @JSONField(name = "l_shop_id")
    private Long lShopId;
    @JSONField(name = "cust_id")
    private Long custId;

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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    @Override
    public String getApiPath() {
        return "/api/pdd-erp/pull-order-enc";
    }

    public Long getlShopId() {
        return lShopId;
    }

    public void setlShopId(Long lShopId) {
        this.lShopId = lShopId;
    }

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(Integer refundStatus) {
        this.refundStatus = refundStatus;
    }

    @Override
    public String toString() {
        return "PddPullOrderRequest{" +
                "pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", shopCode='" + shopCode + '\'' +
                '}';
    }
}
