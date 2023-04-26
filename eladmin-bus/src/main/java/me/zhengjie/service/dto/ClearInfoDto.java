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
 * @date 2021-03-09
 **/
@Data
public class ClearInfoDto implements Serializable {

    /** ID */
    private Long id;

    /** 单据编号(GQ\合同) */
    private String clearNo;

    private String contractNo;

    /** 状态 */
    private String status;

    /** 客户ID */
    private Long customersId;

    /** 店铺ID */
    private Long shopId;

    /** 清关抬头_ID */
    private Long clearCompanyId;

    /** 报关行ID */
    private Long supplierId;

    /** 业务类型 */
    private String busType;

    private String refOrderNo;

    private String refOrderType;

    /** 申报模式 */
    private String declareMode;

    /** 提单号 */
    private String billNo;

    /** 报关单号 */
    private String entryNo;

    /** 报检单号 */
    private String declNo;

    /** 运输方式 */
    private String transWay;

    /** 入境口岸 */
    private String inPort;

    /** QD单号 */
    private String qdCode;

    /** 预估SKU数量 */
    private Integer skuNum;

    /** 预估件数 */
    private Integer totalNum;

    /** 毛重 */
    private BigDecimal groosWeight;

    /** 币种 */
    private String currency;

    /** 主要产品 */
    private String pruduct;

    /** 入库仓 */
    private String inWarehose;

    /** 清关资料链接 */
    private String clearDataLink;

    /** 概报放行单链接 */
    private String draftDeclareDataLink;

    /** 报关报检单链接 */
    private String entryDataLink;

    /** 备注 */
    private String remark;

    /** 预估到港日期 */
    private Timestamp expectArrivalTime;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    private Timestamp createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    private Timestamp updateTime;

    /** 启运国 */
    private String shipCountry;

    private String refQdCode;

    private String inProcessBl;

    private String switchedBl;

    private String booksNo;

    private String refBooksNo;

    private String shipPort;

    private String tradeCountry;

    private String refEnterpriseCode;

    private BigDecimal sumMoney;

    private String dclCusCode;

    private String supvCode;

    private String impexpCode;

    private String impexpMarkCode;

    private String bizopNo;

    private String bizopName;

    private String procNo;

    private String procName;

    private String dcletpsNo;

    private String dcletpsName;

    private String materialType;

    private String circulationType;

    private String inputCode;

    private String inputName;

    private Timestamp advanceTime;

    private String overseasName;

    private String tradeEbpCode;

    private String tradeEbpName;

    private String tradeEbpSccode;

    private String packTypeCode;

    private String districtCode;

    private String destCode;

    private String invtType;

    private String serialNo;

    private String dclTypeCode;
}