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

import com.alibaba.fastjson.annotation.JSONField;
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
* @date 2020-12-18
**/
@Entity
@Data
@Table(name="bus_wms_outstock_item")
public class WmsOutstockItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Transient
    private WmsOutstock wmsOutstock;

    @Column(name = "out_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "outId")
    private Long outId;

    @Column(name = "goods_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "仓库货号")
    @JSONField(name = "productId")
    private String goodsNo;

    @Column(name = "goods_line_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "商品行")
    @JSONField(name = "goodsLine")
    private String goodsLineNo;

    @Column(name = "sku_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "供应商货号")
    private String skuNo;

    @Column(name = "bar_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "条码")
    private String barCode;

    @Column(name = "goods_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @Column(name = "qty",nullable = false)
    @NotNull
    @ApiModelProperty(value = "商品数量")
    private Integer qty;

    @Column(name = "nondefective_num",nullable = false)
    @NotNull
    @ApiModelProperty(value = "正品数量")
    private Integer nondefectiveNum;

    @Column(name = "damage_num",nullable = false)
    @NotNull
    @ApiModelProperty(value = "残品数量")
    private Integer damageNum;

    @Column(name = "batch_no")
    @ApiModelProperty(value = "指定生产批次")
    private String batchNo;

    @Column(name = "exp_start_time")
    @ApiModelProperty(value = "指定效期开始(yyyy-MM-dd HH:mm:ss)")
    private String expStartTime;

    @Column(name = "item_id")
    @ApiModelProperty(value = "商品id")
    private String itemId;

    @Column(name = "exp_end_time")
    @ApiModelProperty(value = "指定效期结束(yyyy-MM-dd HH:mm:ss)")
    private String expEndTime;

    @Column(name = "need_sn")
    @ApiModelProperty(value = "是否回传序列号")
    private Integer needSn;

    @Column(name = "product_id")
    @ApiModelProperty(value = "商品编码")
    private String productId;

    public void copy(WmsOutstockItem source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
