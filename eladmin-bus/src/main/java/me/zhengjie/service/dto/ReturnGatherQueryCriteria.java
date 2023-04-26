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
* @date 2022-04-06
**/
@Data
public class ReturnGatherQueryCriteria{

    /** 精确 */
    @Query
    private String gatherNo;

    /** 精确 */
    @Query
    private String wmsNo;

    /** 精确 */
    @Query
    private Long customersId;

    /** 精确 */
    @Query
    private Long shopId;

    /** 精确 */
    @Query(type = Query.Type.IN)
    private List<Integer> status;

    /** BETWEEN */
    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;

    /** BETWEEN */
    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> preHandleTime;

    /** BETWEEN */
    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> closeTime;
}