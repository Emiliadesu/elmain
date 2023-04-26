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
* @author luob
* @date 2022-04-06
**/
@Entity
@Data
@Table(name="bus_return_gather_detail")
public class ReturnGatherDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "gather_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "提总单ID")
    private Long gatherId;

    @Column(name = "gather_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "提总单号")
    private String gatherNo;

    @Column(name = "goods_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "货号")
    private String goodsNo;

    @Column(name = "bar_code")
    @ApiModelProperty(value = "条码")
    private String barCode;

    @Column(name = "return_ids")
    @ApiModelProperty(value = "退货单id明细")
    private String returnIds;

    @Column(name = "qty",nullable = false)
    @NotNull
    @ApiModelProperty(value = "数量")
    private Integer qty;

    @Column(name = "goods_name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "名称")
    private String goodsName;

    public void copy(ReturnGatherDetail source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}