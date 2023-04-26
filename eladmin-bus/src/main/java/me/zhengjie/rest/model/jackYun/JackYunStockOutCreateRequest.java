package me.zhengjie.rest.model.jackYun;

import java.util.List;

public class JackYunStockOutCreateRequest {
    private JackYunStockOutOrder deliveryOrder;
    private List<JackYunEntryOrderOrderLine>orderLines;

    public JackYunStockOutOrder getDeliveryOrder() {
        return deliveryOrder;
    }

    public List<JackYunEntryOrderOrderLine> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<JackYunEntryOrderOrderLine> orderLines) {
        this.orderLines = orderLines;
    }

    public void setDeliveryOrder(JackYunStockOutOrder deliveryOrder) {
        this.deliveryOrder = deliveryOrder;
    }
}
