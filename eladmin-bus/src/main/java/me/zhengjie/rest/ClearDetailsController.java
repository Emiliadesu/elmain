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
import me.zhengjie.domain.ClearDetails;
import me.zhengjie.service.ClearDetailsService;
import me.zhengjie.service.dto.ClearDetailsQueryCriteria;
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
* @date 2021-08-10
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "clearDetails管理")
@RequestMapping("/api/ClearDetails")
public class ClearDetailsController {

    private final ClearDetailsService ClearDetailsService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('ClearDetails:list')")
    public void download(HttpServletResponse response, ClearDetailsQueryCriteria criteria) throws IOException {
        ClearDetailsService.download(ClearDetailsService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询clearDetails")
    @ApiOperation("查询clearDetails")
    @PreAuthorize("@el.check('ClearDetails:list')")
    public ResponseEntity<Object> query(ClearDetailsQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(ClearDetailsService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增clearDetails")
    @ApiOperation("新增clearDetails")
    @PreAuthorize("@el.check('ClearDetails:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody ClearDetails resources){
        return new ResponseEntity<>(ClearDetailsService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改clearDetails")
    @ApiOperation("修改clearDetails")
    @PreAuthorize("@el.check('ClearDetails:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody ClearDetails resources){
        ClearDetailsService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除clearDetails")
    @ApiOperation("删除clearDetails")
    @PreAuthorize("@el.check('ClearDetails:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        ClearDetailsService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}