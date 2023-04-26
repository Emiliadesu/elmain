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
@Table(name="bus_wms_stock_log")
public class WmsStockLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "order_sn")
    @ApiModelProperty(value = "出入库单号")
    private String orderSn;

    @Column(name = "type")
    @ApiModelProperty(value = "库存操作，0入库，1出库")
    private String type;

    @Column(name = "request")
    @ApiModelProperty(value = "请求报文")
    private String request;

    @Column(name = "response")
    @ApiModelProperty(value = "响应报文")
    private String response;

    @Column(name = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Column(name = "status_text")
    @ApiModelProperty(value = "操作状态")
    private String statusText;

    @Column(name = "operation_time")
    @ApiModelProperty(value = "操作时间")
    private Timestamp operationTime;

    @Column(name = "operation_user")
    @ApiModelProperty(value = "操作人")
    private String operationUser;

    @Column(name = "status")
    @ApiModelProperty(value = "操作状态码")
    private String status;

    @Column(name = "is_success")
    @ApiModelProperty(value = "是否成功")
    private String isSuccess;

    public void copy(WmsStockLog source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}