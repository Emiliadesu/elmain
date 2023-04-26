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
import me.zhengjie.domain.GiftLog;
import me.zhengjie.service.GiftLogService;
import me.zhengjie.service.dto.GiftLogQueryCriteria;
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
* @author leningzhou
* @date 2022-01-17
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "赠品日志管理")
@RequestMapping("/api/giftLog")
public class GiftLogController {

    private final GiftLogService giftLogService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('giftLog:list')")
    public void download(HttpServletResponse response, GiftLogQueryCriteria criteria) throws IOException {
        giftLogService.download(giftLogService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询赠品日志")
    @ApiOperation("查询赠品日志")
    @PreAuthorize("@el.check('giftLog:list')")
    public ResponseEntity<Object> query(GiftLogQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(giftLogService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增赠品日志")
    @ApiOperation("新增赠品日志")
    @PreAuthorize("@el.check('giftLog:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody GiftLog resources){
        return new ResponseEntity<>(giftLogService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改赠品日志")
    @ApiOperation("修改赠品日志")
    @PreAuthorize("@el.check('giftLog:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody GiftLog resources){
        giftLogService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除赠品日志")
    @ApiOperation("删除赠品日志")
    @PreAuthorize("@el.check('giftLog:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        giftLogService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}