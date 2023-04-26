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
import cn.hutool.json.JSONObject;
import me.zhengjie.annotation.Log;
import me.zhengjie.domain.DailyStock;
import me.zhengjie.service.DailyStockService;
import me.zhengjie.service.dto.DailyStockQueryCriteria;
import me.zhengjie.utils.CollectionUtils;
import me.zhengjie.utils.DateUtils;
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
* @author luob
* @date 2021-07-06
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "dailyStock管理")
@RequestMapping("/api/dailyStock")
public class DailyStockController {

    private final DailyStockService dailyStockService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('dailyStock:list')")
    public void download(HttpServletResponse response, DailyStockQueryCriteria criteria) throws IOException {
        List<String> dayTimes = criteria.getDayTime();
        if (CollectionUtils.isNotEmpty(dayTimes)) {
            String start = dayTimes.get(0);
            String end = dayTimes.get(1);
            dayTimes.add(0, DateUtils.format(DateUtils.parse(start), DatePattern.PURE_DATE_FORMAT));
            dayTimes.add(1, DateUtils.format(DateUtils.parse(end), DatePattern.PURE_DATE_FORMAT));
        }
        dailyStockService.download(dailyStockService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询dailyStock")
    @ApiOperation("查询dailyStock")
    @PreAuthorize("@el.check('dailyStock:list')")
    public ResponseEntity<Object> query(DailyStockQueryCriteria criteria, Pageable pageable){
        List<String> dayTimes = criteria.getDayTime();
        if (CollectionUtils.isNotEmpty(dayTimes)) {
            String start = dayTimes.get(0);
            String end = dayTimes.get(1);
            start = DateUtils.format(DateUtils.parse(start), DatePattern.PURE_DATE_FORMAT);
            end = DateUtils.format(DateUtils.parse(end), DatePattern.PURE_DATE_FORMAT);
            dayTimes.add(0, start);
            dayTimes.add(1, end);
        }
        return new ResponseEntity<>(dailyStockService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增dailyStock")
    @ApiOperation("新增dailyStock")
    @PreAuthorize("@el.check('dailyStock:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody DailyStock resources){
        return new ResponseEntity<>(dailyStockService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改dailyStock")
    @ApiOperation("修改dailyStock")
    @PreAuthorize("@el.check('dailyStock:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody DailyStock resources){
        dailyStockService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除dailyStock")
    @ApiOperation("删除dailyStock")
    @PreAuthorize("@el.check('dailyStock:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        dailyStockService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/pull-stock")
    @Log("拉取库存")
    @ApiOperation("拉取库存")
    @PreAuthorize("@el.check('dailyStock:pullStock')")
    public ResponseEntity<Object> pullStock(String dayTime){
        dailyStockService.pullStock(dayTime);
        return new ResponseEntity<>( HttpStatus.OK);
    }

    @GetMapping(value = "/delete-stock")
    @Log("删除日结余库存")
    @ApiOperation("删除日结余库存")
    @PreAuthorize("@el.check('dailyStock:pullStock')")
    public ResponseEntity<Object> deleteStock(String dayTime){
        JSONObject result = new JSONObject();
        try {
            dailyStockService.deleteByDayTime(dayTime);
            result.putOnce("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            result.putOnce("success", false);
            result.putOnce("msg", e.getMessage());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/clear-key")
    @Log("清理redisKey")
    @ApiOperation("清理redisKey")
    @PreAuthorize("@el.check('dailyStock:pullStock')")
    public ResponseEntity<Object> clearKey(){
        JSONObject result = new JSONObject();
        try {
            dailyStockService.clearKey();
            result.putOnce("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            result.putOnce("success", false);
            result.putOnce("msg", e.getMessage());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}