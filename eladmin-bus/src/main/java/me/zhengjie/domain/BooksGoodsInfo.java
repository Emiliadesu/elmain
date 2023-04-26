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
* @author 王淼
* @date 2020-10-13
**/
@Entity
@Data
@Table(name="bus_books_goods_info")
public class BooksGoodsInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "books_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "账册编号")
    private String booksNo;

    @Column(name = "goods_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "货号")
    private String goodsNo;

    @Column(name = "seq_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "备案序号")
    private String seqNo;

    @Column(name = "part_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "料号")
    private String partNo;

    @Column(name = "hs_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "HS编码")
    private String hsCode;

    @Column(name = "goods_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "货品名称")
    private String goodsName;

    @Column(name = "spec",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "商品规格型号")
    private String spec;

    @Column(name = "declare_unit",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "申报计量单位")
    private String declareUnit;

    @Column(name = "mea_unit_one")
    @ApiModelProperty(value = "法定计量单位")
    private String meaUnitOne;

    @Column(name = "mea_unit_two")
    @ApiModelProperty(value = "法定第二计量单位")
    private String meaUnitTwo;

    @Column(name = "mea_num_one")
    @ApiModelProperty(value = "法定数量")
    private String meaNumOne;

    @Column(name = "mea_num_two")
    @ApiModelProperty(value = "法定第二数量")
    private String meaNumTwo;

    @Column(name = "currency_system")
    @ApiModelProperty(value = "币制")
    private String currencySystem;

    @Column(name = "tax_way")
    @ApiModelProperty(value = "征免方式")
    private String taxWay;

    @Column(name = "destination_country")
    @ApiModelProperty(value = "最终目的国")
    private String destinationCountry;

    @Column(name = "make_country")
    @ApiModelProperty(value = "原产国")
    private String makeCountry;

    public void copy(BooksGoodsInfo source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}