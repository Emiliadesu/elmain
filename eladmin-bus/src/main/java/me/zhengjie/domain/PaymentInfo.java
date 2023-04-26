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
* @date 2022-04-26
**/
@Entity
@Data
@Table(name="bus_payment_info")
public class PaymentInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "pay_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "支付机构企业海关备案名	")
    private String payName;

    @Column(name = "pay_customer_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "支付机构企业海关备案代码")
    private String payCustomerCode;

    @Column(name = "pay_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "海关支付代码")
    private String payCode;

    @Column(name = "create_by",nullable = false)
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @Column(name = "create_time",nullable = false)
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @Column(name = "update_by",nullable = false)
    @ApiModelProperty(value = "修改人")
    private String updateBy;

    @Column(name = "update_time",nullable = false)
    @ApiModelProperty(value = "修改时间")
    private Timestamp updateTime;

    public void copy(PaymentInfo source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}