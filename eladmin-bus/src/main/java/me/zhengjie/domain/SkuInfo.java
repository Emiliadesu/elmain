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
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2021-04-15
**/
@Entity
@Data
@Table(name="bus_sku_info")
public class SkuInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "create_time")
    @ApiModelProperty(value = "添加时间")
    private Timestamp createTime;

    @Column(name = "customers_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "客户ID")
    private Long customersId;

    @Column(name = "shop_id",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "店铺ID")
    private String shopId;

    @Column(name = "goods_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "商品编码")
    private String goodsCode;

    @Column(name = "bar_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "条形码")
    private String barCode;

    @Column(name = "goods_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @Column(name = "sn_control",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "是否SN管理")
    private String snControl;

    @Column(name = "sale_l")
    @ApiModelProperty(value = "长")
    private String saleL;

    @Column(name = "sale_w")
    @ApiModelProperty(value = "宽")
    private String saleW;

    @Column(name = "sale_h")
    @ApiModelProperty(value = "高")
    private String saleH;

    @Column(name = "sale_weight")
    @ApiModelProperty(value = "重量")
    private String saleWeight;

    @Column(name = "pack_l")
    @ApiModelProperty(value = "箱长")
    private Timestamp packL;

    @Column(name = "pack_w")
    @ApiModelProperty(value = "箱宽")
    private String packW;

    @Column(name = "pack_h")
    @ApiModelProperty(value = "箱高")
    private String packH;

    @Column(name = "pack_weight")
    @ApiModelProperty(value = "箱重")
    private String packWeight;

    @Column(name = "pack_num")
    @ApiModelProperty(value = "箱规")
    private String packNum;

    @Column(name = "create_by")
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @Column(name = "update_by")
    @ApiModelProperty(value = "修改人")
    private String updateBy;

    @Column(name = "update_time")
    @ApiModelProperty(value = "修改时间")
    private Timestamp updateTime;

    public void copy(SkuInfo source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}