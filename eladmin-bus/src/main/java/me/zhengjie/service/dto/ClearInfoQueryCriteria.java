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

import java.math.BigDecimal;
import java.util.List;
import me.zhengjie.annotation.Query;

/**
* @website https://el-admin.vip
* @author luob
* @date 2021-03-07
**/
@Data
public class ClearInfoQueryCriteria{

    /** 模糊 */
    @Query(type = Query.Type.INNER_LIKE)
    private String clearNo;

    /** 模糊 */
    @Query(type = Query.Type.INNER_LIKE)
    private String contractNo;

    /** 模糊 */
    @Query(type = Query.Type.INNER_LIKE)
    private String refOrderNo;

    /** 精确 */
    @Query
    private String status;

    @Query
    private String refOrderType;

    /** 精确 */
    @Query
    private String busType;

    /** 精确 */
    @Query
    private String declareMode;

    /** 精确 */
    @Query
    private Long id;

    /** 精确 */
    @Query
    private String customersId;

    /** 精确 */
    @Query
    private String shopId;

    /** 精确 */
    @Query
    private String inPort;

    /** 精确 */
    @Query
    private String shipCountry;

    /** 精确 */
    @Query
    private Long billNo;

    /** 精确 */
    @Query
    private String entryNo;

    /** 精确 */
    @Query
    private String declNo;

    @Query
    private BigDecimal sumMoney;
}