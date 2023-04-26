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
* @date 2021-04-21
**/
@Entity
@Data
@Table(name="bus_sku_log")
public class SkuLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "sku_id")
    @ApiModelProperty(value = "订单ID")
    private Long skuId;

    @Column(name = "opt_node")
    @ApiModelProperty(value = "操作节点")
    private String optNode;

    @Column(name = "req_msg")
    @ApiModelProperty(value = "请求报文")
    private String reqMsg;

    @Column(name = "res_msg")
    @ApiModelProperty(value = "返回报文")
    private String resMsg;

    @Column(name = "host")
    @ApiModelProperty(value = "执行机器")
    private String host;

    @Column(name = "success")
    @ApiModelProperty(value = "是否成功")
    private String success;

    @Column(name = "msg")
    @ApiModelProperty(value = "描述")
    private String msg;

    @Column(name = "create_by")
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @Column(name = "create_time",nullable = false)
    @NotNull
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    public void copy(SkuLog source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }

    public SkuLog() {
    }

    public SkuLog(Long skuId, String optNode, String reqMsg, String resMsg, String createBy) {
        this.skuId = skuId;
        this.optNode = optNode;
        this.reqMsg = reqMsg;
        this.resMsg = resMsg;
        this.success = "1";
        this.host = StringUtils.getLocalIp();
        this.createBy = createBy;
        this.setCreateTime(new Timestamp(System.currentTimeMillis()));
    }
}