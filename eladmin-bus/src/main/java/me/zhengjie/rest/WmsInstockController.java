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
import me.zhengjie.domain.WmsInstock;
import me.zhengjie.service.WmsInstockService;
import me.zhengjie.service.dto.WmsInstockQueryCriteria;
import me.zhengjie.utils.StringUtil;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import java.sql.Timestamp;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author 王淼
* @date 2020-12-08
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "wmsInstock管理")
@RequestMapping("/api/wmsInstock")
public class WmsInstockController {

    private final WmsInstockService wmsInstockService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('wmsInstock:list')")
    public void download(HttpServletResponse response, WmsInstockQueryCriteria criteria) throws IOException {
        wmsInstockService.download(wmsInstockService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询wmsInstock")
    @ApiOperation("查询wmsInstock")
    @PreAuthorize("@el.check('wmsInstock:list')")
    public ResponseEntity<Object> query(WmsInstockQueryCriteria criteria, Pageable pageable){

        return new ResponseEntity<>(wmsInstockService.queryAll(criteria,pageable),HttpStatus.OK);
    }
    @GetMapping("query-by-id")
    @Log("根据id查询wmsInstock")
    @ApiOperation("根据id查询wmsInstock")
    @PreAuthorize("@el.check('wmsInstock:list')")
    public ResponseEntity<Object> query(Long id){

        return new ResponseEntity<>(wmsInstockService.queryByIdDto(id),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增wmsInstock")
    @ApiOperation("新增wmsInstock")
    @PreAuthorize("@el.check('wmsInstock:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody WmsInstock resources){
        return new ResponseEntity<>(wmsInstockService.create(resources),HttpStatus.CREATED);
    }

    @PostMapping("/tmpToCreated")
    @Log("创建入库单(主、子added)")
    @ApiOperation("新增wmsInstock")
    @PreAuthorize("@el.check('wmsInstock:add')")
    public ResponseEntity<Object> tmpToCreated(@RequestBody WmsInstock resources){
        resources.setInStatus("00");
        wmsInstockService.update(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改wmsInstock")
    @ApiOperation("修改wmsInstock")
    @PreAuthorize("@el.check('wmsInstock:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody WmsInstock resources){
        wmsInstockService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除wmsInstock")
    @ApiOperation("删除wmsInstock")
    @PreAuthorize("@el.check('wmsInstock:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        wmsInstockService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("upload-arriveTime")
    @Log("上传到货时间")
    @ApiOperation("上传到货时间")
    @PreAuthorize("@el.check('wmsInstock:edit')")
    public ResponseEntity<Object> uploadArriveTime(@RequestBody WmsInstock wmsInstock){
        wmsInstockService.uploadArriveTime(wmsInstock);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("update-status")
    @Log("更改状态")
    @ApiOperation("更改状态")
    @PreAuthorize("@el.check('wmsInstock:sync')")
    public ResponseEntity<Object> updateStatus(@RequestBody WmsInstock wmsInstock){
        wmsInstockService.updateStatus(wmsInstock);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("sync-status")
    @Log("同步通知单状态")
    @ApiOperation("同步通知单状态")
    @PreAuthorize("@el.check('wmsInstock:edit')")
    public ResponseEntity<Object> syncStatus(String idsStr){
        String[]idsStrArray=idsStr.split(",");
        Long[]ids=new Long[idsStrArray.length];
        for (int i = 0; i < idsStrArray.length; i++) {
            ids[i]=Long.parseLong(idsStrArray[i]);
        }
        wmsInstockService.syncStatus(ids);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
