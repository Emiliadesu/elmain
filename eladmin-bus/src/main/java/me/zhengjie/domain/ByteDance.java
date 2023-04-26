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
import java.math.BigDecimal;
import java.io.Serializable;
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-03-21
**/
@Entity
@Data
@Table(name="bus_byte_dance")
public class ByteDance implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "order_id",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "订单编号")
    private String orderId;

    @Column(name = "ebp_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "电商平台代码")
    private String ebpCode;

    @Column(name = "ebp_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "电商平台名")
    private String ebpName;

    @Column(name = "ebc_code")
    @ApiModelProperty(value = "电商企业代码")
    private String ebcCode;

    @Column(name = "ebc_name")
    @ApiModelProperty(value = "电商企业名")
    private String ebcName;

    @Column(name = "shop_id",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "店铺在电商平台的id")
    private String shopId;

    @Column(name = "shop_name")
    @ApiModelProperty(value = "店铺在电商平台的名称")
    private String shopName;

    @Column(name = "ie_flag")
    @ApiModelProperty(value = "进出口标识")
    private String ieFlag;

    @Column(name = "customs_clear_type")
    @ApiModelProperty(value = "通关模式")
    private String customsClearType;

    @Column(name = "customs_code")
    @ApiModelProperty(value = "申报海关代码")
    private String customsCode;

    @Column(name = "port_code")
    @ApiModelProperty(value = "口岸海关代码")
    private String portCode;

    @Column(name = "warehouse_code")
    @ApiModelProperty(value = "商家仓库编码")
    private String warehouseCode;

    @Column(name = "goods_value")
    @ApiModelProperty(value = "商品实际成交价， 含非现金抵扣金额（商品不含税价*商品数量）单位是分，数据库要求元")
    private BigDecimal goodsValue;

    @Column(name = "freight")
    @ApiModelProperty(value = "运杂费（含物流保费）免邮传0，单位是分，数据库要求元")
    private BigDecimal freight;

    @Column(name = "discount")
    @ApiModelProperty(value = "非现金抵扣金额（不含支付满减）使用积分等非现金支付金额，无则填写 \"0\" 单位是分，数据库要求元")
    private BigDecimal discount;

    @Column(name = "tax_total")
    @ApiModelProperty(value = "代扣税款  企业预先代扣的税款金额，无则填写“0” 单位是分,数据库要求元")
    private BigDecimal taxTotal;

    @Column(name = "actural_paid")
    @ApiModelProperty(value = "实际支付金额（商品价格+运杂费+代扣税款- 非现金抵扣金额）单位是分,数据库要求元")
    private BigDecimal acturalPaid;

    @Column(name = "insured_fee")
    @ApiModelProperty(value = "物流保费（物流保价费）  一般传0  单位是分,数据库要求元")
    private BigDecimal insuredFee;

    @Column(name = "currency")
    @ApiModelProperty(value = "币制 限定为人民币，填写“142”")
    private String currency;

    @Column(name = "buyer_reg_no")
    @ApiModelProperty(value = "订购人注册号")
    private String buyerRegNo;

    @Column(name = "buyer_name")
    @ApiModelProperty(value = "订购人姓名")
    private String buyerName;

    @Column(name = "buyer_telephone")
    @ApiModelProperty(value = "订购人电话")
    private String buyerTelephone;

    @Column(name = "buyer_id_type")
    @ApiModelProperty(value = "订购人证件类型")
    private String buyerIdType;

    @Column(name = "buyer_id_number")
    @ApiModelProperty(value = "订购人证件号码")
    private String buyerIdNumber;

    @Column(name = "consignee")
    @ApiModelProperty(value = "收货人")
    private String consignee;

    @Column(name = "consignee_telephone")
    @ApiModelProperty(value = "收货人电话")
    private String consigneeTelephone;

    @Column(name = "consignee_address")
    @ApiModelProperty(value = "收货地址(JSON字符串)")
    private String consigneeAddress;

    @Column(name = "pay_code")
    @ApiModelProperty(value = "支付企业的海关注册登记编号")
    private String payCode;

    @Column(name = "pay_name")
    @ApiModelProperty(value = "支付企业在海关注册登记的企业名称")
    private String payName;

    @Column(name = "pay_transaction_id")
    @ApiModelProperty(value = "支付流水号")
    private String payTransactionId;

    @Column(name = "create_time",nullable = false)
    @NotNull
    @ApiModelProperty(value = "拉单时间")
    private Timestamp createTime;

    @Column(name = "is_confirm",nullable = false)
    @NotNull
    @ApiModelProperty(value = "isConfirm")
    private Boolean isConfirm;

    @Column(name = "province",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "province")
    private String province;

    @Column(name = "city",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "city")
    private String city;

    @Column(name = "town",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "town")
    private String town;

    @Column(name = "detail",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "detail")
    private String detail;

    @Column(name = "pay_time")
    @ApiModelProperty(value = "payTime")
    private String payTime;

    @Column(name = "clean_status")
    @ApiModelProperty(value = "清关状态回传标记，null：未开始,0开始，1结束,-1失败结尾")
    private String cleanStatus;

    @Column(name = "order_status",nullable = false)
    @NotNull
    @ApiModelProperty(value = "订单状态,-1取消，0未发货,1打包,2已发货")
    private Integer orderStatus;

    @OneToMany(mappedBy = "order",cascade={CascadeType.PERSIST,CascadeType.REMOVE})
    private List<ByteDanceItem> items;

    public void copy(ByteDance source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
