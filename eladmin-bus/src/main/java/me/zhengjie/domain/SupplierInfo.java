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
import me.zhengjie.base.BaseUpdateEntity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2021-03-06
**/
@Entity
@Data
@Table(name="bus_supplier_info")
public class SupplierInfo extends BaseUpdateEntity implements Serializable {

    @Column(name = "supplier_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    @Column(name = "nick_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "供应商简称")
    private String nickName;

    @Column(name = "supplier_type",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "供应商类别")
    private String supplierType;

    @Column(name = "code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "代码")
    private String code;

    @Column(name = "contacts")
    @ApiModelProperty(value = "联系人")
    private String contacts;

    @Column(name = "telphone")
    @ApiModelProperty(value = "联系电话")
    private String telphone;

    @Column(name = "create_by")
    @ApiModelProperty(value = "创建者")
    private String createBy;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建日期")
    private Timestamp createTime;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    public void copy(SupplierInfo source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}