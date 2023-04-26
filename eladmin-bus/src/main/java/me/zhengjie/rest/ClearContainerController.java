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
import me.zhengjie.domain.ClearContainer;
import me.zhengjie.service.ClearContainerService;
import me.zhengjie.service.dto.ClearContainerQueryCriteria;
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
* @date 2021-03-23
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "clearcontainer管理")
@RequestMapping("/api/clearContainer")
public class ClearContainerController {

    private final ClearContainerService clearContainerService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('clearContainer:list')")
    public void download(HttpServletResponse response, ClearContainerQueryCriteria criteria) throws IOException {
        clearContainerService.download(clearContainerService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询clearcontainer")
    @ApiOperation("查询clearcontainer")
    @PreAuthorize("@el.check('clearContainer:list')")
    public ResponseEntity<Object> query(ClearContainerQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(clearContainerService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增clearcontainer")
    @ApiOperation("新增clearcontainer")
    @PreAuthorize("@el.check('clearContainer:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody ClearContainer resources){
        return new ResponseEntity<>(clearContainerService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改clearcontainer")
    @ApiOperation("修改clearcontainer")
    @PreAuthorize("@el.check('clearContainer:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody ClearContainer resources){
        clearContainerService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除clearcontainer")
    @ApiOperation("删除clearcontainer")
    @PreAuthorize("@el.check('clearContainer:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        clearContainerService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}