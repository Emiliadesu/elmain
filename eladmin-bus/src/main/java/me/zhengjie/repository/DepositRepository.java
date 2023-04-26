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

import me.zhengjie.domain.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
* @website https://el-admin.vip
* @author luob
* @date 2021-11-13
**/
public interface DepositRepository extends JpaRepository<Deposit, Long>, JpaSpecificationExecutor<Deposit> {
    Deposit findByShopId(Long shopId);

    @Transactional
    @Modifying
    @Query(value = "update bus_deposit set amount = amount + :changeAmount where id = :depositId" ,nativeQuery = true)
    void addAmount(Long depositId, BigDecimal changeAmount);

    @Transactional
    @Modifying
    @Query(value = "update bus_deposit set amount = amount - :changeAmount where id = :depositId" ,nativeQuery = true)
    void subAmount(Long depositId, BigDecimal changeAmount);
}