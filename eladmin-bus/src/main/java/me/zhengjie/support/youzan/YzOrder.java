package me.zhengjie.support.youzan;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradesSoldGetResult;
import me.zhengjie.utils.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author wangm
 * @since 2020-02-04
 */
public class YzOrder implements Serializable {

    public YzOrder() {
    }

    public YzOrder(YouzanTradesSoldGetResult.YouzanTradesSoldGetResultFullorderinfo youZanOrder) {
        YouzanTradesSoldGetResult.YouzanTradesSoldGetResultAddressinfo addressInfo = youZanOrder.getAddressInfo();
        YouzanTradesSoldGetResult.YouzanTradesSoldGetResultOrderinfo orderinfo = youZanOrder.getOrderInfo();
        YouzanTradesSoldGetResult.YouzanTradesSoldGetResultBuyerinfo buyerinfo = youZanOrder.getBuyerInfo();
        YouzanTradesSoldGetResult.YouzanTradesSoldGetResultSourceinfo sourceinfo = youZanOrder.getSourceInfo();
        YouzanTradesSoldGetResult.YouzanTradesSoldGetResultPayinfo payinfo = youZanOrder.getPayInfo();
        if (addressInfo != null) {
            this.deliveryProvince = addressInfo.getDeliveryProvince();
            this.deliveryDistrict = addressInfo.getDeliveryDistrict();
            this.deliveryCity = addressInfo.getDeliveryCity();
            this.receiverTel = addressInfo.getReceiverTel();
            this.deliveryAddress = addressInfo.getDeliveryAddress();
            this.receiverName = addressInfo.getReceiverName();
        }
        if (orderinfo != null) {
            YouzanTradesSoldGetResult.YouzanTradesSoldGetResultOrdertags tags = orderinfo.getOrderTags();
            if (tags != null) {
                this.isDownPaymentPre = tags.getIsDownPaymentPre();
                this.isPreorder = tags.getIsPreorder();
                this.isVirtual = tags.getIsVirtual();
            }
            this.payType = orderinfo.getPayType();
            this.payTime = orderinfo.getPayTime();
            this.tid = orderinfo.getTid();
            YouzanTradesSoldGetResult.YouzanTradesSoldGetResultOrderextra extra = orderinfo.getOrderExtra();
            if (extra != null) {
                this.buyerName = extra.getBuyerName();
                this.fxKdtId = extra.getFxKdtId();
                this.idCardNumber = extra.getIdCardNumber();
                this.fxOrderNo = extra.getFxOrderNo();
                this.deptId = extra.getDeptId();
                this.idCardName = extra.getIdCardName();
                this.fxInnerTransactionNo = extra.getFxInnerTransactionNo();
            }
            this.created = orderinfo.getCreated();
            this.updateTime = orderinfo.getUpdateTime();
            this.status = orderinfo.getStatus();
            this.type = orderinfo.getType();
        }

        List orders = youZanOrder.getOrders();
        if (CollectionUtils.isNotEmpty(orders)) {
            // 将子订单转化
            this.details = new ArrayList<>();
            for (int i = 0; i < orders.size(); i++) {
                Object obj = orders.get(i);
                if (obj instanceof JSONObject) {
                    JSONObject orderJsonObj = (JSONObject) obj;
                    YouzanTradesSoldGetResult.YouzanTradesSoldGetResultOrders order = orderJsonObj.toBean(YouzanTradesSoldGetResult.YouzanTradesSoldGetResultOrders.class);
                    this.isCrossBorder = order.getIsCrossBorder();
                    this.subOrderNo = order.getSubOrderNo();
                    this.customsCode = order.getCustomsCode();
                    this.details.add(new YzOrderDetails(order));
                }else if (obj instanceof YouzanTradesSoldGetResult.YouzanTradesSoldGetResultOrders){
                    YouzanTradesSoldGetResult.YouzanTradesSoldGetResultOrders order = (YouzanTradesSoldGetResult.YouzanTradesSoldGetResultOrders) obj;
                    this.isCrossBorder = order.getIsCrossBorder();
                    this.subOrderNo = order.getSubOrderNo();
                    this.customsCode = order.getCustomsCode();
                    this.details.add(new YzOrderDetails(order));
                }
            }
        }
        if (buyerinfo != null) {
            this.buyerPhone = buyerinfo.getBuyerPhone();
            this.buyerId = buyerinfo.getBuyerId();
        }
        if (sourceinfo != null) {
            YouzanTradesSoldGetResult.YouzanTradesSoldGetResultSource source = sourceinfo.getSource();
            if (source != null) {
                this.platform = source.getPlatform();
            }
        }
        if (payinfo != null) {
            this.payment = payinfo.getPayment();
            this.postFee = payinfo.getPostFee();
            List<String> transaction = payinfo.getTransaction();
            if (CollectionUtils.isNotEmpty(transaction)) {
                this.transaction = new JSONArray(transaction).toString();
            }
            this.totalFee = payinfo.getTotalFee();
        }
    }

