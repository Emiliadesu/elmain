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

import javax.persistence.Column;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2021-07-06
**/
@Data
public class DailyStockDto implements Serializable {

    /** ID */
    private Long id;

    /** 客户ID */
    private Long customersId;

    /** 店铺ID */
    private Long shopId;

    /** 时间 */
    private String dayTime;

    /** WMS货主代码 */
    private String wmsCustomersCode;

    /** 货号 */
    private String goodsNo;

    /** 库位 */
    private String location;

    /** 批次号 */
    private String batchNo;

    /** 库存数量 */
    private Integer qty;

    /** WMS保存时间 */
    private Timestamp wmsTime;

    /** 创建时间 */
    private Timestamp createTime;

    /** 商品ID */
    private Long goodsId;

    /** 商品编码 */
    private String goodsCode;

    /** 条码 */
    private String barCode;

    /** 名称 */
    private String goodsName;

    /** WMS商品名称 */
    private String wmsGoodsName;

    private String customersName;

    private String shopName;

    private String stockStatus;

    private String prodTime;

    private String expTime;

    private String inTime;
}