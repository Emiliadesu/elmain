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
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.domain.*;
import me.zhengjie.entity.Result;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.mq.CBOrderProducer;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.*;
import me.zhengjie.support.MailSupport;
import me.zhengjie.support.WmsSupport;
import me.zhengjie.support.aikucun.AikucunSupport;
import me.zhengjie.support.chinaPost.EMSSupport;
import me.zhengjie.support.douyin.CBOrderReturnChild;
import me.zhengjie.support.douyin.CBOrderReturnMain;
import me.zhengjie.support.meituan.MeiTuanSupport;
import me.zhengjie.support.moGuJie.MoGuJieSupport;
import me.zhengjie.support.oms.OrderChild;
import me.zhengjie.support.oms.OrderMain;
import me.zhengjie.support.pdd.PDDSupport;
import me.zhengjie.support.ymatou.YmatouSupport;
import me.zhengjie.support.youzan.YouZanSupport;
import me.zhengjie.utils.*;
import me.zhengjie.utils.constant.MsgType;
import me.zhengjie.utils.constant.PlatformConstant;
import me.zhengjie.utils.enums.CBOrderStatusEnum;
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
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author luob
* @date 2021-03-25
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "crossBorderOrder管理")
@RequestMapping("/api/crossBorderOrder")
public class CrossBorderOrderController {

    private final CrossBorderOrderService crossBorderOrderService;

    private final CrossBorderOrderDetailsService crossBorderOrderDetailsService;

    @Autowired
    private DouyinService douyinService;

    @Autowired
    private UserCustomerService userCustomerService;

    @Autowired
    private CustomerKeyService customerKeyService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private PddOrderService pddOrderService;

    @Autowired
    private ShopTokenService shopTokenService;

    @Autowired
    private YouZanOrderService youZanOrderService;

    @Autowired
    private QSService qsService;

    @Autowired
    private YmatouService ymatouService;

    @Autowired
    private MoGuJieService moGuJieService;

    @Autowired
    private DouyinMailMarkService douyinMailMarkService;

    @Autowired
    private MeituanService meituanService;

    @Autowired
    private WmsSupport wmsSupport;

    @Autowired
    private PlatformService platformService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private MqCBOrderCommonService mqCBOrderCommonService;

    @Autowired
    private LogisticsInfoService logisticsInfoService;

    @Autowired
    private EMSService emsService;

    @Autowired
    private CBOrderProducer cbOrderProducer;

    @Autowired
    private GuoMeiService guoMeiService;

    @Autowired
    private JackYunService jackYunService;

    @Autowired
    private AikucunService aikucunService;

    @Autowired
    private DewuDeclarePushService dewuDeclarePushService;

    @Autowired
    private CaiNiaoService caiNiaoService;

