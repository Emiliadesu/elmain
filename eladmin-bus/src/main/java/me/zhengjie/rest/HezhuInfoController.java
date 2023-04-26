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
import me.zhengjie.domain.ClearOptLog;
import me.zhengjie.domain.HezhuInfo;
import me.zhengjie.domain.HezhuLog;
import me.zhengjie.service.HezhuInfoService;
import me.zhengjie.service.dto.HezhuInfoQueryCriteria;
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
@Api(tags = "hezhuInfo管理")
@RequestMapping("/api/hezhuInfo")
public class HezhuInfoController {

    private final HezhuInfoService hezhuInfoService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('hezhuInfo:list')")
    public void download(HttpServletResponse response, HezhuInfoQueryCriteria criteria) throws IOException {
        hezhuInfoService.download(hezhuInfoService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询hezhuInfo")
    @ApiOperation("查询hezhuInfo")
    @PreAuthorize("@el.check('hezhuInfo:list')")
    public ResponseEntity<Object> query(HezhuInfoQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(hezhuInfoService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增hezhuInfo")
    @ApiOperation("新增hezhuInfo")
    @PreAuthorize("@el.check('hezhuInfo:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody HezhuInfo resources){
        return new ResponseEntity<>(hezhuInfoService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改hezhuInfo")
    @ApiOperation("修改hezhuInfo")
    @PreAuthorize("@el.check('hezhuInfo:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody HezhuInfo resources){
        hezhuInfoService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除hezhuInfo")
    @ApiOperation("删除hezhuInfo")
    @PreAuthorize("@el.check('hezhuInfo:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        hezhuInfoService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "update-opt-node")
    @Log("hezhuInfo节点上传")
    @PreAuthorize("@el.check('hezhuInfo:list')")
    public ResponseEntity<Object> updateOptNode(@RequestBody HezhuLog log){
        hezhuInfoService.updateOptNode(log);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("spawn-in-out-bound-order")
    @Log("根据核注单生成出入库单")
    @ApiOperation("根据核注单生成出入库单")
    @PreAuthorize("@el.check('hezhuInfo:spawn')")
    public ResponseEntity<Object> spawnInOutBoundOrder(@RequestBody Long[] ids){
        hezhuInfoService.spawnInOutBoundOrder(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
