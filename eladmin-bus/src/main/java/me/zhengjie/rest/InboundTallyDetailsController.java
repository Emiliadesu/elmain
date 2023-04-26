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
import me.zhengjie.domain.InboundTallyDetails;
import me.zhengjie.service.InboundTallyDetailsService;
import me.zhengjie.service.dto.InboundTallyDetailsQueryCriteria;
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
@Api(tags = "inboundTallyDetails管理")
@RequestMapping("/api/inboundTallyDetails")
public class InboundTallyDetailsController {

    private final InboundTallyDetailsService inboundTallyDetailsService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('inboundTallyDetails:list')")
    public void download(HttpServletResponse response, InboundTallyDetailsQueryCriteria criteria) throws IOException {
        inboundTallyDetailsService.download(inboundTallyDetailsService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询inboundTallyDetails")
    @ApiOperation("查询inboundTallyDetails")
    @PreAuthorize("@el.check('inboundTallyDetails:list')")
    public ResponseEntity<Object> query(InboundTallyDetailsQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(inboundTallyDetailsService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增inboundTallyDetails")
    @ApiOperation("新增inboundTallyDetails")
    @PreAuthorize("@el.check('inboundTallyDetails:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody InboundTallyDetails resources){
        return new ResponseEntity<>(inboundTallyDetailsService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改inboundTallyDetails")
    @ApiOperation("修改inboundTallyDetails")
    @PreAuthorize("@el.check('inboundTallyDetails:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody InboundTallyDetails resources){
        inboundTallyDetailsService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除inboundTallyDetails")
    @ApiOperation("删除inboundTallyDetails")
    @PreAuthorize("@el.check('inboundTallyDetails:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        inboundTallyDetailsService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}