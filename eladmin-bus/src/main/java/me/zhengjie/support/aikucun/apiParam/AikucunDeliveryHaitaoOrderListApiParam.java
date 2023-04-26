package me.zhengjie.support.aikucun.apiParam;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;


public class AikucunDeliveryHaitaoOrderListApiParam implements CommonApiParam {
    @JSONField(name = "fields")
    private String fields;

    @JSONField(name = "page")
    private Integer page;

    @JSONField(name = "pageSize")
    private Integer pageSize;

    @JSONField(name = "status")
    private Integer status;

    @JSONField(name = "startDate")
    private String startDate;

    @JSONField(name = "endDate")
    private String endDate;

    @JSONField(name = "activityId")
    private Long activityId;

    @JSONField(name = "adOrderId")
    private String adOrderId;

    @JSONField(name = "cust_id")
    private Long custId; // 本系统货主ID

    @JSONField(name = "l_shop_id")
    private Long lShopId;// 本系统店铺ID

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getAdOrderId() {
        return adOrderId;
    }

    public void setAdOrderId(String adOrderId) {
        this.adOrderId = adOrderId;
    }

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    public Long getlShopId() {
        return lShopId;
    }

    public void setlShopId(Long lShopId) {
        this.lShopId = lShopId;
    }

    @Override
    public String getMethod() {
        return "aikucun.delivery.oversea.order.list";
    }

    @Override
    public String getKeyWord() {
        return "";
    }
}
