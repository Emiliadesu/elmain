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
import me.zhengjie.domain.StockAttr;
import me.zhengjie.service.StockAttrService;
import me.zhengjie.service.dto.StockAttrQueryCriteria;
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
* @author wangm
* @date 2021-06-08
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "卓志库存属性管理")
@RequestMapping("/api/stockAttr")
public class StockAttrController {

    private final StockAttrService stockAttrService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('stockAttr:list')")
    public void download(HttpServletResponse response, StockAttrQueryCriteria criteria) throws IOException {
        stockAttrService.download(stockAttrService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询卓志库存属性")
    @ApiOperation("查询卓志库存属性")
    @PreAuthorize("@el.check('stockAttr:list')")
    public ResponseEntity<Object> query(StockAttrQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(stockAttrService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增卓志库存属性")
    @ApiOperation("新增卓志库存属性")
    @PreAuthorize("@el.check('stockAttr:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody StockAttr resources){
        return new ResponseEntity<>(stockAttrService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改卓志库存属性")
    @ApiOperation("修改卓志库存属性")
    @PreAuthorize("@el.check('stockAttr:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody StockAttr resources){
        stockAttrService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除卓志库存属性")
    @ApiOperation("删除卓志库存属性")
    @PreAuthorize("@el.check('stockAttr:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        stockAttrService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}