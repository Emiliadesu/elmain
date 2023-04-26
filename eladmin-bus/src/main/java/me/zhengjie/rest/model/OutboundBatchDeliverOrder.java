package me.zhengjie.rest.model;

import cn.afterturn.easypoi.excel.annotation.Excel;

public class OutboundBatchDeliverOrder {
    @Excel(name = "序号")
    private Integer index;

    @Excel(name = "省份")
    private String province;

    @Excel(name = "运单号")
    private String logisticsNo;

    @Excel(name = "重量",type = 10)
    private String weight;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getLogisticsNo() {
        return logisticsNo;
    }

    public void setLogisticsNo(String logisticsNo) {
        this.logisticsNo = logisticsNo;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
