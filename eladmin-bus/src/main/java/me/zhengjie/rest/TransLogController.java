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
import me.zhengjie.domain.TransLog;
import me.zhengjie.service.TransLogService;
import me.zhengjie.service.dto.TransLogQueryCriteria;
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
* @date 2021-09-04
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "transLog管理")
@RequestMapping("/api/transLog")
public class TransLogController {

    private final TransLogService transLogService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('transInfo:list')")
    public void download(HttpServletResponse response, TransLogQueryCriteria criteria) throws IOException {
        transLogService.download(transLogService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询transLog")
    @ApiOperation("查询transLog")
    @PreAuthorize("@el.check('transLog:list')")
    public ResponseEntity<Object> query(TransLogQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(transLogService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增transLog")
    @ApiOperation("新增transLog")
    @PreAuthorize("@el.check('transLog:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody TransLog resources){
        return new ResponseEntity<>(transLogService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改transLog")
    @ApiOperation("修改transLog")
    @PreAuthorize("@el.check('transLog:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody TransLog resources){
        transLogService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除transLog")
    @ApiOperation("删除transLog")
    @PreAuthorize("@el.check('transLog:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        transLogService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}