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

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2021-08-26
**/
@Entity
@Data
@Table(name="bus_hezhu_details")
public class HezhuDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "order_id")
    @ApiModelProperty(value = "清关ID")
    private Long orderId;

    @Column(name = "order_no")
    @ApiModelProperty(value = "单据编号")
    private String orderNo;

    @Column(name = "seq_no")
    @ApiModelProperty(value = "序号")
    private Integer seqNo;

    @Column(name = "goods_id")
    @ApiModelProperty(value = "货品ID")
    private Long goodsId;

    @Column(name = "goods_code")
    @ApiModelProperty(value = "货品编码")
    private String goodsCode;

    @Column(name = "goods_no")
    @ApiModelProperty(value = "海关货号")
    private String goodsNo;

    @Column(name = "outer_goods_no")
    @ApiModelProperty(value = "外部货号")
    private String outerGoodsNo;

    @Column(name = "hs_code")
    @ApiModelProperty(value = "HS编码")
    private String hsCode;

    @Column(name = "goods_name")
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @Column(name = "record_no")
    @ApiModelProperty(value = "备案序号")
    private String recordNo;

    @Column(name = "net_weight")
    @ApiModelProperty(value = "净重（千克）")
    private String netWeight;

    @Column(name = "gross_weight")
    @ApiModelProperty(value = "毛重（千克）")
    private String grossWeight;

    @Column(name = "legal_unit")
    @ApiModelProperty(value = "法一单位")
    private String legalUnit;

    @Column(name = "legal_unit_code")
    @ApiModelProperty(value = "法一单位代码")
    private String legalUnitCode;

    @Column(name = "legal_num")
    @ApiModelProperty(value = "法一数量")
    private String legalNum;

    @Column(name = "second_unit")
    @ApiModelProperty(value = "法二单位")
    private String secondUnit;

    @Column(name = "second_unit_code")
    @ApiModelProperty(value = "法二单位代码")
    private String secondUnitCode;

    @Column(name = "second_num")
    @ApiModelProperty(value = "法二数量")
    private String secondNum;

    @Column(name = "qty")
    @ApiModelProperty(value = "数量")
    private String qty;

    @Column(name = "unit")
    @ApiModelProperty(value = "计量单位")
    private String unit;

    @Column(name = "price")
    @ApiModelProperty(value = "商品单价")
    private String price;

    @Column(name = "total_price")
    @ApiModelProperty(value = "商品总价")
    private String totalPrice;

    @Column(name = "property")
    @ApiModelProperty(value = "规格型号")
    private String property;

    @Column(name = "currency")
    @ApiModelProperty(value = "币种")
    private String currency;

    @Column(name = "make_country")
    @ApiModelProperty(value = "原产国")
    private String makeCountry;

    public void copy(HezhuDetails source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}