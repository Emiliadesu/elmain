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
import java.util.List;
import me.zhengjie.annotation.Query;

/**
* @website https://el-admin.vip
* @author leningzhou
* @date 2021-12-03
**/
@Data
public class LogisticsUnarrivePlaceQueryCriteria{

    @Query
    private String logisticsName;

    @Query
    private Long logisticsId;

    @Query
    private String logisticsCode;

    @Query(type = Query.Type.INNER_LIKE)
    private String province;

    @Query(type = Query.Type.INNER_LIKE)
    private String city;

    @Query(type = Query.Type.INNER_LIKE)
    private String district;
}