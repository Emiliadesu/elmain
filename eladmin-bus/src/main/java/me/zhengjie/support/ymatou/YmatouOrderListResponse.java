package me.zhengjie.support.ymatou;

import com.alibaba.fastjson.annotation.JSONField;

public class YmatouOrderListResponse {
    @JSONField(name = "total")
    private Integer total;
    @JSONField(name = "needPrintLog")
    private Boolean needPrintLog;
    @JSONField(name = "orders_info")
    private YmatouOrderInfo[] ordersInfo;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Boolean getNeedPrintLog() {
        return needPrintLog;
    }

    public void setNeedPrintLog(Boolean needPrintLog) {
        this.needPrintLog = needPrintLog;
    }

    public YmatouOrderInfo[] getOrdersInfo() {
        return ordersInfo;
    }

    public void setOrdersInfo(YmatouOrderInfo[] ordersInfo) {
        this.ordersInfo = ordersInfo;
    }
}
