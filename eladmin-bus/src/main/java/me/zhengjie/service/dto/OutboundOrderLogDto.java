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
package me.zhengjie.service.dto;

import lombok.Data;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2021-07-13
**/
@Data
public class OutboundOrderLogDto implements Serializable {

    /** ID */
    private Long id;

    /** 订单ID */
    private Long orderId;

    /** 订单号 */
    private String orderNo;

    /** 操作节点 */
    private String optNode;

    /** 请求报文 */
    private String reqMsg;

    /** 返回报文 */
    private String resMsg;

    /** 关键字 */
    private String keyWord;

    /** 执行机器 */
    private String host;

    /** 是否成功 */
    private String success;

    /** 描述 */
    private String msg;

    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    private Timestamp createTime;
}