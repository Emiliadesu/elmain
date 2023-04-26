package me.zhengjie.support.jackYun;

import me.zhengjie.support.CommonApiParam;

import java.util.List;

public class JackYunDeliverRequest implements CommonApiParam{
    private JackYunDeliverOrder deliveryOrder;
    /**
     *  包裹信息
     */
    private List<JackYunDeliverPackage>packages;
    /**
     * 单据列表
     */
    private List<JackYunDeliverOrderLine>orderLines;

    public JackYunDeliverOrder getDeliveryOrder() {
        return deliveryOrder;
    }

    public void setDeliveryOrder(JackYunDeliverOrder deliveryOrder) {
        this.deliveryOrder = deliveryOrder;
    }

    public List<JackYunDeliverPackage> getPackages() {
        return packages;
    }

    public void setPackages(List<JackYunDeliverPackage> packages) {
        this.packages = packages;
    }

    public List<JackYunDeliverOrderLine> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<JackYunDeliverOrderLine> orderLines) {
        this.orderLines = orderLines;
    }

    @Override
    public String getMethod() {
        return "deliveryorder.confirm";
    }

    @Override
    public String getKeyWord() {
        return "deliveryorder.confirm";
    }
}
