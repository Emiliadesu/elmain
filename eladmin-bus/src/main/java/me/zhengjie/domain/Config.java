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
* @author 王淼
* @date 2020-11-24
**/
@Entity
@Data
@Table(name="bus_config")
public class Config implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Integer id;

    @Column(name = "k",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "k")
    private String k;

    @Column(name = "v")
    @ApiModelProperty(value = "v")
    private String v;

    @Column(name = "des",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "描述")
    private String des;

    @Column(name = "modify_time",nullable = false)
    @ApiModelProperty(value = "modifyTime")
    private Timestamp modifyTime;

    @Column(name = "modify_user",nullable = false)
    @ApiModelProperty(value = "modifyUser")
    private String modifyUser;

    public void copy(Config source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}