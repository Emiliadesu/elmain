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
import me.zhengjie.domain.OrderReturnLog;
import me.zhengjie.service.OrderReturnLogService;
import me.zhengjie.service.dto.OrderReturnLogQueryCriteria;
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
* @date 2021-04-22
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "orderreturnlog管理")
@RequestMapping("/api/orderReturnLog")
public class OrderReturnLogController {

    private final OrderReturnLogService orderReturnLogService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('OrderReturn:list')")
    public void download(HttpServletResponse response, OrderReturnLogQueryCriteria criteria) throws IOException {
        orderReturnLogService.download(orderReturnLogService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询orderreturnlog")
    @ApiOperation("查询orderreturnlog")
    @PreAuthorize("@el.check('OrderReturn:list')")
    public ResponseEntity<Object> query(OrderReturnLogQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(orderReturnLogService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增orderreturnlog")
    @ApiOperation("新增orderreturnlog")
    @PreAuthorize("@el.check('orderReturnLog:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody OrderReturnLog resources){
        return new ResponseEntity<>(orderReturnLogService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改orderreturnlog")
    @ApiOperation("修改orderreturnlog")
    @PreAuthorize("@el.check('orderReturnLog:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody OrderReturnLog resources){
        orderReturnLogService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除orderreturnlog")
    @ApiOperation("删除orderreturnlog")
    @PreAuthorize("@el.check('orderReturnLog:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        orderReturnLogService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}