package me.zhengjie.support.ymatou;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;

public class YmatouOrderInfo {
    /**
     * 买手id
     */
    @JSONField(name = "seller_id")
    private String sellerId;
    /**
     * 订单id
     */
    @JSONField(name = "order_id")
    private Long orderId;
    /**
     * 主单号  合并支付产生的id
     */
    @JSONField(name = "trade_id")
    private String tradeId;
    /**
     * 订单状态
     */
    @JSONField(name = "order_status")
    private Integer orderStatus;
    /**
     * 订单金额
     */
    private BigDecimal amount;
    /**
     * 买家支付金额
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
     * 下单时间 yyyy-MM-dd HH:mm:ss
     */
    @JSONField(name = "order_time")
    private String orderTime;
    /**
     * 付款时间 yyyy-MM-dd HH:mm:ss
     */
    @JSONField(name = "paid_time")
    private String paidTime;
    /**
     * 发货时间 yyyy-MM-dd HH:mm:ss
     */
    @JSONField(name = "shipping_time")
    private String shippingTime;
    /**
     * 取消时间 yyyy-MM-dd HH:mm:ss
     */
    @JSONField(name = "cancel_time")
    private String cancelTime;
    /**
     * 接单时间 yyyy-MM-dd HH:mm:ss
     */
    @JSONField(name = "accept_time")
    private String acceptTime;
    /**
     * 买手备注
     */
    @JSONField(name = "seller_memo")
    private String sellerMemo;
    /**
     * 	买家留言
     */
    @JSONField(name = "buyer_remark")
    private String buyerRemark;
    /**
     * 买家id
     */
    @JSONField(name = "buyer_id")
    private String buyerId;
    /**
     * 收件人姓名
     */
    @JSONField(name = "receiver_name")
    private String receiverName;
    /**
     * 	收件人国家
     */
    @JSONField(name = "receiver_state")
    private String receiverState;
    /**
     * 收件人地址
     */
    @JSONField(name = "receiver_address")
    private String receiverAddress;
    /**
     * 收件人邮编
     */
    @JSONField(name = "receiver_zip")
    private String receiverZip;
    /**
     * 收件人手机
     */
    @JSONField(name = "receiver_mobile")
    private String receiverMobile;
    /**
     * 收件人电话
     */
    @JSONField(name = "receiver_phone")
    private String receiverPhone;
    /**
     * 收件人邮箱
     */
    @JSONField(name = "receiver_mail")
    private String receiverMail;
    /**
     * 支付订单号
     */
    @JSONField(name = "payment_order_no")
    private String paymentOrderNo;
    /**
     * 支付公司流水号,支付宝/微信的支付流水号
     */
    @JSONField(name = "payment_transaction_no")
    private String paymentTransactionNo;
    /**
     * 支付类型 CmbPay:招行一网通, Alipay:支付宝, Weixin:微信, ApplePay:ApplePay
     */
    @JSONField(name = "pay_type")
    private String payType;
    /**
     * 是否国内段已发货
     */
    @JSONField(name = "domestic_delivered")
    private Boolean domesticDelivered;
    /**
     * 证件信息列表
     */
    @JSONField(name = "id_cards")
    private YmatouIdCard[] idCards;
    /**
     * 订单商品明细
     */
    @JSONField(name = "order_items_info")
    private YmatouOrderItemInfo[] orderItemsInfo;
    /**
     * 物流发货信息
     */
    @JSONField(name = "delivery_info")
    private YmatouDeliveryInfo[] deliveryInfo;
    /**
     * 是否预订
     */
    @JSONField(name = "pre_sale")
    private Boolean preSale;

    @JSONField(name = "l_shop_id")
    private Long lShopId;

    @JSONField(name = "cust_id")
    private Long custId;

    @JSONField(name = "shop_id")
    private String shopId;

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getPaidTime() {
        return paidTime;
    }

    public void setPaidTime(String paidTime) {
        this.paidTime = paidTime;
    }

    public String getShippingTime() {
        return shippingTime;
    }

    public void setShippingTime(String shippingTime) {
        this.shippingTime = shippingTime;
    }

    public String getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(String cancelTime) {
        this.cancelTime = cancelTime;
    }

    public String getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(String acceptTime) {
        this.acceptTime = acceptTime;
    }

    public String getSellerMemo() {
        return sellerMemo;
    }

    public void setSellerMemo(String sellerMemo) {
        this.sellerMemo = sellerMemo;
    }

    public String getBuyerRemark() {
        return buyerRemark;
    }

    public void setBuyerRemark(String buyerRemark) {
        this.buyerRemark = buyerRemark;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverState() {
        return receiverState;
    }

    public void setReceiverState(String receiverState) {
        this.receiverState = receiverState;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverZip() {
        return receiverZip;
    }

    public void setReceiverZip(String receiverZip) {
        this.receiverZip = receiverZip;
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverMail() {
        return receiverMail;
    }

    public void setReceiverMail(String receiverMail) {
        this.receiverMail = receiverMail;
    }

    public String getPaymentOrderNo() {
        return paymentOrderNo;
    }

    public void setPaymentOrderNo(String paymentOrderNo) {
        this.paymentOrderNo = paymentOrderNo;
    }

    public String getPaymentTransactionNo() {
        return paymentTransactionNo;
    }

    public void setPaymentTransactionNo(String paymentTransactionNo) {
        this.paymentTransactionNo = paymentTransactionNo;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public Boolean isDomesticDelivered() {
        return domesticDelivered;
    }

    public void setDomesticDelivered(Boolean domesticDelivered) {
        this.domesticDelivered = domesticDelivered;
    }

    public YmatouIdCard[] getIdCards() {
        return idCards;
    }

    public void setIdCards(YmatouIdCard[] idCards) {
        this.idCards = idCards;
    }

    public YmatouOrderItemInfo[] getOrderItemsInfo() {
        return orderItemsInfo;
    }

    public void setOrderItemsInfo(YmatouOrderItemInfo[] orderItemsInfo) {
        this.orderItemsInfo = orderItemsInfo;
    }

    public YmatouDeliveryInfo[] getDeliveryInfo() {
        return deliveryInfo;
    }

    public void setDeliveryInfo(YmatouDeliveryInfo[] deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }

    public Boolean isPreSale() {
        return preSale;
    }

    public void setPreSale(Boolean preSale) {
        this.preSale = preSale;
    }

    public Boolean getDomesticDelivered() {
        return domesticDelivered;
    }

    public Boolean getPreSale() {
        return preSale;
    }

    public Long getlShopId() {
        return lShopId;
    }

    public void setlShopId(Long lShopId) {
        this.lShopId = lShopId;
    }

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }
}
