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
* @date 2021-03-27
**/
@Entity
@Data
@Table(name="bus_order_log")
public class OrderLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "order_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "订单ID")
    private Long orderId;

    @Column(name = "order_no",nullable = false)
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @Column(name = "opt_node",nullable = false)
    @ApiModelProperty(value = "操作节点")
    private String optNode;

    @Column(name = "req_msg",nullable = false)
    @ApiModelProperty(value = "请求报文")
    private String reqMsg;

    @Column(name = "res_msg",nullable = false)
    @ApiModelProperty(value = "返回报文")
    private String resMsg;

    @Column(name = "key_word")
    @ApiModelProperty(value = "关键字")
    private String keyWord;

    @Column(name = "host")
    @ApiModelProperty(value = "执行机器")
    private String host;

    @Column(name = "success")
    @ApiModelProperty(value = "是否成功")
    private String success;

    @Column(name = "msg")
    @ApiModelProperty(value = "描述")
    private String msg;

    @Column(name = "cost_time")
    @ApiModelProperty(value = "花费时间")
    private Long costTime;

    @Column(name = "cost_time_msg")
    @ApiModelProperty(value = "花费时间描述")
    private String costTimeMsg;

    @Column(name = "create_by",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @Column(name = "create_time",nullable = false)
    @NotNull
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @Column(name = "stack")
    @ApiModelProperty(value = "异常堆栈信息")
    private String stack;

    public OrderLog() {
    }

    /**
     * 不带KEYword
     * @param orderId
     * @param orderNo
     * @param optNode
     * @param reqMsg
     * @param resMsg
     * @param success
     * @param msg
     */
    public OrderLog(Long orderId, String orderNo, String optNode, String reqMsg, String resMsg, String success, String msg) {
        this.orderId = orderId;
        this.orderNo = orderNo;
        this.optNode = optNode;
        this.reqMsg = reqMsg;
        this.resMsg = resMsg;
        this.success = success;
        this.msg = msg;

        this.createBy = "SYSTEM";
        this.createTime = new Timestamp(System.currentTimeMillis());
    }

    public OrderLog(Long orderId, String orderNo, String optNode, String reqMsg, String resMsg, String success, String msg,String stack) {
        this.orderId = orderId;
        this.orderNo = orderNo;
        this.optNode = optNode;
        this.reqMsg = reqMsg;
        this.resMsg = resMsg;
        this.success = success;
        this.msg = msg;

        this.createBy = "SYSTEM";
        this.createTime = new Timestamp(System.currentTimeMillis());
        this.stack=stack;
    }

    public OrderLog(@NotNull Long orderId, String orderNo, String optNode, String success, String msg) {
        this.orderId = orderId;
        this.orderNo = orderNo;
        this.optNode = optNode;
        this.success = success;
        this.msg = msg;

        this.host = StringUtils.getLocalIp();
        this.createBy = "SYSTEM";
        this.createTime = new Timestamp(System.currentTimeMillis());
    }

    public OrderLog(@NotNull Long orderId, String orderNo, String optNode, String success, String msg,String stack) {
        this.orderId = orderId;
        this.orderNo = orderNo;
        this.optNode = optNode;
        this.success = success;
        this.msg = msg;

        this.host = StringUtils.getLocalIp();
        this.createBy = "SYSTEM";
        this.createTime = new Timestamp(System.currentTimeMillis());
        this.stack=stack;
    }

    public void copy(OrderLog source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
