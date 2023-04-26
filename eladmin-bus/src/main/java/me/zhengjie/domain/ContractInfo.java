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
* @author leningzhou
* @date 2022-03-02
**/
@Entity
@Data
@Table(name="bus_contract_info")
public class ContractInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "customers_id")
    @ApiModelProperty(value = "客户ID")
    private Long customersId;

    @Column(name = "shop_id")
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;

    @Column(name = "is_sign")
    @ApiModelProperty(value = "是否签署合同")
    private String isSign;

    @Column(name = "open_time")
    @ApiModelProperty(value = "合同开始日期")
    private Timestamp openTime;

    @Column(name = "end_time")
    @ApiModelProperty(value = "合同结束日期")
    private Timestamp endTime;

    @Column(name = "contract_url")
    @ApiModelProperty(value = "合同文件存储url")
    private String contractUrl;

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

    public void copy(ContractInfo source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}