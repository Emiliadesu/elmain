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

import com.alibaba.fastjson.JSONArray;
import me.zhengjie.annotation.Log;
import me.zhengjie.domain.PddCloudPrintData;
import me.zhengjie.service.PddCloudPrintDataService;
import me.zhengjie.service.dto.PddCloudPrintDataQueryCriteria;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.util.List;

/**
* @website https://el-admin.vip
* @author wangmiao
* @date 2022-07-19
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "拼多多云打印数据管理")
@RequestMapping("/api/pddCloudPrintData")
public class PddCloudPrintDataController {

    private final PddCloudPrintDataService pddCloudPrintDataService;

    @PostMapping
    @Log("拼多多云打印")
    @ApiOperation("拼多多云打印")
    @PreAuthorize("@el.check('crossBorderOrder:print')")
    public ResponseEntity<Object> print(@RequestBody String body){
        return new ResponseEntity<>(pddCloudPrintDataService.print(JSONArray.parseArray(body,String.class)),HttpStatus.OK);
    }

    @PostMapping("/by-waveNo")
    @Log("查询拼多多云打印数据")
    @ApiOperation("查询拼多多云打印数据")
    @PreAuthorize("@el.check('crossBorderOrder:print')")
    public ResponseEntity<Object> printByWaveNo(@RequestBody String body){
        return new ResponseEntity<>(pddCloudPrintDataService.printByWaveNo(body.split("[, \n]")),HttpStatus.OK);
    }
}