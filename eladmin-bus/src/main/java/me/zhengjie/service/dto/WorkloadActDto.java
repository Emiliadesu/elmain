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
* @author KC
* @date 2021-01-13
**/
@Data
public class WorkloadActDto implements Serializable {

    /** ID */
    private Long id;

    /** 单号 */
    private String orderSn;

    /** 工作类型 */
    private String workType;

    /** 员工账号 */
    private String userId;

    /** 新增时间 */
    private Timestamp addTime;
}