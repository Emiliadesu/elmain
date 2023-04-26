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
import me.zhengjie.domain.PackageInfo;
import me.zhengjie.domain.SkuMaterial;
import me.zhengjie.service.PackageInfoService;
import me.zhengjie.service.dto.PackageInfoQueryCriteria;
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
* @author leningzhou
* @date 2022-01-26
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "包材信息管理")
@RequestMapping("/api/packageInfo")
public class PackageInfoController {

    private final PackageInfoService packageInfoService;

    @Log("查询耗材")
    @ApiOperation("查询耗材")
    @GetMapping(value = "/query-material-hc")
    public ResponseEntity<Object> queryMaterialHc(){
        List<PackageInfo> packageInfos = packageInfoService.queryMaterialHc();
        return new ResponseEntity<>(packageInfos, HttpStatus.OK);
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('packageInfo:list')")
    public void download(HttpServletResponse response, PackageInfoQueryCriteria criteria) throws IOException {
        packageInfoService.download(packageInfoService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询包材信息")
    @ApiOperation("查询包材信息")
    @PreAuthorize("@el.check('packageInfo:list')")
    public ResponseEntity<Object> query(PackageInfoQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(packageInfoService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增包材信息")
    @ApiOperation("新增包材信息")
    @PreAuthorize("@el.check('packageInfo:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody PackageInfo resources){
        return new ResponseEntity<>(packageInfoService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改包材信息")
    @ApiOperation("修改包材信息")
    @PreAuthorize("@el.check('packageInfo:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody PackageInfo resources){
        packageInfoService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除包材信息")
    @ApiOperation("删除包材信息")
    @PreAuthorize("@el.check('packageInfo:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        packageInfoService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("上传packageInfo")
    @RequestMapping(value = "upload")
    @PreAuthorize("@el.check('packageInfo:list')")
    @Log("上传logistics")
    public ResponseEntity<Object> upload(@RequestParam("file") MultipartFile file, @RequestParam("name") String name){
        List<Map<String, Object>> maps = FileUtils.importMapExcel(file);
        packageInfoService.upload(maps);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}