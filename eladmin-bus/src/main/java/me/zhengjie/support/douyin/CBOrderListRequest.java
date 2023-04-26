package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

import java.util.Arrays;
import java.util.Date;

public class CBOrderListRequest implements CommonApiParam {
    @Override
    public String getMethod() {
        return "crossBorder.orderList";
    }

    @Override
    public String getKeyWord() {
        return "";
    }

    /**
     * 开始时间 必填 格式yyyy-MM-dd HH:mm:ss
     */
    @JSONField(name = "start_time",format = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 截止时间 必填 格式yyyy-MM-dd HH:mm:ss
     */
    @JSONField(name = "end_time",format = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 时间类型 必填，create_time订单创建时间，update_time订单更新时间
     */
    @JSONField(name = "order_by")
    private String orderBy;

    @JSONField(name = "vendor")
    private String vendor;

    /**
     * 订单排序方式 0升序1降序，默认降序
     */
    @JSONField(name = "is_desc")
    private String isDesc;
    /**
     * 要查询的订单id列表，如果该字段不为空（至少有一个订单号），此时参数vendor有用，其他参数不起作用，批量查询订单数不超过100
     */
    @JSONField(name = "order_list")
    private String[] orderList;
    /**
     * 订单搜索页码
     */
    @JSONField(name = "page")
    private String page;
    /**
     * 每页订单数 默认10 最大100，超过则按照100处理
     */
    @JSONField(name = "size")
    private String size;

    @JSONField(name = "access_token")
    private String accessToken;

    @JSONField(name = "cust_id")
    private Long custId;

    @JSONField(name = "shop_id")
    private Long shopId;

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

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getIsDesc() {
        return isDesc;
    }

    public void setIsDesc(String isDesc) {
        this.isDesc = isDesc;
    }

    public String[] getOrderList() {
        return orderList;
    }

    public void setOrderList(String[] orderList) {
        this.orderList = orderList;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
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
        return "CBOrderListRequest{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", orderBy='" + orderBy + '\'' +
                ", vendor='" + vendor + '\'' +
                ", isDesc='" + isDesc + '\'' +
                ", orderList=" + Arrays.toString(orderList) +
                ", page='" + page + '\'' +
                ", size='" + size + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", custId=" + custId +
                ", shopId=" + shopId +
                '}';
    }
}
