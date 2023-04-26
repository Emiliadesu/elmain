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
* @date 2021-03-27
**/
@Data
public class MqLogDto implements Serializable {

    /** ID */
    private Long id;

    /** topic */
    private String topic;

    /** tag */
    private String tag;

    private String msgId;

    /** key */
    private String msgKey;

    /** 消息内容 */
    private String body;

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