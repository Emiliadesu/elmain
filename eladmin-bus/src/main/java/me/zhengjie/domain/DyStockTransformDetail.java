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
* @date 2021-09-26
**/
@Entity
@Data
@Table(name="bus_dy_stock_transform_detail")
public class DyStockTransformDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "main_id")
    @ApiModelProperty(value = "库存变动表头id")
    private Long mainId;

    @Column(name = "goods_no",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "货号")
    private String goodsNo;

    @Column(name = "from_grade",nullable = false)
    @NotNull
    @ApiModelProperty(value = "调整前质量等级")
    private Integer fromGrade;

    @Column(name = "to_grade",nullable = false)
    @NotNull
    @ApiModelProperty(value = "调整前质量等级")
    private Integer toGrade;

    @Column(name = "quantity",nullable = false)
    @NotNull
    @ApiModelProperty(value = "数量")
    private Integer quantity;

    @Column(name = "reason_code")
    @ApiModelProperty(value = "调整原因编码")
    private Integer reasonCode;

    @Column(name = "reason_msg")
    @ApiModelProperty(value = "具体原因")
    private String reasonMsg;

    /**
     * 责任方
     *   1-服务商原因
     *   2-商家原因
     */
    @Column(name = "duty")
    @ApiModelProperty(value = "责任方")
    private String duty;

    /**
     * 备注
     */
    @Column(name = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 证明材料
     */
    @Column(name = "evidence")
    @ApiModelProperty(value = "证明材料")
    private String evidence;

    @Column(name = "wms_batch")
    @ApiModelProperty(value = "仓库批次号")
    private String wmsBatch;

    @Column(name = "location")
    @ApiModelProperty(value = "库位")
    private String location;

    public void copy(DyStockTransformDetail source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
