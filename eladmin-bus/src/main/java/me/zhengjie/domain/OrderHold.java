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
* @date 2021-12-30
**/
@Entity
@Data
@Table(name="bus_order_hold")
public class OrderHold extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "名称")
    private String name;

    @Column(name = "status",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "状态")
    private String status;

    @Column(name = "type",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "类型")
    private String type;

    @Column(name = "customers_id")
    @ApiModelProperty(value = "客户ID")
    private Long customersId;

    @Column(name = "shop_id")
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;

    @Column(name = "platform_code")
    @ApiModelProperty(value = "电商平台代码")
    private String platformCode;

    @Column(name = "start_time",nullable = false)
    @NotNull
    @ApiModelProperty(value = "开始时间")
    private Timestamp startTime;

    @Column(name = "end_time",nullable = false)
    @NotNull
    @ApiModelProperty(value = "结束时间")
    private Timestamp endTime;

    public void copy(OrderHold source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}