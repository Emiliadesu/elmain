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
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author leningzhou
* @date 2022-01-26
**/
@Data
public class PackageInfoDto implements Serializable {

    /** ID */
    private Long id;

    /** 包材编码 */
    private String packageCode;

    /** 包材名称 */
    private String packageName;

    /** 长 */
    private BigDecimal packLength;

    /** 宽 */
    private BigDecimal packWidth;

    /** 高 */
    private BigDecimal packHeight;

    /** 重量 */
    private BigDecimal weight;

    private String addValue;

    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    private Timestamp createTime;

    /** 修改人 */
    private String updateBy;

    /** 修改时间 */
    private Timestamp updateTime;

    private String packageType;

    private String platformCode;
}