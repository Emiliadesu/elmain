package me.zhengjie.rest.model.ruoyuchen.request;

public class RuoYuChenTallyOrderConfirm {
    /**
     * 货主编码
     * Y
     */
    private String ownerCode;
    /**
     * 入库单号-erp的
     * Y
     */
    private String entryOrderCode;
    /**
     * 入库单号-仓库的
     * N
     */
    private String entryOrderId;
    /**
     * 理货单状态 (FULFILLED-确认;CANCELED-取消。)
     * Y
     */
    private String status;

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public String getEntryOrderCode() {
        return entryOrderCode;
    }

    public void setEntryOrderCode(String entryOrderCode) {
        this.entryOrderCode = entryOrderCode;
    }

    public String getEntryOrderId() {
        return entryOrderId;
    }

    public void setEntryOrderId(String entryOrderId) {
        this.entryOrderId = entryOrderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
