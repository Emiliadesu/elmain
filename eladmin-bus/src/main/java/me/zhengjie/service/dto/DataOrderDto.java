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
* @date 2022-03-30
**/
@Data
public class DataOrderDto implements Serializable {

    /** ID */
    private Long id;

    /** 订单号 */
    private String orderNo;

    /** 交易号 */
    private String crossBorderNo;

    /** 客户ID */
    private Long customersId;

    /** 店铺ID */
    private Long shopId;

    /** 客户名称 */
    private String customersName;

    /** 店铺名称 */
    private String shopName;

    /** 清关抬头ID */
    private Long clearCompanyId;

    /** 清关抬头名称 */
    private String clearCompanyName;

    /** 电商平台代码 */
    private String platformCode;

    /** 电商平台名称 */
    private String platformName;

    /** 承运商ID */
    private Long supplierId;

    /** 承运商名称 */
    private String supplierName;

    /** 运单号 */
    private String logisticsNo;

    /** 申报单号 */
    private String declareNo;

    /** 总署清单编号 */
    private String invtNo;

    /** 订单创建时间 */
    private Timestamp orderCreateTime;

    /** 实付金额 */
    private String payment;

    /** 关税 */
    private String tariffAmount;

    /** 增值税 */
    private String addedValueTaxAmount;

    /** 消费税 */
    private String consumptionDutyAmount;

    /** 总税额 */
    private String taxAmount;

    /** 省 */
    private String province;

    /** 市 */
    private String city;

    /** 区 */
    private String district;

    /** 包材编码 */
    private String materialCode;

    /** 抖音4PL单 */
    private String fourPl;

    /** 包裹重量 */
    private String packWeight;

    /** 清关开始时间 */
    private Timestamp clearStartTime;

    /** 清关完成时间 */
    private Timestamp clearSuccessTime;

    /** 出库时间 */
    private Timestamp deliverTime;

    /** 创建时间 */
    private Timestamp createTime;
}