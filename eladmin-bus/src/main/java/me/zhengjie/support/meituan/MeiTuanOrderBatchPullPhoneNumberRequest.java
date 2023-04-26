package me.zhengjie.support.meituan;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

public class MeiTuanOrderBatchPullPhoneNumberRequest implements CommonApiParam {
    /**
     * 订单id
     */
    @JSONField(name = "app_poi_code")
    private String appPoiCode;

    /**
     * 分页查询的偏移量，表示本次查询从第几条数据开始查，0是第一条。
     */
    private String offset;

    /**
     * 分页查询每页展示数量，最大不能超过1000。
     */
    private String limit;

    public String getAppPoiCode() {
        return appPoiCode;
    }

    public void setAppPoiCode(String appPoiCode) {
        this.appPoiCode = appPoiCode;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    @Override
    public String getMethod() {
        return "/api/v1/order/batchPullPhoneNumber";
    }

    @Override
    public String getKeyWord() {
        return getAppPoiCode();
    }
}
