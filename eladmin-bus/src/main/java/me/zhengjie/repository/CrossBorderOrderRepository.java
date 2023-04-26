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

import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.domain.CrossBorderOrderDetails;
import me.zhengjie.service.dto.CrossBorderOrderQueryCriteria;
import me.zhengjie.service.dto.HomeDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
* @website https://el-admin.vip
* @author luob
* @date 2021-03-25
**/
public interface CrossBorderOrderRepository extends JpaRepository<CrossBorderOrder, Long>, JpaSpecificationExecutor<CrossBorderOrder> {
    /**
    * 根据 OrderNo 查询
    * @param order_no /
    * @return /
    */
    CrossBorderOrder findByOrderNo(String order_no);

    @Query(value = "SELECT COUNT(status) as orderTotalCount, COUNT( IF(status = 999,status,null) ) as orderFreezeCount, COUNT( IF(status = 245,status,null) ) as orderDeliverCount, COUNT(IF(status < 245,1,null)  ) as  orderWaitCount,COUNT(IF(status = 888,1,null)  ) as orderCancelCount FROM bus_cross_border_order WHERE create_time >= :startTime AND create_time <= :endTime AND `platform_code` = 'DY'", nativeQuery = true)
    Map<String,Object> orderTotalCount(@Param("startTime")String startTime, @Param("endTime")String endTime);

    @Query(value = "SELECT COUNT(status) as orderTotalCount, COUNT( IF(status = 999,status,null) ) as orderFreezeCount, COUNT( IF(status = 245,status,null) ) as orderDeliverCount, COUNT(IF(status < 245,1,null)  ) as  orderWaitCount,COUNT(IF(status = 888,1,null)  ) as orderCancelCount FROM bus_cross_border_order WHERE create_time >= :startTime AND create_time <= :endTime", nativeQuery = true)
    Map<String,Object> orderTotalCountAll(@Param("startTime")String startTime, @Param("endTime")String endTime);

    @Query(value = "SELECT COUNT(status) as orderTotalCount, COUNT( IF(status = 999,status,null) ) as orderFreezeCount, COUNT( IF(status = 245,status,null) ) as orderDeliverCount, COUNT(IF(status < 245,1,null)  ) as  orderWaitCount,COUNT(IF(status = 888,1,null)  ) as orderCancelCount FROM bus_cross_border_order WHERE create_time >= :startTime AND create_time <= :endTime and platform_code =:platform ", nativeQuery = true)
    Map<String,Object> orderTotalCountWithPlatform(@Param("startTime")String startTime, @Param("endTime")String endTime, @Param("platform")String platform);

    @Query(value = "SELECT COUNT(status) as orderTotalCount, COUNT( IF(status = 999,status,null) ) as orderFreezeCount, COUNT( IF(status = 245,status,null) ) as orderDeliverCount, COUNT(IF(status < 245,1,null)  ) as  orderWaitCount,COUNT(IF(status = 888,1,null)  ) as orderCancelCount FROM bus_cross_border_order WHERE create_time >= :startTime AND create_time <= :endTime AND shop_id in :shopIds AND `platform_code` = 'DY'", nativeQuery = true)
    Map<String,Object> orderTotalCountWithCusId(@Param("startTime")String startTime, @Param("endTime")String endTime, @Param("shopIds")List<Long> shopIds);

    @Query(value = "SELECT COUNT(status) as orderTotalCount, COUNT( IF(status = 999,status,null) ) as orderFreezeCount, COUNT( IF(status = 245,status,null) ) as orderDeliverCount, COUNT(IF(status < 245,1,null)  ) as  orderWaitCount,COUNT(IF(status = 888,1,null)  ) as orderCancelCount FROM bus_cross_border_order WHERE create_time >= :startTime AND create_time <= :endTime AND shop_id in :shopIds", nativeQuery = true)
    Map<String,Object> orderTotalCountWithCusIdAll(@Param("startTime")String startTime, @Param("endTime")String endTime, @Param("shopIds")List<Long> shopIds);

