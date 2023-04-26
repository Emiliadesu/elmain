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
import me.zhengjie.domain.HezhuLog;
import me.zhengjie.domain.TransInfo;
import me.zhengjie.domain.TransLog;
import me.zhengjie.service.TransInfoService;
import me.zhengjie.service.dto.TransInfoQueryCriteria;
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
* @date 2021-09-04
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "transInfo管理")
@RequestMapping("/api/transInfo")
public class TransInfoController {

    private final TransInfoService transInfoService;

    @GetMapping(value = "/query-by-id")
    @Log("查询transInfo")
    @ApiOperation("查询transInfo")
    @PreAuthorize("@el.check('transInfo:list')")
    public ResponseEntity<Object> queryById(Long id){
        return new ResponseEntity<>(transInfoService.queryById(id),HttpStatus.OK);
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('transInfo:list')")
    public void download(HttpServletResponse response, TransInfoQueryCriteria criteria) throws IOException {
        transInfoService.download(transInfoService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询transInfo")
    @ApiOperation("查询transInfo")
    @PreAuthorize("@el.check('transInfo:list')")
    public ResponseEntity<Object> query(TransInfoQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(transInfoService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增transInfo")
    @ApiOperation("新增transInfo")
    @PreAuthorize("@el.check('transInfo:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody TransInfo resources){
        return new ResponseEntity<>(transInfoService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改transInfo")
    @ApiOperation("修改transInfo")
    @PreAuthorize("@el.check('transInfo:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody TransInfo resources){
        transInfoService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除transInfo")
    @ApiOperation("删除transInfo")
    @PreAuthorize("@el.check('transInfo:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        transInfoService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "update-opt-node")
    @Log("transInfo节点上传")
    @PreAuthorize("@el.check('transInfo:list')")
    public ResponseEntity<Object> updateOptNode(@RequestBody TransLog log){
        transInfoService.updateOptNode(log);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}