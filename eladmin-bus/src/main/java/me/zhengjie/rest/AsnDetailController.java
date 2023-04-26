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
import me.zhengjie.domain.AsnDetail;
import me.zhengjie.service.AsnDetailService;
import me.zhengjie.service.dto.AsnDetailQueryCriteria;
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
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author wangm
* @date 2021-03-28
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "出库托盘明细管理")
@RequestMapping("/api/asnDetail")
public class AsnDetailController {

    private final AsnDetailService asnDetailService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('asnDetail:list')")
    public void download(HttpServletResponse response, AsnDetailQueryCriteria criteria) throws IOException {
        asnDetailService.download(asnDetailService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询出库托盘明细")
    @ApiOperation("查询出库托盘明细")
    @PreAuthorize("@el.check('asnDetail:list')")
    public ResponseEntity<Object> query(AsnDetailQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(asnDetailService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增出库托盘明细")
    @ApiOperation("新增出库托盘明细")
    @PreAuthorize("@el.check('asnDetail:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody AsnDetail resources){
        return new ResponseEntity<>(asnDetailService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改出库托盘明细")
    @ApiOperation("修改出库托盘明细")
    @PreAuthorize("@el.check('asnDetail:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody AsnDetail resources){
        asnDetailService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除出库托盘明细")
    @ApiOperation("删除出库托盘明细")
    @PreAuthorize("@el.check('asnDetail:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        asnDetailService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("导入出库托盘明细")
    @ApiOperation("导入出库托盘明细")
    @PreAuthorize("@el.check('asnDetail:uploadAsnD')")
    @PostMapping("/uploadAsnD")
    public ResponseEntity<Object> uploadTrsD(@RequestParam("file") MultipartFile file, @RequestParam("outId") Long outId) {
        List<Map<String, Object>> maps = FileUtils.importMapExcel(file);
        asnDetailService.uploadAsnD(maps,outId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
