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
* @date 2021-12-02
**/
@Data
public class LogisticsInfoDto implements Serializable {

    /** ID */
    private Long id;

    /** 名称 */
    private String name;

    /** 代码 */
    private String code;

    /** 海关备案名称 */
    private String customsName;

    /** 海关备案代码 */
    private String customsCode;

    /** 跨境购代码 */
    private String kjgCode;

    private String kjgName;

    /** 默认字段1(抖音代码) */
    private String default01;

    /** 默认字段2(拼多多代码) */
    private String default02;

    /** 默认字段3 */
    private String default03;

    /** 默认字段4 */
    private String default04;

    /** 默认字段5 */
    private String default05;

    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    private Timestamp createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    private Timestamp updateTime;
}