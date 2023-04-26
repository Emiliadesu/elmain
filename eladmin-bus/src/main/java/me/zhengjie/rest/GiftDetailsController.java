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
import me.zhengjie.domain.GiftDetails;
import me.zhengjie.service.GiftDetailsService;
import me.zhengjie.service.dto.GiftDetailsQueryCriteria;
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
@Api(tags = "赠品明细管理")
@RequestMapping("/api/giftDetails")
public class GiftDetailsController {

    private final GiftDetailsService giftDetailsService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('giftDetails:list')")
    public void download(HttpServletResponse response, GiftDetailsQueryCriteria criteria) throws IOException {
        giftDetailsService.download(giftDetailsService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询赠品明细")
    @ApiOperation("查询赠品明细")
    @PreAuthorize("@el.check('giftDetails:list')")
    public ResponseEntity<Object> query(GiftDetailsQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(giftDetailsService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增赠品明细")
    @ApiOperation("新增赠品明细")
    @PreAuthorize("@el.check('giftDetails:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody GiftDetails resources){
        return new ResponseEntity<>(giftDetailsService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改赠品明细")
    @ApiOperation("修改赠品明细")
    @PreAuthorize("@el.check('giftDetails:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody GiftDetails resources){
        giftDetailsService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除赠品明细")
    @ApiOperation("删除赠品明细")
    @PreAuthorize("@el.check('giftDetails:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        giftDetailsService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}