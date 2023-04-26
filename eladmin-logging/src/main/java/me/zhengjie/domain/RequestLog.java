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
* @author luob
* @date 2021-04-28
**/
@Entity
@Data
@Table(name="bus_request_log")
public class RequestLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "bus_type")
    @ApiModelProperty(value = "业务类型")
    private String busType;

    @Column(name = "bus_type_name")
    @ApiModelProperty(value = "业务类型描述")
    private String busTypeName;

    @Column(name = "bus_no")
    @ApiModelProperty(value = "业务单号")
    private Long busNo;

    @Column(name = "req_url")
    @ApiModelProperty(value = "请求地址")
    private String reqUrl;

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
    @ApiModelProperty(value = "请求耗时")
    private Long costTime;

    @Column(name = "create_by")
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @Column(name = "create_time",nullable = false)
    @NotNull
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    public RequestLog() {
    }

    public RequestLog(Long costTime) {

        this.costTime = costTime;
    }

    public void copy(RequestLog source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}