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
import me.zhengjie.domain.SkuLog;
import me.zhengjie.service.SkuLogService;
import me.zhengjie.service.dto.SkuLogQueryCriteria;
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
* @date 2021-04-21
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "skulog管理")
@RequestMapping("/api/skuLog")
public class SkuLogController {

    private final SkuLogService skuLogService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('skuLog:list')")
    public void download(HttpServletResponse response, SkuLogQueryCriteria criteria) throws IOException {
        skuLogService.download(skuLogService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询skulog")
    @ApiOperation("查询skulog")
    @PreAuthorize("@el.check('skuLog:list')")
    public ResponseEntity<Object> query(SkuLogQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(skuLogService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增skulog")
    @ApiOperation("新增skulog")
    @PreAuthorize("@el.check('skuLog:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody SkuLog resources){
        return new ResponseEntity<>(skuLogService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改skulog")
    @ApiOperation("修改skulog")
    @PreAuthorize("@el.check('skuLog:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody SkuLog resources){
        skuLogService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除skulog")
    @ApiOperation("删除skulog")
    @PreAuthorize("@el.check('skuLog:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        skuLogService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}