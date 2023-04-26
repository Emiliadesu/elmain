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
* @author 王淼
* @date 2020-10-20
**/
@Entity
@Data
@Table(name="bus_customs_tariff")
public class CustomsTariff implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "hs_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "HS编码")
    private String hsCode;

    @Column(name = "name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "货品名称")
    private String name;

    @Column(name = "normal_tariff")
    @ApiModelProperty(value = "普通税率")
    private BigDecimal normalTariff;

    @Column(name = "discount_tariff")
    @ApiModelProperty(value = "优惠税率")
    private BigDecimal discountTariff;

    @Column(name = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Column(name = "export_tariff")
    @ApiModelProperty(value = "出口税率")
    private BigDecimal exportTariff;

    @Column(name = "consumpt_tariff")
    @ApiModelProperty(value = "消费税率")
    private BigDecimal consumptTariff;

    @Column(name = "value_add_tariff")
    @ApiModelProperty(value = "增值税率")
    private BigDecimal valueAddTariff;

    @Column(name = "first_unit")
    @ApiModelProperty(value = "第一法定单位")
    private String firstUnit;

    @Column(name = "second_unit")
    @ApiModelProperty(value = "第二法定单位")
    private String secondUnit;

    public void copy(CustomsTariff source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}