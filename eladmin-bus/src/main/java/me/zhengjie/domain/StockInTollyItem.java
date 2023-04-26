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
import java.util.List;

/**
 * @website https://el-admin.vip
 * @description /
 * @author wangm
 * @date 2021-04-21
 **/
@Entity
@Data
@Table(name="bus_stock_in_tolly_item")
public class StockInTollyItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "pre_tally_num",nullable = false)
    @NotNull
    @ApiModelProperty(value = "通知数量")
    private Integer preTallyNum;

    @Column(name = "product_id")
    @ApiModelProperty(value = "商品id")
    private String productId;

    @Column(name = "supplier_id")
    @ApiModelProperty(value = "供应商id")
    private String supplierId;

    @Column(name = "merchant_id")
    @ApiModelProperty(value = "生产商id")
    private String merchantId;

    @Column(name = "po_code")
    @ApiModelProperty(value = "合同号")
    private String poCode;

    @Column(name = "purchase_price")
    @ApiModelProperty(value = "采购价")
    private BigDecimal purchasePrice;

    @Column(name = "lot_no")
    @ApiModelProperty(value = "批号（WMS操作批次号）")
    private String lotNo;

    @Column(name = "fund_provider_id")
    @ApiModelProperty(value = "资金方企业id")
    private String fundProviderId;

    @Column(name = "bl_no1")
    @ApiModelProperty(value = "提单号大")
    private String blNo1;

    @Column(name = "bl_no2")
    @ApiModelProperty(value = "提单号小")
    private String blNo2;

    @Column(name = "po_no")
    @ApiModelProperty(value = "PO单号")
    private String poNo;

    @Column(name = "shop_id")
    @ApiModelProperty(value = "店铺id")
    private String shopId;

    @Column(name = "is_trace_src")
    @ApiModelProperty(value = "是否朔源")
    private String isTraceSrc;

    @Column(name = "reheck_pic_urls")
    @ApiModelProperty(value = "reheckPicUrls")
    private String reheckPicUrls;

    @Transient
    @ApiModelProperty(value = "入库通知单", hidden = true)
    private StockInTolly stockInTolly;

    @Column(name = "in_tally_id")
    @ApiModelProperty(value = "入库理货单id", hidden = true)
    private Long inTallyId;

    @Column(name = "qty_received")
    @ApiModelProperty(value = "理货数量")
    private Integer qtyReceived;

    @Column(name = "need_sn")
    @ApiModelProperty(value = "是否需要回传序列号")
    private Integer needSn;

    @Column(name = "goods_line",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "商品行")
    private String goodsLine;

    /**
     * 差异明细
     */
    @OneToMany(mappedBy = "stockInTollyItem",cascade={CascadeType.PERSIST,CascadeType.REMOVE})
    private List<ReasonDetail> reasonDetails;

    public void copy(StockInTollyItem source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
