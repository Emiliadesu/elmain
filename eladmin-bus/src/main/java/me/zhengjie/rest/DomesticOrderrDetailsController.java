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
import me.zhengjie.domain.DomesticOrderDetails;
import me.zhengjie.service.DomesticOrderrDetailsService;
import me.zhengjie.service.dto.DomesticOrderrDetailsQueryCriteria;
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
* @date 2022-04-11
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "domesticOrderDetails管理")
@RequestMapping("/api/domesticOrderrDetails")
public class DomesticOrderrDetailsController {

    private final DomesticOrderrDetailsService domesticOrderrDetailsService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('domesticOrderrDetails:list')")
    public void download(HttpServletResponse response, DomesticOrderrDetailsQueryCriteria criteria) throws IOException {
        domesticOrderrDetailsService.download(domesticOrderrDetailsService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询domesticOrderDetails")
    @ApiOperation("查询domesticOrderDetails")
    @PreAuthorize("@el.check('domesticOrderrDetails:list')")
    public ResponseEntity<Object> query(DomesticOrderrDetailsQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(domesticOrderrDetailsService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增domesticOrderDetails")
    @ApiOperation("新增domesticOrderDetails")
    @PreAuthorize("@el.check('domesticOrderrDetails:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody DomesticOrderDetails resources){
        return new ResponseEntity<>(domesticOrderrDetailsService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改domesticOrderDetails")
    @ApiOperation("修改domesticOrderDetails")
    @PreAuthorize("@el.check('domesticOrderrDetails:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody DomesticOrderDetails resources){
        domesticOrderrDetailsService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除domesticOrderDetails")
    @ApiOperation("删除domesticOrderDetails")
    @PreAuthorize("@el.check('domesticOrderrDetails:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        domesticOrderrDetailsService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}