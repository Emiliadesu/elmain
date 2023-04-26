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

import cn.hutool.core.date.DatePattern;
import me.zhengjie.annotation.Log;
import me.zhengjie.domain.Config;
import me.zhengjie.domain.DailyCrossBorderOrder;
import me.zhengjie.domain.DailyCrossBorderOrderDetails;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.DailyCrossBorderOrderDetailsService;
import me.zhengjie.service.DailyCrossBorderOrderService;
import me.zhengjie.service.dto.CrossBorderOrderQueryCriteria;
import me.zhengjie.service.dto.DailyCrossBorderOrderDetailsQueryCriteria;
import me.zhengjie.service.dto.DailyCrossBorderOrderQueryCriteria;
import me.zhengjie.utils.CollectionUtils;
import me.zhengjie.utils.DateUtils;
import me.zhengjie.utils.SecurityUtils;
import me.zhengjie.utils.StringUtils;
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
* @author leningzhou
* @date 2022-07-04
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "dailyCrossBorderOrder管理")
@RequestMapping("/api/dailyCrossBorderOrder")
public class DailyCrossBorderOrderController {

    @Autowired
    private DailyCrossBorderOrderDetailsService dailyCrossBorderOrderDetailsService;

    private final DailyCrossBorderOrderService dailyCrossBorderOrderService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('dailyCrossBorderOrder:list')")
    public void download(HttpServletResponse response, DailyCrossBorderOrderQueryCriteria criteria) throws IOException {
        /*List<String> dayTimes = criteria.getDayTime();
        if (CollectionUtils.isNotEmpty(dayTimes)) {
            String start = dayTimes.get(0);
            String end = dayTimes.get(1);
            dayTimes.add(0, DateUtils.format(DateUtils.parse(start), DatePattern.PURE_DATE_FORMAT));
            dayTimes.add(1, DateUtils.format(DateUtils.parse(end), DatePattern.PURE_DATE_FORMAT));
        }
        dailyCrossBorderOrderService.download(dailyCrossBorderOrderService.queryAll(criteria), response);*/
    }

    @Log("导出数据明细")
    @ApiOperation("导出数据明细")
    @GetMapping(value = "/download-details")
    @PreAuthorize("@el.check('dailyCrossBorderOrder:list')")
    public void downloadDetails(HttpServletResponse response, DailyCrossBorderOrderQueryCriteria criteria) throws IOException {
        /*List<String> dayTimes = criteria.getDayTime();
        if (CollectionUtils.isNotEmpty(dayTimes)) {
            String start = dayTimes.get(0);
            String end = dayTimes.get(1);
            dayTimes.add(0, DateUtils.format(DateUtils.parse(start), DatePattern.PURE_DATE_FORMAT));
            dayTimes.add(1, DateUtils.format(DateUtils.parse(end), DatePattern.PURE_DATE_FORMAT));
        }
        dailyCrossBorderOrderService.downloadDetails(dailyCrossBorderOrderService.queryAll(criteria), response);*/
    }

    @GetMapping
    @Log("查询dailyCrossBorderOrder")
    @ApiOperation("查询dailyCrossBorderOrder")
    @PreAuthorize("@el.check('dailyCrossBorderOrder:list')")
    public ResponseEntity<Object> query(DailyCrossBorderOrderQueryCriteria criteria, Pageable pageable){
        /*List<String> dayTimes = criteria.getDayTime();
        if (CollectionUtils.isNotEmpty(dayTimes)) {
            String start = dayTimes.get(0);
            String end = dayTimes.get(1);
            start = DateUtils.format(DateUtils.parse(start), DatePattern.PURE_DATE_FORMAT);
            end = DateUtils.format(DateUtils.parse(end), DatePattern.PURE_DATE_FORMAT);
            dayTimes.add(0, start);
            dayTimes.add(1, end);
        }
        return new ResponseEntity<>(dailyCrossBorderOrderService.queryAll(criteria,pageable),HttpStatus.OK);*/
        return  null;
    }

    @PostMapping
    @Log("新增dailyCrossBorderOrder")
    @ApiOperation("新增dailyCrossBorderOrder")
    @PreAuthorize("@el.check('dailyCrossBorderOrder:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody DailyCrossBorderOrder resources){
        return new ResponseEntity<>(dailyCrossBorderOrderService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改dailyCrossBorderOrder")
    @ApiOperation("修改dailyCrossBorderOrder")
    @PreAuthorize("@el.check('dailyCrossBorderOrder:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody DailyCrossBorderOrder resources){
        dailyCrossBorderOrderService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除dailyCrossBorderOrder")
    @ApiOperation("删除dailyCrossBorderOrder")
    @PreAuthorize("@el.check('dailyCrossBorderOrder:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        dailyCrossBorderOrderService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}