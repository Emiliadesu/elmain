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
import java.math.BigDecimal;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-03-28
**/
@Entity
@Data
@Table(name="bus_carton_headers")
public class CartonHeaders implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "actual_gross_weight",nullable = false)
    @NotNull
    @ApiModelProperty(value = "实际重量")
    private BigDecimal actualGrossWeight;

    @Column(name = "carton_no")
    @ApiModelProperty(value = "wms箱号")
    private String cartonNo;

    @JoinColumn(name = "out_stock_id")
    @ManyToOne(fetch=FetchType.LAZY,targetEntity = StockOutTolly.class)
    @ApiModelProperty(value = "出库理货单", hidden = true)
    private StockOutTolly stockOutTolly;

    @Column(name = "carton_id")
    @ApiModelProperty(value = "箱子id")
    private String cartonId;

    @Column(name = "is_first",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "是否第一次上报(第一次理货)")
    private String isFirst;

    @Column(name = "materials")
    @ApiModelProperty(value = "包装材质")
    private String materials;

    @Column(name = "package_time",nullable = false)
    @ApiModelProperty(value = "装箱时间")
    private Timestamp packageTime;

    @Column(name = "packaged_by",nullable = false)
    @ApiModelProperty(value = "装箱人")
    private String packagedBy;

    @Column(name = "way_bill")
    @ApiModelProperty(value = "运单号")
    private String wayBill;

    @OneToMany(mappedBy = "cartonHeader",cascade={CascadeType.PERSIST,CascadeType.REMOVE})
    private List<CartonDetail> cartonDetails;

    public void copy(CartonHeaders source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
