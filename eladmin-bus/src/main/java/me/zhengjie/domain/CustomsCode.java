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
* @author luob
* @date 2021-08-21
**/
@Entity
@Data
@Table(name="bus_customs_code")
public class CustomsCode implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "type",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "类型")
    private String type;

    @Column(name = "code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "代码")
    private String code;

    @Column(name = "des",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "描述")
    private String des;

    @Column(name = "create_by")
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @Column(name = "update_by")
    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @Column(name = "update_time")
    @ApiModelProperty(value = "更新时间")
    private Timestamp updateTime;

    @Column(name = "type_des",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "类型描述")
    private String typeDes;

    public void copy(CustomsCode source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}