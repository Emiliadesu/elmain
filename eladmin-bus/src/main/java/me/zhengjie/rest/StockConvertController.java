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
import me.zhengjie.domain.StockConvert;
import me.zhengjie.service.StockConvertService;
import me.zhengjie.service.dto.StockConvertQueryCriteria;
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
* @date 2021-04-18
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "stockConvert管理")
@RequestMapping("/api/stockConvert")
public class StockConvertController {

    private final StockConvertService stockConvertService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('stockConvert:list')")
    public void download(HttpServletResponse response, StockConvertQueryCriteria criteria) throws IOException {
        stockConvertService.download(stockConvertService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询stockConvert")
    @ApiOperation("查询stockConvert")
    @PreAuthorize("@el.check('stockConvert:list')")
    public ResponseEntity<Object> query(StockConvertQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(stockConvertService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @GetMapping("/query-by-id")
    @Log("根据id查询stockConvert")
    @ApiOperation("根据id查询stockConvert")
    @PreAuthorize("@el.check('stockConvert:list')")
    public ResponseEntity<Object> queryById(Long id){
        return new ResponseEntity<>(stockConvertService.queryById(id),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增stockConvert")
    @ApiOperation("新增stockConvert")
    @PreAuthorize("@el.check('stockConvert:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody StockConvert resources){
        return new ResponseEntity<>(stockConvertService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改stockConvert")
    @ApiOperation("修改stockConvert")
    @PreAuthorize("@el.check('stockConvert:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody StockConvert resources){
        stockConvertService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除stockConvert")
    @ApiOperation("删除stockConvert")
    @PreAuthorize("@el.check('stockConvert:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        stockConvertService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/complete")
    @Log("转移单作业完成")
    @ApiOperation("转移单作业完成")
    @PreAuthorize("@el.check('stockConvert:comp')")
    public ResponseEntity<Object> complete(Long id){
        return new ResponseEntity<>(stockConvertService.complete(id),HttpStatus.CREATED);
    }
}
