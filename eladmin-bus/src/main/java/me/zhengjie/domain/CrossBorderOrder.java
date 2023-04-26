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
package me.zhengjie.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.io.Serializable;
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2021-03-25
**/
@Entity
//@Data
@Table(name="bus_cross_border_order")
public class CrossBorderOrder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "order_no",unique = true,nullable = false)
    @NotBlank
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @Column(name = "status",nullable = false)
    @NotNull
    @ApiModelProperty(value = "状态")
    private Integer status;

    @Column(name = "up_status",nullable = false)
    @ApiModelProperty(value = "上游订单状态")
    private String upStatus;

    @Column(name = "customers_id")
    @ApiModelProperty(value = "客户ID")
    private Long customersId;

    @Column(name = "is_lock")
    @ApiModelProperty(value = "是否锁单")
    private String isLock;

    @Column(name = "shop_id")
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;

    @Column(name = "platform_shop_id")
    @ApiModelProperty(value = "平台店铺ID")
    private String platformShopId;

    @Column(name = "clear_company_id")
    @ApiModelProperty(value = "清关抬头ID")
    private Long clearCompanyId;

    @Column(name = "platform_code")
    @ApiModelProperty(value = "电商平台代码")
    private String platformCode;

    @Column(name = "supplier_id")
    @ApiModelProperty(value = "承运商ID")
    private Long supplierId;

    @Transient
    private Long platformId;

    @Column(name = "lp_code")
    @ApiModelProperty(value = "菜鸟LP单号")
    private String lpCode;

    @Column(name = "cn_status")
    @ApiModelProperty(value = "菜鸟状态")
    private String cnStatus;

    @Column(name = "cross_border_no")
    @ApiModelProperty(value = "交易号")
    private String crossBorderNo;

    @Column(name = "ebp_code")
    @ApiModelProperty(value = "电商平台代码")
    private String ebpCode;

    @Column(name = "ebp_name")
    @ApiModelProperty(value = "电商平台名称")
    private String ebpName;

    @Column(name = "order_form")
    @ApiModelProperty(value = "平台跨境购编码")
    private String orderForm;

    @Column(name = "declare_no")
    @ApiModelProperty(value = "申报单号")
    private String declareNo;

    @Column(name = "declare_status")
    @ApiModelProperty(value = "申报状态")
    private String declareStatus;

    @Column(name = "declare_msg")
    @ApiModelProperty(value = "申报信息")
    private String declareMsg;

    @Column(name = "invt_no")
    @ApiModelProperty(value = "总署清单编号")
    private String invtNo;

    @Column(name = "order_create_time")
    @ApiModelProperty(value = "订单创建时间")
    private Timestamp orderCreateTime;

    @Column(name = "err_msg")
    @ApiModelProperty(value = "异常信息")
    private String errMsg;

    @Column(name = "payment")
    @ApiModelProperty(value = "实付金额")
    private String payment;

    @Column(name = "post_fee")
    @ApiModelProperty(value = "运费")
    private String postFee;

    @Column(name = "buyer_account")
    @ApiModelProperty(value = "买家账号")
    private String buyerAccount;

    @Column(name = "tariff_amount")
    @ApiModelProperty(value = "关税")
    private String tariffAmount;

    @Column(name = "added_value_tax_amount")
    @ApiModelProperty(value = "增值税")
    private String addedValueTaxAmount;

    @Column(name = "consumption_duty_amount")
    @ApiModelProperty(value = "消费税")
    private String consumptionDutyAmount;

    @Column(name = "tax_amount")
    @ApiModelProperty(value = "总税额")
    private String taxAmount;

    @Column(name = "net_weight")
    @ApiModelProperty(value = "净重（千克）")
    private String netWeight;

    @Column(name = "gross_weight")
    @ApiModelProperty(value = "毛重（千克）")
    private String grossWeight;

    @Column(name = "dis_amount")
    @ApiModelProperty(value = "优惠金额合计")
    private String disAmount;

    @Column(name = "pre_sell")
    @ApiModelProperty(value = "是否预售")
    private String preSell;

    @Column(name = "freeze_reason")
    @ApiModelProperty(value = "冻结原因")
    private String freezeReason;

    @Column(name = "buyer_remark")
    @ApiModelProperty(value = "客户备注")
    private String buyerRemark;

    @Column(name = "buyer_phone")
    @ApiModelProperty(value = "订购人电话")
    private String buyerPhone;

    @Column(name = "buyer_id_num")
    @ApiModelProperty(value = "订购人身份证号码")
    private String buyerIdNum;

    @Column(name = "buyer_name")
    @ApiModelProperty(value = "订购人姓名")
    private String buyerName;

    @Column(name = "books_no")
    @ApiModelProperty(value = "账册编号")
    private String booksNo;

    @Column(name = "pay_code")
    @ApiModelProperty(value = "支付方式")
    private String payCode;

    @Column(name = "pay_time")
    @ApiModelProperty(value = "支付时间")
    private Timestamp payTime;

    @Column(name = "payment_no")
    @ApiModelProperty(value = "支付单号")
    private String paymentNo;

    @Column(name = "order_seq_no")
    @ApiModelProperty(value = "支付交易号")
    private String orderSeqNo;

    @Column(name = "logistics_no")
    @ApiModelProperty(value = "运单号")
    private String logisticsNo;

    @Column(name = "logistics_status")
    @ApiModelProperty(value = "快递状态")
    private Integer logisticsStatus;

    @Column(name = "order_msg")
    @ApiModelProperty(value = "订单报文")
    private String orderMsg;

    @Column(name = "logistics_msg")
    @ApiModelProperty(value = "快递信息")
    private String logisticsMsg;

    @Column(name = "province")
    @ApiModelProperty(value = "省")
    private String province;

    @Column(name = "city")
    @ApiModelProperty(value = "市")
    private String city;

    @Column(name = "district")
    @ApiModelProperty(value = "区")
    private String district;

    @Column(name = "consignee_name")
    @ApiModelProperty(value = "收货人")
    private String consigneeName;

    @Column(name = "consignee_addr")
    @ApiModelProperty(value = "收货地址")
    private String consigneeAddr;

    @Column(name = "consignee_tel")
    @ApiModelProperty(value = "收货电话")
    private String consigneeTel;

    @Column(name = "add_mark")
    @ApiModelProperty(value = "大头笔")
    private String addMark;

    @Column(name = "received_back_time")
    @ApiModelProperty(value = "接单回传时间")
    private Timestamp receivedBackTime;

    @Column(name = "exp_deliver_time")
    @ApiModelProperty(value = "预计出库时间")
    private Timestamp expDeliverTime;

    @Column(name = "clear_start_time")
    @ApiModelProperty(value = "清关开始时间")
    private Timestamp clearStartTime;

    @Column(name = "clear_start_back_time")
    @ApiModelProperty(value = "清关开始回传时间")
    private Timestamp clearStartBackTime;

    @Column(name = "clear_success_time")
    @ApiModelProperty(value = "清关完成时间")
    private Timestamp clearSuccessTime;

    @Column(name = "clear_success_back_time")
    @ApiModelProperty(value = "清关完成回传时间")
    private Timestamp clearSuccessBackTime;

    @Column(name = "pack_time")
    @ApiModelProperty(value = "打包时间")
    private Timestamp packTime;

    @Column(name = "pack_back_time")
    @ApiModelProperty(value = "打包完成回传时间")
    private Timestamp packBackTime;

    @Column(name = "weighing_time")
    @ApiModelProperty(value = "称重时间")
    private Timestamp weighingTime;

    @Column(name = "deliver_time")
    @ApiModelProperty(value = "出库时间")
    private Timestamp deliverTime;

    @Column(name = "logistics_collect_time")
    @ApiModelProperty(value = "快递揽收时间")
    private Timestamp logisticsCollectTime;

    @Column(name = "logistics_sign_time")
    @ApiModelProperty(value = "快递签收时间")
    private Timestamp logisticsSignTime;

    @Column(name = "cancel_time")
    @ApiModelProperty(value = "取消时间")
    private Timestamp cancelTime;

    @Column(name = "create_by")
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    /**2021-07-12 WMS信息**/
    @Column(name = "wms_status")
    @ApiModelProperty(value = "WMS状态")
    private String wmsStatus;

    @Column(name = "send_pick_flag")
    @ApiModelProperty(value = "拣货发送指令")
    private String sendPickFlag;

    @Column(name = "is_wave")
    @ApiModelProperty(value = "是否产生波次")
    private String isWave;

    @Column(name = "wave_no")
    @ApiModelProperty(value = "波次号")
    private String waveNo;

    @Column(name = "wave_name")
    @ApiModelProperty(value = "波次名称")
    private String waveName;

    @Column(name = "pick_type")
    @ApiModelProperty(value = "拣选单类型")
    private String pickType;

    @Column(name = "area")
    @ApiModelProperty(value = "区域")
    private String area;

    @Column(name = "sku_num")
    @ApiModelProperty(value = "sku数量")
    private String skuNum;

    @Column(name = "total_num")
    @ApiModelProperty(value = "件数")
    private String totalNum;

    @Column(name = "seq_no")
    @ApiModelProperty(value = "格口号")
    private String seqNo;

    @Column(name = "so_no")
    @ApiModelProperty(value = "SO单号")
    private String soNo;

    @Column(name = "is_print")
    @ApiModelProperty(value = "是否打印")
    private String isPrint;

    @Column(name = "pick_print_info")
    @ApiModelProperty(value = "拣选单打印信息")
    private String pickPrintInfo;

    @Column(name = "bill_print_info")
    @ApiModelProperty(value = "面单打印信息")
    private String billPrintInfo;

    @Column(name = "print_time")
    @ApiModelProperty(value = "打印时间")
    private String printTime;

    @Column(name = "pack_weight")
    @ApiModelProperty(value = "包裹重量")
    private String packWeight;

    @Column(name = "theory_weight")
    @ApiModelProperty(value = "包裹理论重量")
    private String theoryWeight;

    @Column(name = "material_code")
    @ApiModelProperty(value = "包材编码")
    private String materialCode;

    @Column(name = "platform_status")
    @ApiModelProperty(value = "平台状态编码")
    private Integer platformStatus;

    @Column(name = "default01")
    @ApiModelProperty(value = "预留字段01")
    private String default01;// 是否更新wms时间

    @Column(name = "default02")
    @ApiModelProperty(value = "预留字段02")// //1服务商申报、2平台申报
    private String default02;// 申报方

    @Column(name = "default03")
    @ApiModelProperty(value = "预留字段03")
    private String default03;// 货主代码

    @Column(name = "default04")
    @ApiModelProperty(value = "预留字段04")
    private String default04;// 平台在跨境购的名称

    @Column(name = "default05")
    @ApiModelProperty(value = "预留字段05")
    private String default05;// 订单来源，1-抖音下发，2-跨境购下发

    @Column(name = "logistics_code")
    @ApiModelProperty(value = "快递公司代码")
    private String logisticsCode;

    @Column(name = "logistics_name")
    @ApiModelProperty(value = "快递公司名称")
    private String logisticsName;

    @Column(name = "refund_reason")
    @ApiModelProperty(value = "退款原因")
    private String refundReason;

    @Column(name = "vas_pack")
    @ApiModelProperty(value = "是否增值包装")
    private String vasPack;

    @Column(name = "four_pl")
    @ApiModelProperty(value = "4PL")
    private String fourPl;

    @Column(name = "logistics_four_pl")
    @ApiModelProperty(value = "快递4PL")
    private String logisticsFourPl;

    @Column(name = "clear_del_start_back_time")
    @ApiModelProperty(value = "清关单据删除开始 回传时间")
    private Timestamp clearDelStartBackTime;

    @Column(name = "clear_del_success_back_time")
    @ApiModelProperty(value = "清关单据删除成功 回传时间")
    private Timestamp clearDelSuccessBackTime;

    @Column(name = "cancel_time_back")
    @ApiModelProperty(value = "订单取消 回传时间")
    private Timestamp cancelTimeBack;

    @Column(name = "clear_del_start_time")
    @ApiModelProperty(value = "订单清关撤单时间")
    private String clearDelStartTime;

    /**2021-07-12 WMS信息**/

    @Transient
    private List<CrossBorderOrderDetails> itemList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUpStatus() {
        return upStatus;
    }

    public void setUpStatus(String upStatus) {
        this.upStatus = upStatus;
    }

    public Long getCustomersId() {
        return customersId;
    }

    public void setCustomersId(Long customersId) {
        this.customersId = customersId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getPlatformShopId() {
        return platformShopId;
    }

    public void setPlatformShopId(String platformShopId) {
        this.platformShopId = platformShopId;
    }

    public Long getClearCompanyId() {
        return clearCompanyId;
    }

    public void setClearCompanyId(Long clearCompanyId) {
        this.clearCompanyId = clearCompanyId;
    }

    public String getPlatformCode() {
        return platformCode;
    }

    public void setPlatformCode(String platformCode) {
        this.platformCode = platformCode;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getLpCode() {
        return lpCode;
    }

    public void setLpCode(String lpCode) {
        this.lpCode = lpCode;
    }

    public String getCnStatus() {
        return cnStatus;
    }

    public void setCnStatus(String cnStatus) {
        this.cnStatus = cnStatus;
    }

    public String getCrossBorderNo() {
        return crossBorderNo;
    }

    public void setCrossBorderNo(String crossBorderNo) {
        this.crossBorderNo = crossBorderNo;
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

    public String getOrderForm() {
        return orderForm;
    }

    public void setOrderForm(String orderForm) {
        this.orderForm = orderForm;
    }

    public String getDeclareNo() {
        return declareNo;
    }

    public void setDeclareNo(String declareNo) {
        this.declareNo = declareNo;
    }

    public String getInvtNo() {
        return invtNo;
    }

    public void setInvtNo(String invtNo) {
        this.invtNo = invtNo;
    }

    public Timestamp getOrderCreateTime() {
        return orderCreateTime;
    }

    public void setOrderCreateTime(Timestamp orderCreateTime) {
        this.orderCreateTime = orderCreateTime;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
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

    public String getBuyerAccount() {
        return buyerAccount;
    }

    public void setBuyerAccount(String buyerAccount) {
        this.buyerAccount = buyerAccount;
    }

    public String getTariffAmount() {
        return tariffAmount;
    }

    public void setTariffAmount(String tariffAmount) {
        this.tariffAmount = tariffAmount;
    }

    public String getAddedValueTaxAmount() {
        return addedValueTaxAmount;
    }

    public void setAddedValueTaxAmount(String addedValueTaxAmount) {
        this.addedValueTaxAmount = addedValueTaxAmount;
    }

    public String getConsumptionDutyAmount() {
        return consumptionDutyAmount;
    }

    public void setConsumptionDutyAmount(String consumptionDutyAmount) {
        this.consumptionDutyAmount = consumptionDutyAmount;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(String netWeight) {
        this.netWeight = netWeight;
    }

    public String getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(String grossWeight) {
        this.grossWeight = grossWeight;
    }

    public String getDisAmount() {
        return disAmount;
    }

    public void setDisAmount(String disAmount) {
        this.disAmount = disAmount;
    }

    public String getPreSell() {
        return preSell;
    }

    public void setPreSell(String preSell) {
        this.preSell = preSell;
    }

    public String getFreezeReason() {
        return freezeReason;
    }

    public void setFreezeReason(String freezeReason) {
        this.freezeReason = freezeReason;
    }

    public String getBuyerRemark() {
        return buyerRemark;
    }

    public void setBuyerRemark(String buyerRemark) {
        this.buyerRemark = buyerRemark;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    public String getBuyerIdNum() {
        return buyerIdNum;
    }

    public void setBuyerIdNum(String buyerIdNum) {
        this.buyerIdNum = buyerIdNum;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBooksNo() {
        return booksNo;
    }

    public void setBooksNo(String booksNo) {
        this.booksNo = booksNo;
    }

    public String getPayCode() {
        return payCode;
    }

    public void setPayCode(String payCode) {
        this.payCode = payCode;
    }

    public Timestamp getPayTime() {
        return payTime;
    }

    public void setPayTime(Timestamp payTime) {
        this.payTime = payTime;
    }

    public String getPaymentNo() {
        return paymentNo;
    }

    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }

    public String getOrderSeqNo() {
        return orderSeqNo;
    }

    public void setOrderSeqNo(String orderSeqNo) {
        this.orderSeqNo = orderSeqNo;
    }

    public String getLogisticsNo() {
        return logisticsNo;
    }

    public void setLogisticsNo(String logisticsNo) {
        this.logisticsNo = logisticsNo;
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

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getConsigneeName() {
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    public String getConsigneeAddr() {
        return consigneeAddr;
    }

    public void setConsigneeAddr(String consigneeAddr) {
        this.consigneeAddr = consigneeAddr;
    }

    public String getConsigneeTel() {
        return consigneeTel;
    }

    public void setConsigneeTel(String consigneeTel) {
        this.consigneeTel = consigneeTel;
    }

    public String getAddMark() {
        return addMark;
    }

    public void setAddMark(String addMark) {
        this.addMark = addMark;
    }

    public Timestamp getReceivedBackTime() {
        return receivedBackTime;
    }

    public void setReceivedBackTime(Timestamp receivedBackTime) {
        this.receivedBackTime = receivedBackTime;
    }

    public Timestamp getClearStartTime() {
        return clearStartTime;
    }

    public void setClearStartTime(Timestamp clearStartTime) {
        this.clearStartTime = clearStartTime;
    }

    public Timestamp getClearStartBackTime() {
        return clearStartBackTime;
    }

    public void setClearStartBackTime(Timestamp clearStartBackTime) {
        this.clearStartBackTime = clearStartBackTime;
    }

    public Timestamp getClearSuccessTime() {
        return clearSuccessTime;
    }

    public void setClearSuccessTime(Timestamp clearSuccessTime) {
        this.clearSuccessTime = clearSuccessTime;
    }

    public Timestamp getClearSuccessBackTime() {
        return clearSuccessBackTime;
    }

    public void setClearSuccessBackTime(Timestamp clearSuccessBackTime) {
        this.clearSuccessBackTime = clearSuccessBackTime;
    }

    public Timestamp getDeliverTime() {
        return deliverTime;
    }

    public void setDeliverTime(Timestamp deliverTime) {
        this.deliverTime = deliverTime;
    }

    public Timestamp getPackTime() {
        return packTime;
    }

    public void setPackTime(Timestamp packTime) {
        this.packTime = packTime;
    }

    public Timestamp getWeighingTime() {
        return weighingTime;
    }

    public void setWeighingTime(Timestamp weighingTime) {
        this.weighingTime = weighingTime;
    }

    public Timestamp getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(Timestamp cancelTime) {
        this.cancelTime = cancelTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public List<CrossBorderOrderDetails> getItemList() {
        return itemList;
    }

    public void setItemList(List<CrossBorderOrderDetails> itemList) {
        this.itemList = itemList;
    }

    public String getDeclareStatus() {
        return declareStatus;
    }

    public void setDeclareStatus(String declareStatus) {
        this.declareStatus = declareStatus;
    }

    public String getDeclareMsg() {
        return declareMsg;
    }

    public void setDeclareMsg(String declareMsg) {
        this.declareMsg = declareMsg;
    }

    public Timestamp getPackBackTime() {
        return packBackTime;
    }

    public void setPackBackTime(Timestamp packBackTime) {
        this.packBackTime = packBackTime;
    }

    public Integer getLogisticsStatus() {
        return logisticsStatus;
    }

    public void setLogisticsStatus(Integer logisticsStatus) {
        this.logisticsStatus = logisticsStatus;
    }

    public String getLogisticsMsg() {
        return logisticsMsg;
    }

    public void setLogisticsMsg(String logisticsMsg) {
        this.logisticsMsg = logisticsMsg;
    }

    public Timestamp getExpDeliverTime() {
        return expDeliverTime;
    }

    public void setExpDeliverTime(Timestamp expDeliverTime) {
        this.expDeliverTime = expDeliverTime;
    }

    public Timestamp getLogisticsCollectTime() {
        return logisticsCollectTime;
    }

    public void setLogisticsCollectTime(Timestamp logisticsCollectTime) {
        this.logisticsCollectTime = logisticsCollectTime;
    }

    public Timestamp getLogisticsSignTime() {
        return logisticsSignTime;
    }

    public void setLogisticsSignTime(Timestamp logisticsSignTime) {
        this.logisticsSignTime = logisticsSignTime;
    }

    public String getWmsStatus() {
        return wmsStatus;
    }

    public void setWmsStatus(String wmsStatus) {
        this.wmsStatus = wmsStatus;
    }

    public String getSendPickFlag() {
        return sendPickFlag;
    }

    public void setSendPickFlag(String sendPickFlag) {
        this.sendPickFlag = sendPickFlag;
    }

    public String getIsWave() {
        return isWave;
    }

    public void setIsWave(String isWave) {
        this.isWave = isWave;
    }

    public String getWaveNo() {
        return waveNo;
    }

    public void setWaveNo(String waveNo) {
        this.waveNo = waveNo;
    }

    public String getIsPrint() {
        return isPrint;
    }

    public void setIsPrint(String isPrint) {
        this.isPrint = isPrint;
    }

    public String getPrintTime() {
        return printTime;
    }

    public void setPrintTime(String printTime) {
        this.printTime = printTime;
    }

    public String getPickPrintInfo() {
        return pickPrintInfo;
    }

    public void setPickPrintInfo(String pickPrintInfo) {
        this.pickPrintInfo = pickPrintInfo;
    }

    public String getBillPrintInfo() {
        return billPrintInfo;
    }

    public void setBillPrintInfo(String billPrintInfo) {
        this.billPrintInfo = billPrintInfo;
    }

    public String getPackWeight() {
        return packWeight;
    }

    public void setPackWeight(String packWeight) {
        this.packWeight = packWeight;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public Integer getPlatformStatus() {
        return platformStatus;
    }

    public String getIsLock() {
        return isLock;
    }

    public void setIsLock(String isLock) {
        this.isLock = isLock;
    }

    public void setPlatformStatus(Integer platformStatus) {
        this.platformStatus = platformStatus;
    }

    public String getDefault01() {
        return default01;
    }

    public void setDefault01(String default01) {
        this.default01 = default01;
    }

    public String getDefault02() {
        return default02;
    }

    public void setDefault02(String default02) {
        this.default02 = default02;
    }

    public String getDefault03() {
        return default03;
    }

    public void setDefault03(String default03) {
        this.default03 = default03;
    }

    public String getDefault04() {
        return default04;
    }

    public void setDefault04(String default04) {
        this.default04 = default04;
    }

    public String getDefault05() {
        return default05;
    }

    public void setDefault05(String default05) {
        this.default05 = default05;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public String getLogisticsCode() {
        return logisticsCode;
    }

    public void setLogisticsCode(String logisticsCode) {
        this.logisticsCode = logisticsCode;
    }

    public String getLogisticsName() {
        return logisticsName;
    }

    public void setLogisticsName(String logisticsName) {
        this.logisticsName = logisticsName;
    }

    public String getSoNo() {
        return soNo;
    }

    public void setSoNo(String soNo) {
        this.soNo = soNo;
    }

    public String getVasPack() {
        return vasPack;
    }

    public void setVasPack(String vasPack) {
        this.vasPack = vasPack;
    }

    public String getFourPl() {
        return fourPl;
    }

    public void setFourPl(String fourPl) {
        this.fourPl = fourPl;
    }

    public Timestamp getClearDelStartBackTime() {
        return clearDelStartBackTime;
    }

    public void setClearDelStartBackTime(Timestamp clearDelStartBackTime) {
        this.clearDelStartBackTime = clearDelStartBackTime;
    }

    public Timestamp getClearDelSuccessBackTime() {
        return clearDelSuccessBackTime;
    }

    public void setClearDelSuccessBackTime(Timestamp clearDelSuccessBackTime) {
        this.clearDelSuccessBackTime = clearDelSuccessBackTime;
    }

    public Timestamp getCancelTimeBack() {
        return cancelTimeBack;
    }

    public void setCancelTimeBack(Timestamp cancelTimeBack) {
        this.cancelTimeBack = cancelTimeBack;
    }

    public String getOrderMsg() {
        return orderMsg;
    }

    public void setOrderMsg(String orderMsg) {
        this.orderMsg = orderMsg;
    }

    public String getWaveName() {
        return waveName;
    }

    public void setWaveName(String waveName) {
        this.waveName = waveName;
    }

    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    public String getPickType() {
        return pickType;
    }

    public void setPickType(String pickType) {
        this.pickType = pickType;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getSkuNum() {
        return skuNum;
    }

    public void setSkuNum(String skuNum) {
        this.skuNum = skuNum;
    }

    public String getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(String totalNum) {
        this.totalNum = totalNum;
    }

    public String getTheoryWeight() {
        return theoryWeight;
    }

    public void setTheoryWeight(String theoryWeight) {
        this.theoryWeight = theoryWeight;
    }

    public String getClearDelStartTime() {
        return clearDelStartTime;
    }

    public void setClearDelStartTime(String clearDelStartTime) {
        this.clearDelStartTime = clearDelStartTime;
    }

    public String getLogisticsFourPl() {
        return logisticsFourPl;
    }

    public void setLogisticsFourPl(String logisticsFourPl) {
        this.logisticsFourPl = logisticsFourPl;
    }
}
