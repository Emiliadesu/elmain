package me.zhengjie.support.ruoYuChen.request;

public class RuoYuChenOutBoundOrderBackPackage {
    /**
     * 物流快递编码
     * Y
     */
    private String logisticsCode;
    /**
     * 物流公司名
     * N
     */
    private String logisticsName;
    /**
     * 运单号
     * Y
     */
    private String expressCode;
    /**
     * 包裹重量
     */
    private String weight;

    public String getLogisticsCode() {
        return logisticsCode;
    }

    public void setLogisticsCode(String logisticsCode) {
        this.logisticsCode = logisticsCode;
    }

    public String getLogisticsName() {
        return logisticsName;
    }

    public void setLogisticsName(String logisticsName) {
        this.logisticsName = logisticsName;
    }

    public String getExpressCode() {
        return expressCode;
    }

    public void setExpressCode(String expressCode) {
        this.expressCode = expressCode;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
