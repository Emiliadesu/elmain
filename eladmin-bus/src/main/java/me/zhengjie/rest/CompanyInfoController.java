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
import me.zhengjie.domain.CompanyInfo;
import me.zhengjie.service.CompanyInfoService;
import me.zhengjie.service.dto.CompanyInfoQueryCriteria;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author luob
* @date 2020-10-09
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "商家信息管理")
@RequestMapping("/api/CompanyInfo")
public class CompanyInfoController {

    private final CompanyInfoService CompanyInfoService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('CompanyInfo:list')")
    public void download(HttpServletResponse response, CompanyInfoQueryCriteria criteria) throws IOException {
        CompanyInfoService.download(CompanyInfoService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询商家信息")
    @ApiOperation("查询商家信息")
    @PreAuthorize("@el.check('CompanyInfo:list')")
    public ResponseEntity<Object> query(CompanyInfoQueryCriteria criteria, Pageable pageable){
        Map<String, Object> map = CompanyInfoService.queryAll(criteria, pageable);
        return new ResponseEntity<>(map,HttpStatus.OK);
    }

    @PostMapping
    @Log("新增商家信息")
    @ApiOperation("新增商家信息")
    @PreAuthorize("@el.check('CompanyInfo:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody CompanyInfo resources){
        return new ResponseEntity<>(CompanyInfoService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改商家信息")
    @ApiOperation("修改商家信息")
    @PreAuthorize("@el.check('CompanyInfo:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody CompanyInfo resources){
        CompanyInfoService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除商家信息")
    @ApiOperation("删除商家信息")
    @PreAuthorize("@el.check('CompanyInfo:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        CompanyInfoService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}