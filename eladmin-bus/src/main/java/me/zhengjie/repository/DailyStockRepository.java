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

import me.zhengjie.domain.DailyStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
* @website https://el-admin.vip
* @author luob
* @date 2021-07-06
**/
public interface DailyStockRepository extends JpaRepository<DailyStock, Long>, JpaSpecificationExecutor<DailyStock> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM `bus_daily_stock` WHERE `day_time` = ?1", nativeQuery = true)
    void deleteByDayTime(String dayTime);

    @Query(value = "select count(1) FROM `bus_daily_stock` WHERE `day_time` = ?1", nativeQuery = true)
    Integer queryCountByDayTime(String dayTime);

    @Query(value = "SELECT goods_no,exp_time,prod_time,batch_no,stock_status,in_time,sum(qty) qty,location FROM bus_daily_stock " +
            "where day_time =:dayTime AND shop_id=:shopId GROUP BY goods_no,exp_time,prod_time,batch_no,stock_status,in_time,location ORDER BY goods_no",nativeQuery = true)
    List<Map<String,Object>>queryAllByShopIdAndDayTime(@Param("shopId")Long shopId, @Param("dayTime")String dayTime);
}