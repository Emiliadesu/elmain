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
package me.zhengjie.domain;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2022-11-21
**/
@Entity
@Data
@Table(name="bus_cainiao_shop_info")
public class CainiaoShopInfo implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "shop_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "店铺")
    private Long shopId;

    @Column(name = "cn_owner_id",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "菜鸟货主id")
    private String cnOwnerId;

    @Column(name = "cp_code",nullable = false)
    @ApiModelProperty(value = "物流云对ISV的授权token")
    private String cpCode;

    @Column(name = "gos_token")
    @ApiModelProperty(value = "gos平台的授权token")
    private String gosToken;

    @Column(name = "business_unit_id")
    @ApiModelProperty(value = "所属BU Id")
    private String businessUnitId;

    @Column(name = "hs_code")
    @ApiModelProperty(value = "HSCode")
    private String hsCode;

    @Column(name = "channel_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "库存分组")
    private String channelCode;

    public void copy(CainiaoShopInfo source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}