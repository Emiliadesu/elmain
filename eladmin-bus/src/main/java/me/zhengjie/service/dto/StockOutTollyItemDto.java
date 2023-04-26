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
* @author wangm
* @date 2021-03-28
**/
@Data
public class StockOutTollyItemDto implements Serializable {

    private Long id;

    /** 理货单号 */
    private StockOutTollySmallDto stockOutTolly;

    /** 理货sku */
    private String skuNo;

    /** 理货数量 */
    private Integer tallyNum;

    /** 正品数量 */
    private Integer avaliableNum;

    /** 残品数量 */
    private Integer defectNum;

    /** 单箱数量，箱规 */
    private Integer numPerBox;

    /** 过期日期 */
    private Timestamp expiryDate;

    /** 生产日期 */
    private Timestamp productDate;

    /** 批次号 */
    private String batchNo;

    /** 箱数 */
    private Integer boxNum;

    /** 毛重(g) */
    private Double grossWeight;

    /** 托盘尺寸 */
    private String traySize;

    /** 托盘材质 */
    private String trayMaterial;

    private String trayNo;

    private String purchaseOrderSn;

    private Timestamp inWarehouseTime;

    /** 是否已上传SN码 */
    private String uploaded;

    /** 货号仓库用 */
    private String goodsNo;

    /** 国际码 */
    private String barcode;

    /** 通知数量 */
    private Integer preTallyNum;
}
