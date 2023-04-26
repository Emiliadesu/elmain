package me.zhengjie.support.beidian;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.List;

public class OrderData{
    /**
     * 贝贝订单号
     */
    @JSONField(name = "oid")
    private String oid;
    /**
     * 用户标识（仅在全球购出现）
     */
    @JSONField(name = "user")
    private String user;
    /**
     * 用户昵称
     */
    @JSONField(name = "nick")
    private String nick;
    /**
     * 省
     */
    @JSONField(name = "province")
    private String province;
    /**
     * 市
     */
    @JSONField(name = "city")
    private String city;
    /**
     * 区县
     */
    @JSONField(name = "county")
    private String county;
    /**
     * 地址
     */
    @JSONField(name = "address")
    private String address;
    /**
     * 该订单购买的商品总数量
     */
    @JSONField(name = "item_num")
    private String itemNum;
    /**
     * 订单状态
     */
    @JSONField(name = "status")
    private Integer status;
    /**
     * 订单总价(包含贝贝承担的现金券和积分等费用，为商家实际所得金额 含运费)
     */
    @JSONField(name = "total_fee")
    private BigDecimal totalFee;
    /**
     * 运费
     */
    @JSONField(name = "shipping_fee")
    private BigDecimal shippingFee;
    /**
     *最终价格(用户实际付款金额，不含现金券、积分、折扣等优惠，包含运费)
     */
    @JSONField(name = "payment")
    private BigDecimal payment;
    /**
     * 折扣
     */
    @JSONField(name = "discount")
    private BigDecimal discount;
    /**
     * 全球购：收件人身份证
     */
    @JSONField(name = "member_card")
    private String memberCard;
    /**
     * 收件人姓名
     */
    @JSONField(name = "receiver_name")
    private String receiverName;
    /**
     * 收件人手机号
     */
    @JSONField(name = "receiver_phone")
    private String receiverPhone;
    /**
     * 收件人地址
     */
    @JSONField(name = "receiver_address")
    private String receiverAddress;
    /**
     * 全球购：支付渠道
     */
    @JSONField(name = "channel_name")
    private String channelName;
    /**
     * 全球购：交易支付流水号
     */
    @JSONField(name = "channel_info")
    private String channelInfo;
    /**
     * 下单时间
     */
    @JSONField(name = "create_time")
    private String createTime;
    /**
     * 付款时间
     */
    @JSONField(name = "pay_time")
    private String payTime;
    /**
     * 全球购：税费
     */
    @JSONField(name = "tax_fee")
    private BigDecimal taxFee;
    /**
     * 全球购：订购人姓名
     */
    @JSONField(name = "user_name")
    private String userName;
    /**
     * 全球购：订购人身份证
     */
    @JSONField(name = "user_member_card")
    private String useMemberCard;
    /**
     * 订单商品详情
     */
    @JSONField(name = "item")
    private List<OrderItem> item;

    private String modifiedTime;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
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

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getItemNum() {
        return itemNum;
    }

    public void setItemNum(String itemNum) {
        this.itemNum = itemNum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public BigDecimal getPayment() {
        return payment;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getMemberCard() {
        return memberCard;
    }

    public void setMemberCard(String memberCard) {
        this.memberCard = memberCard;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelInfo() {
        return channelInfo;
    }

    public void setChannelInfo(String channelInfo) {
        this.channelInfo = channelInfo;
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

    public BigDecimal getTaxFee() {
        return taxFee;
    }

    public void setTaxFee(BigDecimal taxFee) {
        this.taxFee = taxFee;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUseMemberCard() {
        return useMemberCard;
    }

    public void setUseMemberCard(String useMemberCard) {
        this.useMemberCard = useMemberCard;
    }

    public List<OrderItem> getItem() {
        return item;
    }

    public void setItem(List<OrderItem> item) {
        this.item = item;
    }

    public String getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(String modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
}
