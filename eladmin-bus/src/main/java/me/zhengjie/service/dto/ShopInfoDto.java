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
import java.io.Serializable;
import java.sql.Timestamp;

/**
* @website https://el-admin.vip
* @description /
* @author 王淼
* @date 2020-10-20
**/
@Data
public class ShopInfoDto implements Serializable {

    private Long id;

    /** 货主ID */
    private Long custId;

    /** 代码，唯一 */
    private String code;

    /** 名称 */
    private String name;

    /** 联系电话 */
    private String contactPhone;

    /** 电商平台 */
    private Long platformId;

    /** 电商平台 */
    private String platformCode;

    private String orderSourceType;

    /** 账册编号 */
    private String booksNo;

    private String serviceType;

    private ClearCompanyInfoDto clearCompanyInfoDto;

    /** 添加时间 */
    private Timestamp createTime;
    /** 添加人id */
    private Long createUserId;

    private Long serviceId;

    private String registerType;

    private String kjgCode;

    private String pushTo;

    /** 快递公司 */
//    private String logisticsMsg;
}
