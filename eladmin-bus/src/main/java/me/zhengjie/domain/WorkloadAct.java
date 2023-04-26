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
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.annotations.*;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author KC
* @date 2021-01-13
**/
@Entity
@Data
@Table(name="bus_workload_act")
public class WorkloadAct implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "order_sn",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "单号")
    private String orderSn;

    @Column(name = "work_type",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "工作类型")
    private String workType;

    @Column(name = "user_id",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "员工账号")
    private String userId;

    @Column(name = "add_time")
    @CreationTimestamp
    @ApiModelProperty(value = "新增时间")
    private Timestamp addTime;

    public void copy(WorkloadAct source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}