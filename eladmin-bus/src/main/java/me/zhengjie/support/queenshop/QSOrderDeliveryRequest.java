package me.zhengjie.support.queenshop;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

import java.util.Date;

public class QSOrderDeliveryRequest implements CommonApiParam {

    @Override
    public String getMethod() {
        return "qs.pop.trade.delivery";
    }

    @Override
    public String getKeyWord() {
        return getOrderNo();
    }

    @JSONField(name = "orderNo")
    private String orderNo;

    @JSONField(name = "logisticsNo")
    private String logisticsNo;

    @JSONField(name = "logisticsName")
    private String logisticsName;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getLogisticsNo() {
        return logisticsNo;
    }

    public void setLogisticsNo(String logisticsNo) {
        this.logisticsNo = logisticsNo;
    }

    public String getLogisticsName() {
        return logisticsName;
    }

    public void setLogisticsName(String logisticsName) {
        this.logisticsName = logisticsName;
    }

    @Override
    public String toString() {
        return "QSOrderDeliveryRequest{" +
                "orderNo='" + orderNo + '\'' +
                ", logisticsNo='" + logisticsNo + '\'' +
                ", logisticsName='" + logisticsName + '\'' +
                '}';
    }
}
