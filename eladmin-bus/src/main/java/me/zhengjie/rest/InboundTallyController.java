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
import me.zhengjie.domain.InboundTally;
import me.zhengjie.service.InboundTallyService;
import me.zhengjie.service.dto.InboundTallyQueryCriteria;
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
* @date 2021-06-16
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "inboundTally管理")
@RequestMapping("/api/inboundTally")
public class InboundTallyController {

    private final InboundTallyService inboundTallyService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('inboundTally:list')")
    public void download(HttpServletResponse response, InboundTallyQueryCriteria criteria) throws IOException {
        inboundTallyService.download(inboundTallyService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询inboundTally")
    @ApiOperation("查询inboundTally")
    @PreAuthorize("@el.check('inboundTally:list')")
    public ResponseEntity<Object> query(InboundTallyQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(inboundTallyService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增inboundTally")
    @ApiOperation("新增inboundTally")
    @PreAuthorize("@el.check('inboundTally:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody InboundTally resources){
        return new ResponseEntity<>(inboundTallyService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改inboundTally")
    @ApiOperation("修改inboundTally")
    @PreAuthorize("@el.check('inboundTally:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody InboundTally resources){
        inboundTallyService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除inboundTally")
    @ApiOperation("删除inboundTally")
    @PreAuthorize("@el.check('inboundTally:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        inboundTallyService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}