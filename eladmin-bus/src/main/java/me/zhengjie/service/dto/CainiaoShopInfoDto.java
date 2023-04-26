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
* @date 2022-11-21
**/
@Data
public class CainiaoShopInfoDto implements Serializable {

    /** 防止精度丢失 */
    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;

    /** 店铺 */
    private Long shopId;

    /** 菜鸟货主id */
    private String cnOwnerId;

    /** 物流云对ISV的授权token */
    private String cpCode;

    /** gos平台的授权token */
    private String gosToken;

    /** 所属BU Id */
    private String businessUnitId;

    /** HSCode */
    private String hsCode;

    /** 库存分组 */
    private String channelCode;
}