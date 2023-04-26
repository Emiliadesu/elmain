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
* @author leningzhou
* @date 2021-12-03
**/
@Entity
@Data
@Table(name="bus_logistics_unarrive_place")
public class LogisticsUnarrivePlace implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "logistics_id")
    @ApiModelProperty(value = "物流ID")
    private Long logisticsId;

    @Column(name = "logistics_name")
    @ApiModelProperty(value = "物流公司")
    private String logisticsName;

    @Column(name = "logistics_code")
    @ApiModelProperty(value = "物流代码")
    private String logisticsCode;

    @Column(name = "province")
    @ApiModelProperty(value = "省")
    private String province;

    @Column(name = "city")
    @ApiModelProperty(value = "市")
    private String city;

    @Column(name = "district")
    @ApiModelProperty(value = "区")
    private String district;

    @Column(name = "reason")
    @ApiModelProperty(value = "原因")
    private String reason;

    public void copy(LogisticsUnarrivePlace source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}