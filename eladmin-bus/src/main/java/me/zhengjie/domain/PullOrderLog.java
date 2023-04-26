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
* @date 2021-04-01
**/
@Entity
@Data
@Table(name="bus_pull_order_log")
public class PullOrderLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "shop_id",nullable = false)
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;

    @Column(name = "start_time",nullable = false)
    @ApiModelProperty(value = "开始时间")
    private Timestamp startTime;

    @Column(name = "end_time",nullable = false)
    @ApiModelProperty(value = "结束时间")
    private Timestamp endTime;

    @Column(name = "page_no",nullable = false)
    @ApiModelProperty(value = "页码")
    private Integer pageNo;

    @Column(name = "page_size",nullable = false)
    @ApiModelProperty(value = "页大小")
    private Integer pageSize;

    @Column(name = "next_page",nullable = false)
    @ApiModelProperty(value = "页大小")
    private String nextPage;

    @Column(name = "total",nullable = false)
    @ApiModelProperty(value = "总数")
    private String total;

    @Column(name = "result",nullable = false)
    @ApiModelProperty(value = "结果")
    private String result;

    @Column(name = "res_msg")
    @ApiModelProperty(value = "返回报文")
    private String resMsg;

    @Column(name = "host")
    @ApiModelProperty(value = "执行机器")
    private String host;

    @Column(name = "create_time",nullable = false)
    @NotNull
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    public PullOrderLog() {
    }

    public PullOrderLog(Long shopId, Timestamp startTime, Timestamp endTime, Integer pageNo, Integer pageSize, String nextPage, String total, String result, String resMsg) {
        this.shopId = shopId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.nextPage = nextPage;
        this.total = total;
        this.result = result;
        this.resMsg = resMsg;
        this.createTime = new Timestamp(System.currentTimeMillis());
        this.host = StringUtils.getLocalIp();
    }

    public void copy(PullOrderLog source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}