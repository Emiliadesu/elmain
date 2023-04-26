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
import java.math.BigDecimal;
import java.io.Serializable;
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-12-15
**/
@Entity
@Data
@Table(name="bus_customer_complain")
public class CustomerComplain implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "mail_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "运单号")
    private String mailNo;

    @Column(name = "order_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @Column(name = "complain_type",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "客诉类型")
    private String complainType;

    @Column(name = "responsible_party",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "责任方")
    private String responsibleParty;

    @Column(name = "responsible_name")
    @ApiModelProperty(value = "责任人")
    private String responsibleName;

    @Column(name = "process_way")
    @ApiModelProperty(value = "处理方式")
    private String processWay;

    @Column(name = "process_price")
    @ApiModelProperty(value = "赔偿金额")
    private BigDecimal processPrice;

    @Column(name = "return_no")
    @ApiModelProperty(value = "退货单号")
    private String returnNo;

    @Column(name = "reissued_no")
    @ApiModelProperty(value = "补发单号")
    private String reissuedNo;

    @Column(name = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Column(name = "create_user",nullable = false)
    @ApiModelProperty(value = "登记人")
    private String createUser;

    @Column(name = "create_time",nullable = false)
    @ApiModelProperty(value = "登记时间")
    private Timestamp createTime;

    @Column(name = "modify_user")
    @ApiModelProperty(value = "修改人")
    private String modifyUser;

    @Column(name = "modify_time")
    @ApiModelProperty(value = "修改时间")
    private Timestamp modifyTime;

    @Column(name = "plat_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "订单平台")
    private Long platId;

    @Column(name = "customers_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "客户")
    private Long customersId;

    @Column(name = "shop_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "店铺")
    private Long shopId;

    @Transient
    private List<CustomerComplainItem>itemList;
    public void copy(CustomerComplain source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
