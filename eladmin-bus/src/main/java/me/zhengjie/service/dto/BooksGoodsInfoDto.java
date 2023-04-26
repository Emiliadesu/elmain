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
* @author 王淼
* @date 2020-10-13
**/
@Data
public class BooksGoodsInfoDto implements Serializable {

    private Long id;

    /** 账册编号 */
    private String booksNo;

    /** 货号 */
    private String goodsNo;

    /** 备案序号 */
    private String seqNo;

    /** 料号 */
    private String partNo;

    /** HS编码 */
    private String hsCode;

    /** 货品名称 */
    private String goodsName;

    /** 商品规格型号 */
    private String spec;

    /** 申报计量单位 */
    private String declareUnit;

    /** 法定计量单位 */
    private String meaUnitOne;

    /** 法定第二计量单位 */
    private String meaUnitTwo;

    /** 法定数量 */
    private String meaNumOne;

    /** 法定第二数量 */
    private String meaNumTwo;

    /** 币制 */
    private String currencySystem;

    /** 征免方式 */
    private String taxWay;

    /** 最终目的国 */
    private String destinationCountry;

    /** 原产国 */
    private String makeCountry;
}