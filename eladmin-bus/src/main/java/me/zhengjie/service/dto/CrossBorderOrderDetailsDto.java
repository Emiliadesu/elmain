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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2021-03-25
**/
@Data
public class CrossBorderOrderDetailsDto implements Serializable {

    /** ID */
    private Long id;

    /** 订单ID */
    private Long orderId;

    /** 订单号 */
    private Long orderNo;

    /** 货品ID */
    private Long goodsId;

    /** 货品编码 */
    private String goodsCode;

    /** 海关货号 */
    private String goodsNo;

    /** 条码 */
    private String barCode;

    /** HS编码 */
    private String hsCode;

    /** 商品名称 */
    private String goodsName;

    /** 数量 */
    private String qty;

    /** 计量单位 */
    private String unit;

    /** 实付总价 */
    private String payment;

    /** 完税单价 */
    private String dutiableValue;

    /** 关税 */
    private String tariffAmount;

    /** 增值税 */
    private String addedValueTaxAmount;

    /** 消费税 */
    private String consumptionDutyAmount;

    /** 总税额 */
    private String taxAmount;

    /** 原产国 */
    private String makeCountry;

    /** 净重（千克） */
    private String netWeight;

    /** 毛重（千克） */
    private String grossWeight;

    private String fontGoodsName;
}
