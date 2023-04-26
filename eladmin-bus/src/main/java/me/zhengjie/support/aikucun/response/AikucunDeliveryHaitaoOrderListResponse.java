package me.zhengjie.support.aikucun.response;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.util.List;

public class AikucunDeliveryHaitaoOrderListResponse {
    @JSONField(name = "cust_id")
    private Long custId; // 本系统货主ID

    @JSONField(name = "l_shop_id")
    private Long lShopId;// 本系统店铺ID
    /**
     *
     */
    @JSONField(name = "totalRecord")
    private Integer totalRecord;
    /**
     *
     */
    @JSONField(name = "totalPage")
    private Integer totalPage;
    /**
     *
     */
    @JSONField(name = "page")
    private Integer page;
    /**
     *
     */
    @JSONField(name = "pagesize")
    private Integer pagesize;
    /**
     *
     */
    @JSONField(name = "orderList")
    private List<Order>orderList;

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

    public Integer getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(Integer totalRecord) {
        this.totalRecord = totalRecord;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPagesize() {
        return pagesize;
    }

    public void setPagesize(Integer pagesize) {
        this.pagesize = pagesize;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    public static class Order{
        @JSONField(name = "cust_id")
        private Long custId; // 本系统货主ID

        @JSONField(name = "l_shop_id")
        private Long lShopId;// 本系统店铺ID
        /**
         * 平台发货单号，以AD开头，平台财务对账和客服问题跟踪使用，建议必存
         */
        @JSONField(name = "adorderId")
        private String adorderId;

        /**
         * RC单号 海淘报关使用
         */
        @JSONField(name = "rcOrderId")
        private String rcOrderId;

        /**
         * 用户订单号（二级单）
         */
        @JSONField(name = "consumerOrderId")
        private String consumerOrderId;

        /**
         * 订购人唯一编号
         */
        @JSONField(name = "buyerOnlyCode")
        private String buyerOnlyCode;

        /**
         * 支付币制
         */
        @JSONField(name = "payCurrency")
        private String payCurrency;

        /**
         * 商家编码
         */
        @JSONField(name = "merchantCode")
        private String merchantCode;

        /**
         * 商家名称
         */
        @JSONField(name = "merchantName")
        private String merchantName;

        /**
         * 收货人姓名
         */
        @JSONField(name = "deliveryName")
        private String deliveryName;

        /**
         * 收货人联系电话
         */
        @JSONField(name = "deliveryMobile")
        private String deliveryMobile;

        /**
         * 收货人身份证号码
         */
        @JSONField(name = "deliveryIdCard")
        private String deliveryIdCard;

        /**
         * 订购人姓名
         */
        @JSONField(name = "ordererName")
        private String ordererName;

        /**
         * 订购人身份证号码
         */
        @JSONField(name = "ordererIdCard")
        private String ordererIdCard;

        /**
         * 收货人 省
         */
        @JSONField(name = "province")
        private String province;

        /**
         * 收货人 市
         */
        @JSONField(name = "city")
        private String city;

        /**
         * 收货人 区
         */
        @JSONField(name = "area")
        private String area;

        /**
         * 收货人 详细地址 不包括省市区
         */
        @JSONField(name = "address")
        private String address;

        /**
         * 邮政编码
         */
        @JSONField(name = "postCode")
        private String postCode;

        /**
         * 发货地海关编码
         */
        @JSONField(name = "customsCode")
        private String customsCode;

        /**
         * 截(接?)单时间
         */
        @JSONField(name = "createTime")
        private Date createTime;

        /**
         * AD单更新时间
         */
        @JSONField(name = "updateTime")
        private Date updateTime;

        /**
         * 买家下单时间
         */
        @JSONField(name = "consumerCreateTime")
        private Date consumerCreateTime;

        /**
         * 海掏方式 (BC、BBC、CC)
         */
        @JSONField(name = "businessType")
        private String businessType;

        /**
         * 订单完税价 订单总金额，当前币种最小单位（分）注意：订单完税价＝订单商品｜优惠前｜价格总和＋运费＋保费，不含税
         */
        @JSONField(name = "dutiablePrice")
        private Long dutiablePrice;

        /**
         * 订单综合税 订单税费,当前币种最小单位（分）
         */
        @JSONField(name = "totalTax")
        private Long totalTax;

        /**
         * 订单保险费,当前币种最小单位（分）
         */
        @JSONField(name = "insurance")
        private Long insurance;

        /**
         * 订单优惠金额，正数，当前币种最小单位（分）
         */
        @JSONField(name = "coupon")
        private Long coupon;

        /**
         * 订单买家实付金额 订单实际支付金额,当前币种最小单位（分）
         */
        @JSONField(name = "actualpayment")
        private Long actualpayment;

        /**
         * 运费，不含税，默认填“0”,当前币种最小单位（分）
         */
        @JSONField(name = "shippingFee")
        private Long shippingFee;

        /**
         * 运杂费税费, 默认填“0,当前币种最小单位（分）
         */
        @JSONField(name = "shippingFeeTax")
        private Long shippingFeeTax;

        /**
         * 货款金额 商品金额之和。当前币种最小单位（分）
         */
        @JSONField(name = "goodsFee")
        private Long goodsFee;

        /**
         * 订单状态 60-等待发货 70-发货中 80-等待确认收货 90-已完成 100-已取消
         */
        @JSONField(name = "orderStatus")
        private Integer orderStatus;

        /**
         * 支付信息
         */
        @JSONField(name = "payInfo")
        private PayInfo payInfo;

        /**
         * 订单商品明细
         */
        @JSONField(name = "goodsList")
        private List<Goods>goodsList;

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

        public String getAdorderId() {
            return adorderId;
        }

        public void setAdorderId(String adorderId) {
            this.adorderId = adorderId;
        }

        public String getRcOrderId() {
            return rcOrderId;
        }

        public void setRcOrderId(String rcOrderId) {
            this.rcOrderId = rcOrderId;
        }

        public String getConsumerOrderId() {
            return consumerOrderId;
        }

        public void setConsumerOrderId(String consumerOrderId) {
            this.consumerOrderId = consumerOrderId;
        }

        public String getBuyerOnlyCode() {
            return buyerOnlyCode;
        }

        public void setBuyerOnlyCode(String buyerOnlyCode) {
            this.buyerOnlyCode = buyerOnlyCode;
        }

        public String getPayCurrency() {
            return payCurrency;
        }

        public void setPayCurrency(String payCurrency) {
            this.payCurrency = payCurrency;
        }

        public String getMerchantCode() {
            return merchantCode;
        }

        public void setMerchantCode(String merchantCode) {
            this.merchantCode = merchantCode;
        }

        public String getMerchantName() {
            return merchantName;
        }

        public void setMerchantName(String merchantName) {
            this.merchantName = merchantName;
        }

        public String getDeliveryName() {
            return deliveryName;
        }

        public void setDeliveryName(String deliveryName) {
            this.deliveryName = deliveryName;
        }

        public String getDeliveryMobile() {
            return deliveryMobile;
        }

        public void setDeliveryMobile(String deliveryMobile) {
            this.deliveryMobile = deliveryMobile;
        }

        public String getDeliveryIdCard() {
            return deliveryIdCard;
        }

        public void setDeliveryIdCard(String deliveryIdCard) {
            this.deliveryIdCard = deliveryIdCard;
        }

        public String getOrdererName() {
            return ordererName;
        }

        public void setOrdererName(String ordererName) {
            this.ordererName = ordererName;
        }

        public String getOrdererIdCard() {
            return ordererIdCard;
        }

        public void setOrdererIdCard(String ordererIdCard) {
            this.ordererIdCard = ordererIdCard;
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

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPostCode() {
            return postCode;
        }

        public void setPostCode(String postCode) {
            this.postCode = postCode;
        }

        public String getCustomsCode() {
            return customsCode;
        }

        public void setCustomsCode(String customsCode) {
            this.customsCode = customsCode;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        public Date getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Date updateTime) {
            this.updateTime = updateTime;
        }

        public Date getConsumerCreateTime() {
            return consumerCreateTime;
        }

        public void setConsumerCreateTime(Date consumerCreateTime) {
            this.consumerCreateTime = consumerCreateTime;
        }

        public String getBusinessType() {
            return businessType;
        }

        public void setBusinessType(String businessType) {
            this.businessType = businessType;
        }

        public Long getDutiablePrice() {
            return dutiablePrice;
        }

        public void setDutiablePrice(Long dutiablePrice) {
            this.dutiablePrice = dutiablePrice;
        }

        public Long getTotalTax() {
            return totalTax;
        }

        public void setTotalTax(Long totalTax) {
            this.totalTax = totalTax;
        }

        public Long getInsurance() {
            return insurance;
        }

        public void setInsurance(Long insurance) {
            this.insurance = insurance;
        }

        public Long getCoupon() {
            return coupon;
        }

        public void setCoupon(Long coupon) {
            this.coupon = coupon;
        }

        public Long getActualpayment() {
            return actualpayment;
        }

        public void setActualpayment(Long actualpayment) {
            this.actualpayment = actualpayment;
        }

        public Long getShippingFee() {
            return shippingFee;
        }

        public void setShippingFee(Long shippingFee) {
            this.shippingFee = shippingFee;
        }

        public Long getShippingFeeTax() {
            return shippingFeeTax;
        }

        public void setShippingFeeTax(Long shippingFeeTax) {
            this.shippingFeeTax = shippingFeeTax;
        }

        public Long getGoodsFee() {
            return goodsFee;
        }

        public void setGoodsFee(Long goodsFee) {
            this.goodsFee = goodsFee;
        }

        public Integer getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(Integer orderStatus) {
            this.orderStatus = orderStatus;
        }

        public PayInfo getPayInfo() {
            return payInfo;
        }

        public void setPayInfo(PayInfo payInfo) {
            this.payInfo = payInfo;
        }

        public List<Goods> getGoodsList() {
            return goodsList;
        }

        public void setGoodsList(List<Goods> goodsList) {
            this.goodsList = goodsList;
        }
    }
    public static class PayInfo{
        /**
         * 支付公司，当前只支持微信支付，枚举值只有“财付通支付科技有限公司”
         */
        @JSONField(name = "payCompany")
        private String payCompany;

        /**
         * 支付交易流水号
         */
        @JSONField(name = "payNo")
        private String payNo;

        /**
         * 支付完成时间 格式：yyyy-MM-dd HH:mm:ss
         */
        @JSONField(name = "payCompleteTime")
        private String payCompleteTime;

        public String getPayCompany() {
            return payCompany;
        }

        public void setPayCompany(String payCompany) {
            this.payCompany = payCompany;
        }

        public String getPayNo() {
            return payNo;
        }

        public void setPayNo(String payNo) {
            this.payNo = payNo;
        }

        public String getPayCompleteTime() {
            return payCompleteTime;
        }

        public void setPayCompleteTime(String payCompleteTime) {
            this.payCompleteTime = payCompleteTime;
        }
    }
    public static class Goods{
        /**
         * 爱库存系统商家商品唯一ID
         */
        @JSONField(name = "skuId")
        private String skuId;

        /**
         * 商家海关备案的商品唯一ID
         */
        @JSONField(name = "merchantSkuId")
        private String merchantSkuId;

        /**
         * 商品数量
         */
        @JSONField(name = "totalQty")
        private Integer totalQty;

        /**
         * 商品条码
         */
        @JSONField(name = "barCode")
        private String barCode;

        /**
         * 商品单价，当前币种最小单位（分）
         */
        @JSONField(name = "price")
        private Long price;

        /**
         * 商品金额，不含税，当前币种最小单位（分）
         */
        @JSONField(name = "amount")
        private Long amount;

        /**
         * 商品综合税，当前币种最小单位（分）
         */
        @JSONField(name = "goodsTotalTax")
        private Long goodsTotalTax;

        /**
         * hscode
         */
        @JSONField(name = "hscode")
        private String hscode;

        public String getSkuId() {
            return skuId;
        }

        public void setSkuId(String skuId) {
            this.skuId = skuId;
        }

        public String getMerchantSkuId() {
            return merchantSkuId;
        }

        public void setMerchantSkuId(String merchantSkuId) {
            this.merchantSkuId = merchantSkuId;
        }

        public Integer getTotalQty() {
            return totalQty;
        }

        public void setTotalQty(Integer totalQty) {
            this.totalQty = totalQty;
        }

        public String getBarCode() {
            return barCode;
        }

        public void setBarCode(String barCode) {
            this.barCode = barCode;
        }

        public Long getPrice() {
            return price;
        }

        public void setPrice(Long price) {
            this.price = price;
        }

        public Long getAmount() {
            return amount;
        }

        public void setAmount(Long amount) {
            this.amount = amount;
        }

        public Long getGoodsTotalTax() {
            return goodsTotalTax;
        }

        public void setGoodsTotalTax(Long goodsTotalTax) {
            this.goodsTotalTax = goodsTotalTax;
        }

        public String getHscode() {
            return hscode;
        }

        public void setHscode(String hscode) {
            this.hscode = hscode;
        }
    }
}
