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
@Table(name="bus_query_mft_log")
public class QueryMftLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "start_time")
    @ApiModelProperty(value = "开始时间")
    private Timestamp startTime;

    @Column(name = "end_time")
    @ApiModelProperty(value = "结束时间")
    private Timestamp endTime;

    @Column(name = "page_no")
    @ApiModelProperty(value = "页面")
    private Integer pageNo;

    @Column(name = "page_size")
    @ApiModelProperty(value = "页大小")
    private Integer pageSize;

    @Column(name = "next_page")
    @ApiModelProperty(value = "是否有下一页")
    private String nextPage;

    @Column(name = "result")
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

    public QueryMftLog() {
    }

    public QueryMftLog(Timestamp startTime, Timestamp endTime, Integer pageNo, Integer pageSize, String nextPage, String result, String resMsg) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.nextPage = nextPage;
        this.result = result;
        this.resMsg = resMsg;
        this.createTime = new Timestamp(System.currentTimeMillis());
        this.host = StringUtils.getLocalIp();
    }

    public void copy(QueryMftLog source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}