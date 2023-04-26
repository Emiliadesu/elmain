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
import java.io.Serializable;
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-03-23
**/
@Entity
@Data
@Table(name="bus_stock_in_tolly")
public class StockInTolly implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Transient
    @ApiModelProperty(value = "入库通知单", hidden = true)
    private WmsInstock wmsInstock;

    @Column(name = "in_order_id")
    @ApiModelProperty(value = "入库通知单id")
    private Long inOrderId;

    @Column(name = "tally_order_sn",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "理货单号")
    private String tallyOrderSn;

    @Column(name = "sku_num")
    @ApiModelProperty(value = "理货品种数")
    private Integer skuNum;

    @Column(name = "total_num")
    @ApiModelProperty(value = "理货总件数")
    private Integer totalNum;

    @Column(name = "current_num")
    @ApiModelProperty(value = "当前理货次数")
    private Integer currentNum;

    @Column(name = "start_time")
    @ApiModelProperty(value = "理货开始时间")
    private String startTime;

    @Column(name = "end_time")
    @ApiModelProperty(value = "理货完成时间")
    private String endTime;

    @Column(name = "status",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "理货单状态")
    private String status;

    @Column(name = "file_name")
    @ApiModelProperty(value = "fileName")
    private String fileName;

    @Column(name = "reason")
    @ApiModelProperty(value = "reason")
    private String reason;

    @Column(name = "tenant_code")
    @ApiModelProperty(value = "租户编码")
    private String tenantCode;

    @Column(name = "warehouse_id")
    @ApiModelProperty(value = "仓库编码")
    private String warehouseId;

    @Column(name = "asn_status")
    @ApiModelProperty(value = "ASN单状态")
    private String asnStatus;

    @Column(name = "asn_no")
    @ApiModelProperty(value = "ASN单号")
    private String asnNo;

    @Column(name = "shpd_date")
    @ApiModelProperty(value = "审核时间")
    private String shpdDate;

    @Column(name = "recheck_time")
    @ApiModelProperty(value = "验收时间")
    private String recheckTime;

    @Column(name = "finish_receipt_time")
    @ApiModelProperty(value = "收货时间")
    private String finishReceiptTime;

    @Column(name = "verify_by")
    @ApiModelProperty(value = "审核人")
    private String verifyBy;

    @Column(name = "recheck_by")
    @ApiModelProperty(value = "验收人")
    private String recheckBy;

    @Column(name = "receive_by")
    @ApiModelProperty(value = "收货人")
    private String receiveBy;

    @Column(name = "putaway_time")
    @ApiModelProperty(value = "上架时间")
    private String putawayTime;

    @Column(name = "putaway_by")
    @ApiModelProperty(value = "上架人")
    private String putawayBy;

    @Column(name = "asn_type")
    @ApiModelProperty(value = "ASN单号类型")
    private String asnType;

    @Column(name = "grf_id")
    @ApiModelProperty(value = "grfId")
    private Integer grfId;

    @Column(name = "recheck_no")
    @ApiModelProperty(value = "验收单号")
    private String recheckNo;

    @Column(name = "source_sys")
    @ApiModelProperty(value = "sourceSys")
    private String sourceSys;

    @Column(name = "lpn_qty")
    @ApiModelProperty(value = "托盘数")
    private Integer lpnQty;

    @Column(name = "arrive_time")
    @ApiModelProperty(value = "到货时间")
    private String arriveTime;

    @Transient
    private List<StockInTollyItem> items;

    @Transient
    private List<OutofPlanDetail> outofPlanDetails;

    @Transient
    private List<TrsDetail> trsDetailList;

    public void copy(StockInTolly source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
