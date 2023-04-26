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
import me.zhengjie.domain.BaseSku;
import me.zhengjie.domain.SkuMaterial;
import me.zhengjie.service.SkuMaterialService;
import me.zhengjie.service.dto.SkuMaterialQueryCriteria;
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
* @author luob
* @date 2022-05-20
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "skuMaterial管理")
@RequestMapping("/api/skuMaterial")
public class SkuMaterialController {

    private final SkuMaterialService skuMaterialService;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('skuMaterial:list')")
    public void download(HttpServletResponse response, SkuMaterialQueryCriteria criteria) throws IOException {
        skuMaterialService.download(skuMaterialService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询skuMaterial")
    @ApiOperation("查询skuMaterial")
    @PreAuthorize("@el.check('skuMaterial:list')")
    public ResponseEntity<Object> query(SkuMaterialQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(skuMaterialService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增skuMaterial")
    @ApiOperation("新增skuMaterial")
    @PreAuthorize("@el.check('skuMaterial:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody SkuMaterial resources){
        return new ResponseEntity<>(skuMaterialService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改skuMaterial")
    @ApiOperation("修改skuMaterial")
    @PreAuthorize("@el.check('skuMaterial:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody SkuMaterial resources){
        skuMaterialService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除skuMaterial")
    @ApiOperation("删除skuMaterial")
    @PreAuthorize("@el.check('skuMaterial:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        skuMaterialService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("上传skuMaterial")
    @RequestMapping(value = "upload")
    @PreAuthorize("@el.check('skuMaterial:list')")
    @Log("上传skuMaterial")
    public ResponseEntity<Object> upload(@RequestParam("file") MultipartFile file, @RequestParam("name") String name){
        List<Map<String, Object>> maps = FileUtils.importMapExcel(file);
        skuMaterialService.upload(maps);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}