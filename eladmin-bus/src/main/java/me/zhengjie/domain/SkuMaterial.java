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
import me.zhengjie.base.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2022-05-20
**/
@Entity
@Data
@Table(name="bus_sku_material")
public class SkuMaterial extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "customers_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "客户ID")
    private Long customersId;

    @Column(name = "shop_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;

    @Column(name = "goods_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "商品ID")
    private Long goodsId;

    @Column(name = "material_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "耗材ID")
    private Long materialId;

    @Column(name = "goods_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "货号")
    private String goodsNo;

    @Column(name = "goods_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "商品编码")
    private String goodsCode;

    @Column(name = "goods_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @Column(name = "bar_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "商品条码")
    private String barCode;

    @Column(name = "material_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "耗材编码")
    private String materialCode;

    @Column(name = "material_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "耗材名称")
    private String materialName;

    @Column(name = "qty",nullable = false)
    @NotNull
    @ApiModelProperty(value = "绑定数量")
    private Integer qty;


    public void copy(SkuMaterial source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}