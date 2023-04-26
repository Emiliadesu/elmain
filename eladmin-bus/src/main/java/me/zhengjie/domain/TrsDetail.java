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
import java.util.Objects;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-03-23
**/
@Entity
@Data
@Table(name="bus_trs_detail")
public class TrsDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @JoinColumn(name = "asn_detail_id")
    @ManyToOne(fetch=FetchType.LAZY,targetEntity = StockInTolly.class)
    @ApiModelProperty(value = "入库理货单", hidden = true)
    private StockInTolly stockInTolly;

    @Column(name = "doc_line_id",nullable = false)
    @ApiModelProperty(value = "通知单详情的商品行id")
    private String docLineId;

    @Column(name = "transaction_qty",nullable = false)
    @NotNull
    @ApiModelProperty(value = "交易数量")
    private Integer transactionQty;

    @Column(name = "transaction_type")
    @ApiModelProperty(value = "交易类型")
    private Integer transactionType;

    @Column(name = "product_id",nullable = false)
    @ApiModelProperty(value = "商品编码")
    private String productId;

    @Column(name = "sku_no",nullable = false)
    /***
     * 商品货号
     */
    private String skuNo;

    /***
     * 仓库货号
     */
    @Column(name = "goods_no",nullable = false)
    private String goodsNo;

    /***
     * 条码
     */
    @Column(name = "bar_code",nullable = false)
    private String barCode;

    @Column(name = "length",nullable = false)
    @ApiModelProperty(value = "产品长度")
    private String length;

    @Column(name = "width",nullable = false)
    @ApiModelProperty(value = "产品宽度")
    private String width;

    @Column(name = "height",nullable = false)
    @ApiModelProperty(value = "产品高度")
    private String height;

    @Column(name = "net_weight")
    @ApiModelProperty(value = "产品净重")
    private String netWeight;

    @Column(name = "gross_weight",nullable = false)
    @ApiModelProperty(value = "产品毛重")
    private String grossWeight;

    @Column(name = "volume",nullable = false)
    @ApiModelProperty(value = "产品体积")
    private String volume;

    @Column(name = "supplier_id")
    @ApiModelProperty(value = "供应商")
    private String supplierId;

    @Column(name = "merchant_id",nullable = false)
    @ApiModelProperty(value = "商家id")
    private String merchantId;

    @Column(name = "lot_no",nullable = false)
    @ApiModelProperty(value = "源批次号")
    private String lotNo;

    @Column(name = "manufacture_id")
    @ApiModelProperty(value = "制造商id")
    private String manufactureId;

    @Column(name = "po_no")
    @ApiModelProperty(value = "合同号")
    private String poNo;

    @Column(name = "purchase_price")
    @ApiModelProperty(value = "采购进价")
    private String purchasePrice;

    @Column(name = "production_time",nullable = false)
    @ApiModelProperty(value = "生产日期")
    private String productionTime;

    @Column(name = "warehouse_time")
    @ApiModelProperty(value = "入库时间")
    private String warehouseTime;

    @Column(name = "expired_time",nullable = false)
    @ApiModelProperty(value = "失效日期")
    private String expiredTime;

    @Column(name = "account_book_id")
    @ApiModelProperty(value = "账册编号")
    private String accountBookId;

    @Column(name = "fund_provider_id",nullable = false)
    @ApiModelProperty(value = "货主企业编码")
    private String fundProviderId;

    @Column(name = "bl_no1")
    @ApiModelProperty(value = "提单号大")
    private String blNo1;

    @Column(name = "bl_no2")
    @ApiModelProperty(value = "提单号小")
    private String blNo2;

    @Column(name = "shop_id")
    @ApiModelProperty(value = "店铺id")
    private String shopId;

    @Column(name = "is_trace_src")
    @ApiModelProperty(value = "是否溯源")
    private String isTraceSrc;

    @Column(name = "consignor_id",nullable = false)
    @ApiModelProperty(value = "货主企业编码")
    private String consignorId;

    @Column(name = "batch_no")
    @ApiModelProperty(value = "wms批次号")
    private String batchNo;

    @Column(name = "is_damaged",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "是否坏品")
    private String isDamaged;

    @Column(name = "customer_batch_no")
    @ApiModelProperty(value = "客户批次号")
    private String customerBatchNo;

    @Column(name = "virtual_merchant_id",nullable = false)
    @ApiModelProperty(value = "虚拟货主")
    private String virtualMerchantId;

    @Column(name = "sub_type")
    @ApiModelProperty(value = "细分类型")
    private String subType;

    @Column(name = "uom")
    @ApiModelProperty(value = "单位")
    private String uom;

    @Column(name = "lpn")
    @ApiModelProperty(value = "收货托盘号")
    private String lpn;

    @Column(name = "stock_business_type",nullable = false)
    @ApiModelProperty(value = "业务类型")
    private String stockBusinessType;

    @Column(name = "inb_uom")
    @ApiModelProperty(value = "收货单位")
    private String inbUom;

    @Column(name = "inb_uom_qty")
    @ApiModelProperty(value = "收货单位数量")
    private Integer inbUomQty;

    @Column(name = "lot26")
    @ApiModelProperty(value = "监管代码")
    private String lot26;

    @Column(name = "lot27")
    @ApiModelProperty(value = "账册")
    private String lot27;

    @Column(name = "lot28")
    @ApiModelProperty(value = "生产批次号")
    private String lot28;

    @Column(name = "lot29")
    @ApiModelProperty(value = "lot29")
    private String lot29;

    @Column(name = "lot30")
    @ApiModelProperty(value = "lot30")
    private String lot30;

    @Column(name = "lot31")
    @ApiModelProperty(value = "lot31")
    private String lot31;

    @Column(name = "lot32")
    @ApiModelProperty(value = "lot32")
    private String lot32;

    @Column(name = "lot33")
    @ApiModelProperty(value = "lot33")
    private String lot33;

    @Column(name = "lot34")
    @ApiModelProperty(value = "lot34")
    private String lot34;

    @Column(name = "lot35")
    @ApiModelProperty(value = "lot35")
    private String lot35;

    public void copy(TrsDetail source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrsDetail trsDetail = (TrsDetail) o;
        return productId.equals(trsDetail.productId) &&
                productionTime.equals(trsDetail.productionTime) &&
                expiredTime.equals(trsDetail.expiredTime) &&
                isDamaged.equals(trsDetail.isDamaged) &&
                lpn.equals(trsDetail.lpn) &&
                customerBatchNo.equals(trsDetail.customerBatchNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, productionTime, expiredTime, isDamaged, lpn);
    }
}
