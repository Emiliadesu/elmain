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
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-09-26
**/
@Entity
@Data
@Table(name="bus_dy_stock_taking")
public class DyStockTaking implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "shop_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "店铺id")
    private Long shopId;

    @Column(name = "taking_type",nullable = false)
    @NotNull
    @ApiModelProperty(value = "盘点类型")
    private Integer takingType;

    @Column(name = "occurrence_time")
    @ApiModelProperty(value = "盘点完成时间")
    private Long occurrenceTime;

    @Column(name = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Column(name = "create_user_id")
    @ApiModelProperty(value = "创建人")
    private Long createUserId;

    @Column(name = "is_success")
    @ApiModelProperty(value = "是否推送")
    private String isSuccess;

    @Column(name = "status")
    @ApiModelProperty(value = "状态")
    private String status;

    @Column(name = "reject_reason")
    @ApiModelProperty(value = "驳回原因")
    private String rejectReason;

    @Column(name = "inbound_order_no")
    @ApiModelProperty(value = "入库单")
    private String inboundOrderNo;

    @Column(name = "adjust_biz_type")
    @ApiModelProperty(value = "调整类型" +
            "201 库内商品正转残" +
            "202 库内商品残转正" +
            "203 库存批次属性转移" +
            "204 库内商品过期转残")
    private String adjustBizType;

    @Column(name = "idempotent_no")
    @ApiModelProperty(value = "幂等单号")
    private String idempotentNo;

    @Column(name = "suc_time")
    @ApiModelProperty(value = "审核通过时间")
    private Timestamp sucTime;

    @Transient
    private List<DyStockTakingDetail> itemList;

    public void copy(DyStockTaking source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
