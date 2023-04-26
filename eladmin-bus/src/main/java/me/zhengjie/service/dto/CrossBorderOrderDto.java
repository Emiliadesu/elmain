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

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import me.zhengjie.domain.CrossBorderOrderDetails;

import javax.persistence.Column;
import java.sql.Timestamp;
import java.io.Serializable;
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2021-03-25
**/
@Data
public class CrossBorderOrderDto implements Serializable {

    /** ID */
    private Long id;

    /** 订单号 */
    private String orderNo;

    /** 状态 */
    private Integer status;

    /** 状态 */
    private String upStatus;

    /** 客户ID */
    private Long customersId;

    /** 店铺ID */
    private Long shopId;

    /** 清关抬头ID */
    private Long clearCompanyId;

    /** 电商平台平台ID */
    private Long platformId;

    private String platformCode;

    /** 承运商ID */
    private Long supplierId;

    /** 交易号 */
    private String crossBorderNo;

    /** 菜鸟LP单号 */
    private String lpCode;

    /** 申报单号 */
    private String declareNo;

    private String declareStatus;

    private String declareMsg;

    /** 总署清单编号 */
    private String invtNo;

    /** 订单创建时间 */
    private Timestamp orderCreateTime;

    /** 异常信息 */
    private String errMsg;

    /** 实付金额 */
    private String payment;

    /** 运费 */
    private String postFee;

    /** 买家账号 */
    private String buyerAccount;

    /** 关税 */
    private String tariffAmount;

    /** 增值税 */
    private String addedValueTaxAmount;

    /** 消费税 */
    private String consumptionDutyAmount;

    /** 总税额 */
    private String taxAmount;

    /** 净重（千克） */
    private String netWeight;

    /** 毛重（千克） */
    private String grossWeight;

    /** 优惠金额合计 */
    private String disAmount;

    private String preSell;

    private String freezeReason;

    /** 客户备注 */
    private String buyerRemark;

    /** 订购人电话 */
    private String buyerPhone;

    /** 订购人身份证号码 */
    private String buyerIdNum;

    /** 订购人姓名 */
    private String buyerName;

    /** 支付方式 */
    private String payCode;

    /** 支付时间 */
    private Timestamp payTime;

    /** 支付单号 */
    private String paymentNo;

    /** 支付交易号 */
    private String orderSeqNo;

    /** 运单号 */
    private String logisticsNo;

    /** 省 */
    private String province;

    /** 市 */
    private String city;

    /** 区 */
    private String district;

    /** 收货地址 */
    private String consigneeAddr;

    /** 收货电话 */
    private String consigneeTel;

    /** 大头笔 */
    private String addMark;

    /** 接单回传时间 */
    private Timestamp receivedBackTime;

    /** 清关开始时间 */
    private Timestamp clearStartTime;

    /** 清关开始回传时间 */
    private Timestamp clearStartBackTime;

    /** 清关完成时间 */
    private Timestamp clearSuccessTime;

    /** 清关完成回传时间 */
    private Timestamp clearSuccessBackTime;

    /** 出库时间 */
    private Timestamp deliverTime;

    /** 打包时间 */
    private Timestamp packTime;

    /** 打包完成回传时间 */
    private Timestamp packBackTime;

    /** 打包时间 */
    private Timestamp weighingTime;

    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    private Timestamp createTime;

    private List<CrossBorderOrderDetailsDto> itemList;

    private Integer logisticsStatus;

    private String logisticsMsg;

    private Timestamp expDeliverTime;

    private Timestamp logisticsCollectTime;

    private Timestamp logisticsSignTime;

    private String wmsStatus;

    private String sendPickFlag;

    private String isWave;

    private String waveNo;

    private String isPrint;

    private String printTime;

    private String pickPrintInfo;

    private String billPrintInfo;

    private String packWeight;

    private String materialCode;

    /**
     * 平台状态编码
     */
    private Integer platformStatus;

    private String platformShopId;

    private String isLock;

    private String refundReason;

    private String logisticsCode;

    private String logisticsName;

    private String soNo;

    private String waveName;

    private String seqNo;

    private String pickType;

    private String area;

    private String skuNum;

    private String totalNum;

    private String fourPl;

    private String theoryWeight;

    private String logisticsFourPl;
}
