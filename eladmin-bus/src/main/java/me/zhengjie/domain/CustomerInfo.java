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
import java.util.Date;
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-02-27
**/
@Entity
@Data
@Table(name="bus_customer_info")
public class CustomerInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "cust_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "客户名")
    private String custName;

    @Column(name = "cust_nick_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "客户别名")
    private String custNickName;

    @Column(name = "contacts",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "客户联系人姓名")
    private String contacts;

    @Column(name = "telphone",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "客户联系电话")
    private String telphone;

    @Column(name = "address",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "客户地址")
    private String address;

    @Column(name = "create_time",nullable = false)
    @ApiModelProperty(value = "添加时间")
    private Timestamp createTime;

    @Column(name = "create_user_id",nullable = false)
    @ApiModelProperty(value = "添加人id")
    private Long createUserId;

    @Transient
    private List<ShopInfo> children;

    public void copy(CustomerInfo source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }

    public String getLabel() {
        return custNickName;
    }
}
