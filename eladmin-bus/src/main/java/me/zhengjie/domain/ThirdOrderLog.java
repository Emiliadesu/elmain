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
* @author 王淼
* @date 2020-12-21
**/
@Entity
@Data
@Table(name="bus_third_order_log")
public class ThirdOrderLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "method_name")
    @ApiModelProperty(value = "接口名")
    private String methodName;

    @Column(name = "is_success")
    @ApiModelProperty(value = "是否成功")
    private String isSuccess;

    @Column(name = "err_msg")
    @ApiModelProperty(value = "错误信息")
    private String errMsg;

    @Column(name = "code")
    @ApiModelProperty(value = "响应码")
    private Integer code;

    @Column(name = "request")
    @ApiModelProperty(value = "请求报文")
    private String request;

    @Column(name = "response")
    @ApiModelProperty(value = "返回报文")
    private String response;

    @Column(name = "create_time")
    @ApiModelProperty(value = "请求时间")
    private Timestamp createTime;

    @Column(name = "shop_id")
    @ApiModelProperty(value = "店铺id")
    private String shopId;

    @Column(name = "platform_code")
    @ApiModelProperty(value = "platformCode")
    private String platformCode;

    public void copy(ThirdOrderLog source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}