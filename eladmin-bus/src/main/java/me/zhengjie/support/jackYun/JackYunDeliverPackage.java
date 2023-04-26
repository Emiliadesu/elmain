package me.zhengjie.support.jackYun;

import java.util.List;

public class JackYunDeliverPackage {
    /**
     * 物流公司代码
     * T
     */
    private String logisticsCode;
    /**
     * 物流公司名
     * F
     */
    private String logisticsName;
    /**
     * 运单号
     * T
     */
    private String expressCode;
    /**
     * 包裹编号
     * F
     */
    private String packageCode;
    /**
     * 长
     * F
     */
    private String length;
    /**
     * 宽
     * F
     */
    private String width;
    /**
     * 高
     * F
     */
    private String height;
    /**
     * 包裹理论重量
     */
    private String theoreticalWeight;
    /**
     * 包裹重量
     */
    private String weight;
    /**
     * 包裹体积
     */
    private String volume;
    /**
     * 发票号
     */
    private String invoiceNo;
    /**
     * 包材信息
     */
    private List<JackYunDeliverPackageMaterial>packageMaterialList;
    /**
     * 商品信息
     */
    private List<JackYunDeliverItem>items;

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

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getTheoreticalWeight() {
        return theoreticalWeight;
    }

    public void setTheoreticalWeight(String theoreticalWeight) {
        this.theoreticalWeight = theoreticalWeight;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public List<JackYunDeliverPackageMaterial> getPackageMaterialList() {
        return packageMaterialList;
    }

    public void setPackageMaterialList(List<JackYunDeliverPackageMaterial> packageMaterialList) {
        this.packageMaterialList = packageMaterialList;
    }

    public List<JackYunDeliverItem> getItems() {
        return items;
    }

    public void setItems(List<JackYunDeliverItem> items) {
        this.items = items;
    }
}
