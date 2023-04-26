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
* @author luob
* @date 2022-05-04
**/
@Data
public class OrderBatchCheckDetailsDto implements Serializable {

    /** ID */
    private Long id;

    /** 波次编号 */
    private String waveNo;

    /** WMS单号 */
    private String wmsNo;

    /** 订单号 */
    private String orderNo;

    /** 运单号 */
    private String logisticsNo;

    /** 波次单状态 */
    private Integer lineStatus;

    /** 状态 */
    private Integer orderStatus;

    /** 订单序号 */
    private Integer seqNo;

    /** 批量质检ID */
    private Long checkId;
}