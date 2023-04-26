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
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2023-03-21
**/
@Entity
@Data
@Table(name="bus_dewu_declare_push_item")
public class DewuDeclarePushItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "order_id")
    @ApiModelProperty(value = "订单ID")
    private Long orderId;

    @Column(name = "order_no")
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @Column(name = "goods_no")
    @ApiModelProperty(value = "货号")
    private String goodsNo;

    @Column(name = "goods_name")
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @Column(name = "qty")
    @ApiModelProperty(value = "数量")
    private Integer qty;

    @Column(name = "declare_price")
    @ApiModelProperty(value = "申报单价")
    private BigDecimal declarePrice;

    @Column(name = "declare_amount")
    @ApiModelProperty(value = "申报总价")
    private BigDecimal declareAmount;

    @Column(name = "bar_code")
    @ApiModelProperty(value = "商品条码")
    private String barCode;

    @Column(name = "unit")
    @ApiModelProperty(value = "计量单位")
    private String unit;

    @Column(name = "tax")
    @ApiModelProperty(value = "税费(单个商品税费)")
    private BigDecimal tax;

    public void copy(DewuDeclarePushItem source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}