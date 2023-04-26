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
import java.sql.Timestamp;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-03-28
**/
@Entity
@Data
@Table(name="bus_do_lot")
public class DoLot implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "area_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "库位")
    private String areaNo;

    @JoinColumn(name = "out_tally_id")
    @ManyToOne(fetch=FetchType.LAZY,targetEntity = StockOutTolly.class)
    @ApiModelProperty(value = "出库理货单", hidden = true)
    private StockOutTolly stockOutTolly;

    @Column(name = "batch_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "wms批次号")
    private String batchNo;

    @Column(name = "consignor_id",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "委托方id")
    private String consignorId;

    @Column(name = "doc_item_id",nullable =false)
    private String docItemId;

    @Column(name = "expire_time",nullable = false)
    @ApiModelProperty(value = "有效期截止")
    private Timestamp expireTime;

    @Column(name = "fund_provider_id",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "资金方id")
    private String fundProviderId;

    @Column(name = "is_damaged",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "是否坏品")
    private String isDamaged;

    @Column(name = "lot_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "原始批次号")
    private String lotNo;

    @Column(name = "merchant_id",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "商家id")
    private String merchantId;

    @Column(name = "po_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "进仓单号")
    private String poCode;

    @Column(name = "product_id",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "产品id")
    private String productId;

    @Column(name = "production_time",nullable = false)
    @ApiModelProperty(value = "生产日期")
    private Timestamp productionTime;

    @Column(name = "shop_id")
    @ApiModelProperty(value = "店铺id")
    private String shopId;

    @Column(name = "transaction_qty",nullable = false)
    @ApiModelProperty(value = "数量")
    private Integer transactionQty;

    @Column(name = "customer_batch_no")
    @ApiModelProperty(value = "客户批次号")
    private String customerBatchNo;

    @Column(name = "warehouse_id",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "仓库id")
    private String warehouseId;

    @Column(name = "warehouse_time",nullable = false)
    @ApiModelProperty(value = "入库时间")
    private Timestamp warehouseTime;

    public void copy(DoLot source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
