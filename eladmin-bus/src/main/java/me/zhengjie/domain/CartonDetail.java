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
* @date 2021-03-28
**/
@Entity
@Data
@Table(name="bus_carton_detail")
public class CartonDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @JoinColumn(name = "carton_header_id")
    @ManyToOne(fetch=FetchType.LAZY,targetEntity = CartonHeaders.class)
    @ApiModelProperty(value = "wms箱", hidden = true)
    private CartonHeaders cartonHeader;

    @JoinColumn(name = "load_header_id")
    @ManyToOne(fetch=FetchType.LAZY,targetEntity = LoadHeader.class)
    @ApiModelProperty(value = "wms箱", hidden = true)
    private LoadHeader loadHeader;

    @Column(name = "num",nullable = false)
    @NotNull
    @ApiModelProperty(value = "数量")
    private Integer num;

    @Column(name = "product_id",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "产品id")
    private String productId;

    public void copy(CartonDetail source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
