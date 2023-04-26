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
import me.zhengjie.domain.WmsInstockItem;

import java.sql.Timestamp;
import java.io.Serializable;
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author 王淼
* @date 2020-12-08
**/
@Data
public class WmsInstockDto implements Serializable {

    private Long id;

    /** 入库单号，商家自行生成的唯一单号 */
    private String inOrderSn;
    /** 富勒ASN订单号 */
    private String asnNo;

    /** 单据类型，0:采购入库 1:调拨入库 2:销退入库 */
    private String inOrderType;

    /** 原单号(销退入库需填写) */
    private String originalNo;

    /** 报关单号 */
    private String declareNo;

    /** 报检单号 */
    private String inspectNo;

    /** 预期到货时间(yyyy-MM-dd HH:mm:ss) */
    private String expectArriveTime;

    /** 备注 */
    private String remark;

    /** 理货维度：0:件1:箱2:拖。暂时默认件 */
    private String tallyWay;

    /** 创建时间 */
    private Timestamp createTime;

    /** 创建商家 */
    private String createCustomer;

    /** 入库通知单状态 */
    private String inStatus;

    /** 状态最后变化时间戳 */
    private Long statusTime;

    /** 收货完成时间 */
    private Timestamp goodsUpper;

    /** 实际到货时间 */
    private Timestamp actualArriveTime;

    /** 同步流程是否完成 */
    private Boolean syncComplete;

    /** 附件链接 */
    private String fileLinks;

    private String channel;

    private List<WmsInstockItemDto> itemList;

    private ShopInfoDto shopInfo;

    private Long shopId;

    private String poNo;

    private String merchantId;

    private String warehouseId;

    private String accountNumber;

    private String superviseCode;

    private String tenantCode;
}
