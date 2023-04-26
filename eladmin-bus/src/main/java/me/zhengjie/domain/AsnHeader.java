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
* @date 2021-03-28
**/
@Entity
@Data
@Table(name="bus_asn_header")
public class AsnHeader implements Serializable {

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

    @Column(name = "warehouse_id",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "仓库id")
    private String warehouseId;

    @OneToMany(mappedBy = "asnHeader",cascade={CascadeType.PERSIST,CascadeType.REMOVE})
    private List<AsnDetail> asnDetails;

    public void copy(AsnHeader source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
