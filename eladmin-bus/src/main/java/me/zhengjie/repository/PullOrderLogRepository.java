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

import me.zhengjie.domain.PullOrderLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


/**
* @website https://el-admin.vip
* @author luob
* @date 2021-04-01
**/
public interface PullOrderLogRepository extends JpaRepository<PullOrderLog, Long>, JpaSpecificationExecutor<PullOrderLog> {


    @Query(value = "select id,shop_id,page_size,next_page,total,result,res_msg,host,create_time, 0 page_no, date_add(end_time,interval - 1 minute)  start_time,if( date_add(end_time,interval + 23 hour)>sysdate(), sysdate(),date_add(end_time,interval + 23 hour)) end_time \n" +
            "from bus_pull_order_log where shop_id=:shopId and next_page <> 'E' and result = 'T' order by id  desc limit 1" ,nativeQuery = true)
    PullOrderLog getPullTime(@Param("shopId") Long shopId);
}