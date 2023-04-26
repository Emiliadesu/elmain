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
import me.zhengjie.domain.LoadHeader;
import me.zhengjie.service.LoadHeaderService;
import me.zhengjie.service.dto.LoadHeaderQueryCriteria;
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
* @author wangm
* @date 2021-04-01
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "预装载单表头管理")
@RequestMapping("/api/loadHeader")
public class LoadHeaderController {

    private final LoadHeaderService loadHeaderService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('loadHeader:list')")
    public void download(HttpServletResponse response, LoadHeaderQueryCriteria criteria) throws IOException {
        loadHeaderService.download(loadHeaderService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询预装载单表头")
    @ApiOperation("查询预装载单表头")
    @PreAuthorize("@el.check('loadHeader:list')")
    public ResponseEntity<Object> query(LoadHeaderQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(loadHeaderService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增预装载单表头")
    @ApiOperation("新增预装载单表头")
    @PreAuthorize("@el.check('loadHeader:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody LoadHeader resources){
        return new ResponseEntity<>(loadHeaderService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改预装载单表头")
    @ApiOperation("修改预装载单表头")
    @PreAuthorize("@el.check('loadHeader:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody LoadHeader resources){
        loadHeaderService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除预装载单表头")
    @ApiOperation("删除预装载单表头")
    @PreAuthorize("@el.check('loadHeader:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        loadHeaderService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}