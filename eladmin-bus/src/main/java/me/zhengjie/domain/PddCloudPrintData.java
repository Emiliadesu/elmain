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
import java.io.Serializable;

/**
 * @author wangmiao
 * @website https://el-admin.vip
 * @description /
 * @date 2022-07-19
 **/
@Entity
@Data
@Table(name = "bus_pdd_cloud_print_data")
public class PddCloudPrintData implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "order_no", nullable = false)
    @NotBlank
    @ApiModelProperty(value = "拼多多订单号")
    private String orderNo;

    @Column(name = "cross_border_order_no", nullable = false)
    @NotBlank
    @ApiModelProperty(value = "拼多多XP单号")
    private String crossBorderOrderNo;

    @Column(name = "print_data", nullable = false)
    @NotBlank
    @ApiModelProperty(value = "电子面单云打印数据")
    private String printData;

    @Column(name = "mail_no", nullable = false)
    @NotBlank
    @ApiModelProperty(value = "运单号")
    private String mailNo;

    @Column(name = "shop_code", nullable = false)
    @NotBlank
    @ApiModelProperty(value = "平台店铺code")
    private String shopCode;

    @Column(name = "so_no")
    @ApiModelProperty(value = "so单号")
    private String soNo;

    @Column(name = "wave_no")
    @ApiModelProperty(value = "波次号")
    private String waveNo;

    @Column(name = "basket_num")
    @ApiModelProperty(value = "篮号")
    private String basketNum;

    @Column(name = "sender")
    @ApiModelProperty(value = "发件人")
    private String sender;

    @Column(name = "sender_phone")
    @ApiModelProperty(value = "发件人电话")
    private String senderPhone;

    @Column(name = "sku_total")
    @ApiModelProperty(value = "sku数量")
    private String skuTotal;

    @Column(name = "total")
    @ApiModelProperty(value = "商品总数")
    private String total;

    @Column(name = "city")
    @ApiModelProperty(value = "市")
    private String city;

    @Column(name = "detail")
    @ApiModelProperty(value = "明细详情")
    private String detail;

    public void copy(PddCloudPrintData source) {
        BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
    }
}