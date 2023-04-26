package me.zhengjie.support.queenshop;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

import java.util.Arrays;
import java.util.Date;

public class QSOrderListRequest implements CommonApiParam {

    @Override
    public String getMethod() {
        return "qs.pop.trade.search";
    }

    @Override
    public String getKeyWord() {
        return "";
    }

    /**
     * 待发货：WAIT_SELLER_DELIVERY
     已发货：WAIT_BUYER_CONFIRM
     已签收：TRADE_FINISH
     已取消：TRADE_CLOSE
     多个状态用,进行拼接
     */
    @JSONField(name = "orderStatus")
    private String orderStatus;

    /**
     * 无退款或退款关闭：NO_REFUND
     退款中：WAIT_REFUND
     退款成功：REFUND_FINISH
     默认：NO_REFUND
     */
    @JSONField(name = "refundStatus")
    private String refundStatus;

    /**
     * 订单搜索页码
     */
    @JSONField(name = "page")
    private Integer page;
    /**
     * 每页订单数 默认50 最大100，超过则按照100处理
     */
    @JSONField(name = "pageSize")
    private Integer pageSize;

    /**
     * 1-交易时间，2-修改时间
     */
    @JSONField(name = "dateType")
    private Integer dateType;

    /**
     * 开始时间 必填 格式yyyy-MM-dd HH:mm:ss
     */
    @JSONField(name = "startTime")
    private long startTime;

    /**
     * 截止时间 必填 格式yyyy-MM-dd HH:mm:ss
     */
    @JSONField(name = "endTime")
    private long endTime;

    /**
     * 平台订单号
     */
    @JSONField(name = "orderNo")
    private String orderNo;

    @JSONField(name = "cust_id")
    private Long custId;

    @JSONField(name = "shop_id")
    private Long shopId;

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getDateType() {
        return dateType;
    }

    public void setDateType(Integer dateType) {
        this.dateType = dateType;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    @Override
    public String toString() {
        return "QSOrderListRequest{" +
                "orderStatus='" + orderStatus + '\'' +
                ", refundStatus='" + refundStatus + '\'' +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", dateType=" + dateType +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", orderNo='" + orderNo + '\'' +
                ", custId=" + custId +
                ", shopId=" + shopId +
                '}';
    }
}
