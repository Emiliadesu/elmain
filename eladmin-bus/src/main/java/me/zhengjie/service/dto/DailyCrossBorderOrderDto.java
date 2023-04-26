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
* @author leningzhou
* @date 2022-07-04
**/
@Data
public class DailyCrossBorderOrderDto implements Serializable {

    /** ID */
    private Long id;

    /** 订单号 */
    private String orderNo;

    /** 交易号 */
    private String crossBorderNo;

    /** 状态 */
    private String status;

    /** 客户ID */
    private Long customersId;

    /** 店铺ID */
    private String shopId;

    /** 是否预售 */
    private String preSell;

    /** 申报单号 */
    private String declareNo;

    /** 总署清单编号 */
    private String invtNo;

    /** 波次号 */
    private String waveNo;

    /** SO单号 */
    private String soNo;

    /** 申报状态 */
    private String declareStatus;

    /** 申报信息 */
    private String declareMsg;

    /** 冻结原因 */
    private String freezeReason;

    /** 运费 */
    private String postFee;

    /** 实付金额 */
    private String payment;

    /** 预估税费 */
    private String taxAmount;

    /** 运单号 */
    private String logisticsNo;

    /** 省 */
    private String province;

    /** 市 */
    private String city;

    /** 区 */
    private String district;

    /** 收货地址 */
    private String consigneeAddr;

    /** 收货电话 */
    private String consigneeTel;

    /** 支付时间 */
    private Timestamp payTime;

    /** 订单创建时间 */
    private Timestamp orderCreateTime;

    /** 创建时间 */
    private Timestamp createTime;

    /** 接单回传时间 */
    private Timestamp receivedBackTime;

    /** 清关开始时间 */
    private Timestamp clearStartTime;

    /** 清关开始回传时间 */
    private Timestamp clearStartBackTime;

    /** 清关完成时间 */
    private Timestamp clearSuccessTime;

    /** 清关完成回传时间 */
    private Timestamp clearSuccessBackTime;

    /** 出库时间 */
    private Timestamp deliverTime;

    /** 快递揽收时间 */
    private Timestamp logisticsCollectTime;

    /** 取消时间 */
    private Timestamp cancelTime;

    /** 创建人 */
    private String createBy;

    /** 客户名称 */
    private String customersName;

    /** 店铺名称 */
    private String shopName;

    /** 时间 */
    private String dayTime;

    /** 时间 */
    private String isCollect;
}