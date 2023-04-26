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
import me.zhengjie.domain.DetailType;
import me.zhengjie.service.DetailTypeService;
import me.zhengjie.service.dto.DetailTypeQueryCriteria;
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
* @author wangm
* @date 2021-04-15
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "detailType管理")
@RequestMapping("/api/detailType")
public class DetailTypeController {

    private final DetailTypeService detailTypeService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('detailType:list')")
    public void download(HttpServletResponse response, DetailTypeQueryCriteria criteria) throws IOException {
        detailTypeService.download(detailTypeService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询detailType")
    @ApiOperation("查询detailType")
    @PreAuthorize("@el.check('detailType:list')")
    public ResponseEntity<Object> query(DetailTypeQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(detailTypeService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增detailType")
    @ApiOperation("新增detailType")
    @PreAuthorize("@el.check('detailType:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody DetailType resources){
        return new ResponseEntity<>(detailTypeService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改detailType")
    @ApiOperation("修改detailType")
    @PreAuthorize("@el.check('detailType:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody DetailType resources){
        detailTypeService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除detailType")
    @ApiOperation("删除detailType")
    @PreAuthorize("@el.check('detailType:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        detailTypeService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("detailType通知任务完成")
    @ApiOperation("detailType通知任务完成")
    @PreAuthorize("@el.check('detailType:complete')")
    @PostMapping("complete")
    public ResponseEntity<Object> complete(@RequestParam Long id) {
        detailTypeService.complete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
