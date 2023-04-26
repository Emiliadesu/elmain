package me.zhengjie.support.ymatou;

import com.alibaba.fastjson.annotation.JSONField;

public class YmatouOrdersStatusGetResponse {
    @JSONField(name = "needPrintLog")
    private Boolean needPrintLog;
    @JSONField(name = "orders_status")
    private YmatouOrderStatus[] ordersStatus;

    public Boolean getNeedPrintLog() {
        return needPrintLog;
    }

    public void setNeedPrintLog(Boolean needPrintLog) {
        this.needPrintLog = needPrintLog;
    }

    public YmatouOrderStatus[] getOrdersStatus() {
        return ordersStatus;
    }

    public void setOrdersStatus(YmatouOrderStatus[] ordersStatus) {
        this.ordersStatus = ordersStatus;
    }
}
