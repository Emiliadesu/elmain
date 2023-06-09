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
* @author wangm
* @date 2021-04-15
**/
@Data
public class StockAttrNoticeDto implements Serializable {

    private Long id;

    /** 仓库单据id */
    private Long srvlogId;

    /** 仓库id */
    private String warehouseId;

    /** 租户编号 */
    private String tenantCode;

    /** 通知时间 */
    private String createTime;

    /** 是否完成 */
    private String isComplete;
}
