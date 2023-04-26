package me.zhengjie.rest.model.ruoyuchen.request;

public class RuoYuChenOrderCancel {
    /**
     * 货主编码
     * Y
     */
    private String ownerCode;
    /**
     * 单据类型 Y
     * (JYCK=一般交易出库单;HHCK=换货出库;
     * BFCK=补发出库;PTCK=普通出库单;
     * DBCK=调拨出库;QTCK=其他出库;
     * B2BRK=B2B入库;B2BCK=B2B出库; CGRK=采购入库;DBRK=调拨入库; QTRK=其他入库;THRK=销退入库;
     * HHRK=换货入库;CNJG=仓内加工单;
     * CKPD=库存盘点;LYRK=领料入库; LYCK=领料出库;PYRK=盘盈入库;
     * PKCK=盘亏出库;CGTH=采购退货。)
     *
     */
    private String orderType;
    /**
     * 单据编码
     * Y
     */
    private String orderCode;
    /**
     * 仓储系统单据编码
     */
    private String orderId;
    /**
     * 取消的原因
     */
    private String cancelReason;

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }
}
