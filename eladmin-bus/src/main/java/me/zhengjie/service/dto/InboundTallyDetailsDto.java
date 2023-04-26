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
* @author luob
* @date 2021-06-16
**/
@Data
public class InboundTallyDetailsDto implements Serializable {

    /** ID */
    private Long id;

    /** 订单ID */
    private Long tallyId;

    /** 理货单号 */
    private String tallyNo;

    /** 货品ID */
    private Long goodsId;

    /** 货品编码 */
    private String goodsCode;

    /** 海关货号 */
    private String goodsNo;

    /** 条码 */
    private String barCode;

    /** 商品名称 */
    private String goodsName;

    /** 货品属性 */
    private Integer goodsQuality;

    /** 理货数量 */
    private Integer tallyNum;

    /** 生产日期 */
    private String productDate;

    /** 失效日期 */
    private String expiryDate;

    /** 生产批次 */
    private String batchNo;

    /** 图片地址 */
    private String picUrl;
}