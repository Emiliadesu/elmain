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
* @date 2021-12-02
**/
@Entity
@Data
@Table(name="bus_logistics_info")
public class LogisticsInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "name",unique = true,nullable = false)
    @NotBlank
    @ApiModelProperty(value = "名称")
    private String name;

    @Column(name = "code",unique = true,nullable = false)
    @NotBlank
    @ApiModelProperty(value = "代码")
    private String code;

    @Column(name = "customs_name")
    @ApiModelProperty(value = "海关备案名称")
    private String customsName;

    @Column(name = "customs_code")
    @ApiModelProperty(value = "海关备案代码")
    private String customsCode;

    @Column(name = "kjg_code",unique = true)
    @ApiModelProperty(value = "跨境购代码")
    private String kjgCode;

    @Column(name = "kjg_name",unique = true)
    @ApiModelProperty(value = "跨境购名称")
    private String kjgName;

    @Column(name = "default01")
    @ApiModelProperty(value = "默认字段1(抖音代码)")
    private String default01;

    @Column(name = "default02")
    @ApiModelProperty(value = "默认字段2(拼多多代码)")
    private String default02;

    @Column(name = "default03")
    @ApiModelProperty(value = "默认字段3")
    private String default03;

    @Column(name = "default04")
    @ApiModelProperty(value = "默认字段4")
    private String default04;

    @Column(name = "default05")
    @ApiModelProperty(value = "默认字段5")
    private String default05;

    @Column(name = "create_by")
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @Column(name = "update_by")
    @ApiModelProperty(value = "更新者")
    private String updateBy;

    @Column(name = "update_time")
    @ApiModelProperty(value = "更新时间")
    private Timestamp updateTime;

    public void copy(LogisticsInfo source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}