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
import me.zhengjie.domain.OutboundOrderDetails;
import me.zhengjie.service.OutboundOrderDetailsService;
import me.zhengjie.service.dto.OutboundOrderDetailsQueryCriteria;
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
* @date 2021-07-13
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "outboundOrderDetails管理")
@RequestMapping("/api/OutboundOrderDetails")
public class OutboundOrderDetailsController {

    private final OutboundOrderDetailsService OutboundOrderDetailsService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('OutboundOrderDetails:list')")
    public void download(HttpServletResponse response, OutboundOrderDetailsQueryCriteria criteria) throws IOException {
        OutboundOrderDetailsService.download(OutboundOrderDetailsService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询outboundOrderDetails")
    @ApiOperation("查询outboundOrderDetails")
    @PreAuthorize("@el.check('OutboundOrderDetails:list')")
    public ResponseEntity<Object> query(OutboundOrderDetailsQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(OutboundOrderDetailsService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增outboundOrderDetails")
    @ApiOperation("新增outboundOrderDetails")
    @PreAuthorize("@el.check('OutboundOrderDetails:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody OutboundOrderDetails resources){
        return new ResponseEntity<>(OutboundOrderDetailsService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改outboundOrderDetails")
    @ApiOperation("修改outboundOrderDetails")
    @PreAuthorize("@el.check('OutboundOrderDetails:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody OutboundOrderDetails resources){
        OutboundOrderDetailsService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除outboundOrderDetails")
    @ApiOperation("删除outboundOrderDetails")
    @PreAuthorize("@el.check('OutboundOrderDetails:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        OutboundOrderDetailsService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}