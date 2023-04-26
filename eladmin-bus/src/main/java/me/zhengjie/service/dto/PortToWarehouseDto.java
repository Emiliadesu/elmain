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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
* @website https://el-admin.vip
* @description /
* @author lh
* @date 2021-01-10
**/
@Data
public class PortToWarehouseDto implements Serializable {

    /** 防止精度丢失 */
    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;

    /** 港到仓CP */
    private String portToWarehouseName;

    /** 仓CP */
    private String cpName;

    /** QG号 */
    private String qgCode;

    /** 商家性质 */
    private String shopType;

    /** 店铺名称 */
    private String shopName;

    /** LBX号 */
    private String lbxCode;

    /** 总数量 */
    private Integer totalNum;

    /** 品种数 */
    private Integer skuNum;

    /** 提单号 */
    private String blCode;

    /** 合同号 */
    private String contractCode;

    /** 箱型 */
    private String containerType;

    /** 报关行 */
    private String customsBroker;

    /** 报关单号 */
    private String customDeclareCode;

    /** 报检单号 */
    private String inspectionSingleCode;

    /** 入境口岸 */
    private String portName;

    /** 运输方式 */
    private String transportation;

    /** 资料路径 */
    private String dataPath;

    /** 接单日期 */
    private Timestamp orderTakingTime;

    /** 预计到港日期 */
    private Timestamp expectArrivalTime;

    /** 实际到港日期 */
    private Timestamp actualArrivalTime;

    /** 资料提供时间 */
    private Timestamp dataSupplyTime;

    /** 资料审核时间 */
    private Timestamp dataAuditingTime;

    /** 报关报检开始时间 */
    private Timestamp applyStartTime;

    /** 报关报检结束时间 */
    private Timestamp applyEndTime;

    /** 预计送货时间 */
    private Timestamp expectDeliverTime;

    /** 到仓时间 */
    private Timestamp arrivalWarehouseTime;

    /** 入仓卸货完成时间 */
    private Timestamp unloadingEndTime;

    /** 理货完成时间 */
    private Timestamp tallyingEndTime;

    /** 理货报告上传时间 */
    private Timestamp tallyingReportUploadTime;

    /** 理货报告确认时间 */
    private Timestamp tallyingReportConfirmTime;

    /** 海关上架时间 */
    private Timestamp costomGroundingTime;

    /** 收货时间 */
    private Timestamp wmsGroundingTime;

    /** 资料时效 */
    private Integer dataTimeliness;

    /** 资料时效是否超时 */
    private String dataTimelinessStatus;

    /** 资料超时原因 */
    private String dataOvertimeReason;

    /** 清关时效 */
    private Integer customDeclareTimeliness;

    /** 清关时效是否超时 */
    private String customDeclareTimelinessStatus;

    /** 清关超时原因 */
    private String customDeclareOvertimeReason;

    /** 理货时效 */
    private Integer tallyingTimeliness;

    /** 理货时效是否超时 */
    private String tallyingTimelinessStatus;

    /** 理货超时原因 */
    private String tallyingOvertimeReason;

    /** 理货确认时效 */
    private Integer tallyingConfirmTimeliness;

    /** 理货确认时效是否超时 */
    private String tallyingConfirmTimelinessStatus;

    /** 理货确认时效超时原因 */
    private String tallyingConfirmOvertimeReason;

    /** 上架时效 */
    private Integer groundingTimeliness;

    /** 上架时效是否超时 */
    private String groundingTimelinessStatus;

    /** 上架超时原因 */
    private String groundingOvertimeReason;

    /** 备注 */
    private String remark;

    /** 当前状态 */
    private String status;

    /** 创建时间 */
    private Timestamp createTime;

    /** 创建人 */
    private String createUser;

    /** 修改时间 */
    private Timestamp modifyTime;

    /** 修改人 */
    private String modifyUser;
}