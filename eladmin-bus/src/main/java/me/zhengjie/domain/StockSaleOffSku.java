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
* @author leningzhou
* @date 2022-12-15
**/
@Entity
@Data
@Table(name="bus_stock_sale_off_sku")
public class StockSaleOffSku implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "customers_id")
    @ApiModelProperty(value = "客户ID")
    private Long customersId;

    @Column(name = "shop_id")
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;

    @Column(name = "goods_no")
    @ApiModelProperty(value = "海关货号")
    private String goodsNo;

    @Column(name = "outer_goods_no")
    @ApiModelProperty(value = "外部货号")
    private String outerGoodsNo;

    @Column(name = "bar_code")
    @ApiModelProperty(value = "条形码")
    private String barCode;

    @Column(name = "goods_name")
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @Column(name = "warehouse")
    @ApiModelProperty(value = "库位")
    private String warehouse;

    @Column(name = "sum_stock")
    @ApiModelProperty(value = "总库存")
    private Integer sumStock;

    @Column(name = "hold_stock")
    @ApiModelProperty(value = "占用库存")
    private Integer holdStock;

    @Column(name = "use_stock")
    @ApiModelProperty(value = "可用库存")
    private Integer useStock;

    @Column(name = "production_time")
    @ApiModelProperty(value = "生产日期")
    private Timestamp productionTime;

    @Column(name = "invalid_time")
    @ApiModelProperty(value = "失效日期")
    private Timestamp invalidTime;

    @Column(name = "putin_time")
    @ApiModelProperty(value = "入库日期")
    private Timestamp putinTime;

    @Column(name = "batch_code")
    @ApiModelProperty(value = "产品批次号")
    private String batchCode;

    @Column(name = "stock_attribute")
    @ApiModelProperty(value = "库存属性")
    private String stockAttribute;

    @Column(name = "sale_off_day")
    @ApiModelProperty(value = "禁售天数")
    private Integer saleOffDay;

    @Column(name = "is_sale_fail")
    @ApiModelProperty(value = "是否禁售")
    private String isSaleFail;

    public void copy(StockSaleOffSku source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}