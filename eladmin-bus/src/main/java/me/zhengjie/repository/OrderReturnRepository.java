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

import me.zhengjie.domain.OrderReturn;
import me.zhengjie.domain.OrderReturnDetails;
import me.zhengjie.service.dto.OrderReturnDetailsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
* @website https://el-admin.vip
* @author luob
* @date 2021-04-14
**/
public interface OrderReturnRepository extends JpaRepository<OrderReturn, Long>, JpaSpecificationExecutor<OrderReturn> {

    @Query(value = "SELECT b.`return_id`,a.`logistics_no`,a.`is_over_time`,b.`goods_id`,b.`goods_code`,b.`goods_no`,b.`bar_code`,b.`hs_code`,\n" +
            "b.`font_goods_name`,b.`goods_name`,b.`qty`,b.`tax_amount`,b.`normal_num`,b.`damaged_num`,b.`total_num`,\n" +
            "b.`damaged_reason` FROM `bus_order_return` a LEFT JOIN `bus_order_return_details` b ON \n" +
            "a.`logistics_no` = b.`logistics_no`",nativeQuery = true)
    List<Object[]> downloadDetails(List<OrderReturnDetails> all, HttpServletResponse response);

    OrderReturn findByOrderNo(String orderNo);

    OrderReturn findByDeclareNo(String declareNo);

    @Modifying
    @Transactional
    @Query(value = "insert into bus_return_gather_detail(gather_id, gather_no, goods_no, bar_code, return_ids,qty,goods_name) \n" +
            "select ?2 gather_id, ?3 gather_no, b.goods_no goodsNo,b.bar_code barCode, group_concat(a.id) ids, sum(b.qty) qty , b.goods_name goodsName \n" +
            "from bus_order_return a left join bus_order_return_details  b on a.id = b.return_id \n" +
            "where a.status = 337 and (a.is_wave <> '1' or a.is_wave is null ) and shop_id  = ?1  group by  b.goods_no",nativeQuery = true)
    void queryGatherByShop(@Param("shopId")Long shopId, @Param("gatherId")Long gatherId, @Param("gatherNo")String gatherNo);

    @Query(value = "select a.shop_id shopId , b.name shopName , count(a.order_no) orderCount  from bus_order_return a left join bus_shop_info b on a.shop_id = b.id where a.status = 337 and (a.is_wave <> '1' or a.is_wave is null ) group by a.shop_id ",nativeQuery = true)
    List<Map<String, Object>> queryWaitGather();

    List<OrderReturn>findAllByGatherNoAndShopId(String gatherNo,Long shopId);
}