    public YzOrder(YouzanTradeGetResult.YouzanTradeGetResultData data) {
        YouzanTradeGetResult.YouzanTradeGetResultFullorderinfo fullorderinfo=data.getFullOrderInfo();
        YouzanTradeGetResult.YouzanTradeGetResultAddressinfo addressInfo = fullorderinfo.getAddressInfo();
        YouzanTradeGetResult.YouzanTradeGetResultOrderinfo orderinfo = fullorderinfo.getOrderInfo();
        YouzanTradeGetResult.YouzanTradeGetResultBuyerinfo buyerinfo = fullorderinfo.getBuyerInfo();
        YouzanTradeGetResult.YouzanTradeGetResultSourceinfo sourceinfo = fullorderinfo.getSourceInfo();
        YouzanTradeGetResult.YouzanTradeGetResultPayinfo payinfo = fullorderinfo.getPayInfo();
        if (addressInfo != null) {
            this.deliveryProvince = addressInfo.getDeliveryProvince();
            this.deliveryDistrict = addressInfo.getDeliveryDistrict();
            this.deliveryCity = addressInfo.getDeliveryCity();
            this.receiverTel = addressInfo.getReceiverTel();
            this.deliveryAddress = addressInfo.getDeliveryAddress();
            this.receiverName = addressInfo.getReceiverName();
        }
        if (orderinfo != null) {
            YouzanTradeGetResult.YouzanTradeGetResultOrdertags tags = orderinfo.getOrderTags();
            if (tags != null) {
                this.isDownPaymentPre = tags.getIsDownPaymentPre();
                this.isPreorder = tags.getIsPreorder();
                this.isVirtual = tags.getIsVirtual();
            }
            this.payType = orderinfo.getPayType().longValue();
            this.payTime = orderinfo.getPayTime();
            this.tid = orderinfo.getTid();
            YouzanTradeGetResult.YouzanTradeGetResultOrderextra extra = orderinfo.getOrderExtra();
            if (extra != null) {
                this.buyerName = extra.getBuyerName();
                this.fxKdtId = extra.getFxKdtId();
                this.idCardNumber = extra.getIdCardNumber();
                this.fxOrderNo = extra.getFxOrderNo();
                this.deptId = extra.getDeptId();
                this.idCardName = extra.getIdCardName();
                this.fxInnerTransactionNo = extra.getFxInnerTransactionNo();
            }
            this.created = orderinfo.getCreated();
            this.updateTime = orderinfo.getUpdateTime();
            this.status = orderinfo.getStatus();
            this.type = orderinfo.getType().longValue();
        }
        List orders = fullorderinfo.getOrders();
        if (CollectionUtils.isNotEmpty(orders)) {
            // 将子订单转化
            this.details = new ArrayList<>();
            for (int i = 0; i < orders.size(); i++) {
                Object obj = orders.get(i);
                if (obj instanceof JSONObject) {
                    JSONObject orderJsonObj = (JSONObject) obj;
                    YouzanTradesSoldGetResult.YouzanTradesSoldGetResultOrders order = orderJsonObj.toBean(YouzanTradesSoldGetResult.YouzanTradesSoldGetResultOrders.class);
                    this.isCrossBorder = order.getIsCrossBorder();
                    this.subOrderNo = order.getSubOrderNo();
                    this.customsCode = order.getCustomsCode();
                    this.details.add(new YzOrderDetails(order));
                }else if (obj instanceof YouzanTradeGetResult.YouzanTradeGetResultOrders){
                    YouzanTradeGetResult.YouzanTradeGetResultOrders order = (YouzanTradeGetResult.YouzanTradeGetResultOrders) obj;
                    this.isCrossBorder = order.getIsCrossBorder();
                    this.subOrderNo = order.getSubOrderNo();
                    this.customsCode = order.getCustomsCode();
                    this.details.add(new YzOrderDetails(order));
                }
            }
        }
        if (buyerinfo != null) {
            this.buyerPhone = buyerinfo.getBuyerPhone();
            this.buyerId = buyerinfo.getBuyerId();
        }
        if (sourceinfo != null) {
            YouzanTradeGetResult.YouzanTradeGetResultSource source = sourceinfo.getSource();
            if (source != null) {
                this.platform = source.getPlatform();
            }
        }
        if (payinfo != null) {
            this.payment = payinfo.getPayment();
            this.postFee = payinfo.getPostFee();
            List<String> transaction = payinfo.getTransaction();
            if (CollectionUtils.isNotEmpty(transaction)) {
                this.transaction = new JSONArray(transaction).toString();
            }
            this.totalFee = payinfo.getTotalFee();
        }
    }

