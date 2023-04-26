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
* @date 2021-04-15
**/
@Data
public class SkuInfoDto implements Serializable {

    /** ID */
    private Long id;

    /** 添加时间 */
    private Timestamp createTime;

    /** 客户ID */
    private Long customersId;

    /** 店铺ID */
    private String shopId;

    /** 商品编码 */
    private String goodsCode;

    /** 条形码 */
    private String barCode;

    /** 商品名称 */
    private String goodsName;

    /** 是否SN管理 */
    private String snControl;

    /** 长 */
    private String saleL;

    /** 宽 */
    private String saleW;

    /** 高 */
    private String saleH;

    /** 重量 */
    private String saleWeight;

    /** 箱长 */
    private Timestamp packL;

    /** 箱宽 */
    private String packW;

    /** 箱高 */
    private String packH;

    /** 箱重 */
    private String packWeight;

    /** 箱规 */
    private String packNum;

    /** 创建人 */
    private String createBy;

    /** 修改人 */
    private String updateBy;

    /** 修改时间 */
    private Timestamp updateTime;
}