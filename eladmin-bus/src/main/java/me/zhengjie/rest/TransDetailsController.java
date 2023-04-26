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
import me.zhengjie.domain.TransDetails;
import me.zhengjie.service.TransDetailsService;
import me.zhengjie.service.dto.TransDetailsQueryCriteria;
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
@Api(tags = "transDetails管理")
@RequestMapping("/api/transDetails")
public class TransDetailsController {

    private final TransDetailsService transDetailsService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('transDetails:list')")
    public void download(HttpServletResponse response, TransDetailsQueryCriteria criteria) throws IOException {
        transDetailsService.download(transDetailsService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询transDetails")
    @ApiOperation("查询transDetails")
    @PreAuthorize("@el.check('transDetails:list')")
    public ResponseEntity<Object> query(TransDetailsQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(transDetailsService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增transDetails")
    @ApiOperation("新增transDetails")
    @PreAuthorize("@el.check('transDetails:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody TransDetails resources){
        return new ResponseEntity<>(transDetailsService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改transDetails")
    @ApiOperation("修改transDetails")
    @PreAuthorize("@el.check('transDetails:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody TransDetails resources){
        transDetailsService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除transDetails")
    @ApiOperation("删除transDetails")
    @PreAuthorize("@el.check('transDetails:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        transDetailsService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}