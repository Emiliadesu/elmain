package me.zhengjie.rest.model.meituan;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;

public class MeiTuanOrder {
    /**
     * 订单号
     */
    @JSONField(name = "order_id")
    private Long orderId;

    /**
     * 订单展示ID，与用户端、商家端订单详情中展示的订单号码一致。
     */
    @JSONField(name = "wm_order_id_view")
    private Long wmOrderIdView;

    /**
     * APP方门店id，即商家中台系统里门店的编码。如商家在操作绑定门店至开放平台应用中时，未绑定三方门店id信息，则默认APP方门店id与美团门店id相同。
     */
    @JSONField(name = "app_poi_code")
    private String appPoiCode;

    /**
     * 商家门店名称
     */
    @JSONField(name = "wm_poi_name")
    private String wmPoiName;

    /**
     * 订单收货人地址
     * 示例：美团·大众点评北京总部 (恒电大厦B座 3号工位)@#北京市北京市朝阳区屏翠东路屏翠东路美团·大众点评北京总部
     */
    @JSONField(name = "recipient_address")
    private String recipientAddress;

    /**
     * 订单收货人四级地址
     * 示例：美团·大众点评北京总部 (恒电大厦B座 3号工位)@#北京市北京市朝阳区屏翠东路屏翠东路美团·大众点评北京总部
     */
    @JSONField(name = "recipient_address_detail")
    private String recipientAddressDetail;

    /**
     * 订单收货人联系电话，此字段信息可能推送真实手机号码或隐私号，即需兼容138*****678和13812345678_123456两种号码格式
     */
    @JSONField(name = "recipient_phone")
    private String recipientPhone;

    /**
     * 订单收货人姓名
     */
    @JSONField(name = "recipient_name")
    private String recipientName;

    /**
     * 门店配送费，单位是元。当前订单产生时该门店的配送费（商家自配送运费或美团配送运费），此字段数据为运费优惠前的原价。
     */
    @JSONField(name = "shipping_fee")
    private BigDecimal shippingFee;

    /**
     * 订单的实际在线支付总价，单位是元。此字段数据为用户实际支付的订单总金额，含打包袋、配送费等。
     */
    private BigDecimal total;

    /**
     * 订单的总原价，单位是元。此字段数据为未扣减所有优惠前订单的总金额，含打包袋、配送费等。
     */
    @JSONField(name = "original_price")
    private BigDecimal originalPrice;

    /**
     * 订单状态
     * 参考值有：1-用户已提交订单；2-向商家推送订单；4-商家已确认；8-订单已完成；9-订单已取消。
     */
    private Integer status;

    /**
     * 订单创建时间，为10位秒级的时间戳，此字段为用户提交订单的时间。
     */
    private Long ctime;

    /**
     *订单更新时间，为10位秒级的时间戳，此字段信息为当前订单最新订单/配送单状态更新的时间。
     */
    private Long utime;

    /**
     * 预计送达时间。若用户选择立即送出（系统自动计算预计送达时间，请参考estimate_arrival_time字段），则本参数推送0。若用户自行选择了预计送达时间，则本参数推送用户所选时间的时间戳（秒级）。
     */
    private Long deliveryTime;

    /**
     * 支付类型：1-货到付款，2-在线支付。目前订单只支持在线支付，此字段推送信息为2。
     */
    @JSONField(name = "pay_type")
    private Integer payType;

    /**
     * 取货类型：0-普通(配送),1-用户到店自取。此字段的信息默认不推送，商家如有需求可在开发者中心->基础设置->订单订阅字段 页面开启订阅字段“28 取餐类型订阅字段”。
     */
    @JSONField(name = "pick_type")
    private Integer pickType;

    /**
     * 是否为预售单，true-是，false-否。如果为预售单，可以订阅estimate_arrival_time字段获取预计发货时间
     */
    @JSONField(name = "is_pre_sale_order")
    private Boolean isPreSaleOrder;

    /**
     *订单配送方式，该字段信息默认不推送，商家如有需求可在开发者中心->基础设置->订单订阅字段 页面开启订阅字段“22 配送方式”。
     * 商家可在开放平台的【附录】文档中对照查看logistics_code的描述，如0000-商家自配、1001-美团加盟、2002-快送、3001-混合送（即美团专送+快送）等。
     * 如商家想了解自己门店的配送方式以及如何区分等情况，请咨询美团品牌经理。
     */
    @JSONField(name = "logistics_code")
    private Integer logisticsCode;

    /**
     * 订单商品详情，其值为由list序列化得到的json字符串
     */
    private String detail;

    @JSONField(name = "sku_benefit_detail")
    private String skuBenefitDetail;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getWmOrderIdView() {
        return wmOrderIdView;
    }

    public void setWmOrderIdView(Long wmOrderIdView) {
        this.wmOrderIdView = wmOrderIdView;
    }

    public String getAppPoiCode() {
        return appPoiCode;
    }

    public void setAppPoiCode(String appPoiCode) {
        this.appPoiCode = appPoiCode;
    }

    public String getWmPoiName() {
        return wmPoiName;
    }

    public void setWmPoiName(String wmPoiName) {
        this.wmPoiName = wmPoiName;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public String getRecipientAddressDetail() {
        return recipientAddressDetail;
    }

    public void setRecipientAddressDetail(String recipientAddressDetail) {
        this.recipientAddressDetail = recipientAddressDetail;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCtime() {
        return ctime;
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }

    public Long getUtime() {
        return utime;
    }

    public void setUtime(Long utime) {
        this.utime = utime;
    }

    public Long getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Long deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Integer getPickType() {
        return pickType;
    }

    public void setPickType(Integer pickType) {
        this.pickType = pickType;
    }

    public Boolean getPreSaleOrder() {
        return isPreSaleOrder;
    }

    public void setPreSaleOrder(Boolean preSaleOrder) {
        isPreSaleOrder = preSaleOrder;
    }

    public Integer getLogisticsCode() {
        return logisticsCode;
    }

    public void setLogisticsCode(Integer logisticsCode) {
        this.logisticsCode = logisticsCode;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getSkuBenefitDetail() {
        return skuBenefitDetail;
    }

    public void setSkuBenefitDetail(String skuBenefitDetail) {
        this.skuBenefitDetail = skuBenefitDetail;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
