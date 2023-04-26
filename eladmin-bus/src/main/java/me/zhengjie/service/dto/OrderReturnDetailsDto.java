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
* @date 2021-04-14
**/
@Data
public class OrderReturnDetailsDto implements Serializable {

    /** ID */
    private Long id;

    /** 退货单ID */
    private Long returnId;

    /** 物流订单号 */
    private String logisticsNo;

    /** 货品ID */
    private Long goodsId;

    /** 货品编码 */
    private String goodsCode;

    /** 海关货号 */
    private String goodsNo;

    /** 条形码 */
    private String barCode;

    /** HS编码 */
    private String hsCode;

    /** 前端商品名称 */
    private String fontGoodsName;

    /** 商品名称 */
    private String goodsName;

    /** 数量 */
    private String qty;

    /** 总税额 */
    private String taxAmount;

    private Integer normalNum;

    private Integer damagedNum;

    private Integer totalNum;

    private String damagedReason;
}