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
import me.zhengjie.domain.DataOrder;
import me.zhengjie.service.DataOrderService;
import me.zhengjie.service.dto.DataOrderQueryCriteria;
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
* @date 2022-03-30
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "dataorder管理")
@RequestMapping("/api/dataOrder")
public class DataOrderController {

    private final DataOrderService dataOrderService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('dataOrder:list')")
    public void download(HttpServletResponse response, DataOrderQueryCriteria criteria) throws IOException {
        dataOrderService.download(dataOrderService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询dataorder")
    @ApiOperation("查询dataorder")
    @PreAuthorize("@el.check('dataOrder:list')")
    public ResponseEntity<Object> query(DataOrderQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(dataOrderService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增dataorder")
    @ApiOperation("新增dataorder")
    @PreAuthorize("@el.check('dataOrder:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody DataOrder resources){
        return new ResponseEntity<>(dataOrderService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改dataorder")
    @ApiOperation("修改dataorder")
    @PreAuthorize("@el.check('dataOrder:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody DataOrder resources){
        dataOrderService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除dataorder")
    @ApiOperation("删除dataorder")
    @PreAuthorize("@el.check('dataOrder:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        dataOrderService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}