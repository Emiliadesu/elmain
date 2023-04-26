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
* @author luob
* @date 2022-04-06
**/
@Entity
@Data
@Table(name="bus_return_gather")
public class ReturnGather implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "gather_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "提总单号")
    private String gatherNo;

    @Column(name = "wms_no")
    @ApiModelProperty(value = "WMS单号")
    private String wmsNo;

    @Column(name = "status",nullable = false)
    @NotNull
    @ApiModelProperty(value = "状态")
    private Integer status;

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

    @Column(name = "return_ids")
    @ApiModelProperty(value = "退货单id明细")
    private String returnIds;

    @Column(name = "order_num")
    @ApiModelProperty(value = "退货单数")
    private Integer orderNum;

    @Column(name = "sku_num")
    @ApiModelProperty(value = "SKU数量")
    private Integer skuNum;

    @Column(name = "total_num")
    @ApiModelProperty(value = "总数量")
    private Integer totalNum;

    @Column(name = "return_total_num")
    @ApiModelProperty(value = "退货单总件数")
    private Integer returnTotalNum;

    @Column(name = "return_total_order")
    @ApiModelProperty(value = "退货单总订单数")
    private Integer returnTotalOrder;

    @Column(name = "create_by")
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @Column(name = "create_time",nullable = false)
    @NotNull
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @Column(name = "pre_handle_time")
    @ApiModelProperty(value = "预处理完成时间")
    private Timestamp preHandleTime;

    @Column(name = "close_time")
    @ApiModelProperty(value = "关单时间")
    private Timestamp closeTime;

    @Transient
    private List<ReturnGatherDetail> itemList;

    public void copy(ReturnGather source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}