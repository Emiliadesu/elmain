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
package me.zhengjie.domain;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author lh
* @date 2021-01-10
**/
@Entity
@Data
@Table(name="bus_port_to_warehouse")
public class PortToWarehouse implements Serializable {

    @Id
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "port_to_warehouse_name",nullable = false)
    @ApiModelProperty(value = "港到仓CP")
    private String portToWarehouseName;

    @Column(name = "cp_name",nullable = false)
    @ApiModelProperty(value = "仓CP")
    private String cpName;

    @Column(name = "qg_code",unique = true,nullable = false)
    @NotBlank
    @ApiModelProperty(value = "QG号")
    private String qgCode;

    @Column(name = "shop_type",nullable = false)
    @ApiModelProperty(value = "商家性质")
    private String shopType;

    @Column(name = "shop_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "店铺名称")
    private String shopName;

    @Column(name = "lbx_code")
    @ApiModelProperty(value = "LBX号")
    private String lbxCode;

    @Column(name = "total_num",nullable = false)
    @NotNull
    @ApiModelProperty(value = "总数量")
    private Integer totalNum;

    @Column(name = "sku_num",nullable = false)
    @NotNull
    @ApiModelProperty(value = "品种数")
    private Integer skuNum;

    @Column(name = "bl_code")
    @ApiModelProperty(value = "提单号")
    private String blCode;

    @Column(name = "contract_code")
    @ApiModelProperty(value = "合同号")
    private String contractCode;

    @Column(name = "container_type")
    @ApiModelProperty(value = "箱型")
    private String containerType;

    @Column(name = "customs_broker")
    @ApiModelProperty(value = "报关行")
    private String customsBroker;

    @Column(name = "custom_declare_code")
    @ApiModelProperty(value = "报关单号")
    private String customDeclareCode;

    @Column(name = "inspection_single_code")
    @ApiModelProperty(value = "报检单号")
    private String inspectionSingleCode;

    @Column(name = "port_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "入境口岸")
    private String portName;

    @Column(name = "transportation",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "运输方式")
    private String transportation;

    @Column(name = "data_path")
    @ApiModelProperty(value = "资料路径")
    private String dataPath;

    @Column(name = "order_taking_time",nullable = false)
    @NotNull
    @ApiModelProperty(value = "接单日期")
    private Timestamp orderTakingTime;

    @Column(name = "expect_arrival_time")
    @ApiModelProperty(value = "预计到港日期")
    private Timestamp expectArrivalTime;

    @Column(name = "actual_arrival_time")
    @ApiModelProperty(value = "实际到港日期")
    private Timestamp actualArrivalTime;

    @Column(name = "data_supply_time")
    @ApiModelProperty(value = "资料提供时间")
    private Timestamp dataSupplyTime;

    @Column(name = "data_auditing_time")
    @ApiModelProperty(value = "资料审核时间")
    private Timestamp dataAuditingTime;

    @Column(name = "apply_start_time")
    @ApiModelProperty(value = "报关报检开始时间")
    private Timestamp applyStartTime;

    @Column(name = "apply_end_time")
    @ApiModelProperty(value = "报关报检结束时间")
    private Timestamp applyEndTime;

    @Column(name = "expect_deliver_time")
    @ApiModelProperty(value = "预计送货时间")
    private Timestamp expectDeliverTime;

    @Column(name = "arrival_warehouse_time")
    @ApiModelProperty(value = "到仓时间")
    private Timestamp arrivalWarehouseTime;

    @Column(name = "unloading_end_time")
    @ApiModelProperty(value = "入仓卸货完成时间")
    private Timestamp unloadingEndTime;

    @Column(name = "tallying_end_time")
    @ApiModelProperty(value = "理货完成时间")
    private Timestamp tallyingEndTime;

    @Column(name = "tallying_report_upload_time")
    @ApiModelProperty(value = "理货报告上传时间")
    private Timestamp tallyingReportUploadTime;

    @Column(name = "tallying_report_confirm_time")
    @ApiModelProperty(value = "理货报告确认时间")
    private Timestamp tallyingReportConfirmTime;

    @Column(name = "costom_grounding_time")
    @ApiModelProperty(value = "海关上架时间")
    private Timestamp costomGroundingTime;

    @Column(name = "wms_grounding_time")
    @ApiModelProperty(value = "收货时间")
    private Timestamp wmsGroundingTime;

    @Column(name = "data_timeliness")
    @ApiModelProperty(value = "资料时效")
    private Integer dataTimeliness;

    @Column(name = "data_timeliness_status")
    @ApiModelProperty(value = "资料时效是否超时")
    private String dataTimelinessStatus;

    @Column(name = "data_overtime_reason")
    @ApiModelProperty(value = "资料超时原因")
    private String dataOvertimeReason;

    @Column(name = "custom_declare_timeliness")
    @ApiModelProperty(value = "清关时效")
    private Integer customDeclareTimeliness;

    @Column(name = "custom_declare_timeliness_status")
    @ApiModelProperty(value = "清关时效是否超时")
    private String customDeclareTimelinessStatus;

    @Column(name = "custom_declare_overtime_reason")
    @ApiModelProperty(value = "清关超时原因")
    private String customDeclareOvertimeReason;

    @Column(name = "tallying_timeliness")
    @ApiModelProperty(value = "理货时效")
    private Integer tallyingTimeliness;

    @Column(name = "tallying_timeliness_status")
    @ApiModelProperty(value = "理货时效是否超时")
    private String tallyingTimelinessStatus;

    @Column(name = "tallying_overtime_reason")
    @ApiModelProperty(value = "理货超时原因")
    private String tallyingOvertimeReason;

    @Column(name = "tallying_confirm_timeliness")
    @ApiModelProperty(value = "理货确认时效")
    private Integer tallyingConfirmTimeliness;

    @Column(name = "tallying_confirm_timeliness_status")
    @ApiModelProperty(value = "理货确认时效是否超时")
    private String tallyingConfirmTimelinessStatus;

    @Column(name = "tallying_confirm_overtime_reason")
    @ApiModelProperty(value = "理货确认时效超时原因")
    private String tallyingConfirmOvertimeReason;

    @Column(name = "grounding_timeliness")
    @ApiModelProperty(value = "上架时效")
    private Integer groundingTimeliness;

    @Column(name = "grounding_timeliness_status")
    @ApiModelProperty(value = "上架时效是否超时")
    private String groundingTimelinessStatus;

    @Column(name = "grounding_overtime_reason")
    @ApiModelProperty(value = "上架超时原因")
    private String groundingOvertimeReason;

    @Column(name = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Column(name = "status",nullable = false)
    @ApiModelProperty(value = "当前状态")
    private String status;

    @Column(name = "create_time",nullable = false)
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @Column(name = "create_user",nullable = false)
    @ApiModelProperty(value = "创建人")
    private String createUser;

    @Column(name = "modify_time",nullable = false)
    @ApiModelProperty(value = "修改时间")
    private Timestamp modifyTime;

    @Column(name = "modify_user",nullable = false)
    @ApiModelProperty(value = "修改人")
    private String modifyUser;

    public void copy(PortToWarehouse source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
