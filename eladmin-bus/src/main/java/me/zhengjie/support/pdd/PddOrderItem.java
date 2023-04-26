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
package me.zhengjie.support.pdd;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.persistence.*;
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-06-10
**/
@Entity
@Data
@Table(name="bus_pdd_order_item")
public class PddOrderItem implements Serializable {

    @Id
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "pdd_order_id")
    @ApiModelProperty(value = "拼多多订单id")
    private Long pddOrderId;

    @Column(name = "goods_price")
    @ApiModelProperty(value = "商品单件 单价：元")
    private BigDecimal goodsPrice;

    @Column(name = "goods_name")
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @Column(name = "outer_goods_id")
    @ApiModelProperty(value = "商品维度外部编码，注意：编辑商品后必须等待商品审核通过后方可生效，订单中商品信息为交易快照的商品信息。")
    private String outerGoodsId;

    @Column(name = "outer_id")
    @ApiModelProperty(value = "sku维度商家外部编码，注意：编辑商品后必须等待商品审核通过后方可生效，订单中商品信息为交易快照的商品信息")
    private String outerId;

    @Column(name = "sku_id")
    @ApiModelProperty(value = "商品sku编码")
    private String skuId;

    @Column(name = "goods_id")
    @ApiModelProperty(value = "商品编码")
    private String goodsId;

    @Column(name = "goods_count")
    @ApiModelProperty(value = "商品数量")
    private Integer goodsCount;

    public void copy(PddOrderItem source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
