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
import me.zhengjie.domain.Config;
import me.zhengjie.service.ConfigService;
import me.zhengjie.service.dto.ConfigQueryCriteria;
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
* @date 2020-11-24
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "config管理")
@RequestMapping("/api/config")
public class ConfigController {

    private final ConfigService configService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('config:list')")
    public void download(HttpServletResponse response, ConfigQueryCriteria criteria) throws IOException {
        configService.download(configService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询config")
    @ApiOperation("查询config")
    @PreAuthorize("@el.check('config:list')")
    public ResponseEntity<Object> query(ConfigQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(configService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增config")
    @ApiOperation("新增config")
    @PreAuthorize("@el.check('config:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Config resources){
        return new ResponseEntity<>(configService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改config")
    @ApiOperation("修改config")
    @PreAuthorize("@el.check('config:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody Config resources){
        configService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除config")
    @ApiOperation("删除config")
    @PreAuthorize("@el.check('config:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Integer[] ids) {
        configService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}