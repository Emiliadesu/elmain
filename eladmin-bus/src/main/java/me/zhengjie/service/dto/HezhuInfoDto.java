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
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2021-08-26
**/
@Data
public class HezhuInfoDto implements Serializable {

    /** ID */
    private Long id;

    /** 单据编号 */
    private String orderNo;

    /** 状态 */
    private Integer status;

    /** 海关状态 */
    private Integer customsStatus;

    /** 客户ID */
    private Long customersId;

    /** 店铺ID */
    private Long shopId;

    /** 清关抬头ID */
    private Long clearCompanyId;

    /** 业务类型 */
    private String busType;

    /** 预估SKU数量 */
    private Integer skuNum;

    /** 预估件数 */
    private Integer totalNum;

    /** 毛重 */
    private BigDecimal grossWeight;

    /** 净重 */
    private BigDecimal netWeight;

    /** 入库仓 */
    private String inWareHose;

    /** 报关单号 */
    private String entryNo;

    /** 报检单号 */
    private String declNo;

    /** 监管方式 */
    private String regulatoryWay;

    /** 报关类型 */
    private String clearType;

    /** QD单号 */
    private String qdCode;

    /** 关联单证编码 */
    private String refQdCode;

    /** 账册编号 */
    private String booksNo;

    /** 关联账册编号 */
    private String refBooksNo;

    /** 运输方式 */
    private String transWay;

    /** 进境关别 */
    private String inPort;

    /** 启运国 */
    private String shipCountry;

    /** 清关开始时间 */
    private Timestamp clearStartTime;

    /** 清关完成时间 */
    private Timestamp clearEndTime;

    /** 服务完成时间 */
    private Timestamp finishTime;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    private Timestamp createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    private Timestamp updateTime;

    private BigDecimal totalPrice;

    private Long clearId;

    private String clearNo;
}