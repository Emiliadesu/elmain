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
import me.zhengjie.domain.QueryMftLog;
import me.zhengjie.service.QueryMftLogService;
import me.zhengjie.service.dto.QueryMftLogQueryCriteria;
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
* @date 2021-04-03
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "mftlog管理")
@RequestMapping("/api/QueryMftLog")
public class QueryMftLogController {

    private final QueryMftLogService QueryMftLogService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('QueryMftLog:list')")
    public void download(HttpServletResponse response, QueryMftLogQueryCriteria criteria) throws IOException {
        QueryMftLogService.download(QueryMftLogService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询mftlog")
    @ApiOperation("查询mftlog")
    @PreAuthorize("@el.check('QueryMftLog:list')")
    public ResponseEntity<Object> query(QueryMftLogQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(QueryMftLogService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增mftlog")
    @ApiOperation("新增mftlog")
    @PreAuthorize("@el.check('QueryMftLog:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody QueryMftLog resources){
        return new ResponseEntity<>(QueryMftLogService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改mftlog")
    @ApiOperation("修改mftlog")
    @PreAuthorize("@el.check('QueryMftLog:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody QueryMftLog resources){
        QueryMftLogService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除mftlog")
    @ApiOperation("删除mftlog")
    @PreAuthorize("@el.check('QueryMftLog:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        QueryMftLogService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}