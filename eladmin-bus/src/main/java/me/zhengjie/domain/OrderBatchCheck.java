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
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2022-05-04
**/
@Entity
@Data
@Table(name="bus_order_batch_check")
public class OrderBatchCheck implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "wave_no",unique = true,nullable = false)
    @NotBlank
    @ApiModelProperty(value = "波次编号")
    private String waveNo;

    @Column(name = "wave_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "波次名称")
    private String waveName;

    @Column(name = "wave_status",nullable = false)
    @NotNull
    @ApiModelProperty(value = "波次单状态")
    private Integer waveStatus;

    @Column(name = "status",nullable = false)
    @NotNull
    @ApiModelProperty(value = "状态")
    private Integer status;

    @Column(name = "order_num",nullable = false)
    @NotNull
    @ApiModelProperty(value = "订单数量")
    private Integer orderNum;

    @Column(name = "material_code")
    @ApiModelProperty(value = "包材编码")
    private String materialCode;

    @Column(name = "pack_weight")
    @ApiModelProperty(value = "包裹重量")
    private BigDecimal packWeight;

    @Column(name = "wave_create_time")
    @ApiModelProperty(value = "波次创建时间")
    private Timestamp waveCreateTime;

    @Column(name = "wave_create_by")
    @ApiModelProperty(value = "波次创建人")
    private String waveCreateBy;

    @Column(name = "create_by")
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    public void copy(OrderBatchCheck source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}