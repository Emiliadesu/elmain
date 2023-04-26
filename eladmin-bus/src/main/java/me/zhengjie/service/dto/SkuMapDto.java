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
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-04-05
**/
@Data
public class SkuMapDto implements Serializable {

    private Long id;

    /** 商家sku */
    private String ownerSku;

    /** 仓库sku */
    private String warhouseSku;

    /** 创建时间 */
    private Timestamp createTime;

    /** 客户 */
    private CustomerInfoDto customer;

    private String isPrimary;

    /** 渠道 */
    private String channel;

    private Timestamp finalUpdateTime;
}
