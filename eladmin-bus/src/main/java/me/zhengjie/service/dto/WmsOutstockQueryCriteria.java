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
* @date 2020-12-04
**/
@Data
public class WmsOutstockQueryCriteria{
    @Query
    private String soNo;

    @Query
    private String outOrderSn;

    @Query
    private String merchantId;

    @Query(propName = "companyId",type = Query.Type.IN,joinName = "companyInfo")
    private Set<Long> companyIds;

    @Query(propName = "clearCompanyId",type = Query.Type.IN,joinName = "clearCompanyInfo")
    private Set<Long> clearCompanyIds;

    private Long companyId;

    private Long clearCompanyId;
}
