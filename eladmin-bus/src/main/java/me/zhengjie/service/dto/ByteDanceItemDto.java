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
* @date 2021-03-21
**/
@Data
public class ByteDanceItemDto implements Serializable {

    private Long id;

    /** 父订单 */
    private ByteDanceSmallDto order;

    /** 订单明细序号 */
    private Long gnum;

    /** 电商平台自己的sku id */
    private String ecpSkuId;

    /** 商家货号 */
    private String itemNo;

    /** 商品规格型号 */
    private String gModel;

    /** 条码 */
    private String barCode;

    /** 商品数量 */
    private Integer qty;

    /** 单价（商品不含税价，不含运费保费） */
    private BigDecimal price;

    /** 总价 */
    private BigDecimal totalPrice;
}
