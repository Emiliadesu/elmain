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
* @author wangm
* @date 2021-06-21
**/
@Data
public class CombinationOrderDto implements Serializable {

    private Long id;

    /** 组合包的skuId */
    private String combSkuId;

    /** 组合包的名称 */
    private String combName;

    /** 组合包来源平台 */
    private Long platform;

    private Integer splitQty;

    /** 组合包所属系统店铺id */
    private Long shopId;
}