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
import me.zhengjie.domain.StockOutTollyItem;
import me.zhengjie.service.StockOutTollyItemService;
import me.zhengjie.service.dto.StockOutTollyItemQueryCriteria;
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
* @date 2021-03-28
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "出库理货详情管理")
@RequestMapping("/api/stockOutTollyItem")
public class StockOutTollyItemController {

    private final StockOutTollyItemService stockOutTollyItemService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('stockOutTollyItem:list')")
    public void download(HttpServletResponse response, StockOutTollyItemQueryCriteria criteria) throws IOException {
        stockOutTollyItemService.download(stockOutTollyItemService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询出库理货详情")
    @ApiOperation("查询出库理货详情")
    @PreAuthorize("@el.check('stockOutTollyItem:list')")
    public ResponseEntity<Object> query(StockOutTollyItemQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(stockOutTollyItemService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增出库理货详情")
    @ApiOperation("新增出库理货详情")
    @PreAuthorize("@el.check('stockOutTollyItem:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody StockOutTollyItem resources){
        return new ResponseEntity<>(stockOutTollyItemService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改出库理货详情")
    @ApiOperation("修改出库理货详情")
    @PreAuthorize("@el.check('stockOutTollyItem:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody StockOutTollyItem resources){
        stockOutTollyItemService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除出库理货详情")
    @ApiOperation("删除出库理货详情")
    @PreAuthorize("@el.check('stockOutTollyItem:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        stockOutTollyItemService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}