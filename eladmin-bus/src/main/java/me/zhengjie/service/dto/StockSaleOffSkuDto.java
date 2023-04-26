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

import lombok.Data;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author leningzhou
* @date 2022-12-15
**/
@Data
public class StockSaleOffSkuDto implements Serializable {

    /** ID */
    private Long id;

    /** 客户ID */
    private Long customersId;

    /** 店铺ID */
    private Long shopId;

    /** 海关货号 */
    private String goodsNo;

    /** 外部货号 */
    private String outerGoodsNo;

    /** 条形码 */
    private String barCode;

    /** 商品名称 */
    private String goodsName;

    /** 库位 */
    private String warehouse;

    /** 总库存 */
    private Integer sumStock;

    /** 占用库存 */
    private Integer holdStock;

    /** 可用库存 */
    private Integer useStock;

    /** 生产日期 */
    private Timestamp productionTime;

    /** 失效日期 */
    private Timestamp invalidTime;

    /** 入库日期 */
    private Timestamp putinTime;

    /** 产品批次号 */
    private String batchCode;

    /** 库存属性 */
    private String stockAttribute;

    /** 禁售天数 */
    private Integer saleOffDay;

    /** 是否禁售 */
    private String isSaleFail;
}