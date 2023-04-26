package me.zhengjie.support.youzan;

import java.io.Serializable;
import java.util.List;
import java.util.Date;
import com.alibaba.fastjson.annotation.JSONField;

public class YouzanTradeGetResult implements Serializable {

    public static final Long serialVersionUID = 1L;
    /** 表示本次请求是否成功。 true:成功 false：失败 */
    @JSONField(name = "success")
    private boolean success;
    /** 网关返回码描述 */
    @JSONField(name = "message")
    private String message;

    @JSONField(name = "data")
    private YouzanTradeGetResultData data;
    /** 网关返回码，表示本次请求是否成功。200 :成功。 */
    @JSONField(name = "code")
    private int code;

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean getSuccess() {
        return this.success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setData(YouzanTradeGetResultData data) {
        this.data = data;
    }

    public YouzanTradeGetResultData getData() {
        return this.data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public static class YouzanTradeGetResultOrders {

        /** 商品留言 */
        @JSONField(name = "buyer_messages")
        private String buyerMessages;
        /** 商品id，有赞生成的商品唯一值。 */
        @JSONField(name = "item_id")
        private Long itemId;
        /** 非现金抵扣金额 */
        @JSONField(name = "discount")
        private String discount;
        /** 订单商品扩展信息 */
        @JSONField(name = "order_item_extra")
        private String orderItemExtra;
        /** 商品规格id，有赞生成的规格id。无规格商品返回0 */
        @JSONField(name = "sku_id")
        private Long skuId;
        /** 是否赠品，是：true，否：false */
        @JSONField(name = "is_present")
        private Boolean isPresent;
        /** 商品唯一编码 */
        @JSONField(name = "sku_unique_code")
        private String skuUniqueCode;
        /** 0 全款预售，1 定金预售 */
        @JSONField(name = "pre_sale_type")
        private String preSaleType;
        /** 分销单实付金额 ，单位元 */
        @JSONField(name = "fenxiao_payment")
        private String fenxiaoPayment;
        /** 商品规格编码，sku_id值不为0时：返回商品规格编码；sku_id值为0时：当设置商品编码后且开启多网点功能，返回值为商品编码，否则返回空。 */
        @JSONField(name = "outer_sku_id")
        private String outerSkuId;
        /** 单商品现价，减去了商品的优惠金额。单位：元 */
        @JSONField(name = "discount_price")
        private String discountPrice;
        /** 海淘订单子订单号 */
        @JSONField(name = "sub_order_no")
        private String subOrderNo;
        /** 订单明细id */
        @JSONField(name = "oid")
        private String oid;
        /** 税费，单位：元 */
        @JSONField(name = "tax_total")
        private String taxTotal;
        /** 商品别名 */
        @JSONField(name = "alias")
        private String alias;
        /** 商品数量 */
        @JSONField(name = "num")
        private Integer num;
        /** 商品规格信息（无规格商品为空） */
        @JSONField(name = "sku_properties_name")
        private String skuPropertiesName;
        /** 是否为预售商品，是：返回为1，否返回为空 */
        @JSONField(name = "is_pre_sale")
        private String isPreSale;
        /** 商品图片地址 */
        @JSONField(name = "pic_path")
        private String picPath;
        /** 海关编号，更多海关编码请访问：https://doc.youzanyun.com/doc#/content/27031/27100 */
        @JSONField(name = "customs_code")
        private String customsCode;
        /** 分销商品金额,单位：元 */
        @JSONField(name = "fenxiao_discount_price")
        private String fenxiaoDiscountPrice;
        /** 单商品原价，单位：元 */
        @JSONField(name = "price")
        private String price;
        /** 商品名称 */
        @JSONField(name = "title")
        private String title;
        /**
         * 订单类型 0:普通类型商品; 1:拍卖商品; 5:餐饮商品; 10:分销商品; 20:会员卡商品; 21:礼品卡商品; 23:有赞会议商品; 24:周期购; 30:收银台商品;
         * 31:知识付费商品; 35:酒店商品; 40:普通服务类商品; 182:普通虚拟商品; 183:电子卡券商品; 201:外部会员卡商品; 202:外部直接收款商品;
         * 203:外部普通商品; 205:mock不存在商品; 206:小程序二维码
         */
        @JSONField(name = "item_type")
        private Integer itemType;
        /** 分销税费,单位：元 */
        @JSONField(name = "fenxiao_tax_total")
        private String fenxiaoTaxTotal;
        /** 分销运费,单位：元 */
        @JSONField(name = "fenxiao_freight")
        private String fenxiaoFreight;
        /** 商品详情链接 */
        @JSONField(name = "goods_url")
        private String goodsUrl;
        /** 海淘商品贸易模式，BC直邮：9610，BBC保税进口：1210 */
        @JSONField(name = "cross_border_trade_mode")
        private String crossBorderTradeMode;
        /** 是否是跨境海淘订单，是：返回为1，否返回为空 */
        @JSONField(name = "is_cross_border")
        private String isCrossBorder;
        /** 商品优惠前总价,单位：元 */
        @JSONField(name = "total_fee")
        private String totalFee;
        /** 商品最终均摊价，单位：元 */
        @JSONField(name = "payment")
        private String payment;
        /** 分销非现金抵扣金额,单位：元 */
        @JSONField(name = "fenxiao_discount")
        private String fenxiaoDiscount;
        /** 运杂费，单位：元 */
        @JSONField(name = "freight")
        private String freight;
        /** 商品编码，商家自定义编码，可用于和其他系统之间商品的外部id的标识。 */
        @JSONField(name = "outer_item_id")
        private String outerItemId;
        /** 分销单金额 ，单位元 */
        @JSONField(name = "fenxiao_price")
        private String fenxiaoPrice;
        /** 商品积分价（非积分商品则为0），如返回值是100则表示100积分。 */
        @JSONField(name = "points_price")
        private String pointsPrice;

        public void setBuyerMessages(String buyerMessages) {
            this.buyerMessages = buyerMessages;
        }

        public String getBuyerMessages() {
            return this.buyerMessages;
        }

        public void setItemId(Long itemId) {
            this.itemId = itemId;
        }

        public Long getItemId() {
            return this.itemId;
        }

        public void setDiscount(String discount) {
            this.discount = discount;
        }

        public String getDiscount() {
            return this.discount;
        }

        public void setOrderItemExtra(String orderItemExtra) {
            this.orderItemExtra = orderItemExtra;
        }

        public String getOrderItemExtra() {
            return this.orderItemExtra;
        }

        public void setSkuId(Long skuId) {
            this.skuId = skuId;
        }

        public Long getSkuId() {
            return this.skuId;
        }

        public void setIsPresent(Boolean isPresent) {
            this.isPresent = isPresent;
        }

        public Boolean getIsPresent() {
            return this.isPresent;
        }

        public void setSkuUniqueCode(String skuUniqueCode) {
            this.skuUniqueCode = skuUniqueCode;
        }

        public String getSkuUniqueCode() {
            return this.skuUniqueCode;
        }

        public void setPreSaleType(String preSaleType) {
            this.preSaleType = preSaleType;
        }

        public String getPreSaleType() {
            return this.preSaleType;
        }

        public void setFenxiaoPayment(String fenxiaoPayment) {
            this.fenxiaoPayment = fenxiaoPayment;
        }

        public String getFenxiaoPayment() {
            return this.fenxiaoPayment;
        }

        public void setOuterSkuId(String outerSkuId) {
            this.outerSkuId = outerSkuId;
        }

        public String getOuterSkuId() {
            return this.outerSkuId;
        }

        public void setDiscountPrice(String discountPrice) {
            this.discountPrice = discountPrice;
        }

        public String getDiscountPrice() {
            return this.discountPrice;
        }

        public void setSubOrderNo(String subOrderNo) {
            this.subOrderNo = subOrderNo;
        }

        public String getSubOrderNo() {
            return this.subOrderNo;
        }

        public void setOid(String oid) {
            this.oid = oid;
        }

        public String getOid() {
            return this.oid;
        }

        public void setTaxTotal(String taxTotal) {
            this.taxTotal = taxTotal;
        }

        public String getTaxTotal() {
            return this.taxTotal;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getAlias() {
            return this.alias;
        }

        public void setNum(Integer num) {
            this.num = num;
        }

        public Integer getNum() {
            return this.num;
        }

        public void setSkuPropertiesName(String skuPropertiesName) {
            this.skuPropertiesName = skuPropertiesName;
        }

        public String getSkuPropertiesName() {
            return this.skuPropertiesName;
        }

        public void setIsPreSale(String isPreSale) {
            this.isPreSale = isPreSale;
        }

        public String getIsPreSale() {
            return this.isPreSale;
        }

        public void setPicPath(String picPath) {
            this.picPath = picPath;
        }

        public String getPicPath() {
            return this.picPath;
        }

        public void setCustomsCode(String customsCode) {
            this.customsCode = customsCode;
        }

        public String getCustomsCode() {
            return this.customsCode;
        }

        public void setFenxiaoDiscountPrice(String fenxiaoDiscountPrice) {
            this.fenxiaoDiscountPrice = fenxiaoDiscountPrice;
        }

        public String getFenxiaoDiscountPrice() {
            return this.fenxiaoDiscountPrice;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getPrice() {
            return this.price;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return this.title;
        }

        public void setItemType(Integer itemType) {
            this.itemType = itemType;
        }

        public Integer getItemType() {
            return this.itemType;
        }

        public void setFenxiaoTaxTotal(String fenxiaoTaxTotal) {
            this.fenxiaoTaxTotal = fenxiaoTaxTotal;
        }

        public String getFenxiaoTaxTotal() {
            return this.fenxiaoTaxTotal;
        }

        public void setFenxiaoFreight(String fenxiaoFreight) {
            this.fenxiaoFreight = fenxiaoFreight;
        }

        public String getFenxiaoFreight() {
            return this.fenxiaoFreight;
        }

        public void setGoodsUrl(String goodsUrl) {
            this.goodsUrl = goodsUrl;
        }

        public String getGoodsUrl() {
            return this.goodsUrl;
        }

        public void setCrossBorderTradeMode(String crossBorderTradeMode) {
            this.crossBorderTradeMode = crossBorderTradeMode;
        }

        public String getCrossBorderTradeMode() {
            return this.crossBorderTradeMode;
        }

        public void setIsCrossBorder(String isCrossBorder) {
            this.isCrossBorder = isCrossBorder;
        }

        public String getIsCrossBorder() {
            return this.isCrossBorder;
        }

        public void setTotalFee(String totalFee) {
            this.totalFee = totalFee;
        }

        public String getTotalFee() {
            return this.totalFee;
        }

        public void setPayment(String payment) {
            this.payment = payment;
        }

        public String getPayment() {
            return this.payment;
        }

        public void setFenxiaoDiscount(String fenxiaoDiscount) {
            this.fenxiaoDiscount = fenxiaoDiscount;
        }

        public String getFenxiaoDiscount() {
            return this.fenxiaoDiscount;
        }

        public void setFreight(String freight) {
            this.freight = freight;
        }

        public String getFreight() {
            return this.freight;
        }

        public void setOuterItemId(String outerItemId) {
            this.outerItemId = outerItemId;
        }

        public String getOuterItemId() {
            return this.outerItemId;
        }

        public void setFenxiaoPrice(String fenxiaoPrice) {
            this.fenxiaoPrice = fenxiaoPrice;
        }

        public String getFenxiaoPrice() {
            return this.fenxiaoPrice;
        }

        public void setPointsPrice(String pointsPrice) {
            this.pointsPrice = pointsPrice;
        }

        public String getPointsPrice() {
            return this.pointsPrice;
        }
    }

    public static class YouzanTradeGetResultData {

        @JSONField(name = "full_order_info")
        private YouzanTradeGetResultFullorderinfo fullOrderInfo;

        @JSONField(name = "delivery_order")
        private List<YouzanTradeGetResultDeliveryorder> deliveryOrder;

        @JSONField(name = "order_promotion")
        private YouzanTradeGetResultOrderpromotion orderPromotion;

        @JSONField(name = "refund_order")
        private List<YouzanTradeGetResultRefundorder> refundOrder;

        public void setFullOrderInfo(YouzanTradeGetResultFullorderinfo fullOrderInfo) {
            this.fullOrderInfo = fullOrderInfo;
        }

        public YouzanTradeGetResultFullorderinfo getFullOrderInfo() {
            return this.fullOrderInfo;
        }

        public void setDeliveryOrder(List<YouzanTradeGetResultDeliveryorder> deliveryOrder) {
            this.deliveryOrder = deliveryOrder;
        }

        public List<YouzanTradeGetResultDeliveryorder> getDeliveryOrder() {
            return this.deliveryOrder;
        }

        public void setOrderPromotion(YouzanTradeGetResultOrderpromotion orderPromotion) {
            this.orderPromotion = orderPromotion;
        }

        public YouzanTradeGetResultOrderpromotion getOrderPromotion() {
            return this.orderPromotion;
        }

        public void setRefundOrder(List<YouzanTradeGetResultRefundorder> refundOrder) {
            this.refundOrder = refundOrder;
        }

        public List<YouzanTradeGetResultRefundorder> getRefundOrder() {
            return this.refundOrder;
        }
    }

    public static class YouzanTradeGetResultSource {

        /** 微信平台细分。 wx_gzh:微信公众号; yzdh:有赞大号; merchant_xcx:商家小程序; yzdh_xcx:有赞大号小程序; direct_buy:直接购买 */
        @JSONField(name = "wx_entrance")
        private String wxEntrance;
        /**
         * 平台 wx:微信; merchant_3rd:商家自有app; buyer_v:买家版; browser:系统浏览器; alipay:支付宝;qq:腾讯QQ; wb:微博;
         * other:其他
         */
        @JSONField(name = "platform")
        private String platform;

        public void setWxEntrance(String wxEntrance) {
            this.wxEntrance = wxEntrance;
        }

        public String getWxEntrance() {
            return this.wxEntrance;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getPlatform() {
            return this.platform;
        }
    }

    public static class YouzanTradeGetResultInvoiceinfo {

        /** 买家税号 */
        @JSONField(name = "taxpayer_id")
        private String taxpayerId;
        /** 抬头类型 */
        @JSONField(name = "raise_type")
        private String raiseType;
        /** 抬头 */
        @JSONField(name = "user_name")
        private String userName;
        /** 发票详情类型 */
        @JSONField(name = "invoice_detail_type")
        private String invoiceDetailType;
        /** 买家邮箱 */
        @JSONField(name = "email")
        private String email;

        public void setTaxpayerId(String taxpayerId) {
            this.taxpayerId = taxpayerId;
        }

        public String getTaxpayerId() {
            return this.taxpayerId;
        }

        public void setRaiseType(String raiseType) {
            this.raiseType = raiseType;
        }

        public String getRaiseType() {
            return this.raiseType;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserName() {
            return this.userName;
        }

        public void setInvoiceDetailType(String invoiceDetailType) {
            this.invoiceDetailType = invoiceDetailType;
        }

        public String getInvoiceDetailType() {
            return this.invoiceDetailType;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getEmail() {
            return this.email;
        }
    }

    public static class YouzanTradeGetResultRefundorder {

        /** 有赞生成的退款id */
        @JSONField(name = "refund_id")
        private String refundId;

        @JSONField(name = "oids")
        private List<YouzanTradeGetResultOids> oids;
        /** 退款金额，单位：元 */
        @JSONField(name = "refund_fee")
        private String refundFee;
        /**
         * 退款状态 1:买家已经申请退款，等待卖家同意， 10:卖家拒绝退款， 20:卖家已经同意退货，等待买家退货， 30:买家已经退货，等待卖家确认收货， 40:卖家未收到货,拒绝退款，
         * 50:退款关闭， 60:退款成功，
         */
        @JSONField(name = "refund_state")
        private Integer refundState;
        /**
         * 退款类型 1:买家申请退款，2:商家主动退款，
         * 3:一键退款。以下场景会触发一键退款（一键退款主要是由有赞服务器基于以下条件自动触发）订单关闭退款，拼团未成团退款，返现退款，团购返现退款，小程序拼团退款，送礼子订单未被领取退款，送礼社群版到期自动退款，美业退款，订单少付退款，酒店拒单，拼团扣库存失败，代付过期退款，代付超付退款，外卖拒单退，超付+本金组合退款，上云商家一键退款类型，会员卡发卡失败退款。
         */
        @JSONField(name = "refund_type")
        private Integer refundType;

        public void setRefundId(String refundId) {
            this.refundId = refundId;
        }

        public String getRefundId() {
            return this.refundId;
        }

        public void setOids(List<YouzanTradeGetResultOids> oids) {
            this.oids = oids;
        }

        public List<YouzanTradeGetResultOids> getOids() {
            return this.oids;
        }

        public void setRefundFee(String refundFee) {
            this.refundFee = refundFee;
        }

        public String getRefundFee() {
            return this.refundFee;
        }

        public void setRefundState(Integer refundState) {
            this.refundState = refundState;
        }

        public Integer getRefundState() {
            return this.refundState;
        }

        public void setRefundType(Integer refundType) {
            this.refundType = refundType;
        }

        public Integer getRefundType() {
            return this.refundType;
        }
    }

    public static class YouzanTradeGetResultFullorderinfo {

        @JSONField(name = "orders")
        private List<YouzanTradeGetResultOrders> orders;

        @JSONField(name = "address_info")
        private YouzanTradeGetResultAddressinfo addressInfo;

        @JSONField(name = "pay_info")
        private YouzanTradeGetResultPayinfo payInfo;

        @JSONField(name = "order_info")
        private YouzanTradeGetResultOrderinfo orderInfo;

        @JSONField(name = "child_info")
        private YouzanTradeGetResultChildinfo childInfo;

        @JSONField(name = "source_info")
        private YouzanTradeGetResultSourceinfo sourceInfo;

        @JSONField(name = "remark_info")
        private YouzanTradeGetResultRemarkinfo remarkInfo;

        @JSONField(name = "invoice_info")
        private YouzanTradeGetResultInvoiceinfo invoiceInfo;

        @JSONField(name = "buyer_info")
        private YouzanTradeGetResultBuyerinfo buyerInfo;

        public void setOrders(List<YouzanTradeGetResultOrders> orders) {
            this.orders = orders;
        }

        public List<YouzanTradeGetResultOrders> getOrders() {
            return this.orders;
        }

        public void setAddressInfo(YouzanTradeGetResultAddressinfo addressInfo) {
            this.addressInfo = addressInfo;
        }

        public YouzanTradeGetResultAddressinfo getAddressInfo() {
            return this.addressInfo;
        }

        public void setPayInfo(YouzanTradeGetResultPayinfo payInfo) {
            this.payInfo = payInfo;
        }

        public YouzanTradeGetResultPayinfo getPayInfo() {
            return this.payInfo;
        }

        public void setOrderInfo(YouzanTradeGetResultOrderinfo orderInfo) {
            this.orderInfo = orderInfo;
        }

        public YouzanTradeGetResultOrderinfo getOrderInfo() {
            return this.orderInfo;
        }

        public void setChildInfo(YouzanTradeGetResultChildinfo childInfo) {
            this.childInfo = childInfo;
        }

        public YouzanTradeGetResultChildinfo getChildInfo() {
            return this.childInfo;
        }

        public void setSourceInfo(YouzanTradeGetResultSourceinfo sourceInfo) {
            this.sourceInfo = sourceInfo;
        }

        public YouzanTradeGetResultSourceinfo getSourceInfo() {
            return this.sourceInfo;
        }

        public void setRemarkInfo(YouzanTradeGetResultRemarkinfo remarkInfo) {
            this.remarkInfo = remarkInfo;
        }

        public YouzanTradeGetResultRemarkinfo getRemarkInfo() {
            return this.remarkInfo;
        }

        public void setInvoiceInfo(YouzanTradeGetResultInvoiceinfo invoiceInfo) {
            this.invoiceInfo = invoiceInfo;
        }

        public YouzanTradeGetResultInvoiceinfo getInvoiceInfo() {
            return this.invoiceInfo;
        }

        public void setBuyerInfo(YouzanTradeGetResultBuyerinfo buyerInfo) {
            this.buyerInfo = buyerInfo;
        }

        public YouzanTradeGetResultBuyerinfo getBuyerInfo() {
            return this.buyerInfo;
        }
    }

    public static class YouzanTradeGetResultAddressinfo {

        /** 到店自提信息 json格式，字段说明请参考：https://developers.youzanyun.com/article/1556781783745 */
        @JSONField(name = "self_fetch_info")
        private String selfFetchInfo;
        /** 市 */
        @JSONField(name = "delivery_city")
        private String deliveryCity;
        /**
         * 字段为json格式，需要开发者自行解析 lng、lon（经纬度）； checkOutTime（酒店退房时间）； recipients（入住人）； checkInTime（酒店入住时间）；
         * idCardNumber（海淘身份证信息）； areaCode（邮政编码）
         */
        @JSONField(name = "address_extra")
        private String addressExtra;
        /** 同城送预计送达时间-结束时间 非同城送以及没有开启定时达的订单不返回 */
        @JSONField(name = "delivery_end_time")
        private Date deliveryEndTime;
        /** 省 */
        @JSONField(name = "delivery_province")
        private String deliveryProvince;
        /** 收货人姓名 */
        @JSONField(name = "receiver_name")
        private String receiverName;
        /** 同城送预计送达时间-开始时间 非同城送以及没有开启定时达的订单不返回 */
        @JSONField(name = "delivery_start_time")
        private Date deliveryStartTime;
        /** 收货人手机号 */
        @JSONField(name = "receiver_tel")
        private String receiverTel;
        /** 邮政编码 */
        @JSONField(name = "delivery_postal_code")
        private String deliveryPostalCode;
        /** 详细地址 */
        @JSONField(name = "delivery_address")
        private String deliveryAddress;
        /** 区 */
        @JSONField(name = "delivery_district")
        private String deliveryDistrict;

        public void setSelfFetchInfo(String selfFetchInfo) {
            this.selfFetchInfo = selfFetchInfo;
        }

        public String getSelfFetchInfo() {
            return this.selfFetchInfo;
        }

        public void setDeliveryCity(String deliveryCity) {
            this.deliveryCity = deliveryCity;
        }

        public String getDeliveryCity() {
            return this.deliveryCity;
        }

        public void setAddressExtra(String addressExtra) {
            this.addressExtra = addressExtra;
        }

        public String getAddressExtra() {
            return this.addressExtra;
        }

        public void setDeliveryEndTime(Date deliveryEndTime) {
            this.deliveryEndTime = deliveryEndTime;
        }

        public Date getDeliveryEndTime() {
            return this.deliveryEndTime;
        }

        public void setDeliveryProvince(String deliveryProvince) {
            this.deliveryProvince = deliveryProvince;
        }

        public String getDeliveryProvince() {
            return this.deliveryProvince;
        }

        public void setReceiverName(String receiverName) {
            this.receiverName = receiverName;
        }

        public String getReceiverName() {
            return this.receiverName;
        }

        public void setDeliveryStartTime(Date deliveryStartTime) {
            this.deliveryStartTime = deliveryStartTime;
        }

        public Date getDeliveryStartTime() {
            return this.deliveryStartTime;
        }

        public void setReceiverTel(String receiverTel) {
            this.receiverTel = receiverTel;
        }

        public String getReceiverTel() {
            return this.receiverTel;
        }

        public void setDeliveryPostalCode(String deliveryPostalCode) {
            this.deliveryPostalCode = deliveryPostalCode;
        }

        public String getDeliveryPostalCode() {
            return this.deliveryPostalCode;
        }

        public void setDeliveryAddress(String deliveryAddress) {
            this.deliveryAddress = deliveryAddress;
        }

        public String getDeliveryAddress() {
            return this.deliveryAddress;
        }

        public void setDeliveryDistrict(String deliveryDistrict) {
            this.deliveryDistrict = deliveryDistrict;
        }

        public String getDeliveryDistrict() {
            return this.deliveryDistrict;
        }
    }

    public static class YouzanTradeGetResultSourceinfo {

        /** 是否来自线下订单 */
        @JSONField(name = "is_offline_order")
        private Boolean isOfflineOrder;
        /** 订单唯一识别码 */
        @JSONField(name = "book_key")
        private String bookKey;

        @JSONField(name = "source")
        private YouzanTradeGetResultSource source;
        /**
         * 订单标记 wx_apps:微信小程序买家版 wx_shop:微信小程序商家版 wx_wm:微信小程序外卖 wap_wm:移动端外卖 super_store:超级门店
         * weapp_spotlight:新微信小程序买家版 wx_meiye:美业小程序 wx_apps_maidan:小程序餐饮买单 wx_apps_diancan:小程序堂食
         * weapp_youzan:有赞小程序 retail_free_buy:零售自由购 weapp_owl:知识付费小程序 app_spotlight:有赞精选app
         * retail_scan_buy:零售扫码购 weapp_plugin:小程序插件 除以上之外为其他
         */
        @JSONField(name = "order_mark")
        private String orderMark;
        /** 活动类型：群团购：”mall_group_buy“ */
        @JSONField(name = "biz_source")
        private String bizSource;

        public void setIsOfflineOrder(Boolean isOfflineOrder) {
            this.isOfflineOrder = isOfflineOrder;
        }

        public Boolean getIsOfflineOrder() {
            return this.isOfflineOrder;
        }

        public void setBookKey(String bookKey) {
            this.bookKey = bookKey;
        }

        public String getBookKey() {
            return this.bookKey;
        }

        public void setSource(YouzanTradeGetResultSource source) {
            this.source = source;
        }

        public YouzanTradeGetResultSource getSource() {
            return this.source;
        }

        public void setOrderMark(String orderMark) {
            this.orderMark = orderMark;
        }

        public String getOrderMark() {
            return this.orderMark;
        }

        public void setBizSource(String bizSource) {
            this.bizSource = bizSource;
        }

        public String getBizSource() {
            return this.bizSource;
        }
    }

    public static class YouzanTradeGetResultPayinfo {

        @JSONField(name = "phase_payments")
        private List<YouzanTradeGetResultPhasepayments> phasePayments;
        /** 外部订单号（即微信端交易单号） */
        @JSONField(name = "outer_transactions")
        private List<String> outerTransactions;
        /** 优惠前商品总价 */
        @JSONField(name = "total_fee")
        private String totalFee;
        /** 最终支付价格 payment=orders.payment的总和 */
        @JSONField(name = "payment")
        private String payment;
        /** 邮费 */
        @JSONField(name = "post_fee")
        private String postFee;
        /** 礼品卡/储值卡抵扣之后的订单实付金额 */
        @JSONField(name = "deduction_real_pay")
        private Long deductionRealPay;
        /** 支付流水号（即微信端商户单号） */
        @JSONField(name = "transaction")
        private List<String> transaction;
        /** 礼品卡/储值卡具体抵扣的金额 */
        @JSONField(name = "deduction_pay")
        private Long deductionPay;

        public void setPhasePayments(List<YouzanTradeGetResultPhasepayments> phasePayments) {
            this.phasePayments = phasePayments;
        }

        public List<YouzanTradeGetResultPhasepayments> getPhasePayments() {
            return this.phasePayments;
        }

        public void setOuterTransactions(List<String> outerTransactions) {
            this.outerTransactions = outerTransactions;
        }

        public List<String> getOuterTransactions() {
            return this.outerTransactions;
        }

        public void setTotalFee(String totalFee) {
            this.totalFee = totalFee;
        }

        public String getTotalFee() {
            return this.totalFee;
        }

        public void setPayment(String payment) {
            this.payment = payment;
        }

        public String getPayment() {
            return this.payment;
        }

        public void setPostFee(String postFee) {
            this.postFee = postFee;
        }

        public String getPostFee() {
            return this.postFee;
        }

        public void setDeductionRealPay(Long deductionRealPay) {
            this.deductionRealPay = deductionRealPay;
        }

        public Long getDeductionRealPay() {
            return this.deductionRealPay;
        }

        public void setTransaction(List<String> transaction) {
            this.transaction = transaction;
        }

        public List<String> getTransaction() {
            return this.transaction;
        }

        public void setDeductionPay(Long deductionPay) {
            this.deductionPay = deductionPay;
        }

        public Long getDeductionPay() {
            return this.deductionPay;
        }
    }

    public static class YouzanTradeGetResultPhasepayments {

        /** 内部支付流水号 */
        @JSONField(name = "inner_transaction_no")
        private String innerTransactionNo;
        /** 支付开始时间 */
        @JSONField(name = "pay_start_time")
        private Date payStartTime;
        /**
         * 支付类型。取值范围： WEIXIN (微信自有支付) WEIXIN_DAIXIAO (微信代销支付) ALIPAY (支付宝支付) BANKCARDPAY (银行卡支付) PEERPAY
         * (代付) CODPAY (货到付款) BAIDUPAY (百度钱包支付) PRESENTTAKE (直接领取赠品) COUPONPAY(优惠券/码全额抵扣)
         * BULKPURCHASE(来自分销商的采购) MERGEDPAY(合并付货款) ECARD(有赞E卡支付) PURCHASE_PAY (采购单支付) MARKPAY (标记收款)
         * OFCASH (现金支付) PREPAIDCARD (储值卡余额支付)ENCHASHMENT_GIFT_CARD(礼品卡支付)
         */
        @JSONField(name = "pay_way_str")
        private String payWayStr;
        /** 支付阶段 */
        @JSONField(name = "phase")
        private Integer phase;
        /** 阶段支付金额 */
        @JSONField(name = "real_price")
        private String realPrice;
        /** 支付结束时间 */
        @JSONField(name = "pay_end_time")
        private Date payEndTime;
        /** 外部支付流水号 */
        @JSONField(name = "outer_transaction_no")
        private String outerTransactionNo;

        public void setInnerTransactionNo(String innerTransactionNo) {
            this.innerTransactionNo = innerTransactionNo;
        }

        public String getInnerTransactionNo() {
            return this.innerTransactionNo;
        }

        public void setPayStartTime(Date payStartTime) {
            this.payStartTime = payStartTime;
        }

        public Date getPayStartTime() {
            return this.payStartTime;
        }

        public void setPayWayStr(String payWayStr) {
            this.payWayStr = payWayStr;
        }

        public String getPayWayStr() {
            return this.payWayStr;
        }

        public void setPhase(Integer phase) {
            this.phase = phase;
        }

        public Integer getPhase() {
            return this.phase;
        }

        public void setRealPrice(String realPrice) {
            this.realPrice = realPrice;
        }

        public String getRealPrice() {
            return this.realPrice;
        }

        public void setPayEndTime(Date payEndTime) {
            this.payEndTime = payEndTime;
        }

        public Date getPayEndTime() {
            return this.payEndTime;
        }

        public void setOuterTransactionNo(String outerTransactionNo) {
            this.outerTransactionNo = outerTransactionNo;
        }

        public String getOuterTransactionNo() {
            return this.outerTransactionNo;
        }
    }

    public static class YouzanTradeGetResultDeliveryorder {

        /** 物流状态 0:待发货; 1:已发货 */
        @JSONField(name = "express_state")
        private Integer expressState;

        @JSONField(name = "dists")
        private List<YouzanTradeGetResultDists> dists;
        /** 包裹id该字段已废弃， 请使用dists中的dist_id字段 */
        @JSONField(name = "pk_id")
        private Long pkId;
        /** 发货方式。 0:手动发货（商城后台人工发货），1:接口发货（有赞云发货API发货） */
        @JSONField(name = "express_type")
        private Integer expressType;

        @JSONField(name = "oids")
        private List<YouzanTradeGetResultOids> oids;

        public void setExpressState(Integer expressState) {
            this.expressState = expressState;
        }

        public Integer getExpressState() {
            return this.expressState;
        }

        public void setDists(List<YouzanTradeGetResultDists> dists) {
            this.dists = dists;
        }

        public List<YouzanTradeGetResultDists> getDists() {
            return this.dists;
        }

        public void setPkId(Long pkId) {
            this.pkId = pkId;
        }

        public Long getPkId() {
            return this.pkId;
        }

        public void setExpressType(Integer expressType) {
            this.expressType = expressType;
        }

        public Integer getExpressType() {
            return this.expressType;
        }

        public void setOids(List<YouzanTradeGetResultOids> oids) {
            this.oids = oids;
        }

        public List<YouzanTradeGetResultOids> getOids() {
            return this.oids;
        }
    }

    public static class YouzanTradeGetResultItem {

        /** 规格id，有赞生成的商品规格唯一id */
        @JSONField(name = "sku_id")
        private Long skuId;
        /** 是否是赠品，false：不是，true：是 */
        @JSONField(name = "is_present")
        private Boolean isPresent;
        /** 交易明细id */
        @JSONField(name = "oid")
        private String oid;

        @JSONField(name = "promotions")
        private List<YouzanTradeGetResultPromotions> promotions;
        /** 商品id，有赞生成的商品唯一id */
        @JSONField(name = "item_id")
        private Long itemId;

        public void setSkuId(Long skuId) {
            this.skuId = skuId;
        }

        public Long getSkuId() {
            return this.skuId;
        }

        public void setIsPresent(Boolean isPresent) {
            this.isPresent = isPresent;
        }

        public Boolean getIsPresent() {
            return this.isPresent;
        }

        public void setOid(String oid) {
            this.oid = oid;
        }

        public String getOid() {
            return this.oid;
        }

        public void setPromotions(List<YouzanTradeGetResultPromotions> promotions) {
            this.promotions = promotions;
        }

        public List<YouzanTradeGetResultPromotions> getPromotions() {
            return this.promotions;
        }

        public void setItemId(Long itemId) {
            this.itemId = itemId;
        }

        public Long getItemId() {
            return this.itemId;
        }
    }

    public static class YouzanTradeGetResultOrderinfo {

        /** 配送方式（物流类型）， 0:快递发货; 1:到店自提; 2:同城配送; 9:无需发货（虚拟商品订单） */
        @JSONField(name = "express_type")
        private Integer expressType;

        @JSONField(name = "order_extra")
        private YouzanTradeGetResultOrderextra orderExtra;
        /** 多人拼团成功时间（仅限多人拼团使用），其他类型返回为空。 */
        @JSONField(name = "confirm_time")
        private Date confirmTime;
        /** 多网点id，非多网点订单，该字段不返回。 */
        @JSONField(name = "offline_id")
        private Long offlineId;

        @JSONField(name = "order_tags")
        private YouzanTradeGetResultOrdertags orderTags;
        /**
         * 主订单类型 0:普通订单; 1:送礼订单; 2:代付; 3:分销采购单; 4:赠品; 5:心愿单; 6:二维码订单; 7:合并付货款; 8:1分钱实名认证; 9:品鉴; 10:拼团;
         * 15:返利; 35:酒店; 40:外卖; 41:堂食点餐; 46:外卖买单; 51:全员开店; 61:线下收银台订单; 71:美业预约单; 72:美业服务单; 75:知识付费;
         * 81:礼品卡; 100:批发
         */
        @JSONField(name = "type")
        private Integer type;
        /**
         * 订单关闭类型 0:未关闭; 1:过期关闭; 2:标记退款; 3:订单取消; 4:买家取消; 5:卖家取消; 6:部分退款; 10:无法联系上买家; 11:买家误拍或重拍了;
         * 12:买家无诚意完成交易; 13:已通过银行线下汇款; 14:已通过同城见面交易; 15:已通过货到付款交易; 16:已通过网上银行直接汇款; 17:已经缺货无法交易
         */
        @JSONField(name = "close_type")
        private Integer closeType;
        /**
         * 活动类型:0: "没有活动",1: "没有活动", 2: "团购返现",3: "降价拍",4: "拼团",5: "积分兑换",6: "秒杀",7: "优惠套餐",8: "赠品",9:
         * "商品扫码",10: "会员折扣",11: "限时折扣",12: "众筹",13:"周期购",14: "送礼",15: "随机点餐",16: "扫码优惠",19: "享立减",20:
         * "F码",21: "助力砍价",22: "推荐有奖",23: "抽奖拼团",24: "加价购",63: "送礼社区版",101: "满减送",102: "订单返现",103:
         * "供货商满包邮",114: "定金膨胀",115: "第二件半价",256:"积分抵现"
         */
        @JSONField(name = "activity_type")
        private Integer activityType;
        /** 交易完成时间 */
        @JSONField(name = "success_time")
        private Date successTime;
        /**
         * 主订单状态描述，和status对应关系。WAIT_BUYER_PAY：待支付，TRADE_PAID：已支付，WAIT_CONFIRM：待接单，
         * WAIT_SELLER_SEND_GOODS：待发货，WAIT_BUYER_CONFIRM_GOODS：已发货，TRADE_SUCCESS：已完成，TRADE_CLOSED：已关闭
         */
        @JSONField(name = "status_str")
        private String statusStr;
        /**
         * 店铺类型 0:微商城; 1:微小店; 2:爱学贷微商城; 3:批发店铺; 4:批发商城; 5:外卖; 6:美业; 7:超级门店; 8:收银; 9:收银加微商城; 10:零售总部;
         * 99:有赞开放平台平台型应用创建的店铺
         */
        @JSONField(name = "team_type")
        private Integer teamType;
        /**
         * 主订单状态： WAIT_BUYER_PAY：等待买家付款，定金预售描述：定金待付、等待尾款支付开始、尾款待付， TRADE_PAID：订单已支付
         * ，该状态仅代表当前订单已支付成功，表示瞬时状态，稍后会自动修改成后面的状态。如果不关心此状态请再次请求详情接口获取下一个状态，
         * WAIT_CONFIRM：待确认，包含待成团、待接单等等。即：买家已付款，等待成团或等待接单， WAIT_SELLER_SEND_GOODS：等待卖家发货，即：买家已付款，
         * WAIT_BUYER_CONFIRM_GOODS 等待买家确认收货，即：卖家已发货， TRADE_SUCCESS：买家已签收以及订单成功， TRADE_CLOSED：交易关闭
         */
        @JSONField(name = "status")
        private String status;
        /** 有赞订单号，E开头长度24位字母和数字组合 */
        @JSONField(name = "tid")
        private String tid;
        /** 订单过期时间（未付款订单关闭时间） */
        @JSONField(name = "expired_time")
        private Date expiredTime;
        /** 订单创建时间 */
        @JSONField(name = "created")
        private Date created;
        /** 订单支付时间 */
        @JSONField(name = "pay_time")
        private Date payTime;
        /** 订单发货时间（当所有商品发货后才会更新） */
        @JSONField(name = "consign_time")
        private Date consignTime;
        /**
         * 支付渠道。取值范围： WEIXIN:微信自有支付, WEIXIN_DAIXIAO :微信代销支付, ALIPAY :支付宝支付, BANKCARDPAY:银行卡支付,PEERPAY
         * :代付,CODPAY :货到付款),BAIDUPAY :百度钱包支付, PRESENTTAKE :直接领取赠品, COUPONPAY:优惠券/码全额抵扣,
         * BULKPURCHASE:来自分销商的采购, MERGEDPAY:合并付货款, ECARD:有赞E卡支付, PURCHASE_PAY :采购单支付, MARKPAY :标记收款,
         * OFCASH :现金支付, PREPAIDCARD :储值卡余额支付，ENCHASHMENT_GIFT_CARD:礼品卡支付
         */
        @JSONField(name = "pay_type_str")
        private String payTypeStr;
        /** 是否零售订单，true：是，false：否 */
        @JSONField(name = "is_retail_order")
        private Boolean isRetailOrder;
        /** 退款状态 0:未退款， 1:部分退款中，2:部分退款成功， 11:全额退款中， 12:全额退款成功。 */
        @JSONField(name = "refund_state")
        private Integer refundState;
        /** 订单更新时间 */
        @JSONField(name = "update_time")
        private Date updateTime;
        /**
         * 订单支付类型 0:默认值,未支付; 1:微信自有支付; 2:支付宝wap; 3:支付宝wap; 5:财付通; 7:代付; 8:联动优势; 9:货到付款; 10:大账号代销;
         * 11:受理模式; 12:百付宝; 13:sdk支付; 14:合并付货款; 15:赠品; 16:优惠兑换; 17:自动付货款; 18:爱学贷; 19:微信wap; 20:微信红包支付;
         * 21:返利; 22:ump红包; 24:易宝支付; 25:储值卡; 27:qq支付; 28:有赞E卡支付; 29:微信条码; 30:支付宝条码; 33:礼品卡支付; 35:会员余额;
         * 37:银行卡支付;72:微信扫码二维码支付; 100:代收账户; 300:储值账户; 400:保证金账户; 101:收款码; 102:微信; 103:支付宝; 104:刷卡;
         * 105:二维码台卡; 106:储值卡; 107:有赞E卡; 110:标记收款-自有微信支付; 111:标记收款-自有支付宝; 112:标记收款-自有POS刷卡; 113:通联刷卡支付;
         * 200:记账账户; 201:现金;202:组合支付
         */
        @JSONField(name = "pay_type")
        private Integer payType;

        public void setExpressType(Integer expressType) {
            this.expressType = expressType;
        }

        public Integer getExpressType() {
            return this.expressType;
        }

        public void setOrderExtra(YouzanTradeGetResultOrderextra orderExtra) {
            this.orderExtra = orderExtra;
        }

        public YouzanTradeGetResultOrderextra getOrderExtra() {
            return this.orderExtra;
        }

        public void setConfirmTime(Date confirmTime) {
            this.confirmTime = confirmTime;
        }

        public Date getConfirmTime() {
            return this.confirmTime;
        }

        public void setOfflineId(Long offlineId) {
            this.offlineId = offlineId;
        }

        public Long getOfflineId() {
            return this.offlineId;
        }

        public void setOrderTags(YouzanTradeGetResultOrdertags orderTags) {
            this.orderTags = orderTags;
        }

        public YouzanTradeGetResultOrdertags getOrderTags() {
            return this.orderTags;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public Integer getType() {
            return this.type;
        }

        public void setCloseType(Integer closeType) {
            this.closeType = closeType;
        }

        public Integer getCloseType() {
            return this.closeType;
        }

        public void setActivityType(Integer activityType) {
            this.activityType = activityType;
        }

        public Integer getActivityType() {
            return this.activityType;
        }

        public void setSuccessTime(Date successTime) {
            this.successTime = successTime;
        }

        public Date getSuccessTime() {
            return this.successTime;
        }

        public void setStatusStr(String statusStr) {
            this.statusStr = statusStr;
        }

        public String getStatusStr() {
            return this.statusStr;
        }

        public void setTeamType(Integer teamType) {
            this.teamType = teamType;
        }

        public Integer getTeamType() {
            return this.teamType;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return this.status;
        }

        public void setTid(String tid) {
            this.tid = tid;
        }

        public String getTid() {
            return this.tid;
        }

        public void setExpiredTime(Date expiredTime) {
            this.expiredTime = expiredTime;
        }

        public Date getExpiredTime() {
            return this.expiredTime;
        }

        public void setCreated(Date created) {
            this.created = created;
        }

        public Date getCreated() {
            return this.created;
        }

        public void setPayTime(Date payTime) {
            this.payTime = payTime;
        }

        public Date getPayTime() {
            return this.payTime;
        }

        public void setConsignTime(Date consignTime) {
            this.consignTime = consignTime;
        }

        public Date getConsignTime() {
            return this.consignTime;
        }

        public void setPayTypeStr(String payTypeStr) {
            this.payTypeStr = payTypeStr;
        }

        public String getPayTypeStr() {
            return this.payTypeStr;
        }

        public void setIsRetailOrder(Boolean isRetailOrder) {
            this.isRetailOrder = isRetailOrder;
        }

        public Boolean getIsRetailOrder() {
            return this.isRetailOrder;
        }

        public void setRefundState(Integer refundState) {
            this.refundState = refundState;
        }

        public Integer getRefundState() {
            return this.refundState;
        }

        public void setUpdateTime(Date updateTime) {
            this.updateTime = updateTime;
        }

        public Date getUpdateTime() {
            return this.updateTime;
        }

        public void setPayType(Integer payType) {
            this.payType = payType;
        }

        public Integer getPayType() {
            return this.payType;
        }
    }

    public static class YouzanTradeGetResultOrderextra {

        /** 订单返现金额，单位：分 */
        @JSONField(name = "cash")
        private Integer cash;
        /** 团购返现优惠金额，单位：分 */
        @JSONField(name = "tm_cash")
        private Integer tmCash;
        /** 父单号 */
        @JSONField(name = "parent_order_no")
        private String parentOrderNo;
        /** 是否是积分订单：1：是 0：不是 */
        @JSONField(name = "is_points_order")
        private String isPointsOrder;
        /** 身份证姓名 （订购人的身份证号字段可通过订单详情4.0接口“id_card_number ”获取） */
        @JSONField(name = "id_card_name")
        private String idCardName;
        /** 收银员名字，没有值返回为null */
        @JSONField(name = "cashier_name")
        private String cashierName;
        /** 是否来自购物车 是：true 不是：false */
        @JSONField(name = "is_from_cart")
        private String isFromCart;
        /** 拆单时店铺维度的虚拟总单号：发生拆单时，单个店铺生成了多笔订单会生成一个店铺维度的虚拟总单号 */
        @JSONField(name = "kdt_dimension_combine_id")
        private String kdtDimensionCombineId;
        /** 分销店铺id */
        @JSONField(name = "fx_kdt_id")
        private String fxKdtId;
        /** 海淘买家身份证号 */
        @JSONField(name = "id_card_number")
        private String idCardNumber;
        /** 分销订单父单号。分销采购单号，即供货商看到的订单号 */
        @JSONField(name = "purchase_order_no")
        private String purchaseOrderNo;
        /** 是否父单(分销合并订单) 是：1 其他：null */
        @JSONField(name = "is_parent_order")
        private String isParentOrder;
        /** 收银员id，没有值返回为null */
        @JSONField(name = "cashier_id")
        private String cashierId;
        /** 分销单外部支付流水号 */
        @JSONField(name = "fx_outer_transaction_no")
        private String fxOuterTransactionNo;
        /** 美业分店id */
        @JSONField(name = "dept_id")
        private String deptId;
        /** 下单人昵称 */
        @JSONField(name = "buyer_name")
        private String buyerName;
        /** 导购信息 */
        @JSONField(name = "daogou")
        private String daogou;
        /** 是否会员订单 */
        @JSONField(name = "is_member")
        private String isMember;
        /** 分销单订单号，即买家订单 */
        @JSONField(name = "fx_order_no")
        private String fxOrderNo;
        /** 是否子单(分销买家订单) 是：1 其他：null */
        @JSONField(name = "is_sub_order")
        private String isSubOrder;
        /** 虚拟总单号：一次下单发生拆单时，会生成一个虚拟总单号 */
        @JSONField(name = "orders_combine_id")
        private String ordersCombineId;
        /** 支付营销信息 */
        @JSONField(name = "pay_ump_detail")
        private String payUmpDetail;
        /** 下单设备号 */
        @JSONField(name = "create_device_id")
        private String createDeviceId;
        /** 团购返现最大返现金额，单位：分 */
        @JSONField(name = "t_cash")
        private Integer tCash;
        /** 分销单内部支付流水号 */
        @JSONField(name = "fx_inner_transaction_no")
        private String fxInnerTransactionNo;
        /** 使用了同一张优惠券&优惠码的多笔订单对应的虚拟总单号 */
        @JSONField(name = "promotion_combine_id")
        private String promotionCombineId;
        /** 发票抬头 */
        @JSONField(name = "invoice_title")
        private String invoiceTitle;
        /** 结算时间 */
        @JSONField(name = "settle_time")
        private String settleTime;

        public void setCash(Integer cash) {
            this.cash = cash;
        }

        public Integer getCash() {
            return this.cash;
        }

        public void setTmCash(Integer tmCash) {
            this.tmCash = tmCash;
        }

        public Integer getTmCash() {
            return this.tmCash;
        }

        public void setParentOrderNo(String parentOrderNo) {
            this.parentOrderNo = parentOrderNo;
        }

        public String getParentOrderNo() {
            return this.parentOrderNo;
        }

        public void setIsPointsOrder(String isPointsOrder) {
            this.isPointsOrder = isPointsOrder;
        }

        public String getIsPointsOrder() {
            return this.isPointsOrder;
        }

        public void setIdCardName(String idCardName) {
            this.idCardName = idCardName;
        }

        public String getIdCardName() {
            return this.idCardName;
        }

        public void setCashierName(String cashierName) {
            this.cashierName = cashierName;
        }

        public String getCashierName() {
            return this.cashierName;
        }

        public void setIsFromCart(String isFromCart) {
            this.isFromCart = isFromCart;
        }

        public String getIsFromCart() {
            return this.isFromCart;
        }

        public void setKdtDimensionCombineId(String kdtDimensionCombineId) {
            this.kdtDimensionCombineId = kdtDimensionCombineId;
        }

        public String getKdtDimensionCombineId() {
            return this.kdtDimensionCombineId;
        }

        public void setFxKdtId(String fxKdtId) {
            this.fxKdtId = fxKdtId;
        }

        public String getFxKdtId() {
            return this.fxKdtId;
        }

        public void setIdCardNumber(String idCardNumber) {
            this.idCardNumber = idCardNumber;
        }

        public String getIdCardNumber() {
            return this.idCardNumber;
        }

        public void setPurchaseOrderNo(String purchaseOrderNo) {
            this.purchaseOrderNo = purchaseOrderNo;
        }

        public String getPurchaseOrderNo() {
            return this.purchaseOrderNo;
        }

        public void setIsParentOrder(String isParentOrder) {
            this.isParentOrder = isParentOrder;
        }

        public String getIsParentOrder() {
            return this.isParentOrder;
        }

        public void setCashierId(String cashierId) {
            this.cashierId = cashierId;
        }

        public String getCashierId() {
            return this.cashierId;
        }

        public void setFxOuterTransactionNo(String fxOuterTransactionNo) {
            this.fxOuterTransactionNo = fxOuterTransactionNo;
        }

        public String getFxOuterTransactionNo() {
            return this.fxOuterTransactionNo;
        }

        public void setDeptId(String deptId) {
            this.deptId = deptId;
        }

        public String getDeptId() {
            return this.deptId;
        }

        public void setBuyerName(String buyerName) {
            this.buyerName = buyerName;
        }

        public String getBuyerName() {
            return this.buyerName;
        }

        public void setDaogou(String daogou) {
            this.daogou = daogou;
        }

        public String getDaogou() {
            return this.daogou;
        }

        public void setIsMember(String isMember) {
            this.isMember = isMember;
        }

        public String getIsMember() {
            return this.isMember;
        }

        public void setFxOrderNo(String fxOrderNo) {
            this.fxOrderNo = fxOrderNo;
        }

        public String getFxOrderNo() {
            return this.fxOrderNo;
        }

        public void setIsSubOrder(String isSubOrder) {
            this.isSubOrder = isSubOrder;
        }

        public String getIsSubOrder() {
            return this.isSubOrder;
        }

        public void setOrdersCombineId(String ordersCombineId) {
            this.ordersCombineId = ordersCombineId;
        }

        public String getOrdersCombineId() {
            return this.ordersCombineId;
        }

        public void setPayUmpDetail(String payUmpDetail) {
            this.payUmpDetail = payUmpDetail;
        }

        public String getPayUmpDetail() {
            return this.payUmpDetail;
        }

        public void setCreateDeviceId(String createDeviceId) {
            this.createDeviceId = createDeviceId;
        }

        public String getCreateDeviceId() {
            return this.createDeviceId;
        }

        public void setTCash(Integer tCash) {
            this.tCash = tCash;
        }

        public Integer getTCash() {
            return this.tCash;
        }

        public void setFxInnerTransactionNo(String fxInnerTransactionNo) {
            this.fxInnerTransactionNo = fxInnerTransactionNo;
        }

        public String getFxInnerTransactionNo() {
            return this.fxInnerTransactionNo;
        }

        public void setPromotionCombineId(String promotionCombineId) {
            this.promotionCombineId = promotionCombineId;
        }

        public String getPromotionCombineId() {
            return this.promotionCombineId;
        }

        public void setInvoiceTitle(String invoiceTitle) {
            this.invoiceTitle = invoiceTitle;
        }

        public String getInvoiceTitle() {
            return this.invoiceTitle;
        }

        public void setSettleTime(String settleTime) {
            this.settleTime = settleTime;
        }

        public String getSettleTime() {
            return this.settleTime;
        }
    }

    public static class YouzanTradeGetResultOids {

        /** 交易明细id */
        @JSONField(name = "oid")
        private String oid;

        public void setOid(String oid) {
            this.oid = oid;
        }

        public String getOid() {
            return this.oid;
        }
    }

    public static class YouzanTradeGetResultRemarkinfo {

        /** 订单卖家备注 */
        @JSONField(name = "trade_memo")
        private String tradeMemo;
        /** 订单标星等级： 0~5分别表示不同星级。 */
        @JSONField(name = "star")
        private Integer star;
        /** 订单买家留言 */
        @JSONField(name = "buyer_message")
        private String buyerMessage;

        public void setTradeMemo(String tradeMemo) {
            this.tradeMemo = tradeMemo;
        }

        public String getTradeMemo() {
            return this.tradeMemo;
        }

        public void setStar(Integer star) {
            this.star = star;
        }

        public Integer getStar() {
            return this.star;
        }

        public void setBuyerMessage(String buyerMessage) {
            this.buyerMessage = buyerMessage;
        }

        public String getBuyerMessage() {
            return this.buyerMessage;
        }
    }

    public static class YouzanTradeGetResultDists {

        @JSONField(name = "express_info")
        private YouzanTradeGetResultExpressinfo expressInfo;
        /** 包裹id */
        @JSONField(name = "dist_id")
        private String distId;

        public void setExpressInfo(YouzanTradeGetResultExpressinfo expressInfo) {
            this.expressInfo = expressInfo;
        }

        public YouzanTradeGetResultExpressinfo getExpressInfo() {
            return this.expressInfo;
        }

        public void setDistId(String distId) {
            this.distId = distId;
        }

        public String getDistId() {
            return this.distId;
        }
    }

    public static class YouzanTradeGetResultOrdertags {

        /** 是否采购单 */
        @JSONField(name = "is_purchase_order")
        private Boolean isPurchaseOrder;
        /** 是否支付 */
        @JSONField(name = "is_payed")
        private Boolean isPayed;
        /** 是否结算 */
        @JSONField(name = "is_settle")
        private Boolean isSettle;
        /** 是否担保交易 */
        @JSONField(name = "is_secured_transactions")
        private Boolean isSecuredTransactions;
        /** 是否分销单 */
        @JSONField(name = "is_fenxiao_order")
        private Boolean isFenxiaoOrder;
        /** 是否有退款 */
        @JSONField(name = "is_refund")
        private Boolean isRefund;
        /** 是否多门店订单 */
        @JSONField(name = "is_multi_store")
        private Boolean isMultiStore;
        /** 是否虚拟订单。true：是，false：否。 */
        @JSONField(name = "is_virtual")
        private Boolean isVirtual;
        /** 是否有维权 */
        @JSONField(name = "is_feedback")
        private Boolean isFeedback;
        /** 是否预订单 */
        @JSONField(name = "is_preorder")
        private Boolean isPreorder;
        /** 是否线下订单 */
        @JSONField(name = "is_offline_order")
        private Boolean isOfflineOrder;
        /** 是否定金预售 */
        @JSONField(name = "is_down_payment_pre")
        private Boolean isDownPaymentPre;
        /** 是否会员订单 */
        @JSONField(name = "is_member")
        private Boolean isMember;
        /** 是否享受免邮 */
        @JSONField(name = "is_postage_free")
        private Boolean isPostageFree;

        public void setIsPurchaseOrder(Boolean isPurchaseOrder) {
            this.isPurchaseOrder = isPurchaseOrder;
        }

        public Boolean getIsPurchaseOrder() {
            return this.isPurchaseOrder;
        }

        public void setIsPayed(Boolean isPayed) {
            this.isPayed = isPayed;
        }

        public Boolean getIsPayed() {
            return this.isPayed;
        }

        public void setIsSettle(Boolean isSettle) {
            this.isSettle = isSettle;
        }

        public Boolean getIsSettle() {
            return this.isSettle;
        }

        public void setIsSecuredTransactions(Boolean isSecuredTransactions) {
            this.isSecuredTransactions = isSecuredTransactions;
        }

        public Boolean getIsSecuredTransactions() {
            return this.isSecuredTransactions;
        }

        public void setIsFenxiaoOrder(Boolean isFenxiaoOrder) {
            this.isFenxiaoOrder = isFenxiaoOrder;
        }

        public Boolean getIsFenxiaoOrder() {
            return this.isFenxiaoOrder;
        }

        public void setIsRefund(Boolean isRefund) {
            this.isRefund = isRefund;
        }

        public Boolean getIsRefund() {
            return this.isRefund;
        }

        public void setIsMultiStore(Boolean isMultiStore) {
            this.isMultiStore = isMultiStore;
        }

        public Boolean getIsMultiStore() {
            return this.isMultiStore;
        }

        public void setIsVirtual(Boolean isVirtual) {
            this.isVirtual = isVirtual;
        }

        public Boolean getIsVirtual() {
            return this.isVirtual;
        }

        public void setIsFeedback(Boolean isFeedback) {
            this.isFeedback = isFeedback;
        }

        public Boolean getIsFeedback() {
            return this.isFeedback;
        }

        public void setIsPreorder(Boolean isPreorder) {
            this.isPreorder = isPreorder;
        }

        public Boolean getIsPreorder() {
            return this.isPreorder;
        }

        public void setIsOfflineOrder(Boolean isOfflineOrder) {
            this.isOfflineOrder = isOfflineOrder;
        }

        public Boolean getIsOfflineOrder() {
            return this.isOfflineOrder;
        }

        public void setIsDownPaymentPre(Boolean isDownPaymentPre) {
            this.isDownPaymentPre = isDownPaymentPre;
        }

        public Boolean getIsDownPaymentPre() {
            return this.isDownPaymentPre;
        }

        public void setIsMember(Boolean isMember) {
            this.isMember = isMember;
        }

        public Boolean getIsMember() {
            return this.isMember;
        }

        public void setIsPostageFree(Boolean isPostageFree) {
            this.isPostageFree = isPostageFree;
        }

        public Boolean getIsPostageFree() {
            return this.isPostageFree;
        }
    }

    public static class YouzanTradeGetResultOrderpromotion {

        @JSONField(name = "item")
        private List<YouzanTradeGetResultItem> item;

        @JSONField(name = "order")
        private List<YouzanTradeGetResultOrder> order;
        /** 订单级优惠总金额，单位：元 */
        @JSONField(name = "order_discount_fee")
        private String orderDiscountFee;
        /** 商品级优惠总金额，单位：元 */
        @JSONField(name = "item_discount_fee")
        private String itemDiscountFee;
        /** 订单改价金额，单位：元。带“-”负数表示涨价金额，不带“-”表示减价金额。例如：返回值：-0.01-表示涨价0.01元，0.01-表示减价0.01元。 */
        @JSONField(name = "adjust_fee")
        private String adjustFee;

        public void setItem(List<YouzanTradeGetResultItem> item) {
            this.item = item;
        }

        public List<YouzanTradeGetResultItem> getItem() {
            return this.item;
        }

        public void setOrder(List<YouzanTradeGetResultOrder> order) {
            this.order = order;
        }

        public List<YouzanTradeGetResultOrder> getOrder() {
            return this.order;
        }

        public void setOrderDiscountFee(String orderDiscountFee) {
            this.orderDiscountFee = orderDiscountFee;
        }

        public String getOrderDiscountFee() {
            return this.orderDiscountFee;
        }

        public void setItemDiscountFee(String itemDiscountFee) {
            this.itemDiscountFee = itemDiscountFee;
        }

        public String getItemDiscountFee() {
            return this.itemDiscountFee;
        }

        public void setAdjustFee(String adjustFee) {
            this.adjustFee = adjustFee;
        }

        public String getAdjustFee() {
            return this.adjustFee;
        }
    }

    public static class YouzanTradeGetResultChildinfo {

        @JSONField(name = "child_orders")
        private List<YouzanTradeGetResultChildorders> childOrders;
        /** 送礼编号 */
        @JSONField(name = "gift_no")
        private String giftNo;
        /** 送礼标记 */
        @JSONField(name = "gift_sign")
        private String giftSign;

        public void setChildOrders(List<YouzanTradeGetResultChildorders> childOrders) {
            this.childOrders = childOrders;
        }

        public List<YouzanTradeGetResultChildorders> getChildOrders() {
            return this.childOrders;
        }

        public void setGiftNo(String giftNo) {
            this.giftNo = giftNo;
        }

        public String getGiftNo() {
            return this.giftNo;
        }

        public void setGiftSign(String giftSign) {
            this.giftSign = giftSign;
        }

        public String getGiftSign() {
            return this.giftSign;
        }
    }

    public static class YouzanTradeGetResultExpressinfo {

        /** 快递单号 */
        @JSONField(name = "express_no")
        private String expressNo;
        /** 物流公司编号，详情请参考该链接（https://developers.youzanyun.com/article/1556781458693） */
        @JSONField(name = "express_id")
        private Integer expressId;

        public void setExpressNo(String expressNo) {
            this.expressNo = expressNo;
        }

        public String getExpressNo() {
            return this.expressNo;
        }

        public void setExpressId(Integer expressId) {
            this.expressId = expressId;
        }

        public Integer getExpressId() {
            return this.expressId;
        }
    }

    public static class YouzanTradeGetResultPromotions {

        /**
         * 优惠类型:
         * tuan:团购返现,auction:降价拍,groupOn:多人拼团,pointsExchange:积分抵扣,seckill:秒杀,packageBuy:优惠套餐,presentExchange:赠品领取,goodsScan:商品扫码,customerDiscount:会员折扣,timelimitedDiscount:限时折扣,paidPromotion:支付有礼,periodBuy:周期购,scanReduce:收款码优惠,meetReduce:满减送,cashBack:订单返现,supplierMeetReduce:供货商满包邮,bale:打包一口价,coupon:优惠卡券,entireDiscount:整单优惠,groupOnHeaderDiscount:团长优惠,customerPostageFree:会员包邮,periodBuyPostageFree:周期购包邮,ignoreOddChange:抹零,pfGuideMarketing:引导促销,helpCut:助力砍价,sellerDiscount:分销商等级折扣
         */
        @JSONField(name = "promotion_type")
        private String promotionType;
        /** 优惠别名 */
        @JSONField(name = "promotion_title")
        private String promotionTitle;
        /** 优惠券/码编号 */
        @JSONField(name = "coupon_id")
        private String couponId;
        /** 优惠子类型， card:优惠券 code:优惠码 */
        @JSONField(name = "sub_promotion_type")
        private String subPromotionType;
        /** 优惠类型描述 */
        @JSONField(name = "promotion_type_name")
        private String promotionTypeName;
        /** 优惠id,即商家端店铺后台该优惠活动id。 */
        @JSONField(name = "promotion_id")
        private Long promotionId;
        /** 优惠金额，单位：元 */
        @JSONField(name = "discount_fee")
        private String discountFee;
        /** 优惠类型id */
        @JSONField(name = "promotion_type_id")
        private Integer promotionTypeId;
        /** 优惠描述 */
        @JSONField(name = "promotion_condition")
        private String promotionCondition;
        /** 优惠活动别名 */
        @JSONField(name = "promotion_content")
        private String promotionContent;

        public void setPromotionType(String promotionType) {
            this.promotionType = promotionType;
        }

        public String getPromotionType() {
            return this.promotionType;
        }

        public void setPromotionTitle(String promotionTitle) {
            this.promotionTitle = promotionTitle;
        }

        public String getPromotionTitle() {
            return this.promotionTitle;
        }

        public void setCouponId(String couponId) {
            this.couponId = couponId;
        }

        public String getCouponId() {
            return this.couponId;
        }

        public void setSubPromotionType(String subPromotionType) {
            this.subPromotionType = subPromotionType;
        }

        public String getSubPromotionType() {
            return this.subPromotionType;
        }

        public void setPromotionTypeName(String promotionTypeName) {
            this.promotionTypeName = promotionTypeName;
        }

        public String getPromotionTypeName() {
            return this.promotionTypeName;
        }

        public void setPromotionId(Long promotionId) {
            this.promotionId = promotionId;
        }

        public Long getPromotionId() {
            return this.promotionId;
        }

        public void setDiscountFee(String discountFee) {
            this.discountFee = discountFee;
        }

        public String getDiscountFee() {
            return this.discountFee;
        }

        public void setPromotionTypeId(Integer promotionTypeId) {
            this.promotionTypeId = promotionTypeId;
        }

        public Integer getPromotionTypeId() {
            return this.promotionTypeId;
        }

        public void setPromotionCondition(String promotionCondition) {
            this.promotionCondition = promotionCondition;
        }

        public String getPromotionCondition() {
            return this.promotionCondition;
        }

        public void setPromotionContent(String promotionContent) {
            this.promotionContent = promotionContent;
        }

        public String getPromotionContent() {
            return this.promotionContent;
        }
    }

    public static class YouzanTradeGetResultChildorders {

        /** 收货地址详情 */
        @JSONField(name = "address_detail")
        private String addressDetail;
        /** 区 */
        @JSONField(name = "county")
        private String county;
        /** 领取人电话 */
        @JSONField(name = "user_tel")
        private String userTel;
        /** 市 */
        @JSONField(name = "city")
        private String city;
        /** 子订单编号 */
        @JSONField(name = "tid")
        private String tid;
        /** 领取人姓名 */
        @JSONField(name = "user_name")
        private String userName;
        /** 省 */
        @JSONField(name = "province")
        private String province;
        /** 老送礼订单状态：WAIT_EXPRESS(5, "待发货"), EXPRESS(6, "已发货"), SUCCESS(100, "成功") */
        @JSONField(name = "order_state")
        private String orderState;

        public void setAddressDetail(String addressDetail) {
            this.addressDetail = addressDetail;
        }

        public String getAddressDetail() {
            return this.addressDetail;
        }

        public void setCounty(String county) {
            this.county = county;
        }

        public String getCounty() {
            return this.county;
        }

        public void setUserTel(String userTel) {
            this.userTel = userTel;
        }

        public String getUserTel() {
            return this.userTel;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCity() {
            return this.city;
        }

        public void setTid(String tid) {
            this.tid = tid;
        }

        public String getTid() {
            return this.tid;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserName() {
            return this.userName;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getProvince() {
            return this.province;
        }

        public void setOrderState(String orderState) {
            this.orderState = orderState;
        }

        public String getOrderState() {
            return this.orderState;
        }
    }

    public static class YouzanTradeGetResultBuyerinfo {

        /**
         * 微信H5和微信小程序（有赞小程序和小程序插件）的订单会返回微信weixin_openid，三方App（有赞APP开店）的订单会返回open_user_id，2019年1月30号后的订单支持返回该参数
         */
        @JSONField(name = "outer_user_id")
        private String outerUserId;
        /** 买家手机号，买家在个人中心绑定手机号才会返回。否则为空。 */
        @JSONField(name = "buyer_phone")
        private String buyerPhone;
        /** 有赞粉丝id，有赞自动生成。 */
        @JSONField(name = "fans_id")
        private Long fansId;
        /** 粉丝类型， 1:自有粉丝（商家店铺后台绑定的公众号粉丝），9:代销粉丝（有赞大账号粉丝） */
        @JSONField(name = "fans_type")
        private Integer fansType;
        /** 买家id */
        @JSONField(name = "buyer_id")
        private Long buyerId;
        /** 粉丝昵称 */
        @JSONField(name = "fans_nickname")
        private String fansNickname;

        public void setOuterUserId(String outerUserId) {
            this.outerUserId = outerUserId;
        }

        public String getOuterUserId() {
            return this.outerUserId;
        }

        public void setBuyerPhone(String buyerPhone) {
            this.buyerPhone = buyerPhone;
        }

        public String getBuyerPhone() {
            return this.buyerPhone;
        }

        public void setFansId(Long fansId) {
            this.fansId = fansId;
        }

        public Long getFansId() {
            return this.fansId;
        }

        public void setFansType(Integer fansType) {
            this.fansType = fansType;
        }

        public Integer getFansType() {
            return this.fansType;
        }

        public void setBuyerId(Long buyerId) {
            this.buyerId = buyerId;
        }

        public Long getBuyerId() {
            return this.buyerId;
        }

        public void setFansNickname(String fansNickname) {
            this.fansNickname = fansNickname;
        }

        public String getFansNickname() {
            return this.fansNickname;
        }
    }

    public static class YouzanTradeGetResultOrder {

        /** 优惠别名 */
        @JSONField(name = "promotion_title")
        private String promotionTitle;
        /** 优惠金额，单位：元 */
        @JSONField(name = "discount_fee")
        private String discountFee;
        /** 优惠类型描述 */
        @JSONField(name = "promotion_type_name")
        private String promotionTypeName;
        /** 优惠子类型 card 优惠券 code 优惠码 */
        @JSONField(name = "sub_promotion_type")
        private String subPromotionType;
        /**
         * 优惠类型:
         * tuan:团购返现,auction:降价拍,groupOn:多人拼团,pointsExchange:积分抵扣,seckill:秒杀,packageBuy:优惠套餐,presentExchange:赠品领取,goodsScan:商品扫码,customerDiscount:会员折扣,timelimitedDiscount:限时折扣,paidPromotion:支付有礼,periodBuy:周期购,scanReduce:收款码优惠,meetReduce:满减送,cashBack:订单返现,supplierMeetReduce:供货商满包邮,bale:打包一口价,coupon:优惠卡券,entireDiscount:整单优惠,groupOnHeaderDiscount:团长优惠,customerPostageFree:会员包邮,periodBuyPostageFree:周期购包邮,ignoreOddChange:抹零,pfGuideMarketing:引导促销,helpCut:助力砍价,sellerDiscount:分销商等级折扣
         */
        @JSONField(name = "promotion_type")
        private String promotionType;
        /** 优惠券/码编号 */
        @JSONField(name = "coupon_id")
        private String couponId;
        /** 优惠活动别名 */
        @JSONField(name = "promotion_content")
        private String promotionContent;
        /** 优惠描述 */
        @JSONField(name = "promotion_condition")
        private String promotionCondition;
        /** 优惠类型id */
        @JSONField(name = "promotion_type_id")
        private Long promotionTypeId;
        /** 优惠id,即商家端店铺后台该优惠活动id。 */
        @JSONField(name = "promotion_id")
        private Long promotionId;

        public void setPromotionTitle(String promotionTitle) {
            this.promotionTitle = promotionTitle;
        }

        public String getPromotionTitle() {
            return this.promotionTitle;
        }

        public void setDiscountFee(String discountFee) {
            this.discountFee = discountFee;
        }

        public String getDiscountFee() {
            return this.discountFee;
        }

        public void setPromotionTypeName(String promotionTypeName) {
            this.promotionTypeName = promotionTypeName;
        }

        public String getPromotionTypeName() {
            return this.promotionTypeName;
        }

        public void setSubPromotionType(String subPromotionType) {
            this.subPromotionType = subPromotionType;
        }

        public String getSubPromotionType() {
            return this.subPromotionType;
        }

        public void setPromotionType(String promotionType) {
            this.promotionType = promotionType;
        }

        public String getPromotionType() {
            return this.promotionType;
        }

        public void setCouponId(String couponId) {
            this.couponId = couponId;
        }

        public String getCouponId() {
            return this.couponId;
        }

        public void setPromotionContent(String promotionContent) {
            this.promotionContent = promotionContent;
        }

        public String getPromotionContent() {
            return this.promotionContent;
        }

        public void setPromotionCondition(String promotionCondition) {
            this.promotionCondition = promotionCondition;
        }

        public String getPromotionCondition() {
            return this.promotionCondition;
        }

        public void setPromotionTypeId(Long promotionTypeId) {
            this.promotionTypeId = promotionTypeId;
        }

        public Long getPromotionTypeId() {
            return this.promotionTypeId;
        }

        public void setPromotionId(Long promotionId) {
            this.promotionId = promotionId;
        }

        public Long getPromotionId() {
            return this.promotionId;
        }
    }
}
