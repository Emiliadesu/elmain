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
* @date 2021-03-16
**/
@Entity
@Data
@Table(name="tmp_yard_bills")
public class YardBills implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "box_num",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "箱号")
    private String boxNum;

    @Column(name = "flight",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "船名航次")
    private String flight;

    @Column(name = "customer",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "客户")
    private String customer;

    @Column(name = "tel_phone",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "联系电话")
    private String telPhone;

    @Column(name = "in_date",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "进场日期")
    private String inDate;

    @Column(name = "out_date",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "出场日期")
    private String outDate;

    @Column(name = "drop_box",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "落箱费")
    private String dropBox;

    @Column(name = "demurrage",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "滞箱费")
    private String demurrage;

    @Column(name = "refund",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "还箱费")
    private String refund;

    @Column(name = "add_cost",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "附加费")
    private String addCost;

    @Column(name = "total",nullable = false)
    @ApiModelProperty(value = "费用总计")
    private String total;

    @Column(name = "send_address",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "联系场地")
    private String sendAddress;

    @Column(name = "send_tel",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "场地联系电话")
    private String sendTel;

    @Column(name = "create_time")
    @ApiModelProperty(value = "凭据添加时间")
    private Timestamp createTime;

    public void copy(YardBills source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
