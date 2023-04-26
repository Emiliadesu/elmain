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
* @author 王淼
* @date 2020-10-20
**/
@Data
public class CustomsTariffDto implements Serializable {

    private Long id;

    /** HS编码 */
    private String hsCode;

    /** 货品名称 */
    private String name;

    /** 普通税率 */
    private BigDecimal normalTariff;

    /** 优惠税率 */
    private BigDecimal discountTariff;

    /** 备注 */
    private String remark;

    /** 出口税率 */
    private BigDecimal exportTariff;

    /** 消费税率 */
    private BigDecimal consumptTariff;

    /** 增值税率 */
    private BigDecimal valueAddTariff;

    /** 第一法定单位 */
    private String firstUnit;

    /** 第二法定单位 */
    private String secondUnit;
}