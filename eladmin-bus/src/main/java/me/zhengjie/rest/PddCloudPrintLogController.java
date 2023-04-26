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
import me.zhengjie.domain.PddCloudPrintLog;
import me.zhengjie.service.PddCloudPrintLogService;
import me.zhengjie.service.dto.PddCloudPrintLogQueryCriteria;
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
* @author wangmiao
* @date 2022-08-08
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "拼多多云打印日志管理")
@RequestMapping("/api/pddCloudPrintLog")
public class PddCloudPrintLogController {

    private final PddCloudPrintLogService pddCloudPrintLogService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('pddCloudPrintLog:list')")
    public void download(HttpServletResponse response, PddCloudPrintLogQueryCriteria criteria) throws IOException {
        pddCloudPrintLogService.download(pddCloudPrintLogService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询拼多多云打印日志")
    @ApiOperation("查询拼多多云打印日志")
    @PreAuthorize("@el.check('pddCloudPrintLog:list')")
    public ResponseEntity<Object> query(PddCloudPrintLogQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(pddCloudPrintLogService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增拼多多云打印日志")
    @ApiOperation("新增拼多多云打印日志")
    @PreAuthorize("@el.check('pddCloudPrintLog:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody PddCloudPrintLog resources){
        return new ResponseEntity<>(pddCloudPrintLogService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改拼多多云打印日志")
    @ApiOperation("修改拼多多云打印日志")
    @PreAuthorize("@el.check('pddCloudPrintLog:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody PddCloudPrintLog resources){
        pddCloudPrintLogService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除拼多多云打印日志")
    @ApiOperation("删除拼多多云打印日志")
    @PreAuthorize("@el.check('pddCloudPrintLog:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        pddCloudPrintLogService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}