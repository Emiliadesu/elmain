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
import me.zhengjie.domain.ShopInfo;
import me.zhengjie.domain.WmsOutstockItem;

import java.sql.Timestamp;
import java.io.Serializable;
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author 王淼
* @date 2020-12-04
**/
@Data
public class WmsOutstockDto implements Serializable {

    private Long id;

    /** 出库单号 */
    private String outOrderSn;

    /**  富勒SO单号*/
    private String fluxOrderNo;

    /** SO订单号 */
    private String soNo;

    /** 单据类型 */
    private String outOrderType;

    /** 出库联系人 */
    private String receiver;

    /** 出库联系电话 */
    private String phone;

    /** 配送地址 */
    private String address;

    /** 运输方式 */
    private String transportWay;

    /** 托盘要求 */
    private String pallet;

    /** 打托高度 */
    private String palletizedHeight;

    /** 预期理货完成时间(yyyy-MM-dd HH:mm:ss) */
    private String expectTallyTime;

    /** 预期出库时间 */
    private String expectShipTime;

    /** 理货维度 */
    private String tallyWay;

    /** 备注 */
    private String remark;

    /** 附件地址(理货要求等) */
    private String fileLink;

    /** 创建时间 */
    private Timestamp createTime;

    /** 创建商家 */
    private String createCustomer;

    /** 出库通知单状态 */
    private String outStatus;

    /** 出库时间 */
    private Timestamp goodsUpper;

    /** 状态最后变化时间戳 */
    private Long statusTime;

    /** 同步流程是否完成 */
    private Boolean syncComplete;

    /** 渠道 */
    private String channel;

    /** 入库理货单号-调拨单完成之前必须有 */
    private String inTallySn;

    /** 出库理货单号 */
    private String outTallySn;

    private List<WmsOutstockItemDto> itemList;

    private ShopInfoDto shopInfo;

    private Long shopId;

    private String warehouseId;

    private String tenantCode;

    private String superviseCode;

    private String accountNumber;

    private String merchantId;

    private String loadNo;
}
