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
import me.zhengjie.domain.Platform;
import me.zhengjie.service.PlatformService;
import me.zhengjie.service.dto.PlatformQueryCriteria;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
* @date 2020-10-12
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "Platform管理")
@RequestMapping("/api/platform")
public class PlatformController {

    private final PlatformService platformService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('platform:list')")
    public void download(HttpServletResponse response, PlatformQueryCriteria criteria) throws IOException {
        platformService.download(platformService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询Platform")
    @ApiOperation("查询Platform")
    @PreAuthorize("@el.check('platform:list')")
    public ResponseEntity<Object> query(PlatformQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(platformService.queryAll(criteria,pageable),HttpStatus.OK);
    }
    @GetMapping(value = "/dict")
    @Log("查询Platform")
    @ApiOperation("查询Platform")
    public ResponseEntity<Object> queryDict(PlatformQueryCriteria criteria,
                                            @PageableDefault(sort = {"plafName"}, direction = Sort.Direction.ASC)Pageable pageable){
        return new ResponseEntity<>(platformService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增Platform")
    @ApiOperation("新增Platform")
    @PreAuthorize("@el.check('platform:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Platform resources){
        return new ResponseEntity<>(platformService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改Platform")
    @ApiOperation("修改Platform")
    @PreAuthorize("@el.check('platform:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody Platform resources){
        platformService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除Platform")
    @ApiOperation("删除Platform")
    @PreAuthorize("@el.check('platform:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        platformService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}