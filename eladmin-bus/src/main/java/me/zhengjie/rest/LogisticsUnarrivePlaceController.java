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
import me.zhengjie.domain.LogisticsUnarrivePlace;
import me.zhengjie.service.LogisticsUnarrivePlaceService;
import me.zhengjie.service.dto.LogisticsUnarrivePlaceQueryCriteria;
import me.zhengjie.utils.FileUtils;
import me.zhengjie.utils.StringUtils;
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
* @date 2021-12-03
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "物流不可至区域管理")
@RequestMapping("/api/logisticsUnarrivePlace")
public class LogisticsUnarrivePlaceController {

    private final LogisticsUnarrivePlaceService logisticsUnarrivePlaceService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('logisticsUnarrivePlace:list')")
    public void download(HttpServletResponse response, LogisticsUnarrivePlaceQueryCriteria criteria) throws IOException {
        logisticsUnarrivePlaceService.download(logisticsUnarrivePlaceService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询物流不可至区域")
    @ApiOperation("查询物流不可至区域")
    @PreAuthorize("@el.check('logisticsUnarrivePlace:list')")
    public ResponseEntity<Object> query(LogisticsUnarrivePlaceQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(logisticsUnarrivePlaceService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @ApiOperation("上传logistics")
    @RequestMapping(value = "uploadlogistics")
    @PreAuthorize("@el.check('logisticsUnarrivePlace:uploadlogistics')")
    @Log("上传logistics")
    public ResponseEntity<Object> uploadLogistics(@RequestParam("file") MultipartFile file, @RequestParam("name") String name){
        List<Map<String, Object>> maps = FileUtils.importMapExcel(file);
            logisticsUnarrivePlaceService.uploadLogistics(maps);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping
    @Log("新增物流不可至区域")
    @ApiOperation("新增物流不可至区域")
    @PreAuthorize("@el.check('logisticsUnarrivePlace:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody LogisticsUnarrivePlace resources){
        return new ResponseEntity<>(logisticsUnarrivePlaceService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改物流不可至区域")
    @ApiOperation("修改物流不可至区域")
    @PreAuthorize("@el.check('logisticsUnarrivePlace:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody LogisticsUnarrivePlace resources){
        logisticsUnarrivePlaceService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除物流不可至区域")
    @ApiOperation("删除物流不可至区域")
    @PreAuthorize("@el.check('logisticsUnarrivePlace:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        logisticsUnarrivePlaceService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}