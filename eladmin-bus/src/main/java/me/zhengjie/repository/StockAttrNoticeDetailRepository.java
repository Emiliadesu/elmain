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

import me.zhengjie.domain.StockAttrNoticeDetail;
import me.zhengjie.service.dto.StockAttrNoticeDetailDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
* @website https://el-admin.vip
* @author wangm
* @date 2021-04-15
**/
public interface StockAttrNoticeDetailRepository extends JpaRepository<StockAttrNoticeDetail, Long>, JpaSpecificationExecutor<StockAttrNoticeDetail> {
    @Query(value = "select * from bus_stock_attr_notice_detail where notice_id=?1 GROUP BY seq HAVING count(seq)<2;",nativeQuery = true)
    List<StockAttrNoticeDetail> getAllSingularSeq(Long stockAttrId);
}
