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

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2021-11-13
**/
@Entity
@Data
@Table(name="bus_deposit_log")
public class DepositLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "deposit_id")
    @ApiModelProperty(value = "保证金ID")
    private Long depositId;

    @Column(name = "type")
    @ApiModelProperty(value = "变动类型")
    private String type;

    @Column(name = "change_amount")
    @ApiModelProperty(value = "金额")
    private BigDecimal changeAmount;

    @Column(name = "current_amount")
    @ApiModelProperty(value = "当前金额")
    private BigDecimal currentAmount;

    @Column(name = "create_by")
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @Column(name = "order_no")
    @ApiModelProperty(value = "关联订单")
    private String orderNo;

    public DepositLog() {
    }

    public DepositLog(Long depositId, String type, BigDecimal changeAmount, BigDecimal currentAmount, String createBy, Timestamp createTime, String orderNo) {
        this.depositId = depositId;
        this.type = type;
        this.changeAmount = changeAmount;
        this.currentAmount = currentAmount;
        this.createBy = createBy;
        this.createTime = createTime;
        this.orderNo = orderNo;
    }

    public void copy(DepositLog source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}