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
* @author BooksInfo
* @date 2021-07-21
**/
@Entity
@Data
@Table(name="bus_books_info")
public class BooksInfo extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "books_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "账册编号")
    private String booksNo;

    @Column(name = "remark",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "说明")
    private String remark;

//    @Column(name = "create_by")
//    @ApiModelProperty(value = "创建人")
//    private String createBy;
//
//    @Column(name = "create_time")
//    @ApiModelProperty(value = "创建时间")
//    private Timestamp createTime;
//
//    @Column(name = "update_by")
//    @ApiModelProperty(value = "更新人")
//    private String updateBy;
//
//    @Column(name = "update_time")
//    @ApiModelProperty(value = "更新时间")
//    private Timestamp updateTime;

    public void copy(BooksInfo source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}