    @Query(value = "SELECT COUNT(status) as orderTotalCount, COUNT( IF(status = 999,status,null) ) as orderFreezeCount, COUNT( IF(status = 245,status,null) ) as orderDeliverCount, COUNT(IF(status < 245,1,null)  ) as  orderWaitCount,COUNT(IF(status = 888,1,null)  ) as orderCancelCount FROM bus_cross_border_order WHERE create_time >= :startTime AND create_time <= :endTime AND shop_id in :shopIds AND `platform_code` = :platform", nativeQuery = true)
    Map<String,Object> orderTotalCountWithCusIdPlatform(@Param("startTime")String startTime, @Param("endTime")String endTime, @Param("shopIds")List<Long> shopIds,@Param("platform")String platform);

    @Query(value = "SELECT `shop_id` shopId,COUNT(1) allOrder, COUNT( IF(status = 201,status,null) ) as orderreceivedErrCount,  COUNT( IF(status = 999,status,null) ) as orderFreezeCount,  COUNT(IF(`status` = 200, `status` , null)  ) received , COUNT(IF(`status` = 215, `status` , null)  ) receivedBack     \n" +
            " , COUNT(IF(`status` = 220, `status` , null)  ) clearStart , COUNT(IF(`status` = 225, `status` , null)  ) clearStartBack , COUNT(IF(`status` = 230, `status` , null)  ) clearSucc\n" +
            " , COUNT(IF(`status` = 235, `status` , null)  ) clearSuccBack , COUNT(IF(`status` = 240, `status` , null)  ) weighing , COUNT(IF(`status` = 245, `status` , null)  ) deliver   \n" +
            ", COUNT(IF(`status` < 245, `status` , null)  ) waitDeliver , COUNT(IF(`status`  = 888, `status` , null)  ) cancel   \n" +
            "FROM `bus_cross_border_order` WHERE create_time >= :startTime AND create_time <= :endTime GROUP BY `shop_id` ORDER BY allOrder DESC ", nativeQuery = true)
    List<Map<String,Object>> shopOrderCount(@Param("startTime")String startTime, @Param("endTime")String endTime);

    @Query(value = "SELECT `shop_id` shopId,COUNT(1) allOrder,COUNT( IF(status = 201,status,null) ) as orderreceivedErrCount,  COUNT( IF(status = 999,status,null) ) as orderFreezeCount,  COUNT(IF(`status` = 200, `status` , null)  ) received , COUNT(IF(`status` = 215, `status` , null)  ) receivedBack     \n" +
            " , COUNT(IF(`status` = 220, `status` , null)  ) clearStart , COUNT(IF(`status` = 225, `status` , null)  ) clearStartBack , COUNT(IF(`status` = 230, `status` , null)  ) clearSucc\n" +
            " , COUNT(IF(`status` = 235, `status` , null)  ) clearSuccBack , COUNT(IF(`status` = 240, `status` , null)  ) weighing , COUNT(IF(`status` = 245, `status` , null)  ) deliver   \n" +
            ", COUNT(IF(`status` < 245, `status` , null)  ) waitDeliver , COUNT(IF(`status`  = 888, `status` , null)  ) cancel   \n" +
            "FROM `bus_cross_border_order` WHERE order_create_time >= :startTime AND order_create_time <= :endTime and platform_code = :platform GROUP BY `shop_id` ORDER BY allOrder DESC ", nativeQuery = true)
    List<Map<String,Object>> shopOrderCountByPlatform(@Param("startTime")String startTime, @Param("endTime")String endTime, @Param("platform")String platform);


    @Query(value = "SELECT `shop_id` shopId,COUNT(1) allOrder,COUNT( IF(status = 201,status,null) ) as orderreceivedErrCount,  COUNT( IF(status = 999,status,null) ) as orderFreezeCount,  COUNT(IF(`status` = 200, `status` , null)  ) received , COUNT(IF(`status` = 215, `status` , null)  ) receivedBack     \n" +
            " , COUNT(IF(`status` = 220, `status` , null)  ) clearStart , COUNT(IF(`status` = 225, `status` , null)  ) clearStartBack , COUNT(IF(`status` = 230, `status` , null)  ) clearSucc\n" +
            " , COUNT(IF(`status` = 235, `status` , null)  ) clearSuccBack , COUNT(IF(`status` = 240, `status` , null)  ) weighing , COUNT(IF(`status` = 245, `status` , null)  ) deliver   \n" +
            ", COUNT(IF(`status` < 245, `status` , null)  ) waitDeliver , COUNT(IF(`status`  = 888, `status` , null)  ) cancel   \n" +
            "FROM `bus_cross_border_order` WHERE create_time >= :startTime AND create_time <= :endTime AND shop_id in :shopIds GROUP BY `shop_id` ", nativeQuery = true)
    List<Map<String,Object>> shopOrderCountWithCusId(@Param("startTime")String startTime, @Param("endTime")String endTime, @Param("shopIds")List<Long> shopIds);

