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
import javax.persistence.Entity;
import javax.persistence.Table;

import me.zhengjie.base.BaseEntity;
import org.hibernate.annotations.*;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author AddValueBinding
* @date 2021-08-05
**/
@Entity
@Data
@Table(name="bus_add_value_binding")
public class AddValueBinding extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "customer_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "客户")
    private Long customerId;

    @Column(name = "shop_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "店铺")
    private Long shopId;

    @Column(name = "value_id",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "增项值ID")
    private String valueId;

    @Column(name = "value_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "增值项编码")
    private String valueCode;

    @Column(name = "price",nullable = false)
    @NotNull
    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    public void copy(AddValueBinding source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}