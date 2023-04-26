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

/**
* @website https://el-admin.vip
* @description /
* @author 王淼
* @date 2020-10-20
**/
@Entity
@Data
@Table(name="bus_shop_token")
public class ShopToken implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "shop_name")
    @ApiModelProperty(value = "店铺名")
    private String shopName;

    @Column(name = "shop_id")
    @ApiModelProperty(value = "店铺id")
    private Long shopId;

    @Column(name = "platform_shop_id")
    @ApiModelProperty(value = "平台店铺ID")
    private String platformShopId;

    @Column(name = "client_id")
    @ApiModelProperty(value = "应用id")
    private String clientId;

    @Column(name = "client_secret")
    @ApiModelProperty(value = "应用secret")
    private String clientSecret;

    @Column(name = "code")
    @ApiModelProperty(value = "授权码")
    private String code;

    @Column(name = "access_token")
    @ApiModelProperty(value = "令牌")
    private String accessToken;

    @Column(name = "refresh_token")
    @ApiModelProperty(value = "刷新令牌")
    private String refreshToken;

    @Column(name = "code_get_time")
    @ApiModelProperty(value = "授权码获取时间")
    private Timestamp codeGetTime;

    @Column(name = "refresh_time")
    @ApiModelProperty(value = "令牌刷新时间")
    private Timestamp refreshTime;

    @Column(name = "token_time")
    @ApiModelProperty(value = "token有效期")
    private Long tokenTime;

    @Column(name = "platform")
    @ApiModelProperty(value = "电商平台代码")
    private String platform;

    @Column(name = "pub_key")
    @ApiModelProperty(value = "pubKey")
    private String pubKey;

    @Column(name = "pri_key")
    @ApiModelProperty(value = "priKey")
    private String priKey;

    @Column(name = "pull_order_able")
    @ApiModelProperty(value = "是否允许拉单操作")
    private String pullOrderAble;

    @Column(name = "is_push_to_cn")
    @ApiModelProperty(value = "是否推送至菜鸟")
    private String isPushToCn;

    public void copy(ShopToken source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}