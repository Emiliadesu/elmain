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
* @author wangm
* @date 2021-04-15
**/
@Entity
@Data
@Table(name="bus_detail_type")
public class DetailType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "tenant_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "租户编号")
    private String tenantCode;

    @Column(name = "warehouse_id",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "仓库id")
    private String warehouseId;

    @Column(name = "merchant_id",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "商家id")
    private String merchantId;

    @Column(name = "product_id")
    @ApiModelProperty(value = "产品id")
    private String productId;

    @Column(name = "lot_no")
    @ApiModelProperty(value = "原始批次号")
    private String lotNo;

    @Column(name = "customer_batch_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "客户批次号")
    private String customerBatchNo;

    @Column(name = "fm_sub_type",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "变更前细分类型")
    private String fmSubType;

    @Column(name = "to_sub_type",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "目标细分类型")
    private String toSubType;

    @Column(name = "create_time")
    @ApiModelProperty(value = "通知时间")
    private String createTime;

    @Column(name = "is_complete")
    @ApiModelProperty(value = "是否完成")
    private String isComplete;

    @Column(name = "complete_opter")
    @ApiModelProperty(value = "是否完成")
    private String completeOpter;

    public void copy(DetailType source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
