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
* @date 2021-04-18
**/
@Data
public class BusStockConvertDetailDto implements Serializable {

    private Long id;

    /** 转移主单id */
    private Long mainId;

    /** 产品id */
    private String productId;

    /** wms批次号 */
    private String batchNo;

    /** 源商家Id(OP系统) */
    private String fmMerchantId;

    /** 目标商家Id(OP系统) */
    private String toMerchantId;

    /** 转移数量 */
    private Integer convertQty;

    /** 目标数量 */
    private Integer toQty;

    /** 目标好坏品类型 */
    private Integer toIsDamaged;

    /** 源虚拟货主Id(OP系统) */
    private String fmVirtualMerchantId;

    /** 目标虚拟货主Id(OP系统) */
    private String toVirtualMerchantId;

    /** 源客户批次号 */
    private String fmCustomerBatchNo;

    /** 目标客户批次号 */
    private String toCustomerBatchNo;
}