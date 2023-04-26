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
import java.io.Serializable;
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-03-28
**/
@Data
public class StockOutTollyDto implements Serializable {

    private Long id;

    /** 入库单号 */
    private WmsOutstockDto wmsOutstock;

    /** 理货单号 */
    private String tallyOrderSn;

    /** 理货品种数 */
    private Integer skuNum;

    /** 理货总件数 */
    private Integer totalNum;

    /** 当前理货次数 */
    private Integer currentNum;

    /** 理货开始时间 */
    private String startTime;

    /** 理货完成时间 */
    private String endTime;

    /** 出库包装方式 (0:散箱 1:拖) */
    private String packWay;

    /** 出库包装数量 */
    private Integer packNum;

    /** 托盘高度 */
    private Double trayHeight;

    /** 总重量g */
    private Double totalWeight;

    /** 是否已通知,0否1是 */
    private String status;

    private String reason;

    /** 理论重量 */
    private String grossWeight;

    /** 租户编码 */
    private String tenantCode;

    /** 仓库id */
    private String warehouseId;

    /** 申报单号 */
    private String parentDoCode;

    private List<StockOutTollyItemDto> stockOutTollyItems;

    private AsnHeaderDto asnHeader;
}
