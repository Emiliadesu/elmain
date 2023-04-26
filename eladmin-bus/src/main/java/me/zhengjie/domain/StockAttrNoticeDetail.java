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
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-04-15
**/
@Entity
@Data
@Table(name="bus_stock_attr_notice_detail")
public class StockAttrNoticeDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @JoinColumn(name = "noticeId")
    @ManyToOne(fetch=FetchType.LAZY,targetEntity = StockAttrNotice.class)
    @ApiModelProperty(value = "库存属性调整通知单", hidden = true)
    private StockAttrNotice stockAttrNotice;

    @Column(name = "seq",nullable = false)
    @ApiModelProperty(value = "交易序列号")
    private String seq;

    @Column(name = "doc_id")
    @ApiModelProperty(value = "单据id")
    private String docId;

    @Column(name = "doc_code")
    @ApiModelProperty(value = "单据编号")
    private String docCode;

    @Column(name = "product_id")
    @ApiModelProperty(value = "产品id")
    private String productId;

    @Column(name = "merchant_id",nullable = false)
    @ApiModelProperty(value = "商家id")
    private String merchantId;

    @Column(name = "lot_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "wms原批次号")
    private String lotNo;

    @Column(name = "wms_batch_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "wms变更后的批次号")
    private String wmsBatchCode;

    @Column(name = "transaction_type",nullable = false)
    @ApiModelProperty(value = "交易类型")
    private String transactionType;

    @Column(name = "doc_type",nullable = false)
    @ApiModelProperty(value = "单据类型")
    private String docType;

    @Column(name = "transaction_qty",nullable = false)
    @NotNull
    @ApiModelProperty(value = "交易数量")
    private Integer transactionQty;

    @Column(name = "production_time",nullable = false)
    @ApiModelProperty(value = "生产日期")
    private Timestamp productionTime;

    @Column(name = "expire_time",nullable = false)
    @ApiModelProperty(value = "失效日期")
    private Timestamp expireTime;

    @Column(name = "to_warehouse_id",nullable = false)
    @ApiModelProperty(value = "目标仓库id")
    private String toWarehouseId;

    @Column(name = "is_damaged")
    @ApiModelProperty(value = "是否坏品")
    private String isDamaged;

    @Column(name = "damaged_type")
    @ApiModelProperty(value = "坏品类型")
    private String damagedType;

    @Column(name = "consignor_id")
    @ApiModelProperty(value = "委托方id")
    private String consignorId;

    @Column(name = "bl_no2")
    @ApiModelProperty(value = "提单号 小")
    private String blNo2;

    @Column(name = "purchase_price")
    @ApiModelProperty(value = "采购单价")
    private BigDecimal purchasePrice;

    @Column(name = "customer_batch_no")
    @ApiModelProperty(value = "客户批次号")
    private String customerBatchNo;

    @Column(name = "source_asn_in_warehouse_time",nullable = false)
    @ApiModelProperty(value = "首次入库时间")
    private Timestamp sourceAsnInWarehouseTime;

    @Transient
    private String bookNo;

    @Transient
    private String goodsNo;

    @Transient
    private String poNo;

    @Transient
    private Integer preProject;

    public void copy(StockAttrNoticeDetail source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
