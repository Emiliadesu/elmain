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

import com.taobao.pac.sdk.cp.PacServlet;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.domain.CainiaoShopInfo;
import me.zhengjie.service.CainiaoShopInfoService;
import me.zhengjie.service.CrossborderErrorSyncService;
import me.zhengjie.service.dto.CainiaoShopInfoQueryCriteria;
import me.zhengjie.support.CNSupport;
import me.zhengjie.support.cainiao.CaiNiaoSupport;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author wangm
* @date 2022-11-21
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "菜鸟店铺信息管理")
@RequestMapping("/api/cainiaoShopInfo")
public class CainiaoShopInfoController {

    private final CainiaoShopInfoService cainiaoShopInfoService;

    @Autowired
    private CaiNiaoSupport caiNiaoSupport;

    @Autowired
    private CrossborderErrorSyncService crossborderErrorSyncService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('cainiaoShopInfo:list')")
    public void download(HttpServletResponse response, CainiaoShopInfoQueryCriteria criteria) throws IOException {
        cainiaoShopInfoService.download(cainiaoShopInfoService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询菜鸟店铺信息")
    @ApiOperation("查询菜鸟店铺信息")
    @PreAuthorize("@el.check('cainiaoShopInfo:list')")
    public ResponseEntity<Object> query(CainiaoShopInfoQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(cainiaoShopInfoService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增菜鸟店铺信息")
    @ApiOperation("新增菜鸟店铺信息")
    @PreAuthorize("@el.check('cainiaoShopInfo:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody CainiaoShopInfo resources){
        return new ResponseEntity<>(cainiaoShopInfoService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改菜鸟店铺信息")
    @ApiOperation("修改菜鸟店铺信息")
    @PreAuthorize("@el.check('cainiaoShopInfo:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody CainiaoShopInfo resources){
        cainiaoShopInfoService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除菜鸟店铺信息")
    @ApiOperation("删除菜鸟店铺信息")
    @PreAuthorize("@el.check('cainiaoShopInfo:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        cainiaoShopInfoService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping("/error-sync")
    @AnonymousAccess
    @Log("菜鸟错误信息回调接口")
    @ApiOperation("菜鸟错误信息回调接口")
    public void crossborderErrorSync(HttpServletRequest request, HttpServletResponse response) {
        PacServlet pacServlet = new PacServlet();
        pacServlet.setPacClient(caiNiaoSupport.pacClient);
        caiNiaoSupport.pacClient.registerReceiveListener("CROSSBORDER_ERROR_SYNC", crossborderErrorSyncService);
        pacServlet.init();
        try {
            pacServlet.doPost(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/send-order-test")
    @Log("发送订单给菜鸟测试")
    @AnonymousAccess
    @ApiOperation("发送订单给菜鸟测试")
    public void sendOrderTest(String orderNo) {
        cainiaoShopInfoService.sendOrderTest(orderNo);
    }
    @RequestMapping("declare-result-callback-test")
    @Log("申报回执给菜鸟测试")
    @AnonymousAccess
    @ApiOperation("申报回执给菜鸟测试")
    public void declareResultCallBackTest(String orderNo) throws Exception {
        cainiaoShopInfoService.declareResultCallBackTest(orderNo);
    }
    @RequestMapping("/cancel-declare-test")
    @Log("菜鸟订单取消测试")
    @AnonymousAccess
    @ApiOperation("菜鸟订单取消测试")
    public void cancelDeclareTest(String orderNo) {
        cainiaoShopInfoService.cancelDeclareTest(orderNo);
    }

    @RequestMapping("/query-wms-status-test")
    @Log("菜鸟查询测试")
    @AnonymousAccess
    @ApiOperation("菜鸟查询测试")
    public void queryWmsStatusTest(String orderNo) {
        cainiaoShopInfoService.queryWmsStatusTest(orderNo);
    }

    @RequestMapping("/lastmine-hoin-callback-test")
    @Log("菜鸟结算费用测试")
    @AnonymousAccess
    @ApiOperation("菜鸟结算费用测试")
    public void lastmineHoinCallbackTest(String orderNo) {
        cainiaoShopInfoService.lastmineHoinCallbackTest(orderNo);
    }

    @RequestMapping("/query-logistics-detail-test")
    @Log("查询菜鸟物流状态测试")
    @AnonymousAccess
    @ApiOperation("查询菜鸟物流状态测试")
    public void queryLogisticsDetailTest(String orderNo) {
        cainiaoShopInfoService.queryLogisticsDetailTest(orderNo);
    }
}