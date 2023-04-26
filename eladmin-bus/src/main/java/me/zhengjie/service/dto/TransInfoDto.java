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
* @date 2021-09-04
**/
@Data
public class TransInfoDto implements Serializable {

    /** ID */
    private Long id;

    /** 清关ID */
    private Long clearId;

    /** 单据编号 */
    private String orderNo;

    /** 清关单号 */
    private String clearNo;

    /** 状态 */
    private Integer status;

    /** 贸易类型 */
    private String tradeType;

    /** 客户ID */
    private Long customersId;

    /** 店铺ID */
    private Long shopId;

    /** 预估SKU数量 */
    private Integer skuNum;

    /** 预估件数 */
    private Integer totalNum;

    /** 打包方式 */
    private String packWay;

    /** 打包数量 */
    private Integer packNum;

    /** 排车方 */
    private String planCarType;

    /** 是否拼车 */
    private String shareFlag;

    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    private Timestamp createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    private Timestamp updateTime;
}