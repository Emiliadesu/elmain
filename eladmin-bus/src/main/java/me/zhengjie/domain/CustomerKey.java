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
* @date 2021-04-06
**/
@Entity
@Data
@Table(name="bus_customer_key")
public class CustomerKey implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "customer_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "客户id")
    private Long customerId;

    @Column(name = "sign_type",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "加密方式")
    private String signType;

    @Column(name = "sign_key",nullable = false)
    @ApiModelProperty(value = "秘钥")
    private String signKey;

    @Column(name = "create_user_id")
    @ApiModelProperty(value = "创建人id")
    private Long createUserId;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @Column(name = "code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "code")
    private String code;

    @Column(name = "callback_url")
    @ApiModelProperty(value = "callback_url")
    private String callbackUrl;

    public void copy(CustomerKey source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
