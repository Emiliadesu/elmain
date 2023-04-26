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
import me.zhengjie.base.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.io.Serializable;
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2021-09-04
**/
@Entity
@Data
@Table(name="bus_trans_info")
public class TransInfo extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "clear_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "清关ID")
    private Long clearId;

    @Column(name = "order_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "单据编号")
    private String orderNo;

    @Column(name = "clear_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "清关单号")
    private String clearNo;

    @Column(name = "status",nullable = false)
    @NotNull
    @ApiModelProperty(value = "状态")
    private Integer status;

    @Column(name = "trade_type")
    @ApiModelProperty(value = "贸易类型")
    private String tradeType;

    @Column(name = "customers_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "客户ID")
    private Long customersId;

    @Column(name = "shop_id")
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;

    @Column(name = "sku_num")
    @ApiModelProperty(value = "预估SKU数量")
    private Integer skuNum;

    @Column(name = "total_num")
    @ApiModelProperty(value = "预估件数")
    private Integer totalNum;

    @Column(name = "pack_way")
    @ApiModelProperty(value = "打包方式")
    private String packWay;

    @Column(name = "pack_num")
    @ApiModelProperty(value = "打包数量")
    private Integer packNum;

    @Column(name = "plan_car_type")
    @ApiModelProperty(value = "排车方")
    private String planCarType;

    @Column(name = "share_flag")
    @ApiModelProperty(value = "是否拼车")
    private String shareFlag;

    @Column(name = "order_source")
    @ApiModelProperty(value = "单据来源")
    private String orderSource;

    @Transient
    private List<TransDetails> details;

    public void copy(TransInfo source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}