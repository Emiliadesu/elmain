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
import me.zhengjie.utils.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.io.Serializable;
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2022-04-11
**/
@Entity
@Data
@Table(name="bus_domestic_order")
public class DomesticOrder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "order_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @Column(name = "outer_no")
    @ApiModelProperty(value = "订单号")
    private String outerNo;

    @Column(name = "wms_no")
    @ApiModelProperty(value = "WMS单号")
    private String wmsNo;

    @Column(name = "status",nullable = false)
    @NotNull
    @ApiModelProperty(value = "状态")
    private Integer status;

    @Column(name = "wms_status")
    @ApiModelProperty(value = "WMS状态")
    private String wmsStatus;

    @Column(name = "order_type" ,nullable = false)
    @ApiModelProperty(value = "订单类型")
    private String orderType;

    @Column(name = "up_status")
    @ApiModelProperty(value = "上游订单状态")
    private String upStatus;

    @Column(name = "customers_id")
    @ApiModelProperty(value = "客户ID")
    private Long customersId;

    @Column(name = "shop_id")
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;

    @Column(name = "platform_shop_id")
    @ApiModelProperty(value = "平台店铺ID")
    private String platformShopId;

    @Column(name = "platform_code")
    @ApiModelProperty(value = "电商平台代码")
    private String platformCode;

    @Column(name = "supplier_id")
    @ApiModelProperty(value = "承运商ID")
    private Long supplierId;

    @Column(name = "order_create_time")
    @ApiModelProperty(value = "订单创建时间")
    private Timestamp orderCreateTime;

    @Column(name = "err_msg")
    @ApiModelProperty(value = "异常信息")
    private String errMsg;

    @Column(name = "pre_sell")
    @ApiModelProperty(value = "是否预售")
    private String preSell;

    @Column(name = "freeze_reason")
    @ApiModelProperty(value = "冻结原因")
    private String freezeReason;

    @Column(name = "buyer_remark")
    @ApiModelProperty(value = "客户备注")
    private String buyerRemark;

    @Column(name = "logistics_no")
    @ApiModelProperty(value = "运单号")
    private String logisticsNo;

    @Column(name = "province")
    @ApiModelProperty(value = "省")
    private String province;

    @Column(name = "city")
    @ApiModelProperty(value = "市")
    private String city;

    @Column(name = "district")
    @ApiModelProperty(value = "区")
    private String district;

    @Column(name = "consignee_name")
    @ApiModelProperty(value = "收货人")
    private String consigneeName;

    @Column(name = "consignee_addr")
    @ApiModelProperty(value = "收货地址")
    private String consigneeAddr;

    @Column(name = "consignee_tel")
    @ApiModelProperty(value = "收货电话")
    private String consigneeTel;

    @Column(name = "add_mark")
    @ApiModelProperty(value = "大头笔")
    private String addMark;

    @Column(name = "pack_weight")
    @ApiModelProperty(value = "包裹重量")
    private String packWeight;

    @Column(name = "material_code")
    @ApiModelProperty(value = "包材编码")
    private String materialCode;

    @Column(name = "received_back_time")
    @ApiModelProperty(value = "接单回传时间")
    private Timestamp receivedBackTime;

    @Column(name = "check_pack_time")
    @ApiModelProperty(value = "审核通过时间")
    private Timestamp checkPackTime;

    @Column(name = "pre_handel_time")
    @ApiModelProperty(value = "预处理完成时间")
    private Timestamp preHandelTime;

    @Column(name = "pack_time")
    @ApiModelProperty(value = "打包时间")
    private Timestamp packTime;

    @Column(name = "pack_back_time")
    @ApiModelProperty(value = "打包完成回传时间")
    private Timestamp packBackTime;

    @Column(name = "weighing_time")
    @ApiModelProperty(value = "称重时间")
    private Timestamp weighingTime;

    @Column(name = "deliver_time")
    @ApiModelProperty(value = "出库时间")
    private Timestamp deliverTime;

    @Column(name = "cancel_time")
    @ApiModelProperty(value = "取消时间")
    private Timestamp cancelTime;

    @Column(name = "create_by",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @Column(name = "create_time",nullable = false)
    @NotNull
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @Transient
    private List<DomesticOrderDetails> items;

    public void copy(DomesticOrder source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        DomesticOrder order = (DomesticOrder) obj;
        if (StringUtils.equals(order.getOrderNo(), this.getOrderNo())) {
            return true;
        }
        return false;
    }
}