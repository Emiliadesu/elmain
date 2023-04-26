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
import me.zhengjie.base.BaseEntity;
import me.zhengjie.base.BaseUpdateEntity;
import me.zhengjie.service.dto.ExpandDto;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.io.Serializable;
import java.util.List;

/**
 * @website https://el-admin.vip
 * @description /
 * @author luob
 * @date 2021-03-09
 **/
@Entity
@Data
@Table(name="bus_clear_info")
public class ClearInfo extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "clear_no",unique = true)
    @ApiModelProperty(value = "单据编号")
    private String clearNo;

    @Column(name = "contract_no")
    @ApiModelProperty(value = "合同号")
    private String contractNo;

    @Column(name = "status",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "状态")
    private String status;

    @Column(name = "customers_id")
    @ApiModelProperty(value = "客户ID")
    private Long customersId;

    @Column(name = "shop_id")
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;

    @Column(name = "clear_company_id")
    @ApiModelProperty(value = "清关抬头_ID")
    private Long clearCompanyId;

    @Column(name = "supplier_id")
    @ApiModelProperty(value = "报关行ID")
    private Long supplierId;

    @Column(name = "bus_type",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "业务类型")
    private String busType;

    @Column(name = "trade_type")
    @ApiModelProperty(value = "贸易类型")
    private String tradeType;

    @Column(name = "ref_order_no")
    @ApiModelProperty(value = "关联单据号")
    private String refOrderNo;

    @Column(name = "ref_order_type")
    @ApiModelProperty(value = "关联单据类型")
    private String refOrderType;

    @Column(name = "declare_mode")
    @ApiModelProperty(value = "申报模式")
    private String declareMode;

    @Column(name = "bill_no")
    @ApiModelProperty(value = "提单号")
    private String billNo;

    @Column(name = "entry_no")
    @ApiModelProperty(value = "报关单号")
    private String entryNo;

    @Column(name = "decl_no")
    @ApiModelProperty(value = "报检单号")
    private String declNo;

    @Column(name = "trans_way")
    @ApiModelProperty(value = "运输方式")
    private String transWay;

    @Column(name = "in_port")
    @ApiModelProperty(value = "入境口岸")
    private String inPort;

    @Column(name = "qd_code")
    @ApiModelProperty(value = "QD单号")
    private String qdCode;

    @Column(name = "sku_num")
    @ApiModelProperty(value = "预估SKU数量")
    private Integer skuNum;

    @Column(name = "total_num")
    @ApiModelProperty(value = "预估件数")
    private Integer totalNum;

    @Column(name = "groos_weight")
    @ApiModelProperty(value = "毛重")
    private BigDecimal groosWeight;

    @Column(name = "net_weight")
    @ApiModelProperty(value = "净重")
    private BigDecimal netWeight;

    @Column(name = "currency")
    @ApiModelProperty(value = "币种")
    private String currency;

    @Column(name = "pruduct")
    @ApiModelProperty(value = "主要产品")
    private String pruduct;

    @Column(name = "in_warehose")
    @ApiModelProperty(value = "入库仓")
    private String inWarehose;

    @Column(name = "container")
    @ApiModelProperty(value = "箱型")
    private String container;

    @Column(name = "clear_data_link")
    @ApiModelProperty(value = "清关资料链接")
    private String clearDataLink;

    @Column(name = "draft_declare_data_link")
    @ApiModelProperty(value = "概报放行单链接")
    private String draftDeclareDataLink;

    @Column(name = "entry_data_link")
    @ApiModelProperty(value = "报关报检单链接")
    private String entryDataLink;

    @Column(name = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Column(name = "expect_arrival_time")
    @ApiModelProperty(value = "预估到港日期")
    private Timestamp expectArrivalTime;

    @Column(name = "ship_country")
    @ApiModelProperty(value = "启运国")
    private String shipCountry;

    @Column(name = "ref_qd_code")
    @ApiModelProperty(value = "关联单证编码")
    private String refQdCode;

    @Column(name = "ref_enterprise_code")
    @ApiModelProperty(value = "转关企业编码")
    private String refEnterpriseCode;

    @Column(name = "in_process_bl")
    @ApiModelProperty(value = "头程提单号")
    private String inProcessBl;

    @Column(name = "switched_bl")
    @ApiModelProperty(value = "二程提单号")
    private String switchedBl;

    @Column(name = "books_no")
    @ApiModelProperty(value = "账册编号")
    private String booksNo;

    @Column(name = "ref_books_no")
    @ApiModelProperty(value = "关联账册编号")
    private String refBooksNo;

    @Column(name = "ship_port")
    @ApiModelProperty(value = "启运港")
    private String shipPort;

    @Column(name = "trade_country")
    @ApiModelProperty(value = "贸易国")
    private String tradeCountry;

    @Column(name = "order_source")
    @ApiModelProperty(value = "单据来源")
    private String orderSource;

    @Column(name = "sum_money")
    @ApiModelProperty(value = "总金额")
    private BigDecimal sumMoney;

    @Column(name = "dcl_cus_code")
    @ApiModelProperty(value = "申报海关关区代码")
    private String dclCusCode;

    @Column(name = "supv_code")
    @ApiModelProperty(value = "监管方式代码")
    private String supvCode;

    @Column(name = "impexp_code")
    @ApiModelProperty(value = "进出境关别代码")
    private String impexpCode;

    @Column(name = "impexp_mark_code")
    @ApiModelProperty(value = "进出口标记代码")
    private String impexpMarkCode;

    @Column(name = "bizop_no")
    @ApiModelProperty(value = "经营企业编号")
    private String bizopNo;

    @Column(name = "bizop_name")
    @ApiModelProperty(value = "经营企业名称")
    private String bizopName;

    @Column(name = "proc_no")
    @ApiModelProperty(value = "加工企业编号")
    private String procNo;

    @Column(name = "proc_name")
    @ApiModelProperty(value = "加工企业名称")
    private String procName;

    @Column(name = "dcletps_no")
    @ApiModelProperty(value = "申报企业编号")
    private String dcletpsNo;

    @Column(name = "dcletps_name")
    @ApiModelProperty(value = "申报企业名称")
    private String dcletpsName;

    @Column(name = "material_type")
    @ApiModelProperty(value = "料件成品标记代码")
    private String materialType;

    @Column(name = "circulation_type")
    @ApiModelProperty(value = "流转类型")
    private String circulationType;

    @Column(name = "input_code")
    @ApiModelProperty(value = "录入企业编号")
    private String inputCode;

    @Column(name = "input_name")
    @ApiModelProperty(value = "录入单位名称")
    private String inputName;

    @Column(name = "advance_time")
    @ApiModelProperty(value = "进出境日期")
    private Timestamp advanceTime;

    @Column(name = "overseas_name")
    @ApiModelProperty(value = "供应商名称英文")
    private String overseasName;

    @Column(name = "trade_ebp_code")
    @ApiModelProperty(value = "消费生成企业海关编码")
    private String tradeEbpCode;

    @Column(name = "trade_ebp_name")
    @ApiModelProperty(value = "消费生成企业名称")
    private String tradeEbpName;

    @Column(name = "trade_ebp_sccode")
    @ApiModelProperty(value = "消费生成企业社会信用代码")
    private String tradeEbpSccode;

    @Column(name = "pack_type_code")
    @ApiModelProperty(value = "包装种类代码")
    private String packTypeCode;

    @Column(name = "district_code")
    @ApiModelProperty(value = "进口指境内目的地代码")
    private String districtCode;

    @Column(name = "dest_code")
    @ApiModelProperty(value = "目的地代码")
    private String destCode;

    @Column(name = "invt_type")
    @ApiModelProperty(value = "清单类型")
    private String invtType;

    @Column(name = "serial_no")
    @ApiModelProperty(value = "平台流水单号")
    private String serialNo;

    @Column(name = "dcl_type_code")
    @ApiModelProperty(value = "申报类型")
    private String dclTypeCode;

    @Transient
    private List<ClearDetails> details;

    @Transient
    private List<ClearContainer> clearContainerList;

    @Transient
    private List<ExpandDto> expands;


    public void copy(ClearInfo source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}