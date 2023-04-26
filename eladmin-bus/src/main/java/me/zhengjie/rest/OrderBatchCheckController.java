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
import me.zhengjie.domain.OrderBatchCheck;
import me.zhengjie.service.OrderBatchCheckService;
import me.zhengjie.service.dto.OrderBatchCheckQueryCriteria;
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
* @date 2022-05-04
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "orderBatchCheck管理")
@RequestMapping("/api/orderBatchCheck")
public class OrderBatchCheckController {

    private final OrderBatchCheckService orderBatchCheckService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('orderBatchCheck:list')")
    public void download(HttpServletResponse response, OrderBatchCheckQueryCriteria criteria) throws IOException {
        orderBatchCheckService.download(orderBatchCheckService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询orderBatchCheck")
    @ApiOperation("查询orderBatchCheck")
    @PreAuthorize("@el.check('orderBatchCheck:list')")
    public ResponseEntity<Object> query(OrderBatchCheckQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(orderBatchCheckService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增orderBatchCheck")
    @ApiOperation("新增orderBatchCheck")
    @PreAuthorize("@el.check('orderBatchCheck:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody OrderBatchCheck resources){
        return new ResponseEntity<>(orderBatchCheckService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改orderBatchCheck")
    @ApiOperation("修改orderBatchCheck")
    @PreAuthorize("@el.check('orderBatchCheck:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody OrderBatchCheck resources){
        orderBatchCheckService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除orderBatchCheck")
    @ApiOperation("删除orderBatchCheck")
    @PreAuthorize("@el.check('orderBatchCheck:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        orderBatchCheckService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}