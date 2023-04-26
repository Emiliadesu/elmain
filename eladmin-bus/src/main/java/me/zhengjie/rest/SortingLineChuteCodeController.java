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
import me.zhengjie.domain.SortingLineChuteCode;
import me.zhengjie.service.SortingLineChuteCodeService;
import me.zhengjie.service.dto.SortingLineChuteCodeQueryCriteria;
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
@Api(tags = "sortingLineChuteCode管理")
@RequestMapping("/api/sortingLineChuteCode")
public class SortingLineChuteCodeController {

    private final SortingLineChuteCodeService sortingLineChuteCodeService;

    @GetMapping(value = "query-by-line-id")
    @Log("查询sortingLineChuteCode")
    @ApiOperation("查询sortingLineChuteCode")
    @PreAuthorize("@el.check('sortingLineChuteCode:list')")
    public ResponseEntity<Object> queryByLineId(Long lineId){
        return new ResponseEntity<>(sortingLineChuteCodeService.queryByLineId(lineId),HttpStatus.OK);
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('sortingLineChuteCode:list')")
    public void download(HttpServletResponse response, SortingLineChuteCodeQueryCriteria criteria) throws IOException {
        sortingLineChuteCodeService.download(sortingLineChuteCodeService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询sortingLineChuteCode")
    @ApiOperation("查询sortingLineChuteCode")
    @PreAuthorize("@el.check('sortingLineChuteCode:list')")
    public ResponseEntity<Object> query(SortingLineChuteCodeQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(sortingLineChuteCodeService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增sortingLineChuteCode")
    @ApiOperation("新增sortingLineChuteCode")
    @PreAuthorize("@el.check('sortingLineChuteCode:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody SortingLineChuteCode resources){
        return new ResponseEntity<>(sortingLineChuteCodeService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改sortingLineChuteCode")
    @ApiOperation("修改sortingLineChuteCode")
    @PreAuthorize("@el.check('sortingLineChuteCode:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody SortingLineChuteCode resources){
        sortingLineChuteCodeService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除sortingLineChuteCode")
    @ApiOperation("删除sortingLineChuteCode")
    @PreAuthorize("@el.check('sortingLineChuteCode:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        sortingLineChuteCodeService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}