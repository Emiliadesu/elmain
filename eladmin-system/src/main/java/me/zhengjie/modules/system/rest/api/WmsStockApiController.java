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
package me.zhengjie.modules.system.rest.api;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.annotation.rest.AnonymousPostMapping;
import me.zhengjie.domain.*;
import me.zhengjie.entity.Result;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.ByteDanceQueryCriteria;
import me.zhengjie.service.dto.CustomerKeyDto;
import me.zhengjie.utils.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;

/**
* @author Zheng Jie
* @date 2019-03-25
*/
@RestController
@RequiredArgsConstructor
@Api(tags = "系统：API")
@RequestMapping("/api/wmsStock")
public class WmsStockApiController {
    private final ByteDanceService byteDanceService;
    private final WmsInstockService wmsInstockService;
    private final CustomerKeyService customerKeyService;
    private final WmsOutstockService wmsOutstockService;
    private final StockInTollyService stockInTollyService;
    private final StockOutTollyService stockOutTollyService;
    private final StockService stockService;
    private final StockConvertService stockConvertService;
    private final DetailTypeService detailTypeService;
    @Log("接口请求测试")
    @ApiOperation("接口请求测试")
    @AnonymousPostMapping(value = "/test")
    public Object test(ByteDanceQueryCriteria criteria, Pageable pageable) throws Exception {
        Map<String,Object>pageData=byteDanceService.queryAll(criteria,pageable);
        Map<String,Object>map=new HashMap<>();
        map.put("success",true);
        map.put("msg","请求成功");
        map.put("code",0);
        map.put("data",pageData);

        return map;
    }

    /**
     * 入库通知单
     * @param data
     * @param customersCode
     * @return
     */
    @Log("入库通知单下发")
    @ApiOperation("入库通知单下发")
    @AnonymousPostMapping("/add-asn")
    public Result addAsn(@RequestParam("data")String data,
                         @RequestParam("customersCode")String customersCode){
        String decData=null;
        try {
            decData=desData(data,customersCode);
        }catch (Exception e){
            return ResultUtils.getFail(e.getMessage());
        }
        try {
            System.out.println(decData);
            boolean flag=wmsInstockService.addAsn(decData,customersCode);
            if (flag)
                return ResultUtils.getSuccess("入库单下发成功");
            return ResultUtils.getFail("入库单下发失败");
        }catch (Exception e){
            return ResultUtils.getFail("入库单通知下发失败："+e.getMessage());
        }
    }

    @Log("出库通知单下发")
    @ApiOperation("出库通知单下发")
    @AnonymousPostMapping("/add-outbound")
    public Result addOutbound(@RequestParam("data")String data,
                              @RequestParam("customersCode")String customersCode){
        String decData=null;
        System.out.println(customersCode);
        System.out.println(data);
        try {
            decData=desData(data,customersCode);
        }catch (Exception e){
            return ResultUtils.getFail(e.getMessage());
        }
        try {
            boolean flag=wmsOutstockService.addOutBound(decData,customersCode);
            if (flag)
                return ResultUtils.getSuccess("出库单下发成功");
            return ResultUtils.getFail("出库单下发失败");
        }catch (Exception e){
            return ResultUtils.getFail("出库单通知下发失败："+e.getMessage());
        }
    }
    @Log("通知单取消")
    @ApiOperation("通知单取消")
    @AnonymousPostMapping("/cancel-doc")
    public Result cancelNotice(@RequestParam("data")String data,
                               @RequestParam("customersCode")String customersCode){
        String decData=null;
        try {
            decData=desData(data,customersCode);
        }catch (Exception e){
            return ResultUtils.getFail(e.getMessage());
        }
        JSONObject body=null;
        try {
            body=new JSONObject(decData);
        }catch (Exception e){
            return ResultUtils.getFail("json数据解析失败:"+e.getMessage());
        }
        String docType=body.getStr("orderType");
        try {
            if (StringUtil.equals("0",docType)){
                boolean flag=wmsInstockService.cancel(body.getStr("orderSn"),body.getStr("tenantCode"),customersCode,body.getStr("reason"));
                if (flag)
                    return ResultUtils.getSuccess("取消成功");
                return ResultUtils.getFail("取消失败");
            }else if(StringUtil.equals("1",docType)) {
                boolean flag=wmsOutstockService.cancel(body.getStr("orderSn"),customersCode,body.getStr("tenantCode"),body.getStr("reason"));
                if (flag)
                    return ResultUtils.getSuccess("取消成功");
                return ResultUtils.getFail("取消失败");
            }
            return ResultUtils.getFail("orderType不符合规范");
        }catch (Exception e){
            WmsStockLog wmsStockLog=new WmsStockLog();
            wmsStockLog.setOperationUser(customersCode);
            wmsStockLog.setOperationTime(new Timestamp(System.currentTimeMillis()));
            wmsStockLog.setType(docType);
            wmsStockLog.setStatusText(StringUtil.equals("1",docType)?"出库":"入库"+"通知单取消失败");
            wmsStockLog.setRemark(e.getMessage());
            wmsStockLog.setStatus(StringUtil.equals("1",docType)?"-1":"01");
            wmsStockLog.setResponse(new JSONObject(ResultUtils.getFail("取消失败："+e.getMessage())).toString());
            wmsStockLog.setRequest(decData);
            wmsStockLog.setIsSuccess("0");
            wmsStockLog.setOrderSn(body.getStr("orderSn"));
            return ResultUtils.getFail("取消失败："+e.getMessage());
        }
    }
    private String desData(String data,String customersCode){
        if (StringUtil.isEmpty(data)){
            throw new BadRequestException("data必填");
        }
        if (StringUtil.isEmpty(customersCode)){
            throw new BadRequestException("customerCode必填");
        }
        CustomerKeyDto customerKeyDto=customerKeyService.findByCustCode(customersCode);
        if (StringUtil.isEmpty(customerKeyDto.getSignKey())){
            throw new BadRequestException("没有分配秘钥，请联系富立技术人员");
        }
        String dec=null;
        try {
            dec= SecureUtils.decryptDesHex(data,customerKeyDto.getSignKey());
        }catch (Exception e){
            e.printStackTrace();
            throw new BadRequestException("解密失败："+e.getMessage());
        }
        return dec;
    }

