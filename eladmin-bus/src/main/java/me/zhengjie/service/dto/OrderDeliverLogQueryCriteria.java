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
* @date 2021-04-03
**/
@Data
public class OrderDeliverLogQueryCriteria{

    /** 精确 */
    @Query
    private Long shopId;

    /** 精确 */
    @Query
    private String userName;

    /** 精确 */
    @Query
    private String orderNo;

    /** 精确 */
    @Query
    private String mailNo;

    /** BETWEEN */
    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;

    @Query
    private String platformCode;

    @Query
    private String ruleCode;
}