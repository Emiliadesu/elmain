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
import me.zhengjie.domain.OrderHold;
import me.zhengjie.service.OrderHoldService;
import me.zhengjie.service.dto.OrderHoldQueryCriteria;
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
* @date 2021-12-30
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "orderHold管理")
@RequestMapping("/api/orderHold")
public class OrderHoldController {

    private final OrderHoldService orderHoldService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('orderHold:list')")
    public void download(HttpServletResponse response, OrderHoldQueryCriteria criteria) throws IOException {
        orderHoldService.download(orderHoldService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询orderHold")
    @ApiOperation("查询orderHold")
    @PreAuthorize("@el.check('orderHold:list')")
    public ResponseEntity<Object> query(OrderHoldQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(orderHoldService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增orderHold")
    @ApiOperation("新增orderHold")
    @PreAuthorize("@el.check('orderHold:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody OrderHold resources){
        return new ResponseEntity<>(orderHoldService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改orderHold")
    @ApiOperation("修改orderHold")
    @PreAuthorize("@el.check('orderHold:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody OrderHold resources){
        orderHoldService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除orderHold")
    @ApiOperation("删除orderHold")
    @PreAuthorize("@el.check('orderHold:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        orderHoldService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}