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
* @date 2021-07-16
**/
@Data
public class StackStockRecordDto implements Serializable {

    private Long id;

    /**
     * wms批次号
     */
    private String batchNo;

    /**
     * 海关货号
     */
    private String sku;

    /**
     * 入库时间
     */
    private String firstReceiveDate;

    /**
     * 入库单号
     */
    private String asnCode;

    /**
     * 是否坏品
     */
    private String isDamaged;

    /**
     * 客户批次号
     */
    private String customerBatch;

    /**
     * 可用库存
     */
    private Integer stockQty;

    /**
     * 托盘数量
     */
    private Integer plateNum;

    /**
     * 商家code
     */
    private String shopCode;

    /**
     * 快照时间
     */
    private String createDate;
}
