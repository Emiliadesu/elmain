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
* @date 2020-12-21
**/
@Data
public class ThirdOrderLogDto implements Serializable {

    private Long id;

    /** 接口名 */
    private String methodName;

    /** 是否成功 */
    private String isSuccess;

    /** 错误信息 */
    private String errMsg;

    /** 响应码 */
    private Integer code;

    /** 请求报文 */
    private String request;

    /** 返回报文 */
    private String response;

    /** 请求时间 */
    private Timestamp createTime;

    /** 店铺id */
    private String shopId;

    private String platformCode;
}