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
* @author wangm
* @date 2023-03-21
**/
@Data
public class DewuDeclarePushDto implements Serializable {

    /** ID */
    private Long id;

    /** 订单号 */
    private String orderNo;

    /** 申报模式，1-仓库申报，默认传1 */
    private String declareType;

    /** 店铺代码 */
    private String shopCode;

    /** 申报单号 */
    private String declareNo;

    /** 电商平台代码 */
    private String platformCode;

    /** 订单创建时间 */
    private Timestamp orderCreateTime;

    /** 买家账号 */
    private String buyerAccount;

    /** 订购人电话 */
    private String buyerPhone;

    /** 订购人身份证号码 */
    private String buyerIdNum;

    /** 优惠金额合计 */
    private BigDecimal disAmount;

    /** 实付金额 */
    private BigDecimal payment;

    /** 运费 */
    private BigDecimal postFee;

    /** 是否预售，1-是 0-否 */
    private String preSell;

    /** 预计出库时间 */
    private Timestamp expDeliverTime;

    /** 税费 */
    private BigDecimal taxAmount;

    /** 优惠金额(若无传0) */
    private BigDecimal discount;

    /** 快递公司代码 */
    private String logisticsCode;

    /** 运单号 */
    private String logisticsNo;

    /** 收货人 */
    private String consigneeName;

    /** 收货电话 */
    private String consigneeTel;

    /** 省 */
    private String province;

    /** 市 */
    private String city;

    /** 区 */
    private String district;

    /** 三段码 */
    private String addMark;

    /** 毛重（千克） */
    private BigDecimal grossWeight;

    /** 净重（千克） */
    private BigDecimal netWeight;

    /** 账册编号 */
    private String booksNo;

    /** 收货地址 */
    private String address;

    /** 支付流水号 */
    private String paymentNo;

    /** 支付方式 */
    private String payType;

    /** 支付时间 */
    private String payTime;
}