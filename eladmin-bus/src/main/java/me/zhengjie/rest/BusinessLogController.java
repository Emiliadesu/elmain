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
import me.zhengjie.domain.BusinessLog;
import me.zhengjie.service.BusinessLogService;
import me.zhengjie.service.dto.BusinessLogQueryCriteria;
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
* @date 2022-01-11
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "businessLog管理")
@RequestMapping("/api/businessLog")
public class BusinessLogController {

    private final BusinessLogService businessLogService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('businessLog:list')")
    public void download(HttpServletResponse response, BusinessLogQueryCriteria criteria) throws IOException {
        businessLogService.download(businessLogService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询businessLog")
    @ApiOperation("查询businessLog")
    @PreAuthorize("@el.check('businessLog:list')")
    public ResponseEntity<Object> query(BusinessLogQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(businessLogService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增businessLog")
    @ApiOperation("新增businessLog")
    @PreAuthorize("@el.check('businessLog:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody BusinessLog resources){
        return new ResponseEntity<>(businessLogService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改businessLog")
    @ApiOperation("修改businessLog")
    @PreAuthorize("@el.check('businessLog:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody BusinessLog resources){
        businessLogService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除businessLog")
    @ApiOperation("删除businessLog")
    @PreAuthorize("@el.check('businessLog:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        businessLogService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}