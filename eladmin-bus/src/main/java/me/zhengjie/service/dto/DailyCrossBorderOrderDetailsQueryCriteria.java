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
public class DailyCrossBorderOrderDetailsQueryCriteria{
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

    /** 精确 */
    @Query
    private String logisticsNo;
    /** BETWEEN */
    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> deliverTime;
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

    public DailyCrossBorderOrderDetailsQueryCriteria(CrossBorderOrderQueryCriteria criteria) {
        this.orderId = criteria.getId();
        this.orderNo = criteria.getOrderNo();
        this.orderNos = criteria.getOrderNos();
        this.crossBorderNos = criteria.getCrossBorderNos();
        this.logisticsNos = criteria.getLogisticsNos();
        this.customersId = criteria.getCustomersId();
        this.shopId = criteria.getShopId();
        this.crossBorderNo = criteria.getCrossBorderNo();
        this.logisticsNo = criteria.getLogisticsNo();
        this.deliverTime = criteria.getDeliverTime();
        this.orderCreateTime = criteria.getOrderCreateTime();
        this.logisticsCollectTime = criteria.getLogisticsCollectTime();
        this.isCollected = criteria.getIsCollected();
        this.unCollected = criteria.getUnCollected();
        this.collected = criteria.getCollected();
    }
}