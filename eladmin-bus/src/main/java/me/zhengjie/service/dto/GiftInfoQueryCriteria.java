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

import java.sql.Timestamp;
import java.util.List;
import me.zhengjie.annotation.Query;

/**
* @website https://el-admin.vip
* @author leningzhou
* @date 2021-12-27
**/
@Data
public class GiftInfoQueryCriteria{

    /** 绑定类型 */
    @Query
    private String bindingType;

    /** 赠品条码 */
    @Query(type = Query.Type.INNER_LIKE)
    private String giftCode;

    /** 赠品名称 */
    @Query(type = Query.Type.INNER_LIKE)
    private String giftName;

    /** 赠品名称 */
    @Query
    private String goodsCode;

    @Query
    private String goodsName;

    /** 是否绑定SKU */
    @Query
    private Long skuId;

    @Query
    private Long customersId;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> openTime;

    @Query(type = Query.Type.BETWEEN)
    private Timestamp endTime;

    @Query
    private Integer status;

    /** 店铺ID */
    @Query
    private Long shopId;
}