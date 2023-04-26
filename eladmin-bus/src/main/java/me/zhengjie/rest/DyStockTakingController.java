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
import me.zhengjie.domain.DyStockTaking;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.DyStockTakingService;
import me.zhengjie.service.dto.DyStockTakingQueryCriteria;
import me.zhengjie.utils.FileUtils;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author wangm
* @date 2021-09-26
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "抖音库存盘点回告管理")
@RequestMapping("/api/dyStockTaking")
public class DyStockTakingController {

    private final DyStockTakingService dyStockTakingService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('dyStockTaking:list')")
    public void download(HttpServletResponse response, DyStockTakingQueryCriteria criteria) throws IOException {
        dyStockTakingService.download(dyStockTakingService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询抖音库存盘点回告")
    @ApiOperation("查询抖音库存盘点回告")
    @PreAuthorize("@el.check('dyStockTaking:list')")
    public ResponseEntity<Object> query(DyStockTakingQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(dyStockTakingService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增抖音库存盘点回告")
    @ApiOperation("新增抖音库存盘点回告")
    @PreAuthorize("@el.check('dyStockTaking:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody DyStockTaking resources){
        return new ResponseEntity<>(dyStockTakingService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改抖音库存盘点回告")
    @ApiOperation("修改抖音库存盘点回告")
    @PreAuthorize("@el.check('dyStockTaking:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody DyStockTaking resources){
        dyStockTakingService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除抖音库存盘点回告")
    @ApiOperation("删除抖音库存盘点回告")
    @PreAuthorize("@el.check('dyStockTaking:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        dyStockTakingService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("推送库存盘点报告")
    @ApiOperation("推送库存盘点报告")
    @PreAuthorize("@el.check('dyStockTaking:edit')")
    @PostMapping("push")
    public ResponseEntity<Object> push() {
        dyStockTakingService.push();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("上传SKU")
    @RequestMapping(value = "uploadSku")
    @PreAuthorize("@el.check('dyStockTaking:add')")
    @Log("上传SKU")
    public ResponseEntity<Object> uploadSku(@RequestParam("file") MultipartFile file,Long shopId){
        if (shopId==null)
            throw new BadRequestException("请先选择店铺");
        List<Map<String, Object>> maps = FileUtils.importMapExcel(file);
        List<Map<String, Object>> result = dyStockTakingService.uploadSku(maps,shopId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation("根据wms批次和库位追踪预约到货单号")
    @RequestMapping(value = "track_inbound_orderNo")
    @Log("根据wms批次和库位追踪预约到货单号")
    public ResponseEntity<Object> trackInboundOrderNo(String wmsBatch,String location){
        return new ResponseEntity<>(dyStockTakingService.trackInboundOrderNo(wmsBatch,location), HttpStatus.OK);
    }

    @Log("推送库存盘点报告-待审核")
    @ApiOperation("推送库存盘点报告-待审核")
    @PreAuthorize("@el.check('dyStockTaking:edit')")
    @GetMapping("pushProcess")
    public ResponseEntity<Object> pushProcess(Long id) {
        try {
            dyStockTakingService.pushProcess(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("推送库存盘点报告-完成盘点")
    @ApiOperation("推送库存盘点报告-完成盘点")
    @PreAuthorize("@el.check('dyStockTaking:edit')")
    @GetMapping("pushSuccess")
    public ResponseEntity<Object> pushSuccess(Long id) {
        try {
            dyStockTakingService.pushSuccess(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("推送库存盘点通知-取消")
    @ApiOperation("推送库存盘点通知-取消")
    @PreAuthorize("@el.check('dyStockTaking:edit')")
    @GetMapping("pushCancel")
    public ResponseEntity<Object> pushCancel(Long id) {
        try {
            dyStockTakingService.pushCancel(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
