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

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2021-06-16
**/
@Entity
@Data
@Table(name="bus_inbound_tally_details")
public class InboundTallyDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "tally_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "订单ID")
    private Long tallyId;

    @Column(name = "tally_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "理货单号")
    private String tallyNo;

    @Column(name = "goods_id")
    @ApiModelProperty(value = "货品ID")
    private Long goodsId;

    @Column(name = "goods_code")
    @ApiModelProperty(value = "货品编码")
    private String goodsCode;

    @Column(name = "goods_no")
    @ApiModelProperty(value = "海关货号")
    private String goodsNo;

    @Column(name = "bar_code")
    @ApiModelProperty(value = "条码")
    private String barCode;

    @Column(name = "goods_name")
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @Column(name = "goods_quality",nullable = false)
    @NotNull
    @ApiModelProperty(value = "货品属性")
    private Integer goodsQuality;

    @Column(name = "tally_num")
    @ApiModelProperty(value = "理货数量")
    private Integer tallyNum;

    @Column(name = "product_date")
    @ApiModelProperty(value = "生产日期")
    private String productDate;

    @Column(name = "expiry_date")
    @ApiModelProperty(value = "失效日期")
    private String expiryDate;

    @Column(name = "batch_no")
    @ApiModelProperty(value = "生产批次")
    private String batchNo;

    @Column(name = "pic_url")
    @ApiModelProperty(value = "图片地址")
    private String picUrl;

    public void copy(InboundTallyDetails source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}