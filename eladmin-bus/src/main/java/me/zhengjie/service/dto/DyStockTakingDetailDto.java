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
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-09-26
**/
@Data
public class DyStockTakingDetailDto implements Serializable {

    private Long id;

    /** 盘点主id */
    private Long takingId;

    /** 货号 */
    private String goodsNo;

    /** 质量等级 */
    private Integer qualityGrade;

    /** 数量 */
    private Integer quantity;

    /** 盘点原因 */
    private Integer reasonCode;

    /** 具体原因 */
    private String reasonMsg;

    private String duty;

    private String remark;

    private String evidence;
}