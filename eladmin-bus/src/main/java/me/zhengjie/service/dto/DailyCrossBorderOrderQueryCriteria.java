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
* @author leningzhou
* @date 2022-07-04
**/
@Data
public class DailyCrossBorderOrderQueryCriteria{
    @Query
    private Long orderId;

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
    @Query
    private Long customersId;

    /** 精确 */
    @Query(type = Query.Type.IN)
    private List<Long> shopId;

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
    private String waveNo;

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

    public DailyCrossBorderOrderQueryCriteria(CrossBorderOrderQueryCriteria criteria) {
        this.orderId = criteria.getId();
        this.orderNo = criteria.getOrderNo();
        this.orderNos = criteria.getOrderNos();
        this.crossBorderNos = criteria.getCrossBorderNos();
        this.logisticsNos = criteria.getLogisticsNos();
        this.customersId = criteria.getCustomersId();
        this.shopId = criteria.getShopId();
        this.crossBorderNo = criteria.getCrossBorderNo();
        this.declareStatus = criteria.getDeclareStatus();
        this.notEqDeclareStatus = criteria.getNotEqDeclareStatus();
        this.declareMsg = criteria.getDeclareMsg();
        this.waveNo = criteria.getWaveNo();
        this.logisticsNo = criteria.getLogisticsNo();
        this.receivedBackTime = criteria.getReceivedBackTime();
        this.clearStartTime = criteria.getClearStartTime();
        this.clearStartBackTime = criteria.getClearStartBackTime();
        this.clearSuccessTime = criteria.getClearSuccessTime();
        this.clearSuccessBackTime = criteria.getClearSuccessBackTime();
        this.deliverTime = criteria.getDeliverTime();
        this.createTime = criteria.getCreateTime();
        this.orderCreateTime = criteria.getOrderCreateTime();
        this.logisticsCollectTime = criteria.getLogisticsCollectTime();
        this.isCollected = criteria.getIsCollected();
        this.unCollected = criteria.getUnCollected();
        this.collected = criteria.getCollected();
    }
    public DailyCrossBorderOrderQueryCriteria(){}
}