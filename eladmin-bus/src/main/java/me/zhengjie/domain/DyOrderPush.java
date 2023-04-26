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
* @date 2021-09-30
**/
@Entity
@Data
@Table(name="bus_dy_order_push")
public class DyOrderPush implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "order_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @Column(name = "platform_shop_id",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "抖音店铺id")
    private String platformShopId;

    @Column(name = "shop_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "店铺id")
    private Long shopId;

    @Column(name = "is_success",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "是否成功")
    private String isSuccess;

    @Column(name = "create_time",nullable = false)
    @NotNull
    @ApiModelProperty(value = "推送时间")
    private Timestamp createTime;

    @Column(name = "update_time",nullable = false)
    @NotNull
    @ApiModelProperty(value = "最近推送时间")
    private Timestamp updateTime;

    public void copy(DyOrderPush source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}