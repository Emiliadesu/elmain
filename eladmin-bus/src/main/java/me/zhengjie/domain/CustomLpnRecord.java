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
* @author wangm
* @date 2021-06-23
**/
@Entity
@Data
@Table(name="bus_custom_lpn_record")
public class CustomLpnRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "po_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "入库的po单号")
    private String poNo;

    @Column(name = "lpn_code",unique = true,nullable = false)
    @NotBlank
    @ApiModelProperty(value = "收货托盘号")
    private String lpnCode;

    @Column(name = "create_user")
    @ApiModelProperty(value = "录入人")
    private String createUser;

    @Column(name = "create_time")
    @ApiModelProperty(value = "录入时间")
    private Timestamp createTime;

    public void copy(CustomLpnRecord source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}