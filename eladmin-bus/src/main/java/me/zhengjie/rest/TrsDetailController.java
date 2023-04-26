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
import me.zhengjie.domain.TrsDetail;
import me.zhengjie.service.TrsDetailService;
import me.zhengjie.service.dto.TrsDetailQueryCriteria;
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
* @date 2021-03-23
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "入库收货交易明细管理")
@RequestMapping("/api/trsDetail")
public class TrsDetailController {

    private final TrsDetailService trsDetailService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('trsDetail:list')")
    public void download(HttpServletResponse response, TrsDetailQueryCriteria criteria) throws IOException {
        trsDetailService.download(trsDetailService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询入库收货交易明细")
    @ApiOperation("查询入库收货交易明细")
    @PreAuthorize("@el.check('trsDetail:list')")
    public ResponseEntity<Object> query(TrsDetailQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(trsDetailService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增入库收货交易明细")
    @ApiOperation("新增入库收货交易明细")
    @PreAuthorize("@el.check('trsDetail:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody TrsDetail resources){
        return new ResponseEntity<>(trsDetailService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改入库收货交易明细")
    @ApiOperation("修改入库收货交易明细")
    @PreAuthorize("@el.check('trsDetail:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody TrsDetail resources){
        trsDetailService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除入库收货交易明细")
    @ApiOperation("删除入库收货交易明细")
    @PreAuthorize("@el.check('trsDetail:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        trsDetailService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("导入入库收货交易明细")
    @ApiOperation("导入入库收货交易明细")
    @PreAuthorize("@el.check('trsDetail:uploadTrsD')")
    @PostMapping("uploadTrsD")
    public ResponseEntity<Object> uploadTrsD(@RequestParam("file") MultipartFile file,@RequestParam("stockId") Long stockId) {
        List<Map<String, Object>> maps = FileUtils.importMapExcel(file);
        trsDetailService.uploadTrsD(maps,stockId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("导出客户批次号和托盘号数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download-batchNo-lpn")
    @PreAuthorize("@el.check('trsDetail:list')")
    public void downloadBatchNoLpn(HttpServletResponse response) throws IOException {
        trsDetailService.downloadBatchNoLpn(response);
    }
}
