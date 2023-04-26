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
import me.zhengjie.domain.ClearCompanyInfo;
import me.zhengjie.service.ClearCompanyInfoService;
import me.zhengjie.service.dto.ClearCompanyInfoQueryCriteria;
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
* @date 2020-10-09
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "清关抬头信息管理")
@RequestMapping("/api/ClearCompanyInfo")
public class ClearCompanyInfoController {

    private final ClearCompanyInfoService ClearCompanyInfoService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('ClearCompanyInfo:list')")
    public void download(HttpServletResponse response, ClearCompanyInfoQueryCriteria criteria) throws IOException {
        ClearCompanyInfoService.download(ClearCompanyInfoService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询清关抬头信息")
    @ApiOperation("查询清关抬头信息")
    @PreAuthorize("@el.check('ClearCompanyInfo:list')")
    public ResponseEntity<Object> query(ClearCompanyInfoQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(ClearCompanyInfoService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @GetMapping("/query-all-clear-comp")
    @Log("查询清关抬头信息")
    @ApiOperation("查询清关抬头信息")
    public ResponseEntity<Object> queryAllClearComp(ClearCompanyInfoQueryCriteria criteria){
        return new ResponseEntity<>(ClearCompanyInfoService.queryAllClearComp(criteria),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增清关抬头信息")
    @ApiOperation("新增清关抬头信息")
    @PreAuthorize("@el.check('ClearCompanyInfo:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody ClearCompanyInfo resources){
        return new ResponseEntity<>(ClearCompanyInfoService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改清关抬头信息")
    @ApiOperation("修改清关抬头信息")
    @PreAuthorize("@el.check('ClearCompanyInfo:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody ClearCompanyInfo resources){
        ClearCompanyInfoService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除清关抬头信息")
    @ApiOperation("删除清关抬头信息")
    @PreAuthorize("@el.check('ClearCompanyInfo:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        ClearCompanyInfoService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
