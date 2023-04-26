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
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2022-05-04
**/
@Data
public class OrderBatchCheckDto implements Serializable {

    /** ID */
    private Long id;

    /** 波次编号 */
    private String waveNo;

    /** 波次名称 */
    private String waveName;

    /** 波次单状态 */
    private Integer waveStatus;

    /** 状态 */
    private Integer status;

    /** 订单数量 */
    private Integer orderNum;

    /** 包材编码 */
    private String materialCode;

    /** 包裹重量 */
    private BigDecimal packWeight;

    /** 波次创建时间 */
    private Timestamp waveCreateTime;

    /** 波次创建人 */
    private String waveCreateBy;

    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    private Timestamp createTime;
}