    @Query(value = "SELECT `shop_id` shopId,COUNT(1) allOrder,COUNT( IF(status = 201,status,null) ) as orderreceivedErrCount,  COUNT( IF(status = 999,status,null) ) as orderFreezeCount,  COUNT(IF(`status` = 200, `status` , null)  ) received , COUNT(IF(`status` = 215, `status` , null)  ) receivedBack     \n" +
            " , COUNT(IF(`status` = 220, `status` , null)  ) clearStart , COUNT(IF(`status` = 225, `status` , null)  ) clearStartBack , COUNT(IF(`status` = 230, `status` , null)  ) clearSucc\n" +
            " , COUNT(IF(`status` = 235, `status` , null)  ) clearSuccBack , COUNT(IF(`status` = 240, `status` , null)  ) weighing , COUNT(IF(`status` = 245, `status` , null)  ) deliver   \n" +
            ", COUNT(IF(`status` < 245, `status` , null)  ) waitDeliver , COUNT(IF(`status`  = 888, `status` , null)  ) cancel   \n" +
            "FROM `bus_cross_border_order` WHERE create_time >= :startTime AND create_time <= :endTime AND shop_id in :shopIds and platform_code = :platform GROUP BY `shop_id` ", nativeQuery = true)
    List<Map<String,Object>> shopOrderCountWithCusIdPlatform(@Param("startTime")String startTime, @Param("endTime")String endTime, @Param("shopIds")List<Long> shopIds,@Param("platform")String platform);

    @Query(value = "SELECT DATE_FORMAT(`create_time`,'%d-%H') hours , COUNT(1)   allOrder\n" +
            "FROM `bus_cross_border_order` WHERE `create_time` >= :startTime and `create_time` <= :endTime GROUP BY hours", nativeQuery = true)
    List<Map<String,Object>> queryHourInData(@Param("startTime")String startTime, @Param("endTime")String endTime);

    @Query(value = "SELECT DATE_FORMAT(`deliver_time`,'%d-%H') hours , COUNT(1)   allOrder\n" +
            "FROM `bus_cross_border_order` WHERE `create_time` >= :startTime and `create_time` <= :endTime  and `status` = 245 GROUP BY hours", nativeQuery = true)
    List<Map<String,Object>> queryHourOutData(@Param("startTime")String startTime, @Param("endTime")String endTime);

    @Query(value = "SELECT COUNT(1) allOrder " +
            "FROM `bus_cross_border_order` WHERE `create_time` >= :startTime and `create_time` <= :endTime", nativeQuery = true)
    Integer queryAllIntData(@Param("startTime")String startTime, @Param("endTime")String endTime);

    @Query(value = "SELECT COUNT(IF(`status` = 245, `status` , null)) allOrder\n" +
            "FROM `bus_cross_border_order` WHERE `create_time` >= :startTime and `create_time` <= :endTime", nativeQuery = true)
    Integer queryAllOutData(@Param("startTime")String startTime, @Param("endTime")String endTime);

    @Query(value = "SELECT COUNT(IF(`status` < 245, `status` , null)  ) allOrder\n" +
            "FROM `bus_cross_border_order` WHERE `create_time` >= :startTime and `create_time` <= :endTime", nativeQuery = true)
    Integer queryAllWaiteData(@Param("startTime")String startTime, @Param("endTime")String endTime);

