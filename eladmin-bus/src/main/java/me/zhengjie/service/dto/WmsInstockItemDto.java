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
* @date 2020-12-08
**/
@Data
public class WmsInstockItemDto implements Serializable {

    private Long id;

    private Long inId;

    private WmsInstockSmallDto wmsInstock;

    /** 供应商货号 */
    private String skuNo;

    /** 仓库货号 */
    private String goodsNo;

    /** 商品行 */
    private String goodsLineNo;

    /** 条码 */
    private String barCode;

    /** 商品名称 */
    private String goodsName;

    /** 商品数量 */
    private Integer qty;

    /** 商品Id */
    private String itemId;

    /** 商品编码 */
    private String productId;

    /** 客户批次号 */
    private String customerBatchNo;

    /** 生产日期 */
    private String productionTime;

    /** 失效日期 */
    private String expireDate;

    /** 细分类型 */
    private String subType;

    /** 是否回传序列号 */
    private Integer needSn;
}
