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
import me.zhengjie.rest.model.douyin.CreateWarehouseFeeOrder;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2023-02-07
**/
@Entity
@Data
@Table(name="bus_dy_cangzu_fee")
public class DyCangzuFee implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "ws_store_no")
    @ApiModelProperty(value = "仓租单号")
    private String wsStoreNo;

    @Column(name = "warehouse_code")
    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;

    @Column(name = "fee_date")
    @ApiModelProperty(value = "计费日期")
    private Long feeDate;

    @Column(name = "shop_id")
    @ApiModelProperty(value = "店铺id")
    private String shopId;

    @Column(name = "owner_id")
    @ApiModelProperty(value = "货主id")
    private String ownerId;

    @Column(name = "owner_type")
    @ApiModelProperty(value = "货主类型")
    private String ownerType;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @Column(name = "is_push")
    @ApiModelProperty(value = "是否推送")
    private String isPush;

    public DyCangzuFee(CreateWarehouseFeeOrder order) {
        this.wsStoreNo = order.getWsStoreNo();
        this.warehouseCode = order.getWarehouseCode();
        this.feeDate = order.getFeeDate();
        this.shopId=order.getShopId()+"";
        this.ownerId = order.getOwnerId()+"";
        this.ownerType = order.getOwnerType();
        this.createTime = new Timestamp(System.currentTimeMillis());
        this.isPush = "0";
    }

    public DyCangzuFee(){}

    public void copy(DyCangzuFee source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}