    /**
     * 入库通知单
     * @param data
     * @param customersCode
     * @return
     */
    @Log("sku商仓映射添加")
    @ApiOperation("sku商仓映射添加")
    @AnonymousPostMapping("/add-sku-mapping")
    public Result addSkuMapping(@RequestParam("data")String data,
                         @RequestParam("customersCode")String customersCode){
        /*String decData=null;
        try {
            decData=desData(data,customersCode);
        }catch (Exception e){
            return ResultUtils.getFail(e.getMessage());
        }
        try {
            System.out.println(decData);
            boolean flag=skuMapService
            if (flag)
                return ResultUtils.getSuccess("入库单下发成功");
            return ResultUtils.getFail("入库单下发失败");
        }catch (Exception e){
            return ResultUtils.getFail("入库单通知下发失败："+e.getMessage());
        }*/
        return null;
    }

    /**
     * 入库审核通知
     * @param data
     * @param customersCode
     * @return
     */
    @Log("入库审核通知")
    @ApiOperation("入库审核通知")
    @AnonymousPostMapping("/audit-tally")
    public Map auditTally(@RequestParam("data")String data,
                                @RequestParam("customersCode")String customersCode){
        String decData=null;
        Map<String,Object> resMap=new HashMap();
        Result result;
        try {
            decData=desData(data,customersCode);
        }catch (Exception e){
            result=ResultUtils.getFail(e.getMessage());
            resultToMap(resMap, result);
            return resMap;
        }
        try {
            JSONObject jsData=new JSONObject(decData);
            String docNo=jsData.getStr("docCode");
            WmsInstock wmsInstock=wmsInstockService.findByOrderSn(docNo);
            if (wmsInstock==null){
                result=ResultUtils.getFail("找不到入库单号："+docNo);
                resultToMap(resMap, result);
                return resMap;}
            boolean flag=stockInTollyService.auditTally(jsData,wmsInstock,customersCode);
            if (flag){
                result= ResultUtils.getSuccess("成功");
                resultToMap(resMap, result);
            }else{
                result=ResultUtils.getFail("失败");
                resultToMap(resMap, result);
            }
            return resMap;
        }catch (Exception e){
            result=ResultUtils.getFail("失败："+e.getMessage());
            resultToMap(resMap, result);
            return resMap;
        }
    }

