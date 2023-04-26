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
import me.zhengjie.domain.ThirdOrderLog;
import me.zhengjie.service.ThirdOrderLogService;
import me.zhengjie.service.dto.ThirdOrderLogQueryCriteria;
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
@Api(tags = "ThirdOrderLog管理")
@RequestMapping("/api/thirdOrderLog")
public class ThirdOrderLogController {

    private final ThirdOrderLogService thirdOrderLogService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('thirdOrderLog:list')")
    public void download(HttpServletResponse response, ThirdOrderLogQueryCriteria criteria) throws IOException {
        thirdOrderLogService.download(thirdOrderLogService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询ThirdOrderLog")
    @ApiOperation("查询ThirdOrderLog")
    @PreAuthorize("@el.check('thirdOrderLog:list')")
    public ResponseEntity<Object> query(ThirdOrderLogQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(thirdOrderLogService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增ThirdOrderLog")
    @ApiOperation("新增ThirdOrderLog")
    @PreAuthorize("@el.check('thirdOrderLog:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody ThirdOrderLog resources){
        return new ResponseEntity<>(thirdOrderLogService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改ThirdOrderLog")
    @ApiOperation("修改ThirdOrderLog")
    @PreAuthorize("@el.check('thirdOrderLog:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody ThirdOrderLog resources){
        thirdOrderLogService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除ThirdOrderLog")
    @ApiOperation("删除ThirdOrderLog")
    @PreAuthorize("@el.check('thirdOrderLog:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        thirdOrderLogService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}