package me.zhengjie.support.queenshop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.douyin.CBOrderListChild;
import me.zhengjie.utils.StringUtil;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.List;

public class QSOrderListMain {

    @JSONField(name = "cust_id")
    private Long custId; // 本系统货主ID

    @JSONField(name = "l_shop_id")
    private Long lShopId;// 本系统店铺ID

    /**
     * 订单编号
     */
    @JSONField(name = "orderNo")
    private String orderNo;

    /**
     * 清关订单号
     */
    @JSONField(name = "declareNo")
    private String declareNo;


    @JSONField(name = "createTime")
    private String createTime;

    /**
     * 支付时间
     */
    @JSONField(name = "pay_time")
    private String payTime;

    /**
     * 支付方式：
     1-支付宝，2-微信，3-银联
     */
    @JSONField(name = "payType")
    private Integer payType;

    /**
     * 支付单号
     */
    @JSONField(name = "payNo")
    private String payNo;

    /**
     * 电商平台代码
     */
    @JSONField(name = "ebpCode")
    private String ebpCode;

    /**
     * 电商平台名
     */
    @JSONField(name = "ebpName")
    private String ebpName;

    /**
     * 支付人姓名
     */
    @JSONField(name = "payerName")
    private String payerName;

    /**
     * 支付人身份证号码
     */
    @JSONField(name = "payerNumber")
    private String payerNumber;

    /**
     * 消费者网名
     */
    @JSONField(name = "customerId")
    private String customerId;

    /**
     * 收件人
     */
    @JSONField(name = "receiver")
    private String receiver;

    /**
     * 手机号
     */
    @JSONField(name = "tel")
    private String tel;

    /**
     *省份
     */
    @JSONField(name = "province")
    private String province;

    /**
     *城市
     */
    @JSONField(name = "city")
    private String city;

    /**
     *区县
     */
    @JSONField(name = "distinct")
    private String distinct;

    /**
     *详细地址（不含省市区）
     */
    @JSONField(name = "payerName")
    private String address;

    /**
     *消费者实付：实付=货值+运费-优惠
     */
    @JSONField(name = "payAmount")
    private Double payAmount;

    /**
     *总优惠金额（含代金券 优惠券 积分等）
     */
    @JSONField(name = "discount")
    private Double discount;

    /**
     *货值：每个商品的price*num之和
     */
    @JSONField(name = "goodsAmount")
    private Double goodsAmount;

    /**
     *税费，没有为0
     */
    @JSONField(name = "taxAmount")
    private Double taxAmount;

    /**
     *运费，没有为0
     */
    @JSONField(name = "postAmount")
    private Double postAmount;

    /**
     *订单状态
     */
    @JSONField(name = "orderStatus")
    private String orderStatus;

    /**
     *退款状态
     */
    @JSONField(name = "refundStatus")
    private String refundStatus;

    /**
     *
     */
    @JSONField(name = "wmsId")
    private String wmsId;

    /**
     *
     */
    @JSONField(name = "wmsName")
    private String wmsName;

    /**
     *
     */
    @JSONField(name = "logisticsName")
    private String logisticsName;

    /**
     *
     */
    @JSONField(name = "logisticsNo")
    private String logisticsNo;

    /**
     *买家备注
     */
    @JSONField(name = "buyerRemark")
    private String buyerRemark;

    /**
     *卖家备注
     */
    @JSONField(name = "sellerRemark")
    private String sellerRemark;

    /**
     *结算价格，和商家的结算总价
     */
    @JSONField(name = "settleAmount")
    private Double settleAmount;

    @JSONField(name = "goodsList")
    private List<QSOrderListChild> itemList;

    //sku列表
    @JSONField(name = "stockDetails")
    @Transient
    private List<QSStockUpdateRequest> stockDetails;

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

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getDeclareNo() {
        return declareNo;
    }

    public void setDeclareNo(String declareNo) {
        this.declareNo = declareNo;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public String getPayNo() {
        return payNo;
    }

    public void setPayNo(String payNo) {
        this.payNo = payNo;
    }

    public String getEbpCode() {
        return ebpCode;
    }

    public void setEbpCode(String ebpCode) {
        this.ebpCode = ebpCode;
    }

    public String getEbpName() {
        return ebpName;
    }

    public void setEbpName(String ebpName) {
        this.ebpName = ebpName;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getPayerNumber() {
        return payerNumber;
    }

    public void setPayerNumber(String payerNumber) {
        this.payerNumber = payerNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistinct() {
        return distinct;
    }

    public void setDistinct(String distinct) {
        this.distinct = distinct;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(Double payAmount) {
        this.payAmount = payAmount;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getGoodsAmount() {
        return goodsAmount;
    }

    public void setGoodsAmount(Double goodsAmount) {
        this.goodsAmount = goodsAmount;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Double getPostAmount() {
        return postAmount;
    }

    public void setPostAmount(Double postAmount) {
        this.postAmount = postAmount;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public String getWmsId() {
        return wmsId;
    }

    public void setWmsId(String wmsId) {
        this.wmsId = wmsId;
    }

    public String getWmsName() {
        return wmsName;
    }

    public void setWmsName(String wmsName) {
        this.wmsName = wmsName;
    }

    public String getLogisticsName() {
        return logisticsName;
    }

    public void setLogisticsName(String logisticsName) {
        this.logisticsName = logisticsName;
    }

    public String getLogisticsNo() {
        return logisticsNo;
    }

    public void setLogisticsNo(String logisticsNo) {
        this.logisticsNo = logisticsNo;
    }

    public String getBuyerRemark() {
        return buyerRemark;
    }

    public void setBuyerRemark(String buyerRemark) {
        this.buyerRemark = buyerRemark;
    }

    public String getSellerRemark() {
        return sellerRemark;
    }

    public void setSellerRemark(String sellerRemark) {
        this.sellerRemark = sellerRemark;
    }

    public Double getSettleAmount() {
        return settleAmount;
    }

    public void setSettleAmount(Double settleAmount) {
        this.settleAmount = settleAmount;
    }

    public List<QSOrderListChild> getItemList() {
        return itemList;
    }

    public void setItemList(List<QSOrderListChild> itemList) {
        this.itemList = itemList;
    }
}
