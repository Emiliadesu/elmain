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
* @author luob
* @date 2022-06-22
**/
@Entity
@Data
@Table(name="bus_order_material")
public class OrderMaterial implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "order_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "订单ID")
    private Long orderId;

    @Column(name = "customers_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "客户ID")
    private Long customersId;

    @Column(name = "shop_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;

    @Column(name = "customers_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "客户名称")
    private String customersName;

    @Column(name = "shop_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "店铺名称")
    private String shopName;

    @Column(name = "order_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @Column(name = "material_code")
    @ApiModelProperty(value = "耗材编码")
    private String materialCode;

    @Column(name = "material_name")
    @ApiModelProperty(value = "耗材名称")
    private String materialName;

    @Column(name = "qty")
    @ApiModelProperty(value = "使用数量")
    private Integer qty;

    @Column(name = "logistics_no")
    @ApiModelProperty(value = "运单号")
    private String logisticsNo;

    @Column(name = "material_out_code")
    @ApiModelProperty(value = "外部耗材编码")
    private String materialOutCode;

    @Column(name = "material_out_name")
    @ApiModelProperty(value = "外部耗材名称")
    private String materialOutName;

    @Column(name = "default01")
    @ApiModelProperty(value = "预留字段1")
    private String default01;

    @Column(name = "create_by")
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    public void copy(OrderMaterial source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}