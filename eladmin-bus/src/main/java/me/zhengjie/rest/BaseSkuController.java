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
import me.zhengjie.annotation.rest.AnonymousPostMapping;
import me.zhengjie.domain.*;
import me.zhengjie.entity.Result;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.BaseSkuQueryCriteria;
import me.zhengjie.service.dto.CustomerKeyDto;
import me.zhengjie.utils.*;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author luob
* @date 2021-04-15
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "basesku管理")
@RequestMapping("/api/baseSku")
public class BaseSkuController {

    private final BaseSkuService baseSkuService;

    @Autowired
    private UserCustomerService userCustomerService;

    @Autowired
    private CustomerKeyService customerKeyService;

    @GetMapping(value = "/query-by-id")
    @Log("查询baseSku")
    @ApiOperation("查询baseSku")
    @PreAuthorize("@el.check('baseSku:list')")
    public ResponseEntity<Object> queryById(String id){
        return new ResponseEntity<>(baseSkuService.findById(Long.valueOf(id)),HttpStatus.OK);
    }

    @GetMapping(value = "/query-by-goodsNo-and-shop")
    @Log("通过货号查询baseSku")
    @ApiOperation("查询baseSku")
    @PreAuthorize("@el.check('baseSku:list')")
    public ResponseEntity<Object> queryByGoodsNoAndShop(String goodsNo,
                                                        Long shopId,
                                                        Integer expectNum){
        if (null == shopId)
            throw new BadRequestException("请选择店铺");
        if (StringUtil.isBlank(goodsNo))
            throw new BadRequestException("请输入货号");
        if (null == expectNum)
            throw new BadRequestException("请输入数量");
        BaseSku baseSku = baseSkuService.queryByGoodsNoAndShop(goodsNo, shopId);
        if (baseSku == null)
            throw new BadRequestException("货号不存在");
        return new ResponseEntity<>(baseSku, HttpStatus.OK);
    }

