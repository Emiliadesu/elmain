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
import me.zhengjie.domain.OrderLog;
import me.zhengjie.service.OrderLogService;
import me.zhengjie.service.dto.OrderLogQueryCriteria;
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
* @date 2021-03-27
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "orderlog管理")
@RequestMapping("/api/orderLog")
public class OrderLogController {

    private final OrderLogService orderLogService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('orderLog:list')")
    public void download(HttpServletResponse response, OrderLogQueryCriteria criteria) throws IOException {
        orderLogService.download(orderLogService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询orderlog")
    @ApiOperation("查询orderlog")
    @PreAuthorize("@el.check('orderLog:list')")
    public ResponseEntity<Object> query(OrderLogQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(orderLogService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增orderlog")
    @ApiOperation("新增orderlog")
    @PreAuthorize("@el.check('orderLog:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody OrderLog resources){
        return new ResponseEntity<>(orderLogService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改orderlog")
    @ApiOperation("修改orderlog")
    @PreAuthorize("@el.check('orderLog:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody OrderLog resources){
        orderLogService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除orderlog")
    @ApiOperation("删除orderlog")
    @PreAuthorize("@el.check('orderLog:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        orderLogService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}