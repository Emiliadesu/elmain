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

import me.zhengjie.domain.OrderReturnDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

/**
* @website https://el-admin.vip
* @author luob
* @date 2021-04-14
**/
public interface OrderReturnDetailsRepository extends JpaRepository<OrderReturnDetails, Long>, JpaSpecificationExecutor<OrderReturnDetails> {

    @Query(value = "SELECT b.`return_id`,a.`logistics_no`,a.`is_over_time`,b.`goods_id`,b.`goods_code`,b.`goods_no`,b.`bar_code`,b.`hs_code`,\n" +
            "b.`font_goods_name`,b.`goods_name`,b.`qty`,b.`tax_amount`,b.`normal_num`,b.`damaged_num`,b.`total_num`,\n" +
            "b.`damaged_reason`,a.`create_time`,a.`s_express_no`,a.`shop_id`,c.`name`, a.`status`, a.`take_time`,d.`invt_no`,a.`order_no` FROM `bus_order_return` a " +
            "LEFT JOIN `bus_order_return_details` b ON a.`logistics_no` = b.`logistics_no` LEFT JOIN `bus_shop_info` c on a.`shop_id` = c.`id` LEFT JOIN " +
            "`bus_cross_border_order` d on a.`order_no` = d.`order_no`" +
            "where a.`create_time` > :startTime and a.`create_time` <= :endTime and a.`take_time` > :shStartTime and a.`take_time` <= :shEndTime and a.`status` in :status",nativeQuery = true)
    List<Object[]> queryAllWithDetails(@Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime,@Param("shStartTime") Timestamp shStartTime,@Param("shEndTime") Timestamp shEndTime, @Param("status") List<Integer> status);

    List<OrderReturnDetails> findByReturnIdAndBarCode(Long id, String barcode);
}