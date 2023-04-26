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
* @author lh
* @date 2021-01-04
**/
@Entity
@Data
@Table(name="bus_return_goods")
public class ReturnGoods implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "tms_order_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "运单号")
    private String tmsOrderCode;

    @Column(name = "tms_company",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "物流公司")
    private String tmsCompany;

    @Column(name = "is_origin",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "是否原单退回")
    private String isOrigin;

    @Column(name = "origin_order_no")
    @ApiModelProperty(value = "原订单号")
    private String originOrderNo;

    @Column(name = "origin_order_type",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "原订单类型")
    private String originOrderType;

    @Column(name = "owner",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "电商")
    private String owner;

    @Column(name = "owner_shop")
    @ApiModelProperty(value = "店铺")
    private String ownerShop;

    @Column(name = "process_way",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "处理方式")
    private String processWay;

    @Column(name = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Column(name = "goods_go_where")
    @ApiModelProperty(value = "货品去向")
    private String goodsGoWhere;

    @Column(name = "return_owner_order_no")
    @ApiModelProperty(value = "寄回单号")
    private String returnOwnerOrderNo;

    @Column(name = "return_owner_time",nullable = false)
    @NotNull
    @ApiModelProperty(value = "寄回时间")
    private Timestamp returnOwnerTime;

    @Column(name = "status",nullable = false)
    @ApiModelProperty(value = "是否完成")
    private String status;

    @Column(name = "create_user",nullable = false)
    @ApiModelProperty(value = "登记人")
    private String createUser;

    @Column(name = "create_time",nullable = false)
    @ApiModelProperty(value = "登记时间")
    private Timestamp createTime;

    @Column(name = "modify_user",nullable = false)
    @ApiModelProperty(value = "最后修改者")
    private String modifyUser;

    @Column(name = "modify_time",nullable = false)
    @ApiModelProperty(value = "最后修改时间")
    private Timestamp modifyTime;

    public void copy(ReturnGoods source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
