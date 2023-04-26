package me.zhengjie.support.jingdong;

public class JDLdopReceiveTraceGetRequest {

    private String customerCode;   //商家编码

    private String waybillCode;   //运单号

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }
}
