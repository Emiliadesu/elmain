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
* @author 王淼
* @date 2020-10-12
**/
@Entity
@Data
@Table(name="bus_platform")
public class Platform implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "plaf_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "平台名")
    private String plafName;

    @Column(name = "plaf_nick_name")
    @ApiModelProperty(value = "平台别名")
    private String plafNickName;

    @Column(name = "plaf_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "平台代码")
    private String plafCode;

    @Column(name = "ebp_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "ebpCode")
    private String ebpCode;

    @Column(name = "ebp_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "ebpName")
    private String ebpName;

    @Column(name = "order_form",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "orderForm")
    private String orderForm;

    @Column(name = "app_id")
    @ApiModelProperty(value = "开发者应用id")
    private String appId;

    @Column(name = "app_secret")
    @ApiModelProperty(value = "开发者应用secret")
    private String appSecret;

    public void copy(Platform source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}