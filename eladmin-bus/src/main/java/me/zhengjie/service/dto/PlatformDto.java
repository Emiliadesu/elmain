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

/**
* @website https://el-admin.vip
* @description /
* @author 王淼
* @date 2020-10-12
**/
@Data
public class PlatformDto implements Serializable {

    private Long id;

    /** 平台名 */
    private String plafName;

    /** 平台别名 */
    private String plafNickName;

    /** 平台代码 */
    private String plafCode;

    private String ebpCode;

    private String ebpName;

    private String orderForm;

    private String appId;

    private String appSecret;
}