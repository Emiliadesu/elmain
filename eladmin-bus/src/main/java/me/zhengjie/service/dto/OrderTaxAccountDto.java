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
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2021-12-11
**/
@Data
public class OrderTaxAccountDto implements Serializable {

    /** ID */
    private Long id;

    /** 订单号 */
    private String orderNo;

    /** LP单号 */
    private String lpNo;

    /** 运单号 */
    private String mailNo;

    /** 总署清单编号 */
    private String invtNo;

    /** 增值税 */
    private BigDecimal addTax;

    /** 消费税 */
    private BigDecimal consumptionTax;

    /** 总税 */
    private BigDecimal taxAmount;

    /** 订单出库时间 */
    private Timestamp orderTime;

    /** 创建时间 */
    private Timestamp createTime;
}