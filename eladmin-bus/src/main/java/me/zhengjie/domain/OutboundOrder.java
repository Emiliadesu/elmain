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
 * @author luob
 * @website https://el-admin.vip
 * @description /
 * @date 2021-07-13
 **/
@Entity
@Data
@Table(name = "bus_outbound_order")
public class OutboundOrder implements Serializable {

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

    @Column(name = "order_no", unique = true)
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

    @Column(name = "expect_deliver_time")
    @ApiModelProperty(value = "预期发货时间")
    private Timestamp expectDeliverTime;

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

    @Column(name = "deliver_sku_num")
    @ApiModelProperty(value = "出库SKU数")
    private Integer deliverSkuNum;

    @Column(name = "expect_total_num")
    @ApiModelProperty(value = "预期总件数")
    private Integer expectTotalNum;

    @Column(name = "deliver_total_num")
    @ApiModelProperty(value = "出库总件数")
    private Integer deliverTotalNum;

    @Column(name = "deliver_normal_num")
    @ApiModelProperty(value = "出库正品件数")
    private Integer deliverNormalNum;

    @Column(name = "deliver_damaged_num")
    @ApiModelProperty(value = "出库残品件数")
    private Integer deliverDamagedNum;

    @Column(name = "confirm_by")
    @ApiModelProperty(value = "接单确认人")
    private String confirmBy;

    @Column(name = "confirm_time")
    @ApiModelProperty(value = "接单确认时间")
    private Timestamp confirmTime;

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

    @Column(name = "deliver_by")
    @ApiModelProperty(value = "收货人")
    private String deliverBy;

    @Column(name = "deliver_time")
    @ApiModelProperty(value = "收货完成时间")
    private Timestamp deliverTime;

    @Column(name = "deliver_back_time")
    @ApiModelProperty(value = "收货完成回传时间")
    private Timestamp deliverBackTime;

    @Column(name = "cancel_time")
    @ApiModelProperty(value = "取消时间")
    private Timestamp cancelTime;

    @Column(name = "create_by")
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @Column(name = "is_online")
    @ApiModelProperty(value = "是否线上建单")
    private String isOnline;

    @Column(name = "online_src")
    @ApiModelProperty(value = "是否线上建单")
    private String onlineSrc;

    @Column(name = "is_four_pl")
    @ApiModelProperty(value = "是否4PL单")
    private String isFourPl;

    @Column(name = "tally_count")
    @ApiModelProperty(value = "理货次数")
    private Integer tallyCount;

    @Column(name = "freeze_reason")
    @ApiModelProperty(value = "冻结原因")
    private String freezeReason;

    @Column(name = "default_01")
    @ApiModelProperty(value = "预留字段01")
    private String default01;

    @Column(name = "platform_code")
    @ApiModelProperty(value = "电商平台代码")
    private String platformCode;

    @Transient
    private List<OutboundOrderDetails> details;

    @Column(name = "dy_ext_receiver_phone")
    @ApiModelProperty(value = "抖音扩展字段-收货人电话")
    private String dyExtReceiverPhone;

    @Column(name = "dy_ext_receiver_name")
    @ApiModelProperty(value = "抖音扩展字段-收货人姓名")
    private String dyExtReceiverName;

    @Column(name = "dy_ext_tms_type")
    @ApiModelProperty(value = "抖音扩展字段-运输服务，1-自主运输，2-3pl运输，3-4pl运输")
    private Integer dyExtTmsType;

    @Column(name = "dy_ext_target_warehouse_addr")
    @ApiModelProperty(value = "抖音扩展字段-目的仓地址")
    private String dyExtTargetWarehouseAddr;

    @Column(name = "wms_status")
    @ApiModelProperty(value = "wms状态")
    private String wmsStatus;

    public void copy(OutboundOrder source) {
        BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
