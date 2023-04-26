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
* @date 2021-04-03
**/
@Data
public class QueryMftLogDto implements Serializable {

    /** ID */
    private Long id;

    /** 开始时间 */
    private Timestamp startTime;

    /** 结束时间 */
    private Timestamp endTime;

    /** 页面 */
    private Integer pageNo;

    /** 页大小 */
    private Integer pageSize;

    /** 是否有下一页 */
    private String nextPage;

    /** 结果 */
    private String result;

    /** 返回报文 */
    private String resMsg;

    /** 执行机器 */
    private String host;

    /** 创建时间 */
    private Timestamp createTime;
}