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
import java.math.BigDecimal;
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
@Table(name="bus_asn_detail")
public class AsnDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @JoinColumn(name = "asn_header_id")
    @ManyToOne(fetch=FetchType.LAZY,targetEntity = AsnHeader.class)
    @ApiModelProperty(value = "托盘头", hidden = true)
    private AsnHeader asnHeader;

    @Column(name = "product_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "产品id")
    private String productCode;

    @Column(name = "doc_item_id",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "原始订单明细id")
    private String docItemId;

    @Column(name = "qty")
    @NotNull
    @ApiModelProperty(value = "主单位数量")
    private Integer qty;

    @Column(name = "case_qty",nullable = false)
    @NotNull
    @ApiModelProperty(value = "箱数")
    private Integer caseQty;

    @Column(name = "lpn",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "托盘号")
    private String lpn;

    @Column(name = "total_sku_weight",nullable = false)
    @NotNull
    @ApiModelProperty(value = "商品总重")
    private BigDecimal totalSkuWeight;

    @Column(name = "lpn_weight")
    @ApiModelProperty(value = "托盘自重")
    private BigDecimal lpnWeight;

    @Column(name = "lpn_length",nullable = false)
    @NotNull
    @ApiModelProperty(value = "托盘长度")
    private BigDecimal lpnLength;

    @Column(name = "lpn_width",nullable = false)
    @NotNull
    @ApiModelProperty(value = "托盘宽度")
    private BigDecimal lpnWidth;

    @Column(name = "lpn_height",nullable = false)
    @NotNull
    @ApiModelProperty(value = "托盘高度")
    private BigDecimal lpnHeight;

    @Column(name = "lot_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "wms批次号")
    private String lotNo;

    @Column(name = "customer_batch_no")
    @ApiModelProperty(value = "客户批次号")
    private String customerBatchNo;

    @Column(name = "production_time",nullable = false)
    @ApiModelProperty(value = "生产日期")
    private Timestamp productionTime;

    @Column(name = "expire_time",nullable = false)
    @ApiModelProperty(value = "失效日期")
    private Timestamp expireTime;

    @Column(name = "warehouse_time",nullable = false)
    @ApiModelProperty(value = "入仓时间")
    private Timestamp warehouseTime;

    @Column(name = "po_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "入仓单号")
    private String poNo;

    private String materials;

    public void copy(AsnDetail source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
