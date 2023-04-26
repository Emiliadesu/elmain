package me.zhengjie.support.youzan;

import me.zhengjie.support.youzan.YouzanTradeGetResult;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradesSoldGetResult;
import me.zhengjie.utils.StringUtil;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @author wangm
 * @since 2020-02-04
 */
public class YzOrderDetails implements Serializable {

    public YzOrderDetails(){}
    public YzOrderDetails(YouzanTradesSoldGetResult.YouzanTradesSoldGetResultOrders order){
        this.subOrderNo = order.getSubOrderNo();
        this.price = order.getPrice();
        this.skuId = order.getSkuId();
        this.oid = order.getOid();
        this.fenxiaoPrice = order.getFenxiaoPrice();
        this.crossBorderTradeMode = order.getCrossBorderTradeMode();
        this.outerItemId = order.getOuterItemId();
        this.totalFee = order.getTotalFee();
        this.alias = order.getAlias();
        this.outerSkuId = order.getOuterSkuId();
        this.isPreSale = order.getIsPreSale();
        this.title = order.getTitle();
        this.skuUniqueCode = order.getSkuUniqueCode();
        this.fenxiaoPayment = order.getFenxiaoPayment();
        this.itemId = order.getItemId();
        this.preSaleType = order.getPreSaleType();
        this.itemType = order.getItemType();
        this.num = order.getNum();
        this.payment = order.getPayment();
        this.pointsPrice = order.getPointsPrice();
        this.skuPropertiesName = order.getSkuPropertiesName();
        this.discountPrice = order.getDiscountPrice();
        this.fenxiaoTaxTotal=new BigDecimal(StringUtil.isNotEmpty(order.getFenxiaoTaxTotal())?order.getFenxiaoTaxTotal():"0.0");
    }
    public YzOrderDetails(YouzanTradeGetResult.YouzanTradeGetResultOrders order){
        this.subOrderNo = order.getSubOrderNo();
        this.price = order.getPrice();
        this.skuId = order.getSkuId();
        this.oid = order.getOid();
        this.fenxiaoPrice = order.getFenxiaoPrice();
        this.crossBorderTradeMode = order.getCrossBorderTradeMode();
        this.outerItemId = order.getOuterItemId();
        this.totalFee = order.getTotalFee();
        this.alias = order.getAlias();
        this.outerSkuId = order.getOuterSkuId();
        this.isPreSale = order.getIsPreSale();
        this.title = order.getTitle();
        this.skuUniqueCode = order.getSkuUniqueCode();
        this.fenxiaoPayment = order.getFenxiaoPayment();
        this.itemId = order.getItemId().longValue();
        this.preSaleType = order.getPreSaleType();
        this.itemType = order.getItemType().longValue();
        this.num = order.getNum().longValue();
        this.payment = order.getPayment();
        this.pointsPrice = order.getPointsPrice();
        this.skuPropertiesName = order.getSkuPropertiesName();
        this.discountPrice = order.getDiscountPrice();
        this.fenxiaoTaxTotal=new BigDecimal(StringUtil.isNotEmpty(order.getFenxiaoTaxTotal())?order.getFenxiaoTaxTotal():"0.0");
    }


    private String id;
    /**
     * 订单号
     */
    private String orderId;
    /**
     * 海淘订单号
     */
    private String subOrderNo;
    /**
     * 单商品原价
     */
    private String price;
    /**
     * 规格id（无规格商品为0）
     */
    private Long skuId;
    /**
     * 订单明细id
     */
    private String oid;
    /**
     * 分销单金额 ，单位元
     */
    private String fenxiaoPrice;
    /**
     * 海淘商品贸易模式
     */
    private String crossBorderTradeMode;
    /**
     * 商品编码
     */
    private String outerItemId;
    /**
     * 商品优惠后总价
     */
    private String totalFee;
    /**
     * 商品别名
     */
    private String alias;
    /**
     * 商家编码-有赞用此字段作为货号
     */
    private String outerSkuId;
    /**
     * 是否为预售商品 如果是预售商品则为1
     */
    private String isPreSale;
    /**
     * 商品名称
     */
    private String title;
    /**
     * 商品唯一编码
     */
    private String skuUniqueCode;
    /**
     * 分销单实付金额，单位元
     */
    private String fenxiaoPayment;
    /**
     * 商品id
     */
    private Long itemId;
    /**
     *  0 全款预售，1 定金预售
     */
    private String preSaleType;
    /**
     * 订单类型 0:普通类型商品; 1:拍卖商品; 5:餐饮商品; 10:分销商品; 20:会员卡商品; 21:礼品卡商品; 23:有赞会议商品; 24:周期购; 30:收银台商品;31:知识付费商品; 35:酒店商品; 40:普通服务类商品; 182:普通虚拟商品; 183:电子卡券商品; 201:外部会员卡商品; 202:外部直接收款商品;203:外部普通商品; 205:mock不存在商品; 206:小程序二维码
     */
    private Long itemType;
    /**
     * 商品数量
     */
    private Long num;
    /**
     * 商品最终均摊价
     */
    private String payment;
    /**
     * 是否赠品
     */
    private Integer isPresent;
    /**
     * 商品积分价（非积分商品则为0）
     */
    private String pointsPrice;
    /**
     * 规格信息（无规格商品为空）
     */
    private String skuPropertiesName;
    /**
     * 单商品现价，减去了商品的优惠金额
     */
    private String discountPrice;
    /**
     * 分销税费
     */
    private BigDecimal fenxiaoTaxTotal;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSubOrderNo() {
        return subOrderNo;
    }

