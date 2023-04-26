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
* @date 2021-03-23
**/
@Entity
@Data
@Table(name="bus_clear_container")
public class ClearContainer extends BaseUpdateEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "clear_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "清关ID")
    private Long clearId;

    @Column(name = "car_type",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "车型")
    private String carType;

    @Column(name = "plan_car_type",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "排车方")
    private String planCarType;

    @Column(name = "container_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "箱号")
    private String containerNo;

    @Column(name = "plate_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "车牌")
    private String plateNo;

    @Column(name = "share_flag",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "是否拼车")
    private String shareFlag;

    @Column(name = "container_type",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "箱型")
    private String containerType;

    @Column(name = "pack_way",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "打包方式")
    private String packWay;

    @Column(name = "pack_num",nullable = false)
    @NotNull
    @ApiModelProperty(value = "打包数量")
    private Integer packNum;

    public void copy(ClearContainer source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}