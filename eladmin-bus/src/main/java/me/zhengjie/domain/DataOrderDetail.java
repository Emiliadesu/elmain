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
* @date 2022-03-30
**/
@Entity
@Data
@Table(name="bus_data_order_detail")
public class DataOrderDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "order_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @Column(name = "cross_border_no")
    @ApiModelProperty(value = "交易号")
    private String crossBorderNo;

    @Column(name = "customers_id")
    @ApiModelProperty(value = "客户ID")
    private Long customersId;

    @Column(name = "shop_id")
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;

    @Column(name = "customers_name")
    @ApiModelProperty(value = "客户名称")
    private String customersName;

    @Column(name = "shop_name")
    @ApiModelProperty(value = "店铺名称")
    private String shopName;

    @Column(name = "logistics_no")
    @ApiModelProperty(value = "运单号")
    private String logisticsNo;

    @Column(name = "goods_id")
    @ApiModelProperty(value = "货品ID")
    private Long goodsId;

    @Column(name = "goods_code")
    @ApiModelProperty(value = "货品编码")
    private String goodsCode;

    @Column(name = "goods_no")
    @ApiModelProperty(value = "海关货号")
    private String goodsNo;

    @Column(name = "hs_code")
    @ApiModelProperty(value = "HS编码")
    private String hsCode;

    @Column(name = "font_goods_name")
    @ApiModelProperty(value = "前端商品名称")
    private String fontGoodsName;

    @Column(name = "goods_name")
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @Column(name = "qty")
    @ApiModelProperty(value = "数量")
    private String qty;

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

    @Column(name = "consumable_material_code")
    @ApiModelProperty(value = "耗材编码")
    private String consumableMaterialCode;

    @Column(name = "consumable_material_num")
    @ApiModelProperty(value = "耗材数量")
    private String consumableMaterialNum;

    public void copy(DataOrderDetail source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}