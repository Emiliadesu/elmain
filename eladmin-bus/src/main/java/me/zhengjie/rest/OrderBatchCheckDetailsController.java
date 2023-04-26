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
import me.zhengjie.domain.OrderBatchCheckDetails;
import me.zhengjie.service.OrderBatchCheckDetailsService;
import me.zhengjie.service.dto.OrderBatchCheckDetailsQueryCriteria;
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
@Api(tags = "orderBatchCheckDetails管理")
@RequestMapping("/api/orderBatchCheckDetails")
public class OrderBatchCheckDetailsController {

    private final OrderBatchCheckDetailsService orderBatchCheckDetailsService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('orderBatchCheckDetails:list')")
    public void download(HttpServletResponse response, OrderBatchCheckDetailsQueryCriteria criteria) throws IOException {
        orderBatchCheckDetailsService.download(orderBatchCheckDetailsService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询orderBatchCheckDetails")
    @ApiOperation("查询orderBatchCheckDetails")
    @PreAuthorize("@el.check('orderBatchCheckDetails:list')")
    public ResponseEntity<Object> query(OrderBatchCheckDetailsQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(orderBatchCheckDetailsService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增orderBatchCheckDetails")
    @ApiOperation("新增orderBatchCheckDetails")
    @PreAuthorize("@el.check('orderBatchCheckDetails:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody OrderBatchCheckDetails resources){
        return new ResponseEntity<>(orderBatchCheckDetailsService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改orderBatchCheckDetails")
    @ApiOperation("修改orderBatchCheckDetails")
    @PreAuthorize("@el.check('orderBatchCheckDetails:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody OrderBatchCheckDetails resources){
        orderBatchCheckDetailsService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除orderBatchCheckDetails")
    @ApiOperation("删除orderBatchCheckDetails")
    @PreAuthorize("@el.check('orderBatchCheckDetails:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        orderBatchCheckDetailsService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}