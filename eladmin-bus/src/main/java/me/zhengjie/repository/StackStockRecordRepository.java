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

import me.zhengjie.domain.StackStockRecord;
import me.zhengjie.service.dto.StackStockRecordQueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
* @website https://el-admin.vip
* @author wangm
* @date 2021-07-16
**/
public interface StackStockRecordRepository extends JpaRepository<StackStockRecord, Long>, JpaSpecificationExecutor<StackStockRecord> {
    @Query(nativeQuery = true,value = "select a.id,a.batch_no,a.sku,a.first_receive_date,a.asn_code,a.is_damaged,a.stock_qty,a.plate_num,a.shop_code,a.create_date,a.customer_batch" +
            " from bus_stack_stock_record a right JOIN bus_stock_attr b on a.batch_no=b.wms_batch_no" +
            " where create_date = :createDate",
            countQuery = "select count(a.id)" +
                    " from bus_stack_stock_record a right JOIN bus_stock_attr b on a.batch_no=b.wms_batch_no" +
                    " where create_date = :createDate")
    Page<StackStockRecord>queryPage(@Param("createDate")String atTime, Pageable pageable);

    @Query(nativeQuery = true,value = "select a.id,a.batch_no,a.sku,a.first_receive_date,a.asn_code,a.is_damaged,a.stock_qty,a.plate_num,a.shop_code,a.create_date,a.customer_batch" +
            " from bus_stack_stock_record a right JOIN bus_stock_attr b on a.batch_no=b.wms_batch_no" +
            " where create_date = :createDate and shop_code = :shopCode",
            countQuery = "select count(a.id)" +
                    " from bus_stack_stock_record a right JOIN bus_stock_attr b on a.batch_no=b.wms_batch_no" +
                    " where create_date = :createDate and shop_code = :shopCode")
    Page<StackStockRecord>queryPage(@Param("createDate")String atTime,@Param("shopCode")String shopCode,Pageable pageable);

    @Query(nativeQuery = true,value = "select a.id,a.batch_no,a.sku,a.first_receive_date,a.asn_code,a.is_damaged,a.stock_qty,a.plate_num,a.shop_code,a.create_date,a.customer_batch" +
            " from bus_stack_stock_record a right JOIN bus_stock_attr b on a.batch_no=b.wms_batch_no" +
            " where create_date = :createDate and shop_code = :shopCode and sku in (:goodsNos)",
            countQuery = "select count(a.id)" +
                    " from bus_stack_stock_record a right JOIN bus_stock_attr b on a.batch_no=b.wms_batch_no" +
                    " where create_date = :createDate and shop_code = :shopCode and sku in (:goodsNos)")
    Page<StackStockRecord>queryPage(@Param("createDate")String atTime,@Param("shopCode")String shopCode,@Param("goodsNos")List<String>goodsNos,Pageable pageable);

    @Query(nativeQuery = true,value = "select a.id,a.batch_no,a.sku,a.first_receive_date,a.asn_code,a.is_damaged,a.stock_qty,a.plate_num,a.shop_code,a.create_date,a.customer_batch" +
            " from bus_stack_stock_record a right JOIN bus_stock_attr b on a.batch_no=b.wms_batch_no" +
            " where create_date = :createDate and sku in (:goodsNos)",
            countQuery = "select count(a.id)" +
                    " from bus_stack_stock_record a right JOIN bus_stock_attr b on a.batch_no=b.wms_batch_no" +
                    " where create_date = :createDate and sku in (:goodsNos)")
    Page<StackStockRecord>queryPage(@Param("createDate")String atTime,@Param("goodsNos")List<String>goodsNos,Pageable pageable);
}
