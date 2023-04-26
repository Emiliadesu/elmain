package me.zhengjie.support.meituan;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

public class MeiTuanGetCrossBorderDetailRequest implements CommonApiParam {
    @Override
    public String getMethod() {
        return "/api/v1/ecommerce/order/getCrossBorderDetail";
    }

    @Override
    public String getKeyWord() {
        return getAppPoiCode();
    }


    /**
     * 门店ID
     */
    @JSONField(name = "app_poi_code")
    private String appPoiCode;

    /**
     * 订单展示id
     */
    @JSONField(name = "wm_order_id_view")
    private String wmOrderIdView;

    public String getAppPoiCode() {
        return appPoiCode;
    }

    public void setAppPoiCode(String appPoiCode) {
        this.appPoiCode = appPoiCode;
    }

    public String getWmOrderIdView() {
        return wmOrderIdView;
    }

    public void setWmOrderIdView(String wmOrderIdView) {
        this.wmOrderIdView = wmOrderIdView;
    }
}
