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
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author le
* @date 2021-09-28
**/
@Data
public class DouyinMailMarkDto implements Serializable {

    /** ID */
    private Long id;

    /** 订单号 */
    private String orderNo;

    /** 客户ID */
    private Long customersId;

    /** 店铺ID */
    private String shopId;

    /** 运单号 */
    private String logisticsNo;

    /** 大头笔 */
    private String addMark;

    /** 创建时间 */
    private Timestamp createTime;

    /** 收货人 */
    private String consignee;

    /** 收货电话 */
    private String consigneeTelephone;

    /** 订购人证件类型   1-身份证  2-其他 */
    private String buyerIdType;

    /** 订购人证件号码 */
    private String buyerIdNumber;

    /** 进出口标志   i-进口,e-出口 */
    private String ieFlag;

    /** 0-存量  1-报税备货  2-海外集货  3-海外备货 */
    private String whType;

    /** 电商平台代码 */
    private String ebpCode;

    /** 电商平台名称 */
    private String ebpName;

    /** 关区代码 */
    private String portCode;

    /** 服务商仓库编码 */
    private String scspWarehouseCode;

    private String consigneeAddress;

    private String supplierId;

    private String province;

    private String city;

    private String district;

    private String consigneeAddr;

    private String carrierCode;

    private String fourPl;

    private String isSuccess;

}
