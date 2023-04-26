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

import com.alibaba.fastjson.annotation.JSONField;
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
* @date 2021-04-18
**/
@Entity
@Data
@Table(name="bus_stock_convert")
public class StockConvert implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    @JSONField(serialize = false)
    private Long id;

    @Column(name = "stock_convert_id",nullable = false)
    @JSONField(name = "id")
    private String stockConvertId;

    @Column(name = "tenant_code",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "租户编号")
    private String tenantCode;

    @Column(name = "warehouse_id",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "仓库id")
    private String warehouseId;

    @Column(name = "convert_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "转移单单号")
    private String convertNo;

    @Column(name = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Column(name = "source_sys",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "系统来源")
    private String sourceSys;

    @Column(name = "convert_type",nullable = false)
    @NotNull
    @ApiModelProperty(value = "转移单类型")
    private Integer convertType;

    @Column(name = "business_type")
    @ApiModelProperty(value = "业务类型")
    private Integer businessType;

    @Column(name = "create_time")
    @ApiModelProperty(value = "通知时间")
    private String createTime;

    @Column(name = "completer")
    @ApiModelProperty(value = "完成人")
    private String completer;

    @Column(name = "complete_time")
    @ApiModelProperty(value = "完成时间")
    private String completeTime;

    @Transient
    private List<BusStockConvertDetail>detailList;

    public void copy(StockConvert source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
