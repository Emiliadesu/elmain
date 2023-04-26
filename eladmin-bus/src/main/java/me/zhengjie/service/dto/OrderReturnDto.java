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

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2021-04-14
**/
@Data
public class OrderReturnDto implements Serializable {

    /** ID */
    private Long id;

    /** 客户ID */
    private Long customersId;

    /** 店铺ID */
    private Long shopId;

    /** 退货单号 */
    private String tradeReturnNo;

    /** 是否超时 */
    private String isOverTime;

    /** 状态 */
    private Integer status;

    /** 物流订单号 */
    private String logisticsNo;

    /** 原订单号 */
    private String orderNo;

    private String declareNo;

    /** 总税额 */
    private String taxAmount;

    /** 是否入区 */
    private String isBorder;

    /** 售后类型 */
    private String afterSalesType;

    /** 售后单号 */
    private String afterSalesNo;

    /** 订单清关时间 */
    private Timestamp salesCustomsTime;

    /** 正向物流单号 */
    private String sExpressNo;

    /** 正向物流公司 */
    private String sExpressName;

    /** 逆向物流单号 */
    private String rExpressNo;

    /** 逆向物流公司 */
    private String rExpressName;

    /** 退货类型 */
    private String returnType;

    /** 质检结果 */
    private String checkResult;

    /** 收货时间 */
    private Timestamp takeTime;

    /** 收货回传时间 */
    private Timestamp takeBackTime;

    /** 质检完成时间 */
    private Timestamp checkTime;

    /** 质检完成回传时间 */
    private Timestamp checkBackTime;

    /** 申报开始时间 */
    private Timestamp declareStartTime;

    /** 申报开始回传时间 */
    private Timestamp declareStartBackTime;

    /** 申报完成时间 */
    private Timestamp declareEndTime;

    /** 申报完成回传时间 */
    private Timestamp declareEndBackTime;

    /** 保税仓上架时间 */
    private Timestamp bondedGroundTime;

    /** 保税仓上架回传时间 */
    private Timestamp bondedGroundBackTime;

    /** 理货完成时间 */
    private Timestamp tallyTime;

    /** 理货完成回传时间 */
    private Timestamp tallyBackTime;

    /** 退货仓上架时间 */
    private Timestamp returnGroundTime;

    /** 退货仓上架回传时间 */
    private Timestamp returnGroundBackTime;

    /** 取消时间 */
    private Timestamp cancelTime;

    /** 取消回传时间 */
    private Timestamp cancelBackTime;

    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    private Timestamp createTime;

    /** 创建人 */
    private String updateBy;

    /** 创建时间 */
    private Timestamp updateTime;

    private String declareStatus;

    private String declareMsg;

    private Timestamp salesDeliverTime;

    private String invtNo;

    private String remark;

    private String platformCode;

    private String orderSource;

    private String wmsNo;

    private String isWave;

    private Timestamp closeTime;

    private String gatherNo;

    private String decFlag;

    private String orderServiceStatus;

    private String checkType;

    private String fourPl;

    private String logisticsFulfilNo;
}