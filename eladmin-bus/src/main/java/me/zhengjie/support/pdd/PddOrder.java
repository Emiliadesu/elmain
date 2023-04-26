/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.support.pdd;

import java.sql.Timestamp;
import java.math.BigDecimal;
import java.io.Serializable;
import java.util.List;

/**
 * @website https://el-admin.vip
 * @description /
 * @author wangm
 * @date 2021-06-10
 **/
public class PddOrder implements Serializable {

    private String id;

    private String pddUserId;

    private BigDecimal deliveryInstallValue;

    private BigDecimal deliveryHomeValue;

    private BigDecimal homeInstallValue;

    private String cardInfoList;

    private String idCardNum;

    private String idCardName;

    private Integer freeSf;

    private Integer confirmStatus;

    private Integer groupStatus;

    private Integer returnFreightPayer;

    private Integer homeDeliveryType;

    private Integer stepTradeStatus;

    private BigDecimal stepPaidFee;

    private BigDecimal advancedPaidFee;

    private BigDecimal stepDiscountAmount;



    private Integer tradeType;



    private String orderSn;



    private BigDecimal capitalFreeDiscount;



    private BigDecimal sellerDiscount;



    private BigDecimal platformDiscount;



    private String remark;



    private String lastShipTime;



    private String updatedAt;



    private Integer refundStatus;



    private Integer isLuckyFlag;



    private Integer orderStatus;



    private String shippingTime;



    private String trackingNumber;



    private Long logisticsId;



    private String payType;



    private String payNo;



    private BigDecimal postage;


    private BigDecimal discountAmount;


    private BigDecimal goodsAmount;


    private BigDecimal payAmount;


    private String receiverPhone;



    private String address;



    private String town;



    private String city;



    private String province;



    private String country;



    private String createdTime;



    private String receiverName;



    private String confirmTime;



    private String receiveTime;



    private Integer townId;



    private Integer cityId;



    private Integer provinceId;



    private Integer countryId;



    private Integer isStockOut;


    private String innerTransactionId;



    private String buyerMemo;


    private Integer invoiceStatus;



    private String preSaleTime;



    private Integer isPreSale;



    private Integer afterSalesStatus;



    private String payTime;



    private Long depotId;



    private String depotName;



    private String depotCode;



    private Long wareId;



    private String wareName;


    private Integer wareType;



    private String wareSn;



    private Integer depotType;



    private String receiverAddress;



    private Integer selfContained;



    private Long lShopId;



    private Long custId;



    private Integer riskControlStatus;

    private Timestamp pullTime;



    private String declareStatus;

    private String status;

    private List<PddOrderItem> orderItems;

    public Long getlShopId() {
        return lShopId;
    }

