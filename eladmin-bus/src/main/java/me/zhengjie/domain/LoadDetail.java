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
* @author wangm
* @date 2021-04-01
**/
@Entity
@Data
@Table(name="bus_load_detail")
public class LoadDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @JoinColumn(name = "load_header_id")
    @ManyToOne(fetch=FetchType.LAZY,targetEntity = LoadHeader.class)
    @ApiModelProperty(value = "预装载单据头", hidden = true)
    private LoadHeader loadHeader;

    @Column(name = "do_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "do编号")
    private String doNo;

    @Column(name = "lpn")
    @ApiModelProperty(value = "托盘号")
    private String lpn;

    public void copy(LoadDetail source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
