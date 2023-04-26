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
import java.sql.Timestamp;
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author luob
* @date 2021-03-25
**/
@Data
public class CBOrderDetailsDto implements Serializable {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 客户ID
     */
    private String customersName;

    /**
     * 店铺ID
     */
    private Long shopName;

    /**
     * 订单创建时间
     */
    private Timestamp orderCreateTime;

    private Timestamp deliverTime;

    private String goodsCode;

    private String goodsNo;

    private String fontGoodsName;

    private String goodsName;

    private String qty;

}

