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
* @date 2020-10-13
**/
@Entity
@Data
@Table(name="bus_location_info")
public class LocationInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "location",unique = true,nullable = false)
    @NotBlank
    @ApiModelProperty(value = "库位编码")
    private String location;

    @Column(name = "type")
    @ApiModelProperty(value = "库位类型")
    private String type;

    @Column(name = "area",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "区域")
    private String area;

    @Column(name = "modify_time")
    @ApiModelProperty(value = "modifyTime")
    private Timestamp modifyTime;

    @Column(name = "modify_user")
    @ApiModelProperty(value = "modifyUser")
    private String modifyUser;

    public void copy(LocationInfo source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}