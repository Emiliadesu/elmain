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
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2021-03-25
**/
@Entity
//@Data
@Table(name="bus_cross_border_order_details")
public class CrossBorderOrderDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "order_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "订单ID")
    private Long orderId;

    @Column(name = "order_no",nullable = false)
    @NotNull
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @Column(name = "goods_id")
    @ApiModelProperty(value = "货品ID")
    private Long goodsId;

    @Column(name = "goods_code")
    @ApiModelProperty(value = "货品编码")
    private String goodsCode;

    @Column(name = "goods_no")
    @ApiModelProperty(value = "海关货号")
    private String goodsNo;

    @Column(name = "plat_sku_id")
    @ApiModelProperty(value = "平台skuId")
    private String platSkuId;

    @Column(name = "bar_code")
    @ApiModelProperty(value = "条码")
    private String barCode;

    @Column(name = "hs_code")
    @ApiModelProperty(value = "HS编码")
    private String hsCode;

    @Column(name = "font_goods_name")
    @ApiModelProperty(value = "商品名称")
    private String fontGoodsName;

    @Column(name = "goods_name")
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @Column(name = "qty")
    @ApiModelProperty(value = "数量")
    private String qty;

    @Column(name = "unit")
    @ApiModelProperty(value = "计量单位")
    private String unit;

    @Column(name = "payment")
    @ApiModelProperty(value = "实付总价")
    private String payment;

    @Column(name = "dutiable_value")
    @ApiModelProperty(value = "完税单价")
    private String dutiableValue;

    @Column(name = "dutiable_total_value")
    @ApiModelProperty(value = "完税总价")
    private String dutiableTotalValue;

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

    @Column(name = "make_country")
    @ApiModelProperty(value = "原产国")
    private String makeCountry;

    @Column(name = "net_weight")
    @ApiModelProperty(value = "净重（千克）")
    private String netWeight;

    @Column(name = "gross_weight")
    @ApiModelProperty(value = "毛重（千克）")
    private String grossWeight;

    public void copy(CrossBorderOrderDetails source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }

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

    public String getPlatSkuId() {
        return platSkuId;
    }

    public void setPlatSkuId(String platSkuId) {
        this.platSkuId = platSkuId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public String getGoodsNo() {
        return goodsNo;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getHsCode() {
        return hsCode;
    }

    public void setHsCode(String hsCode) {
        this.hsCode = hsCode;
    }

    public String getFontGoodsName() {
        return fontGoodsName;
    }

    public void setFontGoodsName(String fontGoodsName) {
        this.fontGoodsName = fontGoodsName;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getDutiableValue() {
        return dutiableValue;
    }

    public void setDutiableValue(String dutiableValue) {
        this.dutiableValue = dutiableValue;
    }

    public String getDutiableTotalValue() {
        return dutiableTotalValue;
    }

    public void setDutiableTotalValue(String dutiableTotalValue) {
        this.dutiableTotalValue = dutiableTotalValue;
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

    public String getMakeCountry() {
        return makeCountry;
    }

    public void setMakeCountry(String makeCountry) {
        this.makeCountry = makeCountry;
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

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
