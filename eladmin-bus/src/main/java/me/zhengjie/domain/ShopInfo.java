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

/**
* @website https://el-admin.vip
* @description /
* @author 王淼
* @date 2020-10-20
**/
@Entity
@Data
@Table(name="bus_shop_info")
public class ShopInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "cust_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "货主ID")
    private Long custId;

    @Column(name = "code",unique = true,nullable = false)
    @NotBlank
    @ApiModelProperty(value = "代码，唯一")
    private String code;

    @Column(name = "name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "名称")
    private String name;

    @Column(name = "books_no")
    @ApiModelProperty(value = "账册编号")
    private String booksNo;

    @Column(name = "contact_phone")
    @ApiModelProperty(value = "联系电话")
    private String contactPhone;

    @Column(name = "platform_id")
    @ApiModelProperty(value = "电商平台ID")
    private Long platformId;

    @Column(name = "platform_code")
    @ApiModelProperty(value = "电商平台代码")
    private String platformCode;

    @Column(name = "order_source_type")
    @ApiModelProperty(value = "订单来源类型：1:拉取,2:推送,0:占位符")
    private String orderSourceType;

    @Column(name = "service_type")
    @ApiModelProperty(value = "业务类型")
    private String serviceType;

    @Column(name = "register_type")
    @ApiModelProperty(value = "备案模式")
    private String registerType;

    @Column(name = "kjg_code")
    @ApiModelProperty(value = "跨境购店铺代码")
    private String kjgCode;

    @Transient
    private ClearCompanyInfo clearCompanyInfo;

    @Column(name = "create_time",nullable = false)
    @ApiModelProperty(value = "添加时间")
    private Timestamp createTime;

    @Column(name = "create_user_id",nullable = false)
    @ApiModelProperty(value = "添加人id")
    private Long createUserId;

    @Column(name = "service_id")
    @ApiModelProperty(value = "业务抬头id")
    private Long serviceId;

//    @Column(name = "logistics_msg")
//    @ApiModelProperty(value = "快递公司")
//    private String logisticsMsg;

    @Column(name = "push_to")
    @ApiModelProperty(value = "推单平台")
    private String pushTo;

    public void copy(ShopInfo source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }

    public String getLabel() {
        return name;
    }
}
