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
* @author AddValueOrderDetails
* @date 2021-08-05
**/
@Entity
@Data
@Table(name="bus_add_value_order_details")
public class AddValueOrderDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "order_id")
    @NotNull
    @ApiModelProperty(value = "上游明细ID")
    private Long orderId;

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
    @ApiModelProperty(value = "条码")
    private String barCode;

    @Column(name = "goods_name")
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @Column(name = "default01")
    @ApiModelProperty(value = "客户批次号")
    private String default01;

    @Column(name = "qty",nullable = false)
    @NotNull
    @ApiModelProperty(value = "要求加工数量")
    private Integer qty;

    @Column(name = "finish_qty")
    @ApiModelProperty(value = "实际完成数量")
    private Integer finishQty;

    public void copy(AddValueOrderDetails source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}