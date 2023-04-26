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
import me.zhengjie.domain.HezhuLog;
import me.zhengjie.service.HezhuLogService;
import me.zhengjie.service.dto.HezhuLogQueryCriteria;
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
@Api(tags = "hezhuLog管理")
@RequestMapping("/api/hezhuLog")
public class HezhuLogController {

    private final HezhuLogService hezhuLogService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('hezhuInfo:list')")
    public void download(HttpServletResponse response, HezhuLogQueryCriteria criteria) throws IOException {
        hezhuLogService.download(hezhuLogService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询hezhuLog")
    @ApiOperation("查询hezhuLog")
    @PreAuthorize("@el.check('hezhuInfo:list')")
    public ResponseEntity<Object> query(HezhuLogQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(hezhuLogService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增hezhuLog")
    @ApiOperation("新增hezhuLog")
    @PreAuthorize("@el.check('hezhuLog:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody HezhuLog resources){
        return new ResponseEntity<>(hezhuLogService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改hezhuLog")
    @ApiOperation("修改hezhuLog")
    @PreAuthorize("@el.check('hezhuLog:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody HezhuLog resources){
        hezhuLogService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除hezhuLog")
    @ApiOperation("删除hezhuLog")
    @PreAuthorize("@el.check('hezhuLog:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        hezhuLogService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}