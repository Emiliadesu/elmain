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
import me.zhengjie.domain.ClearOptLog;
import me.zhengjie.service.ClearOptLogService;
import me.zhengjie.service.dto.ClearOptLogQueryCriteria;
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
* @date 2021-03-11
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "clearOptLog管理")
@RequestMapping("/api/clearOptLog")
public class ClearOptLogController {

    private final ClearOptLogService clearOptLogService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('clearOptLog:list')")
    public void download(HttpServletResponse response, ClearOptLogQueryCriteria criteria) throws IOException {
        clearOptLogService.download(clearOptLogService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询clearOptLog")
    @ApiOperation("查询clearOptLog")
    @PreAuthorize("@el.check('clearOptLog:list')")
    public ResponseEntity<Object> query(ClearOptLogQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(clearOptLogService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增clearOptLog")
    @ApiOperation("新增clearOptLog")
    @PreAuthorize("@el.check('clearOptLog:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody ClearOptLog resources){
        return new ResponseEntity<>(clearOptLogService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改clearOptLog")
    @ApiOperation("修改clearOptLog")
    @PreAuthorize("@el.check('clearOptLog:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody ClearOptLog resources){
        clearOptLogService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除clearOptLog")
    @ApiOperation("删除clearOptLog")
    @PreAuthorize("@el.check('clearOptLog:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        clearOptLogService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}