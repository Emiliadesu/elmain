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

import me.zhengjie.domain.CombinationOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
* @website https://el-admin.vip
* @author wangm
* @date 2021-06-21
**/
public interface CombinationOrderRepository extends JpaRepository<CombinationOrder, Long>, JpaSpecificationExecutor<CombinationOrder> {
    /**
    * 根据 CombSkuId 查询
    * @param comb_sku_id /
    * @return /
    */
    CombinationOrder findByCombSkuId(String comb_sku_id);
}