    public void setlShopId(Long lShopId) {
        this.lShopId = lShopId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPddUserId() {
        return pddUserId;
    }

    public void setPddUserId(String pddUserId) {
        this.pddUserId = pddUserId;
    }

    public BigDecimal getDeliveryInstallValue() {
        return deliveryInstallValue;
    }

    public void setDeliveryInstallValue(BigDecimal deliveryInstallValue) {
        this.deliveryInstallValue = deliveryInstallValue;
    }

    public BigDecimal getDeliveryHomeValue() {
        return deliveryHomeValue;
    }

    public void setDeliveryHomeValue(BigDecimal deliveryHomeValue) {
        this.deliveryHomeValue = deliveryHomeValue;
    }

    public BigDecimal getHomeInstallValue() {
        return homeInstallValue;
    }

    public void setHomeInstallValue(BigDecimal homeInstallValue) {
        this.homeInstallValue = homeInstallValue;
    }

    public String getCardInfoList() {
        return cardInfoList;
    }

    public void setCardInfoList(String cardInfoList) {
        this.cardInfoList = cardInfoList;
    }

    public String getIdCardNum() {
        return idCardNum;
    }

    public void setIdCardNum(String idCardNum) {
        this.idCardNum = idCardNum;
    }

    public String getIdCardName() {
        return idCardName;
    }

    public void setIdCardName(String idCardName) {
        this.idCardName = idCardName;
    }

    public Integer getFreeSf() {
        return freeSf;
    }

    public void setFreeSf(Integer freeSf) {
        this.freeSf = freeSf;
    }

    public Integer getConfirmStatus() {
        return confirmStatus;
    }

    public void setConfirmStatus(Integer confirmStatus) {
        this.confirmStatus = confirmStatus;
    }

    public Integer getGroupStatus() {
        return groupStatus;
    }

    public void setGroupStatus(Integer groupStatus) {
        this.groupStatus = groupStatus;
    }

    public Integer getReturnFreightPayer() {
        return returnFreightPayer;
    }

    public void setReturnFreightPayer(Integer returnFreightPayer) {
        this.returnFreightPayer = returnFreightPayer;
    }

    public Integer getHomeDeliveryType() {
        return homeDeliveryType;
    }

    public void setHomeDeliveryType(Integer homeDeliveryType) {
        this.homeDeliveryType = homeDeliveryType;
    }

    public Integer getStepTradeStatus() {
        return stepTradeStatus;
    }

    public void setStepTradeStatus(Integer stepTradeStatus) {
        this.stepTradeStatus = stepTradeStatus;
    }

    public BigDecimal getStepPaidFee() {
        return stepPaidFee;
    }

    public void setStepPaidFee(BigDecimal stepPaidFee) {
        this.stepPaidFee = stepPaidFee;
    }

    public BigDecimal getAdvancedPaidFee() {
        return advancedPaidFee;
    }

    public void setAdvancedPaidFee(BigDecimal advancedPaidFee) {
        this.advancedPaidFee = advancedPaidFee;
    }

    public BigDecimal getStepDiscountAmount() {
        return stepDiscountAmount;
    }

    public void setStepDiscountAmount(BigDecimal stepDiscountAmount) {
        this.stepDiscountAmount = stepDiscountAmount;
    }

    public Integer getTradeType() {
        return tradeType;
    }

    public void setTradeType(Integer tradeType) {
        this.tradeType = tradeType;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public BigDecimal getCapitalFreeDiscount() {
        return capitalFreeDiscount;
    }

    public void setCapitalFreeDiscount(BigDecimal capitalFreeDiscount) {
        this.capitalFreeDiscount = capitalFreeDiscount;
    }

    public BigDecimal getSellerDiscount() {
        return sellerDiscount;
    }

    public void setSellerDiscount(BigDecimal sellerDiscount) {
        this.sellerDiscount = sellerDiscount;
    }

    public BigDecimal getPlatformDiscount() {
        return platformDiscount;
    }

    public void setPlatformDiscount(BigDecimal platformDiscount) {
        this.platformDiscount = platformDiscount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getLastShipTime() {
        return lastShipTime;
    }

    public void setLastShipTime(String lastShipTime) {
        this.lastShipTime = lastShipTime;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(Integer refundStatus) {
        this.refundStatus = refundStatus;
    }

    public Integer getIsLuckyFlag() {
        return isLuckyFlag;
    }

    public void setIsLuckyFlag(Integer isLuckyFlag) {
        this.isLuckyFlag = isLuckyFlag;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getShippingTime() {
        return shippingTime;
    }

    public void setShippingTime(String shippingTime) {
        this.shippingTime = shippingTime;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Long getLogisticsId() {
        return logisticsId;
    }

    public void setLogisticsId(Long logisticsId) {
        this.logisticsId = logisticsId;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPayNo() {
        return payNo;
    }

    public void setPayNo(String payNo) {
        this.payNo = payNo;
    }

    public BigDecimal getPostage() {
        return postage;
    }

    public void setPostage(BigDecimal postage) {
        this.postage = postage;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getGoodsAmount() {
        return goodsAmount;
    }

    public void setGoodsAmount(BigDecimal goodsAmount) {
        this.goodsAmount = goodsAmount;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(String confirmTime) {
        this.confirmTime = confirmTime;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
    }

    public Integer getTownId() {
        return townId;
    }

    public void setTownId(Integer townId) {
        this.townId = townId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public Integer getIsStockOut() {
        return isStockOut;
    }

    public void setIsStockOut(Integer isStockOut) {
        this.isStockOut = isStockOut;
    }

    public String getInnerTransactionId() {
        return innerTransactionId;
    }

    public void setInnerTransactionId(String innerTransactionId) {
        this.innerTransactionId = innerTransactionId;
    }

    public String getBuyerMemo() {
        return buyerMemo;
    }

    public void setBuyerMemo(String buyerMemo) {
        this.buyerMemo = buyerMemo;
    }

    public Integer getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(Integer invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public String getPreSaleTime() {
        return preSaleTime;
    }

    public void setPreSaleTime(String preSaleTime) {
        this.preSaleTime = preSaleTime;
    }

    public Integer getIsPreSale() {
        return isPreSale;
    }

    public void setIsPreSale(Integer isPreSale) {
        this.isPreSale = isPreSale;
    }

    public Integer getAfterSalesStatus() {
        return afterSalesStatus;
    }

    public void setAfterSalesStatus(Integer afterSalesStatus) {
        this.afterSalesStatus = afterSalesStatus;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public Long getDepotId() {
        return depotId;
    }

    public void setDepotId(Long depotId) {
        this.depotId = depotId;
    }

    public String getDepotName() {
        return depotName;
    }

    public void setDepotName(String depotName) {
        this.depotName = depotName;
    }

    public String getDepotCode() {
        return depotCode;
    }

    public void setDepotCode(String depotCode) {
        this.depotCode = depotCode;
    }

    public Long getWareId() {
        return wareId;
    }

    public void setWareId(Long wareId) {
        this.wareId = wareId;
    }

    public String getWareName() {
        return wareName;
    }

    public void setWareName(String wareName) {
        this.wareName = wareName;
    }

    public Integer getWareType() {
        return wareType;
    }

    public void setWareType(Integer wareType) {
        this.wareType = wareType;
    }

    public String getWareSn() {
        return wareSn;
    }

    public void setWareSn(String wareSn) {
        this.wareSn = wareSn;
    }

    public Integer getDepotType() {
        return depotType;
    }

    public void setDepotType(Integer depotType) {
        this.depotType = depotType;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public Integer getSelfContained() {
        return selfContained;
    }

    public void setSelfContained(Integer selfContained) {
        this.selfContained = selfContained;
    }

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    public Integer getRiskControlStatus() {
        return riskControlStatus;
    }

    public void setRiskControlStatus(Integer riskControlStatus) {
        this.riskControlStatus = riskControlStatus;
    }

    public Timestamp getPullTime() {
        return pullTime;
    }

    public void setPullTime(Timestamp pullTime) {
        this.pullTime = pullTime;
    }

    public String getDeclareStatus() {
        return declareStatus;
    }

    public void setDeclareStatus(String declareStatus) {
        this.declareStatus = declareStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<PddOrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<PddOrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
