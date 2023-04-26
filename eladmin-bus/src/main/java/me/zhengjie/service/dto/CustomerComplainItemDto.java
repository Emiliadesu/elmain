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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-12-15
**/
@Data
public class CustomerComplainItemDto implements Serializable {

    /** 防止精度丢失 */
    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;

    /** 客诉ID */
    private Long complainId;

    /** 条码 */
    private String barCode;

    private String goodsName;

    /** 商品状态 */
    private String goodsStatus;

    /** 商品数量 */
    private Integer qty;

    /** 客诉数量 */
    private Integer custQty;
}
