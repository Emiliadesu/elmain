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
import me.zhengjie.domain.GiftInfo;
import me.zhengjie.service.GiftInfoService;
import me.zhengjie.service.dto.GiftInfoQueryCriteria;
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
* @date 2021-12-27
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "赠品信息绑定管理")
@RequestMapping("/api/giftInfo")
public class GiftInfoController {

    private final GiftInfoService giftInfoService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('giftInfo:list')")
    public void download(HttpServletResponse response, GiftInfoQueryCriteria criteria) throws IOException {
        giftInfoService.download(giftInfoService.queryAll(criteria), response);
    }

    @GetMapping(value = "/query-by-id")
    @Log("查询giftInfo")
    @ApiOperation("查询giftInfo")
    @PreAuthorize("@el.check('giftInfo:list')")
    public ResponseEntity<Object> queryById(Long id){
        return new ResponseEntity<>(giftInfoService.queryByIdWithDetails(id),HttpStatus.OK);
    }

    @GetMapping
    @Log("查询赠品信息绑定")
    @ApiOperation("查询赠品信息绑定")
    @PreAuthorize("@el.check('giftInfo:list')")
    public ResponseEntity<Object> query(GiftInfoQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(giftInfoService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增赠品信息绑定")
    @ApiOperation("新增赠品信息绑定")
    @PreAuthorize("@el.check('giftInfo:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody GiftInfo resources){
        return new ResponseEntity<>(giftInfoService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改赠品信息绑定")
    @ApiOperation("修改赠品信息绑定")
    @PreAuthorize("@el.check('giftInfo:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody GiftInfo resources){
        giftInfoService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除赠品信息绑定")
    @ApiOperation("删除赠品信息绑定")
    @PreAuthorize("@el.check('giftInfo:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        giftInfoService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}