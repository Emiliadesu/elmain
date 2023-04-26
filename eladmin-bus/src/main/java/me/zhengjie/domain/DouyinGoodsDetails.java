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
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author le
* @date 2021-09-28
**/
@Entity
@Data
@Table(name="bus_douyin_goods_details")
public class DouyinGoodsDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "item_no")
    @ApiModelProperty(value = "商品id")
    private String itemNo;

    @Column(name = "bar_code")
    @ApiModelProperty(value = "barCode")
    private String barCode;

    @Column(name = "item_name")
    @ApiModelProperty(value = "净重")
    private String itemName;

    @Column(name = "price")
    @ApiModelProperty(value = "price")
    private BigDecimal price;

    @Column(name = "qty")
    @ApiModelProperty(value = "数量")
    private Long qty;

    @Column(name = "currency")
    @ApiModelProperty(value = "货币类型")
    private String currency;

    @Column(name = "weight")
    @ApiModelProperty(value = "毛重")
    private String weight;

    @Column(name = "net_weight_qty")
    @ApiModelProperty(value = "netWeightQty")
    private String netWeightQty;

    @Column(name = "mark_id")
    @ApiModelProperty(value = "订单ID")
    private Long markId;

    @Column(name = "record_name")
    @ApiModelProperty(value = "备案名")
    private String recordName;

    public void copy(DouyinGoodsDetails source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
