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
import me.zhengjie.domain.YardBills;
import me.zhengjie.service.YardBillsService;
import me.zhengjie.service.dto.YardBillsDto;
import me.zhengjie.service.dto.YardBillsQueryCriteria;
import me.zhengjie.utils.DateUtils;
import me.zhengjie.utils.WordUtils;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author wangm
* @date 2021-03-16
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "堆场凭据管理")
@RequestMapping("/api/YardBills")
public class YardBillsController {

    private final YardBillsService YardBillsService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('YardBills:list')")
    public void download(HttpServletResponse response, YardBillsQueryCriteria criteria) throws IOException {
        YardBillsService.download(YardBillsService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询堆场凭据")
    @ApiOperation("查询堆场凭据")
    @PreAuthorize("@el.check('YardBills:list')")
    public ResponseEntity<Object> query(YardBillsQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(YardBillsService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增堆场凭据")
    @ApiOperation("新增堆场凭据")
    @PreAuthorize("@el.check('YardBills:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody YardBills resources){
        return new ResponseEntity<>(YardBillsService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改堆场凭据")
    @ApiOperation("修改堆场凭据")
    @PreAuthorize("@el.check('YardBills:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody YardBills resources){
        YardBillsService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除堆场凭据")
    @ApiOperation("删除堆场凭据")
    @PreAuthorize("@el.check('YardBills:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        YardBillsService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("打印堆场凭据")
    @ApiOperation("打印堆场凭据")
    @PreAuthorize("@el.check('YardBills:print')")
    @PostMapping("/print-bill")
    public void print(@RequestBody YardBills yardBills, HttpServletRequest request,HttpServletResponse response) {
        YardBillsDto bill=YardBillsService.findById(yardBills.getId());
        Map<String, String> map = new HashMap<>();
        map.put("boxNum", bill.getBoxNum());
        map.put("flight", bill.getFlight());
        map.put("customer", bill.getCustomer());
        map.put("telPhone", bill.getTelPhone());
        map.put("InDate", bill.getInDate());
        map.put("outDate", bill.getOutDate());
        map.put("dropBox", "￥"+bill.getDropBox());
        map.put("demurrage", "￥"+bill.getDemurrage());
        map.put("refund", "￥"+bill.getRefund());
        map.put("addCost", "￥"+bill.getAddCost());
        map.put("total", "￥"+bill.getTotal());
        map.put("printDate", DateUtils.now());
        map.put("sendAddress", bill.getSendAddress());
        map.put("sendTel", bill.getSendTel());
        try {
            WordUtils.prntBill(map,request.getServletContext().getClassLoader().getResourceAsStream("template/word/安运打印模板.docx"),response.getOutputStream());
        }catch (Exception e){
            e.printStackTrace();       }
        //return new ResponseEntity<>(HttpStatus.OK);
    }
}
