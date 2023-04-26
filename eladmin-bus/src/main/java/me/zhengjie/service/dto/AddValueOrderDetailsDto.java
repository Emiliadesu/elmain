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
* @author AddValueOrderDetails
* @date 2021-08-05
**/
@Data
public class AddValueOrderDetailsDto implements Serializable {

    private Long id;

    /** 上游明细ID */
    private String orderId;

    /** 客户批次号 */
    private String default01;

    /** 要求加工数量 */
    private Integer qty;

    private Long goodsId;

    private String goodsCode;

    private String goodsNo;

    private String barCode;

    private String goodsName;

}