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
import me.zhengjie.domain.StockInTolly;
import me.zhengjie.domain.WmsInstock;
import me.zhengjie.domain.WmsInstockItem;
import me.zhengjie.service.StockInTollyService;
import me.zhengjie.service.WmsInstockItemService;
import me.zhengjie.service.WmsInstockService;
import me.zhengjie.service.dto.StockInTollyQueryCriteria;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author wangm
* @date 2021-03-23
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "入库理货单管理")
@RequestMapping("/api/stockInTolly")
public class StockInTollyController {

    private final StockInTollyService stockInTollyService;
    private final WmsInstockService wmsInstockService;
    private final WmsInstockItemService wmsInstockItemService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('stockInTolly:list')")
    public void download(HttpServletResponse response, StockInTollyQueryCriteria criteria) throws IOException {
        stockInTollyService.download(stockInTollyService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询入库理货单")
    @ApiOperation("查询入库理货单")
    @PreAuthorize("@el.check('stockInTolly:list')")
    public ResponseEntity<Object> query(StockInTollyQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(stockInTollyService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @GetMapping("query-by-id")
    @Log("根据理货单id查询入库理货单")
    @ApiOperation("根据理货单id查询入库理货单")
    @PreAuthorize("@el.check('stockInTolly:list')")
    public ResponseEntity<Object> queryById(Long id){
        return new ResponseEntity<>(stockInTollyService.findByIdDto(id),HttpStatus.OK);
    }

    @GetMapping("query-by-in-id")
    @Log("根据理货单id查询入库理货单")
    @ApiOperation("根据理货单id查询入库理货单")
    @PreAuthorize("@el.check('stockInTolly:list')")
    public ResponseEntity<Object> queryByInId(Long inId){
        return new ResponseEntity<>(stockInTollyService.queryByInIdDto(inId),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增入库理货单")
    @ApiOperation("新增入库理货单")
    @PreAuthorize("@el.check('stockInTolly:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody StockInTolly resources){
        return new ResponseEntity<>(stockInTollyService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改入库理货单")
    @ApiOperation("修改入库理货单")
    @PreAuthorize("@el.check('stockInTolly:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody StockInTolly resources){
        stockInTollyService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除入库理货单")
    @ApiOperation("删除入库理货单")
    @PreAuthorize("@el.check('stockInTolly:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        stockInTollyService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping("tally-reg")
    @Log("根据通知单id新建或理货单状态更改为理货中")
    @ApiOperation("根据通知单id查询入库理货单")
    @PreAuthorize("@el.check('stockInTolly:reg')")
    public ResponseEntity<Object> tallyReg(@RequestParam(name = "inId") Long inId){
        WmsInstock wmsInstock=wmsInstockService.queryById(inId);
        List<WmsInstockItem>itemList=wmsInstockItemService.queryAllByInId(wmsInstock.getId());
        wmsInstock.setItemList(itemList);
        return new ResponseEntity<>(stockInTollyService.tallyReg(wmsInstock),HttpStatus.OK);
    }

    @PostMapping("tally-end")
    @Log("理货结束")
    @ApiOperation("理货结束")
    @PreAuthorize("@el.check('stockInTolly:edit')")
    public ResponseEntity<Object> tallyEnd(@RequestParam(name = "id") Long id){
        stockInTollyService.tallyEnd(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("tally-push")
    @Log("理货推送")
    @ApiOperation("理货推送")
    @PreAuthorize("@el.check('stockInTolly:push')")
    public ResponseEntity<Object> tallyPush(String inIdsStr){
        String[]idsStrArray=inIdsStr.split(",");
        Long[]ids=new Long[idsStrArray.length];
        for (int i = 0; i < idsStrArray.length; i++) {
            ids[i]=Long.parseLong(idsStrArray[i]);
        }
        stockInTollyService.tallyPush(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
