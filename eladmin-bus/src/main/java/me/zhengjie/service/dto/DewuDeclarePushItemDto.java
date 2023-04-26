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
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2023-03-21
**/
@Data
public class DewuDeclarePushItemDto implements Serializable {

    /** ID */
    private Long id;

    /** 订单ID */
    private Long orderId;

    /** 订单号 */
    private String orderNo;

    /** 货号 */
    private String goodsNo;

    /** 商品名称 */
    private String goodsName;

    /** 数量 */
    private String qty;

    /** 申报单价 */
    private BigDecimal declarePrice;

    /** 申报总价 */
    private BigDecimal declareAmount;

    /** 商品条码 */
    private String barCode;

    /** 计量单位 */
    private String unit;

    /** 税费(单个商品税费) */
    private BigDecimal taxAmount;
}