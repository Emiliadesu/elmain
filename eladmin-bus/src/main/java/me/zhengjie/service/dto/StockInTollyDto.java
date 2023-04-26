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
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-03-23
**/
@Data
public class StockInTollyDto implements Serializable {

    private Long id;

    /** 入库单号 */
    private WmsInstockDto wmsInstock;

    /** 理货单号 */
    private String tallyOrderSn;

    /** 理货品种数 */
    private Integer skuNum;

    /** 理货总件数 */
    private Integer totalNum;

    /** 当前理货次数 */
    private Integer currentNum;

    /** 理货开始时间 */
    private String startTime;

    /** 理货完成时间 */
    private String endTime;

    /** 理货单状态 */
    private String status;

    private String fileName;

    private String reason;

    /** 租户编码 */
    private String tenantCode;

    /** 仓库编码 */
    private String warehouseId;

    /** ASN单状态 */
    private String asnStatus;

    /** ASN单号 */
    private String asnNo;

    /** 审核时间 */
    private String shpdDate;

    /** 验收时间 */
    private String recheckTime;

    /** 收货时间 */
    private String finishReceiptTime;

    /** 审核人 */
    private String verifyBy;

    /** 验收人 */
    private String recheckBy;

    /** 收货人 */
    private String receiveBy;

    /** 上架时间 */
    private String putawayTime;

    /** 上架人 */
    private String putawayBy;

    /** ASN单号类型 */
    private String asnType;

    private Integer grfId;

    /** 验收单号 */
    private String recheckNo;

    private String sourceSys;

    /** 托盘数 */
    private Integer lpnQty;

    /** 到货时间 */
    private String arriveTime;

    /** 理货详情列表*/
    private List<StockInTollyItemDto>items;
    /** 超收列表*/
    private List<OutofPlanDetailDto> outofPlanDetails;
}
