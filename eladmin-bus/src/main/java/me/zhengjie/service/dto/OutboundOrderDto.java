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
* @date 2021-07-13
**/
@Data
public class OutboundOrderDto implements Serializable {

    /** ID */
    private Long id;

    /** 客户ID */
    private Long customersId;

    /** 店铺ID */
    private Long shopId;

    /** 入库单号 */
    private String orderNo;

    /** 外部单号 */
    private String outNo;

    /** WMS单号 */
    private String wmsNo;

    /** 单据类型 */
    private String orderType;

    /** 状态 */
    private Integer status;

    /** 原单号 */
    private String originalNo;

    /** 预期发货时间 */
    private Timestamp expectDeliverTime;

    /** 理货维度 */
    private String tallyWay;

    /** 托数 */
    private Integer palletNum;

    /** 箱数 */
    private Integer boxNum;

    /** 预期SKU数 */
    private Integer expectSkuNum;

    /** 出库SKU数 */
    private Integer deliverSkuNum;

    /** 预期总件数 */
    private Integer expectTotalNum;

    /** 出库总件数 */
    private Integer deliverTotalNum;

    /** 出库正品件数 */
    private Integer deliverNormalNum;

    /** 出库残品件数 */
    private Integer deliverDamagedNum;

    /** 接单确认人 */
    private String confirmBy;

    /** 接单确认时间 */
    private Timestamp confirmTime;

    /** 理货人 */
    private String tallyBy;

    /** 理货开始时间 */
    private Timestamp tallyStartTime;

    /** 理货开始回传时间 */
    private Timestamp tallyStartBackTime;

    /** 理货结束时间 */
    private Timestamp tallyEndTime;

    /** 理货结束回传时间 */
    private Timestamp tallyEndBackTime;

    /** 收货人 */
    private String deliverBy;

    /** 收货完成时间 */
    private Timestamp deliverTime;

    /** 收货完成回传时间 */
    private Timestamp deliverBackTime;

    /** 取消时间 */
    private Timestamp cancelTime;

    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    private Timestamp createTime;

    private String isOnline;
    
    private String onlineSrc;

    private String freezeReason;

    private String platformCode;

    private String isFourPl;
}
