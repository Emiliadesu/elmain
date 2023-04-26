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
@Table(name="bus_sorting_rule")
public class SortingRule extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "rule_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "规则名称")
    private String ruleName;

    @Column(name = "rule_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "规则代码")
    private String ruleCode;

    @Column(name = "rule_code1")
    @ApiModelProperty(value = "规则代码1(跨境购)")
    private String ruleCode1;

    @Column(name = "rule_code2")
    @ApiModelProperty(value = "规则代码2(菜鸟)")
    private String ruleCode2;

    @Column(name = "status",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "状态")
    private String status;


    public void copy(SortingRule source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}