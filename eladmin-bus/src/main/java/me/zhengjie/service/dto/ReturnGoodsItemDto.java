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
* @author lh
* @date 2021-01-04
**/
@Data
public class ReturnGoodsItemDto implements Serializable {

    private Long id;

    /** 退货id */
    private String returnGoodsId;

    /** 海关货号 */
    private String sku;

    /** 条码 */
    private String barCode;

    private String skuName;

    /** 货品数量 */
    private Integer num;

    /** 货品状态 */
    private String status;
}