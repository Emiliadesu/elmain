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

import me.zhengjie.base.BaseEntity;
import me.zhengjie.base.BaseUpdateEntity;
import org.hibernate.annotations.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2020-10-09
**/
@Entity
@Data
@Table(name="bus_company_info")
public class CompanyInfo extends BaseUpdateEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    @ApiModelProperty(value = "ID")
    private Long companyId;

    @Column(name = "company_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "商家名称")
    private String companyName;

    @Column(name = "bus_type",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "业务类别")
    private String busType;

    @Column(name = "in_area")
    @ApiModelProperty(value = "进仓库区")
    private String inArea;

//    @Column(name = "update_by")
//    @ApiModelProperty(value = "更新者")
//    @LastModifiedBy
//    private String updateBy;
//
//    @Column(name = "update_time")
//    @UpdateTimestamp
//    @ApiModelProperty(value = "更新时间")
//    private Timestamp updateTime;

    public void copy(CompanyInfo source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
