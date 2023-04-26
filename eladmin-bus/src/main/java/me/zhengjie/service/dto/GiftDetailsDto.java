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
* @author leningzhou
* @date 2021-12-27
**/
@Data
public class GiftDetailsDto implements Serializable {

    private Long id;

    /** 赠品ID */
    private Long giftId;

    /** 赠品条码 */
    private String giftCode;

    /** 赠品名称 */
    private String giftName;

    /** 商品编码 */
    private String goodsCode;

    /** 商品名称 */
    private String goodsName;

    /** 放置数量 */
    private String placeCounts;

    /** 是否绑定SKU */
    private Long skuId;

    /** 绑定类型 */
    private String bindingType;

}