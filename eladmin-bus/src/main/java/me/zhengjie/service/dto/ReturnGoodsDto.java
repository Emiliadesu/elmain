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
* @author lh
* @date 2021-01-04
**/
@Data
public class ReturnGoodsDto implements Serializable {

    private Long id;

    /** 运单号 */
    private String tmsOrderCode;

    /** 物流公司 */
    private String tmsCompany;

    /** 是否原单退回 */
    private String isOrigin;

    /** 原订单号 */
    private String originOrderNo;

    /** 原订单类型 */
    private String originOrderType;

    /** 电商 */
    private String owner;

    /** 店铺 */
    private String ownerShop;

    /** 处理方式 */
    private String processWay;

    /** 备注 */
    private String remark;

    /** 货品去向 */
    private String goodsGoWhere;

    /** 寄回单号 */
    private String returnOwnerOrderNo;

    /** 寄回时间 */
    private Timestamp returnOwnerTime;

    /** 是否完成 */
    private String status;

    /** 登记人 */
    private String createUser;

    /** 登记时间 */
    private Timestamp createTime;

    /** 最后修改者 */
    private String modifyUser;

    /** 最后修改时间 */
    private Timestamp modifyTime;
}