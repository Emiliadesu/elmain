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
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-06-10
**/
@Entity
@Data
@Table(name="bus_pdd_mail_mark")
public class PddMailMark implements Serializable {

    @Id
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "order_sn",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "拼多多订单号")
    private String orderSn;

    @Column(name = "mail_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "运单号")
    private String mailNo;

    @Column(name = "add_mark",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "大头笔")
    private String addMark;

    @Column(name = "shop_code")
    @ApiModelProperty(value = "店铺code")
    private String shopCode;

    public void copy(PddMailMark source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}