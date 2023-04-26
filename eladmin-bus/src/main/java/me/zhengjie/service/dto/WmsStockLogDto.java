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
public class WmsStockLogDto implements Serializable {

    private Long id;

    /** 出入库单号 */
    private String orderSn;

    /** 库存操作，0入库，1出库 */
    private String type;

    /** 请求报文 */
    private String request;

    /** 响应报文 */
    private String response;

    /** 备注 */
    private String remark;

    /** 操作状态 */
    private String statusText;

    /** 操作时间 */
    private Timestamp operationTime;

    /** 操作人 */
    private String operationUser;

    /** 操作状态码 */
    private String status;

    /** 是否成功 */
    private String isSuccess;
}