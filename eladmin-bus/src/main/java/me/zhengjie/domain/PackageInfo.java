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
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author leningzhou
* @date 2022-01-26
**/
@Entity
@Data
@Table(name="bus_package_info")
public class PackageInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "package_code" ,nullable = false)
    @ApiModelProperty(value = "包材编码")
    private String packageCode;

    @Column(name = "package_name",nullable = false)
    @ApiModelProperty(value = "包材名称")
    private String packageName;

    @Column(name = "package_type",nullable = false)
    @ApiModelProperty(value = "包材类型")
    private String packageType;

    @Column(name = "platform_code",nullable = false)
    @ApiModelProperty(value = "平台")
    private String platformCode;

    @Column(name = "p_length")
    @ApiModelProperty(value = "长")
    private BigDecimal packLength;

    @Column(name = "p_width")
    @ApiModelProperty(value = "宽")
    private BigDecimal packWidth;

    @Column(name = "p_height")
    @ApiModelProperty(value = "高")
    private BigDecimal packHeight;

    @Column(name = "weight")
    @ApiModelProperty(value = "重量")
    private BigDecimal weight;

    @Column(name = "add_value")
    @ApiModelProperty(value = "是否增值")
    private String addValue;

    @Column(name = "create_by")
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @Column(name = "update_by")
    @ApiModelProperty(value = "修改人")
    private String updateBy;

    @Column(name = "update_time")
    @ApiModelProperty(value = "修改时间")
    private Timestamp updateTime;

    public void copy(PackageInfo source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}