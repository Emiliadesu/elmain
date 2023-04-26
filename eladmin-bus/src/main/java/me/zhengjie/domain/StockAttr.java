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
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-06-08
**/
@Entity
@Data
@Table(name="bus_stock_attr")
public class StockAttr implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "wms_batch_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "仓库批次号")
    private String wmsBatchNo;

    @Column(name = "in_order_sn",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "入库单号")
    private String inOrderSn;

    @Column(name = "sub_type",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "细分类型")
    private String subType;

    @Column(name = "book_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "账册号")
    private String bookNo;

    @Column(name = "supervise_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "供应商编码")
    private String superviseCode;

    @Column(name = "customer_batch_no")
    @ApiModelProperty(value = "客户批次号")
    private String customerBatchNo;

    @Column(name = "update_time",nullable = false)
    @NotNull
    @ApiModelProperty(value = "最近修改时间")
    private Timestamp updateTime;

    public void copy(StockAttr source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
