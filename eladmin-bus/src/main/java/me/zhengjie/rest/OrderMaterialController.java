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
import me.zhengjie.domain.OrderMaterial;
import me.zhengjie.service.OrderMaterialService;
import me.zhengjie.service.dto.OrderMaterialQueryCriteria;
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
* @date 2022-06-22
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "orderMaterial管理")
@RequestMapping("/api/orderMaterial")
public class OrderMaterialController {

    private final OrderMaterialService orderMaterialService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('orderMaterial:list')")
    public void download(HttpServletResponse response, OrderMaterialQueryCriteria criteria) throws IOException {
        orderMaterialService.download(orderMaterialService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询orderMaterial")
    @ApiOperation("查询orderMaterial")
    @PreAuthorize("@el.check('orderMaterial:list')")
    public ResponseEntity<Object> query(OrderMaterialQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(orderMaterialService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增orderMaterial")
    @ApiOperation("新增orderMaterial")
    @PreAuthorize("@el.check('orderMaterial:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody OrderMaterial resources){
        return new ResponseEntity<>(orderMaterialService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改orderMaterial")
    @ApiOperation("修改orderMaterial")
    @PreAuthorize("@el.check('orderMaterial:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody OrderMaterial resources){
        orderMaterialService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除orderMaterial")
    @ApiOperation("删除orderMaterial")
    @PreAuthorize("@el.check('orderMaterial:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        orderMaterialService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}