    @Log("拉单")
    @ApiOperation("拉单")
    @PostMapping(value = "/pullOrder")
    @PreAuthorize("@el.check('crossBorderOrder:pullOrder')")
    public ResponseEntity<Object> pullOrder(String orderNo, String shopId,@RequestBody(required = false) List<String>createTime){
        if (StringUtil.equalsIgnoreCase("null",orderNo))
            orderNo=null;
        if (StringUtil.isBlank(orderNo)&& CollectionUtil.isEmpty(createTime))
            throw new BadRequestException("订单号和订单创建时间至少有一个不能为空");
        String resMsg = pullOrderExt(orderNo, shopId,createTime);
        JSONObject result = new JSONObject();
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private String pullOrderExt(String orderNo, String shopId,List<String>createTime) {
        if (StringUtil.isBlank(orderNo)){
            crossBorderOrderService.pullOrderByTimeRange(DateUtils.parseDateTime(createTime.get(0)),DateUtils.parseDateTime(createTime.get(1)),shopId);
            return "成功";
        }else {
            CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(orderNo);
            // 如果当前订单平台状态为102货100，那么就删除当前订单，重新拉取
            if (order != null && (StringUtil.equals(order.getUpStatus(), "102")
                    || StringUtil.equals(order.getUpStatus(), "100"))) {
                crossBorderOrderService.deleteAll(new Long[]{order.getId()});
            }
            if (order != null) {
                throw new BadRequestException("订单已存在，请勿重复拉取");
            }
            String resMsg;
            ShopInfo shopInfo = shopInfoService.findById(Long.parseLong(shopId));
            try {
                if (StringUtil.equals(shopInfo.getPlatformCode(), "PDD")) {
                    ShopToken shopToken = shopTokenService.queryByShopId(shopInfo.getId());
                    pddOrderService.pullOrderByOrderSn(new String[]{orderNo}, shopToken);
                    resMsg = "拉取成功";
                } else if (StringUtil.equals(shopInfo.getPlatformCode(), "DY")) {
                    douyinService.pullOrderById(shopId, orderNo);
                    resMsg = "拉取成功";
                } else if (StringUtil.equals(shopInfo.getPlatformCode(), "YZ")) {
                    ShopToken shopToken = shopTokenService.queryByShopId(shopInfo.getId());
                    youZanOrderService.pullOrderByOrderNo(new String[]{orderNo}, shopToken);
                    resMsg = "拉取成功";
                } else if (StringUtil.equals(shopInfo.getPlatformCode(), "MGJ")) {
                    ShopToken shopToken = shopTokenService.queryByShopId(shopInfo.getId());
                    moGuJieService.pullOrderByOrderNo(new String[]{orderNo}, shopToken);
                    resMsg = "拉取成功";
                } else if (StringUtil.equals(shopInfo.getPlatformCode(), "Ymatou")) {
                    ShopToken shopToken = shopTokenService.queryByShopId(shopInfo.getId());
                    ymatouService.pullOrderByOrderNo(new String[]{orderNo}, shopToken);
                    resMsg = "拉取成功";
                }else {
                    resMsg = "拉取失败";
                }
            } catch (Exception e) {
                resMsg = e.getMessage();
            }
            return resMsg;
        }
    }

    @Log("批量拉单")
    @ApiOperation("批量拉单")
    @PostMapping(value = "/pullOrderBatch")
    @PreAuthorize("@el.check('crossBorderOrder:pullOrder')")
    public ResponseEntity<Object> pullOrderBatch(@RequestBody String[] orderNos, String shopId){
        if (orderNos==null)
            throw new BadRequestException("请输入订单号");
        StringBuilder resMsg=new StringBuilder();
        for (String orderNo : orderNos) {
            try {
                String msg=pullOrderExt(orderNo,shopId,null);
                resMsg.append("订单号:").append(orderNo).append(msg).append("\r\n");
            }catch (Exception e){
                resMsg.append("订单号:").append(orderNo).append(e.getMessage()).append("\r\n");
            }
        }
        JSONObject result = new JSONObject();
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("清关申报")
    @ApiOperation("清关申报")
    @GetMapping(value = "/clear")
    @PreAuthorize("@el.check('crossBorderOrder:clear')")
    public ResponseEntity<Object> clear(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                crossBorderOrderService.declare(idArr[i]);
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + idArr[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("刷新申报单状态")
    @ApiOperation("刷新申报单状态")
    @GetMapping(value = "/refresh-clear-status")
    @PreAuthorize("@el.check('crossBorderOrder:refreshClearStatus')")
    public ResponseEntity<Object> refreshClearStatus(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                crossBorderOrderService.refreshClearStatus(idArr[i]);
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + idArr[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("刷新WMS状态")
    @ApiOperation("刷新WMS状态")
    @GetMapping(value = "/refresh-wms-status")
    @PreAuthorize("@el.check('crossBorderOrder:refreshWmsStatus')")
    public ResponseEntity<Object> refreshWmsStatus(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                crossBorderOrderService.refreshWmsStatus(idArr[i]);
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + idArr[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }



    @Log("回传接单")
    @ApiOperation("回传接单")
    @GetMapping(value = "/confirm-order")
    @PreAuthorize("@el.check('crossBorderOrder:recover')")
    public ResponseEntity<Object> confirmOrder(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
//                CrossBorderOrder order = crossBorderOrderService.queryByIdWithDetails(Long.valueOf(idArr[i]));
//                sfgjSupport.getMail(order);
                //mqCBOrderCommonService.confirmOrder(idArr[i]);
                cbOrderProducer.send(
                        MsgType.CB_ORDER_215,
                        idArr[i],
                        idArr[i]
                );
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + idArr[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("推送预售")
    @ApiOperation("推送预售")
    @GetMapping(value = "/push-pre-sell")
    @PreAuthorize("@el.check('crossBorderOrder:print')")
    public ResponseEntity<Object> pushPreSell(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                cbOrderProducer.send(
                        MsgType.CB_ORDER_235,
                        idArr[i],
                        idArr[i]
                );
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + idArr[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("回传拣货开始")
    @ApiOperation("回传拣货开始")
    @GetMapping(value = "/confirm-pick-start")
    @PreAuthorize("@el.check('crossBorderOrder:confirmOrder')")
    public ResponseEntity<Object> confirmPickStart(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                CrossBorderOrder order = crossBorderOrderService.queryById(Long.valueOf(idArr[i]));
                switch (order.getPlatformCode()) {
                    case "DY":
                        douyinService.confirmPickStart(order);
                        break;
                    case "GM":
                        guoMeiService.confirmPickStart(order);
                        break;
                }
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + idArr[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("回传拣货完成")
    @ApiOperation("回传拣货完成")
    @GetMapping(value = "/confirm-pick-end")
    @PreAuthorize("@el.check('crossBorderOrder:confirmOrder')")
    public ResponseEntity<Object> confirmPickEnd(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                CrossBorderOrder order = crossBorderOrderService.queryById(Long.valueOf(idArr[i]));
                switch (order.getPlatformCode()) {
                    case "DY":
                        douyinService.confirmPickEnd(order);
                        break;
                    case "GM":
                        guoMeiService.confirmPickEnd(order);
                        break;
                }

                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + idArr[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("回传打包")
    @ApiOperation("回传打包")
    @GetMapping(value = "/confirm-pack")
    @PreAuthorize("@el.check('crossBorderOrder:confirmOrder')")
    public ResponseEntity<Object> confirmPack(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                CrossBorderOrder order = crossBorderOrderService.queryById(Long.valueOf(idArr[i]));
                switch (order.getPlatformCode()) {
                    case "DY":
                        douyinService.confirmPack(order);
                        break;
                    case "GM":
                        guoMeiService.confirmPack(order);
                        break;
                }
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + idArr[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("清关开始回传")
    @ApiOperation("清关开始回传")
    @GetMapping(value = "/confirm-clear-start")
    @PreAuthorize("@el.check('crossBorderOrder:confirmClearStart')")
    public ResponseEntity<Object> confirmClearStart(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                CrossBorderOrder order = crossBorderOrderService.queryById(Long.valueOf(idArr[i]));
                switch (order.getPlatformCode()){
                    case "DY":
                        douyinService.confirmClearStart(order);
                        break;
                    case "PDD":
                        pddOrderService.confirmClearStart(order);
                        break;
                    case "GM":
                        guoMeiService.confirmClearStart(order);
                        break;
                    case "YZ":
                    case "MGJ":
                    case "Ymatou":
                    case "MeiTuan":
                        order.setStatus(CBOrderStatusEnum.STATUS_225.getCode());
                        crossBorderOrderService.update(order);
                        break;
                     default:
                            omsService.confirmClearStart(order);
                }
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("清关异常回传")
    @ApiOperation("清关异常回传")
    @GetMapping(value = "/confirm-dec-err")
    @PreAuthorize("@el.check('crossBorderOrder:confirmClearSucc')")
    public ResponseEntity<Object> confirmDecErr(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                CrossBorderOrder order = crossBorderOrderService.queryById(Long.valueOf(idArr[i]));
                switch (order.getPlatformCode()){
                    case "DY":
                        douyinService.confirmClearErr(String.valueOf(idArr[i]));
                        break;
                    case "QS":
                        qsService.confirmClearErr(order);
                        break;
                    case "GM":
                        guoMeiService.confirmClearErr(order.getOrderNo());
                }
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("回传接单异常")
    @ApiOperation("回传接单异常")
    @GetMapping(value = "/confirm-order-err")
    @PreAuthorize("@el.check('crossBorderOrder:confirmClearSucc')")
    public ResponseEntity<Object> confirmOrderErr(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                CrossBorderOrder order = crossBorderOrderService.queryById(Long.valueOf(idArr[i]));
                douyinService.confirmOrderErr(order);
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("更新WMS时间")
    @ApiOperation("更新WMS时间")
    @GetMapping(value = "/update-wms-time")
    @PreAuthorize("@el.check('crossBorderOrder:confirmClearSucc')")
    public ResponseEntity<Object> updateWmsTime(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                crossBorderOrderService.updateWmsOrderTime(idArr[i]);
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
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
        JSONObject stack = new JSONObject();
        for (int i = 0; i < idArr.length; i++) {
            try {
                crossBorderOrderService.pushWmsOrder(idArr[i]);
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + "报错：" + e.getMessage() + ",";
                stack.putOnce(i+"",StringUtil.exceptionStackInfoToString(e));
            }
        }
        result.putOnce("resMsg", resMsg);
        result.putOnce("stack",stack);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("恢复wms订单")
    @ApiOperation("恢复wms订单")
    @GetMapping(value = "/recover-wms-order")
    @PreAuthorize("@el.check('crossBorderOrder:recover')")
    public ResponseEntity<Object> recoverWmsOrder(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                CrossBorderOrder order = crossBorderOrderService.queryById(Long.valueOf(idArr[i]));
                if (StringUtils.isEmpty(order.getSoNo())) {
                    JSONObject wmsOrder = wmsSupport.querySo(order.getCrossBorderNo(), order.getDeclareNo()+"QX");
                    String soNo = wmsOrder.getJSONObject("header").getStr("orderno");
                    if (!StringUtils.equals(order.getSoNo(), soNo)) {
                        order.setSoNo(soNo);
                    }
                }
                if (StringUtils.isEmpty(order.getSoNo()))
                    throw new BadRequestException("SO单号不存在");
                if (!StringUtils.equals("3", order.getIsLock()))
                    throw new BadRequestException("订单未锁单取消确认，不能恢复");
                wmsSupport.recover(order.getSoNo());
                order.setIsLock("4");// 订单变回锁单恢复
                crossBorderOrderService.update(order);
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("获取运单")
    @ApiOperation("获取运单")
    @GetMapping(value = "/get-mail-no")
    @PreAuthorize("@el.check('crossBorderOrder:confirmClearSucc')")
    public ResponseEntity<Object> confirmMailNo(String ids, Long supplierId){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                CrossBorderOrder order1 = crossBorderOrderService.queryByIdWithDetails(Long.valueOf(idArr[i]));
                if (order1 == null)
                    throw new BadRequestException("无运单请求记录："+ order1.getOrderNo());
                if (StringUtils.equals(PlatformConstant.DY, order1.getPlatformCode())) {
                    DouyinMailMark douyinMailMark1 = douyinMailMarkService.queryByOrderNo(order1.getOrderNo());
                    if (douyinMailMark1 == null)
                        throw new BadRequestException("无抖音推送记录");
                    DouyinMailMark douyinMailMark = douyinMailMarkService.queryById(douyinMailMark1.getId());
                    CrossBorderOrder order = douyinMailMarkService.toOrder(douyinMailMark);
                    logisticsInfoService.getLogisticsByLogis(order, supplierId);
                    order1.setSupplierId(order.getSupplierId());
                    order1.setLogisticsCode(order.getLogisticsCode());
                    order1.setLogisticsName(order.getLogisticsName());
                    order1.setLogisticsNo(order.getLogisticsNo());
                    order1.setAddMark(order.getAddMark());
                    crossBorderOrderService.update(order1);
                    douyinMailMark.setSupplierId(String.valueOf(order.getSupplierId()));
                    douyinMailMark.setAddMark(order.getAddMark());
                    douyinMailMark.setLogisticsNo(order.getLogisticsNo());
                    douyinMailMarkService.update(douyinMailMark);
                    resMsg = resMsg + idArr[i] + "处理成功," ;
                }else {
                    logisticsInfoService.getLogisticsByLogis(order1, supplierId);
                    crossBorderOrderService.update(order1);
                }

            } catch (Exception e) {
                e.printStackTrace();
                resMsg = resMsg + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @Log("回传运单")
    @ApiOperation("回传运单")
    @GetMapping(value = "/confirm-mail-no")
    @PreAuthorize("@el.check('crossBorderOrder:confirmClearSucc')")
    public ResponseEntity<Object> confirmMailNo(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                CrossBorderOrder order = crossBorderOrderService.queryById(Long.valueOf(idArr[i]));
                if (order == null)
                    throw new BadRequestException("无运单请求记录："+ order.getOrderNo());
                DouyinMailMark douyinMailMark = douyinMailMarkService.queryByOrderNo(order.getOrderNo());
                douyinService.getMailNo(String.valueOf(douyinMailMark.getId()));
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                e.printStackTrace();
                resMsg = resMsg + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @Log("清关完成回传")
    @ApiOperation("清关完成回传")
    @GetMapping(value = "/confirm-clear-succ")
    @PreAuthorize("@el.check('crossBorderOrder:confirmClearSucc')")
    public ResponseEntity<Object> confirmClearSucc(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                CrossBorderOrder order = crossBorderOrderService.queryByIdWithDetails(Long.valueOf(idArr[i]));
//                emsService.getDecinfo(order);
                switch (order.getPlatformCode()){
                    case "DY":
                        douyinService.confirmClearSuccess(order);
                        break;
                    case "PDD":
                        pddOrderService.confirmClearSuccess(order);
                        break;
                    case "GM":
                        guoMeiService.confirmClearSuccess(order);
                    default:
                        omsService.confirmClearSuccess(order);
                        break;
                }

                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("出库回传")
    @ApiOperation("出库回传")
    @GetMapping(value = "/confirm-deliver")
    @PreAuthorize("@el.check('crossBorderOrder:confirmDeliver')")
    public ResponseEntity<Object> confirmDeliver(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                CrossBorderOrder order = crossBorderOrderService.queryById(Long.valueOf(idArr[i]));
                switch (order.getPlatformCode()){
                    case "DY":
                        douyinService.confirmPack(order);
                        break;
                    case "PDD":
                        pddOrderService.confirmDeliver(order);
                        break;
                    case "YZ":
                        youZanOrderService.confirmDeliver(order);
                        break;
                    case "MGJ":
                        moGuJieService.confirmDeliver(order);
                        break;
                    case "Ymatou":
                        ymatouService.confirmDeliver(order);
                        break;
                    case "MeiTuan":
                        meituanService.confirmDeliver(order);
                        break;
                    case "GM":
                        guoMeiService.confirmPack(order);
                        break;
                    default:
                        if (StringUtil.contains(order.getPlatformCode(),"-JCY")){
                            List<CrossBorderOrderDetails>detailsList= crossBorderOrderDetailsService.queryByOrderId(order.getId());
                            order.setItemList(detailsList);
                            jackYunService.deliver(order);
                        }else {
                            omsService.confirmDeliver(order);
                        }
                        break;

                }
                if (StringUtil.isNotBlank(order.getLpCode())){
                    caiNiaoService.lastmineHoinCallBack(order);
                }
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @Log("解冻")
    @ApiOperation("解冻")
    @GetMapping(value = "/un-freeze")
    @PreAuthorize("@el.check('crossBorderOrder:unFreeze')")
    public ResponseEntity<Object> unFreeze(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                CrossBorderOrder order = crossBorderOrderService.queryById(Long.valueOf(idArr[i]));
                crossBorderOrderService.unFreeze(order);
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("解冻")
    @ApiOperation("解冻")
    @GetMapping(value = "/dec-mail-no")
    @PreAuthorize("@el.check('crossBorderOrder:unFreeze')")
    public ResponseEntity<Object> decMailNo(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                CrossBorderOrder order = crossBorderOrderService.queryById(Long.valueOf(idArr[i]));
                DouyinMailMark douyinMailMark = douyinMailMarkService.queryByOrderNo(order.getOrderNo());
                CrossBorderOrder order1 = douyinMailMarkService.toOrder(douyinMailMark);
                emsService.getDecinfo(order1);
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("重新拉取")
    @ApiOperation("重新拉取")
    @GetMapping(value = "/re-pull")
    @PreAuthorize("@el.check('crossBorderOrder:recover')")
    public ResponseEntity<Object> rePull(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                CrossBorderOrder order = crossBorderOrderService.queryById(Long.valueOf(idArr[i]));
                if (order != null && (StringUtil.equals(order.getUpStatus(),"102")
                        ||StringUtil.equals(order.getUpStatus(),"100")||StringUtil.isEmpty(order.getCrossBorderNo())
                        ||StringUtil.isEmpty(order.getPaymentNo())
                        ||StringUtil.isBlank(order.getLogisticsNo()))) {
                    ShopInfo shopInfo = shopInfoService.findById(order.getShopId());
                    if (StringUtil.equals(shopInfo.getOrderSourceType(),"2") || StringUtil.equals(shopInfo.getOrderSourceType(),"0")){
                        //推单模式，禁止重新拉取
                        throw new BadRequestException(order.getOrderNo()+"是推单模式，禁止重新拉取");
                    }
                    crossBorderOrderService.deleteAll(new Long[]{order.getId()});
                    if (StringUtil.equals(order.getPlatformCode(),"PDD")){
                        ShopToken shopToken=shopTokenService.queryByShopId(order.getShopId());
                        pddOrderService.pullOrderByOrderSn(new String[]{order.getOrderNo()},shopToken);
                    }else if (StringUtil.equals(order.getPlatformCode(),"DY"))
                        douyinService.pullOrderById(String.valueOf(order.getShopId()), order.getOrderNo());
                    else if (StringUtil.equals(order.getPlatformCode(),"YZ"))
                        youZanOrderService.pullOrderByOrderNo(new String[]{order.getOrderNo()}, shopTokenService.queryByShopId(order.getShopId()));
                    else if (StringUtil.equals(order.getPlatformCode(),"Ymatou"))
                        ymatouService.pullOrderByOrderNo(new String[]{order.getOrderNo()},shopTokenService.queryByShopId(order.getShopId()));
                    else if (StringUtil.equals(order.getPlatformCode(),"MGJ"))
                        moGuJieService.pullOrderByOrderNo(new String[]{order.getOrderNo()},shopTokenService.queryByShopId(order.getShopId()));
                    resMsg = resMsg + idArr[i] + "处理成功," ;
                }else {
                    resMsg = resMsg + "报错：不用重新拉取"+ ",";
                }
            } catch (Exception e) {
                resMsg = resMsg + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation("出库")
    @GetMapping(value = "/deliver")
    public ResponseEntity<Object> deliver(@Validated String weight, @Validated String mailNo, @Validated String materialCode){
        JSONObject result = new JSONObject();
        try {
            String deliver = crossBorderOrderService.deliver(weight, mailNo, materialCode);
            result.putOnce("success", true);
            result.putOnce("data", deliver);
            result.putOnce("msg", "出库成功");
        }catch (Exception e) {
            result.putOnce("success", false);
            result.putOnce("msg", e.getMessage());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation("波次包材变化")
    @GetMapping(value = "/material-code-change")
    public ResponseEntity<Object> materialCodeChange(@Validated String waveNo, @Validated String materialCode){
        JSONObject result = new JSONObject();
        try {
            BigDecimal theoryWeight = crossBorderOrderService.materialCodeChange(waveNo, materialCode);
            result.putOnce("success", true);
            result.putOnce("theoryWeight", theoryWeight);
        }catch (Exception e) {
            result.putOnce("success", false);
            result.putOnce("msg", e.getMessage());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation("运单包材查询")
    @GetMapping(value = "/query-material-code-by-mail-no")
    public ResponseEntity<Object> queryMaterialCodeByMailNo(@Validated String mailNo){
        JSONObject result = new JSONObject();
        try {
            CrossBorderOrder order = crossBorderOrderService.queryMaterialCodeByMailNo(mailNo);
            result.putOnce("success", true);
            result.putOnce("theoryWeight", order.getTheoryWeight());
            result.putOnce("materialCode", order.getMaterialCode());
        }catch (Exception e) {
            result.putOnce("success", false);
            result.putOnce("msg", e.getMessage());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation("运单包材变化")
    @GetMapping(value = "/material-code-change-by-mail-no")
    public ResponseEntity<Object> materialCodeChangeByMailNo(@Validated String mailNo, @Validated String materialCode){
        JSONObject result = new JSONObject();
        try {
            CrossBorderOrder order = crossBorderOrderService.materialCodeChangeByMailNo(mailNo, materialCode);
            result.putOnce("success", true);
            result.putOnce("theoryWeight", order.getTheoryWeight());
        }catch (Exception e) {
            result.putOnce("success", false);
            result.putOnce("msg", e.getMessage());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation("波次状态查询")
    @GetMapping(value = "/batch-deliver-scan")
    public JSONObject batchDeliverScan(@Validated String waveNo){
        JSONObject batchDeliverScan = crossBorderOrderService.batchDeliverScan(waveNo);
        return batchDeliverScan;
    }

    @ApiOperation("查询订单状态")
    @GetMapping(value = "/refound-scan")
    public ResponseEntity<Object> refoundScan(@Validated String mailNo){
        JSONObject result = new JSONObject();
        try {
            CrossBorderOrder order = crossBorderOrderService.queryByMailNo(mailNo);
            if (order == null)
                throw new BadRequestException("运单号不存在");
            Integer status = douyinService.getStatus(order);
            if (status.intValue() == 3) {
                result.putOnce("resMsg", "包裹已出库");
                result.putOnce("success", true);
                result.putOnce("msg", "出库成功");
            }else {
                result.putOnce("success", false);
                switch (status) {
                    case 2:
                        result.putOnce("msg", "待发货");
                        break;
                    case 16:
                        result.putOnce("msg", "退款中");
                        break;
                    case 17:
                        result.putOnce("msg", "已退款-商家同意");
                        break;
                    case 21:
                        result.putOnce("msg", "已退款");
                        break;
                    case 25:
                        result.putOnce("msg", "取消退款，可发货");
                        break;
                    case 4:
                        result.putOnce("msg", "退款完成");
                        break;
                    case 5:
                        result.putOnce("msg", "5");
                        break;
                    default:
                        result.putOnce("msg", "未知状态：" + status);
                        break;
                }
            }
        }catch (Exception e) {
            result.putOnce("success", false);
            result.putOnce("msg", e.getMessage());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation("更新订单锁单状态")
    @GetMapping(value = "/update-lock-status")
    public ResponseEntity<Object> updateLockStatus( @Validated String mailNo){
        JSONObject result = new JSONObject();
        try {
            CrossBorderOrder order = crossBorderOrderService.queryByMailNo(mailNo);
            order.setIsLock("3");
            crossBorderOrderService.update(order);
            result.putOnce("success", true);
            result.putOnce("msg", "出库成功");
        }catch (Exception e) {
            result.putOnce("success", false);
            result.putOnce("msg", e.getMessage());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("取消申报")
    @ApiOperation("取消申报")
    @GetMapping(value = "/cancel-declare")
    @PreAuthorize("@el.check('crossBorderOrder:cancel')")
    public ResponseEntity<Object> cancelDeclare(String ids) throws Exception {
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                crossBorderOrderService.cancelDec(Long.valueOf(idArr[i]));
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>( HttpStatus.OK);
    }

    @Log("取消订单+申报")
    @ApiOperation("取消订单+申报")
    @GetMapping(value = "/cancel")
    @PreAuthorize("@el.check('crossBorderOrder:cancel')")
    public ResponseEntity<Object> cancel(String ids) throws Exception {
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                crossBorderOrderService.cancel(Long.valueOf(idArr[i]));
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>( HttpStatus.OK);
    }

    @Log("查询平台订单状态")
    @ApiOperation("查询平台订单状态")
    @GetMapping(value = "/get-plat-status")
    @PreAuthorize("@el.check('crossBorderOrder:getPlatStatus')")
    public ResponseEntity<Object> getPlatStatus(String id) throws Exception {
        if (id == null)
            throw new BadRequestException("未选择任何记录");
        CrossBorderOrder order = crossBorderOrderService.queryById(Long.valueOf(id));
        JSONObject result = new JSONObject();
        if (StringUtil.equals(order.getPlatformCode(),"DY")){
            String resMsg = "";
            Integer status = douyinService.getStatus(order);
//            Integer upStatus = douyinService.getUpStatus(order, null);
            switch (status) {
                case 2:
                    resMsg = "待发货";
                    break;
                case 3:
                    resMsg = "已发货";
                    break;
                case 16:
                    resMsg = "退款中";
                    break;
                case 17:
                    resMsg = "已退款-商家同意";
                    break;
                case 21:
                    resMsg = "已退款";
                    break;
                case 25:
                    resMsg = "取消退款，可发货";
                    break;
                case 4:
                    resMsg = "退款完成";
                    break;
                case 5:
                    resMsg = "5";
                    break;
                default:
                    resMsg = "未知状态：" + status;
                    break;
            }
            result.putOnce("resMsg", resMsg);
        }else if (StringUtil.equals(order.getPlatformCode(),"PDD")){
            String statusCode=pddOrderService.getOrderStatus(order.getOrderNo(),order.getPlatformShopId());
            result.putOnce("resMsg", PDDSupport.translationStatus(statusCode));
        }else if (StringUtil.equals(order.getPlatformCode(),"YZ")){
            String statusCode=youZanOrderService.getOrderStatus(order.getOrderNo(),order.getPlatformShopId());
            result.putOnce("resMsg", YouZanSupport.translationStatusCode(statusCode));
        }else if (StringUtil.equals(order.getPlatformCode(),"Ymatou")){
            int statusCode=ymatouService.getOrderStatus(order.getOrderNo(),order.getPlatformShopId());
            result.putOnce("resMsg", YmatouSupport.getStatusText(statusCode));
        }else if (StringUtil.equals(order.getPlatformCode(),"MGJ")){
            String statusCode=moGuJieService.getOrderStatus(order.getOrderNo(),order.getShopId());
            result.putOnce("resMsg", MoGuJieSupport.translationStatusCode(statusCode));
        }else if (StringUtil.equals(order.getPlatformCode(),"MeiTuan")){
            Integer statusCode=meituanService.getOrderStatus(order.getOrderNo(),order.getShopId());
            result.putOnce("resMsg", MeiTuanSupport.translationStatusCode(statusCode));
        }else if (StringUtil.equals(order.getPlatformCode(),"AiKuCun")){
            Integer statusCode=aikucunService.getOrderStatus(order.getOrderNo(),order.getShopId());
            result.putOnce("resMsg", AikucunSupport.translationStatusCode(statusCode));
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('crossBorderOrder:list')")
    public void download(HttpServletResponse response, CrossBorderOrderQueryCriteria criteria) throws IOException {
        Config config = configService.queryByK("DOWNLOAD_SWITCH");
        if (config != null && StringUtils.equals("0", config.getV())) {
            throw new BadRequestException("导出开关被关");
        }
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> shopIds = userCustomerService.queryShops(userId);
        if (CollectionUtils.isEmpty(criteria.getShopId()) && CollectionUtils.isNotEmpty(shopIds)) {
            criteria.setShopId(shopIds);
        }
        crossBorderOrderService.download(criteria, response);
    }

    @Log("导出数据明细")
    @ApiOperation("导出数据明细")
    @GetMapping(value = "/download-details")
    @PreAuthorize("@el.check('crossBorderOrder:list')")
    public void downloadDetails(HttpServletResponse response, CrossBorderOrderQueryCriteria criteria) throws IOException {
        Config config = configService.queryByK("DOWNLOAD_SWITCH");
        if (config != null && StringUtils.equals("0", config.getV())) {
            throw new BadRequestException("导出开关被关");
        }
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> shopIds = userCustomerService.queryShops(userId);
        if (CollectionUtils.isEmpty(criteria.getShopId()) && CollectionUtils.isNotEmpty(shopIds)) {
            criteria.setShopId(shopIds);
        }
        crossBorderOrderService.downloadDetails(criteria, response);
    }

    @Log("导出数据明细")
    @ApiOperation("导出数据明细")
    @GetMapping(value = "/download-in")
    @PreAuthorize("@el.check('crossBorderOrder:list')")
    public void downloadIn(HttpServletResponse response, CrossBorderOrderQueryCriteria criteria) throws IOException {
        Config config = configService.queryByK("DOWNLOAD_SWITCH");
        if (config != null && StringUtils.equals("0", config.getV())) {
            throw new BadRequestException("导出开关被关");
        }
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> shopIds = userCustomerService.queryShops(userId);
        if (CollectionUtils.isEmpty(criteria.getShopId()) && CollectionUtils.isNotEmpty(shopIds)) {
            criteria.setShopId(shopIds);
        }
        crossBorderOrderService.downloadIn(criteria, response);
    }

    @GetMapping
    @Log("查询crossBorderOrder")
    @ApiOperation("查询crossBorderOrder")
    @PreAuthorize("@el.check('crossBorderOrder:list')")
    public ResponseEntity<Object> query(CrossBorderOrderQueryCriteria criteria, Pageable pageable){
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> shopIds = userCustomerService.queryShops(userId);
        if (CollectionUtils.isEmpty(criteria.getShopId()) && CollectionUtils.isNotEmpty(shopIds)) {
            criteria.setShopId(shopIds);
        }
        return new ResponseEntity<>(crossBorderOrderService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @GetMapping(value = "/query-by-id")
    @Log("查询crossBorderOrder")
    @ApiOperation("查询crossBorderOrder")
    @PreAuthorize("@el.check('crossBorderOrder:list')")
    public ResponseEntity<Object> queryById(String id){
        CrossBorderOrder order=crossBorderOrderService.queryByIdWithDetails(Long.valueOf(id));
        try {
            crossBorderOrderService.decryptMask(order);
            Platform platform=platformService.findByCode(order.getPlatformCode());
            order.setPlatformId(platform.getId());
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(order,HttpStatus.OK);
    }


    @PostMapping
    @Log("新增crossBorderOrder")
    @ApiOperation("新增crossBorderOrder")
    @PreAuthorize("@el.check('crossBorderOrder:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody CrossBorderOrder resources){
        return new ResponseEntity<>(crossBorderOrderService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改crossBorderOrder")
    @ApiOperation("修改crossBorderOrder")
    @PreAuthorize("@el.check('crossBorderOrder:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody CrossBorderOrder resources){
        crossBorderOrderService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除crossBorderOrder")
    @ApiOperation("删除crossBorderOrder")
    @PreAuthorize("@el.check('crossBorderOrder:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        crossBorderOrderService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @AnonymousAccess
    @RequestMapping("/query-orders")
    @Log("接口分页查询订单信息")
    @ApiOperation("接口分页查询订单信息")
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
            CrossBorderOrderQueryCriteria criteria  = new CrossBorderOrderQueryCriteria();
            String shopCode = reqObject.getStr("shopCode");
            if (StringUtil.isNotBlank(shopCode)) {
                ShopInfo shopInfo = shopInfoService.queryByShopCode(shopCode);
                if (shopInfo == null) {
                    return ResultUtils.getFail("shopCode不存在：" + shopCode);
                }
                List<Long> shopIds = new ArrayList<>();
                shopIds.add(shopInfo.getId());
                criteria.setShopId(shopIds);
            }

            criteria.setCustomersId(custCode.getCustomerId());
            Pageable pageable = new PageRequest(pageNo, pageSize);
            List<Timestamp> createTime = new ArrayList<>();
            createTime.add(new Timestamp(startTime.getTime()));
            createTime.add(new Timestamp(endTime.getTime()));
            criteria.setCreateTime(createTime);
            Map<String, Object> map = crossBorderOrderService.queryOrders(criteria, pageable);
            return ResultUtils.getSuccess(map);
        }catch (Exception e) {
            return ResultUtils.getFail(e.getMessage());
        }

    }

    @AnonymousAccess
    @RequestMapping("/query-by-orderno")
    @Log("接口分页查询订单信息")
    @ApiOperation("接口分页查询订单信息")
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
            CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(orderNo);
            try {
                youZanOrderService.decryptMask(order);
            }catch (Exception e){
                e.printStackTrace();
            }
            CBOrderOutDTO cbOrderOutDTO = new CBOrderOutDTO();
            BeanUtil.copyProperties(order,cbOrderOutDTO, CopyOptions.create().setIgnoreNullValue(true));
            List<CrossBorderOrderDetails> list = crossBorderOrderDetailsService.queryByOrderId(order.getId());
            List<CBOrderOutDetailsDTO> detailsDTOS = new ArrayList<>();
            for (CrossBorderOrderDetails details : list) {
                CBOrderOutDetailsDTO detailsDTO = new CBOrderOutDetailsDTO();
                BeanUtil.copyProperties(details,detailsDTO, CopyOptions.create().setIgnoreNullValue(true));
                detailsDTOS.add(detailsDTO);
            }
            ShopInfoDto shopInfoDto = shopInfoService.queryById(order.getShopId());
            cbOrderOutDTO.setShopCode(shopInfoDto.getCode());
            cbOrderOutDTO.setShopName(shopInfoDto.getName());
            cbOrderOutDTO.setItemList(detailsDTOS);
            return ResultUtils.getSuccess(cbOrderOutDTO);
        }catch (Exception e) {
            return ResultUtils.getFail(e.getMessage());
        }

    }

    @Autowired
    private OmsService omsService;

    @AnonymousAccess
    @RequestMapping("/push-order")
    @Log("推送订单信息")
    @ApiOperation("推送订单信息")
    public Result pushOrder(@RequestParam("data") String data,
                                 @RequestParam("customersCode") String customersCode) {
        try {
            CustomerKeyDto custCode = customerKeyService.findByCustCode(customersCode);
            if (custCode == null)
               throw new BadRequestException("customersCode不存在或客户未配置key");
            String signKey = custCode.getSignKey();
            String originData = SecureUtils.decryptDesHex(data, signKey);
            JSONObject reqObject = JSONUtil.parseObj(originData);
            OrderMain orderMain = JSON.parseObject(originData, OrderMain.class);
            List<OrderChild> itemList = JSONArray.parseArray(reqObject.getStr("skuDetails"), OrderChild.class);
            orderMain.setSkuDetails(itemList);
            omsService.pushOrder(orderMain);
            return ResultUtils.getSuccess();
        }catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.getFail(e.getMessage());
        }
    }

    @AnonymousAccess
    @RequestMapping("/cancel-order")
    @Log("取消订单")
    @ApiOperation("取消订单")
    public Result cancelOrder(@RequestParam("data") String data,
                            @RequestParam("customersCode") String customersCode) {
        try {
            CustomerKeyDto custCode = customerKeyService.findByCustCode(customersCode);
            if (custCode == null)
                throw new BadRequestException("customersCode不存在或客户未配置key");
            String signKey = custCode.getSignKey();
            String originData = SecureUtils.decryptDesHex(data, signKey);
            JSONObject reqObject = JSONUtil.parseObj(originData);
            String orderNo = reqObject.getStr("orderNo");
            String remark = reqObject.getStr("remark");
            omsService.cancelOrder(orderNo,remark,custCode.getCustomerId());
            return ResultUtils.getSuccess();
        }catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.getFail(e.getMessage());
        }
    }


    @Log("订单总量情况")
    @ApiOperation("订单总量情况")
    @PostMapping(value = "/order-total-count")
    @PreAuthorize("@el.check('home:orderTotalCount')")
    public ResponseEntity<Object> orderTotalCount(@RequestParam Map<String,Object>map){
        Map<String,Object> homeDto = crossBorderOrderService.orderTotalCountAll(map);
        return new ResponseEntity<>(homeDto, HttpStatus.OK);
    }

    @Log("店铺订单情况")
    @ApiOperation("店铺订单情况")
    @PostMapping(value = "/shop-order-count")
    @PreAuthorize("@el.check('home:shopOrderCount')")
    public ResponseEntity<Object> shopOrderCount(@RequestParam Map<String,Object>map){
        List<Map<String, Object>> homeDto = crossBorderOrderService.shopOrderCountAll(map);
        return new ResponseEntity<>(homeDto, HttpStatus.OK);
    }

    @Log("售后处理")
    @ApiOperation("售后处理")
    @PostMapping(value = "/refund-opt")
    @PreAuthorize("@el.check('crossBorderOrder:refundOpt')")
    public ResponseEntity<Object> refundOpt(@RequestParam Long id,@RequestParam Integer action,String optionReason){
        crossBorderOrderService.refundOpt(id,action,optionReason);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("查询小时订单情况")
    @ApiOperation("查询小时订单情况")
    @GetMapping(value = "/get-order-hour")
    @PreAuthorize("@el.check('crossBorderOrder:list')")
    public ResponseEntity<Object> getOrderHour(String startCountTime, String endCountTime, String startOrderCreateTime, String endOrderCreateTime, String preSell){
        LinkedList<Map<String, Object>> resultDto = crossBorderOrderService.getOrderHour(startCountTime, endCountTime, startOrderCreateTime, endOrderCreateTime, preSell);
        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }

    @Log("订单生产进度")
    @ApiOperation("订单生产进度")
    @GetMapping(value = "/order-process")
    @PreAuthorize("@el.check('crossBorderOrder:list')")
    public ResponseEntity<Object> orderProcess( String startCreateTime, String endCreateTime, String platformCode){
        JSONObject result =  crossBorderOrderService.orderProcess(startCreateTime, endCreateTime, platformCode);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @AnonymousAccess
    @RequestMapping("/push-declare-order")
    @Log("推送申报单信息")
    @ApiOperation("推送申报单信息")
    public Result pushDeclareOrder(@RequestParam("data") String data,
                            @RequestParam("customersCode") String customersCode) {
        try {
            CustomerKeyDto custCode = customerKeyService.findByCustCode(customersCode);
            if (custCode == null)
                throw new BadRequestException("customersCode不存在或客户未配置key");
            String signKey = custCode.getSignKey();
            String originData = SecureUtils.decryptDesHex(data, signKey);
            JSONObject reqObject = JSONUtil.parseObj(originData);
            DewuDeclarePush orderMain = JSON.parseObject(originData, DewuDeclarePush.class);
            List<DewuDeclarePushItem> itemList = JSONArray.parseArray(reqObject.getStr("skuDetails"), DewuDeclarePushItem.class);
            orderMain.setSkuDetails(itemList);
            dewuDeclarePushService.createOrder(orderMain);
            return ResultUtils.getSuccess();
        }catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.getFail(e.getMessage());
        }
    }

    @AnonymousAccess
    @RequestMapping("/cancel-declare-order")
    @Log("取消申报单")
    @ApiOperation("取消申报单")
    public Result cancelDeclareOrder(@RequestParam("data") String data,
                                   @RequestParam("customersCode") String customersCode) {
        try {
            CustomerKeyDto custCode = customerKeyService.findByCustCode(customersCode);
            if (custCode == null)
                throw new BadRequestException("customersCode不存在或客户未配置key");
            String signKey = custCode.getSignKey();
            String originData = SecureUtils.decryptDesHex(data, signKey);
            JSONObject reqObject = JSONUtil.parseObj(originData);

            dewuDeclarePushService.cancelOrder(reqObject.getStr("orderNo"));
            return ResultUtils.getSuccess();
        }catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.getFail(e.getMessage());
        }
    }
}
