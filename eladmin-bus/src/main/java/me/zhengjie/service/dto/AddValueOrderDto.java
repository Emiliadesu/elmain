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
* @author AddValueOrder
* @date 2021-08-05
**/
@Data
public class AddValueOrderDto implements Serializable {

    private Long id;

    /** 客户 */
    private Long customersId;

    /** 店铺 */
    private Long shopId;

    /** 状态 */
    private Integer status;

    /** 外部单号 */
    private String outOrderNo;

    /** 仓区 */
    private Integer warehouse;
    /** 类型 */
    private Integer type;

    /** 关联单号 */
    private String refNo;

    /** 备注 */
    private String remark;

    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    private Timestamp createTime;

    /** 更新人 */
    private String updateBy;

    /** 更新时间 */
    private Timestamp updateTime;

    /** 合同费目编码 */
    private String addCode;

    /** 合同费目名称 */
    private String addName;

    private String finishTime;

    private Integer refType;

    private String orderNo;
}