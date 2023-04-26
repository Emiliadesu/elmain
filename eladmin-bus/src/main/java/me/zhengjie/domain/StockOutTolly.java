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
@Table(name="bus_stock_out_tolly")
public class StockOutTolly implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @JoinColumn(name = "out_order_sn")
    @ManyToOne(fetch=FetchType.LAZY,targetEntity = WmsOutstock.class)
    @ApiModelProperty(value = "出库通知单", hidden = true)
    private WmsOutstock wmsOutstock;

    @Column(name = "tally_order_sn",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "理货单号")
    private String tallyOrderSn;

    @Column(name = "sku_num",nullable = false)
    @NotNull
    @ApiModelProperty(value = "理货品种数")
    private Integer skuNum;

    @Column(name = "total_num")
    @ApiModelProperty(value = "理货总件数")
    private Integer totalNum;

    @Column(name = "current_num")
    @ApiModelProperty(value = "当前理货次数")
    private Integer currentNum;

    @Column(name = "start_time",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "理货开始时间")
    private String startTime;

    @Column(name = "end_time")
    @ApiModelProperty(value = "理货完成时间")
    private String endTime;

    @Column(name = "pack_way")
    @ApiModelProperty(value = "出库包装方式 (0:散箱 1:拖)")
    private String packWay;

    @Column(name = "pack_num")
    @ApiModelProperty(value = "出库包装数量")
    private Integer packNum;

    @Column(name = "tray_height")
    @ApiModelProperty(value = "托盘高度")
    private Double trayHeight;

    @Column(name = "total_weight")
    @ApiModelProperty(value = "总重量g")
    private Double totalWeight;

    @Column(name = "status")
    @ApiModelProperty(value = "是否已通知,0否1是")
    private String status;

    @Column(name = "reason")
    @ApiModelProperty(value = "reason")
    private String reason;

    @Column(name = "gross_weight")
    @ApiModelProperty(value = "理论重量")
    private String grossWeight;

    @Column(name = "tenant_code")
    @ApiModelProperty(value = "租户编码")
    private String tenantCode;

    @Column(name = "warehouse_id")
    @ApiModelProperty(value = "仓库id")
    private String warehouseId;

    @Column(name = "parent_do_code")
    @ApiModelProperty(value = "申报单号")
    private String parentDoCode;

    @OneToMany(mappedBy = "stockOutTolly",cascade={CascadeType.PERSIST,CascadeType.REMOVE})
    private List<CartonHeaders> cartonHeaders;

    @OneToMany(mappedBy = "stockOutTolly",cascade={CascadeType.PERSIST,CascadeType.REMOVE})
    private List<DoLot> doLots;

    @OneToMany(mappedBy = "stockOutTolly",cascade={CascadeType.PERSIST,CascadeType.REMOVE})
    private List<StockOutTollyItem> stockOutTollyItems;

    @Transient
    private AsnHeader asnHeader;

    public void copy(StockOutTolly source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
