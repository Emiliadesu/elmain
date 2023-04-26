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
package me.zhengjie.service.dto;

import lombok.Data;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author 王淼
* @date 2020-10-20
**/
@Data
public class ShopTokenDto implements Serializable {

    private Long id;

    /** 店铺名 */
    private String shopName;

    /** 店铺id(拼多多则是user_id) */
    private Long shopId;

    /** 应用id */
    private String clientId;

    /** 平台店铺id */
    private String platformShopId;

    /** 应用secret */
    private String clientSecret;

    /** 授权码 */
    private String code;

    /** 令牌 */
    private String accessToken;

    /** 刷新令牌 */
    private String refreshToken;

    /** 授权码获取时间 */
    private Timestamp codeGetTime;

    /** 令牌刷新时间 */
    private Timestamp refreshTime;

    /** token有效期 */
    private Long tokenTime;

    /** 电商平台代码 */
    private String platform;

    private String pubKey;

    private String priKey;

    /** 是否允许拉单操作 */
    private String pullOrderAble;

    /** 是否推送至菜鸟 */
    private String isPushToCn;
}