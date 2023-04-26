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
import me.zhengjie.domain.WmsStockLog;
import me.zhengjie.service.WmsStockLogService;
import me.zhengjie.service.dto.WmsStockLogQueryCriteria;
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
* @author 王淼
* @date 2020-12-21
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "wmsStockLog管理")
@RequestMapping("/api/wmsStockLog")
public class WmsStockLogController {

    private final WmsStockLogService wmsStockLogService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('wmsStockLog:list')")
    public void download(HttpServletResponse response, WmsStockLogQueryCriteria criteria) throws IOException {
        wmsStockLogService.download(wmsStockLogService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询wmsStockLog")
    @ApiOperation("查询wmsStockLog")
    @PreAuthorize("@el.check('wmsStockLog:list')")
    public ResponseEntity<Object> query(WmsStockLogQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(wmsStockLogService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增wmsStockLog")
    @ApiOperation("新增wmsStockLog")
    @PreAuthorize("@el.check('wmsStockLog:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody WmsStockLog resources){
        return new ResponseEntity<>(wmsStockLogService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改wmsStockLog")
    @ApiOperation("修改wmsStockLog")
    @PreAuthorize("@el.check('wmsStockLog:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody WmsStockLog resources){
        wmsStockLogService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除wmsStockLog")
    @ApiOperation("删除wmsStockLog")
    @PreAuthorize("@el.check('wmsStockLog:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        wmsStockLogService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}