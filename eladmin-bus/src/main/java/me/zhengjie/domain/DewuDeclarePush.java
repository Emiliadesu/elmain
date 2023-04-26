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
import me.zhengjie.utils.CollectionUtils;
import me.zhengjie.utils.StringUtil;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2023-03-21
**/
@Entity
@Data
@Table(name="bus_dewu_declare_push")
public class DewuDeclarePush implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "order_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @Column(name = "declare_type")
    @ApiModelProperty(value = "申报模式，1-仓库申报，默认传1")
    private String declareType;

    @Column(name = "shop_code")
    @ApiModelProperty(value = "店铺代码")
    private String shopCode;

    @Column(name = "declare_no")
    @ApiModelProperty(value = "申报订单号")
    private String declareNo;

    @Column(name = "platform_code")
    @ApiModelProperty(value = "电商平台代码")
    private String platformCode;

    @Column(name = "create_time")
    @ApiModelProperty(value = "订单创建时间")
    private Timestamp createTime;

    @Column(name = "buyer_account")
    @ApiModelProperty(value = "买家账号")
    private String buyerAccount;

    @Column(name = "buyer_name")
    @ApiModelProperty(value = "买家姓名")
    private String buyerName;

    @Column(name = "buyer_phone")
    @ApiModelProperty(value = "订购人电话")
    private String buyerPhone;

    @Column(name = "buyer_id_num")
    @ApiModelProperty(value = "订购人身份证号码")
    private String buyerIdNum;

    @Column(name = "total_amount")
    @ApiModelProperty(value = "优惠金额合计")
    private BigDecimal totalAmount;

    @Column(name = "payment")
    @ApiModelProperty(value = "实付金额")
    private BigDecimal payment;

    @Column(name = "fee")
    @ApiModelProperty(value = "运费")
    private BigDecimal fee;

    @Column(name = "pre_sell")
    @ApiModelProperty(value = "是否预售，1-是 0-否")
    private String preSell;

    @Column(name = "exp_deliver_time")
    @ApiModelProperty(value = "预计出库时间")
    private Timestamp expDeliverTime;

    @Column(name = "tax_amount")
    @ApiModelProperty(value = "税费")
    private BigDecimal taxAmount;

    @Column(name = "discount")
    @ApiModelProperty(value = "优惠金额(若无传0)")
    private BigDecimal discount;

    @Column(name = "logistics_code")
    @ApiModelProperty(value = "快递公司代码")
    private String logisticsCode;

    @Column(name = "logistics_no")
    @ApiModelProperty(value = "运单号")
    private String logisticsNo;

    @Column(name = "consignee_name")
    @ApiModelProperty(value = "收货人")
    private String consigneeName;

    @Column(name = "consignee_phone")
    @ApiModelProperty(value = "收货电话")
    private String consigneePhone;

    @Column(name = "province")
    @ApiModelProperty(value = "省")
    private String province;

    @Column(name = "city")
    @ApiModelProperty(value = "市")
    private String city;

    @Column(name = "district")
    @ApiModelProperty(value = "区")
    private String district;

    @Column(name = "add_mark")
    @ApiModelProperty(value = "三段码")
    private String addMark;

    @Column(name = "gross_weight")
    @ApiModelProperty(value = "毛重（千克）")
    private BigDecimal grossWeight;

    @Column(name = "net_weight")
    @ApiModelProperty(value = "净重（千克）")
    private BigDecimal netWeight;

    @Column(name = "books_no")
    @ApiModelProperty(value = "账册编号")
    private String booksNo;

    @Column(name = "address")
    @ApiModelProperty(value = "收货地址")
    private String address;

    @Column(name = "payment_no")
    @ApiModelProperty(value = "支付流水号")
    private String paymentNo;

    @Column(name = "pay_type")
    @ApiModelProperty(value = "支付方式")
    private String payType;

    @Column(name = "pay_time")
    @ApiModelProperty(value = "支付时间")
    private String payTime;

    @Column(name = "status")
    @ApiModelProperty(value = "状态")
    private String status;

    @Column(name = "declare_status")
    @ApiModelProperty(value = "申报状态")
    private String declareStatus;

    @Column(name = "declared_no")
    @ApiModelProperty(value = "申报单号")
    private String declaredNo;

    @Column(name = "declare_msg")
    @ApiModelProperty(value = "申报消息")
    private String declareMsg;

    @Column(name = "inv_no")
    @ApiModelProperty(value = "清关回执单号")
    private String invNo;

    @Transient
    private List<DewuDeclarePushItem>skuDetails;

    public void copy(DewuDeclarePush source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }

    public CrossBorderOrder toCrossBorderOrder() {
        CrossBorderOrder order = new CrossBorderOrder();
        order.setOrderNo(this.orderNo);
        order.setCrossBorderNo(this.declareNo);
        order.setPlatformCode(this.platformCode);
        order.setOrderCreateTime(this.createTime);
        order.setBuyerAccount(this.buyerAccount);
        order.setBuyerName(this.buyerName);
        order.setBuyerPhone(this.buyerPhone);
        order.setBuyerIdNum(this.buyerIdNum);
        order.setPayment(this.payment.toString());
        order.setPostFee(this.fee.toString());
        order.setPreSell(this.preSell);
        order.setExpDeliverTime(this.expDeliverTime);
        order.setTaxAmount(this.taxAmount.toString());
        order.setDisAmount(this.discount.toString());
        order.setLogisticsCode(this.logisticsCode);
        order.setLogisticsNo(this.logisticsNo);
        order.setConsigneeName(this.consigneeName);
        order.setConsigneeTel(this.consigneePhone);
        order.setProvince(this.province);
        order.setCity(this.city);
        order.setDistrict(this.district);
        order.setAddMark(this.addMark);
        order.setGrossWeight(this.grossWeight.toString());
        order.setNetWeight(this.netWeight.toString());
        order.setBooksNo(this.booksNo);
        order.setConsigneeAddr(this.address);
        order.setPaymentNo(this.paymentNo);
        if (StringUtil.equals(this.payType,"ALIPAY"))
            order.setPayCode("02");
        else if (StringUtil.equals(this.payType,"WEIXIN"))
            order.setPayCode("13");
        if (CollectionUtils.isNotEmpty(this.skuDetails)){
            List<CrossBorderOrderDetails>details = new ArrayList<>();
            for (DewuDeclarePushItem skuDetail : this.skuDetails) {
                CrossBorderOrderDetails detail = new CrossBorderOrderDetails();
                detail.setOrderNo(skuDetail.getOrderNo());
                detail.setGoodsNo(skuDetail.getGoodsNo());
                detail.setGoodsName(skuDetail.getGoodsName());
                detail.setQty(skuDetail.getQty()+"");
                detail.setDutiableValue(skuDetail.getDeclarePrice().toString());
                detail.setDutiableTotalValue(skuDetail.getDeclareAmount().toString());
                detail.setBarCode(skuDetail.getBarCode());
                detail.setUnit(skuDetail.getUnit());
                detail.setTaxAmount(skuDetail.getTax().toString());
                details.add(detail);
            }
        }
        return order;
    }
}