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
import me.zhengjie.domain.DyStockTransform;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.DyStockTransformService;
import me.zhengjie.service.dto.DyStockTransformQueryCriteria;
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
* @author 王淼
* @date 2021-09-26
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "抖音库存类型变动回告管理")
@RequestMapping("/api/dyStockTransform")
public class DyStockTransformController {

    private final DyStockTransformService dyStockTransformService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('dyStockTransform:list')")
    public void download(HttpServletResponse response, DyStockTransformQueryCriteria criteria) throws IOException {
        dyStockTransformService.download(dyStockTransformService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询抖音库存类型变动回告")
    @ApiOperation("查询抖音库存类型变动回告")
    @PreAuthorize("@el.check('dyStockTransform:list')")
    public ResponseEntity<Object> query(DyStockTransformQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(dyStockTransformService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增抖音库存类型变动回告")
    @ApiOperation("新增抖音库存类型变动回告")
    @PreAuthorize("@el.check('dyStockTransform:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody DyStockTransform resources){
        return new ResponseEntity<>(dyStockTransformService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改抖音库存类型变动回告")
    @ApiOperation("修改抖音库存类型变动回告")
    @PreAuthorize("@el.check('dyStockTransform:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody DyStockTransform resources){
        dyStockTransformService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除抖音库存类型变动回告")
    @ApiOperation("删除抖音库存类型变动回告")
    @PreAuthorize("@el.check('dyStockTransform:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        dyStockTransformService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("推送库存变动通知")
    @ApiOperation("推送库存变动通知")
    @PreAuthorize("@el.check('dyStockTransform:edit')")
    @PostMapping("push")
    public ResponseEntity<Object> push() {
        dyStockTransformService.push();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("推送库存变动通知-审核")
    @ApiOperation("推送库存变动通知-审核")
    @PreAuthorize("@el.check('dyStockTransform:edit')")
    @GetMapping("pushProcess")
    public ResponseEntity<Object> pushProcess(Long id) {
        try {
            dyStockTransformService.pushProcess(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("推送库存变动通知-完成")
    @ApiOperation("推送库存变动通知-完成")
    @PreAuthorize("@el.check('dyStockTransform:edit')")
    @GetMapping("pushSuccess")
    public ResponseEntity<Object> pushSuccess(Long id) {
        try {
            dyStockTransformService.pushSuccess(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("推送库存变动通知-取消")
    @ApiOperation("推送库存变动通知-取消")
    @PreAuthorize("@el.check('dyStockTransform:edit')")
    @GetMapping("pushCancel")
    public ResponseEntity<Object> pushCancel(Long id) {
        try {
            dyStockTransformService.pushCancel(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
