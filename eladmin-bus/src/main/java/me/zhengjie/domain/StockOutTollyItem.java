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
import java.sql.Timestamp;
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
@Table(name="bus_stock_out_tolly_item")
public class StockOutTollyItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @JoinColumn(name = "out_stock_id")
    @ManyToOne(fetch=FetchType.LAZY,targetEntity = StockOutTolly.class)
    @ApiModelProperty(value = "出库理货单", hidden = true)
    private StockOutTolly stockOutTolly;

    @Column(name = "sku_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "理货sku")
    private String skuNo;

    @Column(name = "tally_num",nullable = false)
    @NotNull
    @ApiModelProperty(value = "理货数量")
    private Integer tallyNum;

    @Column(name = "avaliable_num",nullable = false)
    @NotNull
    @ApiModelProperty(value = "正品数量")
    private Integer avaliableNum;

    @Column(name = "defect_num",nullable = false)
    @NotNull
    @ApiModelProperty(value = "残品数量")
    private Integer defectNum;

    @Column(name = "num_per_box",nullable = false)
    @NotNull
    @ApiModelProperty(value = "单箱数量，箱规")
    private Integer numPerBox;

    @Column(name = "expiry_date")
    @ApiModelProperty(value = "过期日期")
    private Timestamp expiryDate;

    @Column(name = "product_date")
    @ApiModelProperty(value = "生产日期")
    private Timestamp productDate;

    @Column(name = "batch_no")
    @ApiModelProperty(value = "批次号")
    private String batchNo;

    @Column(name = "box_num")
    @ApiModelProperty(value = "箱数")
    private Integer boxNum;

    @Column(name = "gross_weight")
    @ApiModelProperty(value = "毛重(g)")
    private Double grossWeight;

    @Column(name = "tray_size",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "托盘尺寸")
    private String traySize;

    @Column(name = "tray_material",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "托盘材质")
    private String trayMaterial;

    @Column(name = "tray_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "trayNo")
    private String trayNo;

    @Column(name = "purchase_order_sn")
    @ApiModelProperty(value = "purchaseOrderSn")
    private String purchaseOrderSn;

    @Column(name = "in_warehouse_time")
    @ApiModelProperty(value = "inWarehouseTime")
    private Timestamp inWarehouseTime;

    @Column(name = "uploaded")
    @ApiModelProperty(value = "是否已上传SN码")
    private String uploaded;

    @Column(name = "goods_no")
    @ApiModelProperty(value = "货号仓库用")
    private String goodsNo;

    @Column(name = "barcode")
    @ApiModelProperty(value = "国际码")
    private String barcode;

    @Column(name = "pre_tally_num")
    @ApiModelProperty(value = "通知数量")
    private Integer preTallyNum;

    @Column(name = "need_sn")
    @ApiModelProperty(value = "是否回传序列号")
    private Integer needSn;

    public void copy(StockOutTollyItem source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