    @Query(value = "SELECT a.`order_no` , a.`status` , c.`cust_nick_name` , d.`name` , b.`payment` , b.`tax_amount` , a.`logistics_no` ,\n" +
            "a.`province` , a.`city` ,a.`district` , a.`consignee_addr` ,a.`pay_time` ,a.`order_create_time` , a.`deliver_time` ,  \n" +
            "a.logistics_collect_time,b.`goods_code` , b.`font_goods_name` ,b.`goods_name` ,b.`goods_no`, b.`bar_code`, b.`qty`,a.cross_border_no \n" +
            "FROM `bus_cross_border_order` a LEFT JOIN `bus_cross_border_order_details` b ON a.`id` = b.`order_id`\n" +
            "LEFT JOIN `bus_customer_info` c on a.`customers_id` = c.`id` LEFT JOIN `bus_shop_info` d on a.`shop_id` = d.`id` where 1=1 " +
            "and `order_create_time` > :startTime and `order_create_time` <= :endTime and `shop_id` in :shopId"  , nativeQuery = true)
    List<Object[]> queryAllWithDetails(@Param("startTime") Timestamp startTime, @Param("endTime")Timestamp endTime, @Param("shopId")List<Long> shopId);

    CrossBorderOrder findByLogisticsNo(String mailNo);

    @Query(value = "select * from bus_cross_border_order where (payment_no is null or payment_no ='') and `status`=999",nativeQuery = true)
    List<CrossBorderOrder> queryByNonPayNo();

    @Query(value = "select count(id) from bus_cross_border_order where province = :province and city = :city and district = :district and consignee_addr = :consigneeAddr" +
            " and shop_id = :shopId and create_time > :createDate",nativeQuery = true)
    int queryCountByAddress(@Param("province")String province,
                            @Param("city")String city,
                            @Param("district")String district,
                            @Param("consigneeAddr")String consigneeAddr,
                            @Param("shopId")Long shopId,
                            @Param("createDate")String createDate);

    CrossBorderOrder findByOrderNoAndDeclareNo(String orderNo, String declareNo);

    CrossBorderOrder findByCrossBorderNo(String crossBorderNo);

    @Query(value = "select * from bus_cross_border_order where logistics_status = 1 and logistics_no is not null and status = 245 and platform_code !='DY'  and supplier_id in(4,7);",nativeQuery = true)
    List<CrossBorderOrder> findAllUnCollectedOrder();

    @Query(value = "select * from bus_cross_border_order where platform_code not in('DY','DW') and `status` not in (240,245,888);",nativeQuery = true)
    List<CrossBorderOrder> refreshPlatformStatus2();

    @Query(value = "select * from bus_cross_border_order where status = 888 and  (declare_status <> '99' or declare_status is null) and declare_no is not null and platform_code != 'DY' and create_time > date_sub(now(), interval 1 month)  ;",nativeQuery = true)
    List<CrossBorderOrder> findCancelNoCucc();

    @Query(value = "select * from bus_cross_border_order where status = 245 and (invt_no is null or invt_no = '') and declare_no is not null and platform_code <> 'DW';",nativeQuery = true)
    List<CrossBorderOrder> findDeliverNoInvtNo();

    List<CrossBorderOrder> findByPlatformCodeAndStatusAndClearDelStartBackTimeIsNullAndDeclareNoIsNotNull(String platformCode,Integer status);

    List<CrossBorderOrder> findByPlatformCodeAndStatusAndClearDelSuccessBackTimeIsNullAndClearDelStartBackTimeIsNotNullAndDeclareNoIsNotNull(String platformCode,Integer status);

    List<CrossBorderOrder> findByPlatformCodeAndStatusAndCancelTimeBackIsNullAndInvtNoIsNullAndDeclareNoIsNotNull(String platformCode, Integer status);

    CrossBorderOrder findByCrossBorderNoAndDeclareNo(String crossBorderNo, String declareNo);

    List<CrossBorderOrder> findByWaveNo(String waveNo);

    @Query(value = "select * from bus_cross_border_order where status = 888 and  declare_no is not null and declare_status != 99",nativeQuery = true)
    List<CrossBorderOrder> findNoDecDel();

    @Query(value = "select * from bus_cross_border_order where status = 888 and declare_status = 29 and clear_del_start_time is not null and clear_del_start_time != ''",nativeQuery = true)
    List<CrossBorderOrder>clearDelIsSucc();

