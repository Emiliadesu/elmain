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

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import me.zhengjie.utils.StringUtil;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-04-01
**/
@Entity
@Data
@Table(name="bus_vehicle_header")
public class VehicleHeader implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "tenant_code",nullable = false)
    @ApiModelProperty(value = "租户编码")
    private String tenantCode;

    @Column(name = "warehouse_id",nullable = false)
    @ApiModelProperty(value = "仓库编码")
    private String warehouseId;

    @Column(name = "load_no")
    @ApiModelProperty(value = "交接单号")
    private String loadNo;

    @Column(name = "lpn_qty")
    @ApiModelProperty(value = "托盘数")
    private Integer lpnQty;

    @Column(name = "build_lpn_qty")
    @ApiModelProperty(value = "打托数")
    private Integer buildLpnQty;

    @Column(name = "split_lpn_qty")
    @ApiModelProperty(value = "拆托数")
    private Integer splitLpnQty;

    @Column(name = "load_type",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "交接类型 WL：一般订单 TT：调拨 RTV：退供")
    private String loadType;

    @Column(name = "entruck_type",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "装车类型")
    private String entruckType;

    @Column(name = "loadDetails")
    @ApiModelProperty(value = "装载单明细")
    private String loadDetails;

    @Transient
    private List<String>loadDetailsArray;

    @OneToMany(mappedBy = "vehHeader",cascade={CascadeType.PERSIST,CascadeType.REMOVE})
    private List<VehicleDetail> vehicleDetails;

    @Column(name = "status")
    @ApiModelProperty(value = "推送状态")
    private String status;

    public List<String> getLoadDetailsArray() {
        if (CollectionUtil.isNotEmpty(loadDetailsArray))
            return this.loadDetailsArray;
        if (StringUtil.isEmpty(this.loadDetails))
            return null;
        this.loadDetailsArray=new ArrayList<>();
        loadDetailsArray.addAll(Arrays.asList(this.loadDetails.split(",")));
        return loadDetailsArray;
    }

    public void copy(VehicleHeader source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
