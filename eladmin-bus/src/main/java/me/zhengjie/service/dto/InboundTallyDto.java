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
* @author luob
* @date 2021-06-16
**/
@Data
public class InboundTallyDto implements Serializable {

    /** ID */
    private Long id;

    /** 入库单ID */
    private Long orderId;

    /** 入库单号 */
    private String orderNo;

    /** 理货单号 */
    private String tallyNo;

    /** 理货状态 */
    private Integer status;

    private Integer tallyCount;

    /** 托数 */
    private Integer palletNum;

    /** 箱数 */
    private Integer boxNum;

    /** 预期SKU数 */
    private Integer expectSkuNum;

    /** 理货SKU数 */
    private Integer tallySkuNum;

    /** 预期总件数 */
    private Integer expectTotalNum;

    /** 理货总件数 */
    private Integer tallyTotalNum;

    /** 理货正品件数 */
    private Integer tallyNormalNum;

    /** 理货残品件数 */
    private Integer tallyDamagedNum;

    /** 理货开始时间 */
    private Timestamp tallyStartTime;

    /** 理货结束时间 */
    private Timestamp tallyEndTime;

    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    private Timestamp createTime;
}