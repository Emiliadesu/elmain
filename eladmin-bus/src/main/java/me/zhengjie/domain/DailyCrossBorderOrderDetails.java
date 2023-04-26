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
* @author leningzhou
* @date 2022-07-04
**/
@Entity
@Data
@Table(name="bus_daily_cross_border_order_details")
public class DailyCrossBorderOrderDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "order_id")
    @ApiModelProperty(value = "订单ID")
    private Long orderId;

    @Column(name = "detail_id")
    @ApiModelProperty(value = "明细源ID")
    private Long detailId;

    @Column(name = "order_no")
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @Column(name = "cross_border_no")
    @ApiModelProperty(value = "交易单号")
    private String crossBorderNo;

    @Column(name = "status")
    @ApiModelProperty(value = "状态")
    private String status;

    @Column(name = "customers_id")
    @ApiModelProperty(value = "客户ID")
    private Long customersId;

    @Column(name = "shop_id")
    @ApiModelProperty(value = "店铺ID")
    private String shopId;

    @Column(name = "customers_name")
    @ApiModelProperty(value = "客户名称")
    private String customersName;

    @Column(name = "shop_name")
    @ApiModelProperty(value = "店铺名称")
    private String shopName;

    @Column(name = "day_time")
    @ApiModelProperty(value = "时间")
    private String dayTime;

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

    @Column(name = "consignee_addr")
    @ApiModelProperty(value = "收货地址")
    private String consigneeAddr;

    @Column(name = "pay_time")
    @ApiModelProperty(value = "支付时间")
    private Timestamp payTime;

    @Column(name = "order_create_time")
    @ApiModelProperty(value = "订单创建时间")
    private Timestamp orderCreateTime;

    @Column(name = "deliver_time")
    @ApiModelProperty(value = "出库时间")
    private Timestamp deliverTime;

    @Column(name = "logistics_collect")
    @ApiModelProperty(value = "是否揽收")
    private Long logisticsCollect;

    @Column(name = "logistics_collect_time")
    @ApiModelProperty(value = "快递揽收时间")
    private Timestamp logisticsCollectTime;

    @Column(name = "goods_id")
    @ApiModelProperty(value = "货品ID")
    private Long goodsId;

    @Column(name = "goods_code")
    @ApiModelProperty(value = "货品编码")
    private String goodsCode;

    @Column(name = "goods_name")
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @Column(name = "font_goods_name")
    @ApiModelProperty(value = "商品海关备案名")
    private String fontGoodsName;

    @Column(name = "goods_no")
    @ApiModelProperty(value = "海关货号")
    private String goodsNo;

    @Column(name = "bar_code")
    @ApiModelProperty(value = "条码")
    private String barCode;

    @Column(name = "qty")
    @ApiModelProperty(value = "数量")
    private String qty;

    @Column(name = "payment")
    @ApiModelProperty(value = "实付总价")
    private String payment;

    @Column(name = "tax_amount")
    @ApiModelProperty(value = "总税额")
    private String taxAmount;

    @Column(name = "is_collect")
    @ApiModelProperty(value = "是否揽收")
    private String isCollect;

    public void copy(DailyCrossBorderOrderDetails source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}