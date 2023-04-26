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
* @date 2021-04-14
**/
@Entity
@Data
@Table(name="bus_order_return")
public class OrderReturn  implements Serializable {

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

    @Column(name = "trade_return_no")
    @ApiModelProperty(value = "退货单号")
    private String tradeReturnNo;

    @Column(name = "logistics_fulfil_no")
    @ApiModelProperty(value = "LFO单号")
    private String logisticsFulfilNo;

    @Column(name = "status",nullable = false)
    @NotNull
    @ApiModelProperty(value = "状态")
    private Integer status;

    @Column(name = "is_over_time",nullable = false)
    @ApiModelProperty(value = "是否超时")
    private String isOverTime;

    @Column(name = "logistics_no")
    @ApiModelProperty(value = "物流订单号")
    private String logisticsNo;

    @Column(name = "order_no")
    @ApiModelProperty(value = "原订单号")
    private String orderNo;

    @Column(name = "declare_no")
    @ApiModelProperty(value = "申报单号")
    private String declareNo;

    @Column(name = "invt_no")
    @ApiModelProperty(value = "总署清单编号")
    private String invtNo;

    @Column(name = "platform_code")
    @ApiModelProperty(value = "平台")
    private String platformCode;

    @Column(name = "dec_flag")
    @ApiModelProperty(value = "申报标记")
    private String decFlag;

    @Column(name = "remark")
    @ApiModelProperty(value = "备注说明")
    private String remark;

    @Column(name = "tax_amount")
    @ApiModelProperty(value = "总税额")
    private String taxAmount;

    @Column(name = "is_border")
    @ApiModelProperty(value = "是否入区")
    private String isBorder;

    @Column(name = "after_sales_type")
    @ApiModelProperty(value = "售后类型")
    private String afterSalesType;

    @Column(name = "after_sales_no")
    @ApiModelProperty(value = "售后单号")
    private String afterSalesNo;

    @Column(name = "sales_customs_time")
    @ApiModelProperty(value = "订单清关时间")
    private Timestamp salesCustomsTime;

    @Column(name = "sales_deliver_time")
    @ApiModelProperty(value = "订单放行时间")
    private Timestamp salesDeliverTime;

    @Column(name = "s_express_no")
    @ApiModelProperty(value = "正向物流单号")
    private String sExpressNo;

    @Column(name = "s_express_name")
    @ApiModelProperty(value = "正向物流公司")
    private String sExpressName;

    @Column(name = "r_express_no")
    @ApiModelProperty(value = "逆向物流单号")
    private String rExpressNo;

    @Column(name = "r_express_name")
    @ApiModelProperty(value = "逆向物流公司")
    private String rExpressName;

    @Column(name = "return_type")
    @ApiModelProperty(value = "退货类型")
    private String returnType;

    @Column(name = "check_result")
    @ApiModelProperty(value = "质检结果")
    private String checkResult;

    @Column(name = "order_service_status")
    @ApiModelProperty(value = "订单售后状态")
    private String orderServiceStatus;//

    @Column(name = "check_type")
    @ApiModelProperty(value = "质检结果类型")
    private String checkType;

    @Column(name = "order_source")
    @ApiModelProperty(value = "单据来源")
    private String orderSource;

    @Column(name = "declare_status")
    @ApiModelProperty(value = "清关状态")
    private String declareStatus;

    @Column(name = "declare_msg")
    @ApiModelProperty(value = "清关信息")
    private String declareMsg;

    @Column(name = "take_time")
    @ApiModelProperty(value = "收货时间")
    private Timestamp takeTime;

    @Column(name = "take_back_time")
    @ApiModelProperty(value = "收货回传时间")
    private Timestamp takeBackTime;

    @Column(name = "check_time")
    @ApiModelProperty(value = "质检完成时间")
    private Timestamp checkTime;

    @Column(name = "check_back_time")
    @ApiModelProperty(value = "质检完成回传时间")
    private Timestamp checkBackTime;

    @Column(name = "declare_start_time")
    @ApiModelProperty(value = "申报开始时间")
    private Timestamp declareStartTime;

    @Column(name = "declare_start_back_time")
    @ApiModelProperty(value = "申报开始回传时间")
    private Timestamp declareStartBackTime;

    @Column(name = "declare_end_time")
    @ApiModelProperty(value = "申报完成时间")
    private Timestamp declareEndTime;

    @Column(name = "declare_end_back_time")
    @ApiModelProperty(value = "申报完成回传时间")
    private Timestamp declareEndBackTime;

    @Column(name = "bonded_ground_time")
    @ApiModelProperty(value = "保税仓上架时间")
    private Timestamp bondedGroundTime;

    @Column(name = "bonded_ground_back_time")
    @ApiModelProperty(value = "保税仓上架回传时间")
    private Timestamp bondedGroundBackTime;

    @Column(name = "tally_time")
    @ApiModelProperty(value = "理货完成时间")
    private Timestamp tallyTime;

    @Column(name = "tally_back_time")
    @ApiModelProperty(value = "理货完成回传时间")
    private Timestamp tallyBackTime;

    @Column(name = "return_ground_time")
    @ApiModelProperty(value = "退货仓上架时间")
    private Timestamp returnGroundTime;

    @Column(name = "return_ground_back_time")
    @ApiModelProperty(value = "退货仓上架回传时间")
    private Timestamp returnGroundBackTime;

    @Column(name = "cancel_time")
    @ApiModelProperty(value = "取消时间")
    private Timestamp cancelTime;

    @Column(name = "cancel_back_time")
    @ApiModelProperty(value = "取消回传时间")
    private Timestamp cancelBackTime;

    @Column(name = "create_by")
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @Column(name = "wms_no")
    @ApiModelProperty(value = "WMS单号")
    private String wmsNo;

    @Column(name = "is_wave")
    @ApiModelProperty(value = "产生波次")
    private String isWave;

    @Column(name = "gather_no")
    @ApiModelProperty(value = "提总单号")
    private String gatherNo;

    @Column(name = "close_time")
    @ApiModelProperty(value = "关单时间")
    private Timestamp closeTime;

    @Column(name = "four_pl")
    @ApiModelProperty(value = "是否4PL单")
    private String fourPl;

    @Transient
    private List<OrderReturnDetails> itemList;

    @Transient
    private CrossBorderOrder order;

    public void copy(OrderReturn source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}