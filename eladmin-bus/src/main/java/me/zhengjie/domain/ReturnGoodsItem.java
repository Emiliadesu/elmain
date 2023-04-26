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
* @author lh
* @date 2021-01-04
**/
@Entity
@Data
@Table(name="bus_return_goods_item")
public class ReturnGoodsItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "return_goods_id",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "退货id")
    private String returnGoodsId;

    @Column(name = "sku",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "海关货号")
    private String sku;

    @Column(name = "bar_code")
    @ApiModelProperty(value = "条码")
    private String barCode;

    @Column(name = "sku_name")
    @ApiModelProperty(value = "skuName")
    private String skuName;

    @Column(name = "num",nullable = false)
    @NotNull
    @ApiModelProperty(value = "货品数量")
    private Integer num;

    @Column(name = "status")
    @ApiModelProperty(value = "货品状态")
    private String status;

    public void copy(ReturnGoodsItem source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}