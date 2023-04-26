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
import me.zhengjie.base.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2021-10-04
**/
@Entity
@Data
@Table(name="bus_sorting_line")
public class SortingLine extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "line_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "分拣线名称")
    private String lineName;

    @Column(name = "line_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "分拣线代码")
    private String lineCode;

    @Column(name = "area",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "绑定区域")
    private String area;

    @Column(name = "user_id",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "绑定用户ID")
    private String userId;

    @Column(name = "status",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "状态")
    private String status;


    public void copy(SortingLine source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}