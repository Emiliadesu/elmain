package me.zhengjie.support.ymatou;


import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

public class YmatouOrdersStatusGetAPIRequest implements CommonApiParam {
    @JSONField(name = "order_ids")
    private Long[] order_ids;

    public Long[] getOrder_ids() {
        return order_ids;
    }

    public void setOrder_ids(Long[] order_ids) {
        this.order_ids = order_ids;
    }

    @Override
    public String getMethod() {
        return "ymatou.orders.status.get";
    }

    @Override
    public String getKeyWord() {
        return "";
    }
}
