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
import me.zhengjie.domain.OutboundOrderLog;
import me.zhengjie.service.OutboundOrderLogService;
import me.zhengjie.service.dto.OutboundOrderLogQueryCriteria;
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
* @date 2021-07-13
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "outboundOrderLog管理")
@RequestMapping("/api/outboundOrderLog")
public class OutboundOrderLogController {

    private final OutboundOrderLogService outboundOrderLogService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('outboundOrderLog:list')")
    public void download(HttpServletResponse response, OutboundOrderLogQueryCriteria criteria) throws IOException {
        outboundOrderLogService.download(outboundOrderLogService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询outboundOrderLog")
    @ApiOperation("查询outboundOrderLog")
    @PreAuthorize("@el.check('outboundOrderLog:list')")
    public ResponseEntity<Object> query(OutboundOrderLogQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(outboundOrderLogService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增outboundOrderLog")
    @ApiOperation("新增outboundOrderLog")
    @PreAuthorize("@el.check('outboundOrderLog:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody OutboundOrderLog resources){
        return new ResponseEntity<>(outboundOrderLogService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改outboundOrderLog")
    @ApiOperation("修改outboundOrderLog")
    @PreAuthorize("@el.check('outboundOrderLog:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody OutboundOrderLog resources){
        outboundOrderLogService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除outboundOrderLog")
    @ApiOperation("删除outboundOrderLog")
    @PreAuthorize("@el.check('outboundOrderLog:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        outboundOrderLogService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}