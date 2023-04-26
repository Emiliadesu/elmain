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
* @author leningzhou
* @date 2021-12-27
**/
@Entity
@Data
@Table(name="bus_gift_details")
public class GiftDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "gift_id")
    @ApiModelProperty(value = "赠品ID")
    private Long giftId;

    @Column(name = "gift_code")
    @ApiModelProperty(value = "赠品条码")
    private String giftCode;

    @Column(name = "gift_name")
    @ApiModelProperty(value = "赠品名称")
    private String giftName;

    @Column(name = "goods_code")
    @ApiModelProperty(value = "商品编码")
    private String goodsCode;

    @Column(name = "place_counts")
    @ApiModelProperty(value = "放置数量")
    private String placeCounts;

    @Column(name = "sku_id")
    @ApiModelProperty(value = "绑定SKU")
    private Long skuId;

    @Column(name = "goods_name")
    @ApiModelProperty(value = "商品名称")
    private String goodsName;


    public void copy(GiftDetails source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}