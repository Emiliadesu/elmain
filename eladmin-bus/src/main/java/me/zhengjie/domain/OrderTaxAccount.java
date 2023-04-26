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

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2021-12-11
**/
@Entity
@Data
@Table(name="bus_order_tax_account")
public class OrderTaxAccount implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "order_no")
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @Excel(name = "LP单号")
    @Column(name = "lp_no")
    @ApiModelProperty(value = "LP单号")
    private String lpNo;

    @Excel(name = "运单号")
    @Column(name = "mail_no")
    @ApiModelProperty(value = "运单号")
    private String mailNo;

    @Excel(name = "总署清单编号")
    @Column(name = "invt_no")
    @ApiModelProperty(value = "总署清单编号")
    private String invtNo;

    @Column(name = "add_tax")
    @ApiModelProperty(value = "增值税")
    private BigDecimal addTax;

    @Column(name = "consumption_tax")
    @ApiModelProperty(value = "消费税")
    private BigDecimal consumptionTax;

    @Column(name = "tax_amount")
    @ApiModelProperty(value = "总税")
    private BigDecimal taxAmount;

    @Column(name = "order_time")
    @ApiModelProperty(value = "订单出库时间")
    private Timestamp orderTime;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    public void copy(OrderTaxAccount source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}