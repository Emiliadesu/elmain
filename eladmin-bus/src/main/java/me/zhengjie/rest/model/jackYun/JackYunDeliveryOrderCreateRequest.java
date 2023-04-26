package me.zhengjie.rest.model.jackYun;

import java.util.List;

public class JackYunDeliveryOrderCreateRequest{
    private JackYunDeliveryOrder deliveryOrder;

    private List<JackYunDeliveryOrderOrderLine> orderLines;

    public JackYunDeliveryOrder getDeliveryOrder() {
        return deliveryOrder;
    }

    public void setDeliveryOrder(JackYunDeliveryOrder deliveryOrder) {
        this.deliveryOrder = deliveryOrder;
    }

    public List<JackYunDeliveryOrderOrderLine> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<JackYunDeliveryOrderOrderLine> orderLines) {
        this.orderLines = orderLines;
    }
}
