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
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author leningzhou
* @date 2022-07-04
**/
@Entity
@Data
@Table(name="bus_daily_cross_border_order")
public class DailyCrossBorderOrder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "order_id")
    @ApiModelProperty(value = "订单id")
    private Long orderId;

    @Column(name = "order_no")
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @Column(name = "cross_border_no")
    @ApiModelProperty(value = "交易号")
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

    @Column(name = "pre_sell")
    @ApiModelProperty(value = "是否预售")
    private String preSell;

    @Column(name = "declare_no")
    @ApiModelProperty(value = "申报单号")
    private String declareNo;

    @Column(name = "invt_no")
    @ApiModelProperty(value = "总署清单编号")
    private String invtNo;

    @Column(name = "wave_no")
    @ApiModelProperty(value = "波次号")
    private String waveNo;

    @Column(name = "so_no")
    @ApiModelProperty(value = "SO单号")
    private String soNo;

    @Column(name = "declare_status")
    @ApiModelProperty(value = "申报状态")
    private String declareStatus;

    @Column(name = "declare_msg")
    @ApiModelProperty(value = "申报信息")
    private String declareMsg;

    @Column(name = "freeze_reason")
    @ApiModelProperty(value = "冻结原因")
    private String freezeReason;

    @Column(name = "post_fee")
    @ApiModelProperty(value = "运费")
    private String postFee;

    @Column(name = "payment")
    @ApiModelProperty(value = "实付金额")
    private String payment;

    @Column(name = "tax_amount")
    @ApiModelProperty(value = "预估税费")
    private String taxAmount;

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

    @Column(name = "consignee_tel")
    @ApiModelProperty(value = "收货电话")
    private String consigneeTel;

    @Column(name = "pay_time")
    @ApiModelProperty(value = "支付时间")
    private Timestamp payTime;

    @Column(name = "order_create_time")
    @ApiModelProperty(value = "订单创建时间")
    private Timestamp orderCreateTime;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @Column(name = "received_back_time")
    @ApiModelProperty(value = "接单回传时间")
    private Timestamp receivedBackTime;

    @Column(name = "clear_start_time")
    @ApiModelProperty(value = "清关开始时间")
    private Timestamp clearStartTime;

    @Column(name = "clear_start_back_time")
    @ApiModelProperty(value = "清关开始回传时间")
    private Timestamp clearStartBackTime;

    @Column(name = "clear_success_time")
    @ApiModelProperty(value = "清关完成时间")
    private Timestamp clearSuccessTime;

    @Column(name = "clear_success_back_time")
    @ApiModelProperty(value = "清关完成回传时间")
    private Timestamp clearSuccessBackTime;

    @Column(name = "deliver_time")
    @ApiModelProperty(value = "出库时间")
    private Timestamp deliverTime;

    @Column(name = "logistics_collect_time")
    @ApiModelProperty(value = "快递揽收时间")
    private Timestamp logisticsCollectTime;

    @Column(name = "cancel_time")
    @ApiModelProperty(value = "取消时间")
    private Timestamp cancelTime;

    @Column(name = "create_by")
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @Column(name = "customers_name")
    @ApiModelProperty(value = "客户名称")
    private String customersName;

    @Column(name = "shop_name")
    @ApiModelProperty(value = "店铺名称")
    private String shopName;

    @Column(name = "day_time")
    @ApiModelProperty(value = "时间")
    private String dayTime;

    @Column(name = "is_collect")
    @ApiModelProperty(value = "是否揽收")
    private String isCollect;

    @Transient
    private List<CrossBorderOrderDetails> itemList;

    public List<CrossBorderOrderDetails> getItemList() {
        return itemList;
    }

    public void setItemList(List<CrossBorderOrderDetails> itemList) {
        this.itemList = itemList;
    }

    public void copy(DailyCrossBorderOrder source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}