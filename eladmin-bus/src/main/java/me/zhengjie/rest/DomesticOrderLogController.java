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
import me.zhengjie.domain.DomesticOrderLog;
import me.zhengjie.service.DomesticOrderLogService;
import me.zhengjie.service.dto.DomesticOrderLogQueryCriteria;
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
* @date 2022-04-11
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "domesticOrderLog管理")
@RequestMapping("/api/DomesticOrderLog")
public class DomesticOrderLogController {

    private final DomesticOrderLogService DomesticOrderLogService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('DomesticOrderLog:list')")
    public void download(HttpServletResponse response, DomesticOrderLogQueryCriteria criteria) throws IOException {
        DomesticOrderLogService.download(DomesticOrderLogService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询domesticOrderLog")
    @ApiOperation("查询domesticOrderLog")
    @PreAuthorize("@el.check('DomesticOrderLog:list')")
    public ResponseEntity<Object> query(DomesticOrderLogQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(DomesticOrderLogService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增domesticOrderLog")
    @ApiOperation("新增domesticOrderLog")
    @PreAuthorize("@el.check('DomesticOrderLog:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody DomesticOrderLog resources){
        return new ResponseEntity<>(DomesticOrderLogService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改domesticOrderLog")
    @ApiOperation("修改domesticOrderLog")
    @PreAuthorize("@el.check('DomesticOrderLog:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody DomesticOrderLog resources){
        DomesticOrderLogService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除domesticOrderLog")
    @ApiOperation("删除domesticOrderLog")
    @PreAuthorize("@el.check('DomesticOrderLog:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        DomesticOrderLogService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}