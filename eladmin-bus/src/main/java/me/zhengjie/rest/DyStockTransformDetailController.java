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
import me.zhengjie.domain.DyStockTransformDetail;
import me.zhengjie.service.DyStockTransformDetailService;
import me.zhengjie.service.dto.ActTransactionLogSoAsnInvLotAtt;
import me.zhengjie.service.dto.DyStockTransformDetailQueryCriteria;
import me.zhengjie.support.WmsSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author wangm
* @date 2021-09-26
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "抖音库存类型变动回告详情管理")
@RequestMapping("/api/dyStockTransformDetail")
public class DyStockTransformDetailController {

    private final DyStockTransformDetailService dyStockTransformDetailService;

    @Autowired
    private WmsSupport wmsSupport;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('dyStockTransformDetail:list')")
    public void download(HttpServletResponse response, DyStockTransformDetailQueryCriteria criteria) throws IOException {
        dyStockTransformDetailService.download(dyStockTransformDetailService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询抖音库存类型变动回告详情")
    @ApiOperation("查询抖音库存类型变动回告详情")
    @PreAuthorize("@el.check('dyStockTransformDetail:list')")
    public ResponseEntity<Object> query(DyStockTransformDetailQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(dyStockTransformDetailService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增抖音库存类型变动回告详情")
    @ApiOperation("新增抖音库存类型变动回告详情")
    @PreAuthorize("@el.check('dyStockTransformDetail:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody DyStockTransformDetail resources){
        return new ResponseEntity<>(dyStockTransformDetailService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改抖音库存类型变动回告详情")
    @ApiOperation("修改抖音库存类型变动回告详情")
    @PreAuthorize("@el.check('dyStockTransformDetail:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody DyStockTransformDetail resources){
        dyStockTransformDetailService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除抖音库存类型变动回告详情")
    @ApiOperation("删除抖音库存类型变动回告详情")
    @PreAuthorize("@el.check('dyStockTransformDetail:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        dyStockTransformDetailService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/query-by-mainId")
    @Log("根据库存调整id查询明细")
    @ApiOperation("根据库存调整id查询明细")
    @PreAuthorize("@el.check('dyStockTransformDetail:list')")
    public ResponseEntity<Object> queryByMainId(Long mainId){
        return new ResponseEntity<>(dyStockTransformDetailService.queryByMainIdDto(mainId),HttpStatus.CREATED);
    }
    @GetMapping("/get-flux-trans-records")
    @Log("根据货号查询库存转残转良记录")
    @ApiOperation("根据货号查询库存转残转良记录")
    public ResponseEntity<Object> getFluxTransRecords(String goodsNo,String fmGrad,String toGrad){
        return new ResponseEntity<>(wmsSupport.getTransRecords(goodsNo,fmGrad,toGrad),HttpStatus.CREATED);
    }

}