    /**
     * ERP系统店铺id
     */
    private Long lShopId;
    /**
     * ERP系统商家的客户id
     */
    private Long custId;

    private String id;
    /**
     * 主订单号
     */
    private String tid;
    /**
     * 海淘订单号
     */
    private String subOrderNo;
    /**
     * 海关编号
     */
    private String customsCode;
    /**
     * 是否是跨境海淘订单("1":是,"0":否)
     */
    private String isCrossBorder;
    /**
     * 省
     */
    private String deliveryProvince;
    /**
     * 市
     */
    private String deliveryCity;
    /**
     * 区
     */
    private String deliveryDistrict;
    /**
     * 详细地址
     */
    private String deliveryAddress;
    /**
     * 收货人姓名
     */
    private String receiverName;
    /**
     * 收货人手机号
     */
    private String receiverTel;
    /**
     * 是否定金预售
     */
    private Boolean isDownPaymentPre;
    /**
     * 是否预订单
     */
    private Boolean isPreorder;
    /**
     * 是否虚拟订单
     */
    private Boolean isVirtual;
    /**
     * 支付类型 0:默认值,未支付; 1:微信自有支付; 2:支付宝wap; 3:支付宝wap; 5:财付通; 7:代付; 8:联动优势; 9:货到付款; 10:大账号代销; 11:受理模式;12:百付宝; 13:sdk支付; 14:合并付货款; 15:赠品; 16:优惠兑换; 17:自动付货款; 18:爱学贷; 19:微信wap; 20:微信红包支付; 21:返利;22:ump红包; 24:易宝支付; 25:储值卡; 27:qq支付; 28:有赞E卡支付; 29:微信条码; 30:支付宝条码; 33:礼品卡支付; 35:会员余额;72:微信扫码二维码支付; 100:代收账户; 300:储值账户; 400:保证金账户; 101:收款码; 102:微信; 103:支付宝; 104:刷卡; 105:二维码台卡;106:储值卡; 107:有赞E卡; 110:标记收款-自有微信支付; 111:标记收款-自有支付宝; 112:标记收款-自有POS刷卡; 113:通联刷卡支付; 200:记账账户;201:现金
     */
    private Long payType;
    /**
     * 订单支付时间
     */
    private Date payTime;
    /**
     * 下单人昵称
     */
    private String buyerName;
    /**
     * 分销店铺id
     */
    private String fxKdtId;
    /**
     * 分销单订单号
     */
    private String fxOrderNo;
    /**
     * 美业分店id
     */
    private String deptId;
    /**
     * 海淘身份证信息
     */
    private String idCardNumber;
    /**
     * 身份证姓名信息 （订购人的身份证号字段可通过订单详情4.0接口“id_card_number ”获取）
     */
    private String idCardName;
    /**
     * 分销单内部支付流水号
     */
    private String fxInnerTransactionNo;
    /**
     * 主订单状态 WAIT_BUYER_PAY （等待买家付款，定金预售描述：定金待付、等待尾款支付开始、尾款待付）； TRADE_PAID（订单已支付 ）；WAIT_CONFIRM（待确认，包含待成团、待接单等等。即：买家已付款，等待成团或等待接单）； WAIT_SELLER_SEND_GOODS（等待卖家发货，即：买家已付款）；WAIT_BUYER_CONFIRM_GOODS (等待买家确认收货，即：卖家已发货) ； TRADE_SUCCESS（买家已签收以及订单成功）； TRADE_CLOSED（交易关闭）；PS：TRADE_PAID状态仅代表当前订单已支付成功，表示瞬时状态，稍后会自动修改成后面的状态。如果不关心此状态请再次请求详情接口获取下一个状态。
     */
    private String status;
    /**
     * 主订单类型 0:普通订单; 1:送礼订单; 2:代付; 3:分销采购单; 4:赠品; 5:心愿单; 6:二维码订单; 7:合并付货款; 8:1分钱实名认证; 9:品鉴; 10:拼团;15:返利; 35:酒店; 40:外卖; 41:堂食点餐; 46:外卖买单; 51:全员开店; 61:线下收银台订单; 71:美业预约单; 72:美业服务单; 75:知识付费;81:礼品卡; 100:批发
     */
    private Long type;
    /**
     * 买家手机号
     */
    private String buyerPhone;
    /**
     * 买家id
     */
    private Long buyerId;
    /**
     * 平台 wx:微信; merchant_3rd:商家自有app; buyer_v:买家版; browser:系统浏览器; alipay:支付宝;qq:腾讯QQ; wb:微博;other:其他
     */
    private String platform;
    /**
     * 最终支付价格 payment=orders.payment的总和
     */
    private String payment;
    /**
     * 邮费
     */
    private String postFee;
    /**
     * 有赞支付流水号
     */
    private String transaction;
    /**
     * 优惠前商品总价
     */
    private String totalFee;
    /**
     * 店铺id
     */
    private Long kdtId;
    /**
     * 店铺id
     */
    private String shopId;
    /**
     * 买家身份证号码
     */
    private String buyerIdCardNo;
    /**
     * 订单创建时间
     */
    private Date created;
    /**
     * 订单最后修改时间
     */
    private Date updateTime;
    /**
     * 订单拉取时间
     */
    private Date pullTime;

