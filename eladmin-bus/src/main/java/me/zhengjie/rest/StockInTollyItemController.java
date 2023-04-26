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
import me.zhengjie.domain.StockInTollyItem;
import me.zhengjie.service.StockInTollyItemService;
import me.zhengjie.service.dto.StockInTollyItemQueryCriteria;
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
* @date 2021-03-23
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "入库理货单明细管理")
@RequestMapping("/api/stockInTollyItem")
public class StockInTollyItemController {

    private final StockInTollyItemService stockInTollyItemService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('stockInTollyItem:list')")
    public void download(HttpServletResponse response, StockInTollyItemQueryCriteria criteria) throws IOException {
        stockInTollyItemService.download(stockInTollyItemService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询入库理货单明细")
    @ApiOperation("查询入库理货单明细")
    @PreAuthorize("@el.check('stockInTollyItem:list')")
    public ResponseEntity<Object> query(StockInTollyItemQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(stockInTollyItemService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增入库理货单明细")
    @ApiOperation("新增入库理货单明细")
    @PreAuthorize("@el.check('stockInTollyItem:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody StockInTollyItem resources){
        return new ResponseEntity<>(stockInTollyItemService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改入库理货单明细")
    @ApiOperation("修改入库理货单明细")
    @PreAuthorize("@el.check('stockInTollyItem:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody StockInTollyItem resources){
        stockInTollyItemService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除入库理货单明细")
    @ApiOperation("删除入库理货单明细")
    @PreAuthorize("@el.check('stockInTollyItem:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        stockInTollyItemService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("更新理货统计")
    @ApiOperation("更新理货统计")
    @PreAuthorize("@el.check('stockInTollyItem:edit')")
    @PostMapping("update-stock-item")
    public ResponseEntity<Object> updateStockItem(Long stockId) {
        stockInTollyItemService.updateStockItem(stockId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
