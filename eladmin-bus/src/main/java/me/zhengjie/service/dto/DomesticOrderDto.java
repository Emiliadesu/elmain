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
* @date 2022-04-11
**/
@Data
public class DomesticOrderDto implements Serializable {

    /** ID */
    private Long id;

    /** 订单号 */
    private String orderNo;

    /** 订单号 */
    private String outerNo;

    /** WMS单号 */
    private String wmsNo;

    /** 状态 */
    private Integer status;

    /** WMS状态 */
    private String wmsStatus;

    /** 上游订单状态 */
    private String upStatus;

    /** 客户ID */
    private Long customersId;

    /** 店铺ID */
    private Long shopId;

    /** 平台店铺ID */
    private String platformShopId;

    /** 电商平台代码 */
    private String platformCode;

    /** 承运商ID */
    private Long supplierId;

    /** 订单创建时间 */
    private Timestamp orderCreateTime;

    /** 异常信息 */
    private String errMsg;

    /** 是否预售 */
    private String preSell;

    /** 冻结原因 */
    private String freezeReason;

    /** 客户备注 */
    private String buyerRemark;

    /** 运单号 */
    private String logisticsNo;

    /** 省 */
    private String province;

    /** 市 */
    private String city;

    /** 区 */
    private String district;

    /** 收货人 */
    private String consigneeName;

    /** 收货地址 */
    private String consigneeAddr;

    /** 收货电话 */
    private String consigneeTel;

    /** 大头笔 */
    private String addMark;

    /** 包裹重量 */
    private String packWeight;

    /** 包材编码 */
    private String materialCode;

    /** 接单回传时间 */
    private Timestamp receivedBackTime;

    /** 审核通过时间 */
    private Timestamp checkPackTime;

    /** 预处理完成时间 */
    private Timestamp preHandelTime;

    /** 打包时间 */
    private Timestamp packTime;

    /** 打包完成回传时间 */
    private Timestamp packBackTime;

    /** 称重时间 */
    private Timestamp weighingTime;

    /** 出库时间 */
    private Timestamp deliverTime;

    /** 取消时间 */
    private Timestamp cancelTime;

    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    private Timestamp createTime;

    private String orderType;
}