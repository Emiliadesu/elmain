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
package me.zhengjie.repository;

import me.zhengjie.domain.QueryMftLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
* @website https://el-admin.vip
* @author luob
* @date 2021-04-03
**/
public interface QueryMftLogRepository extends JpaRepository<QueryMftLog, Long>, JpaSpecificationExecutor<QueryMftLog> {

    @Query(value = "select id,page_size,next_page,result,res_msg,host,create_time, if(next_page = 'F',1,page_no) page_no, if(next_page = 'F',date_add(end_time,interval - 5 minute) ,start_time) start_time,if(next_page = 'F',if( date_add(end_time,interval + 23 hour)>sysdate(), sysdate(),date_add(end_time,interval + 23 hour)),end_time) end_time from bus_query_mft_log  where next_page <> 'E' and result = 'T' order by id desc limit 1" ,nativeQuery = true)
    QueryMftLog getPullTime();
}