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
import java.sql.Timestamp;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-03-28
**/
@Data
public class AsnDetailDto implements Serializable {

    private Long id;

    /** 产品id */
    private String productCode;

    private AsnHeaderSmallDto asnHeader;

    /** 原始订单明细id */
    private String docItemId;

    /** 主单位数量 */
    private Integer qty;

    /** 箱数 */
    private Integer caseQty;

    /** 托盘号 */
    private String lpn;

    /** 商品总重 */
    private BigDecimal totalSkuWeight;

    /** 托盘自重 */
    private BigDecimal lpnWeight;

    /** 托盘长度 */
    private BigDecimal lpnLength;

    /** 托盘宽度 */
    private BigDecimal lpnWidth;

    /** 托盘高度 */
    private BigDecimal lpnHeight;

    /** wms批次号 */
    private String lotNo;

    /** 客户批次号 */
    private String customerBatchNo;

    /** 生产日期 */
    private Timestamp productionTime;

    /** 失效日期 */
    private Timestamp expireTime;

    /** 入仓时间 */
    private Timestamp warehouseTime;

    /** 入仓单号 */
    private String poNo;

    private String materials;
}
