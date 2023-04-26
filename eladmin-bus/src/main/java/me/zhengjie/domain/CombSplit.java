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
* @date 2021-06-21
**/
@Entity
@Data
@Table(name="bus_comb_split")
public class CombSplit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "comb_id",nullable = false)
    @ApiModelProperty(value = "组合包的id")
    private Long combId;

    @Column(name = "comb_sku_id",nullable = false)
    @ApiModelProperty(value = "组合包sku")
    private String combSkuId;

    @Column(name = "split_sku_id",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "货号")
    private String splitSkuId;

    @Column(name = "split_sku_name")
    @ApiModelProperty(value = "品名")
    private String splitSkuName;

    @Column(name = "qty",nullable = false)
    @NotNull
    @ApiModelProperty(value = "数量")
    private Integer qty;

    @Column(name = "payment")
    @ApiModelProperty(value = "价格")
    private BigDecimal payment;

    public void copy(CombSplit source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
