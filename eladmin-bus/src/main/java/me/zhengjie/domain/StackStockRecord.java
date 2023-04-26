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
* @date 2021-07-16
**/
@Entity
@Data
@Table(name="bus_stack_stock_record")
public class StackStockRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "batch_no")
    @ApiModelProperty(value = "wms批次号")
    private String batchNo;

    @Column(name = "sku")
    @ApiModelProperty(value = "海关货号")
    private String sku;

    @Column(name = "first_receive_date")
    @ApiModelProperty(value = "入库时间")
    private String firstReceiveDate;

    @Column(name = "asn_code")
    @ApiModelProperty(value = "入库单号")
    private String asnCode;

    @Column(name = "is_damaged")
    @ApiModelProperty(value = "是否坏品")
    private String isDamaged;

    @Column(name = "customer_batch")
    @ApiModelProperty(value = "客户批次号")
    private String customerBatch;

    @Column(name = "stock_qty")
    @ApiModelProperty(value = "可用库存")
    private Integer stockQty;

    @Column(name = "plate_num")
    @ApiModelProperty(value = "库位托盘数量")
    private Integer plateNum;

    @Column(name = "shop_code")
    @ApiModelProperty(value = "商家店铺code")
    private String shopCode;

    @Column(name = "create_date")
    @ApiModelProperty(value = "快照时间")
    private String createDate;

    public void copy(StackStockRecord source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
