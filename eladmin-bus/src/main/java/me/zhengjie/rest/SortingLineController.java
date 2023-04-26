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
import me.zhengjie.domain.SortingLine;
import me.zhengjie.service.SortingLineService;
import me.zhengjie.service.dto.SortingLineQueryCriteria;
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
* @date 2021-10-04
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "sortingLine管理")
@RequestMapping("/api/sortingLine")
public class SortingLineController {

    private final SortingLineService sortingLineService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('sortingLine:list')")
    public void download(HttpServletResponse response, SortingLineQueryCriteria criteria) throws IOException {
        sortingLineService.download(sortingLineService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询sortingLine")
    @ApiOperation("查询sortingLine")
    @PreAuthorize("@el.check('sortingLine:list')")
    public ResponseEntity<Object> query(SortingLineQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(sortingLineService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增sortingLine")
    @ApiOperation("新增sortingLine")
    @PreAuthorize("@el.check('sortingLine:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody SortingLine resources){
        return new ResponseEntity<>(sortingLineService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改sortingLine")
    @ApiOperation("修改sortingLine")
    @PreAuthorize("@el.check('sortingLine:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody SortingLine resources){
        sortingLineService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除sortingLine")
    @ApiOperation("删除sortingLine")
    @PreAuthorize("@el.check('sortingLine:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        sortingLineService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}