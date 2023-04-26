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
import java.sql.Timestamp;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-03-28
**/
@Data
public class DoLotDto implements Serializable {

    private Long id;

    private StockOutTollySmallDto stockOutTolly;

    /** 库位 */
    private String areaNo;

    /** wms批次号 */
    private String batchNo;

    /** 委托方id */
    private String consignorId;

    /** 订单明细id */
    private String docItemId;

    /** 有效期截止 */
    private Timestamp expireTime;

    /** 资金方id */
    private String fundProviderId;

    /** 是否坏品 */
    private String isDamaged;

    /** 原始批次号 */
    private String lotNo;

    /** 商家id */
    private String merchantId;

    /** 进仓单号 */
    private String poCode;

    /** 产品id */
    private String productId;

    /** 生产日期 */
    private Timestamp productionTime;

    /** 店铺id */
    private String shopId;

    /** 数量 */
    private Integer transactionQty;

    /** 仓库id */
    private String warehouseId;

    /** 入库时间 */
    private Timestamp warehouseTime;
}
