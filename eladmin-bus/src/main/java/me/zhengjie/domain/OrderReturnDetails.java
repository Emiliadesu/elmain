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
* @date 2021-04-14
**/
@Entity
@Data
@Table(name="bus_order_return_details")
public class OrderReturnDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "return_id")
    @ApiModelProperty(value = "退货单ID")
    private Long returnId;

    @Column(name = "logistics_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "物流订单号")
    private String logisticsNo;

    @Column(name = "goods_id")
    @ApiModelProperty(value = "货品ID")
    private Long goodsId;

    @Column(name = "goods_code")
    @ApiModelProperty(value = "货品编码")
    private String goodsCode;

    @Column(name = "goods_no")
    @ApiModelProperty(value = "海关货号")
    private String goodsNo;

    @Column(name = "bar_code")
    @ApiModelProperty(value = "条形码")
    private String barCode;

    @Column(name = "hs_code")
    @ApiModelProperty(value = "HS编码")
    private String hsCode;

    @Column(name = "font_goods_name")
    @ApiModelProperty(value = "前端商品名称")
    private String fontGoodsName;

    @Column(name = "goods_name")
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @Column(name = "qty")
    @ApiModelProperty(value = "数量")
    private String qty;

    @Column(name = "tax_amount")
    @ApiModelProperty(value = "总税额")
    private String taxAmount;

    @Column(name = "normal_num")
    @ApiModelProperty(value = "正品数量")
    private Integer normalNum;

    @Column(name = "damaged_num")
    @ApiModelProperty(value = "残品数量")
    private Integer damagedNum;

    @Column(name = "total_num")
    @ApiModelProperty(value = "总数量")
    private Integer totalNum;

    @Column(name = "damaged_reason")
    @ApiModelProperty(value = "残次原因")
    private String damagedReason;

    public void copy(OrderReturnDetails source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}