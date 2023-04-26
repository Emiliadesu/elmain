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

import com.alibaba.fastjson.JSONObject;
import me.zhengjie.annotation.Log;
import me.zhengjie.domain.WmsOutstock;
import me.zhengjie.service.WmsInstockService;
import me.zhengjie.service.WmsOutstockService;
import me.zhengjie.service.dto.WmsOutstockQueryCriteria;
import me.zhengjie.utils.FluxUtils;
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
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author 王淼
* @date 2020-12-04
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "wmsOutStock管理")
@RequestMapping("/api/wmsOutstock")
public class WmsOutstockController {

    private final WmsOutstockService wmsOutstockService;
    @Autowired
    private WmsInstockService wmsInstockService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('wmsOutstock:list')")
    public void download(HttpServletResponse response, WmsOutstockQueryCriteria criteria) throws IOException {
        wmsOutstockService.download(wmsOutstockService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询wmsOutStock")
    @ApiOperation("查询wmsOutStock")
    @PreAuthorize("@el.check('wmsOutstock:list')")
    public ResponseEntity<Object> query(WmsOutstockQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(wmsOutstockService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增wmsOutStock")
    @ApiOperation("新增wmsOutStock")
    @PreAuthorize("@el.check('wmsOutstock:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody WmsOutstock resources){
        return new ResponseEntity<>(wmsOutstockService.create(resources),HttpStatus.CREATED);
    }

    @PostMapping("/tmpToCreated")
    @Log("创建入库单(主、子都已经added)")
    @ApiOperation("新增wmsOutstock")
    @PreAuthorize("@el.check('wmsOutstock:add')")
    public ResponseEntity<Object> tmpToCreated(@RequestBody WmsOutstock resources){
        resources.setOutStatus("00");
        wmsOutstockService.update(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改wmsOutStock")
    @ApiOperation("修改wmsOutStock")
    @PreAuthorize("@el.check('wmsOutstock:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody WmsOutstock resources){
        wmsOutstockService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除wmsOutStock")
    @ApiOperation("删除wmsOutStock")
    @PreAuthorize("@el.check('wmsOutstock:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        wmsOutstockService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("query-by-id")
    @Log("根据id查询wmsOutstock")
    @ApiOperation("根据id查询wmsOutstock")
    @PreAuthorize("@el.check('wmsOutstock:list')")
    public ResponseEntity<Object> query(Long id){
        return new ResponseEntity<>(wmsOutstockService.queryByIdDto(id),HttpStatus.OK);
    }

    @PostMapping("get-batch-info")
    @Log("根据id查询wmsOutstock")
    @ApiOperation("根据id查询wmsOutstock")
    @PreAuthorize("@el.check('wmsOutstock:list')")
    public ResponseEntity<Object> getBatchInfo(@RequestParam(name = "soNo") String soNo,@RequestParam(name = "barCode")String barCode){
        JSONObject resp=FluxUtils.getWmsOutStockInfo(soNo,barCode);
        return new ResponseEntity<>(resp,HttpStatus.OK);
    }

    @PostMapping("push-lpn-stock")
    @Log("推送出仓商品和托盘信息")
    @ApiOperation("推送出仓商品和托盘信息")
    @PreAuthorize("@el.check('wmsOutstock:pushLpnStock')")
    public ResponseEntity<Object> pushLpnStock(@RequestParam(name = "id")Long id){
        wmsOutstockService.pushLpnStock(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PostMapping("push-pre-load")
    @Log("推送预装载单信息")
    @ApiOperation("推送预装载单信息")
    @PreAuthorize("@el.check('wmsOutstock:pushPreLoad')")
    public ResponseEntity<Object> pushPreLoad(@RequestParam(name = "id")Long id){
        wmsOutstockService.pushPreLoad(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("sync-status")
    @Log("同步通知单状态")
    @ApiOperation("同步通知单状态")
    @PreAuthorize("@el.check('wmsOutstock:edit')")
    public ResponseEntity<Object> syncStatus(String idsStr){
        String[]idsStrArray=idsStr.split(",");
        Long[]ids=new Long[idsStrArray.length];
        for (int i = 0; i < idsStrArray.length; i++) {
            ids[i]=Long.parseLong(idsStrArray[i]);
        }
        wmsOutstockService.syncStatus(ids);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("out-stock")
    @Log("出库回传")
    @ApiOperation("出库回传")
    @PreAuthorize("@el.check('wmsOutstock:deliver')")
    public ResponseEntity<Object> outStock(String idStr){
        wmsOutstockService.outStock(Long.parseLong(idStr));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("get-order-sns")
    @Log("得到所有待上传车辆信息的出库单号")
    @ApiOperation("得到所有待上传车辆信息的出库单号")
    @PreAuthorize("@el.check('wmsOutstock:list')")
    public ResponseEntity<Object> getOrderSns(){
        return new ResponseEntity<>(wmsOutstockService.getOrderSns(),HttpStatus.OK);
    }
}
