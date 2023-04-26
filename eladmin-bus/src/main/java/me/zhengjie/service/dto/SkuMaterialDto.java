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
* @author luob
* @date 2022-05-20
**/
@Data
public class SkuMaterialDto implements Serializable {

    /** ID */
    private Long id;

    /** 客户ID */
    private Long customersId;

    /** 店铺ID */
    private Long shopId;

    /** 商品ID */
    private Long goodsId;

    /** 耗材ID */
    private Long materialId;

    /** 货号 */
    private String goodsNo;

    /** 商品编码 */
    private String goodsCode;

    /** 商品名称 */
    private String goodsName;

    /** 商品条码 */
    private String barCode;

    /** 耗材编码 */
    private String materialCode;

    /** 耗材名称 */
    private String materialName;

    /** 绑定数量 */
    private Integer qty;

    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    private Timestamp createTime;

    /** 修改人 */
    private String updateBy;

    /** 修改时间 */
    private Timestamp updateTime;
}