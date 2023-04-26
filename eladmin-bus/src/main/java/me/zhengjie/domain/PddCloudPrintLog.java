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
* @author wangmiao
* @date 2022-08-08
**/
@Entity
@Data
@Table(name="bus_pdd_cloud_print_log")
public class PddCloudPrintLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "mail_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "运单号")
    private String mailNo;

    @Column(name = "print_operator",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "打印人")
    private String printOperator;

    @Column(name = "print_time",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "打印时间")
    private String printTime;

    @Column(name = "print_count",nullable = false)
    @NotNull
    @ApiModelProperty(value = "当时打印次数")
    private Integer printCount;

    public void copy(PddCloudPrintLog source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}