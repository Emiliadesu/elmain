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

import me.zhengjie.domain.OrderHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
* @website https://el-admin.vip
* @author luob
* @date 2021-12-30
**/
public interface OrderHoldRepository extends JpaRepository<OrderHold, Long>, JpaSpecificationExecutor<OrderHold> {

    @Query(value = "select * from bus_logistics_unarrive_place where 1=1 and logistics_id = ?1 and province = ?2 and city = ?3 and district = ?4 ", nativeQuery = true)
    OrderHold findOrderHold(Long shopId, String platformCode);
}