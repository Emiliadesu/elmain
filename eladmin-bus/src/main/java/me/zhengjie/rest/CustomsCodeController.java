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
import me.zhengjie.domain.CustomsCode;
import me.zhengjie.service.CustomsCodeService;
import me.zhengjie.service.dto.CustomsCodeQueryCriteria;
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
* @date 2021-08-21
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "customsCode管理")
@RequestMapping("/api/customsCode")
public class CustomsCodeController {

    private final CustomsCodeService customsCodeService;

    @GetMapping(value = "/query-customs-code")
    @Log("查询customsCode")
    @ApiOperation("查询customsCode")
    @PreAuthorize("@el.check('customsCode:list')")
    public ResponseEntity<Object> queryCustomsCode(){
        return new ResponseEntity<>(customsCodeService.queryCustomsCode(),HttpStatus.OK);
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('customsCode:list')")
    public void download(HttpServletResponse response, CustomsCodeQueryCriteria criteria) throws IOException {
        customsCodeService.download(customsCodeService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询customsCode")
    @ApiOperation("查询customsCode")
    @PreAuthorize("@el.check('customsCode:list')")
    public ResponseEntity<Object> query(CustomsCodeQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(customsCodeService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增customsCode")
    @ApiOperation("新增customsCode")
    @PreAuthorize("@el.check('customsCode:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody CustomsCode resources){
        return new ResponseEntity<>(customsCodeService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改customsCode")
    @ApiOperation("修改customsCode")
    @PreAuthorize("@el.check('customsCode:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody CustomsCode resources){
        customsCodeService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除customsCode")
    @ApiOperation("删除customsCode")
    @PreAuthorize("@el.check('customsCode:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        customsCodeService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}