    @GetMapping(value = "/query-by-barCode-and-shop")
    @Log("通过货号查询baseSku")
    @ApiOperation("查询baseSku")
    @PreAuthorize("@el.check('baseSku:list')")
    public ResponseEntity<Object> queryByBarCodeAndShop(String barCode,
                                                        Long shopId,
                                                        Integer expectNum){
        if (null == shopId)
            throw new BadRequestException("请选择店铺");
        if (StringUtil.isBlank(barCode))
            throw new BadRequestException("请输入条码");
        if (null == expectNum)
            throw new BadRequestException("请输入数量");
        BaseSku baseSku = baseSkuService.queryByBarCodeAndShopId(barCode, shopId);
        if (baseSku == null)
            throw new BadRequestException("条码不存在");
        return new ResponseEntity<>(baseSku, HttpStatus.OK);
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('baseSku:list')")
    public void download(HttpServletResponse response, BaseSkuQueryCriteria criteria) throws IOException {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> shopIds = userCustomerService.queryShops(userId);
        if (CollectionUtils.isEmpty(criteria.getShopId()) && CollectionUtils.isNotEmpty(shopIds)) {
            criteria.setShopId(shopIds);
        }
        baseSkuService.download(baseSkuService.queryAll(criteria), response);
    }

    @Log("导出备案数据")
    @ApiOperation("导出备案数据")
    @GetMapping(value = "/downloads")
    @PreAuthorize("@el.check('baseSku:lists')")
    public void downloads(HttpServletResponse response, BaseSkuQueryCriteria criteria) throws IOException {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> shopIds = userCustomerService.queryShops(userId);
        if (CollectionUtils.isEmpty(criteria.getShopId()) && CollectionUtils.isNotEmpty(shopIds)) {
            criteria.setShopId(shopIds);
        }
        baseSkuService.downloads(baseSkuService.queryAll(criteria), response);
    }

    @GetMapping("/querySkuAndGiftSku")
    @Log("查询主品和赠品basesku")
    @ApiOperation("查询主品basesku")
    public ResponseEntity<Object> querySkuAndGiftSku(Long shopId){
        return new ResponseEntity<>(baseSkuService.querySkuAndGiftSku(shopId),HttpStatus.OK);
    }

    @GetMapping
    @Log("查询basesku")
    @ApiOperation("查询basesku")
    @PreAuthorize("@el.check('baseSku:list')")
    public ResponseEntity<Object> query(BaseSkuQueryCriteria criteria, Pageable pageable){
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> shopIds = userCustomerService.queryShops(userId);
        if (CollectionUtils.isEmpty(criteria.getShopId()) && CollectionUtils.isNotEmpty(shopIds)) {
            criteria.setShopId(shopIds);
        }
        return new ResponseEntity<>(baseSkuService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增basesku")
    @ApiOperation("新增basesku")
    @PreAuthorize("@el.check('baseSku:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody BaseSku resources){
        return new ResponseEntity<>(baseSkuService.create(resources),HttpStatus.CREATED);
    }

    @PostMapping("/get-baseSku")
    @Log("查询baseSku，根据传入的条件查询")
    @ApiOperation("查询baseSku，根据传入的条件查询")
    @PreAuthorize("@el.check('baseSku:list')")
    public ResponseEntity<Object> getBaseSku(BaseSku baseSku,String shopCode){
        return new ResponseEntity<>(baseSkuService.getProductIdByBarcode(baseSku,shopCode),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改basesku")
    @ApiOperation("修改basesku")
    @PreAuthorize("@el.check('baseSku:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody BaseSku resources){
        baseSkuService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除basesku")
    @ApiOperation("删除basesku")
    @PreAuthorize("@el.check('baseSku:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        baseSkuService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("上传SKU")
    @RequestMapping(value = "uploadSku")
    @PreAuthorize("@el.check('baseSku:uploadSku')")
    @Log("上传SKU")
    public ResponseEntity<Object> uploadSku(@RequestParam("file") MultipartFile file, @RequestParam("name") String name){
        List<Map<String, Object>> maps = FileUtils.importMapExcel(file);
        if(StringUtils.equals("dy", name)) {
            baseSkuService.uploadDYSku(maps);
        }else if(StringUtils.equals("ruoYuChen", name)) {
            baseSkuService.uploadRYCSku(maps);
        }else if(StringUtils.equals("sku", name)) {
            baseSkuService.uploadSku(maps);
        }else if (StringUtils.equals("size",name)){
            baseSkuService.uploadSize(maps);
        }else if (StringUtils.equals("gift",name)){
            baseSkuService.uploadGift(maps);
        }else if (StringUtils.equals("CN",name)){
            baseSkuService.uploadCNGoodsNo(maps);
        }else {
            baseSkuService.uploadGoodsNo(maps);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("根据条码和类型模糊查询sku")
    @ApiOperation("根据条码和类型模糊查询sku")
    @GetMapping(value = "/query-by-type-and-bar-code-like")
    public ResponseEntity<Object> queryByTypeAndBarCodeLike(String barCode, String type, Long shopId){
        List<BaseSku> baseSkus = baseSkuService.queryByTypeAndBarCodeLike(type, barCode, shopId);
        return new ResponseEntity<>(baseSkus, HttpStatus.OK);
    }

    @Log("推送Wms")
    @ApiOperation("推送Wms")
    @GetMapping(value = "/push-wms")
    @PreAuthorize("@el.check('baseSku:pushWms')")
    public ResponseEntity<Object> pushWms(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                baseSkuService.pushWms(Long.valueOf(idArr[i]));
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("同步尺寸信息")
    @ApiOperation("同步尺寸信息")
    @GetMapping(value = "/sync-size")
    @PreAuthorize("@el.check('baseSku:pushWms')")
    public ResponseEntity<Object> syncSize(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                baseSkuService.syncSize(Long.valueOf(idArr[i]));
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("商品备案审核通过")
    @ApiOperation("商品备案审核通过")
    @GetMapping(value = "/audit-pass")
    @PreAuthorize("@el.check('baseSku:auditPass')")
    public ResponseEntity<Object> auditPass(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                baseSkuService.auditPass(Long.valueOf(idArr[i]));
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("商品备案下发")
    @ApiOperation("商品备案下发")
    @AnonymousPostMapping("/push-sku")
    public Result pushSku(@RequestParam(value = "data")String data,
                         @RequestParam(value = "customersCode")String customersCode){
        if (StringUtil.isEmpty(data))
            return ResultUtils.getFail("data必填");
        if (StringUtil.isEmpty(customersCode))
            return ResultUtils.getFail("customersCode必填");
        CustomerKeyDto customerKeyDto = customerKeyService.findByCustCode(customersCode);
        if (customerKeyDto == null)
            return ResultUtils.getFail("没有分配秘钥，请联系富立技术人员");
        try {
            String decData = SecureUtils.decryptDesHex(data,customerKeyDto.getSignKey());
            baseSkuService.addSku(decData, customerKeyDto.getCustomerId());
            return ResultUtils.getSuccess();
        }catch (Exception e) {
            return ResultUtils.getFail(e.getMessage());
        }
    }

    @Log("商品禁用和解禁")
    @ApiOperation("商品禁用和解禁")
    @PostMapping(value = "/ban-sku")
    @PreAuthorize("@el.check('baseSku:ban')")
    public ResponseEntity<Object> BanSku(String ids,int opt){
        Map<String,Object>map=new HashMap<>();
        map.put("msg","成功");
        map.put("code",200);
        try {
            baseSkuService.banOrUnbanSku(ids,opt);
        }catch (Exception e){
            e.printStackTrace();
            map.put("msg",e.getMessage()==null?"Null":e.getMessage());
            map.put("code",400);
        }
        return new ResponseEntity<>(map,HttpStatus.OK);
    }



}
