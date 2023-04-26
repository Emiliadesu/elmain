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
* @author wangm
* @date 2021-02-27
**/
@Data
public class CustomerInfoDto implements Serializable {

    private Long id;

    /** 客户名 */
    private String custName;

    /** 客户别名 */
    private String custNickName;

    /** 客户联系人姓名 */
    private String contacts;

    /** 客户联系电话 */
    private String telphone;

    /** 客户地址 */
    private String address;

    /** 添加时间 */
    private Timestamp createTime;

    /** 添加人id */
    private Long createUserId;
}