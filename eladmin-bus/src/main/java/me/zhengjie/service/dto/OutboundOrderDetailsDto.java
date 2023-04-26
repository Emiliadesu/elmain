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
* @date 2021-07-13
**/
@Data
public class OutboundOrderDetailsDto implements Serializable {

    /** ID */
    private Long id;

    /** 订单ID */
    private Long orderId;

    /** 入库单号 */
    private String orderNo;

    /** 货品ID */
    private Long goodsId;

    /** 货品编码 */
    private String goodsCode;

    /** 海关货号 */
    private String goodsNo;

    /** 条码 */
    private String barCode;

    /** 商品名称 */
    private String goodsName;

    /** 商品行 */
    private String goodsLineNo;

    /** 预期发货数量 */
    private Integer expectNum;

    /** 实际发货数量 */
    private Integer deliverNum;

    /** 短少数量 */
    private Integer lackNum;

    /** 正品数量 */
    private Integer normalNum;

    /** 残品数量 */
    private Integer damagedNum;

    private String default01;
}