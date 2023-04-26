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
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-04-05
**/
@Entity
@Data
@Table(name="bus_sku_map")
public class SkuMap implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "owner_sku",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "商家sku")
    private String ownerSku;

    @Column(name = "warhouse_sku",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "仓库sku")
    private String warhouseSku;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @JoinColumn(name = "customer_id")
    @ManyToOne(fetch=FetchType.LAZY,targetEntity = CustomerInfo.class)
    @ApiModelProperty(value = "客户", hidden = true)
    private CustomerInfo customer;

    @Column(name = "is_primary",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "isPrimary")
    private String isPrimary;

    @Column(name = "channel",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "渠道")
    private String channel;

    @Column(name = "final_update_time")
    @ApiModelProperty(value = "finalUpdateTime")
    private Timestamp finalUpdateTime;

    public void copy(SkuMap source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
