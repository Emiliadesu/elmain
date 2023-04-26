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
package me.zhengjie.domain;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.io.Serializable;
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author le
* @date 2021-09-28
**/
@Entity
@Data
@Table(name="bus_douyin_mail_mark")
public class DouyinMailMark implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "order_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @Column(name = "customers_id")
    @ApiModelProperty(value = "客户ID")
    private Long customersId;

    @Column(name = "shop_id")
    @ApiModelProperty(value = "店铺ID")
    private String shopId;

    @Column(name = "supplier_id")
    @ApiModelProperty(value = "承运商ID")
    private String supplierId;

    @Column(name = "logistics_no")
    @ApiModelProperty(value = "运单号")
    private String logisticsNo;

    @Column(name = "add_mark")
    @ApiModelProperty(value = "大头笔")
    private String addMark;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @Column(name = "consignee")
    @ApiModelProperty(value = "收货人")
    private String consignee;

    @Column(name = "consignee_telephone")
    @ApiModelProperty(value = "收货电话")
    private String consigneeTelephone;

    @Column(name = "buyer_id_type")
    @ApiModelProperty(value = "订购人证件类型   1-身份证  2-其他")
    private String buyerIdType;

    @Column(name = "buyer_id_number")
    @ApiModelProperty(value = "订购人证件号码")
    private String buyerIdNumber;

    @Column(name = "ie_flag")
    @ApiModelProperty(value = "进出口标志   i-进口,e-出口")
    private String ieFlag;

    @Column(name = "wh_type")
    @ApiModelProperty(value = "0-存量  1-报税备货  2-海外集货  3-海外备货")
    private String whType;

    @Column(name = "ebp_code")
    @ApiModelProperty(value = "电商平台代码")
    private String ebpCode;

    @Column(name = "ebp_name")
    @ApiModelProperty(value = "电商平台名称")
    private String ebpName;

    @Column(name = "port_code")
    @ApiModelProperty(value = "关区代码")
    private String portCode;

    @Column(name = "scsp_warehouse_code")
    @ApiModelProperty(value = "服务商仓库编码")
    private String scspWarehouseCode;

    @Column(name = "consignee_address")
    @ApiModelProperty(value = "consigneeAddress")
    private String consigneeAddress;

    @Column(name = "province")
    @ApiModelProperty(value = "省")
    private String province;

    @Column(name = "city")
    @ApiModelProperty(value = "市")
    private String city;

    @Column(name = "district")
    @ApiModelProperty(value = "区")
    private String district;

    @Column(name = "consignee_addr")
    @ApiModelProperty(value = "收货地址")
    private String consigneeAddr;

    @Column(name = "is_success")
    @ApiModelProperty(value = "是否完成回传")
    private String isSuccess;

    @Column(name = "carrier_code")
    @ApiModelProperty(value = "快递编码")
    private String carrierCode;

    @Column(name = "four_pl")
    @ApiModelProperty(value = "是否4PL单")
    private String fourPl;


    @Transient
    private List<DouyinGoodsDetails>detailList;

    public void copy(DouyinMailMark source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
