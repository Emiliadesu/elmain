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
* @date 2021-03-23
**/
@Data
public class ReasonDetailDto implements Serializable {

    private Long id;

    /** 入库理货id */
    private StockInTollyItemSmallDto stockInTollyItem;

    /** 差异原因代码 00 QS问题 01 包装问题 02 标签信息不完善 03 产品日期问题 04 合格证问题 05 无卫检 06 效期问题 07 错货 08 质量问题 09 其他问题 100 供应商问题 200 缺货 300 整单未送 */
    private String reasonCode;

    /** 差异原因描述 */
    private String reasonDescr;

    /** 差异数量 */
    private Integer num;
}
