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

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author 王淼
* @date 2020-12-08
**/
@Entity
@Getter
@Setter
@Table(name="bus_wms_instock_item")
public class WmsInstockItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Transient
    private WmsInstock wmsInstock;

    @Column(name = "in_id")
    @ApiModelProperty(value = "入库通知单id")
    private Long inId;

    @Column(name = "sku_no")
    @ApiModelProperty(value = "供应商货号")
    private String skuNo;

    @Column(name = "goods_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "仓库货号")
    private String goodsNo;

    @Column(name = "goods_line_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "商品行")
    @JSONField(name = "goodsLine")
    private String goodsLineNo;

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

    @Column(name = "item_id")
    @ApiModelProperty(value = "商品Id")
    private String itemId;

    @Column(name = "product_id",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "商品编码")
    private String productId;

    @Column(name = "customer_batch_no")
    @ApiModelProperty(value = "客户批次号")
    private String customerBatchNo;

    @Column(name = "production_time")
    @ApiModelProperty(value = "生产日期")
    private String productionTime;

    @Column(name = "expire_date")
    @ApiModelProperty(value = "失效日期")
    private String expireDate;

    @Column(name = "sub_type")
    @ApiModelProperty(value = "细分类型")
    private String subType;

    @Column(name = "need_sn")
    @ApiModelProperty(value = "是否回传序列号")
    private Integer needSn;
}
