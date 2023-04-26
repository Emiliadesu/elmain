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
import me.zhengjie.domain.OrderHoldDetail;
import me.zhengjie.service.OrderHoldDetailService;
import me.zhengjie.service.dto.OrderHoldDetailQueryCriteria;
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
@Api(tags = "orderHoldDetail管理")
@RequestMapping("/api/orderHoldDetail")
public class OrderHoldDetailController {

    private final OrderHoldDetailService orderHoldDetailService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('orderHoldDetail:list')")
    public void download(HttpServletResponse response, OrderHoldDetailQueryCriteria criteria) throws IOException {
        orderHoldDetailService.download(orderHoldDetailService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询orderHoldDetail")
    @ApiOperation("查询orderHoldDetail")
    @PreAuthorize("@el.check('orderHoldDetail:list')")
    public ResponseEntity<Object> query(OrderHoldDetailQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(orderHoldDetailService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增orderHoldDetail")
    @ApiOperation("新增orderHoldDetail")
    @PreAuthorize("@el.check('orderHoldDetail:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody OrderHoldDetail resources){
        return new ResponseEntity<>(orderHoldDetailService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改orderHoldDetail")
    @ApiOperation("修改orderHoldDetail")
    @PreAuthorize("@el.check('orderHoldDetail:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody OrderHoldDetail resources){
        orderHoldDetailService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除orderHoldDetail")
    @ApiOperation("删除orderHoldDetail")
    @PreAuthorize("@el.check('orderHoldDetail:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        orderHoldDetailService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}