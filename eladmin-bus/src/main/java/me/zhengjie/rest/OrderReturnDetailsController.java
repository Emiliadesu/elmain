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
import me.zhengjie.domain.OrderReturnDetails;
import me.zhengjie.service.OrderReturnDetailsService;
import me.zhengjie.service.dto.OrderReturnDetailsQueryCriteria;
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
* @date 2021-04-14
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "orderreturndetails管理")
@RequestMapping("/api/OrderReturnDetails")
public class OrderReturnDetailsController {

    private final OrderReturnDetailsService OrderReturnDetailsService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('OrderReturnDetails:list')")
    public void download(HttpServletResponse response, OrderReturnDetailsQueryCriteria criteria) throws IOException {
        OrderReturnDetailsService.download(OrderReturnDetailsService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询orderreturndetails")
    @ApiOperation("查询orderreturndetails")
    @PreAuthorize("@el.check('OrderReturnDetails:list')")
    public ResponseEntity<Object> query(OrderReturnDetailsQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(OrderReturnDetailsService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增orderreturndetails")
    @ApiOperation("新增orderreturndetails")
    @PreAuthorize("@el.check('OrderReturnDetails:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody OrderReturnDetails resources){
        return new ResponseEntity<>(OrderReturnDetailsService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改orderreturndetails")
    @ApiOperation("修改orderreturndetails")
    @PreAuthorize("@el.check('OrderReturnDetails:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody OrderReturnDetails resources){
        OrderReturnDetailsService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除orderreturndetails")
    @ApiOperation("删除orderreturndetails")
    @PreAuthorize("@el.check('OrderReturnDetails:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        OrderReturnDetailsService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}