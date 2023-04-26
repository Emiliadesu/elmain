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
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-03-23
**/
@Entity
@Data
@Table(name="bus_outof_plan_detail")
public class OutofPlanDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @JoinColumn(name = "tally_order_id")
    @ManyToOne(fetch=FetchType.LAZY,targetEntity = StockInTolly.class)
    @ApiModelProperty(value = "入库理货单", hidden = true)
    private StockInTolly stockInTolly;

    @Column(name = "ean13",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "商品不在通知单的条码")
    private String ean13;

    @Column(name = "product_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "商品名")
    private String productName;

    @Column(name = "receive_qty",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "收货数量")
    private String receiveQty;

    @Column(name = "pic_url")
    @ApiModelProperty(value = "图片地址")
    private String picUrl;

    public void copy(OutofPlanDetail source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
