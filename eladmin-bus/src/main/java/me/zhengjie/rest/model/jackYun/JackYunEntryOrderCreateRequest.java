package me.zhengjie.rest.model.jackYun;

import java.util.List;

public class JackYunEntryOrderCreateRequest {
    private JackYunEntryOrder entryOrder;
    private List<JackYunEntryOrderOrderLine>orderLines;

    public JackYunEntryOrder getEntryOrder() {
        return entryOrder;
    }

    public void setEntryOrder(JackYunEntryOrder entryOrder) {
        this.entryOrder = entryOrder;
    }

    public List<JackYunEntryOrderOrderLine> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<JackYunEntryOrderOrderLine> orderLines) {
        this.orderLines = orderLines;
    }
}
