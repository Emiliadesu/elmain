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
* @date 2022-05-04
**/
@Entity
@Data
@Table(name="bus_order_batch_check_details")
public class OrderBatchCheckDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "wave_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "波次编号")
    private String waveNo;

    @Column(name = "wms_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "WMS单号")
    private String wmsNo;

    @Column(name = "order_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @Column(name = "logistics_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "运单号")
    private String logisticsNo;

    @Column(name = "line_status",nullable = false)
    @NotNull
    @ApiModelProperty(value = "波次单状态")
    private Integer lineStatus;

    @Column(name = "order_status",nullable = false)
    @NotNull
    @ApiModelProperty(value = "状态")
    private Integer orderStatus;

    @Column(name = "seq_no")
    @ApiModelProperty(value = "订单序号")
    private Integer seqNo;

    @Column(name = "check_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "批量质检ID")
    private Long checkId;

    public void copy(OrderBatchCheckDetails source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}