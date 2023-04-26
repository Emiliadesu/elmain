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

import io.swagger.models.auth.In;
import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.persistence.*;
import javax.validation.constraints.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import me.zhengjie.base.BaseEntity;
import org.hibernate.annotations.*;
import java.sql.Timestamp;
import java.io.Serializable;
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author AddValueOrder
* @date 2021-08-05
**/
@Entity
@Data
@Table(name="bus_add_value_order")
public class AddValueOrder extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "customers_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "客户")
    private Long customersId;

    @Column(name = "shop_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "店铺")
    private Long shopId;

    @Column(name = "status",nullable = false)
    @ApiModelProperty(value = "状态")
    private Integer status;

    @Column(name = "order_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @Column(name = "out_order_no")
    @ApiModelProperty(value = "外部单号")
    private String outOrderNo;

    @Column(name = "warehouse")
    @ApiModelProperty(value = "仓区")
    private Integer warehouse;

    @Column(name = "type",nullable = false)
    @NotNull
    @ApiModelProperty(value = "类型")
    private Integer type;

    @Column(name = "add_code",nullable = false)
    @ApiModelProperty(value = "增值编码")
    @NotNull
    private String addCode;

    @Column(name = "add_name")
    @ApiModelProperty(value = "增值名称")
    private String addName;

    @Column(name = "ref_no")
    @ApiModelProperty(value = "关联单号")
    private String refNo;

    @Column(name = "ref_type")
    @ApiModelProperty(value = "关联单类型")
    private Integer refType;

    @Column(name = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Column(name = "finish_time")
    @ApiModelProperty(value = "完成时间")
    private String finishTime;

    @Column(name = "finish_qty")
    @ApiModelProperty(value = "实际完成数量")
    private Integer finishQty;

    @Transient
    private List<AddValueOrderDetails> details;

    public void copy(AddValueOrder source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}