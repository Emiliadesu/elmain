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
* @author leningzhou
* @date 2022-01-17
**/
@Entity
@Data
@Table(name="bus_gift_log")
public class GiftLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "gift_id")
    @ApiModelProperty(value = "赠品ID")
    private Long giftId;

    @Column(name = "gift_code")
    @ApiModelProperty(value = "赠品条码")
    private String giftCode;

    @Column(name = "opt_node")
    @ApiModelProperty(value = "操作节点")
    private String optNode;

    @Column(name = "req_msg")
    @ApiModelProperty(value = "请求报文")
    private String reqMsg;

    @Column(name = "res_msg")
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
    @ApiModelProperty(value = "时间描述")
    private String costTimeMsg;

    @Column(name = "create_by")
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @Column(name = "create_time",nullable = false)
    @NotNull
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @Column(name = "stack")
    @ApiModelProperty(value = "异常堆栈信息")
    private String stack;

    public GiftLog(Long giftId, String giftCode,String optNode,  String reqMsg, String resMsg, String success, String msg, String createBy) {
        this.giftId = giftId;
        this.giftCode = giftCode;
        this.optNode = optNode;
        this.reqMsg = reqMsg;
        this.resMsg = resMsg;
        this.success = success;
        this.msg = msg;
        this.createBy = createBy;
        this.host = StringUtils.getLocalIp();
        this.createTime = new Timestamp(System.currentTimeMillis());
    }

    public GiftLog(){

    }

    public void copy(GiftLog source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}