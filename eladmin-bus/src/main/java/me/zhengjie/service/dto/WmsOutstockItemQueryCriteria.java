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
import java.util.Set;

import me.zhengjie.annotation.Query;

/**
* @website https://el-admin.vip
* @author 王淼
* @date 2020-12-18
**/
@Data
public class WmsOutstockItemQueryCriteria{

    /** 精确 */
    @Query(propName = "id",type = Query.Type.EQUAL,joinName = "wmsOutstock")
    private Long outId;

    private Long companyId;

    @Query(propName = "companyId",type = Query.Type.IN,joinName = "companyInfo")
    private Set<Long> companyIds;
}
