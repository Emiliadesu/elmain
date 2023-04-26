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
package me.zhengjie.service.dto;

import lombok.Data;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-03-23
**/
@Data
public class TrsDetailDto implements Serializable {

    private Long id;

    /** 理货详情id */
    private StockInTollySmallDto stockInTolly;

    /** 理货详情的id */
    private String docLineId;

    /** 交易数量 */
    private Integer transactionQty;

    /** 交易类型 */
    private Integer transactionType;

    /** 商品编码 */
    private String productId;

    /** 商品货号 */
    private String skuNo;

    /** 仓库货号 */
    private String goodsNo;

    /** 条码 */
    private String barCode;

    /** 产品长度 */
    private String length;

    /** 产品宽度 */
    private String width;

    /** 产品高度 */
    private String height;

    /** 产品净重 */
    private String netWeight;

    /** 产品毛重 */
    private String grossWeight;

    /** 产品体积 */
    private String volume;

    /** 供应商 */
    private String supplierId;

    /** 商家id */
    private String merchantId;

    /** 源批次号 */
    private String lotNo;

    /** 制造商id */
    private String manufactureId;

    /** 合同号 */
    private String poNo;

    /** 采购进价 */
    private String purchasePrice;

    /** 生产日期 */
    private String productionTime;

    /** 入库时间 */
    private String warehouseTime;

    /** 失效日期 */
    private String expiredTime;

    /** 账册编号 */
    private String accountBookId;

    /** 货主企业编码 */
    private String fundProviderId;

    /** 提单号大 */
    private String blNo1;

    /** 提单号小 */
    private String blNo2;

    /** 店铺id */
    private String shopId;

    /** 是否溯源 */
    private String isTraceSrc;

    /** 货主企业编码 */
    private String consignorId;

    /** wms批次号 */
    private String batchNo;

    /** 是否坏品 */
    private String isDamaged;

    /** 客户批次号 */
    private String customerBatchNo;

    /** 虚拟货主 */
    private String virtualMerchantId;

    /** 细分类型 */
    private String subType;

    /** 单位 */
    private String uom;

    /** 收货托盘号 */
    private String lpn;

    /** 业务类型 */
    private String stockBusinessType;

    /** 收货单位 */
    private String inbUom;

    /** 收货单位数量 */
    private Integer inbUomQty;

    /** 监管代码 */
    private String lot26;

    /** 账册 */
    private String lot27;

    /** 生产批次号 */
    private String lot28;

    private String lot29;

    private String lot30;

    private String lot31;

    private String lot32;

    private String lot33;

    private String lot34;

    private String lot35;
}