    /**
     * 快递公司名称
     */
    private List<YzOrderDetails> details;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getSubOrderNo() {
        return subOrderNo;
    }

    public void setSubOrderNo(String subOrderNo) {
        this.subOrderNo = subOrderNo;
    }

    public String getCustomsCode() {
        return customsCode;
    }

    public void setCustomsCode(String customsCode) {
        this.customsCode = customsCode;
    }

    public String getIsCrossBorder() {
        return isCrossBorder;
    }

    public void setIsCrossBorder(String isCrossBorder) {
        this.isCrossBorder = isCrossBorder;
    }

    public String getDeliveryProvince() {
        return deliveryProvince;
    }

    public void setDeliveryProvince(String deliveryProvince) {
        this.deliveryProvince = deliveryProvince;
    }

    public String getDeliveryCity() {
        return deliveryCity;
    }

    public void setDeliveryCity(String deliveryCity) {
        this.deliveryCity = deliveryCity;
    }

    public String getDeliveryDistrict() {
        return deliveryDistrict;
    }

    public void setDeliveryDistrict(String deliveryDistrict) {
        this.deliveryDistrict = deliveryDistrict;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverTel() {
        return receiverTel;
    }

    public void setReceiverTel(String receiverTel) {
        this.receiverTel = receiverTel;
    }


    public Long getPayType() {
        return payType;
    }

    public void setPayType(Long payType) {
        this.payType = payType;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getFxKdtId() {
        return fxKdtId;
    }

    public void setFxKdtId(String fxKdtId) {
        this.fxKdtId = fxKdtId;
    }

    public String getFxOrderNo() {
        return fxOrderNo;
    }

    public void setFxOrderNo(String fxOrderNo) {
        this.fxOrderNo = fxOrderNo;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getIdCardNumber() {
        return idCardNumber;
    }

    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    public String getIdCardName() {
        return idCardName;
    }

    public void setIdCardName(String idCardName) {
        this.idCardName = idCardName;
    }

    public String getFxInnerTransactionNo() {
        return fxInnerTransactionNo;
    }

    public void setFxInnerTransactionNo(String fxInnerTransactionNo) {
        this.fxInnerTransactionNo = fxInnerTransactionNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getPostFee() {
        return postFee;
    }

    public void setPostFee(String postFee) {
        this.postFee = postFee;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public Long getKdtId() {
        return kdtId;
    }

    public void setKdtId(Long kdtId) {
        this.kdtId = kdtId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getBuyerIdCardNo() {
        return buyerIdCardNo;
    }

    public void setBuyerIdCardNo(String buyerIdCardNo) {
        this.buyerIdCardNo = buyerIdCardNo;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getPullTime() {
        return pullTime;
    }

    public void setPullTime(Date pullTime) {
        this.pullTime = pullTime;
    }

    public Boolean getDownPaymentPre() {
        return isDownPaymentPre;
    }

    public void setDownPaymentPre(Boolean downPaymentPre) {
        isDownPaymentPre = downPaymentPre;
    }

    public Boolean getPreorder() {
        return isPreorder;
    }

    public void setPreorder(Boolean preorder) {
        isPreorder = preorder;
    }

    public Boolean getVirtual() {
        return isVirtual;
    }

    public void setVirtual(Boolean virtual) {
        isVirtual = virtual;
    }

    public List<YzOrderDetails> getDetails() {
        return details;
    }

    public void setDetails(List<YzOrderDetails> details) {
        this.details = details;
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

    @Override
    public String toString() {
        return "YzOrder{" +
        "id=" + id +
        ", tid=" + tid +
        ", subOrderNo=" + subOrderNo +
        ", customsCode=" + customsCode +
        ", isCrossBorder=" + isCrossBorder +
        ", deliveryProvince=" + deliveryProvince +
        ", deliveryCity=" + deliveryCity +
        ", deliveryDistrict=" + deliveryDistrict +
        ", deliveryAddress=" + deliveryAddress +
        ", receiverName=" + receiverName +
        ", receiverTel=" + receiverTel +
        ", isDownPaymentPre=" + isDownPaymentPre +
        ", isPreorder=" + isPreorder +
        ", isVirtual=" + isVirtual +
        ", payType=" + payType +
        ", payTime=" + payTime +
        ", buyerName=" + buyerName +
        ", fxKdtId=" + fxKdtId +
        ", fxOrderNo=" + fxOrderNo +
        ", deptId=" + deptId +
        ", idCardNumber=" + idCardNumber +
        ", idCardName=" + idCardName +
        ", fxInnerTransactionNo=" + fxInnerTransactionNo +
        ", status=" + status +
        ", type=" + type +
        ", buyerPhone=" + buyerPhone +
        ", buyerId=" + buyerId +
        ", platform=" + platform +
        ", payment=" + payment +
        ", postFee=" + postFee +
        ", transaction=" + transaction +
        ", totalFee=" + totalFee +
        ", kdtId=" + kdtId +
        ", shopId=" + shopId +
        ", buyerIdCardNo=" + buyerIdCardNo +
        ", created=" + created +
        ", pullTime=" + pullTime +
        "}";
    }

    public void updateOrderInfo(YzOrder newOrder) {
        this.buyerPhone=newOrder.buyerPhone;
        this.buyerId=newOrder.buyerId;
        this.buyerIdCardNo=newOrder.getBuyerIdCardNo();
        this.buyerName=newOrder.getBuyerName();
        /*this.deliveryProvince=newOrder.getDeliveryProvince();
        this.deliveryCity=newOrder.deliveryCity;
        this.deliveryDistrict=newOrder.deliveryDistrict;
        this.deliveryAddress=newOrder.deliveryAddress;
        this.receiverName=newOrder.getReceiverName();
        this.receiverTel=newOrder.receiverTel;*/
    }
}
