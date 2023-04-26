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
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2021-05-13
**/
@Entity
@Data
@Table(name="bus_inbound_order")
public class InboundOrder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "customers_id")
    @ApiModelProperty(value = "客户ID")
    private Long customersId;

    @Column(name = "shop_id")
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;

    @Column(name = "order_no",unique = true)
    @ApiModelProperty(value = "入库单号")
    private String orderNo;

    @Column(name = "out_no")
    @ApiModelProperty(value = "外部单号")
    private String outNo;

    @Column(name = "wms_no")
    @ApiModelProperty(value = "WMS单号")
    private String wmsNo;

    @Column(name = "order_type")
    @ApiModelProperty(value = "单据类型")
    private String orderType;

    @Column(name = "status")
    @ApiModelProperty(value = "状态")
    private Integer status;

    @Column(name = "original_no")
    @ApiModelProperty(value = "原单号")
    private String originalNo;

    @Column(name = "declare_no")
    @ApiModelProperty(value = "报关单号")
    private String declareNo;

    @Column(name = "inspect_no")
    @ApiModelProperty(value = "报检单号")
    private String inspectNo;

    @Column(name = "expect_arrive_time")
    @ApiModelProperty(value = "预期到货时间")
    private Timestamp expectArriveTime;

    @Column(name = "tally_way")
    @ApiModelProperty(value = "理货维度")
    private String tallyWay;

    @Column(name = "pallet_num")
    @ApiModelProperty(value = "托数")
    private Integer palletNum;

    @Column(name = "box_num")
    @ApiModelProperty(value = "箱数")
    private Integer boxNum;

    @Column(name = "expect_sku_num")
    @ApiModelProperty(value = "预期SKU数")
    private Integer expectSkuNum;

    @Column(name = "grounding_sku_num")
    @ApiModelProperty(value = "上架SKU数")
    private Integer groundingSkuNum;

    @Column(name = "expect_total_num")
    @ApiModelProperty(value = "预期总件数")
    private Integer expectTotalNum;

    @Column(name = "grounding_total_num")
    @ApiModelProperty(value = "上架总件数")
    private Integer groundingTotalNum;

    @Column(name = "grounding_normal_num")
    @ApiModelProperty(value = "上架正品件数")
    private Integer groundingNormalNum;

    @Column(name = "grounding_damaged_num")
    @ApiModelProperty(value = "上架残品件数")
    private Integer groundingDamagedNum;

    @Column(name = "confirm_by")
    @ApiModelProperty(value = "接单确认人")
    private String confirmBy;

    @Column(name = "confirm_time")
    @ApiModelProperty(value = "接单确认时间")
    private Timestamp confirmTime;

    @Column(name = "arrive_by")
    @ApiModelProperty(value = "到货登记人")
    private String arriveBy;

    @Column(name = "car_number")
    @ApiModelProperty(value = "车牌号")
    private String carNumber;

    @Column(name = "arrive_time")
    @ApiModelProperty(value = "到货时间")
    private Timestamp arriveTime;

    @Column(name = "arrive_back_time")
    @ApiModelProperty(value = "到货回传时间")
    private Timestamp arriveBackTime;

    @Column(name = "tally_by")
    @ApiModelProperty(value = "理货人")
    private String tallyBy;

    @Column(name = "tally_start_time")
    @ApiModelProperty(value = "理货开始时间")
    private Timestamp tallyStartTime;

    @Column(name = "tally_start_back_time")
    @ApiModelProperty(value = "理货开始回传时间")
    private Timestamp tallyStartBackTime;

    @Column(name = "tally_end_time")
    @ApiModelProperty(value = "理货结束时间")
    private Timestamp tallyEndTime;

    @Column(name = "tally_end_back_time")
    @ApiModelProperty(value = "理货结束回传时间")
    private Timestamp tallyEndBackTime;

    @Column(name = "take_by")
    @ApiModelProperty(value = "收货人")
    private String takeBy;

    @Column(name = "take_time")
    @ApiModelProperty(value = "收货完成时间")
    private Timestamp takeTime;

    @Column(name = "take_back_time")
    @ApiModelProperty(value = "收货完成回传时间")
    private Timestamp takeBackTime;

    @Column(name = "cancel_time")
    @ApiModelProperty(value = "取消时间")
    private Timestamp cancelTime;

    @Column(name = "create_by")
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @Transient
    private List<InboundOrderDetails> details;

    @Transient
    private InboundTally tally;

    @Column(name = "stock_record_url")
    @ApiModelProperty(value = "理货报告url")
    private String stockRecordUrl;

    @Column(name = "stock_record_name")
    @ApiModelProperty(value = "理货报告文件名")
    private String stockRecordName;

    @Column(name = "is_online")
    @ApiModelProperty(value = "是否线上建单")
    private String isOnline;

    @Column(name = "online_src")
    @ApiModelProperty(value = "是否线上建单")
    private String onlineSrc;

    @Column(name = "is_four_pl")
    @ApiModelProperty(value = "是否4pl单")
    private String isFourPl;

    @Column(name = "tally_count")
    @ApiModelProperty(value = "理货次数")
    private Integer tallyCount;

    @Column(name = "default_01")
    @ApiModelProperty(value = "预留字段01")
    private String default01;

    @Column(name = "freeze_reason")
    @ApiModelProperty(value = "冻结原因")
    private String freezeReason;

    @Column(name = "declare_detail_list")
    @ApiModelProperty(value = "清关资料json字符串")
    private String declareDetailList;

    @Column(name = "platform_code")
    @ApiModelProperty(value = "电商平台代码")
    private String platformCode;

    @Column(name = "dy_ext_sub_lading_bill_no")
    @ApiModelProperty(value = "抖音扩展字段-分提单号")
    private String dyExtSubLadingBillNo;

    @Column(name = "dy_ext_transaction_method")
    @ApiModelProperty(value = "抖音扩展字段-成交方式，1-CIF，2-C&F，3-FOB，4-C&I，5-市场价，6-垫仓，7-EXW")
    private Integer dyExtTransactionMethod;

    @Column(name = "dy_ext_port_custom_type")
    @ApiModelProperty(value = "抖音扩展字段-港到仓清关服务，1-商家自理，2-港到仓清关3pl，3-港到仓清关4pl")
    private Integer dyExtPortCustomType;

    @Column(name = "dy_ext_tms_type")
    @ApiModelProperty(value = "抖音扩展字段-运输服务，1-自主运输，2-3pl运输，3-4pl运输")
    private Integer dyExtTmsType;

    @Column(name = "dy_ext_send_warehouse_addr")
    @ApiModelProperty(value = "抖音扩展字段-发货仓地址")
    private String dyExtSendWarehouseAddr;

    @Column(name = "wms_status")
    @ApiModelProperty(value = "wms状态")
    private String wmsStatus;

    public void copy(InboundOrder source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
