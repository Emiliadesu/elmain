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

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author leningzhou
* @date 2021-12-27
**/
@Data
public class GiftInfoDto implements Serializable {

    private Long id;

    /** 绑定类型 */
    private String bindingType;

    /** 生效时间 */
    private Timestamp openTime;

    /** 结束时间 */
    private Timestamp endTime;

    private Integer status;

    /** 赠品条码 */
    private String giftCode;

    /** 赠品名称 */
    private String giftName;

    /** 是否绑定SKU */
    private Long skuId;

    private Long customersId;

    /** 店铺ID */
    private Long shopId;

    private String goodsCode;

    private String goodsName;

    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    private Timestamp createTime;

    /** 修改人 */
    private String updateBy;

    /** 修改时间 */
    private Timestamp updateTime;

    private Long giftId;

    private String giftNo;

    private String mainNo;

    private String mainCode;

    private String mainName;

    private Integer placeCounts;
}