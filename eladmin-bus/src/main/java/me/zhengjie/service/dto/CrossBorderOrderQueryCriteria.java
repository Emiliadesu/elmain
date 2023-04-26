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
* @date 2021-03-25
**/
@Data
public class CrossBorderOrderQueryCriteria{

    @Query
    private Long id;

    /** 精确 */
    @Query
    private String orderNo;

    @Query(propName = "orderNo",type = Query.Type.IN)
    private List<String> orderNos;

    @Query(propName = "crossBorderNo",type = Query.Type.IN)
    private List<String>crossBorderNos;

    @Query(propName = "logisticsNo",type = Query.Type.IN)
    private List<String> logisticsNos;

    /** 精确 */
    @Query(type = Query.Type.IN)
    private List<Integer> status;

    /** 精确 */
    @Query
    private String preSell;

    @Query
    private String isLock;

    @Query
    private String fourPl;

    @Query
    private String logisticsFourPl;

    @Query
    private String pickType;

    @Query
    private String area;

    /** 精确 */
    @Query
    private Long customersId;

    /** 精确 */
    @Query(type = Query.Type.IN)
    private List<Long> shopId;

    /** 精确 */
    @Query
    private Long platformId;
    /** 精确 */
    @Query
    private String platformCode;

    @Query(propName = "platformCode", type = Query.Type.NOT_EQUAL)
    private String nonPlatformCode;

    @Query(propName = "platformCode", type = Query.Type.NOT_EQUAL)
    private String nonPlatformCode2;

    @Query
    private Integer platformStatus;

    @Query(propName = "platformStatus", type = Query.Type.NOT_EQUAL)
    private Integer notEqPlatformStatus;

    @Query( type = Query.Type.NOT_EQUAL)
    private String default01;

    /** 精确 */
    @Query
    private String crossBorderNo;

    @Query
    private String declareStatus;

    @Query(propName = "declareStatus", type = Query.Type.NOT_EQUAL)
    private String notEqDeclareStatus;

    @Query
    private String declareMsg;

    @Query
    private String upStatus;

    @Query
    private String isWave;

    @Query
    private String waveNo;

    @Query
    private String isPrint;

    @Query(type = Query.Type.IN)
    private List<String> wmsStatus;

    /** 精确 */
    @Query
    private String default02;

    /** 精确 */
    @Query
    private String logisticsNo;
    /** BETWEEN */
    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> receivedBackTime;
    /** BETWEEN */
    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> clearStartTime;
    /** BETWEEN */
    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> clearStartBackTime;
    /** BETWEEN */
    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> clearSuccessTime;
    /** BETWEEN */
    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> clearSuccessBackTime;
    /** BETWEEN */
    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> deliverTime;
    /** BETWEEN */
    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;
    /** BETWEEN */
    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> orderCreateTime;
    /** BETWEEN */
    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> logisticsCollectTime;

    private Boolean isCollected;

    @Query(type = Query.Type.IS_NULL,propName = "logisticsCollectTime")
    private Boolean unCollected;

    @Query(type = Query.Type.NOT_NULL,propName = "logisticsCollectTime")
    private Boolean collected;

    private Integer exceptOrderType;

    @Query
    private Long supplierId;

    @Query
    private Integer logisticsStatus;

    @Query
    private String lpCode;
}
