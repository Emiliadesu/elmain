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
import me.zhengjie.domain.StockAttrNotice;
import me.zhengjie.service.StockAttrNoticeService;
import me.zhengjie.service.dto.StockAttrNoticeQueryCriteria;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author wangm
* @date 2021-04-15
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "stockAttrNotice管理")
@RequestMapping("/api/stockAttrNotice")
public class StockAttrNoticeController {

    private final StockAttrNoticeService stockAttrNoticeService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('stockAttrNotice:list')")
    public void download(HttpServletResponse response, StockAttrNoticeQueryCriteria criteria) throws IOException {
        stockAttrNoticeService.download(stockAttrNoticeService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询stockAttrNotice")
    @ApiOperation("查询stockAttrNotice")
    @PreAuthorize("@el.check('stockAttrNotice:list')")
    public ResponseEntity<Object> query(StockAttrNoticeQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(stockAttrNoticeService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增stockAttrNotice")
    @ApiOperation("新增stockAttrNotice")
    @PreAuthorize("@el.check('stockAttrNotice:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody StockAttrNotice resources){
        return new ResponseEntity<>(stockAttrNoticeService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改stockAttrNotice")
    @ApiOperation("修改stockAttrNotice")
    @PreAuthorize("@el.check('stockAttrNotice:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody StockAttrNotice resources){
        stockAttrNoticeService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除stockAttrNotice")
    @ApiOperation("删除stockAttrNotice")
    @PreAuthorize("@el.check('stockAttrNotice:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        stockAttrNoticeService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("stockAttrNotice通知任务完成")
    @ApiOperation("stockAttrNotice通知任务完成")
    @PreAuthorize("@el.check('stockAttrNotice:complete')")
    @PostMapping("complete")
    public ResponseEntity<Object> complete(@RequestParam Long id) {
        stockAttrNoticeService.complete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("stockAttrNotice推送库存批次属性通知")
    @ApiOperation("stockAttrNotice推送库存批次属性通知")
    @PreAuthorize("@el.check('stockAttrNotice:edit')")
    @PostMapping("push")
    public ResponseEntity<Object> push(@RequestParam Long id) {
        Map<String,Object> resMap=stockAttrNoticeService.push(id);
        return new ResponseEntity<>(resMap,HttpStatus.OK);
    }
}
