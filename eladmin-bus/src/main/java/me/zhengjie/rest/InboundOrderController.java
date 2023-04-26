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
import me.zhengjie.domain.InboundOrder;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.InboundOrderService;
import me.zhengjie.service.UserCustomerService;
import me.zhengjie.service.dto.InboundOrderDto;
import me.zhengjie.service.dto.InboundOrderQueryCriteria;
import me.zhengjie.service.dto.ReturnGatherQueryCriteria;
import me.zhengjie.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author luob
* @date 2021-05-13
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "inboundOrder管理")
@RequestMapping("/api/inboundOrder")
public class InboundOrderController {

    private final InboundOrderService inboundOrderService;

    @Autowired
    private UserCustomerService userCustomerService;

    @Value("${spring.profiles.active}")
    private String active;

    @Value("${server.port}")
    private String serverPort;

    @GetMapping(value = "/verify-pass")
    @Log("审核通过")
    @ApiOperation("审核通过")
    @PreAuthorize("@el.check('inboundOrder:verifyPass')")
    public ResponseEntity<Object> verifyPass(Long orderId){
        JSONObject result = new JSONObject();
        try {
            inboundOrderService.verifyPass(orderId);
            result.putOnce("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            result.putOnce("success", false);
            result.putOnce("msg", e.getMessage());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/arrive-sign")
    @Log("到货登记")
    @ApiOperation("到货登记")
    @PreAuthorize("@el.check('inboundOrder:arriveSign')")
    public ResponseEntity<Object> arriveSign(@RequestBody InboundOrderDto resources){
        inboundOrderService.arriveSign(resources.getId(), resources.getCarNumber(), resources.getArriveTime());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/start-tally")
    @Log("开始理货")
    @ApiOperation("开始理货")
    @PreAuthorize("@el.check('inboundOrder:startTally')")
    public ResponseEntity<Object> startTally(Long orderId){
        inboundOrderService.startTally(orderId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/query-by-id")
    @Log("查询inboundOrder")
    @ApiOperation("查询inboundOrder")
    @PreAuthorize("@el.check('inboundOrder:list')")
    public ResponseEntity<Object> queryById(Long id){
        return new ResponseEntity<>(inboundOrderService.queryByIdWithDetails(id),HttpStatus.OK);
    }

    @GetMapping(value = "/query-tally-by-id")
    @Log("查询inboundOrder")
    @ApiOperation("查询inboundOrder")
    @PreAuthorize("@el.check('inboundOrder:list')")
    public ResponseEntity<Object> queryTallyById(Long id){
        return new ResponseEntity<>(inboundOrderService.queryTallyById(id),HttpStatus.OK);
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('inboundOrder:list')")
    public void download(HttpServletResponse response, InboundOrderQueryCriteria criteria) throws IOException {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> shopIds = userCustomerService.queryShops(userId);
        if (CollectionUtils.isEmpty(criteria.getShopId()) && CollectionUtils.isNotEmpty(shopIds)) {
            criteria.setShopId(shopIds);
        }
        inboundOrderService.download(inboundOrderService.queryAll(criteria), response);
    }

    @Log("导出明细")
    @ApiOperation("导出明细")
    @GetMapping(value = "/download-details")
    @PreAuthorize("@el.check('inboundOrder:list')")
    public void doExportDetails(HttpServletResponse response, InboundOrderQueryCriteria criteria) throws IOException {
        inboundOrderService.doExportDetails(inboundOrderService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询inboundOrder")
    @ApiOperation("查询inboundOrder")
    @PreAuthorize("@el.check('inboundOrder:list')")
    public ResponseEntity<Object> query(InboundOrderQueryCriteria criteria, Pageable pageable){
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> shopIds = userCustomerService.queryShops(userId);
        if (CollectionUtils.isEmpty(criteria.getShopId()) && CollectionUtils.isNotEmpty(shopIds)) {
            criteria.setShopId(shopIds);
        }
        return new ResponseEntity<>(inboundOrderService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增inboundOrder")
    @ApiOperation("新增inboundOrder")
    @PreAuthorize("@el.check('inboundOrder:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody InboundOrder resources){
        return new ResponseEntity<>(inboundOrderService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改inboundOrder")
    @ApiOperation("修改inboundOrder")
    @PreAuthorize("@el.check('inboundOrder:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody InboundOrder resources){
        if (StringUtil.isNotBlank(resources.getStockRecordUrl())&&resources.getStockRecordUrl().indexOf("/")==0){
            resources.setStockRecordUrl(StringUtil.urlEncodeCn(resources.getStockRecordUrl()));
            if (StringUtil.equals(active,"dev")){
                resources.setStockRecordUrl("http://"+StringUtils.getLocalIp()+":"+serverPort+resources.getStockRecordUrl());
            }else if (StringUtils.equals(active,"prod"))
                resources.setStockRecordUrl("http://erp.fl56.net:"+serverPort+resources.getStockRecordUrl());
            else if (StringUtils.equals(active,"test"))
                resources.setStockRecordUrl("http://120.55.9.185"+resources.getStockRecordUrl());
            else
                resources.setStockRecordUrl("http://127.0.0.1:"+serverPort+resources.getStockRecordUrl());
        }
        inboundOrderService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除inboundOrder")
    @ApiOperation("删除inboundOrder")
    @PreAuthorize("@el.check('inboundOrder:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        inboundOrderService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("刷新入库单状态")
    @ApiOperation("刷新入库单状态")
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
                inboundOrderService.refreshStatus(Long.valueOf(idArr[i]));
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
        List<Map<String, Object>> result = inboundOrderService.uploadSku(maps);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("dy-confirm-order")
    @Log("抖音回传接单")
    @ApiOperation("抖音回传接单")
    @PreAuthorize("@el.check('inboundOrder:dyConfirm')")
    public ResponseEntity<Object> dyConfirmOrder(@Validated Long id){
        inboundOrderService.dyConfirmOrder(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("dy-confirm-arrive")
    @Log("抖音回传到仓")
    @ApiOperation("抖音回传到仓")
    @PreAuthorize("@el.check('inboundOrder:dyConfirm')")
    public ResponseEntity<Object> dyConfirmArrive(@Validated Long id){
        inboundOrderService.dyConfirmArrive(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("dy-confirm-stock-tally")
    @Log("抖音回传开始理货")
    @ApiOperation("抖音回传开始理货")
    @PreAuthorize("@el.check('inboundOrder:dyConfirm')")
    public ResponseEntity<Object> dyConfirmStockTally(@Validated Long id){
        inboundOrderService.dyConfirmStockTally(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("dy-confirm-stocked-tally")
    @Log("抖音回传理货完成")
    @ApiOperation("抖音回传理货完成")
    @PreAuthorize("@el.check('inboundOrder:dyConfirm')")
    public ResponseEntity<Object> dyConfirmStockedTally(@Validated Long id){
        inboundOrderService.dyConfirmStockedTally(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("dy-confirm-up")
    @Log("抖音回传上架")
    @ApiOperation("抖音回传上架")
    @PreAuthorize("@el.check('inboundOrder:dyConfirm')")
    public ResponseEntity<Object> dyConfirmUp(@Validated Long id){
        inboundOrderService.dyConfirmUp(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("dy-confirm-cancel")
    @Log("抖音回传取消")
    @ApiOperation("抖音回传取消")
    @PreAuthorize("@el.check('inboundOrder:dyConfirm')")
    public ResponseEntity<Object> dyConfirmCancel(@Validated Long id){
        inboundOrderService.dyConfirmCancel(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
