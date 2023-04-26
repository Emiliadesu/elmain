package me.zhengjie.support.ymatou;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;

public class YmatouOrderItemInfo {
    /**
     * 订单号
     */
    @JSONField(name = "order_id")
    private long orderId;
    /**
     * 子订单编号
     */
    @JSONField(name = "order_item_id")
    private String orderItemId;
    /**
     * 退货退款单ID
     */
    @JSONField(name = "refund_id")
    private String refundId;
    /**
     * 退货退款状态 null: 无退款,-1:退款审核拒绝, 0:退款审核中, 1:退款审核通过
     */
    @JSONField(name = "refund_status")
    private String refundStatus;
    /**
     * 退货数量
     */
    @JSONField(name = "refund_num")
    private Integer refundNum;
    /**
     * SkuId
     */
    @JSONField(name = "sku_id")
    private String skuId;
    /**
     * 买手商品编码
     */
    @JSONField(name = "outer_sku_id")
    private String outerSkuId;
    /**
     * 商品Id
     */
    @JSONField(name = "product_id")
    private String productId;
    /**
     * 商品名称
     */
    @JSONField(name = "product_title")
    private String productTitle;
    /**
     * SKU的属性值
     */
    @JSONField(name = "sku_properties_name")
    private String skuPropertiesName;
    /**
     * 商品物流方式
     * 1.国内现货 2. 直邮 3. 官方（贝海）直邮 4. 第三方保税 5. 官方（贝海）保税 7. 拼邮
     */
    @JSONField(name = "delivery_type")
    private Integer deliveryType;
    /**
     * 拼邮类型，null和0 ：未设置。1：国际拼邮。2：国内拼邮
     */
    @JSONField(name = "delivery_sub_type")
    private Integer deliverySubType;
    /**
     * 商品价格
     */
    private BigDecimal price;
    /**
     * 商品数量
     */
    private Integer num;
    /**
     * 支付金额
     */
    private BigDecimal payment;
    /**
     * 订单运费
     */
    @JSONField(name = "shipping_fee")
    private BigDecimal shippingFee;
    /**
     * 平台优惠券分摊金额
     */
    @JSONField(name = "p_coupon_discount")
    private BigDecimal pCouponDiscount;
    /**
     * 买手优惠券分摊金额
     */
    @JSONField(name = "m_coupon_discount")
    private BigDecimal mCouponDiscount;
    /**
     * 平台促销活动分摊金额
     */
    @JSONField(name = "p_promotion_discount")
    private BigDecimal pPromotionDiscount;
    /**
     * 买手促销活动分摊金额
     */
    @JSONField(name = "m_promotion_discount")
    private BigDecimal mPromotionDiscount;
    /**
     * 买手调价金额
     */
    @JSONField(name = "m_adjust_discount")
    private BigDecimal mAdjustDiscount;
    /**
     * 安利返点金额
     */
    @JSONField(name = "rebate_amount")
    private BigDecimal rebateAmount;
    /**
     * 组合商品信息
     */
    @JSONField(name = "sub_product_info")
    private YmatouSubProductInfo[] subProductInfo;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public Integer getRefundNum() {
        return refundNum;
    }

    public void setRefundNum(Integer refundNum) {
        this.refundNum = refundNum;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getOuterSkuId() {
        return outerSkuId;
    }

    public void setOuterSkuId(String outerSkuId) {
        this.outerSkuId = outerSkuId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getSkuPropertiesName() {
        return skuPropertiesName;
    }

    public void setSkuPropertiesName(String skuPropertiesName) {
        this.skuPropertiesName = skuPropertiesName;
    }

    public Integer getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(Integer deliveryType) {
        this.deliveryType = deliveryType;
    }

    public Integer getDeliverySubType() {
        return deliverySubType;
    }

    public void setDeliverySubType(Integer deliverySubType) {
        this.deliverySubType = deliverySubType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public BigDecimal getPayment() {
        return payment;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public BigDecimal getpCouponDiscount() {
        return pCouponDiscount;
    }

    public void setpCouponDiscount(BigDecimal pCouponDiscount) {
        this.pCouponDiscount = pCouponDiscount;
    }

    public BigDecimal getmCouponDiscount() {
        return mCouponDiscount;
    }

    public void setmCouponDiscount(BigDecimal mCouponDiscount) {
        this.mCouponDiscount = mCouponDiscount;
    }

    public BigDecimal getpPromotionDiscount() {
        return pPromotionDiscount;
    }

    public void setpPromotionDiscount(BigDecimal pPromotionDiscount) {
        this.pPromotionDiscount = pPromotionDiscount;
    }

    public BigDecimal getmPromotionDiscount() {
        return mPromotionDiscount;
    }

    public void setmPromotionDiscount(BigDecimal mPromotionDiscount) {
        this.mPromotionDiscount = mPromotionDiscount;
    }

    public BigDecimal getmAdjustDiscount() {
        return mAdjustDiscount;
    }

    public void setmAdjustDiscount(BigDecimal mAdjustDiscount) {
        this.mAdjustDiscount = mAdjustDiscount;
    }

    public BigDecimal getRebateAmount() {
        return rebateAmount;
    }

    public void setRebateAmount(BigDecimal rebateAmount) {
        this.rebateAmount = rebateAmount;
    }

    public YmatouSubProductInfo[] getSubProductInfo() {
        return subProductInfo;
    }

    public void setSubProductInfo(YmatouSubProductInfo[] subProductInfo) {
        this.subProductInfo = subProductInfo;
    }
}
