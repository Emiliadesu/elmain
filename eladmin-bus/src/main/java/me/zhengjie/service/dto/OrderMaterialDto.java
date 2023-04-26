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
* @date 2022-06-22
**/
@Data
public class OrderMaterialDto implements Serializable {

    /** ID */
    private Long id;

    /** 订单ID */
    private Long orderId;

    /** 客户ID */
    private Long customersId;

    /** 店铺ID */
    private Long shopId;

    /** 客户名称 */
    private String customersName;

    /** 店铺名称 */
    private String shopName;

    /** 订单号 */
    private String orderNo;

    /** 耗材编码 */
    private String materialCode;

    /** 耗材名称 */
    private String materialName;

    /** 运单号 */
    private String logisticsNo;

    /** 外部耗材编码 */
    private String materialOutCode;

    /** 外部耗材名称 */
    private String materialOutName;

    /** 使用数量 */
    private Integer qty;

    /** default01  */
    private String default01;

    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    private Timestamp createTime;
}