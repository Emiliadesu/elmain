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
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author 王淼
* @date 2021-09-26
**/
@Data
public class DyStockTransformDto implements Serializable {

    private Long id;

    /** 库存变动类型 1：良转次 2：次转良 */
    private Integer transformType;

    /** 店铺id */
    private Long shopId;

    /** 操作时间 */
    private Long occurrenceTime;

    /** 备注 */
    private String remark;

    /** 创建人 */
    private Long createUserId;

    private String isSuccess;

    /**
     * 审核状态
     */
    private String status;
    /**
     * 驳回原因
     */
    private String rejectReason;
    /**
     * 入库单号
     */
    private String inboundOrderNo;

    /**
     *调整类型
     */
    private String adjustBizType;
    /**
     * 幂等单号
     */
    private String idempotentNo;

    /**
     * 审核完成时间
     */
    private Timestamp sucTime;

    private List<DyStockTransformDetailDto> itemList;
}
