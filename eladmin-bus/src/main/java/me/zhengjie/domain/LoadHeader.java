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
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-04-01
**/
@Entity
@Data
@Table(name="bus_load_header")
public class LoadHeader implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @JoinColumn(name = "out_order_id")
    @ManyToOne(fetch=FetchType.LAZY,targetEntity = WmsOutstock.class)
    @ApiModelProperty(value = "出库通知单id", hidden = true)
    private WmsOutstock wmsOutstock;

    @Column(name = "tenant_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "租户编码")
    private String tenantCode;

    @Column(name = "load_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "装载单号")
    private String loadNo;

    @Column(name = "vechile_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "车牌号")
    private String vechileNo;

    @Column(name = "warehouse_id",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "仓库编码")
    private String warehouseId;

    @OneToMany(mappedBy = "loadHeader",cascade={CascadeType.PERSIST,CascadeType.REMOVE})
    private List<LoadDetail>loadDetails;

    @OneToMany(mappedBy = "loadHeader",cascade={CascadeType.PERSIST,CascadeType.REMOVE})
    private List<CartonDetail>cartonDetails;

    public void copy(LoadHeader source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
