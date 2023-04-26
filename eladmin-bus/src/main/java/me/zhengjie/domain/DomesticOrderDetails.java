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
* @date 2022-04-11
**/
@Entity
@Data
@Table(name="bus_domestic_order_details")
public class DomesticOrderDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "order_id")
    @ApiModelProperty(value = "订单ID")
    private Long orderId;

    @Column(name = "order_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @Column(name = "goods_id")
    @ApiModelProperty(value = "货品ID")
    private Long goodsId;

    @Column(name = "goods_code")
    @ApiModelProperty(value = "货品编码")
    private String goodsCode;

    @Column(name = "goods_no")
    @ApiModelProperty(value = "海关货号")
    private String goodsNo;

    @Column(name = "font_goods_name")
    @ApiModelProperty(value = "前端商品名称")
    private String fontGoodsName;

    @Column(name = "goods_name")
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @Column(name = "qty")
    @ApiModelProperty(value = "数量")
    private String qty;

    public void copy(DomesticOrderDetails source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}