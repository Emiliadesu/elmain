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
import me.zhengjie.domain.BooksInfo;
import me.zhengjie.service.BooksInfoService;
import me.zhengjie.service.dto.BooksInfoQueryCriteria;
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
* @author BooksInfo
* @date 2021-07-21
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "账册信息管理")
@RequestMapping("/api/booksInfo")
public class BooksInfoController {

    private final BooksInfoService booksInfoService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('booksInfo:list')")
    public void download(HttpServletResponse response, BooksInfoQueryCriteria criteria) throws IOException {
        booksInfoService.download(booksInfoService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询账册信息")
    @ApiOperation("查询账册信息")
    @PreAuthorize("@el.check('booksInfo:list')")
    public ResponseEntity<Object> query(BooksInfoQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(booksInfoService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @GetMapping("/queryBooks")
    @Log("查询账册信息")
    @ApiOperation("查询账册信息")
//    @PreAuthorize("@el.check('booksInfo:list')")
    public ResponseEntity<Object> queryBooks(BooksInfoQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(booksInfoService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增账册信息")
    @ApiOperation("新增账册信息")
    @PreAuthorize("@el.check('booksInfo:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody BooksInfo resources){
        return new ResponseEntity<>(booksInfoService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改账册信息")
    @ApiOperation("修改账册信息")
    @PreAuthorize("@el.check('booksInfo:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody BooksInfo resources){
        booksInfoService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除账册信息")
    @ApiOperation("删除账册信息")
    @PreAuthorize("@el.check('booksInfo:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        booksInfoService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}