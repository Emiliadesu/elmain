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
package me.zhengjie.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import me.zhengjie.domain.BaseSku;

import javax.persistence.Column;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2021-04-15
**/
@Data
public class BaseSkuDto implements Serializable {

    /** ID */
    private Long id;

    /** 客户ID */
    private Long customersId;

    /** 店铺ID */
    private Long shopId;

    private Integer status;

    /** 商品编码 */
    private String goodsCode;

    /** 条形码 */
    private String barCode;

    /** 商品名称 */
    private String goodsName;

    /** 是否SN管理 */
    private String snControl;

    /** 长 */
    private BigDecimal saleL;

    /** 宽 */
    private BigDecimal saleW;

    /** 高 */
    private BigDecimal saleH;

    private BigDecimal saleVolume;

    /** 重量 */
    private BigDecimal saleWeight;

    /** 箱长 */
    private BigDecimal packL;

    /** 箱宽 */
    private BigDecimal packW;

    /** 箱高 */
    private BigDecimal packH;

    private BigDecimal packVolume;

    /** 箱重 */
    private BigDecimal packWeight;

    /** 箱规 */
    private Integer packNum;

    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    private Timestamp createTime;

    /** 修改人 */
    private String updateBy;

    /** 修改时间 */
    private Timestamp updateTime;

    /** 海关货号 */
    private String goodsNo;

    private String outerGoodsNo;

    private String hsCode;

    /** 海关备案名中文 */
    private String goodsNameC;

    /** 海关备案名英文 */
    private String goodsNameE;

    /** 净重 */
    private BigDecimal netWeight;

    /** 毛重 */
    private BigDecimal grossWeight;

    private Integer lifecycle;

    /** 法一单位 */
    private String legalUnit;

    /** 法一单位代码 */
    private String legalUnitCode;

    /** 法一数量 */
    private BigDecimal legalNum;

    /** 法二单位 */
    private String secondUnit;

    /** 法二单位代码 */
    private String secondUnitCode;

    /** 法二数量 */
    private BigDecimal secondNum;

    /** 供应商名称 */
    private String supplier;

    /** 品牌 */
    private String brand;

    /** 规格型号 */
    private String property;

    /** 原产地 */
    private String makeContry;

    /** 用途 */
    private String guse;

    /** 成分 */
    private String gcomposition;

    /** 功能 */
    private String gfunction;

    /** 申报单位 */
    private String unit;

    /** 申报单位代码 */
    private String unitCode;

    /** 商品备注 */
    private String remark;

    private String registerType;

    private String isNew;

    private String auditingFailReason;

    private String booksNo;

    private String recordNo;

    private String isGift;

    private String warehouseCode;

    private String platformCode;


}