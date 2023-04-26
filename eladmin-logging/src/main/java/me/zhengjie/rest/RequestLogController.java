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
import me.zhengjie.domain.RequestLog;
import me.zhengjie.service.RequestLogService;
import me.zhengjie.service.dto.RequestLogQueryCriteria;
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
* @date 2021-04-28
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "requestlog管理")
@RequestMapping("/api/requestLog")
public class RequestLogController {

    private final RequestLogService requestLogService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('requestLog:list')")
    public void download(HttpServletResponse response, RequestLogQueryCriteria criteria) throws IOException {
        requestLogService.download(requestLogService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询requestlog")
    @ApiOperation("查询requestlog")
    @PreAuthorize("@el.check('requestLog:list')")
    public ResponseEntity<Object> query(RequestLogQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(requestLogService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增requestlog")
    @ApiOperation("新增requestlog")
    @PreAuthorize("@el.check('requestLog:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody RequestLog resources){
        return new ResponseEntity<>(requestLogService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改requestlog")
    @ApiOperation("修改requestlog")
    @PreAuthorize("@el.check('requestLog:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody RequestLog resources){
        requestLogService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除requestlog")
    @ApiOperation("删除requestlog")
    @PreAuthorize("@el.check('requestLog:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        requestLogService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}