    /**
     * "出库流程控制"
     * @param data
     * @param customersCode
     * @return
     */
    @Log("出库流程控制")
    @ApiOperation("出库流程控制")
    @AnonymousPostMapping("/audit-out-tally")
    public Map auditOutTally(@RequestParam("data")String data,
                             @RequestParam("customersCode")String customersCode){
        String decData=null;
        Map<String,Object> resMap=new HashMap();
        try {
            decData=desData(data,customersCode);
        }catch (Exception e){
            Result result=ResultUtils.getFail(e.getMessage());
            resultToMap(resMap, result);
            return resMap;
        }
        JSONObject jsData=new JSONObject(decData);
        String docNo=jsData.getStr("docCode");
        WmsOutstock wmsOutstock=wmsOutstockService.findByOutOrderSn(docNo);
        if (wmsOutstock==null){
            Result result=ResultUtils.getFail("出库单据"+docNo+"不存在");
            resultToMap(resMap, result);
            return resMap;
        }
        try {
            boolean flag=stockOutTollyService.auditTally(decData,wmsOutstock,customersCode);
            if (flag){
                Result result=ResultUtils.getSuccess("成功");
                resultToMap(resMap, result);
                return resMap;
            }
            Result result=ResultUtils.getFail("失败");
            resultToMap(resMap, result);
            return resMap;
        }catch (Exception e){
            Result result=ResultUtils.getFail("失败："+e.getMessage());
            resultToMap(resMap, result);
            return resMap;
        }
    }
    @Log("堆库分页查询")
    @ApiOperation("堆库分页查询")
    @AnonymousPostMapping("/stack-stock-query")
    public Map stackStockQuery(@RequestParam("data")String data,
                              @RequestParam("customersCode")String customersCode){
        String decData=null;
        Map<String,Object> resMap=new HashMap();
        try {
            decData=desData(data,customersCode);
        }catch (Exception e){
            Result result = ResultUtils.getFail(e.getMessage());
            resultToMap(resMap, result);
            return resMap;
        }
        try {
            JSONObject resp=stockService.queryStock(decData,customersCode);
            Result result =  ResultUtils.getSuccess();
            resultToMap(resMap, result);
            resMap.put("batchStockStatisticsVos",resp.get("item"));
            resMap.put("totalCount",resp.get("totalCount"));
            resMap.put("pageSize",resp.get("pageSize"));
            resMap.put("pageCount",resp.get("pageCount"));
            return resMap;
        }catch (Exception e){
            Result result =  ResultUtils.getSuccess();
            resultToMap(resMap, result);
            resMap.put("batchStockStatisticsVos",new ArrayList<>());
            resMap.put("totalCount",0);
            resMap.put("pageSize",0);
            resMap.put("pageCount",0);
            resMap.put("errMsg",e.getMessage());
            return resMap;
        }
    }
    @Log("SAP库存查询")
    @ApiOperation("SAP库存查询")
    @AnonymousPostMapping("/sap-stock-query")
    public Map sapStockQuery(@RequestParam("data")String data,
                             @RequestParam("customersCode")String customersCode){
        String decData=null;
        Map<String,Object> resMap=new HashMap();
        try {
            decData=desData(data,customersCode);
        }catch (Exception e){
            Result result = ResultUtils.getFail(e.getMessage());
            resultToMap(resMap, result);
            return resMap;
        }
        try {
            JSONObject resp=stockService.querySapStock(decData,customersCode);
            Result result =  ResultUtils.getSuccess();
            resultToMap(resMap, result);
            resMap.put("batchStockVos",resp.get("item"));
            return resMap;
        }catch (Exception e){
            Result result =  ResultUtils.getFail("SAP库存查询失败："+e.getMessage());
            resultToMap(resMap, result);
            return resMap;
        }
    }

    private void resultToMap(Map<String, Object> resMap, Result result) {
        resMap.put("flag",result.isSuccess()?"success":"failure");
        resMap.put("code",result.getCode());
        resMap.put("message",result.getMsg());
    }

    @Log("转移单接入")
    @ApiOperation("转移单接入")
    @AnonymousPostMapping("/stock-convert-add")
    public Map dubheWmsStockConvertAdd(@RequestParam("data")String data,
                          @RequestParam("customersCode")String customersCode){
        String decData=null;
        Map<String,Object> resMap=new HashMap();
        Result result;
        try {
            decData=desData(data,customersCode);
        }catch (Exception e){
            result=ResultUtils.getFail(e.getMessage());
            resultToMap(resMap, result);
            return resMap;
        }
        try {
            boolean flag=stockConvertService.add(decData,customersCode);
            if (flag){
                result=ResultUtils.getSuccess();
                resultToMap(resMap, result);
            }else {
                result=ResultUtils.getSuccess();
                resultToMap(resMap, result);
            }
            return resMap;
        }catch (Exception e){
            result=ResultUtils.getFail("失败："+e.getMessage());
            resultToMap(resMap, result);
            return resMap;
        }
    }

    @Log("细分类型变更")
    @ApiOperation("细分类型变更")
    @AnonymousPostMapping("/subtype-update")
    public Map subtypeUpdate(@RequestParam("data")String data,
                                       @RequestParam("customersCode")String customersCode){
        String decData=null;
        Map<String,Object> resMap=new HashMap();
        Result result;
        try {
            decData=desData(data,customersCode);
        }catch (Exception e){
            result=ResultUtils.getFail(e.getMessage());
            resultToMap(resMap, result);
            return resMap;
        }
        try {
            DetailType detailType=com.alibaba.fastjson.JSONObject.parseObject(decData,DetailType.class);
            boolean flag=detailTypeService.add(detailType,customersCode);
            if (flag){
                result=ResultUtils.getSuccess();
                resultToMap(resMap, result);
            }else {
                result=ResultUtils.getSuccess();
                resultToMap(resMap, result);
            }
            return resMap;
        }catch (Exception e){
            result=ResultUtils.getFail("失败："+e.getMessage());
            resultToMap(resMap, result);
            return resMap;
        }
    }
}
