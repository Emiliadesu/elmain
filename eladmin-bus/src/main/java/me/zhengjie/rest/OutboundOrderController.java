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

import cn.hutool.json.JSONObject;
import me.zhengjie.annotation.Log;
import me.zhengjie.domain.OutboundOrder;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.OutboundOrderService;
import me.zhengjie.service.UserCustomerService;
import me.zhengjie.service.dto.InboundOrderQueryCriteria;
import me.zhengjie.service.dto.OutboundOrderQueryCriteria;
import me.zhengjie.utils.CollectionUtils;
import me.zhengjie.utils.FileUtils;
import me.zhengjie.utils.SecurityUtils;
import me.zhengjie.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @author luob
 * @website https://el-admin.vip
 * @date 2021-07-13
 **/
@RestController
@RequiredArgsConstructor
@Api(tags = "outboundOrder管理")
@RequestMapping("/api/outboundOrder")
public class OutboundOrderController {

    private final OutboundOrderService outboundOrderService;

    @Autowired
    private UserCustomerService userCustomerService;

    @GetMapping(value = "/query-by-id")
    @Log("查询outboundOrder")
    @ApiOperation("查询outboundOrder")
    @PreAuthorize("@el.check('outboundOrder:list')")
    public ResponseEntity<Object> queryById(Long id){
        return new ResponseEntity<>(outboundOrderService.queryByIdWithDetails(id),HttpStatus.OK);
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('outboundOrder:list')")
    public void download(HttpServletResponse response, OutboundOrderQueryCriteria criteria) throws IOException {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> shopIds = userCustomerService.queryShops(userId);
        if (CollectionUtils.isEmpty(criteria.getShopId()) && CollectionUtils.isNotEmpty(shopIds)) {
            criteria.setShopId(shopIds);
        }
        outboundOrderService.download(outboundOrderService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询outboundOrder")
    @ApiOperation("查询outboundOrder")
    @PreAuthorize("@el.check('outboundOrder:list')")
    public ResponseEntity<Object> query(OutboundOrderQueryCriteria criteria, Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> shopIds = userCustomerService.queryShops(userId);
        if (CollectionUtils.isEmpty(criteria.getShopId()) && CollectionUtils.isNotEmpty(shopIds)) {
            criteria.setShopId(shopIds);
        }
        return new ResponseEntity<>(outboundOrderService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @PostMapping
    @Log("新增outboundOrder")
    @ApiOperation("新增outboundOrder")
    @PreAuthorize("@el.check('outboundOrder:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody OutboundOrder resources) {
        return new ResponseEntity<>(outboundOrderService.create(resources), HttpStatus.CREATED);
    }

    @Log("导出明细")
    @ApiOperation("导出明细")
    @GetMapping(value = "/download-details")
    @PreAuthorize("@el.check('outboundOrder:list')")
    public void doExportDetails(HttpServletResponse response, OutboundOrderQueryCriteria criteria) throws IOException {
        outboundOrderService.doExportDetails(outboundOrderService.queryAll(criteria), response);
    }

    @PutMapping
    @Log("修改outboundOrder")
    @ApiOperation("修改outboundOrder")
    @PreAuthorize("@el.check('outboundOrder:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody OutboundOrder resources) {
        outboundOrderService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除outboundOrder")
    @ApiOperation("删除outboundOrder")
    @PreAuthorize("@el.check('outboundOrder:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        outboundOrderService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("出库通知单审核通过")
    @ApiOperation("出库通知单审核通过")
    @PreAuthorize("@el.check('outboundOrder:verifyPass')")
    @GetMapping("/verify-pass")
    public ResponseEntity<Object> verifyPass(Long orderId) {
        JSONObject result = new JSONObject();
        try {
            outboundOrderService.verifyPass(orderId);
            result.putOnce("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            result.putOnce("success", false);
            result.putOnce("msg", e.getMessage());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("刷新出库单状态")
    @ApiOperation("刷新出库单状态")
    @GetMapping(value = "/refresh-status")
    @PreAuthorize("@el.check('inboundOrder:refreshStatus')")
    public ResponseEntity<Object> refreshStatus(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                outboundOrderService.refreshStatus(Long.valueOf(idArr[i]));
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation("上传SKU")
    @RequestMapping(value = "uploadSku")
    @PreAuthorize("@el.check('inboundOrder:add')")
    @Log("上传SKU")
    public ResponseEntity<Object> uploadSku(@RequestParam("file") MultipartFile file){
        List<Map<String, Object>> maps = FileUtils.importMapExcel(file);
        List<Map<String, Object>> result = outboundOrderService.uploadSku(maps);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("dy-confirm-order")
    @Log("抖音回传接单")
    @ApiOperation("抖音回传接单")
    @PreAuthorize("@el.check('outboundOrder:dyConfirm')")
    public ResponseEntity<Object> dyConfirmOrder(@Validated Long id){
        outboundOrderService.dyConfirmOrder(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("dy-confirm-stock-tally")
    @Log("抖音回传开始理货")
    @ApiOperation("抖音回传开始理货")
    @PreAuthorize("@el.check('outboundOrder:dyConfirm')")
    public ResponseEntity<Object> dyConfirmStockTally(@Validated Long id){
        outboundOrderService.dyConfirmStockTally(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("dy-confirm-stocked-tally")
    @Log("抖音回传理货完成")
    @ApiOperation("抖音回传理货完成")
    @PreAuthorize("@el.check('outboundOrder:dyConfirm')")
    public ResponseEntity<Object> dyConfirmStockedTally(@Validated Long id){
        outboundOrderService.dyConfirmStockedTally(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("dy-confirm-out")
    @Log("抖音回传出库")
    @ApiOperation("抖音回传出库")
    @PreAuthorize("@el.check('outboundOrder:dyConfirm')")
    public ResponseEntity<Object> dyConfirmOut(@Validated Long id){
        outboundOrderService.dyConfirmOut(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("dy-confirm-cancel")
    @Log("抖音回传取消")
    @ApiOperation("抖音回传取消")
    @PreAuthorize("@el.check('outboundOrder:dyConfirm')")
    public ResponseEntity<Object> dyConfirmCancel(@Validated Long id){
        outboundOrderService.dyConfirmCancel(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
