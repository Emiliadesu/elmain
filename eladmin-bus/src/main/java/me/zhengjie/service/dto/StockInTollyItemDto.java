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
import java.math.BigDecimal;
import java.io.Serializable;
import java.util.List;

/**
 * @website https://el-admin.vip
 * @description /
 * @author wangm
 * @date 2021-04-21
 **/
@Data
public class StockInTollyItemDto implements Serializable {

    private Long id;

    /** 通知数量 */
    private Integer preTallyNum;

    /** 商品id */
    private String productId;

    /** 供应商id */
    private String supplierId;

    /** 生产商id */
    private String merchantId;

    /** 合同号 */
    private String poCode;

    /** 采购价 */
    private BigDecimal purchasePrice;

    /** 批号（WMS操作批次号） */
    private String lotNo;

    /** 资金方企业id */
    private String fundProviderId;

    /** 提单号大 */
    private String blNo1;

    /** 提单号小 */
    private String blNo2;

    /** PO单号 */
    private String poNo;

    /** 店铺id */
    private String shopId;

    /** 是否朔源 */
    private String isTraceSrc;

    private String reheckPicUrls;

    /** 理货单号 */
    private StockInTollySmallDto stockInTolly;

    /** 理货数量 */
    private Integer qtyReceived;

    /** 是否需要回传序列号 */
    private Integer needSn;

    /** 商品行 */
    private String goodsLine;

    /** 差异明细*/
    private List<ReasonDetailDto> reasonDetails;
}
