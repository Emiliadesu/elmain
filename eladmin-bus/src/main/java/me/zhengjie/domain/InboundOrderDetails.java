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
import java.sql.Timestamp;
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2021-05-13
**/
@Entity
@Data
@Table(name="bus_inbound_order_details")
public class InboundOrderDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "order_id")
    @NotNull
    @ApiModelProperty(value = "入库单ID")
    private Long orderId;

    @Column(name = "order_no")
    @ApiModelProperty(value = "入库单号")
    private String orderNo;

    @Column(name = "goods_id")
    @ApiModelProperty(value = "货品ID")
    private Long goodsId;

    @Column(name = "goods_code")
    @ApiModelProperty(value = "货品编码")
    private String goodsCode;

    @Column(name = "goods_no")
    @ApiModelProperty(value = "海关货号")
    private String goodsNo;

    @Column(name = "bar_code")
    @ApiModelProperty(value = "条码")
    private String barCode;

    @Column(name = "goods_name")
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @Column(name = "goods_line_no")
    @ApiModelProperty(value = "商品行")
    private String goodsLineNo;

    @Column(name = "expect_num")
    @ApiModelProperty(value = "预期到货数量")
    private Integer expectNum;

    @Column(name = "take_num")
    @ApiModelProperty(value = "实际收货数量")
    private Integer takeNum;

    @Column(name = "lack_num")
    @ApiModelProperty(value = "短少数量")
    private Integer lackNum;

    @Column(name = "normal_num")
    @ApiModelProperty(value = "正品数量")
    private Integer normalNum;

    @Column(name = "damaged_num")
    @ApiModelProperty(value = "残品数量")
    private Integer damagedNum;

    @Column(name = "expire_date")
    @ApiModelProperty(value = "失效日期")
    private Timestamp expireDate;

    @Column(name = "default_01")
    @ApiModelProperty(value = "预留字段01")
    private String default01;

    @Column(name = "default_02")
    @ApiModelProperty(value = "预留字段02,wms批次号或者供应商批次号，虚拟回传用")
    private String default02;

    @Transient
    private List<InboundTallyDetails> tallyDetails;

    public void copy(InboundOrderDetails source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
