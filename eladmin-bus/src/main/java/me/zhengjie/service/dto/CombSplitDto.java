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
* @date 2021-06-21
**/
@Data
public class CombSplitDto implements Serializable {

    private Long id;

    /** 组合包的id */
    private Long combId;

    /** 组合包sku */
    private String combSkuId;

    /** 货号 */
    private String splitSkuId;

    /** 品名 */
    private String splitSkuName;

    /** 数量 */
    private Integer qty;

    /** 价格 */
    private BigDecimal payment;
}