package me.zhengjie.support.douyin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.utils.StringUtil;

import java.math.BigDecimal;
import java.util.List;

public class CBOrderReturnMain{

    @JSONField(name = "cust_id")
    private Long custId; // 本系统货主ID

    @JSONField(name = "l_shop_id")
    private Long lShopId;// 本系统店铺ID

    /**
     * 物流订单号
     */
    @JSONField(name = "logistics_no")
    private String logisticsNo;

    /**
     * 退货单号
     */
    @JSONField(name = "trade_return_no")
    private String tradeReturnNo;

    /**
     * 店铺在电商平台的id
     */
    @JSONField(name = "shop_id")
    private String shopId;
    /**
     * 店铺在电商平台的名称
     */
    @JSONField(name = "shop_name")
    private String shopName;
    /**
     * 交易订单号
     */
    @JSONField(name = "order_id")
    private String orderId;
    /**
     * 是否入区
     */
    @JSONField(name = "is_border")
    private Integer isBorder;
    /**
     * 售后类型
     */
    @JSONField(name = "after_sale_type")
    private Integer afterSaleType;
    /**
     * 售后单号
     */
    @JSONField(name = "after_sale_no")
    private String afterSaleNo;
    /**
     * 订单清关时间
     */
    @JSONField(name = "sale_customs_time")
    private String saleCustomsTime;
    /**
     * 正向物流单号
     */
    @JSONField(name = "s_express_no")
    private String sExpressNo;
    /**
     * 正向物流公司
     */
    @JSONField(name = "s_express_name")
    private String sExpressName;
    /**
     * 逆向物流单号
     */
    @JSONField(name = "r_express_no")
    private String rExpressNo;
    /**
     * 逆向物流公司
     */
    @JSONField(name = "r_express_name")
    private String rExpressName;
    /**
     * 退货类型(整单/部分退货）
     */
    @JSONField(name = "return_type")
    private Integer returnType;
    /**
     * 服务商编码
     */
    @JSONField(name = "vendor")
    private String vendor;
    /**
     * 退货仓名称
     */
    @JSONField(name = "r_warehouse_name")
    private String rWarehouseName;
    /**
     * 退货仓资源编码
     */
    @JSONField(name = "r_warehouse_code")
    private String rWarehouseCode;


    @JSONField(name = "order_detail_list")
    private List<CBOrderReturnChild> itemList;

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    public Long getlShopId() {
        return lShopId;
    }

    public void setlShopId(Long lShopId) {
        this.lShopId = lShopId;
    }

    public String getLogisticsNo() {
        return logisticsNo;
    }

    public void setLogisticsNo(String logisticsNo) {
        this.logisticsNo = logisticsNo;
    }

    public String getTradeReturnNo() {
        return tradeReturnNo;
    }

    public void setTradeReturnNo(String tradeReturnNo) {
        this.tradeReturnNo = tradeReturnNo;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getIsBorder() {
        return isBorder;
    }

    public void setIsBorder(Integer isBorder) {
        this.isBorder = isBorder;
    }

    public Integer getAfterSaleType() {
        return afterSaleType;
    }

    public void setAfterSaleType(Integer afterSaleType) {
        this.afterSaleType = afterSaleType;
    }

    public String getAfterSaleNo() {
        return afterSaleNo;
    }

    public void setAfterSaleNo(String afterSaleNo) {
        this.afterSaleNo = afterSaleNo;
    }

    public String getSaleCustomsTime() {
        return saleCustomsTime;
    }

    public void setSaleCustomsTime(String saleCustomsTime) {
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

    public Integer getReturnType() {
        return returnType;
    }

    public void setReturnType(Integer returnType) {
        this.returnType = returnType;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getrWarehouseName() {
        return rWarehouseName;
    }

    public void setrWarehouseName(String rWarehouseName) {
        this.rWarehouseName = rWarehouseName;
    }

    public String getrWarehouseCode() {
        return rWarehouseCode;
    }

    public void setrWarehouseCode(String rWarehouseCode) {
        this.rWarehouseCode = rWarehouseCode;
    }

    public List<CBOrderReturnChild> getItemList() {
        return itemList;
    }

    public void setItemList(List<CBOrderReturnChild> itemList) {
        this.itemList = itemList;
    }
}
