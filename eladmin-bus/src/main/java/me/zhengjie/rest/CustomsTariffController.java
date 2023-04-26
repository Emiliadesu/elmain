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
import me.zhengjie.domain.CustomsTariff;
import me.zhengjie.service.CustomsTariffService;
import me.zhengjie.service.dto.CustomsTariffQueryCriteria;
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
* @author 王淼
* @date 2020-10-20
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "customs_tarrif管理")
@RequestMapping("/api/customsTariff")
public class CustomsTariffController {

    private final CustomsTariffService customsTariffService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('customsTariff:list')")
    public void download(HttpServletResponse response, CustomsTariffQueryCriteria criteria) throws IOException {
        customsTariffService.download(customsTariffService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询customs_tarrif")
    @ApiOperation("查询customs_tarrif")
    @PreAuthorize("@el.check('customsTariff:list')")
    public ResponseEntity<Object> query(CustomsTariffQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(customsTariffService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增customs_tarrif")
    @ApiOperation("新增customs_tarrif")
    @PreAuthorize("@el.check('customsTariff:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody CustomsTariff resources){
        return new ResponseEntity<>(customsTariffService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改customs_tarrif")
    @ApiOperation("修改customs_tarrif")
    @PreAuthorize("@el.check('customsTariff:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody CustomsTariff resources){
        customsTariffService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除customs_tarrif")
    @ApiOperation("删除customs_tarrif")
    @PreAuthorize("@el.check('customsTariff:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        customsTariffService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}