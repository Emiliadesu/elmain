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
* @date 2021-03-21
**/
@Entity
@Data
@Table(name="bus_byte_dance_item")
public class ByteDanceItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @JoinColumn(name = "order_id")
    @ManyToOne(fetch=FetchType.LAZY,targetEntity = ByteDance.class)
    @ApiModelProperty(value = "抖店订单", hidden = true)
    private ByteDance order;

    @Column(name = "gnum",nullable = false)
    @NotNull
    @ApiModelProperty(value = "订单明细序号")
    private Long gnum;

    @Column(name = "ecp_sku_id",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "电商平台自己的sku id")
    private String ecpSkuId;

    @Column(name = "item_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "商家货号")
    private String itemNo;

    @Column(name = "g_model",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "商品规格型号")
    private String gModel;

    @Column(name = "bar_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "条码")
    private String barCode;

    @Column(name = "qty",nullable = false)
    @NotNull
    @ApiModelProperty(value = "商品数量")
    private Integer qty;

    @Column(name = "price",nullable = false)
    @NotNull
    @ApiModelProperty(value = "单价（商品不含税价，不含运费保费）")
    private BigDecimal price;

    @Column(name = "total_price",nullable = false)
    @NotNull
    @ApiModelProperty(value = "总价")
    private BigDecimal totalPrice;

    public void copy(ByteDanceItem source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
