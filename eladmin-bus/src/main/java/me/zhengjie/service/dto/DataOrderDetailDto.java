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
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2022-03-30
**/
@Data
public class DataOrderDetailDto implements Serializable {

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

    /** 运单号 */
    private String logisticsNo;

    /** 货品ID */
    private Long goodsId;

    /** 货品编码 */
    private String goodsCode;

    /** 海关货号 */
    private String goodsNo;

    /** HS编码 */
    private String hsCode;

    /** 前端商品名称 */
    private String fontGoodsName;

    /** 商品名称 */
    private String goodsName;

    /** 数量 */
    private String qty;

    /** 实付总价 */
    private String payment;

    /** 完税单价 */
    private String dutiableValue;

    /** 完税总价 */
    private String dutiableTotalValue;

    /** 关税 */
    private String tariffAmount;

    /** 增值税 */
    private String addedValueTaxAmount;

    /** 消费税 */
    private String consumptionDutyAmount;

    /** 耗材编码 */
    private String consumableMaterialCode;

    /** 耗材数量 */
    private String consumableMaterialNum;
}