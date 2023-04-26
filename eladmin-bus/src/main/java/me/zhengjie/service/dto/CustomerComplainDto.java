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
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-12-15
**/
@Data
public class CustomerComplainDto implements Serializable {

    /** 防止精度丢失 */
    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;

    /** 运单号 */
    private String mailNo;

    /** 订单号 */
    private String orderNo;

    /** 客诉类型 */
    private String complainType;

    /** 责任方 */
    private String responsibleParty;

    /** 责任人 */
    private String responsibleName;

    /** 处理方式 */
    private String processWay;

    /** 赔偿金额 */
    private BigDecimal processPrice;

    /** 退货单号 */
    private String returnNo;

    /** 补发单号 */
    private String reissuedNo;

    /** 备注 */
    private String remark;

    /** 登记人 */
    private String createUser;

    /** 登记时间 */
    private Timestamp createTime;

    /** 修改人 */
    private String modifyUser;

    /** 修改时间 */
    private Timestamp modifyTime;

    /** 订单平台 */
    private Long platId;

    /** 客户 */
    private Long customersId;

    /** 店铺 */
    private Long shopId;

    private List<CustomerComplainItemDto>itemList;
}
