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
import me.zhengjie.domain.PackCheck;
import me.zhengjie.service.PackCheckService;
import me.zhengjie.service.dto.PackCheckQueryCriteria;
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
* @date 2021-07-22
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "packCheck管理")
@RequestMapping("/api/packCheck")
public class PackCheckController {

    private final PackCheckService packCheckService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('packCheck:list')")
    public void download(HttpServletResponse response, PackCheckQueryCriteria criteria) throws IOException {
        packCheckService.download(packCheckService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询packCheck")
    @ApiOperation("查询packCheck")
    @PreAuthorize("@el.check('packCheck:list')")
    public ResponseEntity<Object> query(PackCheckQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(packCheckService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增packCheck")
    @ApiOperation("新增packCheck")
    @PreAuthorize("@el.check('packCheck:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody PackCheck resources){
        return new ResponseEntity<>(packCheckService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改packCheck")
    @ApiOperation("修改packCheck")
    @PreAuthorize("@el.check('packCheck:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody PackCheck resources){
        packCheckService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除packCheck")
    @ApiOperation("删除packCheck")
    @PreAuthorize("@el.check('packCheck:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        packCheckService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}