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
import me.zhengjie.base.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.io.Serializable;
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2021-04-15
**/
@Entity
@Data
@Table(name="bus_base_sku")
public class BaseSku extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "customers_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "客户ID")
    private Long customersId;

    @Column(name = "shop_id",nullable = false)
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;

    @Column(name = "status",nullable = false)
    @ApiModelProperty(value = "状态")
    private Integer status;

    @Column(name = "goods_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "商品编码")
    private String goodsCode;

    @Column(name = "bar_code",nullable = false)
    @ApiModelProperty(value = "条形码")
    private String barCode;

    @Column(name = "goods_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @Column(name = "sn_control")
    @ApiModelProperty(value = "是否SN管理")
    private String snControl;

    @Column(name = "sale_l")
    @ApiModelProperty(value = "长")
    private BigDecimal saleL;

    @Column(name = "sale_w")
    @ApiModelProperty(value = "宽")
    private BigDecimal saleW;

    @Column(name = "sale_h")
    @ApiModelProperty(value = "高")
    private BigDecimal saleH;

    @Column(name = "sale_volume")
    @ApiModelProperty(value = "体积")
    private BigDecimal saleVolume;

    @Column(name = "sale_weight")
    @ApiModelProperty(value = "重量")
    private BigDecimal saleWeight;

    @Column(name = "pack_l")
    @ApiModelProperty(value = "箱长")
    private BigDecimal packL;

    @Column(name = "pack_w")
    @ApiModelProperty(value = "箱宽")
    private BigDecimal packW;

    @Column(name = "pack_h")
    @ApiModelProperty(value = "箱高")
    private BigDecimal packH;

    @Column(name = "pack_volume")
    @ApiModelProperty(value = "箱体积")
    private BigDecimal packVolume;

    @Column(name = "pack_weight")
    @ApiModelProperty(value = "箱重")
    private BigDecimal packWeight;

    @Column(name = "pack_num")
    @ApiModelProperty(value = "箱规")
    private Integer packNum;

    @Column(name = "goods_no")
    @ApiModelProperty(value = "海关货号")
    private String goodsNo;

    @Column(name = "part_no")
    @ApiModelProperty(value = "料号")
    private String partNo;

    @Column(name = "outer_goods_no")
    @ApiModelProperty(value = "外部货号")
    private String outerGoodsNo;

    @Column(name = "hs_code")
    @ApiModelProperty(value = "HS编码")
    private String hsCode;

    @Column(name = "goods_name_c",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "海关备案名中文")
    private String goodsNameC;

    @Column(name = "goods_name_e",nullable = false)
    @ApiModelProperty(value = "海关备案名英文")
    private String goodsNameE;

    @Column(name = "net_weight")
    @ApiModelProperty(value = "净重")
    private BigDecimal netWeight;

    @Column(name = "gross_weight")
    @ApiModelProperty(value = "毛重")
    private BigDecimal grossWeight;

    @Column(name = "lifecycle")
    @ApiModelProperty(value = "保质期天数")
    private Integer lifecycle;

    @Column(name = "legal_unit")
    @ApiModelProperty(value = "法一单位")
    private String legalUnit;

    @Column(name = "legal_unit_code")
    @ApiModelProperty(value = "法一单位代码")
    private String legalUnitCode;

    @Column(name = "legal_num")
    @ApiModelProperty(value = "法一数量")
    private BigDecimal legalNum;

    @Column(name = "second_unit")
    @ApiModelProperty(value = "法二单位")
    private String secondUnit;

    @Column(name = "second_unit_code")
    @ApiModelProperty(value = "法二单位代码")
    private String secondUnitCode;

    @Column(name = "second_num")
    @ApiModelProperty(value = "法二数量")
    private BigDecimal secondNum;

    @Column(name = "supplier")
    @ApiModelProperty(value = "供应商名称")
    private String supplier;

    @Column(name = "brand")
    @ApiModelProperty(value = "品牌")
    private String brand;

    @Column(name = "property")
    @ApiModelProperty(value = "规格型号")
    private String property;

    @Column(name = "declare_element")
    @ApiModelProperty(value = "申报要素")
    private String declareElement;

    @Column(name = "make_contry")
    @ApiModelProperty(value = "原产地")
    private String makeContry;

    @Column(name = "make_contry_code")
    @ApiModelProperty(value = "原产地代码")
    private String makeContryCode;

    @Column(name = "guse")
    @ApiModelProperty(value = "用途")
    private String guse;

    @Column(name = "gcomposition")
    @ApiModelProperty(value = "成分")
    private String gcomposition;

    @Column(name = "gfunction")
    @ApiModelProperty(value = "功能")
    private String gfunction;

    @Column(name = "unit")
    @ApiModelProperty(value = "申报单位")
    private String unit;

    @Column(name = "unit_code")
    @ApiModelProperty(value = "申报单位代码")
    private String unitCode;

    @Column(name = "remark")
    @ApiModelProperty(value = "商品备注")
    private String remark;

    @Column(name = "register_type")
    @ApiModelProperty(value = "备案模式")
    private String registerType;

    @Column(name = "is_new")
    @ApiModelProperty(value = "是否新品")
    private String isNew;

    @Column(name = "books_no")
    @ApiModelProperty(value = "账册编号")
    private String booksNo;

    @Column(name = "record_no")
    @ApiModelProperty(value = "备案序号")
    private String recordNo;

    @Column(name = "auditing_fail_reason")
    @ApiModelProperty(value = "审核不过原因")
    private String auditingFailReason;

    @Column(name = "is_gift")
    @ApiModelProperty(value = "是否赠品")
    private String isGift;

    @Column(name = "stock_unit")
    @ApiModelProperty(value = "商品单位")
    private String stockUnit;

    @Column(name = "warehouse_code")
    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;

    @Column(name = "platform_code")
    @ApiModelProperty(value = "电商平台代码")
    private String platformCode;


    public void copy(BaseSku source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
