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
import me.zhengjie.domain.DouyinGoodsDetails;
import me.zhengjie.service.DouyinGoodsDetailsService;
import me.zhengjie.service.dto.DouyinGoodsDetailsQueryCriteria;
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
* @author le
* @date 2021-09-28
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "douyingoodsdetails管理")
@RequestMapping("/api/douyinGoodsDetails")
public class DouyinGoodsDetailsController {

    private final DouyinGoodsDetailsService douyinGoodsDetailsService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('douyinGoodsDetails:list')")
    public void download(HttpServletResponse response, DouyinGoodsDetailsQueryCriteria criteria) throws IOException {
        douyinGoodsDetailsService.download(douyinGoodsDetailsService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询douyingoodsdetails")
    @ApiOperation("查询douyingoodsdetails")
    @PreAuthorize("@el.check('douyinGoodsDetails:list')")
    public ResponseEntity<Object> query(DouyinGoodsDetailsQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(douyinGoodsDetailsService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增douyingoodsdetails")
    @ApiOperation("新增douyingoodsdetails")
    @PreAuthorize("@el.check('douyinGoodsDetails:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody DouyinGoodsDetails resources){
        return new ResponseEntity<>(douyinGoodsDetailsService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改douyingoodsdetails")
    @ApiOperation("修改douyingoodsdetails")
    @PreAuthorize("@el.check('douyinGoodsDetails:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody DouyinGoodsDetails resources){
        douyinGoodsDetailsService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除douyingoodsdetails")
    @ApiOperation("删除douyingoodsdetails")
    @PreAuthorize("@el.check('douyinGoodsDetails:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        douyinGoodsDetailsService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}