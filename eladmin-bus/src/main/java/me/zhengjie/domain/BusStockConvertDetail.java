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
* @date 2021-04-18
**/
@Entity
@Data
@Table(name="bus_stock_convert_detail")
public class BusStockConvertDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "main_id")
    @ApiModelProperty(value = "转移主单id")
    private Long mainId;

    @Column(name = "product_id",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "产品id")
    private String productId;

    @Column(name = "batch_no")
    @ApiModelProperty(value = "wms批次号")
    private String batchNo;

    @Column(name = "fm_merchant_id",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "源商家Id(OP系统)")
    private String fmMerchantId;

    @Column(name = "to_merchant_id",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "目标商家Id(OP系统)")
    private String toMerchantId;

    @Column(name = "convert_qty",nullable = false)
    @NotNull
    @ApiModelProperty(value = "转移数量")
    private Integer convertQty;

    @Column(name = "to_qty")
    @ApiModelProperty(value = "目标数量")
    private Integer toQty;

    @Column(name = "to_is_damaged")
    @ApiModelProperty(value = "目标好坏品类型")
    private Integer toIsDamaged;

    @Column(name = "fm_virtual_merchant_id",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "源虚拟货主Id(OP系统)")
    private String fmVirtualMerchantId;

    @Column(name = "to_virtual_merchant_id",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "目标虚拟货主Id(OP系统)")
    private String toVirtualMerchantId;

    @Column(name = "fm_customer_batch_no")
    @ApiModelProperty(value = "源客户批次号")
    private String fmCustomerBatchNo;

    @Column(name = "to_customer_batch_no")
    @ApiModelProperty(value = "目标客户批次号")
    private String toCustomerBatchNo;

    public void copy(BusStockConvertDetail source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}