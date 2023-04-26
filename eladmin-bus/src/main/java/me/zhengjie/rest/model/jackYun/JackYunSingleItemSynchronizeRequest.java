package me.zhengjie.rest.model.jackYun;

public class JackYunSingleItemSynchronizeRequest extends JackYunBasicRequest{
    /**
     * 操作类型 add/update
     */
    private String actionType;
    /**
     * 供应商编码
     */
    private String supplierCode;
    /**
     * 供应商名称
     */
    private String supplierName;
    /**
     * 商品信息
     */
    private JackYunSingleItem item;

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public JackYunSingleItem getItem() {
        return item;
    }

    public void setItem(JackYunSingleItem item) {
        this.item = item;
    }
}