    @Query(value = "select count(1) currentCreateOrder, sum(total_num)/count(1) currentQty, COUNT(IF(status = 245, status , null)) currentDeliverOrder\n" +
            ", COUNT(IF(status != 245, status , null)) currentDeliverOrder \n" +
            "from bus_cross_border_order \n" +
            "where order_create_time >= '2022-11-01 18:00:00' and order_create_time < '2022-11-02 18:00:00' and create_time < '2022-11-02 05:00:00'\n" +
            "and pre_sell = '0' and status != 888 ", nativeQuery = true)
    Map<String, Object> queryHourOrderCount(String startCountTime, String endCountTime, String startOrderCreateTime, String endOrderCreateTime, String preSell);

    // 查询小时进单
    @Query(value = "select DATE_FORMAT(a.create_time, '%Y-%m-%d %H') AS time, count(a.order_no) as createOrder, \n" +
            "COUNT(IF(a.pick_type = 1, order_no , null)  ) as createPLOrder, COUNT(IF(a.pick_type = 2, order_no , null)  ) as createSDOrder\n" +
            "from bus_cross_border_order a \n" +
            "where a.create_time >= :startCreateTime and  a.create_time <= :endCreateTime and a.platform_code = :platformCode and (a.pre_sell <> '1' or a.pre_sell is null) \n" +
            "group by time order by time", nativeQuery = true)
    List<Map<String,Object>> queryProcessIn(@Param("startCreateTime")String startCreateTime, @Param("endCreateTime")String endCreateTime, @Param("platformCode")String platformCode);

    // 查询小时出库
    @Query(value = "select DATE_FORMAT(a.deliver_time, '%Y-%m-%d %H') AS time, count(a.order_no) as deliverAllOrder, \n" +
            "COUNT(IF(a.pick_type = 1, order_no , null)  ) as deliverPLOrder, COUNT(IF(a.pick_type = 2, order_no , null)  ) as deliverSDOrder\n" +
            "from bus_cross_border_order a \n" +
            "where a.deliver_time >= :startOutTime and  a.deliver_time <= :endOutTime and platform_code = :platformCode and (a.pre_sell <> '1' or a.pre_sell is null) \n" +
            "group by time order by time", nativeQuery = true)
    List<Map<String, Object>> queryProcessOut(String startOutTime, String endOutTime, String platformCode);

    // 查询当前未出
    @Query(value = "select COUNT(IF(  a.status != 888, order_no , null) ) as allOrder,  " +
            "TRUNCATE(sum(total_num)/count(order_no),2) allPiece, " +
            "TRUNCATE(sum(IF(a.pick_type = 1, total_num , 0)  )/count(IF(a.pick_type = 1, order_no , null)  ),2) allPLPiece,  " +
            "TRUNCATE(sum(IF(a.pick_type = 2, total_num , 0)  )/count(IF(a.pick_type = 2, order_no , null)  ),2) allSDPiece,  " +
            "COUNT(IF((a.pick_type = 1 and a.status != 245 and a.status != 888), order_no , null)  ) as waitPLOrder, " +
            "COUNT(IF(a.pick_type = 1, order_no , null)  ) as allPLOrder, COUNT(IF(a.pick_type = 2, order_no , null)  ) as allSDOrder,\n" +
            "COUNT(IF((a.pick_type = 1 and a.status = 245), order_no , null)  ) as allPLDeliverOrder, COUNT(IF((a.pick_type = 2 and a.status = 245), order_no , null)  ) as allSDDeliverOrder\n" +
            ", COUNT(IF( a.status = 245, order_no , null)  ) as allDeliverOrder," +
            "COUNT(IF((a.pick_type = 2 and a.status != 245 and a.status != 888), order_no , null)  ) as waitSDOrder,\n" +
            "COUNT(IF((a.pick_type is null and a.status != 245 and a.status != 888), order_no , null)  ) as waitWave\n" +
            ", COUNT(IF( (a.status != 245 and a.status != 888), order_no , null)  ) as waitAllOrder\n" +
            "from bus_cross_border_order a \n" +
            "where a.create_time >= :startOutTime and  a.create_time <= :endOutTime and a.platform_code = :platformCode and (a.pre_sell <> '1' or a.pre_sell is null) ", nativeQuery = true)
    List<Map<String, Object>> queryCurrentNoDeliver(String startOutTime, String endOutTime, String platformCode);
    @Query(value ="SELECT * FROM bus_cross_border_order WHERE `status` = 235 AND lp_code IS NOT NULL",nativeQuery = true)
    List<CrossBorderOrder> queryByCNDeliverOrder();
}
