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
import me.zhengjie.base.BaseUpdateEntity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2021-03-11
**/
@Entity
@Data
@Table(name="bus_clear_opt_log")
public class ClearOptLog extends BaseUpdateEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "clear_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "清关ID")
    private Long clearId;

    @Column(name = "opt_node",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "操作节点")
    private String optNode;

    @Column(name = "opt_time",nullable = false)
    @NotNull
    @ApiModelProperty(value = "操作时间")
    private Timestamp optTime;

    @Column(name = "req_msg")
    @ApiModelProperty(value = "请求报文")
    private String reqMsg;

    @Column(name = "res_msg")
    @ApiModelProperty(value = "返回报文")
    private String resMsg;

    @Column(name = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    public ClearOptLog() {
    }

    public ClearOptLog(@NotNull Long clearId, @NotBlank String optNode, @NotNull Timestamp optTime, String reqMsg, String resMsg) {
        this.clearId = clearId;
        this.optNode = optNode;
        this.optTime = optTime;
        this.reqMsg = reqMsg;
        this.resMsg = resMsg;
    }

    public void copy(ClearOptLog source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}