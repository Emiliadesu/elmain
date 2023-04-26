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
import me.zhengjie.domain.DyStockTakingDetail;
import me.zhengjie.service.DyStockTakingDetailService;
import me.zhengjie.service.dto.DyStockTakingDetailQueryCriteria;
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
* @date 2021-09-26
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "抖音库存盘点回告详情管理")
@RequestMapping("/api/dyStockTakingDetail")
public class DyStockTakingDetailController {

    private final DyStockTakingDetailService dyStockTakingDetailService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('dyStockTakingDetail:list')")
    public void download(HttpServletResponse response, DyStockTakingDetailQueryCriteria criteria) throws IOException {
        dyStockTakingDetailService.download(dyStockTakingDetailService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询抖音库存盘点回告详情")
    @ApiOperation("查询抖音库存盘点回告详情")
    @PreAuthorize("@el.check('dyStockTakingDetail:list')")
    public ResponseEntity<Object> query(DyStockTakingDetailQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(dyStockTakingDetailService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增抖音库存盘点回告详情")
    @ApiOperation("新增抖音库存盘点回告详情")
    @PreAuthorize("@el.check('dyStockTakingDetail:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody DyStockTakingDetail resources){
        return new ResponseEntity<>(dyStockTakingDetailService.create(resources),HttpStatus.CREATED);
    }

    @PostMapping("/query-by-talkingId")
    @Log("根据盘点主id获取详情")
    @ApiOperation("根据盘点主id获取详情")
    @PreAuthorize("@el.check('dyStockTakingDetail:list')")
    public ResponseEntity<Object> queryByTalkingId(Long talkingId){
        return new ResponseEntity<>(dyStockTakingDetailService.queryByTalkingIdDto(talkingId),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改抖音库存盘点回告详情")
    @ApiOperation("修改抖音库存盘点回告详情")
    @PreAuthorize("@el.check('dyStockTakingDetail:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody DyStockTakingDetail resources){
        dyStockTakingDetailService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除抖音库存盘点回告详情")
    @ApiOperation("删除抖音库存盘点回告详情")
    @PreAuthorize("@el.check('dyStockTakingDetail:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        dyStockTakingDetailService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
