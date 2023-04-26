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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.domain.*;
import me.zhengjie.entity.Result;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.*;
import me.zhengjie.support.WmsSupport;
import me.zhengjie.utils.*;
import me.zhengjie.utils.enums.CBReturnOrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author luob
* @date 2021-04-14
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "orderreturn管理")
@RequestMapping("/api/OrderReturn")
public class OrderReturnController {

    private final OrderReturnService OrderReturnService;

    @Autowired
    private DouyinService douyinService;

    @Autowired
    private WmsSupport wmsSupport;

    @Autowired
    private UserCustomerService userCustomerService;

    @Autowired
    private CustomerKeyService customerKeyService;

    @Autowired
    private ShopInfoService shopInfoService;


    @Log("清关申报")
    @ApiOperation("清关申报")
    @GetMapping(value = "/clear")
    @PreAuthorize("@el.check('OrderReturn:clear')")
    public ResponseEntity<Object> clear(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                OrderReturnService.declare(idArr[i]);
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + idArr[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("上架完成")
    @ApiOperation("上架完成")
    @GetMapping(value = "/ground")
    @PreAuthorize("@el.check('OrderReturn:ground')")
    public ResponseEntity<Object> ground(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                OrderReturnService.ground(idArr[i]);
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + idArr[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("回传收货")
    @ApiOperation("回传收货")
    @GetMapping(value = "/confirm-return-book")
    @PreAuthorize("@el.check('OrderReturn:ground')")
    public ResponseEntity<Object> confirmReturnBook(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                douyinService.confirmReturnBook(idArr[i]);
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + idArr[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("回传质检通过")
    @ApiOperation("回传质检通过")
    @GetMapping(value = "/confirm-return-check")
    @PreAuthorize("@el.check('OrderReturn:ground')")
    public ResponseEntity<Object> confirmReturnCheck(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                douyinService.confirmReturnCheck(idArr[i]);
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + idArr[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("回传质检异常")
    @ApiOperation("回传质检异常")
    @GetMapping(value = "/confirm-return-check-err")
    @PreAuthorize("@el.check('OrderReturn:ground')")
    public ResponseEntity<Object> confirmReturnCheckErr(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                douyinService.confirmReturnCheckErr(idArr[i]);
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + idArr[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("强制申报")
    @ApiOperation("强制申报")
    @GetMapping(value = "/force-dec")
    @PreAuthorize("@el.check('OrderReturn:ground')")
    public ResponseEntity<Object> forceDec(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                OrderReturnService.forceDec(Long.valueOf(idArr[i]));
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + idArr[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("回传申报开始")
    @ApiOperation("回传申报开始")
    @GetMapping(value = "/confirm-return-dec-start")
    @PreAuthorize("@el.check('OrderReturn:ground')")
    public ResponseEntity<Object> confirmReturnDecStart(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                douyinService.confirmReturnDecStart(idArr[i]);
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + idArr[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("回传申报异常")
    @ApiOperation("回传申报异常")
    @GetMapping(value = "/confirm-return-dec-err")
    @PreAuthorize("@el.check('OrderReturn:ground')")
    public ResponseEntity<Object> confirmReturnDecErr(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                OrderReturn orderReturn = OrderReturnService.queryById(Long.valueOf(idArr[i]));
                if (StringUtils.equals("1", orderReturn.getOrderSource())) {
                    douyinService.confirmReturnDecErr(idArr[i]);
                }else {
                    orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_900.getCode());// 关单
                    orderReturn.setCloseTime(new Timestamp(System.currentTimeMillis()));
                    OrderReturnService.update(orderReturn);
                }

                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + idArr[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("回传申报完成")
    @ApiOperation("回传申报完成")
    @GetMapping(value = "/confirm-return-dec-end")
    @PreAuthorize("@el.check('OrderReturn:ground')")
    public ResponseEntity<Object> confirmReturnDecEnd(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                douyinService.confirmReturnDecEnd(idArr[i]);
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + idArr[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("回传保税仓上架")
    @ApiOperation("回传保税仓上架")
    @GetMapping(value = "/confirm-return-ground")
    @PreAuthorize("@el.check('OrderReturn:ground')")
    public ResponseEntity<Object> confirmReturnGround(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                douyinService.confirmReturnGround(idArr[i]);
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + idArr[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("回传理货完成")
    @ApiOperation("回传理货完成")
    @GetMapping(value = "/confirm-tally")
    @PreAuthorize("@el.check('OrderReturn:ground')")
    public ResponseEntity<Object> confirmTally(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                douyinService.confirmTally(idArr[i]);
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + idArr[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("回传退货仓上架")
    @ApiOperation("回传退货仓上架")
    @GetMapping(value = "/confirm-out-return-ground")
    @PreAuthorize("@el.check('OrderReturn:ground')")
    public ResponseEntity<Object> confirmOutReturnGround(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                douyinService.confirmOutReturnGround(idArr[i]);
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + idArr[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("退货登记")
    @ApiOperation("退货登记")
    @GetMapping(value = "/return-book")
    @PreAuthorize("@el.check('OrderReturn:returnBook')")
    public ResponseEntity<Object> returnBook(@Validated String mailNo){
        JSONObject result = new JSONObject();
        try {
            OrderReturn orderReturn = OrderReturnService.returnBook(mailNo);
            result.putOnce("orderReturn", orderReturn);
            result.putOnce("success", true);
            if (StringUtils.equals("0", orderReturn.getIsBorder())) {
                result.putOnce("msg", "非入区订单，请单独放置");
            }else {
                result.putOnce("msg", "登记成功");
            }
        }catch (Exception e) {
            e.printStackTrace();
            result.putOnce("success", false);
            result.putOnce("msg", e.getMessage());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("推送wms订单")
    @ApiOperation("推送wms订单")
    @GetMapping(value = "/push-wms-order")
    @PreAuthorize("@el.check('crossBorderOrder:confirmClearSucc')")
    public ResponseEntity<Object> pushWmsOrder(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                OrderReturn orderReturn = OrderReturnService.queryByIdWithDetails(Long.valueOf(idArr[i]));
                if (orderReturn.getStatus().intValue() != CBReturnOrderStatusEnum.STATUS_337.getCode().intValue())
                    throw new BadRequestException("此状态不能推送");
                if (!StringUtils.equals("1", orderReturn.getIsWave()))
                    throw new BadRequestException("未产生波次不能推送");
                wmsSupport.pushResturn(orderReturn);
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("刷新wms状态")
    @ApiOperation("刷新wms状态")
    @GetMapping(value = "/update-wms-status")
    @PreAuthorize("@el.check('crossBorderOrder:confirmClearSucc')")
    public ResponseEntity<Object> updateWMsStatus(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                OrderReturnService.updateWMsStatus(Long.valueOf(idArr[i]));
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @Log("生成退货单")
    @ApiOperation("生成退货单")
    @GetMapping(value = "/create-return")
    public ResponseEntity<Object> createReturn(@Validated Long orderId, @Validated String isBorder){
        OrderReturnService.createReturn(orderId, isBorder);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("退货质检")
    @ApiOperation("退货质检")
    @GetMapping(value = "/check-return")
    @PreAuthorize("@el.check('OrderReturn:check')")
    public ResponseEntity<Object> checkReturn(@Validated String mailNo){
        OrderReturn orderReturn = OrderReturnService.queryByMailNo(mailNo);
        if(orderReturn == null)
            throw new BadRequestException("单号不存在");
        if (orderReturn.getStatus().intValue() != CBReturnOrderStatusEnum.STATUS_310.getCode().intValue())
            throw new BadRequestException("当前状态不允许退货质检：" + orderReturn.getStatus());
        return new ResponseEntity<>(orderReturn, HttpStatus.OK);
    }

    @Log("退货商品扫描")
    @ApiOperation("退货商品扫描")
    @GetMapping(value = "/check-barcode")
    @PreAuthorize("@el.check('OrderReturn:check')")
    public ResponseEntity<Object> checkBarcode(@Validated String mailNo, @Validated String barcode){
        JSONObject result = new JSONObject();
        try {
            OrderReturnService.checkBarcode(mailNo, barcode);
            result.putOnce("success", true);
            result.putOnce("msg", "成功");
        }catch (Exception e) {
            result.putOnce("success", false);
            result.putOnce("msg", e.getMessage());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("提交质检")
    @ApiOperation("提交质检")
    @RequestMapping(value = "/check")
    @PreAuthorize("@el.check('OrderReturn:check')")
    public ResponseEntity<Object> check(@RequestParam(value = "mailNo") String mailNo,
                                        @RequestParam(value = "checkTyp") String checkTyp,
                                        @RequestBody() String goodsList){
        JSONObject result = new JSONObject();
        try {
            com.alibaba.fastjson.JSONObject obj = JSON.parseObject(goodsList);
            List<OrderReturnDetails> itemList = JSONArray.parseArray(obj.getString("goodsList"), OrderReturnDetails.class);
            OrderReturnService.check(checkTyp, mailNo, itemList);
            result.putOnce("success", true);
            result.putOnce("msg", "成功");
        }catch (Exception e) {
            e.printStackTrace();
            result.putOnce("success", false);
            result.putOnce("msg", e.getMessage());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/query-by-id")
    @Log("查询orderreturn")
    @ApiOperation("查询orderreturn")
    @PreAuthorize("@el.check('OrderReturn:list')")
    public ResponseEntity<Object> queryById(String id){
        return new ResponseEntity<>(OrderReturnService.queryByIdWithDetails(Long.valueOf(id)),HttpStatus.OK);
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('OrderReturn:list')")
    public void download(HttpServletResponse response, OrderReturnQueryCriteria criteria) throws IOException {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> shopIds = userCustomerService.queryShops(userId);
        if (CollectionUtils.isEmpty(criteria.getShopIds()) && CollectionUtils.isNotEmpty(shopIds)) {
            criteria.setShopIds(shopIds);
        }
        OrderReturnService.download(OrderReturnService.queryAll(criteria), response);
    }

    @Log("导出数据明细")
    @ApiOperation("导出数据明细")
    @GetMapping(value = "/download-details")
    @PreAuthorize("@el.check('OrderReturnDetails:list')")
    public void downloadDetails(HttpServletResponse response, OrderReturnQueryCriteria criteria) throws IOException {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> shopIds = userCustomerService.queryShops(userId);
        if (CollectionUtils.isEmpty(criteria.getShopIds()) && CollectionUtils.isNotEmpty(shopIds)) {
            criteria.setShopIds(shopIds);
        }
        OrderReturnService.downloadDetails(criteria, response);
    }

    @GetMapping
    @Log("查询orderreturn")
    @ApiOperation("查询orderreturn")
    @PreAuthorize("@el.check('OrderReturn:list')")
    public ResponseEntity<Object> query(OrderReturnQueryCriteria criteria, Pageable pageable){
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> shopIds = userCustomerService.queryShops(userId);
        if (CollectionUtils.isEmpty(criteria.getShopIds()) && CollectionUtils.isNotEmpty(shopIds)) {
            criteria.setShopIds(shopIds);
        }
        return new ResponseEntity<>(OrderReturnService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增orderreturn")
    @ApiOperation("新增orderreturn")
    @PreAuthorize("@el.check('OrderReturn:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody OrderReturn resources){
        return new ResponseEntity<>(OrderReturnService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改orderreturn")
    @ApiOperation("修改orderreturn")
    @PreAuthorize("@el.check('OrderReturn:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody OrderReturn resources){
        OrderReturnService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除orderreturn")
    @ApiOperation("删除orderreturn")
    @PreAuthorize("@el.check('OrderReturn:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        OrderReturnService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @AnonymousAccess
    @RequestMapping("/query-orders")
    @Log("接口分页查询退货信息")
    @ApiOperation("接口分页查询退货信息")
    public Result queryOrders(@RequestParam("data") String data,
                              @RequestParam("customersCode") String customersCode) {
        try {
            CustomerKeyDto custCode = customerKeyService.findByCustCode(customersCode);
            if (custCode == null)
                throw new BadRequestException("customersCode不存在或客户未配置key");
            String signKey = custCode.getSignKey();
            String originData = SecureUtils.decryptDesHex(data, signKey);
            JSONObject reqObject = JSONUtil.parseObj(originData);
            Date startTime = reqObject.getDate("startTime");
            Date endTime = reqObject.getDate("endTime");
            int pageNo = reqObject.getInt("pageNo");
            int pageSize = reqObject.getInt("pageSize");
            if (pageSize > 30) {
                return ResultUtils.getFail("页大小最大30");
            }
            OrderReturnQueryCriteria criteria = new OrderReturnQueryCriteria();
            String shopCode = reqObject.getStr("shopCode");
            if (StringUtil.isNotBlank(shopCode)) {
                ShopInfo shopInfo = shopInfoService.queryByShopCode(shopCode);
                if (shopInfo == null) {
                    return ResultUtils.getFail("shopCode不存在：" + shopCode);
                }
                List<Long> shopIds = new ArrayList<>();
                shopIds.add(shopInfo.getId());
                criteria.setShopIds(shopIds);
            }

            criteria.setCustomersId(custCode.getCustomerId());
            Pageable pageable = new PageRequest(pageNo, pageSize);
            List<Timestamp> createTime = new ArrayList<>();
            createTime.add(new Timestamp(startTime.getTime()));
            createTime.add(new Timestamp(endTime.getTime()));
            criteria.setCreateTime(createTime);
            Map<String, Object> map = OrderReturnService.queryOrders(criteria, pageable);
            return ResultUtils.getSuccess(map);
        }catch (Exception e) {
            return ResultUtils.getFail(e.getMessage());
        }

    }

    @AnonymousAccess
    @RequestMapping("/query-by-orderno")
    @Log("接口分页查询退货信息")
    @ApiOperation("接口分页查询退货信息")
    public Result queryByOrderNo(@RequestParam("data") String data,
                                 @RequestParam("customersCode") String customersCode) {
        try {
            CustomerKeyDto custCode = customerKeyService.findByCustCode(customersCode);
            if (custCode == null)
                throw new BadRequestException("customersCode不存在或客户未配置key");
            String signKey = custCode.getSignKey();
            String originData = SecureUtils.decryptDesHex(data, signKey);
            JSONObject reqObject = JSONUtil.parseObj(originData);
            String orderNo = reqObject.getStr("orderNo");
            CrossBorderOrderQueryCriteria criteria  = new CrossBorderOrderQueryCriteria();
            criteria.setCustomersId(custCode.getCustomerId());
            criteria.setOrderNo(orderNo);
            OrderReturn order = OrderReturnService.queryByOrderNoWithDetails(orderNo);

            OrderReturnOutDto orderReturnOutDto = new OrderReturnOutDto();
            BeanUtil.copyProperties(order,orderReturnOutDto, CopyOptions.create().setIgnoreNullValue(true));
            List<OrderReturnDetails> list = order.getItemList();
            List<OrderReturnDetailsOutDto> detailsDTOS = new ArrayList<>();
            for (OrderReturnDetails details : list) {
                OrderReturnDetailsOutDto detailsDTO = new OrderReturnDetailsOutDto();
                BeanUtil.copyProperties(details,detailsDTO, CopyOptions.create().setIgnoreNullValue(true));
                detailsDTOS.add(detailsDTO);
            }
            ShopInfoDto shopInfoDto = shopInfoService.queryById(order.getShopId());
            orderReturnOutDto.setShopCode(shopInfoDto.getCode());
            orderReturnOutDto.setShopName(shopInfoDto.getName());
            orderReturnOutDto.setItemList(detailsDTOS);
            return ResultUtils.getSuccess(orderReturnOutDto);
        }catch (Exception e) {
            return ResultUtils.getFail(e.getMessage());
        }

    }

    @ApiOperation("上传4Pl单")
    @RequestMapping(value = "upload-4pl")
    @Log("上传logistics")
    public ResponseEntity<Object> upload4Pl(@RequestParam("file") MultipartFile file, @RequestParam("name") String name){
        List<Map<String, Object>> maps = FileUtils.importMapExcel(file);
        OrderReturnService.upload4Pl(maps);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}