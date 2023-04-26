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
import java.util.Objects;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2021-07-13
**/
@Entity
@Data
@Table(name="bus_outbound_order_details")
public class OutboundOrderDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "order_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "订单ID")
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
    @ApiModelProperty(value = "预期发货数量")
    private Integer expectNum;

    @Column(name = "deliver_num")
    @ApiModelProperty(value = "实际发货数量")
    private Integer deliverNum;

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
    @ApiModelProperty(value = "预留字段02")
    private String default02;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (goodsLineNo==null){
            if (o == null || getClass() != o.getClass()) return false;
            OutboundOrderDetails that = (OutboundOrderDetails) o;
            return Objects.equals(goodsNo, that.goodsNo);
        }else {
            if (o == null || getClass() != o.getClass()) return false;
            OutboundOrderDetails that = (OutboundOrderDetails) o;
            return Objects.equals(goodsLineNo, that.goodsLineNo);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(goodsLineNo);
    }

    public void copy(OutboundOrderDetails source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
