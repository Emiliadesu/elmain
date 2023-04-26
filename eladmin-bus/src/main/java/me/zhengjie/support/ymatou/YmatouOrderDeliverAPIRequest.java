package me.zhengjie.support.ymatou;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

public class YmatouOrderDeliverAPIRequest implements CommonApiParam {
    @JSONField(name = "deliver_orders")
    private YmatouDeliverOrder[]deliverOrders;

    public YmatouDeliverOrder[] getDeliverOrders() {
        return deliverOrders;
    }

    public void setDeliverOrders(YmatouDeliverOrder[] deliverOrders) {
        this.deliverOrders = deliverOrders;
    }

    @Override
    public String getMethod() {
        return "ymatou.order.deliver";
    }

    @Override
    public String getKeyWord() {
        return "";
    }
}
