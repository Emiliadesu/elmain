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
import java.util.List;
import me.zhengjie.annotation.Query;

/**
* @website https://el-admin.vip
* @author luob
* @date 2021-04-14
**/
@Data
public class OrderReturnQueryCriteria{

    /** 精确 */
    @Query
    private Long customersId;

    @Query(propName = "tradeReturnNo",type = Query.Type.IN)
    private List<String> tradeReturnNos;

    /** 精确 */
    @Query
    private String shopId;

    /** 精确 */
    @Query(type = Query.Type.IN, propName = "shopId")
    private List<Long> shopIds;

    /** 精确 */
    @Query
    private String tradeReturnNo;

    /** 精确 */
    @Query(type = Query.Type.IN)
    private List<Integer> status;

    @Query
    private String isBorder;

    @Query
    private String isOverTime;

    /** 精确 */
    @Query
    private String sExpressNo;

    /** 精确 */
    @Query
    private String orderNo;

    /** 精确 */
    @Query
    private String declareNo;

    @Query(propName = "orderNo",type = Query.Type.IN)
    private List<String> orderNos;

    @Query(propName = "sExpressNo",type = Query.Type.IN)
    private List<String> sExpressNos;

    /** BETWEEN */
    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;

    /** BETWEEN */
    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> takeTime;

    /** BETWEEN */
    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> closeTime;

    @Query
    private String platformCode;

    @Query
    private String wmsNo;

    @Query
    private String isWave;

    @Query
    private String orderSource;

    @Query
    private String checkResult;

    @Query
    private String orderServiceStatus;

    @Query
    private String fourPl;

}