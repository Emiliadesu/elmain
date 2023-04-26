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

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2021-08-26
**/
@Entity
@Data
@Table(name="bus_hezhu_info")
public class HezhuInfo extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "clear_id")
    @ApiModelProperty(value = "清关ID")
    private Long clearId;

    @Column(name = "clear_no")
    @ApiModelProperty(value = "清关单号")
    private String clearNo;

    @Column(name = "order_no",unique = true)
    @ApiModelProperty(value = "单据编号")
    private String orderNo;

    @Column(name = "status")
    @ApiModelProperty(value = "状态")
    private Integer status;

    @Column(name = "trade_type")
    @ApiModelProperty(value = "贸易类型")
    private String tradeType;

    @Column(name = "customs_status")
    @ApiModelProperty(value = "海关状态")
    private Integer customsStatus;

    @Column(name = "customers_id")
    @ApiModelProperty(value = "客户ID")
    private Long customersId;

    @Column(name = "shop_id")
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;

    @Column(name = "clear_company_id")
    @ApiModelProperty(value = "清关抬头ID")
    private Long clearCompanyId;

    @Column(name = "bus_type")
    @ApiModelProperty(value = "业务类型")
    private String busType;

    @Column(name = "sku_num")
    @ApiModelProperty(value = "预估SKU数量")
    private Integer skuNum;

    @Column(name = "total_num")
    @ApiModelProperty(value = "预估件数")
    private Integer totalNum;

    @Column(name = "gross_weight")
    @ApiModelProperty(value = "毛重")
    private BigDecimal grossWeight;

    @Column(name = "net_weight")
    @ApiModelProperty(value = "净重")
    private BigDecimal netWeight;

    @Column(name = "total_price")
    @ApiModelProperty(value = "总金额")
    private BigDecimal totalPrice;

    @Column(name = "in_ware_hose")
    @ApiModelProperty(value = "入库仓")
    private String inWareHose;

    @Column(name = "entry_no")
    @ApiModelProperty(value = "报关单号")
    private String entryNo;

    @Column(name = "decl_no")
    @ApiModelProperty(value = "报检单号")
    private String declNo;

    @Column(name = "regulatory_way")
    @ApiModelProperty(value = "监管方式")
    private String regulatoryWay;

    @Column(name = "clear_type")
    @ApiModelProperty(value = "报关类型")
    private String clearType;

    @Column(name = "qd_code")
    @ApiModelProperty(value = "QD单号")
    private String qdCode;

    @Column(name = "ref_qd_code")
    @ApiModelProperty(value = "关联单证编码")
    private String refQdCode;

    @Column(name = "books_no")
    @ApiModelProperty(value = "账册编号")
    private String booksNo;

    @Column(name = "ref_books_no")
    @ApiModelProperty(value = "关联账册编号")
    private String refBooksNo;

    @Column(name = "trans_way")
    @ApiModelProperty(value = "运输方式")
    private String transWay;

    @Column(name = "in_port")
    @ApiModelProperty(value = "进境关别")
    private String inPort;

    @Column(name = "ship_country")
    @ApiModelProperty(value = "启运国")
    private String shipCountry;

    @Column(name = "clear_start_time")
    @ApiModelProperty(value = "清关开始时间")
    private Timestamp clearStartTime;

    @Column(name = "clear_end_time")
    @ApiModelProperty(value = "清关完成时间")
    private Timestamp clearEndTime;

    @Column(name = "finish_time")
    @ApiModelProperty(value = "服务完成时间")
    private Timestamp finishTime;

    @Column(name = "order_source")
    @ApiModelProperty(value = "单据来源")
    private String orderSource;

    public void copy(HezhuInfo source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}