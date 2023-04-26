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
import me.zhengjie.domain.StockOutTolly;
import me.zhengjie.domain.WmsOutstock;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.StockOutTollyService;
import me.zhengjie.service.WmsOutstockService;
import me.zhengjie.service.dto.StockOutTollyQueryCriteria;
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
@Api(tags = "出库理货单管理")
@RequestMapping("/api/stockOutTolly")
public class StockOutTollyController {

    private final StockOutTollyService stockOutTollyService;
    private final WmsOutstockService wmsOutstockService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('stockOutTolly:list')")
    public void download(HttpServletResponse response, StockOutTollyQueryCriteria criteria) throws IOException {
        stockOutTollyService.download(stockOutTollyService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询出库理货单")
    @ApiOperation("查询出库理货单")
    @PreAuthorize("@el.check('stockOutTolly:list')")
    public ResponseEntity<Object> query(StockOutTollyQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(stockOutTollyService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增出库理货单")
    @ApiOperation("新增出库理货单")
    @PreAuthorize("@el.check('stockOutTolly:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody StockOutTolly resources){
        return new ResponseEntity<>(stockOutTollyService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改出库理货单")
    @ApiOperation("修改出库理货单")
    @PreAuthorize("@el.check('stockOutTolly:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody StockOutTolly resources){
        stockOutTollyService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除出库理货单")
    @ApiOperation("删除出库理货单")
    @PreAuthorize("@el.check('stockOutTolly:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        stockOutTollyService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("tally-reg")
    @Log("根据通知单id新建或理货单状态更改为理货中")
    @ApiOperation("根据通知单id查询入库理货单")
    @PreAuthorize("@el.check('stockOutTolly:reg')")
    public ResponseEntity<Object> tallyReg(@RequestParam(name = "outId") Long outId){
        WmsOutstock wmsOutstock=wmsOutstockService.queryById(outId);
        if (wmsOutstock==null)
            throw new BadRequestException("无出库通知单数据");
        return new ResponseEntity<>(stockOutTollyService.tallyReg(wmsOutstock),HttpStatus.OK);
    }
    @PostMapping("tally-end")
    @Log("根据通知单id新建或理货单状态更改为理货中")
    @ApiOperation("根据通知单id查询入库理货单")
    @PreAuthorize("@el.check('stockOutTolly:edit')")
    public ResponseEntity<Object> tallyEnd(@RequestParam(name = "id") Long id){
        stockOutTollyService.tallyEnd(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("update-status-by-out-id")
    @Log("根据通知单id新建或理货单状态更改为理货中")
    @ApiOperation("根据通知单id查询入库理货单")
    @PreAuthorize("@el.check('stockOutTolly:edit')")
    public ResponseEntity<Object> updateStatusByOutId(@RequestParam(name = "outId") Long outId,@RequestParam(name = "targetStatus") String targetStatus){
        stockOutTollyService.updateStatusByOutId(outId,targetStatus);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("query-by-id")
    @Log("根据理货单id查询出库理货单")
    @ApiOperation("根据理货单id查询出库理货单")
    @PreAuthorize("@el.check('stockOutTolly:list')")
    public ResponseEntity<Object> queryById(Long id){
        return new ResponseEntity<>(stockOutTollyService.findById(id),HttpStatus.OK);
    }

    @GetMapping("query-by-out-id")
    @Log("根据理货单id查询出库理货单")
    @ApiOperation("根据理货单id查询出库理货单")
    @PreAuthorize("@el.check('stockOutTolly:list')")
    public ResponseEntity<Object> queryByOutId(Long outId){
        return new ResponseEntity<>(stockOutTollyService.queryByOutIdDto(outId),HttpStatus.OK);
    }

    @GetMapping("check-diff")
    @Log("检查理货数据是否有差异")
    @ApiOperation("检查理货数据是否有差异")
    @PreAuthorize("@el.check('stockOutTolly:list')")
    public ResponseEntity<Object> checkDiff(Long id){
        return new ResponseEntity<>(stockOutTollyService.checkDiff(id),HttpStatus.OK);
    }
}
