package me.zhengjie.support.moGuJie;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.utils.StringUtil;

import java.util.List;

public class MogujieTradeSoldGetResp {

    /**
     * 响应
     */
    private Result result;
    /**
     * 是否成功
     */
    private Boolean success;
    /**
     * 查询结果状态表示
     */
    @JSONField(name = "status")
    private Status status;

    private Long lShopId;

    private Long custId;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getLShopId() {
        return lShopId;
    }

    public void setLShopId(Long lShopId) {
        this.lShopId = lShopId;
    }

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    public static class Result {
        private Data data;

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }
    }

    public static class Status {
        /**
         * 查询结果描述
         */
        private String msg;
        /**
         * 状态码
         */
        private String code;

        public String getMsg() {
            return msg;
        }

        public String getCode() {
            return code;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    public static class Data {
        /**
         *搜索到的订单总数量
         */
        private Integer totalNum;
        /**
         *是否有下一页
         */
        private Boolean hasNext;
        /**
         *搜索到的订单列表
         */
        private List<OrderData> openApiOrderDetailResDtos;

        public Integer getTotalNum() {
            return totalNum;
        }

        public void setTotalNum(Integer totalNum) {
            this.totalNum = totalNum;
        }

        public Boolean getHasNext() {
            return hasNext;
        }

        public void setHasNext(Boolean hasNext) {
            this.hasNext = hasNext;
        }

        public List<OrderData> getOpenApiOrderDetailResDtos() {
            return openApiOrderDetailResDtos;
        }

        public void setOpenApiOrderDetailResDtos(List<OrderData> openApiOrderDetailResDtos) {
            this.openApiOrderDetailResDtos = openApiOrderDetailResDtos;
        }
    }

    public static class OrderData {
        /**
         *买家账户id
         */
        private String buyerAccountId;
        /**
         *买家昵称
         */
        private String buyerName;
        /**
         *买家id
         */
        private String buyerUserId;
        /**
         *订单创建时间
         */
        private String createdStr;
        /**
         *商品列表，商品级订单
         */
        private List<OrderDataDetail> itemOrderInfos;
        /**
         *包含的商品级订单数量
         */
        private Integer number;
        /**
         *订单状态
         */
        private String orderStatus;
        /**
         *支付级订单id
         */
        private String payOrderId;
        /**
         *订单支付时间
         */
        private String payTimeStr;
        /**
         *平台优惠 分
         */
        private Double platformPromotionAmount;
        /**
         *支付级优惠 分
         */
        private Double promotionAmount;
        /**
         *收货地址
         */
        private String receiverAddress;
        /**
         *收货人 区
         */
        private String receiverArea;
        /**
         *收货人 市
         */
        private String receiverCity;
        /**
         *收货人 手机
         */
        private String receiverMobile;
        /**
         *收货人 姓名
         */
        private String receiverName;
        /**
         *收货人联系电话
         */
        private String receiverPhone;
        /**
         *收货人 省
         */
        private String receiverProvince;
        /**
         *收货人 邮编
         */
        private String receiverZip;
        /**
         * 运费
         */
        private Double shipExpense;
        /**
         * 运单号
         */
        private String shipExpressId;
        /**
         * 快递公司名
         */
        private String shipExpressName;
        /**
         *发货类型 0 - 快递
         *      1 - 小店卖家直接发货
         *      2 - 海淘-转运
         *      3 - 海淘-商业快件
         *      4 - 虚拟交易
         *      5 - 保税仓
         */
        private Short shipType;
        /**
         * 店铺级订单id
         */
        private String shopOrderId;
        /**
         *订单价格 分
         */
        private Double shopOrderPrice;
        /**
         *订单市场类型
         */
        private String shopOrderType;
        /**
         *店铺优惠 分
         */
        private Double shopPromotionAmount;
        /**
         *多阶段订单状态
         */
        private String stageStatus;
        /**
         *跨境专用属性
         */
        private ExtraMap extraMap;
        /**
         *店铺单改价差额
         */
        private Double modifyPrice;
        /**
         * 订单最近修改时间
         */
        private String updatedStr;

        private Long lShopId;

        private Long custId;

        public String getBuyerAccountId() {
            return buyerAccountId;
        }

        public String getUpdatedStr() {
            return updatedStr;
        }

        public void setUpdatedStr(String updatedStr) {
            this.updatedStr = updatedStr;
        }

        public void setBuyerAccountId(String buyerAccountId) {
            this.buyerAccountId = buyerAccountId;
        }

        public String getBuyerName() {
            return buyerName;
        }

        public void setBuyerName(String buyerName) {
            this.buyerName = buyerName;
        }

        public String getBuyerUserId() {
            return buyerUserId;
        }

        public void setBuyerUserId(String buyerUserId) {
            this.buyerUserId = buyerUserId;
        }

        public String getCreatedStr() {
            return createdStr;
        }

        public void setCreatedStr(String createdStr) {
            this.createdStr = createdStr;
        }

        public List<OrderDataDetail> getItemOrderInfos() {
            return itemOrderInfos;
        }

        public void setItemOrderInfos(List<OrderDataDetail> itemOrderInfos) {
            this.itemOrderInfos = itemOrderInfos;
        }

        public Integer getNumber() {
            return number;
        }

        public void setNumber(Integer number) {
            this.number = number;
        }

        public String getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(String orderStatus) {
            this.orderStatus = orderStatus;
        }

        public String getPayOrderId() {
            return payOrderId;
        }

        public void setPayOrderId(String payOrderId) {
            this.payOrderId = payOrderId;
        }

        public String getPayTimeStr() {
            return payTimeStr;
        }

        public void setPayTimeStr(String payTimeStr) {
            this.payTimeStr = payTimeStr;
        }

        public Double getPlatformPromotionAmount() {
            return platformPromotionAmount;
        }

        public void setPlatformPromotionAmount(Double platformPromotionAmount) {
            this.platformPromotionAmount = platformPromotionAmount;
        }

        public Double getPromotionAmount() {
            return promotionAmount;
        }

        public void setPromotionAmount(Double promotionAmount) {
            this.promotionAmount = promotionAmount;
        }

        public String getReceiverAddress() {
            return receiverAddress;
        }

        public void setReceiverAddress(String receiverAddress) {
            this.receiverAddress = receiverAddress;
        }

        public String getReceiverArea() {
            return receiverArea;
        }

        public void setReceiverArea(String receiverArea) {
            this.receiverArea = receiverArea;
        }

        public String getReceiverCity() {
            return receiverCity;
        }

        public void setReceiverCity(String receiverCity) {
            this.receiverCity = receiverCity;
        }

        public String getReceiverMobile() {
            return receiverMobile;
        }

        public void setReceiverMobile(String receiverMobile) {
            this.receiverMobile = receiverMobile;
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

        public String getReceiverProvince() {
            return receiverProvince;
        }

        public void setReceiverProvince(String receiverProvince) {
            this.receiverProvince = receiverProvince;
        }

        public String getReceiverZip() {
            return receiverZip;
        }

        public void setReceiverZip(String receiverZip) {
            this.receiverZip = receiverZip;
        }

        public Double getShipExpense() {
            return shipExpense;
        }

        public void setShipExpense(Double shipExpense) {
            this.shipExpense = shipExpense;
        }

        public String getShipExpressId() {
            return shipExpressId;
        }

        public void setShipExpressId(String shipExpressId) {
            this.shipExpressId = shipExpressId;
        }

        public String getShipExpressName() {
            return shipExpressName;
        }

        public void setShipExpressName(String shipExpressName) {
            this.shipExpressName = shipExpressName;
        }

        public Short getShipType() {
            return shipType;
        }

        public void setShipType(Short shipType) {
            this.shipType = shipType;
        }

        public String getShopOrderId() {
            return shopOrderId;
        }

        public void setShopOrderId(String shopOrderId) {
            this.shopOrderId = shopOrderId;
        }

        public Double getShopOrderPrice() {
            return shopOrderPrice;
        }

        public void setShopOrderPrice(Double shopOrderPrice) {
            this.shopOrderPrice = shopOrderPrice;
        }

        public String getShopOrderType() {
            return shopOrderType;
        }

        public void setShopOrderType(String shopOrderType) {
            this.shopOrderType = shopOrderType;
        }

        public Double getShopPromotionAmount() {
            return shopPromotionAmount;
        }

        public void setShopPromotionAmount(Double shopPromotionAmount) {
            this.shopPromotionAmount = shopPromotionAmount;
        }

        public String getStageStatus() {
            return stageStatus;
        }

        public void setStageStatus(String stageStatus) {
            this.stageStatus = stageStatus;
        }

        public ExtraMap getExtraMap() {
            return extraMap;
        }

        public void setExtraMap(ExtraMap extraMap) {
            this.extraMap = extraMap;
        }

        public Double getModifyPrice() {
            return modifyPrice;
        }

        public void setModifyPrice(Double modifyPrice) {
            this.modifyPrice = modifyPrice;
        }

        public Long getLShopId() {
            return lShopId;
        }

        public void setLShopId(Long lShopId) {
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
            return "OrderData{" +
                    "buyerAccountId='" + buyerAccountId + '\'' +
                    ", buyerName='" + buyerName + '\'' +
                    ", buyerUserId='" + buyerUserId + '\'' +
                    ", createdStr='" + createdStr + '\'' +
                    ", itemOrderInfos=" + itemOrderInfos +
                    ", number=" + number +
                    ", orderStatus='" + orderStatus + '\'' +
                    ", payOrderId='" + payOrderId + '\'' +
                    ", payTimeStr='" + payTimeStr + '\'' +
                    ", platformPromotionAmount=" + platformPromotionAmount +
                    ", promotionAmount=" + promotionAmount +
                    ", receiverAddress='" + receiverAddress + '\'' +
                    ", receiverArea='" + receiverArea + '\'' +
                    ", receiverCity='" + receiverCity + '\'' +
                    ", receiverMobile='" + receiverMobile + '\'' +
                    ", receiverName='" + receiverName + '\'' +
                    ", receiverPhone='" + receiverPhone + '\'' +
                    ", receiverProvince='" + receiverProvince + '\'' +
                    ", receiverZip='" + receiverZip + '\'' +
                    ", shipExpense=" + shipExpense +
                    ", shipExpressId='" + shipExpressId + '\'' +
                    ", shipExpressName='" + shipExpressName + '\'' +
                    ", shipType=" + shipType +
                    ", shopOrderId='" + shopOrderId + '\'' +
                    ", shopOrderPrice=" + shopOrderPrice +
                    ", shopOrderType='" + shopOrderType + '\'' +
                    ", shopPromotionAmount=" + shopPromotionAmount +
                    ", stageStatus='" + stageStatus + '\'' +
                    ", extraMap=" + extraMap +
                    ", modifyPrice=" + modifyPrice +
                    ", updatedStr='" + updatedStr + '\'' +
                    '}';
        }
    }

    public static class OrderDataDetail {
        /**
         *商品code
         */
        private String itemCode;
        /**
         *商品id
         */
        private String itemId;
        /**
         *商品级订单id 海淘订单号
         */
        private String itemOrderId;
        /**
         *商品现价
         */
        private Double nowPrice;
        /**
         *商品数量
         */
        private Long number;
        /**
         *订单子状态
         */
        private String orderStatus;
        /**
         *商品原价
         */
        private Double price;
        /**
         * 商品退款状态
         */
        private String refundStatus;
        /**
         *商品sku描述
         */
        private List<SkuAttribute> skuAttributes;
        /**
         *商品skuCode
         */
        private String skuCode;
        /**
         *商品skuId
         */
        private String skuId;
        /**
         *商品名称
         */
        private String title;
        /**
         *卖家实收
         */
        private Double sellerFinal;

        public String getItemCode() {
            return itemCode;
        }

        public void setItemCode(String itemCode) {
            this.itemCode = itemCode;
        }

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public String getItemOrderId() {
            return itemOrderId;
        }

        public void setItemOrderId(String itemOrderId) {
            this.itemOrderId = itemOrderId;
        }

        public Double getNowPrice() {
            return nowPrice;
        }

        public void setNowPrice(Double nowPrice) {
            this.nowPrice = nowPrice;
        }

        public Long getNumber() {
            return number;
        }

        public void setNumber(Long number) {
            this.number = number;
        }

        public String getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(String orderStatus) {
            this.orderStatus = orderStatus;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public List<SkuAttribute> getSkuAttributes() {
            return skuAttributes;
        }

        public void setSkuAttributes(List<SkuAttribute> skuAttributes) {
            this.skuAttributes = skuAttributes;
        }

        public String getSkuCode() {
            return skuCode;
        }

        public void setSkuCode(String skuCode) {
            this.skuCode = skuCode;
        }

        public String getSkuId() {
            return skuId;
        }

        public void setSkuId(String skuId) {
            this.skuId = skuId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Double getSellerFinal() {
            return sellerFinal;
        }

        public void setSellerFinal(Double sellerFinal) {
            this.sellerFinal = sellerFinal;
        }

        public String getRefundStatus() {
            return refundStatus;
        }

        public void setRefundStatus(String refundStatus) {
            this.refundStatus = refundStatus;
        }
    }

    private static class SkuAttribute {
        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class ExtraMap {
        /**
         *身份证编号
         */
        private String idCardCode;
        /**
         *身份证姓名
         */
        private String idCardName;
        /**
         *身份证手机号
         */
        private String idCardMobile;
        /**
         *支付企业代码
         */
        private String payChannelCode;
        /**
         *交易支付流水号
         */
        private String paymentNo;
        /**
         *关税
         */
        private Double tariff;
        /**
         *地方版支付报关流水号
         */
        private String declareNo;
        /**
         *总署版支付报关流水号
         */
        private String declareNoZS;
        /**
         *跨境购的订购人和支付人是否一致
         */
        private Character identityCheck;
        /**
         * 保税仓代码
         */
        private String storage;
        /**
         * 保税仓名
         */
        private String storageName;

        public String getIdCardCode() {
            return idCardCode;
        }

        public void setIdCardCode(String idCardCode) {
            this.idCardCode = idCardCode;
        }

        public String getIdCardName() {
            return idCardName;
        }

        public void setIdCardName(String idCardName) {
            this.idCardName = idCardName;
        }

        public String getIdCardMobile() {
            return idCardMobile;
        }

        public void setIdCardMobile(String idCardMobile) {
            this.idCardMobile = idCardMobile;
        }

        public String getPayChannelCode() {
            return payChannelCode;
        }

        public void setPayChannelCode(String payChannelCode) {
            this.payChannelCode = payChannelCode;
        }

        public String getPaymentNo() {
            return paymentNo;
        }

        public void setPaymentNo(String paymentNo) {
            this.paymentNo = paymentNo;
        }

        public Double getTariff() {
            return tariff;
        }

        public void setTariff(Double tariff) {
            this.tariff = tariff;
        }

        public String getDeclareNo() {
            return declareNo;
        }

        public void setDeclareNo(String declareNo) {
            this.declareNo = declareNo;
        }

        public String getDeclareNoZS() {
            return declareNoZS;
        }

        public void setDeclareNoZS(String declareNoZS) {
            this.declareNoZS = declareNoZS;
        }

        public Character getIdentityCheck() {
            return identityCheck;
        }

        public void setIdentityCheck(Character identityCheck) {
            this.identityCheck = identityCheck;
        }

        public String getStorage() {
            return storage;
        }

        public void setStorage(String storage) {
            this.storage = storage;
        }

        public String getStorageName() {
            return storageName;
        }

        public void setStorageName(String storageName) {
            this.storageName = storageName;
        }
    }
    public boolean isSuccessful(){
        if (this.status!=null){
            return StringUtil.equals(status.getCode(),"0000000");
        }return false;
    }
}
