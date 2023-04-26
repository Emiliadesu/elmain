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
* @author wangm
* @date 2021-12-15
**/
@Entity
@Data
@Table(name="bus_customer_complain_item")
public class CustomerComplainItem implements Serializable {

    @Id
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "complain_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "客诉ID")
    private Long complainId;

    @Column(name = "bar_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "条码")
    private String barCode;

    @Column(name = "goods_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "goodsName")
    private String goodsName;

    @Column(name = "goods_status")
    @ApiModelProperty(value = "商品状态")
    private String goodsStatus;

    @Column(name = "qty",nullable = false)
    @NotNull
    @ApiModelProperty(value = "商品数量")
    private Integer qty;

    @Column(name = "cust_qty",nullable = false)
    @NotNull
    @ApiModelProperty(value = "商品数量")
    private Integer custQty;

    public void copy(CustomerComplainItem source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
