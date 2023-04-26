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
* @date 2022-03-30
**/
@Entity
@Data
@Table(name="bus_data_order")
public class DataOrder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "order_no")
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @Column(name = "cross_border_no")
    @ApiModelProperty(value = "交易号")
    private String crossBorderNo;

    @Column(name = "customers_id")
    @ApiModelProperty(value = "客户ID")
    private Long customersId;

    @Column(name = "shop_id")
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;

    @Column(name = "customers_name")
    @ApiModelProperty(value = "客户名称")
    private String customersName;

    @Column(name = "shop_name")
    @ApiModelProperty(value = "店铺名称")
    private String shopName;

    @Column(name = "clear_company_id")
    @ApiModelProperty(value = "清关抬头ID")
    private Long clearCompanyId;

    @Column(name = "clear_company_name")
    @ApiModelProperty(value = "清关抬头名称")
    private String clearCompanyName;

    @Column(name = "platform_code")
    @ApiModelProperty(value = "电商平台代码")
    private String platformCode;

    @Column(name = "platform_name")
    @ApiModelProperty(value = "电商平台名称")
    private String platformName;

    @Column(name = "supplier_id")
    @ApiModelProperty(value = "承运商ID")
    private Long supplierId;

    @Column(name = "supplier_name")
    @ApiModelProperty(value = "承运商名称")
    private String supplierName;

    @Column(name = "logistics_no")
    @ApiModelProperty(value = "运单号")
    private String logisticsNo;

    @Column(name = "declare_no")
    @ApiModelProperty(value = "申报单号")
    private String declareNo;

    @Column(name = "invt_no")
    @ApiModelProperty(value = "总署清单编号")
    private String invtNo;

    @Column(name = "order_create_time")
    @ApiModelProperty(value = "订单创建时间")
    private Timestamp orderCreateTime;

    @Column(name = "payment")
    @ApiModelProperty(value = "实付金额")
    private String payment;

    @Column(name = "tariff_amount")
    @ApiModelProperty(value = "关税")
    private String tariffAmount;

    @Column(name = "added_value_tax_amount")
    @ApiModelProperty(value = "增值税")
    private String addedValueTaxAmount;

    @Column(name = "consumption_duty_amount")
    @ApiModelProperty(value = "消费税")
    private String consumptionDutyAmount;

    @Column(name = "tax_amount")
    @ApiModelProperty(value = "总税额")
    private String taxAmount;

    @Column(name = "province")
    @ApiModelProperty(value = "省")
    private String province;

    @Column(name = "city")
    @ApiModelProperty(value = "市")
    private String city;

    @Column(name = "district")
    @ApiModelProperty(value = "区")
    private String district;

    @Column(name = "material_code")
    @ApiModelProperty(value = "包材编码")
    private String materialCode;

    @Column(name = "four_pl")
    @ApiModelProperty(value = "抖音4PL单")
    private String fourPl;

    @Column(name = "pack_weight")
    @ApiModelProperty(value = "包裹重量")
    private String packWeight;

    @Column(name = "clear_start_time")
    @ApiModelProperty(value = "清关开始时间")
    private Timestamp clearStartTime;

    @Column(name = "clear_success_time")
    @ApiModelProperty(value = "清关完成时间")
    private Timestamp clearSuccessTime;

    @Column(name = "deliver_time")
    @ApiModelProperty(value = "出库时间")
    private Timestamp deliverTime;

    @Column(name = "create_time",nullable = false)
    @NotNull
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    public void copy(DataOrder source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}