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
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-04-15
**/
@Data
public class StockAttrNoticeDetailDto implements Serializable {

    private Long id;

    /** 属性调整通知id */
    private StockAttrNoticeDto stockAttrNotice;

    /** 交易序列号 */
    private String seq;

    /** 单据id */
    private String docId;

    /** 单据编号 */
    private String docCode;

    /** 产品id */
    private String productId;

    /** 商家id */
    private String merchantId;

    /** 交易类型 */
    private String transactionType;

    /** 单据类型 */
    private String docType;

    /** 交易数量 */
    private Integer transactionQty;

    /** 生产日期 */
    private Timestamp productionTime;

    /** 失效日期 */
    private Timestamp expireTime;

    private String blNo2;

    private BigDecimal purchasePrice;

    /** 目标仓库id */
    private String toWarehouseId;

    /** 是否坏品 */
    private String isDamaged;

    /** 坏品类型 */
    private String damagedType;

    /** 委托方id */
    private String consignorId;

    /** 客户批次号 */
    private String customerBatchNo;

    /** 首次入库时间 */
    private Timestamp sourceAsnInWarehouseTime;

    private String wmsBatchCode;

    private String lotNo;
}
