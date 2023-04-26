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

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2022-04-06
**/
@Data
public class ReturnGatherDto implements Serializable {

    /** ID */
    private Long id;

    /** 提总单号 */
    private String gatherNo;

    /** WMS单号 */
    private String wmsNo;

    /** 客户ID */
    private Long customersId;

    /** 店铺ID */
    private Long shopId;

    /** 客户名称 */
    private String customersName;

    /** 店铺名称 */
    private String shopName;

    /** SKU数量 */
    private Integer skuNum;

    /** 总数量 */
    private Integer totalNum;

    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    private Timestamp createTime;

    /** 预处理完成时间 */
    private Timestamp preHandleTime;

    /** 关单时间 */
    private Timestamp closeTime;

    private String returnIds;

    private Integer status;

    private Integer orderNum;

    private Integer returnTotalNum;

    private Integer returnTotalOrder;

}