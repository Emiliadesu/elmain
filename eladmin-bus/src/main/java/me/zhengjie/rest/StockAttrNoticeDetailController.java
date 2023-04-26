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
import me.zhengjie.domain.StockAttrNoticeDetail;
import me.zhengjie.service.StockAttrNoticeDetailService;
import me.zhengjie.service.dto.StockAttrNoticeDetailQueryCriteria;
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
* @date 2021-04-15
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "stockAttrNoticeDetail管理")
@RequestMapping("/api/stockAttrNoticeDetail")
public class StockAttrNoticeDetailController {

    private final StockAttrNoticeDetailService stockAttrNoticeDetailService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('stockAttrNoticeDetail:list')")
    public void download(HttpServletResponse response, StockAttrNoticeDetailQueryCriteria criteria) throws IOException {
        stockAttrNoticeDetailService.download(stockAttrNoticeDetailService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询stockAttrNoticeDetail")
    @ApiOperation("查询stockAttrNoticeDetail")
    @PreAuthorize("@el.check('stockAttrNoticeDetail:list')")
    public ResponseEntity<Object> query(StockAttrNoticeDetailQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(stockAttrNoticeDetailService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增stockAttrNoticeDetail")
    @ApiOperation("新增stockAttrNoticeDetail")
    @PreAuthorize("@el.check('stockAttrNoticeDetail:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody StockAttrNoticeDetail resources){
        return new ResponseEntity<>(stockAttrNoticeDetailService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改stockAttrNoticeDetail")
    @ApiOperation("修改stockAttrNoticeDetail")
    @PreAuthorize("@el.check('stockAttrNoticeDetail:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody StockAttrNoticeDetail resources){
        stockAttrNoticeDetailService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除stockAttrNoticeDetail")
    @ApiOperation("删除stockAttrNoticeDetail")
    @PreAuthorize("@el.check('stockAttrNoticeDetail:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        stockAttrNoticeDetailService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/get-all-singular")
    @Log("得到所有单数且小于2的seq交易序列号")
    @ApiOperation("得到所有单数且小于2的seq交易序列号")
    @PreAuthorize("@el.check('stockAttrNoticeDetail:list')")
    public ResponseEntity<Object> getAllSingularSeq(@Validated Long stockAttrId){
        return new ResponseEntity<>(stockAttrNoticeDetailService.getAllSingularSeq(stockAttrId),HttpStatus.CREATED);
    }

    @PostMapping("add-detail-by-preProject")
    @Log("新增库存属性通知明细")
    @ApiOperation("新增库存属性通知明细")
    @PreAuthorize("@el.check('stockAttrNoticeDetail:add')")
    public ResponseEntity<Object> addDetailByPreProject(@Validated @RequestBody StockAttrNoticeDetail resources){
        stockAttrNoticeDetailService.addDetailByPreProject(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
