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
import me.zhengjie.domain.HezhuOrder;
import me.zhengjie.service.HezhuOrderService;
import me.zhengjie.service.dto.HezhuOrderQueryCriteria;
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
* @date 2021-08-26
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "hezhuOrder管理")
@RequestMapping("/api/hezhuOrder")
public class HezhuOrderController {

    private final HezhuOrderService hezhuOrderService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('hezhuOrder:list')")
    public void download(HttpServletResponse response, HezhuOrderQueryCriteria criteria) throws IOException {
        hezhuOrderService.download(hezhuOrderService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询hezhuOrder")
    @ApiOperation("查询hezhuOrder")
    @PreAuthorize("@el.check('hezhuOrder:list')")
    public ResponseEntity<Object> query(HezhuOrderQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(hezhuOrderService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增hezhuOrder")
    @ApiOperation("新增hezhuOrder")
    @PreAuthorize("@el.check('hezhuOrder:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody HezhuOrder resources){
        return new ResponseEntity<>(hezhuOrderService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改hezhuOrder")
    @ApiOperation("修改hezhuOrder")
    @PreAuthorize("@el.check('hezhuOrder:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody HezhuOrder resources){
        hezhuOrderService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除hezhuOrder")
    @ApiOperation("删除hezhuOrder")
    @PreAuthorize("@el.check('hezhuOrder:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        hezhuOrderService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}