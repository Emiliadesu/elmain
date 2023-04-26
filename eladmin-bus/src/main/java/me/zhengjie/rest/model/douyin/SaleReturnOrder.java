package me.zhengjie.rest.model.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author luob
 * @description 抖音推送销退单
 * @date 2023/1/7
 */
public class SaleReturnOrder {

    /**
     * LO单号
     */
    @JsonProperty(value = "logistics_no")
    @JSONField(name = "logistics_no")
    private String logisticsNo;

    /**
     * LFO单号（唯一键）
     */
    @JsonProperty(value = "logistics_fulfil_no")
    @JSONField(name = "logistics_fulfil_no")
    private String logisticsFulfilNo;

    /**
     * 行业退货单号，退货仓和保税仓单号相同
     * 库存上记录的入库单号
     */
    @JsonProperty(value = "trade_return_no")
    @JSONField(name = "trade_return_no")
    private String tradeReturnNo;

    /**
     * 店铺ID
     */
    @JsonProperty(value = "shop_id")
    @JSONField(name = "shop_id")
    private Long shopId;

    /**
     * 货主名称
     */
    @JsonProperty(value = "shop_name")
    @JSONField(name = "shop_name")
    private String shopName;

    /**
     * 货主ID
     */
    @JsonProperty(value = "owner_id")
    @JSONField(name = "owner_id")
    private Long ownerId;

    /**
     * 货主类型: "CB_OWNER"-类自营 "CB_OWNER_POP"-跨境pop货主。
     */
    @JsonProperty(value = "owner_type")
    @JSONField(name = "owner_type")
    private String ownerType;

    /**
     * 交易销售单号
     */
    @JsonProperty(value = "trade_sale_no")
    @JSONField(name = "trade_sale_no")
    private String tradeSaleNo;

    /**
     * 订单交易方式,1-bc直邮集货2-bc直邮备货3-cc直邮集货4-cc直邮备货5-bbc保税备货
     */
    @JsonProperty(value = "trade_type")
    @JSONField(name = "trade_type")
    private String tradeType;

    /**
     * 是否入区,0-不入区1-入区
     */
    @JsonProperty(value = "is_border")
    @JSONField(name = "is_border")
    private String isBorder;

    /**
     * 售后类型,0:发货后退货退款;1:发货后仅退款
     */
    @JsonProperty(value = "after_sale_type")
    @JSONField(name = "after_sale_type")
    private String afterSaleType;

    /**
     * 售后单号
     */
    @JsonProperty(value = "after_sale_no")
    @JSONField(name = "after_sale_no")
    private String afterSaleNo;

    /**
     * 销售清关时间
     */
    @JsonProperty(value = "sale_customs_time")
    @JSONField(name = "sale_customs_time")
    private Long saleCustomsTime;

    /**
     * 正向物流单号
     */
    @JsonProperty(value = "s_express_no")
    @JSONField(name = "s_express_no")
    private String sExpressNo;

    /**
     * 正向物流名称
     */
    @JsonProperty(value = "s_express_name")
    @JSONField(name = "s_express_name")
    private String sExpressName;

    /**
     * 逆向物流单号
     */
    @JsonProperty(value = "r_express_no")
    @JSONField(name = "r_express_no")
    private String rExpressNo;

    /**
     * 逆向物流名称
     */
    @JsonProperty(value = "r_express_name")
    @JSONField(name = "r_express_name")
    private String rExpressName;

    /**
     * 退货类型,0-部分退货1-整单
     */
    @JsonProperty(value = "return_type")
    @JSONField(name = "return_type")
    private String returnType;

    /**
     * 服务商编码
     */
    @JsonProperty(value = "vendor")
    @JSONField(name = "vendor")
    private String vendor;

    /**
     * 入库仓编码，单据类型=跨境逆向退货，传退货仓编码,单据类型=跨境保税退货，传正向仓编码
     */
    @JsonProperty(value = "r_warehouse_code")
    @JSONField(name = "r_warehouse_code")
    private String rWarehouseCode;

    /**
     * 跨境销退类型，1-跨境逆向退货，2-跨境保税退货
     */
    @JsonProperty(value = "cb_sale_return_type")
    @JSONField(name = "cb_sale_return_type")
    private String cbSaleReturnType;

    /**
     * 订单详情
     */
    @JsonProperty(value = "order_details")
    @JSONField(name = "order_details")
    private List<SaleReturnOrderDetail> orderDetails;

    public String getLogisticsNo() {
        return logisticsNo;
    }

    public void setLogisticsNo(String logisticsNo) {
        this.logisticsNo = logisticsNo;
    }

    public String getLogisticsFulfilNo() {
        return logisticsFulfilNo;
    }

    public void setLogisticsFulfilNo(String logisticsFulfilNo) {
        this.logisticsFulfilNo = logisticsFulfilNo;
    }

    public String getTradeReturnNo() {
        return tradeReturnNo;
    }

    public void setTradeReturnNo(String tradeReturnNo) {
        this.tradeReturnNo = tradeReturnNo;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    public String getTradeSaleNo() {
        return tradeSaleNo;
    }

    public void setTradeSaleNo(String tradeSaleNo) {
        this.tradeSaleNo = tradeSaleNo;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getIsBorder() {
        return isBorder;
    }

    public void setIsBorder(String isBorder) {
        this.isBorder = isBorder;
    }

    public String getAfterSaleType() {
        return afterSaleType;
    }

    public void setAfterSaleType(String afterSaleType) {
        this.afterSaleType = afterSaleType;
    }

    public String getAfterSaleNo() {
        return afterSaleNo;
    }

    public void setAfterSaleNo(String afterSaleNo) {
        this.afterSaleNo = afterSaleNo;
    }

    public Long getSaleCustomsTime() {
        return saleCustomsTime;
    }

    public void setSaleCustomsTime(Long saleCustomsTime) {
        this.saleCustomsTime = saleCustomsTime;
    }

    public String getsExpressNo() {
        return sExpressNo;
    }

    public void setsExpressNo(String sExpressNo) {
        this.sExpressNo = sExpressNo;
    }

    public String getsExpressName() {
        return sExpressName;
    }

    public void setsExpressName(String sExpressName) {
        this.sExpressName = sExpressName;
    }

    public String getrExpressNo() {
        return rExpressNo;
    }

    public void setrExpressNo(String rExpressNo) {
        this.rExpressNo = rExpressNo;
    }

    public String getrExpressName() {
        return rExpressName;
    }

    public void setrExpressName(String rExpressName) {
        this.rExpressName = rExpressName;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getrWarehouseCode() {
        return rWarehouseCode;
    }

    public void setrWarehouseCode(String rWarehouseCode) {
        this.rWarehouseCode = rWarehouseCode;
    }

    public String getCbSaleReturnType() {
        return cbSaleReturnType;
    }

    public void setCbSaleReturnType(String cbSaleReturnType) {
        this.cbSaleReturnType = cbSaleReturnType;
    }

    public List<SaleReturnOrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<SaleReturnOrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }
}
