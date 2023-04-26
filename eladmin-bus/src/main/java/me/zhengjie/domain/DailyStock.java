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
* @date 2021-07-06
**/
@Entity
@Data
@Table(name="bus_daily_stock")
public class DailyStock implements Serializable {

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

    @Column(name = "customers_name")
    @ApiModelProperty(value = "客户名称")
    private String customersName;

    @Column(name = "shop_name")
    @ApiModelProperty(value = "店铺名称")
    private String shopName;

    @Column(name = "day_time")
    @ApiModelProperty(value = "时间")
    private String dayTime;

    @Column(name = "wms_customers_code")
    @ApiModelProperty(value = "WMS货主代码")
    private String wmsCustomersCode;

    @Column(name = "goods_no")
    @ApiModelProperty(value = "货号")
    private String goodsNo;

    @Column(name = "location")
    @ApiModelProperty(value = "库位")
    private String location;

    @Column(name = "batch_no")
    @ApiModelProperty(value = "批次号")
    private String batchNo;

    @Column(name = "stock_status")
    @ApiModelProperty(value = "商品状态")
    private String stockStatus;

    @Column(name = "prod_time")
    @ApiModelProperty(value = "生产日期")
    private String prodTime;

    @Column(name = "exp_time")
    @ApiModelProperty(value = "失效日期")
    private String expTime;

    @Column(name = "in_time")
    @ApiModelProperty(value = "入库日期")
    private String inTime;

    @Column(name = "qty")
    @ApiModelProperty(value = "库存数量")
    private Integer qty;

    @Column(name = "wms_time")
    @ApiModelProperty(value = "WMS保存时间")
    private Timestamp wmsTime;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @Column(name = "goods_id")
    @ApiModelProperty(value = "商品ID")
    private Long goodsId;

    @Column(name = "goods_code")
    @ApiModelProperty(value = "商品编码")
    private String goodsCode;

    @Column(name = "bar_code")
    @ApiModelProperty(value = "条码")
    private String barCode;

    @Column(name = "goods_name")
    @ApiModelProperty(value = "名称")
    private String goodsName;

    @Column(name = "wms_goods_name")
    @ApiModelProperty(value = "WMS商品名称")
    private String wmsGoodsName;

    public void copy(DailyStock source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}