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
import me.zhengjie.domain.DyStockTakingDetail;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-09-26
**/
@Data
public class DyStockTakingDto implements Serializable {

    private Long id;

    /** 店铺id */
    private Long shopId;

    /** 盘点类型 */
    private Integer takingType;

    /** 盘点完成时间 */
    private Long occurrenceTime;

    /** 备注 */
    private String remark;
    /** 是否完成推送 */
    private String isSuccess;

    /** 创建人 */
    private Long createUserId;

    /**
     * 状态
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
     * 调整类型
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

    private List<DyStockTakingDetailDto> itemList;
}