    public void setSubOrderNo(String subOrderNo) {
        this.subOrderNo = subOrderNo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getFenxiaoPrice() {
        return fenxiaoPrice;
    }

    public void setFenxiaoPrice(String fenxiaoPrice) {
        this.fenxiaoPrice = fenxiaoPrice;
    }

    public String getCrossBorderTradeMode() {
        return crossBorderTradeMode;
    }

    public void setCrossBorderTradeMode(String crossBorderTradeMode) {
        this.crossBorderTradeMode = crossBorderTradeMode;
    }

    public String getOuterItemId() {
        return outerItemId;
    }

    public void setOuterItemId(String outerItemId) {
        this.outerItemId = outerItemId;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getOuterSkuId() {
        return outerSkuId;
    }

    public void setOuterSkuId(String outerSkuId) {
        this.outerSkuId = outerSkuId;
    }

    public String getIsPreSale() {
        return isPreSale;
    }

    public void setIsPreSale(String isPreSale) {
        this.isPreSale = isPreSale;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSkuUniqueCode() {
        return skuUniqueCode;
    }

    public void setSkuUniqueCode(String skuUniqueCode) {
        this.skuUniqueCode = skuUniqueCode;
    }

    public String getFenxiaoPayment() {
        return fenxiaoPayment;
    }

    public void setFenxiaoPayment(String fenxiaoPayment) {
        this.fenxiaoPayment = fenxiaoPayment;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getPreSaleType() {
        return preSaleType;
    }

    public void setPreSaleType(String preSaleType) {
        this.preSaleType = preSaleType;
    }

    public Long getItemType() {
        return itemType;
    }

    public void setItemType(Long itemType) {
        this.itemType = itemType;
    }

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public Integer getIsPresent() {
        return isPresent;
    }

    public void setIsPresent(Integer isPresent) {
        this.isPresent = isPresent;
    }

    public String getPointsPrice() {
        return pointsPrice;
    }

    public void setPointsPrice(String pointsPrice) {
        this.pointsPrice = pointsPrice;
    }

    public String getSkuPropertiesName() {
        return skuPropertiesName;
    }

    public void setSkuPropertiesName(String skuPropertiesName) {
        this.skuPropertiesName = skuPropertiesName;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public BigDecimal getFenxiaoTaxTotal() {
        return fenxiaoTaxTotal;
    }

    public void setFenxiaoTaxTotal(BigDecimal fenxiaoTaxTotal) {
        this.fenxiaoTaxTotal = fenxiaoTaxTotal;
    }

    @Override
    public String toString() {
        return "YzOrderDetails{" +
        "id=" + id +
        ", orderId=" + orderId +
        ", subOrderNo=" + subOrderNo +
        ", price=" + price +
        ", skuId=" + skuId +
        ", oid=" + oid +
        ", fenxiaoPrice=" + fenxiaoPrice +
        ", crossBorderTradeMode=" + crossBorderTradeMode +
        ", outerItemId=" + outerItemId +
        ", totalFee=" + totalFee +
        ", alias=" + alias +
        ", outerSkuId=" + outerSkuId +
        ", isPreSale=" + isPreSale +
        ", title=" + title +
        ", skuUniqueCode=" + skuUniqueCode +
        ", fenxiaoPayment=" + fenxiaoPayment +
        ", itemId=" + itemId +
        ", preSaleType=" + preSaleType +
        ", itemType=" + itemType +
        ", num=" + num +
        ", payment=" + payment +
        ", isPresent=" + isPresent +
        ", pointsPrice=" + pointsPrice +
        ", skuPropertiesName=" + skuPropertiesName +
        ", discountPrice=" + discountPrice +
        ", fenxiaoTaxTotal=" + fenxiaoTaxTotal +
        "}";
    }
}
