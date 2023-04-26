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

import me.zhengjie.domain.InboundOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
* @website https://el-admin.vip
* @author luob
* @date 2021-05-13
**/
public interface InboundOrderRepository extends JpaRepository<InboundOrder, Long>, JpaSpecificationExecutor<InboundOrder> {
    /**
    * 根据 OrderNo 查询
    * @param order_no /
    * @return /
    */
    InboundOrder findByOrderNo(String order_no);

    List<InboundOrder> findByStatus(Integer status);

    @Query(value = "SELECT * FROM bus_inbound_order WHERE is_online ='1' and take_back_time is null and `status`not in (999,888,600,646)",nativeQuery = true)
    List<InboundOrder> findAll1();

    InboundOrder findInboundOrderByWmsNo(String wmsNo);
}
