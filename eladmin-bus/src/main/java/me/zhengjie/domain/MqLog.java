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
* @date 2021-03-27
**/
@Entity
@Data
@Table(name="bus_mq_log")
public class MqLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "topic",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "topic")
    private String topic;

    @Column(name = "tag",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "tag")
    private String tag;

    @Column(name = "msg_id")
    @ApiModelProperty(value = "msgId")
    private String msgId;

    @Column(name = "msg_key")
    @ApiModelProperty(value = "key")
    private String msgKey;

    @Column(name = "body",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "消息内容")
    private String body;

    @Column(name = "host")
    @ApiModelProperty(value = "执行机器")
    private String host;

    @Column(name = "success")
    @ApiModelProperty(value = "是否成功")
    private String success;

    @Column(name = "msg")
    @ApiModelProperty(value = "描述")
    private String msg;

    @Column(name = "create_by",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @Column(name = "create_time",nullable = false)
    @NotNull
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    public void copy(MqLog source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }

    public MqLog() {
    }

    public MqLog(@NotBlank String topic, @NotBlank String tag, String msgId,  String msgKey, @NotBlank String body, String success, String msg) {
        this.topic = topic;
        this.tag = tag;
        this.msgId = msgId;
        this.msgKey = msgKey;
        this.body = body;
        this.host = StringUtils.getLocalIp();;
        this.success = success;
        this.msg = msg;
        this.createBy = "SYSTEM";
        this.createTime = new Timestamp(System.currentTimeMillis());
    }
}