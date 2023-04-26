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
package me.zhengjie.rest;

import me.zhengjie.annotation.Log;
import me.zhengjie.domain.OrderDeliverLog;
import me.zhengjie.service.OrderDeliverLogService;
import me.zhengjie.service.dto.OrderDeliverLogQueryCriteria;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author luob
* @date 2021-04-03
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "orderdeliverlog管理")
@RequestMapping("/api/orderDeliverLog")
public class OrderDeliverLogController {

    private final OrderDeliverLogService orderDeliverLogService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('orderDeliverLog:list')")
    public void download(HttpServletResponse response, OrderDeliverLogQueryCriteria criteria) throws IOException {
        orderDeliverLogService.download(orderDeliverLogService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询orderdeliverlog")
    @ApiOperation("查询orderdeliverlog")
    @PreAuthorize("@el.check('orderDeliverLog:list')")
    public ResponseEntity<Object> query(OrderDeliverLogQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(orderDeliverLogService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增orderdeliverlog")
    @ApiOperation("新增orderdeliverlog")
    @PreAuthorize("@el.check('orderDeliverLog:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody OrderDeliverLog resources){
        return new ResponseEntity<>(orderDeliverLogService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改orderdeliverlog")
    @ApiOperation("修改orderdeliverlog")
    @PreAuthorize("@el.check('orderDeliverLog:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody OrderDeliverLog resources){
        orderDeliverLogService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除orderdeliverlog")
    @ApiOperation("删除orderdeliverlog")
    @PreAuthorize("@el.check('orderDeliverLog:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        orderDeliverLogService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}