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
* @author leningzhou
* @date 2021-12-27
**/
@Entity
@Data
@Table(name="bus_gift_info")
public class GiftInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "customers_id")
    @ApiModelProperty(value = "客户")
    private Long customersId;

    @Column(name = "shop_id")
    @ApiModelProperty(value = "店铺")
    private Long shopId;

    @Column(name = "binding_type")
    @ApiModelProperty(value = "绑定类型")
    private String bindingType;

    @Column(name = "open_time")
    @ApiModelProperty(value = "生效时间")
    private Timestamp openTime;

    @Column(name = "end_time")
    @ApiModelProperty(value = "结束时间")
    private Timestamp endTime;

    @Column(name = "status")
    @ApiModelProperty(value = "状态")
    private Integer status;

    @Column(name = "place_counts")
    @ApiModelProperty(value = "放置数量")
    private Integer placeCounts;

    @Column(name = "gift_id")
    @ApiModelProperty(value = "赠品ID")
    private Long giftId;

    @Column(name = "gift_no")
    @ApiModelProperty(value = "赠品货号")
    private String giftNo;

    @Column(name = "main_no")
    @ApiModelProperty(value = "主品货号")
    private String mainNo;

    @Column(name = "main_code")
    @ApiModelProperty(value = "主品条码")
    private String mainCode;

    @Column(name = "main_name")
    @ApiModelProperty(value = "主品名称")
    private String mainName;

    @Column(name = "gift_code")
    @ApiModelProperty(value = "赠品条码")
    private String giftCode;

    @Column(name = "gift_name")
    @ApiModelProperty(value = "赠品名称")
    private String giftName;

    @Column(name = "sku_id")
    @ApiModelProperty(value = "主品ID")
    private Long skuId;

    @Column(name = "create_by")
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @Column(name = "update_by")
    @ApiModelProperty(value = "修改人")
    private String updateBy;

    @Column(name = "update_time")
    @ApiModelProperty(value = "修改时间")
    private Timestamp updateTime;

    public void copy(GiftInfo source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }

}