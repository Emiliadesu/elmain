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
import me.zhengjie.domain.InboundOrderLog;
import me.zhengjie.service.InboundOrderLogService;
import me.zhengjie.service.dto.InboundOrderLogQueryCriteria;
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
* @date 2021-06-09
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "inboundOrderLog管理")
@RequestMapping("/api/inboundOrderLog")
public class InboundOrderLogController {

    private final InboundOrderLogService inboundOrderLogService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('inboundOrderLog:list')")
    public void download(HttpServletResponse response, InboundOrderLogQueryCriteria criteria) throws IOException {
        inboundOrderLogService.download(inboundOrderLogService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询inboundOrderLog")
    @ApiOperation("查询inboundOrderLog")
    @PreAuthorize("@el.check('inboundOrderLog:list')")
    public ResponseEntity<Object> query(InboundOrderLogQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(inboundOrderLogService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增inboundOrderLog")
    @ApiOperation("新增inboundOrderLog")
    @PreAuthorize("@el.check('inboundOrderLog:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody InboundOrderLog resources){
        return new ResponseEntity<>(inboundOrderLogService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改inboundOrderLog")
    @ApiOperation("修改inboundOrderLog")
    @PreAuthorize("@el.check('inboundOrderLog:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody InboundOrderLog resources){
        inboundOrderLogService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除inboundOrderLog")
    @ApiOperation("删除inboundOrderLog")
    @PreAuthorize("@el.check('inboundOrderLog:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        inboundOrderLogService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}