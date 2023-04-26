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
* @date 2021-10-04
**/
@Data
public class SortingRuleDto implements Serializable {

    /** ID */
    private Long id;

    /** 规则名称 */
    private String ruleName;

    /** 规则代码 */
    private String ruleCode;

    /** 规则代码1(跨境购) */
    private String ruleCode1;

    /** 规则代码2(菜鸟) */
    private String ruleCode2;

    /** 状态 */
    private String status;

    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    private Timestamp createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    private Timestamp updateTime;
}