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
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2021-06-16
**/
@Entity
@Data
@Table(name="bus_inbound_tally")
public class InboundTally implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "order_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "入库单ID")
    private Long orderId;

    @Column(name = "order_no",unique = true,nullable = false)
    @NotBlank
    @ApiModelProperty(value = "入库单号")
    private String orderNo;

    @Column(name = "tally_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "理货单号")
    private String tallyNo;

    @Column(name = "status",nullable = false)
    @NotNull
    @ApiModelProperty(value = "理货状态")
    private Integer status;

    @Column(name = "tally_count")
    @ApiModelProperty(value = "托数")
    private Integer tally_count;

    @Column(name = "pallet_num")
    @ApiModelProperty(value = "托数")
    private Integer palletNum;

    @Column(name = "box_num")
    @ApiModelProperty(value = "箱数")
    private Integer boxNum;

    @Column(name = "expect_sku_num")
    @ApiModelProperty(value = "预期SKU数")
    private Integer expectSkuNum;

    @Column(name = "tally_sku_num")
    @ApiModelProperty(value = "理货SKU数")
    private Integer tallySkuNum;

    @Column(name = "expect_total_num")
    @ApiModelProperty(value = "预期总件数")
    private Integer expectTotalNum;

    @Column(name = "tally_total_num")
    @ApiModelProperty(value = "理货总件数")
    private Integer tallyTotalNum;

    @Column(name = "tally_normal_num")
    @ApiModelProperty(value = "理货正品件数")
    private Integer tallyNormalNum;

    @Column(name = "tally_damaged_num")
    @ApiModelProperty(value = "理货残品件数")
    private Integer tallyDamagedNum;

    @Column(name = "tally_start_time")
    @ApiModelProperty(value = "理货开始时间")
    private Timestamp tallyStartTime;

    @Column(name = "tally_end_time")
    @ApiModelProperty(value = "理货结束时间")
    private Timestamp tallyEndTime;

    @Column(name = "create_by")
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    public void copy(InboundTally source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}