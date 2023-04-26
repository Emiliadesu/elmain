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
* @date 2021-07-22
**/
@Entity
@Data
@Table(name="bus_pack_check_details")
public class PackCheckDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "check_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "主单ID")
    private Long checkId;

    @Column(name = "goods_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "商品ID")
    private Long goodsId;

    @Column(name = "bar_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "条码")
    private String barCode;

    @Column(name = "goods_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "条码")
    private String goodsName;

    @Column(name = "expect_qty",nullable = false)
    @NotNull
    @ApiModelProperty(value = "预期数量")
    private Integer expectQty;

    @Column(name = "current_qty",nullable = false)
    @NotNull
    @ApiModelProperty(value = "当前数量")
    private Integer currentQty;

    public void copy(PackCheckDetails source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}