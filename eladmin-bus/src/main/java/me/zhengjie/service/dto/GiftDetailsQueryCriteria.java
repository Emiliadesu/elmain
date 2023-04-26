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
* @author leningzhou
* @date 2021-12-27
**/
@Data
public class GiftDetailsQueryCriteria{


    /** 赠品ID */
    @Query
    private Long giftId;


    /** 赠品条码 */
    @Query
    private String giftCode;


    /** 赠品名称 */
    @Query
    private String giftName;

    /** 商品编码 */
    @Query
    private String goodsCode;

    /** 商品名称 */
    @Query
    private String goodsName;


    /** 放置数量 */
    @Query
    private String placeCounts;

    /** 是否绑定SKU */
    @Query
    private Long skuId;

    /** 绑定类型 */
    @Query
    private String bindingType;

}