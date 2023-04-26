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
* @date 2022-01-11
**/
@Entity
@Data
@Table(name="bus_business_log")
public class BusinessLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "type")
    @ApiModelProperty(value = "类型")
    private String type;

    @Column(name = "direction")
    @ApiModelProperty(value = "方向")
    private String direction;

    @Column(name = "description")
    @ApiModelProperty(value = "描述")
    private String description;

    @Column(name = "key_word")
    @ApiModelProperty(value = "关键字")
    private String keyWord;

    @Column(name = "req_url")
    @ApiModelProperty(value = "请求地址")
    private String reqUrl;

    @Column(name = "request_ip")
    @ApiModelProperty(value = "请求IP")
    private String requestIp;

    @Column(name = "req_params")
    @ApiModelProperty(value = "请求参数")
    private String reqParams;

    @Column(name = "res_params")
    @ApiModelProperty(value = "返回参数")
    private String resParams;

    @Column(name = "host")
    @ApiModelProperty(value = "处理机器")
    private String host;

    @Column(name = "time")
    @ApiModelProperty(value = "花费时间")
    private Long time;

    @Column(name = "create_time")
    @ApiModelProperty(value = "处理时间")
    private Timestamp createTime;

    public void copy(BusinessLog source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}