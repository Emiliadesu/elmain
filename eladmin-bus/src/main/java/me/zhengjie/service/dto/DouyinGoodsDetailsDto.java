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
* @author le
* @date 2021-09-28
**/
@Data
public class DouyinGoodsDetailsDto implements Serializable {

    /** ID */
    private Long id;

    /** 商品id */
    private String itemNo;

    private String barCode;

    /** 净重 */
    private String itemName;

    private BigDecimal price;

    /** 数量 */
    private Long qty;

    /** 货币类型 */
    private String currency;

    /** 毛重 */
    private String weight;

    private String netWeightQty;

    /** 订单ID */
    private Long markId;
}