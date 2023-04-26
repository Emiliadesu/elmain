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
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author wangm
* @date 2021-04-01
**/
@Data
public class VehicleHeaderDto implements Serializable {

    private Long id;

    /** 租户编码 */
    private String tenantCode;

    /** 仓库编码*/
    private String warehouseId;

    /** 交接单号 */
    private String loadNo;

    /** 托盘数 */
    private Integer lpnQty;

    /** 打托数 */
    private Integer buildLpnQty;

    /** 拆托数 */
    private Integer splitLpnQty;

    /** 交接类型 WL：一般订单 TT：调拨 RTV：退供 */
    private String loadType;

    /** 装车类型 */
    private String entruckType;

    private String status;

    private List<String> loadDetailsArray;

    private String loadDetails;
}
