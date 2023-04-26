package me.zhengjie.support.ymatou;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;


import java.util.Date;

public class YmatouOrderListAPIRequest implements CommonApiParam {
    @JSONField(name = "order_status")
    private String orderStatus;

    @JSONField(name = "date_type")
    private Integer dateType;

    @JSONField(name = "sort_type")
    private Integer sortType;

    @JSONField(name = "start_date")
    private Date startDate;

    @JSONField(name = "end_date")
    private Date endDate;

    @JSONField(name = "page_no")
    private Integer pageNo;

    @JSONField(name = "page_rows")
    private Integer pageRows;

    @JSONField(name = "l_shop_id")
    private Long lShopId;

    @JSONField(name = "cust_id")
    private Long custId;

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getDateType() {
        return dateType;
    }

    public void setDateType(Integer dateType) {
        this.dateType = dateType;
    }

    public Integer getSortType() {
        return sortType;
    }

    public void setSortType(Integer sortType) {
        this.sortType = sortType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageRows() {
        return pageRows;
    }

    public void setPageRows(Integer pageRows) {
        this.pageRows = pageRows;
    }

    public Long getLShopId() {
        return lShopId;
    }

    public void setLShopId(Long lShopId) {
        this.lShopId = lShopId;
    }

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    @Override
    public String getMethod() {
        return "ymatou.order.list.get";
    }

    @Override
    public String getKeyWord() {
        return "";
    }
}
