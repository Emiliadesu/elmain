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
import java.util.List;
import me.zhengjie.annotation.Query;

/**
* @website https://el-admin.vip
* @author luob
* @date 2021-04-15
**/
@Data
public class BaseSkuQueryCriteria{

    /** 精确 */
    @Query
    private Long customersId;

    /** 精确 */
    @Query
    private String goodsCode;

    /** 精确 */
    @Query
    private String barCode;

    @Query
    private String isNew;

    @Query
    private Integer status;

    @Query(type = Query.Type.IN)
    private List<String> goodsNo;

    @Query
    private String outerGoodsNo;

    @Query
    private String platformCode;

    @Query
    private String warehouseCode;

    @Query
    private String isGift;

    /** 模糊 */
    @Query(type = Query.Type.INNER_LIKE)
    private String goodsName;

    @Query(type = Query.Type.IN)
    private List<Long> shopId;

    /** 模糊 */
    @Query(type = Query.Type.INNER_LIKE, propName = "barCode")
    private String barCodeLike;

    /** 模糊 */
    @Query(type = Query.Type.INNER_LIKE, propName = "goodsNo")
    private String goodsNoLike;

    @Query(type = Query.Type.EQUAL, propName = "shopId")
    private Long shopIdEq;

    /** 模糊 */
    @Query(type = Query.Type.NOT_NULL, propName = "goodsNo")
    private String goodsNoNotNull;
}