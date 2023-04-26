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
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2021-04-03
**/
@Entity
@Data
@Table(name="bus_order_deliver_log")
public class OrderDeliverLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "shop_id")
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;

    @Column(name = "platform_code")
    @ApiModelProperty(value = "电商平台")
    private String platformCode;

    @Column(name = "user_name")
    @ApiModelProperty(value = "扫描人")
    private String userName;

    @Column(name = "order_no")
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @Column(name = "mail_no")
    @ApiModelProperty(value = "运单号")
    private String mailNo;

    @Column(name = "weight")
    @ApiModelProperty(value = "重量")
    private String weight;

    @Column(name = "rule_code")
    @ApiModelProperty(value = "规则代码")
    private String ruleCode;

    @Column(name = "req_msg")
    @ApiModelProperty(value = "请求报文")
    private String reqMsg;

    @Column(name = "res_msg")
    @ApiModelProperty(value = "返回报文")
    private String resMsg;

    @Column(name = "host")
    @ApiModelProperty(value = "执行机器")
    private String host;

    @Column(name = "cost_time")
    @ApiModelProperty(value = "花费时间")
    private Long costTime;

    @Column(name = "create_time",nullable = false)
    @NotNull
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    public OrderDeliverLog() {
    }

    public OrderDeliverLog(Long shopId, String platformCode, String userName, String orderNo, String mailNo, String ruleCode, String weight, String reqMsg, String resMsg, Long costTime) {
        this.shopId = shopId;
        this.platformCode = platformCode;
        this.userName = userName;
        this.orderNo = orderNo;
        this.mailNo = mailNo;
        this.ruleCode = ruleCode;
        this.weight = weight;
        this.reqMsg = reqMsg;
        this.resMsg = resMsg;
        this.costTime = costTime;
        this.createTime = new Timestamp(System.currentTimeMillis());
        this.host = StringUtils.getLocalIp();
    }

    public void copy(OrderDeliverLog source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}