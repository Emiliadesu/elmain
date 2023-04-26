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
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-03-21
**/
@Data
public class ByteDanceDto implements Serializable {

    private Long id;

    /** 订单编号 */
    private String orderId;

    /** 电商平台代码 */
    private String ebpCode;

    /** 电商平台名 */
    private String ebpName;

    /** 电商企业代码 */
    private String ebcCode;

    /** 电商企业名 */
    private String ebcName;

    /** 店铺在电商平台的id */
    private String shopId;

    /** 店铺在电商平台的名称 */
    private String shopName;

    /** 进出口标识 */
    private String ieFlag;

    /** 通关模式 */
    private String customsClearType;

    /** 申报海关代码 */
    private String customsCode;

    /** 口岸海关代码 */
    private String portCode;

    /** 商家仓库编码 */
    private String warehouseCode;

    /** 商品实际成交价， 含非现金抵扣金额（商品不含税价*商品数量）单位是分，数据库要求元 */
    private BigDecimal goodsValue;

    /** 运杂费（含物流保费）免邮传0，单位是分，数据库要求元 */
    private BigDecimal freight;

    /** 非现金抵扣金额（不含支付满减）使用积分等非现金支付金额，无则填写 "0" 单位是分，数据库要求元 */
    private BigDecimal discount;

    /** 代扣税款  企业预先代扣的税款金额，无则填写“0” 单位是分,数据库要求元 */
    private BigDecimal taxTotal;

    /** 实际支付金额（商品价格+运杂费+代扣税款- 非现金抵扣金额）单位是分,数据库要求元 */
    private BigDecimal acturalPaid;

    /** 物流保费（物流保价费）  一般传0  单位是分,数据库要求元 */
    private BigDecimal insuredFee;

    /** 币制 限定为人民币，填写“142” */
    private String currency;

    /** 订购人注册号 */
    private String buyerRegNo;

    /** 订购人姓名 */
    private String buyerName;

    /** 订购人电话 */
    private String buyerTelephone;

    /** 订购人证件类型 */
    private String buyerIdType;

    /** 订购人证件号码 */
    private String buyerIdNumber;

    /** 收货人 */
    private String consignee;

    /** 收货人电话 */
    private String consigneeTelephone;

    /** 收货地址(JSON字符串) */
    private String consigneeAddress;

    /** 支付企业的海关注册登记编号 */
    private String payCode;

    /** 支付企业在海关注册登记的企业名称 */
    private String payName;

    /** 支付流水号 */
    private String payTransactionId;

    /** 拉单时间 */
    private Timestamp createTime;

    private Boolean isConfirm;

    private String province;

    private String city;

    private String town;

    private String detail;

    private String payTime;

    /** 清关状态回传标记，null：未开始,0开始，1结束,-1失败结尾 */
    private String cleanStatus;

    /** 订单状态,-1取消，0未发货,1打包,2已发货 */
    private Integer orderStatus;

    private List<ByteDanceItemDto>items;
}
