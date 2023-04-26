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
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-06-08
**/
@Data
public class StockAttrDto implements Serializable {

    private Long id;

    /** 仓库批次号 */
    private String wmsBatchNo;

    /** 入库单号 */
    private String inOrderSn;

    /** 细分类型 */
    private String subType;

    /** 账册号 */
    private String bookNo;

    /** 供应商编码 */
    private String superviseCode;

    /** 客户批次号 */
    private String customerBatchNo;

    /** 最近修改时间 */
    private Timestamp updateTime;
}