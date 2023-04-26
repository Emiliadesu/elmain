package me.zhengjie.support.jackYun;

import me.zhengjie.support.CommonApiParam;

import java.util.List;

public class JackYunEntryOrderConfirmRequest implements CommonApiParam {
    private JackYunEntryOrderConfirm entryOrder;
    private List<JackYunDeliverOrderLine> orderLines;

    public List<JackYunDeliverOrderLine> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<JackYunDeliverOrderLine> orderLines) {
        this.orderLines = orderLines;
    }

    public JackYunEntryOrderConfirm getEntryOrder() {
        return entryOrder;
    }

    public void setEntryOrder(JackYunEntryOrderConfirm entryOrder) {
        this.entryOrder = entryOrder;
    }

    @Override
    public String getMethod() {
        return "entryorder.confirm";
    }

    @Override
    public String getKeyWord() {
        return null;
    }
}
