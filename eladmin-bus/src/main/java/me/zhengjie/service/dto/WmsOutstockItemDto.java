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
* @date 2020-12-18
**/
@Data
public class WmsOutstockItemDto implements Serializable {

    private Integer id;

    /** 出库单号，商家自行生成的唯一单号 */
    private String outOrderSn;

    /** 仓库货号 */
    private String goodsNo;

    /** 商品行 */
    private String goodsLineNo;

    /** 供应商货号 */
    private String skuNo;

    /** 条码 */
    private String barCode;

    /** 商品名称 */
    private String goodsName;

    /** 商品数量 */
    private Integer qty;

    /** 正品数量 */
    private Integer nondefectiveNum;

    /** 残品数量 */
    private Integer damageNum;

    /** 指定生产批次 */
    private String batchNo;

    /** 指定效期开始(yyyy-MM-dd HH:mm:ss) */
    private String expStartTime;

    /** 商品id */
    private String itemId;

    /** 指定效期结束(yyyy-MM-dd HH:mm:ss) */
    private String expEndTime;

    private WmsOutstockSmallDto wmsOutstock;

    /** 是否回传序列号 */
    private Integer needSn;

    private String productId;
}
