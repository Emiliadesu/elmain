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
import me.zhengjie.domain.LoadDetail;
import me.zhengjie.service.LoadDetailService;
import me.zhengjie.service.dto.LoadDetailQueryCriteria;
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
* @date 2021-04-01
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "预装载单详情表管理")
@RequestMapping("/api/loadDetail")
public class LoadDetailController {

    private final LoadDetailService loadDetailService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('loadDetail:list')")
    public void download(HttpServletResponse response, LoadDetailQueryCriteria criteria) throws IOException {
        loadDetailService.download(loadDetailService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询预装载单详情表")
    @ApiOperation("查询预装载单详情表")
    @PreAuthorize("@el.check('loadDetail:list')")
    public ResponseEntity<Object> query(LoadDetailQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(loadDetailService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增预装载单详情表")
    @ApiOperation("新增预装载单详情表")
    @PreAuthorize("@el.check('loadDetail:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody LoadDetail resources){
        return new ResponseEntity<>(loadDetailService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改预装载单详情表")
    @ApiOperation("修改预装载单详情表")
    @PreAuthorize("@el.check('loadDetail:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody LoadDetail resources){
        loadDetailService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除预装载单详情表")
    @ApiOperation("删除预装载单详情表")
    @PreAuthorize("@el.check('loadDetail:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        loadDetailService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("导入预装载单托盘")
    @ApiOperation("导入出库托盘明细")
    @PreAuthorize("@el.check('loadDetail:uploadLpn')")
    @PostMapping("/uploadLpn")
    public ResponseEntity<Object> uploadLpn(@RequestParam("file") MultipartFile file,
                                             @RequestParam("loadHeadId") Long loadHeadId,
                                             @RequestParam("doNo") String doNo) {
        List<Map<String, Object>> maps = FileUtils.importMapExcel(file);
        loadDetailService.uploadLpn(maps,loadHeadId,doNo);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
