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
* @date 2021-08-10
**/
@Data
public class ClearDetailsDto implements Serializable {

    /** ID */
    private Long id;

    /** 清关ID */
    private Long clearId;

    /** 单据编号 */
    private String clearNo;

    /** 序号 */
    private Integer seqNo;

    /** 货品ID */
    private Long goodsId;

    /** 货品编码 */
    private String goodsCode;

    /** 海关货号 */
    private String goodsNo;

    private String outerGoodsNo;

    /** HS编码 */
    private String hsCode;

    /** 商品名称 */
    private String goodsName;

    /** 净重（千克） */
    private String netWeight;

    /** 毛重（千克） */
    private String grossWeight;

    /** 法一单位 */
    private String legalUnit;

    /** 法一单位代码 */
    private String legalUnitCode;

    /** 法一数量 */
    private String legalNum;

    /** 法二单位 */
    private String secondUnit;

    /** 法二单位代码 */
    private String secondUnitCode;

    /** 法二数量 */
    private String secondNum;

    /** 数量 */
    private String qty;

    /** 计量单位 */
    private String unit;

    /** 商品单价 */
    private String price;

    /** 商品总价 */
    private String totalPrice;

    /** 规格型号 */
    private String property;

    /** 币种 */
    private String currency;

    /** 原产国 */
    private String makeCountry;
}