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
import me.zhengjie.utils.StringUtil;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-06-21
**/
@Entity
@Data
@Table(name="bus_combination_order")
public class CombinationOrder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "comb_sku_id",unique = true,nullable = false)
    @NotBlank
    @ApiModelProperty(value = "组合包的skuId")
    private String combSkuId;

    @Column(name = "comb_name")
    @ApiModelProperty(value = "组合包的名称")
    private String combName;

    @Column(name = "platform",nullable = false)
    @NotNull
    @ApiModelProperty(value = "组合包来源平台")
    private Long platform;

    @Column(name = "split_qty",nullable = false)
    @ApiModelProperty(value = "splitQty")
    private Integer splitQty;

    @Transient
    private String platformCode;

    @Column(name = "shop_code",nullable = false)
    @ApiModelProperty(value = "组合包所属的平台商家code")
    private String shopCode;

    @Column(name = "shop_id")
    @ApiModelProperty(value = "组合包所属系统店铺id")
    private Long shopId;

    @Transient
    private List<CombSplit>splitList;

    public void copy(CombinationOrder source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        CombinationOrder order = (CombinationOrder) obj;
        if (StringUtil.isNotEmpty(order.getCombSkuId())) {
            if (StringUtil.equals(order.getCombSkuId(), this.combSkuId)) {
                return true;
            }
        }
        return false;
    }
}
