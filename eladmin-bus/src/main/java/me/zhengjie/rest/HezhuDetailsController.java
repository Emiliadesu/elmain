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
import me.zhengjie.domain.HezhuDetails;
import me.zhengjie.service.HezhuDetailsService;
import me.zhengjie.service.dto.HezhuDetailsQueryCriteria;
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
* @date 2021-08-26
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "hezhuDetails管理")
@RequestMapping("/api/hezhuDetails")
public class HezhuDetailsController {

    private final HezhuDetailsService hezhuDetailsService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('hezhuDetails:list')")
    public void download(HttpServletResponse response, HezhuDetailsQueryCriteria criteria) throws IOException {
        hezhuDetailsService.download(hezhuDetailsService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询hezhuDetails")
    @ApiOperation("查询hezhuDetails")
    @PreAuthorize("@el.check('hezhuDetails:list')")
    public ResponseEntity<Object> query(HezhuDetailsQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(hezhuDetailsService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增hezhuDetails")
    @ApiOperation("新增hezhuDetails")
    @PreAuthorize("@el.check('hezhuDetails:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody HezhuDetails resources){
        return new ResponseEntity<>(hezhuDetailsService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改hezhuDetails")
    @ApiOperation("修改hezhuDetails")
    @PreAuthorize("@el.check('hezhuDetails:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody HezhuDetails resources){
        hezhuDetailsService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除hezhuDetails")
    @ApiOperation("删除hezhuDetails")
    @PreAuthorize("@el.check('hezhuDetails:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        hezhuDetailsService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}