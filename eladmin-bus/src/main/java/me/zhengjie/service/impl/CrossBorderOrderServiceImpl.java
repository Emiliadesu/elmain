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
package me.zhengjie.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.json.XML;
import com.alibaba.fastjson.JSON;
import com.taobao.pac.sdk.cp.dataobject.response.CROSSBORDER_LOGISTICS_DETAIL_QUERY.PackageStatus;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.exception.EntityExistException;
import me.zhengjie.mq.CBOrderProducer;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.*;
import me.zhengjie.support.GZTOSupport;
import me.zhengjie.support.MailSupport;
import me.zhengjie.support.cainiao.CaiNiaoSupport;
import me.zhengjie.support.douyin.CBOrderOperateRequest;
import me.zhengjie.support.fuliPre.DocOrderPackingSummary;
import me.zhengjie.support.kjg.KJGSupport;
import me.zhengjie.support.WmsSupport;
import me.zhengjie.support.meituan.MeiTuanOrderRefundAgreeRequest;
import me.zhengjie.support.meituan.MeiTuanOrderRefundRejectRequest;
import me.zhengjie.support.meituan.MeiTuanSupport;
import me.zhengjie.support.moGuJie.MoGuJieSupport;
import me.zhengjie.support.oms.OrderMain;
import me.zhengjie.support.pdd.PDDSupport;
import me.zhengjie.support.pdd.PddOrder;
import me.zhengjie.support.queenshop.QSSupport;
import me.zhengjie.support.sf.SFGJSupport;
import me.zhengjie.support.ymatou.YmatouSupport;
import me.zhengjie.support.youzan.YouZanSupport;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.CrossBorderOrderRepository;
import me.zhengjie.service.mapstruct.CrossBorderOrderMapper;
import me.zhengjie.utils.constant.MsgType;
import me.zhengjie.utils.constant.PlatformConstant;
import me.zhengjie.utils.enums.BooleanEnum;
import me.zhengjie.utils.enums.CBOrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author luob
* @date 2021-03-25
**/
@Slf4j
@Service
@RequiredArgsConstructor
public class CrossBorderOrderServiceImpl implements CrossBorderOrderService {

    private final CrossBorderOrderRepository crossBorderOrderRepository;
    private final CrossBorderOrderMapper crossBorderOrderMapper;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private KJGSupport kjgSupport;

    @Autowired
    private GZTOSupport gztoSupport;

    @Autowired
    private CBOrderProducer cbOrderProducer;

    @Autowired
    private OrderLogService orderLogService;

    @Autowired
    private QueryMftLogService queryMftLogService;

    @Autowired
    private OrderDeliverLogService orderDeliverLogService;

    @Autowired
    private DouyinService douyinService;

    @Autowired
    private CrossBorderOrderDetailsService crossBorderOrderDetailsService;

    @Autowired
    private WmsSupport wmsSupport;

    @Autowired
    private CustomerInfoService customerInfoService;

    @Autowired
    private UserCustomerService userCustomerService;

    @Autowired
    private PddOrderService pddOrderService;

    @Autowired
    private YouZanOrderService youZanOrderService;

    @Autowired
    private ShopTokenService shopTokenService;

    @Autowired
    private ClearCompanyInfoService clearCompanyInfoService;

    @Autowired
    private QSService qsService;

    @Autowired
    private YmatouService ymatouService;

    @Autowired
    private MoGuJieService moGuJieService;

    @Autowired
    private MeituanService meituanService;

    @Autowired
    private GuoMeiService guoMeiService;

    @Autowired
    private SFGJSupport sfgjSupport;

    @Autowired
    private PackageInfoService packageInfoService;

    @Autowired
    private LogisticsInfoService logisticsInfoService;

    @Autowired
    private SkuMaterialService skuMaterialService;

    @Autowired
    private OrderMaterialService orderMaterialService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private MailSupport mailSupport;

    @Autowired
    private DailyCrossBorderOrderService dailyCrossBorderOrderService;

    @Autowired
    private CaiNiaoService caiNiaoService;

    @Autowired
    private PddCloudPrintDataService printDataService;
    /**
     * 清关开始
     *
     * @param orderId
     */
    @Override
    public void declare(String orderId) throws Exception {
        // 清关开始：1.获取订单申报所需的海关信息(税费计算等)，2.获取运单号，3.申报
        CrossBorderOrder order = queryByIdWithDetails(Long.valueOf(orderId));
        if (order == null) throw new BadRequestException("清关开始订单为空：" + orderId);
        if (StringUtil.equals(order.getPlatformCode(), "PDD")) {
            declarePdd(order);
            return;
        }
        if (StringUtil.equals(order.getPlatformCode(), "MeiTuan") && StringUtil.isBlank(order.getPaymentNo())) {
            //等待美团推送清关信息
            return;
        }
        try {
            if (order.getStatus().intValue() != CBOrderStatusEnum.STATUS_215.getCode().intValue()) {
                log.error("订单申报海关：当前订单不允许申报,单号：{}，当前状态：{}", order.getOrderNo(), order.getStatus());
                throw new BadRequestException(order.getOrderNo() + "当前状态不允许申报,");
            }
//            // 1.获取申报所需信息
//            kjgSupport.getDecinfo(order);
            try {
                decrypt(order);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BadRequestException("订单解密失败:" + e.getMessage());
            }
//            // 2.获取运单
//            if (StringUtils.isBlank(order.getLogisticsNo())) {
//                gztoSupport.getMail(order, null);
//            }

            // 3.清关申报
            String declareNo = kjgSupport.declare(order);
            order.setDeclareNo(declareNo);//保存申报单号
            order.setStatus(CBOrderStatusEnum.STATUS_220.getCode());// 更新状态为申报开始
            order.setClearStartTime(new Timestamp(System.currentTimeMillis()));
            try {
                encrypt(order);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BadRequestException("订单加密失败" + e.getMessage());
            }
            update(order);
            crossBorderOrderDetailsService.updateBatch(order.getItemList());
            // 保存订单日志
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_220.getCode()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderLogService.create(orderLog);

            //清关开始，延迟三秒发送清关开始回传消息
            cbOrderProducer.delaySend(
                    MsgType.CB_ORDER_225,
                    orderId,
                    order.getOrderNo(),
                    3000
            );
        } catch (Exception e) {
            // 保存订单日志
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_220.getCode()),
                    BooleanEnum.FAIL.getCode(),
                    e.getMessage()
            );
            orderLogService.create(orderLog);
            try {
                encrypt(order);
            } catch (Exception e2) {
                e2.printStackTrace();
                throw new BadRequestException("订单加密失败" + e2.getMessage());
            }
            throw e;
        }
    }

    public void decrypt(CrossBorderOrder order) throws Exception {
        /*ShopToken shopToken=shopTokenService.queryByShopId(orderDeclare.getShopId());
        if (shopToken==null) return;*/
        if (StringUtil.equals(order.getPlatformCode(), "YZ")) {
            youZanOrderService.orderDecrypt(order);
        }
    }

    public void encrypt(CrossBorderOrder declareEnc) throws Exception {
        /*ShopToken shopToken=shopTokenService.queryByShopId(declareEnc.getShopId());
        if (shopToken==null) return;*/
        if (StringUtil.equals(declareEnc.getPlatformCode(), "YZ")) {
            youZanOrderService.orderEncrypt(declareEnc);
        }
    }

    /**
     * 定时器：重新拉取无支付单号的冻结单
     */
    @Override
    public void rePullByNoPayNo() {
        List<CrossBorderOrder> orderList = crossBorderOrderRepository.queryByNonPayNo();
        if (CollectionUtil.isEmpty(orderList))
            return;
        ShopToken shopToken = null;
        for (CrossBorderOrder order : orderList) {
            if (StringUtil.equals("PDD", order.getPlatformCode())) {
                String orderNo = order.getOrderNo();
                crossBorderOrderRepository.deleteById(order.getId());
                if (shopToken == null || !StringUtil.equals(shopToken.getPlatformShopId(), order.getPlatformShopId())) {
                    shopToken = shopTokenService.queryByShopId(order.getShopId());
                }
                for (int i = 0; i < 50; i++) {
                    try {
                        pddOrderService.pullOrderByOrderSn(new String[]{orderNo},shopToken);
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error(e.getMessage(),e);
                        if (!StringUtil.contains(e.getMessage(),"获取订单失败：业务服务错误")){
                            throw new BadRequestException(e.getMessage());
                        }else if (i > 48)
                            throw new BadRequestException(e.getMessage());
                    }
                }
            } else if (StringUtil.equals("MGJ", order.getPlatformCode())) {
                String orderNo = order.getOrderNo();
                crossBorderOrderRepository.deleteById(order.getId());
                if (shopToken == null || !StringUtil.equals(shopToken.getPlatformShopId(), order.getPlatformShopId())) {
                    shopToken = shopTokenService.queryByShopId(order.getShopId());
                }
                try {
                    moGuJieService.pullOrderByOrderNo(new String[]{orderNo}, shopToken);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void orderDeclare() throws Exception {
        CrossBorderOrder order = new CrossBorderOrder();
        order.setStatus(215);
        List<CrossBorderOrder> list = crossBorderOrderRepository.findAll(Example.of(order));
        if (CollectionUtil.isEmpty(list))
            return;
        for (CrossBorderOrder crossBorderOrder : list) {
            if (!StringUtils.equals("2", order.getDefault02())) {
                cbOrderProducer.send(
                        MsgType.CB_ORDER_220,
                        String.valueOf(crossBorderOrder.getId()),
                        order.getOrderNo()
                );
            }
        }
    }

    /**
     * 订单脱敏
     *
     * @param order
     * @throws Exception
     */
    public void decryptMask(CrossBorderOrder order) throws Exception {
        if (order == null)
            throw new BadRequestException("订单不存在");
        switch (order.getPlatformCode()) {
            case "PDD":
                pddOrderService.decryptMask(order);
                break;
            case "YZ":
                decrypt(order);
                youZanOrderService.decryptMask(order);
                break;
            case "DY":
                youZanOrderService.decryptMask(order);
                break;
        }
    }

    @Override
    public void pullOrderByTimeRange(Date startTime, Date endTime, String shopId) {
        ShopInfo shopInfo = shopInfoService.findById(Long.parseLong(shopId));
        if (shopInfo == null)
            throw new BadRequestException("店铺code不存在");
        if (StringUtil.equals("PDD", shopInfo.getPlatformCode())) {
            if (endTime.getTime() - startTime.getTime() > 24 * 3600 * 1000) {
                //拼多多只能一次拉取24小时的待发货订单,超过24小时则需要分开拉单
                long day = (endTime.getTime() - startTime.getTime()) % (24 * 3600 * 1000) == 0 ?
                        ((endTime.getTime() - startTime.getTime()) / (24 * 3600 * 1000)) :
                        ((endTime.getTime() - startTime.getTime()) / (24 * 3600 * 1000) + 1);
                for (int i = 1; i <= day; i++) {
                    Date endC;
                    if (i == day)
                        endC = endTime;
                    else
                        endC = new Date(startTime.getTime() + i * 24 * 3600 * 1000);
                    Date startC = new Date(startTime.getTime() + (i - 1) * 24 * 3600 * 1000);
                    pddOrderService.pullOrderByTimeRange(startC, endC, shopInfo);
                }
            } else
                pddOrderService.pullOrderByTimeRange(startTime, endTime, shopInfo);
        } else if (StringUtil.equals("YZ", shopInfo.getPlatformCode())) {
            try {
                youZanOrderService.pullOrderByTimeRange(startTime, endTime, shopInfo);
            } catch (Exception e) {
                throw new BadRequestException(e.getMessage());
            }
        } else if (StringUtil.equals("MGJ", shopInfo.getPlatformCode())) {
            try {
                moGuJieService.pullOrderByTimeRange(startTime, endTime, shopInfo);
            } catch (Exception e) {
                throw new BadRequestException(e.getMessage());
            }
        } else if (StringUtil.equals("Ymatou", shopInfo.getPlatformCode())) {
            try {
                ymatouService.pullOrderByTimeRange(startTime, endTime, shopInfo);
            } catch (Exception e) {
                throw new BadRequestException(e.getMessage());
            }
        }
    }


    /**
     * 清关开始
     *
     * @param order
     */
    @Override
    public void declarePdd(CrossBorderOrder order) throws Exception {
        // 清关开始：1.获取订单申报所需的海关信息(税费计算等)，2.获取运单号，3.申报
        try {
            if (order.getStatus().intValue() != CBOrderStatusEnum.STATUS_215.getCode().intValue()) {
                log.error("订单申报海关：当前订单不允许申报,单号：{}，当前状态：{}", order.getOrderNo(), order.getStatus());
                throw new BadRequestException(order.getOrderNo() + "当前状态不允许申报,");
            }
            // 1.获取申报所需信息
            kjgSupport.getDecinfo(order);
            crossBorderOrderDetailsService.updateBatch(order.getItemList());
            // 2.获取运单
            //pddOrderService.getMailNo(order);
            update(order);
            // 3.清关申报,多多云同时进行获取大头笔和清关申报
            String declareNo = pddOrderService.declare(order);
            order.setDeclareNo(declareNo);//保存申报单号
            order.setStatus(CBOrderStatusEnum.STATUS_220.getCode());// 更新状态为申报开始
            order.setClearStartTime(new Timestamp(System.currentTimeMillis()));
            update(order);

            // 保存订单日志
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_220.getCode()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderLogService.create(orderLog);

            //清关开始，延迟三秒发送清关开始回传消息
            cbOrderProducer.delaySend(
                    MsgType.CB_ORDER_225,
                    order.getId() + "",
                    order.getOrderNo(),
                    3000
            );
        } catch (Exception e) {
            // 保存订单日志
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_220.getCode()),
                    BooleanEnum.FAIL.getCode(),
                    e.getMessage(),
                    StringUtil.exceptionStackInfoToString(e)
            );
            orderLogService.create(orderLog);
            throw e;
        }
    }

    //批量更新申报单状态
    @Override
    public void updateMftStatus() {
        // 查询待更新状态订单
        CrossBorderOrderQueryCriteria criteria = new CrossBorderOrderQueryCriteria();
        List<Integer> status = new ArrayList<>();
        status.add(CBOrderStatusEnum.STATUS_215.getCode());// 接单回传
        status.add(CBOrderStatusEnum.STATUS_225.getCode());// 清关开始回传
        status.add(CBOrderStatusEnum.STATUS_227.getCode());// 清关异常回传
        criteria.setStatus(status);
        List<CrossBorderOrder> all = crossBorderOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        for (CrossBorderOrder order : all) {
            cbOrderProducer.send(
                    MsgType.CB_ORDER_REFRESH,
                    String.valueOf(order.getId()),
                    order.getOrderNo()
            );

        }
    }

    //刷新订单申报状态
    @Override
    public void refreshClearStatus(String orderId) throws Exception {
        CrossBorderOrder order = crossBorderOrderRepository.findById(Long.valueOf(orderId)).orElseGet(CrossBorderOrder::new);
        if (StringUtils.equals("2", order.getDefault02())) {
            // 抖音申报的不走这个逻辑
            return;
        }
        if (order == null) throw new BadRequestException("刷新状态订单为空：" + orderId);
        if (order.getStatus().intValue() >= CBOrderStatusEnum.STATUS_235.getCode().intValue())
            throw new BadRequestException("当前状态不可刷新清关状态");
        String res = kjgSupport.refresh(order);
        JSONObject resJSON = JSONUtil.xmlToJson(res);
        JSONObject resHeader = resJSON.getJSONObject("Message").getJSONObject("Header");
        if (StringUtil.equals("T", resHeader.getStr("Result"))) {
            JSONObject mft = resJSON.getJSONObject("Message").getJSONObject("Body").getJSONObject("Mft");
            String ManifestId = mft.getStr("ManifestId");
            order.setInvtNo(ManifestId);// 保存总署清单编号
            if ("99".equals(String.valueOf(mft.get("Status")))) {
                order.setDeclareStatus("99");// 申报单取消状态
            }
            if (mft.get("CheckFlg") instanceof Integer && 0 == mft.getInt("CheckFlg")) {
                // 预校验异常
                order.setDeclareMsg(mft.getStr("CheckMsg"));
            }

            cn.hutool.json.JSONArray MftInfos = mft.getJSONObject("MftInfos").getJSONArray("MftInfo");
            String Status;
            String Result = "预校验未通过";
            if (MftInfos == null) {
                Status = mft.getStr("Status");
            } else {
                Status = MftInfos.getJSONObject(0).getStr("Status");
                Result = MftInfos.getJSONObject(0).getStr("Result");
            }
            boolean needCallMq = false;
            order.setDeclareStatus(Status);
            order.setDeclareMsg(Result);
            if ("22".equals(String.valueOf(Status))) {
                // 单证放行清关完成
                if (StringUtils.equals("2", order.getDefault02())) {
                    // 平台申报直接改状态为清关完成回传
                    order.setStatus(CBOrderStatusEnum.STATUS_235.getCode());
                } else {
                    order.setStatus(CBOrderStatusEnum.STATUS_230.getCode());
                    needCallMq = true;
                }
                order.setClearSuccessTime(new Timestamp(MftInfos.getJSONObject(0).getDate("CreateTime").getTime()));
                order.setDeclareMsg("清关完成");
            }
            // 更新订单状态
            update(order);

            if (StringUtil.contains(order.getDeclareMsg(), "[Code:1371")
                    || StringUtil.contains(order.getDeclareMsg(), "[Code:1313")
                    || StringUtil.contains(order.getDeclareMsg(), "人工审核")) {
                douyinService.confirmClearErr(String.valueOf(order.getId()));
            }

            // 发送清关完成回传消息
            if (needCallMq) {
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_230.getCode()),
                        BooleanEnum.SUCCESS.getCode(),
                        BooleanEnum.SUCCESS.getDescription()
                );
                orderLogService.create(orderLog);
                cbOrderProducer.send(
                        MsgType.CB_ORDER_235,
                        String.valueOf(order.getId()),
                        order.getOrderNo()
                );
            }
        } else {
            throw new BadRequestException(resHeader.getStr("ResultMsg"));
        }
    }

    //取消状态订单
    @Override
    public void updateCancelOrder() {
        // 查询未出库的订单
        CrossBorderOrderQueryCriteria criteria = new CrossBorderOrderQueryCriteria();
        List<Integer> status = new ArrayList<>();
        status.add(CBOrderStatusEnum.STATUS_200.getCode());
        status.add(CBOrderStatusEnum.STATUS_201.getCode());
        status.add(CBOrderStatusEnum.STATUS_215.getCode());
        status.add(CBOrderStatusEnum.STATUS_220.getCode());
        status.add(CBOrderStatusEnum.STATUS_225.getCode());
        status.add(CBOrderStatusEnum.STATUS_230.getCode());
        status.add(CBOrderStatusEnum.STATUS_235.getCode());
        status.add(CBOrderStatusEnum.STATUS_227.getCode());
        status.add(CBOrderStatusEnum.STATUS_999.getCode());
        criteria.setStatus(status);
        criteria.setNonPlatformCode("DY");
        criteria.setNonPlatformCode2("DW");

        List<CrossBorderOrder> all = crossBorderOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        if (CollectionUtils.isNotEmpty(all)) {
            for (CrossBorderOrder order : all) {
                if (StringUtil.equals(order.getPlatformCode(),"DY"))
                    continue;
                cbOrderProducer.send(
                        MsgType.CB_ORDER_888,
                        String.valueOf(order.getId()),
                        order.getOrderNo()
                );
            }
        }
    }

    @Override
    public synchronized void refreshCancelStatus(String id) throws Exception {
        CrossBorderOrder order = queryById(Long.valueOf(id));
        log.info("开始刷新订单取消状态：{}", order.getOrderNo());
        Integer dyStatus = getPlatformStatus(order);
        log.info("平台返回状态状态：{},{}", order.getOrderNo(), dyStatus);
        if (dyStatus.intValue() == 17 || dyStatus.intValue() == 21 || dyStatus.intValue() == 4) {
            // 可取消
            if(StringUtil.equals(order.getPlatformCode(),"GM")){
                //国美回传取消订单
                guoMeiService.orderCancelBySys(order,20);
            }else
                cancel(order.getId());
        }
        if (dyStatus.intValue() == 16) {
            // 退款中，冻结锁单
            // 只有国美有这个逻辑
            if (StringUtil.equals("GM",order.getPlatformCode())) {
                guoMeiService.orderCancelBySys(order,10);
            } else {
                cancel(order.getId());
            }
        }
        if (dyStatus.intValue() == 25) {
            // 取消退款，取消锁单
            if (StringUtils.equals("GM", order.getPlatformCode())) {
                guoMeiService.orderCancelBySys(order,30);
            }
        }
        if (dyStatus.intValue() == 2 && StringUtils.equals("1", order.getIsLock())) {
            // 取消锁单
            if (StringUtils.equals("GM", order.getPlatformCode())){
                guoMeiService.orderCancelBySys(order,30);
            }else
                cancelLockOrder(order);
        }
    }

    /**
     * 锁单
     *
     * @param order
     */
    @Override
    public void lockOrder(CrossBorderOrder order) throws Exception {
        order = queryById(order.getId());
        if (order.getStatus().intValue() == CBOrderStatusEnum.STATUS_245.getCode().intValue()
                || order.getStatus().intValue() == CBOrderStatusEnum.STATUS_240.getCode().intValue())
            throw new BadRequestException("订单已出库，锁单失败");
        if (!StringUtils.equals("1", order.getIsLock())) {
            Integer dyStatus = getPlatformStatus(order);
            order.setPlatformStatus(dyStatus);
            order.setIsLock("1");
            update(order);
            // 发起富勒订单取消
            ShopInfoDto shopInfoDto = shopInfoService.queryById(order.getShopId());
            ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.findById(shopInfoDto.getServiceId());
            String wmsResult = wmsSupport.cancelOrder(order.getDeclareNo(), clearCompanyInfo.getCustomsCode());
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(order.getStatus()),
                    BooleanEnum.SUCCESS.getCode(),
                    "订单退款中，锁单" + wmsResult
            );
            orderLogService.create(orderLog);
        }

    }

    /**
     * 取消锁单
     *
     * @param order
     */
    @Override
    public void cancelLockOrder(CrossBorderOrder order) throws Exception {
        order = queryById(order.getId());
        Integer dyStatus = getPlatformStatus(order);
        order.setPlatformStatus(dyStatus);
        order.setIsLock("2");
        update(order);

        OrderLog orderLog = new OrderLog(
                order.getId(),
                order.getOrderNo(),
                String.valueOf(order.getStatus()),
                BooleanEnum.SUCCESS.getCode(),
                "订单取消退款，释放锁单"
        );
        orderLogService.create(orderLog);
    }

    @Override
    public void refundOpt(Long id, Integer action, String optionReason) {
        CrossBorderOrder order = crossBorderOrderRepository.findById(id).orElse(null);
        if (order == null)
            throw new BadRequestException("订单不存在");
        if (order.getStatus() >= 240 && order.getStatus() <= 245 && action == 1)
            throw new BadRequestException("订单已称重或者发货，请联系客服及时拦截包裹");
        if (action == 1) {
            //同意退款
            try {
                cancel(id);
            } catch (Exception e) {
                e.printStackTrace();
                if (StringUtil.contains(e.getMessage(), "当前状态不允许取消发货"))
                    throw new BadRequestException(e.getMessage());
                order.setStatus(CBOrderStatusEnum.STATUS_999.getCode());
                order.setFreezeReason("订单已退款，撤单失败");
                update(order);
            }
            if (StringUtil.equals("MeiTuan", order.getPlatformCode())) {
                ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());
                if (shopToken == null)
                    return;
                Map<String, Object> map = new HashMap<>();
                map.put("accessToken", shopToken.getAccessToken());
                MeiTuanOrderRefundAgreeRequest request = new MeiTuanOrderRefundAgreeRequest();
                request.setReason(optionReason);
                request.setOrderId(order.getOrderNo());
                map.put("request", request);
                try {
                    meituanService.refundAgree(com.alibaba.fastjson.JSONObject.toJSONString(map));
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new BadRequestException("调用美团同意退款接口出错:" + e.getMessage());
                }
            }
        } else if (action == 0) {
            //拒绝退款
            order.setIsLock("0");
            update(order);
            ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());
            if (shopToken == null)
                return;
            Map<String, Object> map = new HashMap<>();
            map.put("accessToken", shopToken.getAccessToken());
            MeiTuanOrderRefundRejectRequest request = new MeiTuanOrderRefundRejectRequest();
            request.setReason(optionReason);
            request.setOrderId(order.getOrderNo());
            map.put("request", request);
            try {
                meituanService.refundReject(com.alibaba.fastjson.JSONObject.toJSONString(map));
            } catch (Exception e) {
                e.printStackTrace();
                throw new BadRequestException("调用美团同意退款接口出错:" + e.getMessage());
            }
        } else
            throw new BadRequestException("未知的操作代码");
    }

    @Override
    public void pushWmsOrder(String id) throws Exception {
        // 只有清关完成回传的单子才推送wms订单
        CrossBorderOrder order = queryByIdWithDetails(Long.valueOf(id));
        if (order.getStatus().intValue() != CBOrderStatusEnum.STATUS_235.getCode().intValue())
            throw new BadRequestException("此订单状态无法推送WMS订单");
        ShopInfo shopInfo = shopInfoService.findById(order.getShopId());
        if (StringUtil.equals("1",shopInfo.getPushTo())){
            //处理推送菜鸟的
            PddCloudPrintData printData = printDataService.findByOrderNo(order.getOrderNo());
            if (printData==null){
                PddOrder pddOrder=pddOrderService.getOrderByOrderSn(order.getOrderNo(),order.getPlatformShopId());
                if (pddOrder==null)
                    throw new BadRequestException("获取的拼多多订单为空");
                pddOrder.setReceiverName(order.getConsigneeName());
                pddOrder.setReceiverPhone(order.getConsigneeTel());
                printData=pddOrderService.getMailNo(pddOrder);
                String mailNo=printData.getMailNo();
                order.setLogisticsNo(mailNo);
                printData.setShopCode(order.getPlatformShopId());
                printData.setSender(shopInfo.getName());
                printData.setSenderPhone(shopInfo.getContactPhone());
                cbOrderProducer.send(
                        MsgType.CB_ORDER_PDD_SAVE_PRINTDATA,
                        new JSONObject(printData).toString(),
                        order.getOrderNo()
                );
            }
            if (StringUtils.isBlank(order.getLpCode())) {
                String lpCode = caiNiaoService.sendOrderToWmsAsPdd(order,printData);
                order.setLpCode(lpCode);
                update(order);
            }
            if (StringUtil.isBlank(order.getInvtNo())){
                refreshDecInfo(order.getId()+"");
            }
            cbOrderProducer.delaySend(
                    MsgType.CN_PUSH_CLEAR_SUCC,
                    order.getId()+"",
                    order.getOrderNo(),
                    60000L
            );
            cbOrderProducer.delaySend(
                    MsgType.CN_PUSH_TAX,
                    order.getId()+"",
                    order.getOrderNo(),
                    70000L
            );
            //caiNiaoService.declareResultCallBack(order);
            //caiNiaoService.taxAmountCallback(order);
        }else {
            wmsSupport.pushOrder(order);
        }
    }

    @Override
    public CrossBorderOrder queryByOrderNoAndDeclareNo(String orderNo, String declareNo) {
        return crossBorderOrderRepository.findByOrderNoAndDeclareNo(orderNo, declareNo);
    }

    @Override
    public CrossBorderOrder queryByCrossBorderNoAndDeclareNo(String crossBorderNo, String declareNo) {
        return crossBorderOrderRepository.findByCrossBorderNoAndDeclareNo(crossBorderNo, declareNo);
    }




    @Override
    public CrossBorderOrder queryByCrossBorderNo(String orderNo) {
        return crossBorderOrderRepository.findByCrossBorderNo(orderNo);
    }

    @Override
    public void upgradeLogisticsInfo() {
        List<CrossBorderOrder> unCollectedOrders = crossBorderOrderRepository.findAllUnCollectedOrder();
        if (CollectionUtils.isEmpty(unCollectedOrders))
            return;
        for (CrossBorderOrder unCollectedOrder : unCollectedOrders) {
            cbOrderProducer.send(
                    MsgType.CB_UPGRADEUNCOLLECTEDORDER,
                    String.valueOf(unCollectedOrder.getId()),
                    unCollectedOrder.getOrderNo()
            );
        }
    }

    @Override
    public void checkCollected(String orderId) throws Exception{
        try {
            CrossBorderOrder order = queryById(Long.valueOf(orderId));
            if (4 == order.getSupplierId().intValue()) {
                com.alibaba.fastjson.JSONObject response = gztoSupport.getTrance(order.getLogisticsNo());
                if (response.getBoolean("status")) {
                    com.alibaba.fastjson.JSONArray respData = response.getJSONArray("data");
                    if (respData != null && respData.size() > 0) {
                        // 这就算已揽收
                        order.setLogisticsStatus(2);// 已揽收
                        order.setLogisticsCollectTime(DateUtils.parseDateTime(respData.getJSONObject(0).getJSONObject("traces").getString("scanDate")));
                        update(order);
                    }
                }
            }else if (7 == order.getSupplierId().intValue()){
                // 顺丰
                JSONObject response = sfgjSupport.queryRoute(order.getLogisticsNo(), order.getConsigneeTel().substring(order.getConsigneeTel().length() - 4));
                if (response.getJSONObject("apiResultData") != null &&
                        response.getJSONObject("apiResultData").getBool("success")) {
                    JSONArray routesArray = response.getJSONObject("apiResultData").getJSONObject("msgData").getJSONArray("routeResps").getJSONObject(0).getJSONArray("routes");
                    if (CollectionUtils.isNotEmpty(routesArray)) {
                        order.setLogisticsStatus(2);// 已揽收
                        order.setLogisticsCollectTime(new Timestamp(System.currentTimeMillis()));
                        update(order);
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void refreshDecInfo() {
        // 1.查询所有取消订单但取消状态不是99的
        List<CrossBorderOrder> all1 = crossBorderOrderRepository.findCancelNoCucc();
        if (CollectionUtils.isNotEmpty(all1)) {
            for (CrossBorderOrder order : all1) {
                cbOrderProducer.send(
                        MsgType.CB_UPGRAD_DECINFO,
                        String.valueOf(order.getId()),
                        order.getOrderNo()
                );
            }
        }
        // 2.查询所有已出库订单但是总署清单编号为空的
        List<CrossBorderOrder> all2 = crossBorderOrderRepository.findDeliverNoInvtNo();
        if (CollectionUtils.isNotEmpty(all2)) {
            for (CrossBorderOrder order : all2) {
                cbOrderProducer.send(
                        MsgType.CB_UPGRAD_DECINFO,
                        String.valueOf(order.getId()),
                        order.getOrderNo()
                );
            }
        }
    }

    // 弃用
    @Override
    public void confirmClearDelStart() {
        List<CrossBorderOrder>list=crossBorderOrderRepository.findByPlatformCodeAndStatusAndClearDelStartBackTimeIsNullAndDeclareNoIsNotNull("DY",CBOrderStatusEnum.STATUS_888.getCode());
        if (CollectionUtils.isEmpty(list))
            return;
        for (CrossBorderOrder order : list) {
            cbOrderProducer.send(MsgType.CB_ORDER_880,order.getId()+"",order.getOrderNo());
        }
    }

    // 弃用
    @Override
    public void confirmClearDelSucc() {
        List<CrossBorderOrder>list=crossBorderOrderRepository.findByPlatformCodeAndStatusAndClearDelSuccessBackTimeIsNullAndClearDelStartBackTimeIsNotNullAndDeclareNoIsNotNull("DY",CBOrderStatusEnum.STATUS_888.getCode());
        if (CollectionUtils.isEmpty(list))
            return;
        for (CrossBorderOrder order : list) {
            cbOrderProducer.send(MsgType.CB_ORDER_884,order.getId()+"",order.getOrderNo());
        }
    }

    // 弃用
    @Override
    public void confirmCloseOrder() {
        List<CrossBorderOrder>list=crossBorderOrderRepository.findByPlatformCodeAndStatusAndCancelTimeBackIsNullAndInvtNoIsNullAndDeclareNoIsNotNull("DY",CBOrderStatusEnum.STATUS_888.getCode());
        if (CollectionUtils.isEmpty(list))
            return;
        for (CrossBorderOrder order : list) {
            cbOrderProducer.delaySend(MsgType.CB_ORDER_886,order.getId()+"",order.getOrderNo(),3000);
        }
    }

    /**
     * 刷新海关撤单状态
     * 查询所有已取消，declareNo存在，状海关状态非99的的单据
     */
    @Override
    public void updateDecDelStatus() {
        List<CrossBorderOrder> list = crossBorderOrderRepository.findNoDecDel();
        if (CollectionUtils.isNotEmpty(list)) {
            for (CrossBorderOrder order : list) {
                cbOrderProducer.send(MsgType.CB_ORDER_DEC_DEL, String.valueOf(order.getId()),order.getOrderNo());
            }
        }
    }

    @Override
    public void updateDecDelStatus(Long id) throws Exception {
        CrossBorderOrder order = queryById(Long.valueOf(id));
        String res = kjgSupport.refresh(order);
        JSONObject resJSON = JSONUtil.xmlToJson(res);
        JSONObject resHeader = resJSON.getJSONObject("Message").getJSONObject("Header");
        if (StringUtil.equals("T", resHeader.getStr("Result"))) {
            JSONObject mft = resJSON.getJSONObject("Message").getJSONObject("Body").getJSONObject("Mft");
            String ManifestId = mft.getStr("ManifestId");
            String Status = mft.getStr("Status");
            order.setInvtNo(ManifestId);// 保存总署清单编号
            order.setDeclareStatus(Status);
            update(order);
            if (StringUtils.equals(PlatformConstant.DY, order.getPlatformCode()) && StringUtils.equals("99", Status)) {
                // 撤单成功，回传抖音
                if (order.getClearSuccessTime() != null && order.getClearDelStartBackTime() == null) {
                    cbOrderProducer.send(MsgType.CB_ORDER_880,order.getId()+"",order.getOrderNo());
                }
                if (order.getClearSuccessTime() != null && order.getClearDelSuccessBackTime() == null) {
                    cbOrderProducer.delaySend(MsgType.CB_ORDER_884,order.getId()+"",order.getOrderNo(),3000);
                }
                if (order.getClearSuccessTime() == null && order.getCancelTimeBack() == null) {
                    cbOrderProducer.delaySend(MsgType.CB_ORDER_886,order.getId()+"",order.getOrderNo(),3000);
                }
            }
        } else {
            log.info("刷新海关撤单状态失败：{}， {}", order.getOrderNo(), resHeader.getStr("ResultMsg"));
        }
    }



    @Override
    public void refreshDecInfo(String orderId) throws Exception {
        CrossBorderOrder order = queryById(Long.valueOf(orderId));
        String res = kjgSupport.refresh(order);
        JSONObject resJSON = JSONUtil.xmlToJson(res);
        JSONObject resHeader = resJSON.getJSONObject("Message").getJSONObject("Header");
        if (StringUtil.equals("T", resHeader.getStr("Result"))) {
            JSONObject mft = resJSON.getJSONObject("Message").getJSONObject("Body").getJSONObject("Mft");
            String ManifestId = mft.getStr("ManifestId");
            String Status = mft.getStr("Status");
            order.setInvtNo(ManifestId);// 保存总署清单编号
            order.setDeclareStatus(Status);
            update(order);
        } else {
            throw new BadRequestException(resHeader.getStr("ResultMsg"));
        }
    }

    // 处理卡接单状态订单
    @Override
    public void handel200() throws Exception {
        CrossBorderOrderQueryCriteria criteria = new CrossBorderOrderQueryCriteria();
        List<Integer> status = new ArrayList<>();
        status.add(CBOrderStatusEnum.STATUS_200.getCode());
        criteria.setStatus(status);
        criteria.setPreSell("0");
        List<CrossBorderOrder> all = crossBorderOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        if (CollectionUtils.isNotEmpty(all)) {
            for (CrossBorderOrder order : all) {
                if (!StringUtil.equals(order.getUpStatus(), "102")
                        && !StringUtil.equals(order.getUpStatus(), "100")
                        && !StringUtils.isEmpty(order.getBuyerName())) {
                    cbOrderProducer.send(
                            MsgType.CB_ORDER_215,
                            String.valueOf(order.getId()),
                            order.getOrderNo()
                    );
                }/*else {
                    deleteAll(new Long[]{order.getId()});
                    douyinService.pullOrderById(String.valueOf(order.getShopId()), order.getOrderNo());
                }*/
            }
        }
    }

    @Override
    public void refreshWmsStatus() {
        CrossBorderOrderQueryCriteria criteria = new CrossBorderOrderQueryCriteria();
        List<Integer> status = new ArrayList<>();
        status.add(CBOrderStatusEnum.STATUS_235.getCode());
        criteria.setStatus(status);

        List<CrossBorderOrder> all = crossBorderOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        if (CollectionUtils.isNotEmpty(all)) {
            for (CrossBorderOrder order : all) {
                if (StringUtils.isNotEmpty(order.getDeclareNo())) {
                    cbOrderProducer.send(
                            MsgType.CB_ORDER_WMS,
                            String.valueOf(order.getId()),
                            order.getOrderNo()
                    );
                }
            }
        }
    }

    @Override
    public void updateOrderWmsStatus(String data) {
        if (StringUtils.isEmpty(data))
            throw new BadRequestException("data为空");
        JSONObject dataObject = JSONUtil.parseObj(data);
        JSONObject orderObject = dataObject.getJSONObject("xmldata").getJSONObject("data").getJSONArray("orderinfo").getJSONObject(0);
        String orderNo = orderObject.getStr("docNo");
        CrossBorderOrder order = queryByCrossBorderNo(orderNo);
        if (order != null ) {
            order.setSoNo(orderObject.getStr("orderNo"));
            order.setWmsStatus(orderObject.getStr("userDefine1"));
            order.setIsWave("1");
            order.setWaveNo(orderObject.getStr("soReferenceA"));
            order.setIsPrint("1");
            order.setBillPrintInfo(orderObject.getStr("soReferenceB"));
            order.setPickPrintInfo(orderObject.getStr("soReferenceC"));
            order.setWaveName(orderObject.getStr("userDefine3"));
            if (StringUtils.contains(order.getWaveName(), "单品")) {
                order.setPickType("1");
            }else {
                order.setPickType("2");
            }
            order.setSeqNo(orderObject.getStr("userDefine2"));
            order.setArea(orderObject.getStr("userDefine4"));
            update(order);

        }
        if (StringUtils.equals("0", order.getSendPickFlag())
                || StringUtils.isEmpty(order.getSendPickFlag())) {
            // 回传拣货
            cbOrderProducer.send(
                    MsgType.CB_ORDER_236,
                    String.valueOf(order.getId()),
                    order.getOrderNo()
            );
        }
    }

    @Override
    public void refreshWmsStatus(String orderId) {
        CrossBorderOrder order = queryById(Long.valueOf(orderId));

        JSONObject wmsOrder = wmsSupport.querySo(order.getCrossBorderNo(), order.getDeclareNo());
        if (wmsOrder == null)
            throw new BadRequestException("获取WMS状态失败:" + order.getOrderNo());
        order.setWmsStatus(wmsOrder.getJSONObject("header").getStr("sostatus"));
        String waveNo = wmsOrder.getJSONObject("header").getStr("waveno");
        String soNo = wmsOrder.getJSONObject("header").getStr("orderno");
        if (!StringUtils.equals(order.getSoNo(), soNo)) {
            order.setSoNo(soNo);
        }
        if (!StringUtils.equals("*", waveNo)
                && !StringUtils.equals(waveNo, order.getWaveNo())) {
            order.setIsWave("1");
            order.setWaveNo(waveNo);
            if (StringUtils.equals("0", order.getSendPickFlag())
                    || StringUtils.isEmpty(order.getSendPickFlag())) {
                // 回传拣货
                cbOrderProducer.send(
                        MsgType.CB_ORDER_236,
                        String.valueOf(order.getId()),
                        order.getOrderNo()
                );
            }
        }
        String billPrintInfo = wmsOrder.getJSONObject("header").getStr("invoiceno");
        if (StringUtils.isNotEmpty(billPrintInfo)) {
            order.setIsPrint("1");
            order.setBillPrintInfo(billPrintInfo);
        }
        order.setPickPrintInfo(wmsOrder.getJSONObject("header").getStr("iAddress1"));
        update(order);
    }

    @Override
    public void refreshWmsStatusAndCancel(String body) throws Exception {
        CrossBorderOrder order = queryById(Long.parseLong(body));
        Integer statusCode = getPlatformStatus(order);
        order.setPlatformStatus(statusCode);
        update(order);
        if (statusCode==16||statusCode==17||statusCode==21){
            //取消订单
            cancel(order.getId());
        }
    }

    @Override
    public void refreshPlatformStatus() {
        CrossBorderOrderQueryCriteria criteria = new CrossBorderOrderQueryCriteria();
        criteria.setNotEqPlatformStatus(3);
        List<Integer> status = new ArrayList<>();
        status.add(CBOrderStatusEnum.STATUS_245.getCode());
        criteria.setStatus(status);
        criteria.setPlatformCode(PlatformConstant.DY);

        List<CrossBorderOrder> all = crossBorderOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        if (CollectionUtils.isNotEmpty(all)) {
            for (CrossBorderOrder order : all) {
                cbOrderProducer.send(
                        MsgType.CB_REFRESH_ORDER_STATUS,
                        String.valueOf(order.getId()),
                        order.getOrderNo()
                );

            }
        }
    }

    @Override
    public void refreshPlatformStatus2() {
        List<CrossBorderOrder> all = crossBorderOrderRepository.refreshPlatformStatus2();
        if (CollectionUtils.isNotEmpty(all)) {
            for (CrossBorderOrder order : all) {
                cbOrderProducer.send(
                        MsgType.CB_REFRESH_ORDER_STATUS_AND_CANCEL,
                        String.valueOf(order.getId()),
                        order.getOrderNo()
                );
            }
        }
    }



    @Override
    public void refreshPlatformStatus(String body) throws Exception {
        CrossBorderOrder order = queryById(Long.parseLong(body));
        Integer statusCode = getPlatformStatus(order);
        order.setPlatformStatus(statusCode);
        update(order);
    }

    @Override
    public void refreshPlatformCancelStatus() {
        CrossBorderOrderQueryCriteria criteria = new CrossBorderOrderQueryCriteria();
        criteria.setNotEqPlatformStatus(21);
        List<Integer> status = new ArrayList<>();
        status.add(CBOrderStatusEnum.STATUS_888.getCode());
        criteria.setStatus(status);
        criteria.setPlatformCode(PlatformConstant.DY);

        List<CrossBorderOrder> all = crossBorderOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        if (CollectionUtils.isNotEmpty(all)) {
            for (CrossBorderOrder order : all) {
                cbOrderProducer.send(
                        MsgType.CB_REFRESH_ORDER_STATUS,
                        String.valueOf(order.getId()),
                        order.getOrderNo()
                );

            }
        }
    }

    @Override
    public void updateWmsOrderTime() {
        CrossBorderOrderQueryCriteria criteria = new CrossBorderOrderQueryCriteria();
        criteria.setDefault01("1");// 未更新过wms时间
        criteria.setPlatformCode(PlatformConstant.DY);
        List<Integer> status = new ArrayList<>();
        status.add(CBOrderStatusEnum.STATUS_220.getCode());
        status.add(CBOrderStatusEnum.STATUS_225.getCode());
        status.add(CBOrderStatusEnum.STATUS_227.getCode());
        status.add(CBOrderStatusEnum.STATUS_230.getCode());
        status.add(CBOrderStatusEnum.STATUS_237.getCode());
        criteria.setStatus(status);
        List<CrossBorderOrder> all = crossBorderOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        if (CollectionUtils.isNotEmpty(all)) {
            for (CrossBorderOrder order : all) {
                cbOrderProducer.send(
                        MsgType.CB_UPDATE_WMS_TIME,
                        String.valueOf(order.getId()),
                        order.getOrderNo()
                );
            }
        }
    }

    @Override
    public void updateWmsOrderTime(String body) {
        CrossBorderOrder order = queryById(Long.parseLong(body));
        if (!StringUtil.equals("1", order.getDefault01())) {
            // 更新时间
            wmsSupport.updateOrderTime(order.getOrderNo(), order.getOrderCreateTime());
            order.setDefault01("1");
            update(order);
        }
    }

    // 出库逻辑
    @Override
    public String deliver(String weight, String mailNo, String materialCode) throws Exception {
        Long start = new Date().getTime();
        String res = "";
        CrossBorderOrder order = queryByMailNo(mailNo);
        log.info("抖音出库查询订单花费时间：" + (new Date().getTime() - start));
        if (order == null)
            throw new BadRequestException("该运单号不存在本系统，请检查是否为其他系统运单");
        if (StringUtils.isNotBlank(order.getLpCode()))
            throw new BadRequestException("该运单号是菜鸟订单，请从菜鸟出库");
        if (StringUtils.equals(order.getDefault05(), "2")) {
            // 系统中原来无此订单，从跨境购下发的
            throw new BadRequestException("此订单请从老系统出库");
        }

        if (order.getStatus().intValue() == CBOrderStatusEnum.STATUS_888.getCode().intValue()) {
            throw new BadRequestException("订单已取消，请将包裹放置在取消暂存区");
        }
        if (order.getStatus().intValue() == CBOrderStatusEnum.STATUS_245.getCode().intValue()) {
            throw new BadRequestException("订单已发货，请确认是否重复扫描发货");
        }
        if (order.getStatus().intValue() == CBOrderStatusEnum.STATUS_240.getCode().intValue()) {
            throw new BadRequestException("订单已称重，请确认是否重复扫描发货");
        }
        if (StringUtils.equals("1", order.getIsLock()))
            throw new BadRequestException("订单退款申锁单，请撕掉面单并还货");
        if (StringUtils.equals("2", order.getIsLock()))
            throw new BadRequestException("订单取消锁单，请撕掉面单并还货");
        if (StringUtils.equals("3", order.getIsLock()))
            throw new BadRequestException("订单还未锁单恢复，请联系打单恢复");
        if (order.getStatus().intValue() != CBOrderStatusEnum.STATUS_235.getCode().intValue())
            throw new BadRequestException("未清关完成回传的订单不允许出库，当前状态：" + CBOrderStatusEnum.getDesc(order.getStatus()));


        if (StringUtils.isBlank(materialCode) || StringUtils.equals("null", materialCode))
            throw new BadRequestException("请输入包材");

        if (StringUtil.equals("1",order.getFourPl())) {
            order.setMaterialCode(materialCode);
            PackageInfo packageInfo = packageInfoService.queryByPackageCode(order.getMaterialCode());
            if (packageInfo == null)
                throw new BadRequestException("此订单采集的包材不存在：" + order.getMaterialCode());
            if (!StringUtils.equals("BC", packageInfo.getPackageType()))
                throw new BadRequestException("此订单采集的不是包材：" + order.getMaterialCode());
            if (!StringUtils.equals("DY", packageInfo.getPlatformCode()))
                throw new BadRequestException("此订单采集的包材不是抖音包材：" + order.getMaterialCode());
        }
        log.info("抖音出库基础校验花费时间：" + (new Date().getTime() - start));
        try {
            // 重量校验
            order.setPackWeight(weight);
            checkTheoryWeight(order);

            // 请求出库之前必须要查询抖音订单的状态
            Integer dyStatus = getPlatformStatus(order);
            log.info("抖音出库平台状态花费时间：" + (new Date().getTime() - start));
            // 2-备货中、25-用户取消退款申请。这两个状态可发货
            // 3-已发货、5-已签收(需要再次确认)
            // 16-申请退款中、17-商家同意退款、25-取消退款、可发货
            if (dyStatus.intValue() == 3) {
                throw new BadRequestException("订单已发货，请确认是否重复扫描发货");
            }
            if (dyStatus.intValue() == 16 || dyStatus.intValue() == 17 || dyStatus.intValue() == 21) {
                // 退款申请中或已同意退款，直接取消
                // 取消申报单
                try {
                    decrypt(order);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new BadRequestException("订单解密失败" + e.getMessage());
                }
                if (StringUtils.equals(PlatformConstant.DY, order.getPlatformCode()) && StringUtils.equals("2", order.getDefault02())) {
                    // 抖音自申报的单子，不用我们取消
                    throw new BadRequestException("订单已取消，请将包裹放置在取消暂存区");
                }
                try {
                    encrypt(order);
                } catch (Exception e) {
                    throw new BadRequestException("订单加密失败：" + e.getMessage());
                }
                /*String canRes = kjgSupport.cancelOrder(order);
                JSONObject canResJSON = JSONUtil.xmlToJson(canRes);
                JSONObject canResHeader = canResJSON.getJSONObject("Message").getJSONObject("Header");*/
                try {
                    cancel(order.getId());
                }catch (Exception e){
                    throw new BadRequestException("用户已退款，请将包裹放置在取消暂存区,且申报单取消失败，请联系技术:" + (e.getMessage()==null?"Null":e.getMessage()));
                }
                throw new BadRequestException("用户已退款，请将包裹放置在取消暂存区");
                /*if (StringUtil.equals("T", canResHeader.getStr("Result"))) {
                    order.setCancelTime(new Timestamp(System.currentTimeMillis()));
                    order.setStatus(CBOrderStatusEnum.STATUS_888.getCode());
                    if (StringUtil.isNotBlank(order.getInvtNo())) {
                        order.setClearDelStartTime(DateUtils.now());//有总署单号
                        order.setDeclareStatus("29");//清单撤销中
                    }
                    update(order);
                    OrderLog orderLog = new OrderLog(
                            order.getId(),
                            order.getOrderNo(),
                            String.valueOf(CBOrderStatusEnum.STATUS_888.getCode()),
                            BooleanEnum.SUCCESS.getCode(),
                            BooleanEnum.SUCCESS.getDescription()
                    );
                    orderLogService.create(orderLog);

                } else {
                    // 发起取消失败
                }*/
            }
            if (dyStatus.intValue() == 2 || dyStatus.intValue() == 25) {
                // 正常出库
                res = kjgSupport.deliver(weight, mailNo);
                log.info("抖音出库跨境购请求花费时间：" + (new Date().getTime() - start));
                JSONObject resJson = XML.toJSONObject(res);
                JSONObject wmsFxResponse = resJson.getJSONObject("wmsFxResponse");
                Boolean success = wmsFxResponse.getBool("success");
                String resultCode = wmsFxResponse.getStr("resultCode");
                String default01 = wmsFxResponse.getStr("default01");
                if (success) {
                    if (StringUtil.equals("02", resultCode)
                            || StringUtil.equals("04", resultCode)) {
                        if (order.getStatus().intValue() == CBOrderStatusEnum.STATUS_235.getCode()) {
                            if (StringUtils.equals(order.getPlatformCode(), PlatformConstant.DY)) {
                                // 如果是新模式订单，出库的时候还需要请求富勒出库
                                if (StringUtils.isEmpty(order.getSoNo()))
                                    throw new BadRequestException("新模式订单无SO单号，请联系技术");
                                wmsSupport.deliver(order.getSoNo());
                            }

                            // 货物放行成功，更改订单状态，通知MQ消息处理
                            order.setDeclareStatus("24");
                            order.setDeclareMsg("货物放行");
                            if (StringUtils.isEmpty(order.getPackWeight())) {
                                order.setPackWeight(weight);
                            }
                            order.setStatus(CBOrderStatusEnum.STATUS_240.getCode());
                            order.setWeighingTime(new Timestamp(System.currentTimeMillis()));
                            order.setPackTime(new Timestamp(System.currentTimeMillis() - 1 * 60 * 1000));// 往前推1分钟

                            update(order);
                            log.info("抖音出库更新订单花费时间：" + (new Date().getTime() - start));

                            cbOrderProducer.send(
                                    MsgType.CB_ORDER_240,
                                    String.valueOf(order.getId()),
                                    order.getOrderNo()
                            );
                            if (StringUtils.isNotEmpty(SecurityUtils.getCurrentUsernameForNull())) {
                                OrderDeliverLog odlog = new OrderDeliverLog(
                                        order.getShopId(),
                                        order.getPlatformCode(),
                                        SecurityUtils.getCurrentUsername(),
                                        order.getOrderNo(),
                                        order.getLogisticsNo(),
                                        "",
                                        weight,
                                        "",
                                        res,
                                        (new Date().getTime() - start)
                                );
                                orderDeliverLogService.create(odlog);
                            }
                            log.info("抖音出库总花费时间：" + (new Date().getTime() - start));

                            if (order.getSupplierId() != null) {
                                LogisticsInfo logisticsInfo = logisticsInfoService.queryById(order.getSupplierId());
                                default01 = logisticsInfo.getKjgCode();
                                if (StringUtils.equals("73698071-8-1", default01)) {
                                    default01 = "73698071-8";
                                }
                            }
                        }else {
                            throw new BadRequestException("订单状态不正确");
                        }

                    } else if (StringUtil.equals("03", resultCode)
                            || StringUtil.equals("05", resultCode)) {
                        // 抽检
                        throw new BadRequestException("海关抽检，请将包裹交给质控");
                    } else if (StringUtil.equals("04", resultCode)) {
                        // 重复扫描
                        throw new BadRequestException("重复扫描，海关已货物放行");
                    } else if (StringUtil.equals("01", resultCode)) {
                        // 取消中
                        throw new BadRequestException("系统异常，请联系技术");
                    } else if (StringUtil.equals("00", resultCode)) {
                        // 异常状态
                        throw new BadRequestException("系统异常，请联系技术");
                    } else {
                        throw new BadRequestException("未知异常，请联系技术");
                    }
                    return default01;
                } else {
                    throw new BadRequestException("系统异常，请联系技术");
                }
            } else {
                throw new BadRequestException("系统异常，请联系技术");
            }
        } catch (Exception e) {
            if (StringUtils.isNotEmpty(SecurityUtils.getCurrentUsernameForNull())) {
                OrderDeliverLog log = new OrderDeliverLog(
                        order.getShopId(),
                        order.getPlatformCode(),
                        SecurityUtils.getCurrentUsername(),
                        order.getOrderNo(),
                        order.getLogisticsNo(),
                        "",
                        weight,
                        "",
                        res,
                        (new Date().getTime() - start)
                );
                orderDeliverLogService.create(log);
            }
            try {
                encrypt(order);
            } catch (Exception e2) {
                e2.printStackTrace();
                throw new BadRequestException("订单加密失败" + e2.getMessage());
            }
            throw new BadRequestException("订单出库失败" + e.getMessage());
        }
    }

    // 校验重量
    @Override
    public void checkTheoryWeight(CrossBorderOrder order) {
        if (StringUtils.isBlank(order.getTheoryWeight()))
            throw new BadRequestException("理论重量为空");
        if (StringUtils.isBlank(order.getPackWeight()))
            throw new BadRequestException("实际重量为空");
        BigDecimal theoryWeight = new BigDecimal(order.getTheoryWeight());
        BigDecimal packWeight = new BigDecimal(order.getPackWeight());
        if (theoryWeight.compareTo(BigDecimal.ZERO) == 0)
            throw new BadRequestException("理论重量为0");
        if (packWeight.compareTo(BigDecimal.ZERO) == 0)
            throw new BadRequestException("实际重量为0");
        if (packWeight.compareTo(BigDecimal.ZERO) < 0)
            throw new BadRequestException("实际重量不能为负数");
        if (packWeight.compareTo(new BigDecimal(20)) > 0)
            throw new BadRequestException("实际重量不能大于20KG");

        BigDecimal subtract = theoryWeight.subtract(packWeight).abs();
        BigDecimal divide = subtract.divide(packWeight, 2);
        Config config = configService.queryByK("WEIGH_FLOAT");
        if (config == null)
            throw new BadRequestException("称重浮动值未配置，请联系技术");
        BigDecimal v = new BigDecimal(config.getV());
        if (divide.compareTo(v) == 1)
            throw new BadRequestException("理论重量差异较大，请检查");
    }
    // 批量出库
    @Override
    public JSONObject batchDeliver(String weight, String waveNo, String materialCode) {
        if (StringUtils.isBlank(waveNo))
            throw new BadRequestException("波次号不能为空");
        if (StringUtils.isBlank(materialCode))
            throw new BadRequestException("包材编码不能为空");
        if (StringUtils.isBlank(weight))
            throw new BadRequestException("重量不能为空");

        List<CrossBorderOrder> list = queryByWaveNo(waveNo);
        if (CollectionUtils.isEmpty(list))
            throw new BadRequestException("该波次查询订单为空，请联系技术");
        PackageInfo packageInfo = packageInfoService.queryByPackageCode(materialCode);
        if (packageInfo == null)
            throw new BadRequestException("包材编码不存在");
        for (CrossBorderOrder order : list) {
            if (order.getStatus().intValue() == CBOrderStatusEnum.STATUS_245.getCode().intValue())
                throw new BadRequestException("订单已发货，请确认是否重复扫描发货");
            if (order.getStatus().intValue() == CBOrderStatusEnum.STATUS_240.getCode().intValue())
                throw new BadRequestException("订单已称重，请确认是否重复扫描发货");
            if (!StringUtils.equals(PlatformConstant.DY, order.getPlatformCode()))
                throw new BadRequestException("该波次不是抖音波次，不能批量出库");
            if (!StringUtils.contains(order.getWaveName(),"单品"))
                throw new BadRequestException("该波次不是批量单，不能批量出库");
            if (StringUtils.equals("1", order.getFourPl()) && !StringUtils.equals("DY", packageInfo.getPlatformCode()))
                throw new BadRequestException("该单为4PL单，必须用抖音纸箱：" + order.getLogisticsNo());
        }

        CrossBorderOrder orderT = list.get(0);
        orderT.setPackWeight(weight);
        checkTheoryWeight(orderT);

        int outSucc = 0;
        StringBuilder SucStr = new StringBuilder();
        SucStr.append("出库成功：");
        StringBuilder errStr = new StringBuilder();
        for (CrossBorderOrder order : list) {
            try {
                deliver(weight, order.getLogisticsNo(), materialCode);
                outSucc = outSucc + 1;
                // 不报异常出库成功
            } catch (Exception e) {
                errStr.append(e.getMessage()).append(":").append(order.getLogisticsNo()).append("---").append(order.getSeqNo()).append("号格").append(" ");
            }
            order.setMaterialCode(materialCode);
            order.setPackWeight(weight);
            update(order);
        }
        SucStr.append(outSucc).append("单");
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOnce("SucStr", SucStr.toString());
        jsonObject.putOnce("errStr", errStr.toString());
        return jsonObject;
    }

    // 波次包材变化
    @Override
    public BigDecimal materialCodeChange(String waveNo, String materialCode) {
        if (StringUtils.isBlank(waveNo))
            throw new BadRequestException("波次号不能为空");
        if (StringUtils.isBlank(materialCode))
            throw new BadRequestException("包材编码不能为空");
        List<CrossBorderOrder> list = queryByWaveNo(waveNo);
        if (CollectionUtils.isEmpty(list))
            throw new BadRequestException("该波次查询订单为空，请联系技术");
        PackageInfo packageInfo = packageInfoService.queryByPackageCode(materialCode);
        if (packageInfo == null)
            throw new BadRequestException("包材编码不存在");

        for (CrossBorderOrder order : list) {
            if (!StringUtils.equals(PlatformConstant.DY, order.getPlatformCode()))
                throw new BadRequestException("该波次不是抖音波次，不能批量出库");
            if (!StringUtils.contains(order.getWaveName(),"单品"))
                throw new BadRequestException("该波次不是批量单，不能批量出库");
            if (StringUtils.equals("1", order.getFourPl()) && !StringUtils.equals("DY", packageInfo.getPlatformCode()))
                throw new BadRequestException("该单为4PL单，必须用抖音纸箱：" + order.getLogisticsNo());
        }
        CrossBorderOrder orderT = list.get(0);
        orderT.setMaterialCode(materialCode);

        BigDecimal totalTheoryWeight = countTheoryWeight(orderT);

        // 保存理论重量
        for (CrossBorderOrder order : list) {
            order.setTheoryWeight(String.valueOf(totalTheoryWeight));
            order.setMaterialCode(materialCode);
            update(order);
        }
        return totalTheoryWeight;
    }

    @Override
    public CrossBorderOrder queryMaterialCodeByMailNo(String mailNo) {
        CrossBorderOrder order = queryByMailNo(mailNo);
        if (order.getStatus().intValue() == CBOrderStatusEnum.STATUS_888.getCode().intValue()) {
            throw new BadRequestException("订单已取消，请将包裹放置在取消暂存区");
        }
        if (order.getStatus().intValue() == CBOrderStatusEnum.STATUS_245.getCode().intValue()) {
            throw new BadRequestException("订单已发货，请确认是否重复扫描发货");
        }
        if (order.getStatus().intValue() == CBOrderStatusEnum.STATUS_240.getCode().intValue()) {
            throw new BadRequestException("订单已称重，请确认是否重复扫描发货");
        }
        if (StringUtils.equals("1", order.getIsLock()))
            throw new BadRequestException("订单退款申锁单，请撕掉面单并还货");
        if (StringUtils.equals("2", order.getIsLock()))
            throw new BadRequestException("订单取消锁单，请撕掉面单并还货");
        if (StringUtils.equals("3", order.getIsLock()))
            throw new BadRequestException("订单还未锁单恢复，请联系打单恢复");
        if (order.getStatus().intValue() != CBOrderStatusEnum.STATUS_235.getCode().intValue())
            throw new BadRequestException("未清关完成回传的订单不允许出库，当前状态：" + CBOrderStatusEnum.getDesc(order.getStatus()));
        if (StringUtils.isEmpty(order.getSoNo())) {
            refreshWmsStatus(String.valueOf(order.getId()));
        }
        if (StringUtils.isBlank(order.getMaterialCode())) {
            if (StringUtils.isNotBlank(order.getSoNo())) {
                DocOrderPackingSummary docOrderPackingSummary = wmsSupport.getDocOrderPackingSummary(order.getSoNo());
                if (docOrderPackingSummary != null) {
                    String cartongroup = docOrderPackingSummary.getCartongroup();
                    if (StringUtils.isNotEmpty(cartongroup)) {
                        order.setMaterialCode(cartongroup);
                    }
                }
            }
        }
        if (StringUtils.isNotBlank(order.getMaterialCode())) {
            // 判断4PL单
            PackageInfo packageInfo = packageInfoService.queryByPackageCode(order.getMaterialCode());
            if (packageInfo == null) {
                order.setMaterialCode("");
            }
            if (packageInfo != null ) {
                if (!StringUtils.equals("BC", packageInfo.getPackageType())) {
                    order.setMaterialCode("");
                }
                if (StringUtils.equals("1", order.getFourPl()) && !StringUtils.equals("DY", packageInfo.getPlatformCode())) {
                    order.setMaterialCode("");
                }
            }
        }
        BigDecimal totalTheoryWeight = countTheoryWeight(order);
        order.setTheoryWeight(String.valueOf(totalTheoryWeight));
        update(order);
        return order;
    }

    @Override
    public CrossBorderOrder materialCodeChangeByMailNo(String mailNo, String materialCode) {
        if (StringUtils.isBlank(materialCode))
            throw new BadRequestException("包材编码不能为空");
        PackageInfo packageInfo = packageInfoService.queryByPackageCode(materialCode);
        if (packageInfo == null)
            throw new BadRequestException("包材编码未维护在系统");
        CrossBorderOrder order = queryByMailNo(mailNo);
        if (StringUtils.equals("1", order.getFourPl()) && !StringUtils.equals("DY", packageInfo.getPlatformCode()))
            throw new BadRequestException("该单为4PL单，必须用抖音纸箱：" + order.getLogisticsNo());
        order.setMaterialCode(materialCode);
        BigDecimal theoryWeight = countTheoryWeight(order);
        order.setTheoryWeight(String.valueOf(theoryWeight));
        update(order);
        return order;
    }

    //波次状态查询
    @Override
    public JSONObject batchDeliverScan(String waveNo) {
        if (StringUtils.isBlank(waveNo))
            throw new BadRequestException("波次号必填");
        List<CrossBorderOrder> list = queryByWaveNo(waveNo);
        if (CollectionUtils.isEmpty(list))
            throw new BadRequestException("该波次号查询到的订单为空");
        StringBuilder SucStr = new StringBuilder();
        SucStr.append("出库成功：");
        StringBuilder errStr = new StringBuilder();
        int outNum = 0;
        for (CrossBorderOrder order : list) {
            Integer status = order.getStatus();
            if (status.intValue() != CBOrderStatusEnum.STATUS_240.getCode() &&
                    status.intValue() != CBOrderStatusEnum.STATUS_245.getCode()) {
                errStr.append(order.getLogisticsNo()).append("(").append(CBOrderStatusEnum.getDesc(status)).append(")").append("---").append(order.getSeqNo()).append("号格").append("; ");
            }else {
                outNum = outNum + 1;
            }
        }
        SucStr.append(outNum).append("单");
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOnce("SucStr", SucStr.toString());
        jsonObject.putOnce("errStr", errStr.toString());
        return jsonObject;
    }

    @Override
    public void clearDelIsSucc(String dayStr) {
        List<CrossBorderOrder>list=crossBorderOrderRepository.clearDelIsSucc();
        if (CollectionUtils.isNotEmpty(list)){
            StringBuilder builder=new StringBuilder();
            for (CrossBorderOrder order : list) {
                try {
                    Date clearDelStartTime=DateUtils.parseDateTime(order.getClearDelStartTime());
                    int day;
                    if (StringUtil.isBlank(dayStr))
                        day=4;
                    else
                        day=Integer.parseInt(dayStr);
                    long timeout=System.currentTimeMillis()-clearDelStartTime.getTime();
                    String declareStatus=kjgSupport.getDeclareStatus(order);
                    if (StringUtil.equals(declareStatus,"29")){
                        if (timeout>day*24*3600*1000L){
                            //邮件预警
                            builder.append("订单号:").append(order.getOrderNo()).append("清单撤销中已超时</br>");
                        }
                    }else {
                        order.setDeclareStatus(declareStatus);
                        update(order);
                    }
                }catch (Exception ignored){

                }
            }
            if (builder.length()>0) {
                try {
                    mailSupport.sendMail("取消单撤单超时","wangmiaowj@qq.com,luobin@fl56.net",builder.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public CrossBorderOrder queryByLogisticsNo(String logisticsNo) {
        return crossBorderOrderRepository.findByLogisticsNo(logisticsNo);
    }

    // 计算理论重量
    @Override
    public BigDecimal countTheoryWeight(CrossBorderOrder order) {
        String materialCode = order.getMaterialCode();
        if (StringUtils.isBlank(materialCode))
            return BigDecimal.ZERO;
        PackageInfo packageInfo = packageInfoService.queryByPackageCode(materialCode);
        if (packageInfo == null)
            return BigDecimal.ZERO;
        Config switchConfig = configService.queryByK("ADD_VALUE_JUMP_SWITCH");
        Config config = configService.queryByK("ADD_VALUE_JUMP_WHITE_LIST");
        String adjwl = config.getV();
        String[] split = adjwl.split(",");
        List<String> shopIds = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            shopIds.add(split[i]);
        }
        if (StringUtils.equals("1", switchConfig.getV())
                &&!CollectionUtils.contains(shopIds, order.getShopId().toString())
                &&!StringUtils.equals("1", order.getVasPack())
                && StringUtils.equals("1", packageInfo.getAddValue())) {
            throw new BadRequestException("商家未订购增值服务，但使用增值包材，请联系商务："+packageInfo.getPackageName());
        }
        BigDecimal materialHcWeight = BigDecimal.ZERO;
        // 批量单，查询使用的耗材
        List<OrderMaterial> orderMaterialList = orderMaterialService.queryByOrderId(order.getId());
        if (CollectionUtils.isNotEmpty(orderMaterialList)) {
            for (OrderMaterial orderMaterial : orderMaterialList) {
                PackageInfo packageInfoHc = packageInfoService.queryByPackageCode(orderMaterial.getMaterialCode());
                if (StringUtils.equals("1", switchConfig.getV())
                        &&!CollectionUtils.contains(shopIds, order.getShopId().toString())
                        &&!StringUtils.equals("1", order.getVasPack())
                        && StringUtils.equals("1", packageInfoHc.getAddValue())) {
                    throw new BadRequestException("商家未订购增值服务，但使用增值耗材，请联系商务："+packageInfoHc.getPackageName());
                }
                materialHcWeight = materialHcWeight.add(packageInfoHc.getWeight());
            }
        }
        BigDecimal skuWeight = BigDecimal.ZERO;
        List<CrossBorderOrderDetails> details = crossBorderOrderDetailsService.queryByOrderId(order.getId());
        for (CrossBorderOrderDetails crossBorderOrderDetails : details) {
            BaseSku baseSku = baseSkuService.queryByGoodsNo(crossBorderOrderDetails.getGoodsNo());
            BigDecimal saleWeight = baseSku.getSaleWeight();
            if (saleWeight == null || saleWeight.compareTo(BigDecimal.ZERO) == 0) {
                baseSkuService.syncSize(baseSku.getId());
                BaseSku baseSku1 = baseSkuService.queryByGoodsNo(crossBorderOrderDetails.getGoodsNo());
                saleWeight = baseSku1.getSaleWeight();
            }
            if (saleWeight == null || saleWeight.compareTo(BigDecimal.ZERO) == 0) {
               if (!StringUtils.equals(PlatformConstant.DY, order.getPlatformCode())) {
                   saleWeight = BigDecimal.ONE;
               }
            }
            if (saleWeight == null || saleWeight.compareTo(BigDecimal.ZERO) == 0) {
                throw new BadRequestException("此商品未维护重量，请维护：" + baseSku.getBarCode());
            }
            skuWeight = skuWeight.add(saleWeight.multiply(new BigDecimal(crossBorderOrderDetails.getQty())));
        }

        BigDecimal totalTheoryWeight = BigDecimal.ZERO;
        totalTheoryWeight = totalTheoryWeight.add(materialHcWeight);// 耗材重量
        totalTheoryWeight = totalTheoryWeight.add(packageInfo.getWeight());// 纸箱重量
        totalTheoryWeight = totalTheoryWeight.add(skuWeight);// 商品重量

        return totalTheoryWeight;
    }


    @Override
    public Integer getPlatformStatus(CrossBorderOrder order) throws Exception {
        Integer statusCode = -1;
        switch (order.getPlatformCode()) {
            case "DY":
                statusCode = douyinService.getStatus(order);
                break;
            case "PDD": {
                String pddStatusCode = pddOrderService.getOrderStatus(order.getOrderNo(), order.getPlatformShopId());
                statusCode = PDDSupport.translationToDYStatus(pddStatusCode);
                break;
            }
            case "YZ": {
                String youZanStatusCode = youZanOrderService.getOrderStatus(order.getOrderNo(), order.getPlatformShopId());
                statusCode = YouZanSupport.translationToDYStatus(youZanStatusCode);
                break;
            }
            case "QS": {
                String qsStatusCode = qsService.getOrderStatus(order.getOrderNo(), order.getPlatformShopId());
                statusCode = QSSupport.translationToDYStatus(qsStatusCode);
                break;
            }
            case "Ymatou": {
                int ymatouStatus = ymatouService.getOrderStatus(order.getOrderNo(), order.getPlatformShopId());
                statusCode = YmatouSupport.translationToDYStatus(ymatouStatus);
                break;
            }
            case "MGJ": {
                String mgjStatusCode = moGuJieService.getOrderStatus(order.getOrderNo(), order.getShopId());
                statusCode = MoGuJieSupport.translationToDYStatus(mgjStatusCode);
                break;
            }
            case "MeiTuan": {
                int meiTuanStatusCode = meituanService.getOrderStatus(order.getOrderNo(), order.getShopId());
                statusCode = MeiTuanSupport.translationToDYStatus(meiTuanStatusCode);
                break;
            }case "GM":{
                int guomeiStatus=guoMeiService.guomeiOrderCancel(order);
                if (guomeiStatus==0)
                    statusCode=2;//可发货
                else if (guomeiStatus==10)
                    statusCode=16;//冻结
                else if (guomeiStatus==30)
                    statusCode=25;//取消退款
                else
                    statusCode=21;//取消
                break;
            }
            default:
                statusCode = 2;// 当不是以上这些平台的订单时，状态统一为待发货
                break;
        }
        return statusCode;
    }

    @Override
    public CrossBorderOrder queryShopId(Long shopId) {
        if (shopId == null)
            return null;
        CrossBorderOrder order = new CrossBorderOrder();
        order.setShopId(shopId);
        Example<CrossBorderOrder> example = Example.of(order);
        Optional<CrossBorderOrder> one = crossBorderOrderRepository.findOne(example);
        if (one.isPresent())
            return one.get();
        else
            return null;
    }

    /**
     * 只取消erp和WMS
     * @param id
     */
    @Override
    public void cancelWmsOrder(Long id) {
        CrossBorderOrder order = queryById(id);
        if (order == null)
            throw new BadRequestException("订单不存在");
        if (order.getStatus() >= CBOrderStatusEnum.STATUS_240.getCode()
                && order.getStatus() <= CBOrderStatusEnum.STATUS_245.getCode()) {
            // 称重完成到出库不允许取消
            throw new BadRequestException("称重/出库状态不允许取消发货");
        }
        String wmsResult = "";
        if (order.getStatus() >= CBOrderStatusEnum.STATUS_220.getCode()
                && order.getStatus() < CBOrderStatusEnum.STATUS_240.getCode()) {
            ShopInfoDto shopInfoDto = shopInfoService.queryById(order.getShopId());
            ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.findById(shopInfoDto.getServiceId());
            wmsResult = wmsSupport.cancelOrder(order.getDeclareNo(), clearCompanyInfo.getCustomsCode());
        }
        if (StringUtils.equals("2", order.getDefault05())) {
            // 跨境购直接下发的单子还要改erp订单状态
            order.setOrderNo(order.getOrderNo()+"QX");
            order.setCrossBorderNo(order.getCrossBorderNo()+"QX");
            order.setLogisticsNo(order.getLogisticsNo()+"QX");
            order.setStatus(CBOrderStatusEnum.STATUS_888.getCode());
            order.setCancelTime(new Timestamp(System.currentTimeMillis()));
            update(order);
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_888.getCode()),
                    BooleanEnum.SUCCESS.getCode(),
                    wmsResult
            );
            orderLogService.create(orderLog);
        }

    }

    // 富勒出库
    @Override
    public void deliverWms(Long id) {
        CrossBorderOrder order = queryById(id);
        if (StringUtils.isBlank(order.getSoNo())) {
            refreshWmsStatus(String.valueOf(id));
        }
        try {
            wmsSupport.deliver(order.getSoNo());
            if (StringUtils.equals(order.getDefault05(), "2")) {
                order.setStatus(CBOrderStatusEnum.STATUS_245.getCode());
                order.setDeliverTime(new Timestamp(System.currentTimeMillis()));
                update(order);
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(order.getStatus()),
                        BooleanEnum.SUCCESS.getCode(),
                        ""
                );
                orderLogService.create(orderLog);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void cancel(Long id) throws Exception {
        CrossBorderOrder order = queryById(id);
        if (order == null)
            throw new BadRequestException("订单不存在");
        if (StringUtil.isNotBlank(order.getLpCode())){
            PackageStatus status = caiNiaoService.queryWmsStatus(order.getLpCode(),order.getShopId());
            if (StringUtil.equals(status.getLogisticStatus(),"CONSIGN")||StringUtil.equals(status.getLogisticStatus(),"ACCEPT"))
                throw new BadRequestException("菜鸟已发货，不允许取消");
        }
        if (order.getStatus() >= CBOrderStatusEnum.STATUS_240.getCode()
                && order.getStatus() <= CBOrderStatusEnum.STATUS_245.getCode()) {
            // 称重完成到出库不允许取消
            throw new BadRequestException("称重/出库状态不允许取消发货");
        }
        String wmsResult = "";
        if (order.getStatus() >= CBOrderStatusEnum.STATUS_220.getCode()
                && order.getStatus() < CBOrderStatusEnum.STATUS_240.getCode()) {
            // 发起取消申报
            try {
                decrypt(order);
            } catch (Exception e) {
                throw new BadRequestException("订单解密失败：" + e.getMessage());
            }
            if (!StringUtils.equals("2", order.getDefault02())) {
                // 非抖音申报的单子才取消申报
                kjgSupport.cancelOrder(order);
                order.setDeclareStatus("29");//清单撤销中
                order.setClearDelStartTime(DateUtils.now());
            }
            ShopInfoDto shopInfoDto = shopInfoService.queryById(order.getShopId());
            ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.findById(shopInfoDto.getServiceId());
            if (StringUtil.equals(shopInfoDto.getPushTo(),"1")){
                //菜鸟取消订单
                if (StringUtil.isNotBlank(order.getLpCode()))
                    caiNiaoService.cancelDeclare(order);
            }else{
                wmsResult = wmsSupport.cancelOrder(order.getDeclareNo(), clearCompanyInfo.getCustomsCode());
            }
            try {
                encrypt(order);
            } catch (Exception e) {
                throw new BadRequestException("订单加密失败：" + e.getMessage());
            }
            // 抖音的单子，发起撤单开始请求
            if (StringUtils.equals(PlatformConstant.DY, order.getPlatformCode())) {
                try {
                    if (order.getClearSuccessTime() == null) {
                        douyinService.confirmCloseClearSuccess(order);
                    }else {
                        douyinService.confirmDelClearStart(order);
                    }
                }catch (Exception e) {
                    // 不做处理
                    e.printStackTrace();
                }
            }

        }
        order.setStatus(CBOrderStatusEnum.STATUS_888.getCode());
        order.setCancelTime(new Timestamp(System.currentTimeMillis()));
        update(order);
        OrderLog orderLog = new OrderLog(
                order.getId(),
                order.getOrderNo(),
                String.valueOf(CBOrderStatusEnum.STATUS_888.getCode()),
                BooleanEnum.SUCCESS.getCode(),
                wmsResult
        );
        orderLogService.create(orderLog);
    }

    // 取消申报
    @Override
    public void cancelDec(Long id) throws Exception {
        CrossBorderOrder order = queryById(id);
        if (order == null)
            throw new BadRequestException("订单不存在");
        if (StringUtil.isNotBlank(order.getLpCode())){
            PackageStatus status = caiNiaoService.queryWmsStatus(order.getLpCode(),order.getShopId());
            if (StringUtil.equals(status.getLogisticStatus(),"CONSIGN"))
                throw new BadRequestException("菜鸟已发货，不允许取消");
        }
        if (order.getStatus().intValue() >= CBOrderStatusEnum.STATUS_240.getCode().intValue()
                && order.getStatus().intValue() <= CBOrderStatusEnum.STATUS_245.getCode().intValue()) {
            // 称重完成到出库不允许取消
            throw new BadRequestException("当前状态不允许取消发货");
        }
        if (order.getStatus().intValue() >= CBOrderStatusEnum.STATUS_220.getCode().intValue()
                && order.getStatus().intValue() < CBOrderStatusEnum.STATUS_240.getCode().intValue()) {
            // 发起取消申报
            try {
                decrypt(order);
            } catch (Exception e) {
                throw new BadRequestException("订单解密失败：" + e.getMessage());
            }
            kjgSupport.cancelOrder(order);
            if (StringUtil.isNotBlank(order.getInvtNo())){
                order.setClearDelStartTime(DateUtils.now());
                order.setDeclareStatus("29");//清单撤销中
                update(order);
            }
        }
    }

    // 解冻
    @Override
    public void unFreeze(CrossBorderOrder order) {
        if (order == null) throw new BadRequestException("解冻订单为空");
        if (order.getStatus().intValue() != CBOrderStatusEnum.STATUS_999.getCode().intValue()
                && order.getStatus().intValue() != CBOrderStatusEnum.STATUS_201.getCode().intValue())
            throw new BadRequestException("非冻结状态不能解冻");
        List<CrossBorderOrderDetails> list = crossBorderOrderDetailsService.queryByOrderId(order.getId());
        ShopInfoDto shopInfoDto = shopInfoService.queryById(order.getShopId());
        ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.findById(shopInfoDto.getServiceId());
        for (CrossBorderOrderDetails item : list) {
            String goodsNo = item.getGoodsNo();
            Integer qty = wmsSupport.queryInventoryBySku(goodsNo, clearCompanyInfo.getCustomsCode());
            if (qty.intValue() == 0) {
                // 还有货无库存，
                throw new BadRequestException("无库存不能解冻");
            }
        }
        //解冻
        if (StringUtil.equals(order.getPlatformCode(),"PDD")){
            try {
                PddOrder pddOrder=pddOrderService.getOrderByOrderSn(order.getOrderNo(),order.getPlatformShopId());
                if (pddOrder==null)
                    throw new BadRequestException("获取的拼多多订单为空");
                pddOrder.setReceiverName(order.getConsigneeName());
                pddOrder.setReceiverPhone(order.getConsigneeTel());
                PddCloudPrintData pddCloudPrintData=pddOrderService.getMailNo(pddOrder);
                String mailNo=pddCloudPrintData.getMailNo();
                order.setLogisticsNo(mailNo);
                pddCloudPrintData.setShopCode(order.getPlatformShopId());
                pddCloudPrintData.setSender(shopInfoDto.getName());
                pddCloudPrintData.setSenderPhone(shopInfoDto.getContactPhone());
                cbOrderProducer.send(
                        MsgType.CB_ORDER_PDD_SAVE_PRINTDATA,
                        new JSONObject(pddCloudPrintData).toString(),
                        order.getOrderNo()
                );
            }catch (Exception e){
                e.printStackTrace();
                throw new BadRequestException(e.getMessage());
            }
        }
        order.setStatus(CBOrderStatusEnum.STATUS_200.getCode());
        update(order);
        cbOrderProducer.send(
                MsgType.CB_ORDER_215,
                String.valueOf(order.getId()),
                order.getOrderNo()
        );
    }

    @Override
    public void unFreezeBatch() {
        CrossBorderOrderQueryCriteria criteria = new CrossBorderOrderQueryCriteria();
        List<Integer> status = new ArrayList<>();
        status.add(CBOrderStatusEnum.STATUS_999.getCode());
        criteria.setStatus(status);
        List<CrossBorderOrder> all = crossBorderOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        if (CollectionUtils.isNotEmpty(all)) {
            for (CrossBorderOrder order : all) {
                try {
                    unFreeze(order);
                } catch (Exception e) {
                    log.warn(e.getMessage());
                }
            }
        }
    }

    @Override
    public Map<String, Map<String, Integer>> reportHourOrder() {
        // 查询头天16点到今天16点创建的订单数据
        String end = DateUtils.nowDate() + " 16:00:00";
        String start = DateUtils.formatDate(DateUtils.offsetDay(new Date(), -1)) + " 16:00:00";

        List<Map<String, Object>> inData = crossBorderOrderRepository.queryHourInData(start, end);
        List<Map<String, Object>> outData = crossBorderOrderRepository.queryHourOutData(start, end);
        Integer allOut = crossBorderOrderRepository.queryAllOutData(start, end);
        Integer allIn = crossBorderOrderRepository.queryAllIntData(start, end);
        Integer allWait = crossBorderOrderRepository.queryAllWaiteData(start, end);

        List<Date> dateList = DateUtils.betweenHours(DateUtils.parse(start), DateUtils.parse(end));
        Map<String, Map<String, Integer>> resultItem = new LinkedHashMap<>();
        for (Date date : dateList) {
            String time = DateUtils.format(date, "dd-HH");
            Map<String, Integer> orderNumItem = new HashMap<>();
            String inOrders = "0";
            String outOrders = "0";
            for (Map<String, Object> map : inData) {
                if (StringUtils.equals(time, map.get("hours").toString())) {
                    inOrders = map.get("allOrder").toString();
                    continue;
                }
            }
            for (Map<String, Object> map : outData) {
                if (StringUtils.equals(time, map.get("hours").toString())) {
                    outOrders = map.get("allOrder").toString();
                    continue;
                }
            }
            orderNumItem.put("hourIn", Integer.valueOf(inOrders));
            orderNumItem.put("hourOut", Integer.valueOf(outOrders));
            orderNumItem.put("allOut", allOut);
            orderNumItem.put("allIn", allIn);
            orderNumItem.put("allWait", allWait);
            resultItem.put(time, orderNumItem);
        }
        return resultItem;
    }


    @Override
    public void handelDecErr() {
        CrossBorderOrderQueryCriteria criteria = new CrossBorderOrderQueryCriteria();
        List<Integer> status = new ArrayList<>();
        status.add(CBOrderStatusEnum.STATUS_225.getCode());
        criteria.setStatus(status);
        criteria.setDeclareStatus("23");
        criteria.setDeclareMsg("[Code:1301;Desc:系统异常，请稍后重试]");
        List<CrossBorderOrder> all = crossBorderOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        if (CollectionUtils.isNotEmpty(all)) {
            for (CrossBorderOrder order : all) {
                // 先取消申报单，再重新申报
                List<CrossBorderOrderDetails> details = crossBorderOrderDetailsService.queryByOrderId(order.getId());
                order.setItemList(details);
                try {
                    String refresh = kjgSupport.refresh(order);
                    JSONObject refreshJSON = JSONUtil.xmlToJson(refresh);
                    JSONObject refreshHeader = refreshJSON.getJSONObject("Message").getJSONObject("Header");
                    if (StringUtil.equals("T", refreshHeader.getStr("Result"))) {
                        JSONObject mft = refreshJSON.getJSONObject("Message").getJSONObject("Body").getJSONObject("Mft");
                        String kjgStatus = mft.getStr("Status");
                        String kjgResult = mft.getStr("Result");
                        if ("23".equals(kjgStatus) && StringUtils.equals("[Code:1301;Desc:系统异常，请稍后重试]", kjgResult)) {
                            // 只有查询申报单状态肯定为23异常的才取消重推
                            kjgSupport.cancelOrder(order);
                            if (StringUtil.equals(order.getPlatformCode(), "PDD"))
                                order.setDeclareNo(pddOrderService.declare(order));
                            else {
                                String declareNo = kjgSupport.declare(order);
                                order.setDeclareNo(declareNo);
                            }
                            update(order);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public CrossBorderOrder queryByMailNo(String mailNo) {
        return crossBorderOrderRepository.findByLogisticsNo(mailNo);
    }

    @Override
    public CrossBorderOrder queryByOrderNo(String orderNo) {
        if (StringUtil.isBlank(orderNo))
            throw new BadRequestException("订单号为空");
        CrossBorderOrder order = new CrossBorderOrder();
        order.setOrderNo(orderNo);
        Example<CrossBorderOrder> example = Example.of(order);
        Optional<CrossBorderOrder> one = crossBorderOrderRepository.findOne(example);
        if (one.isPresent())
            return one.get();
        else
            return null;
    }

    @Override
    public CrossBorderOrder queryByMftNo(String mftNo) {
        if (StringUtil.isBlank(mftNo))
            return null;
        CrossBorderOrder order = new CrossBorderOrder();
        order.setDeclareNo(mftNo);
        Example<CrossBorderOrder> example = Example.of(order);
        Optional<CrossBorderOrder> one = crossBorderOrderRepository.findOne(example);
        if (one.isPresent())
            return one.get();
        else
            return null;
    }

    @Override
    public List<CrossBorderOrder> findByIds(String ids) {
        String[] split = ids.split(",");
        List<Long> list = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            list.add(Long.valueOf(split[i]));
        }
        List<CrossBorderOrder> allById = crossBorderOrderRepository.findAllById(list);
        return allById;
    }


    @Override
    public CrossBorderOrder queryById(Long id) {
        CrossBorderOrder crossBorderOrder = crossBorderOrderRepository.findById(id).orElseGet(CrossBorderOrder::new);
        return crossBorderOrder;
    }

    @Override
    public CrossBorderOrder queryByOrderNoWithDetails(String orderNo) {
        CrossBorderOrder order = crossBorderOrderRepository.findByOrderNo(orderNo);
        if (order == null)
            return null;
        List<CrossBorderOrderDetails> list = crossBorderOrderDetailsService.queryByOrderId(order.getId());
        order.setItemList(list);
        return order;
    }

    @Override
    public List<CrossBorderOrder> queryByWaveNo(String waveNo) {
        return crossBorderOrderRepository.findByWaveNo(waveNo);
    }


    @Override
    public CrossBorderOrder queryByCrossBorderNoWithDetails(String crossBorderNo) {
        CrossBorderOrder order = crossBorderOrderRepository.findByCrossBorderNo(crossBorderNo);
        if (order == null)
            return null;
        List<CrossBorderOrderDetails> list = crossBorderOrderDetailsService.queryByOrderId(order.getId());
        order.setItemList(list);
        return order;
    }


    @Override
    public CrossBorderOrder queryByIdWithDetails(Long id) {
        CrossBorderOrder crossBorderOrder = crossBorderOrderRepository.findById(id).orElseGet(CrossBorderOrder::new);
        List<CrossBorderOrderDetails> list = crossBorderOrderDetailsService.queryByOrderId(id);
        crossBorderOrder.setItemList(list);
        return crossBorderOrder;
    }

    @Override
    public Map<String, Object> queryAll(CrossBorderOrderQueryCriteria criteria, Pageable pageable) {
        if (criteria.getIsCollected()!=null){
            if (criteria.getIsCollected())
                criteria.setCollected(true);
            else
                criteria.setUnCollected(true);
        }
        Page<CrossBorderOrder> page = crossBorderOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(crossBorderOrderMapper::toDto));
    }

    @Override
    public Map<String, Object> queryOrders(CrossBorderOrderQueryCriteria criteria, Pageable pageable) {
        Page<CrossBorderOrder> page = crossBorderOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        List<CrossBorderOrder> content = page.getContent();
        List<CBOrderOutDTO> results = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(content)) {
            for (CrossBorderOrder order : content) {
                try {
                    youZanOrderService.decryptMask(order);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                CBOrderOutDTO cbOrderOutDTO = new CBOrderOutDTO();
                BeanUtil.copyProperties(order, cbOrderOutDTO, CopyOptions.create().setIgnoreNullValue(true));

                List<CrossBorderOrderDetails> list = crossBorderOrderDetailsService.queryByOrderId(order.getId());
                List<CBOrderOutDetailsDTO> detailsDTOS = new ArrayList<>();
                for (CrossBorderOrderDetails details : list) {
                    CBOrderOutDetailsDTO detailsDTO = new CBOrderOutDetailsDTO();
                    BeanUtil.copyProperties(details, detailsDTO, CopyOptions.create().setIgnoreNullValue(true));
                    detailsDTOS.add(detailsDTO);
                }
                ShopInfoDto shopInfoDto = shopInfoService.queryById(order.getShopId());
                cbOrderOutDTO.setShopCode(shopInfoDto.getCode());
                cbOrderOutDTO.setShopName(shopInfoDto.getName());
                cbOrderOutDTO.setItemList(detailsDTOS);
                results.add(cbOrderOutDTO);
            }
        }
        Page<CBOrderOutDTO> pageDTO = new PageImpl<>(results, pageable, page.getTotalElements());
        return PageUtil.toPage(pageDTO);
    }

    @Override
    public List<CrossBorderOrderDto> queryAll(CrossBorderOrderQueryCriteria criteria) {
        return crossBorderOrderMapper.toDto(crossBorderOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
    }

    @Override
    @Transactional
    public CrossBorderOrderDto findById(Long id) {
        CrossBorderOrder crossBorderOrder = crossBorderOrderRepository.findById(id).orElseGet(CrossBorderOrder::new);
        ValidationUtil.isNull(crossBorderOrder.getId(), "CrossBorderOrder", "id", id);
        return crossBorderOrderMapper.toDto(crossBorderOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CrossBorderOrderDto create(CrossBorderOrder resources) {
        if (crossBorderOrderRepository.findByOrderNo(resources.getOrderNo()) != null) {
            throw new EntityExistException(CrossBorderOrder.class, "order_no", resources.getOrderNo());
        }
        return crossBorderOrderMapper.toDto(crossBorderOrderRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CrossBorderOrder createWithDetail(CrossBorderOrder resources) {
        List<CrossBorderOrderDetails> itemList = resources.getItemList();
        CrossBorderOrder save = crossBorderOrderRepository.save(resources);
        List<OrderMaterial> orderMaterialList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(itemList)) {
            for (CrossBorderOrderDetails item : itemList) {
                item.setOrderId(save.getId());

                List<SkuMaterial> skuMaterials = skuMaterialService.queryByGoodsNo(item.getGoodsNo());
                if (CollectionUtils.isNotEmpty(skuMaterials)) {
                    for (SkuMaterial skuMaterial : skuMaterials) {
                        OrderMaterial orderMaterial = new OrderMaterial();
                        orderMaterial.setMaterialCode(skuMaterial.getMaterialCode());
                        if (StringUtils.equals("DYHC6002", orderMaterial.getMaterialCode())) {
                            orderMaterial.setQty(skuMaterial.getQty());
                        }else {
                            orderMaterial.setQty(new BigDecimal(skuMaterial.getQty()).multiply(new BigDecimal(item.getQty())).intValue());
                        }

                        orderMaterialList.add(orderMaterial);
                    }
                }
            }
            crossBorderOrderDetailsService.createAll(itemList);
        }
        handleOrderMaterial(orderMaterialList, save);
        return save;
    }

    //处理订单耗材
    private void handleOrderMaterial(List<OrderMaterial> orderMaterialList, CrossBorderOrder order) {
        try {
            if (CollectionUtils.isEmpty(orderMaterialList))
                return;
            Map<String, Integer> orderMaterialMap = new HashMap();
            List<OrderMaterial> saveOrderMaterialList = new ArrayList<>();
            for (OrderMaterial orderMaterial : orderMaterialList) {
                Integer qty = orderMaterialMap.get(orderMaterial.getMaterialCode());
                if (qty == null) {
                    orderMaterialMap.put(orderMaterial.getMaterialCode(), orderMaterial.getQty());
                    if (StringUtils.equals("DYHC6002", orderMaterial.getMaterialCode())) {
                        int totalNum = Integer.valueOf(order.getTotalNum()).intValue();
                        String code;
                        if (totalNum <= 2) {
                            code = "DYHC2009";
                        }else if (totalNum > 2 && totalNum <= 4) {
                            code = "DYHC2010";
                        }else {
                            code = "DYHC2008";
                        }
                        // 遇到冰袋，则系统只取其中一条新增,并新增一条泡沫箱信息
                        orderMaterialMap.put(code, 1);
                    }
                }else {
                    if (!StringUtils.equals("DYHC6002", orderMaterial.getMaterialCode())) {
                        orderMaterialMap.put(orderMaterial.getMaterialCode(), orderMaterial.getQty() + qty);
                    }
                }
            }
            for (String key : orderMaterialMap.keySet()) {
                Integer value = orderMaterialMap.get(key);
                PackageInfo packageInfo = packageInfoService.queryByPackageCode(key);
                OrderMaterial orderMaterial = new OrderMaterial();
                orderMaterial.setOrderId(order.getId());
                orderMaterial.setOrderNo(order.getOrderNo());
                orderMaterial.setCustomersId(order.getCustomersId());
                orderMaterial.setShopId(order.getShopId());

                CustomerInfoDto customerInfoDto = customerInfoService.queryById(order.getCustomersId());
                ShopInfoDto shopInfoDto = shopInfoService.queryById(order.getShopId());
                if (customerInfoDto != null)
                    orderMaterial.setCustomersName(customerInfoDto.getCustNickName());
                if (shopInfoDto != null)
                    orderMaterial.setShopName(shopInfoDto.getName());

                orderMaterial.setMaterialCode(key);
                orderMaterial.setMaterialName(packageInfo.getPackageName());
                orderMaterial.setQty(value);

                orderMaterial.setCreateBy("SYSTEM");
                orderMaterial.setCreateTime(new Timestamp(System.currentTimeMillis()));
                saveOrderMaterialList.add(orderMaterial);
            }
            // 保存使用耗材明细
            orderMaterialService.createAll(saveOrderMaterialList);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CrossBorderOrder resources) {
        crossBorderOrderRepository.save(resources);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            crossBorderOrderRepository.deleteById(id);
        }
    }

    @Override
    public void download(CrossBorderOrderQueryCriteria criteria, HttpServletResponse response) throws IOException {
        // 控制查询时间段
        if (CollectionUtils.isNotEmpty(criteria.getStatus())&&criteria.getStatus().size()==1&&criteria.getStatus().get(0)==245){
            List<DailyCrossBorderOrderDto>all = dailyCrossBorderOrderService.queryAll(new DailyCrossBorderOrderQueryCriteria(criteria));
            List<Map<String, Object>> list = new ArrayList<>();
            for (DailyCrossBorderOrderDto crossBorderOrder : all) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("订单号", crossBorderOrder.getOrderNo());
                map.put("交易单号",crossBorderOrder.getCrossBorderNo());
                map.put("状态", crossBorderOrder.getStatus());
                map.put("客户", crossBorderOrder.getCustomersName());
                map.put("店铺", crossBorderOrder.getShopName());
                map.put("是否预售", crossBorderOrder.getPreSell());
                map.put("申报单号", crossBorderOrder.getDeclareNo());
                map.put("总署清单编号", crossBorderOrder.getInvtNo());
                map.put("波次号", crossBorderOrder.getWaveNo());
                map.put("SO单号", crossBorderOrder.getSoNo());
                map.put("申报状态", crossBorderOrder.getDeclareStatus());
                map.put("申报信息", crossBorderOrder.getDeclareMsg());
                map.put("冻结原因", crossBorderOrder.getFreezeReason());
                map.put("运费", crossBorderOrder.getPostFee());
                map.put("实付金额", crossBorderOrder.getPayment());
                map.put("预估税费", crossBorderOrder.getTaxAmount());
                map.put("运单号", crossBorderOrder.getLogisticsNo());
                map.put("省", crossBorderOrder.getProvince());
                map.put("市", crossBorderOrder.getCity());
                map.put("区", crossBorderOrder.getDistrict());
                map.put("收货地址", crossBorderOrder.getConsigneeAddr());
                map.put("收货电话", crossBorderOrder.getConsigneeTel());
                map.put("支付时间", crossBorderOrder.getPayTime());
                map.put("订单创建时间", crossBorderOrder.getOrderCreateTime());
                map.put("接单时间", crossBorderOrder.getCreateTime());
                map.put("接单回传时间", crossBorderOrder.getReceivedBackTime());
                map.put("清关开始时间", crossBorderOrder.getClearStartTime());
                map.put("清关开始回传时间", crossBorderOrder.getClearStartBackTime());
                map.put("清关完成时间", crossBorderOrder.getClearSuccessTime());
                map.put("清关完成回传时间", crossBorderOrder.getClearSuccessBackTime());
                map.put("出库时间", crossBorderOrder.getDeliverTime());
                map.put("是否揽收", crossBorderOrder.getIsCollect());
                map.put("揽收时间", crossBorderOrder.getLogisticsCollectTime());
                list.add(map);
            }
            FileUtil.downloadExcel(list, response);
        }else
            download2(criteria,response);
    }

    private void download2(CrossBorderOrderQueryCriteria criteria, HttpServletResponse response) throws IOException {
        // 控制查询时间段
        List<CrossBorderOrderDto> all = queryAll(criteria);
        List<Map<String, Object>> list = new ArrayList<>();
        CustomerInfoDto customerInfoDto = null;
        ShopInfoDto shopInfoDto = null;
        for (CrossBorderOrderDto crossBorderOrder : all) {
            try {
                CrossBorderOrder cbOrder = crossBorderOrderMapper.toEntity(crossBorderOrder);
                decrypt(cbOrder);
                crossBorderOrder = crossBorderOrderMapper.toDto(cbOrder);
                crossBorderOrder = decryptMask(crossBorderOrder);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (customerInfoDto==null|| !customerInfoDto.getId().equals(crossBorderOrder.getCustomersId()))
                customerInfoDto = customerInfoService.queryById(crossBorderOrder.getCustomersId());
            if (shopInfoDto == null || !shopInfoDto.getId().equals(crossBorderOrder.getShopId()))
                shopInfoDto = shopInfoService.queryById(crossBorderOrder.getShopId());
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("订单号", crossBorderOrder.getOrderNo());
            map.put("交易单号",crossBorderOrder.getCrossBorderNo());
            map.put("状态", CBOrderStatusEnum.getDesc(crossBorderOrder.getStatus()));
            map.put("客户", customerInfoDto.getCustNickName());
            map.put("店铺", shopInfoDto.getName());
            map.put("是否预售", StringUtils.equals("1", crossBorderOrder.getPreSell()) ? "是" : "否");
            map.put("申报单号", crossBorderOrder.getDeclareNo());
            map.put("总署清单编号", crossBorderOrder.getInvtNo());
            map.put("波次号", crossBorderOrder.getWaveNo());
            map.put("SO单号", crossBorderOrder.getSoNo());
            map.put("申报状态", crossBorderOrder.getDeclareStatus());
            map.put("申报信息", crossBorderOrder.getDeclareMsg());
            map.put("冻结原因", crossBorderOrder.getFreezeReason());
            map.put("运费", crossBorderOrder.getPostFee());
            map.put("实付金额", crossBorderOrder.getPayment());
            map.put("预估税费", crossBorderOrder.getTaxAmount());
            map.put("运单号", crossBorderOrder.getLogisticsNo());
            map.put("省", crossBorderOrder.getProvince());
            map.put("市", crossBorderOrder.getCity());
            map.put("区", crossBorderOrder.getDistrict());
            map.put("收货地址", PrivacyUtil.maskAddressData(crossBorderOrder.getConsigneeAddr()));
            map.put("收货电话", PrivacyUtil.maskPhoneData(crossBorderOrder.getConsigneeTel()));
            map.put("支付时间", crossBorderOrder.getPayTime());
            map.put("订单创建时间", crossBorderOrder.getOrderCreateTime());
            map.put("接单时间", crossBorderOrder.getCreateTime());
            map.put("接单回传时间", crossBorderOrder.getReceivedBackTime());
            map.put("清关开始时间", crossBorderOrder.getClearStartTime());
            map.put("清关开始回传时间", crossBorderOrder.getClearStartBackTime());
            map.put("清关完成时间", crossBorderOrder.getClearSuccessTime());
            map.put("清关完成回传时间", crossBorderOrder.getClearSuccessBackTime());
            map.put("出库时间", crossBorderOrder.getDeliverTime());
            map.put("是否揽收", crossBorderOrder.getLogisticsCollectTime()==null?"否":"是");
            map.put("揽收时间", crossBorderOrder.getLogisticsCollectTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    private CrossBorderOrderDto decryptMask(CrossBorderOrderDto crossBorderOrder) throws Exception {
        CrossBorderOrder order = crossBorderOrderMapper.toEntity(crossBorderOrder);
        decryptMask(order);
        return crossBorderOrderMapper.toDto(order);
    }

    @Override
    public void downloadIn(CrossBorderOrderQueryCriteria criteria, HttpServletResponse response) throws IOException {

        List<CrossBorderOrderDto> all = queryAll(criteria);
        List<Map<String, Object>> list = new ArrayList<>();
        CustomerInfoDto customerInfoDto = null;
        ShopInfoDto shopInfoDto = null;
        for (CrossBorderOrderDto crossBorderOrder : all) {
            try {
                CrossBorderOrder cbOrder = crossBorderOrderMapper.toEntity(crossBorderOrder);
                decrypt(cbOrder);
                crossBorderOrder = crossBorderOrderMapper.toDto(cbOrder);
                crossBorderOrder = decryptMask(crossBorderOrder);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (customerInfoDto==null|| !customerInfoDto.getId().equals(crossBorderOrder.getCustomersId()))
                customerInfoDto = customerInfoService.queryById(crossBorderOrder.getCustomersId());
            if (shopInfoDto == null || !shopInfoDto.getId().equals(crossBorderOrder.getShopId()))
                shopInfoDto = shopInfoService.queryById(crossBorderOrder.getShopId());
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("订单号", crossBorderOrder.getOrderNo());
            map.put("交易单号",crossBorderOrder.getCrossBorderNo());
            map.put("运单号",crossBorderOrder.getLogisticsNo());
            map.put("快递",crossBorderOrder.getSupplierId());
            map.put("状态", CBOrderStatusEnum.getDesc(crossBorderOrder.getStatus()));
            map.put("客户", customerInfoDto.getCustNickName());
            map.put("店铺", shopInfoDto.getName());
            map.put("是否预售", StringUtils.equals("1", crossBorderOrder.getPreSell()) ? "是" : "否");
            map.put("波次号", crossBorderOrder.getWaveNo());
            map.put("SO单号", crossBorderOrder.getSoNo());
            map.put("SKU数量", crossBorderOrder.getSkuNum());
            map.put("总件数", crossBorderOrder.getTotalNum());
            map.put("订单结构", crossBorderOrder.getPickType());
            map.put("纸箱", crossBorderOrder.getMaterialCode());
            map.put("理论重量", crossBorderOrder.getTheoryWeight());
            map.put("实际重量", crossBorderOrder.getPackWeight());
            map.put("最晚出库时间", crossBorderOrder.getExpDeliverTime());
            map.put("支付时间", crossBorderOrder.getPayTime());
            map.put("订单创建时间", crossBorderOrder.getOrderCreateTime());
            map.put("接单时间", crossBorderOrder.getCreateTime());
            map.put("接单回传时间", crossBorderOrder.getReceivedBackTime());
            map.put("清关开始时间", crossBorderOrder.getClearStartTime());
            map.put("清关开始回传时间", crossBorderOrder.getClearStartBackTime());
            map.put("清关完成时间", crossBorderOrder.getClearSuccessTime());
            map.put("清关完成回传时间", crossBorderOrder.getClearSuccessBackTime());
            map.put("出库时间", crossBorderOrder.getDeliverTime());
            map.put("是否揽收", crossBorderOrder.getLogisticsCollectTime()==null?"否":"是");
            map.put("揽收时间", crossBorderOrder.getLogisticsCollectTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public CrossBorderOrder queryByLpCode(String cnOrderNo) {
        return null;
    }


    @Override
    public void downloadDetails(CrossBorderOrderQueryCriteria criteria, HttpServletResponse response) throws IOException {
        List<Timestamp> orderCreateTime = criteria.getOrderCreateTime();
        if (CollectionUtils.isEmpty(orderCreateTime)) {
            throw new BadRequestException("导出时订单创建时间必选");
        }
        Timestamp start = orderCreateTime.get(0);
        Timestamp end = orderCreateTime.get(1);
        int days = DateUtils.differentDays(new Date(end.getTime()), new Date(start.getTime()));
        if (days > 30) {
            if (CollectionUtils.isEmpty(orderCreateTime)) {
                throw new BadRequestException("导出时订单创建时间不能大于30天");
            }
        }

        List<Object[]> all = crossBorderOrderRepository.queryAllWithDetails(start, end, criteria.getShopId());

        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] objects : all) {
            /*try {
                CrossBorderOrder cbOrder=new CrossBorderOrder();
                cbOrder.setConsigneeAddr(objects[10]+"");
                decrypt(cbOrder);
                objects[10]=cbOrder.getConsigneeAddr();
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("订单号", objects[0]);
            map.put("交易单号",objects[21]);
            map.put("状态", CBOrderStatusEnum.getDesc((Integer) objects[1]));
            map.put("客户", objects[2]);
            map.put("店铺", objects[3]);
//            map.put("实付金额", objects[4]);
//            map.put("总税额", objects[5]);
            map.put("运单号", objects[6]);
            map.put("省", objects[7]);
            map.put("市", objects[8]);
            map.put("区", objects[9]);
            map.put("收货地址", PrivacyUtil.maskAddressData(objects[10] + ""));
            map.put("支付时间", objects[11]);
            map.put("订单创建时间", objects[12]);
            map.put("出库时间", objects[13]);
            map.put("是否揽收", objects[14]==null?"否":"是");
            map.put("揽收时间", objects[14]);
            map.put("商品编码", objects[15]);
            map.put("商品名称", objects[16]);
            map.put("商品海关备案名", objects[17]);
            map.put("海关货号", objects[18]);
            map.put("条码", objects[19]);
            map.put("数量", objects[20]);
            map.put("实付金额", objects[4]);
            map.put("总税额", objects[5]);
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


    @Override
    public Map<String, Object> orderTotalCount(String startTime, String endTime) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<Long> shopIds = userCustomerService.queryShops(currentUserId);
        if (!CollectionUtils.isNotEmpty(shopIds)) {
            return crossBorderOrderRepository.orderTotalCount(startTime, endTime);
        } else {
            return crossBorderOrderRepository.orderTotalCountWithCusId(startTime, endTime, shopIds);
        }

    }

    @Override
    public Map<String, Object> orderTotalCountAll(Map<String, Object> map) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Object startTime = map.get("startTime");
        Object endTime = map.get("endTime");
        if (startTime == null || endTime == null)
            throw new BadRequestException("订单创建时间不能为空");
        Object platform = map.get("platform");
        Object shopId = map.get("shopId");
        List<Long> shopIds;
        if (ObjectUtil.isEmpty(shopId)) {
            shopIds = userCustomerService.queryShops(currentUserId);
            if (CollectionUtil.isNotEmpty(shopIds)) {
                if (ObjectUtil.isNotEmpty(platform)) {
                    //查询条件：用户所能看到的所有店铺、要查的平台、订单创建起止时间
                    return crossBorderOrderRepository.orderTotalCountWithCusIdPlatform(startTime.toString(), endTime.toString(), shopIds, platform.toString());
                }
                //查询条件：用户所能看到的所有店铺、订单创建起止时间
                return crossBorderOrderRepository.orderTotalCountWithCusIdAll(startTime.toString(), endTime.toString(), shopIds);
            }
            if (ObjectUtil.isNotEmpty(platform)) {
                //查询条件：所有店铺、要查的平台、订单创建起止时间
                return crossBorderOrderRepository.orderTotalCountWithPlatform(startTime.toString(), endTime.toString(), platform.toString());
            }
            //查询条件：所有店铺、订单创建起止时间
            return crossBorderOrderRepository.orderTotalCountAll(startTime.toString(), endTime.toString());
        } else {
            shopIds = new ArrayList<>();
            shopIds.add(Long.parseLong(shopId.toString()));
            if (ObjectUtil.isNotEmpty(platform)) {
                //查询条件：用户要查的店铺、要查的平台、订单创建起止时间
                return crossBorderOrderRepository.orderTotalCountWithCusIdPlatform(startTime.toString(), endTime.toString(), shopIds, platform.toString());
            }
            //查询条件：用户要查的店铺、订单创建起止时间
            return crossBorderOrderRepository.orderTotalCountWithCusIdAll(startTime.toString(), endTime.toString(), shopIds);
        }
    }

    @Override
    public List<Map<String, Object>> shopOrderCount(String startTime, String endTime) {
        List<Map<String, Object>> result;
        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<Long> shopIds = userCustomerService.queryShops(currentUserId);
        if (!CollectionUtils.isNotEmpty(shopIds)) {
            result = crossBorderOrderRepository.shopOrderCount(startTime, endTime);
        } else {
            result = crossBorderOrderRepository.shopOrderCountWithCusId(startTime, endTime, shopIds);
        }
        List<ShopInfo> shopList = shopInfoService.queryByPlafCodeByWithCusId(PlatformConstant.DY, shopIds);
        List<Map<String, Object>> newList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(shopList)) {
            for (ShopInfo shopInfo : shopList) {
                String totalOrder = "0";
                String tokenFlag;
                try {
//                    totalOrder = String.valueOf(douyinService.queryShopTotalOrder(startTime, endTime, shopInfo.getId()));
                    tokenFlag = "";
                } catch (Exception e) {
                    totalOrder = "0";
                    tokenFlag = "ERR";
                }
                boolean flag = true;
                Map<String, Object> addMapItem = new HashMap<>();
                for (Map<String, Object> resultItem : result) {
                    Map<String, Object> newMapItem = new HashMap<>(resultItem);
                    Long shopId = Long.valueOf(newMapItem.get("shopId") == null ? "0" : newMapItem.get("shopId").toString());
                    if (shopId.intValue() == shopInfo.getId().intValue()) {
                        // 去查询当前店铺的创建单量
                        newMapItem.put("totalOrder", totalOrder);
                        newMapItem.put("tokenFlag", tokenFlag);
                        newList.add(newMapItem);
                        flag = false;
                    }
                }
                if (flag) {
                    addMapItem.put("shopId", shopInfo.getId());
                    addMapItem.put("totalOrder", totalOrder);
                    addMapItem.put("tokenFlag", tokenFlag);
                    addMapItem.put("allOrder", 0);
                    addMapItem.put("orderreceivedErrCount", 0);
                    addMapItem.put("orderFreezeCount", 0);
                    addMapItem.put("received", 0);
                    addMapItem.put("cancel", 0);
                    addMapItem.put("receivedBack", 0);
                    addMapItem.put("clearStart", 0);
                    addMapItem.put("clearStartBack", 0);
                    addMapItem.put("clearSucc", 0);
                    addMapItem.put("clearSuccBack", 0);
                    addMapItem.put("weighing", 0);
                    addMapItem.put("deliver", 0);
                    addMapItem.put("waitDeliver", 0);
                    newList.add(addMapItem);
                }
            }
        }
        Integer totaltotalOrder = 0;
        Integer totalallOrder = 0;
        Integer totalalorderreceivedErrCount = 0;
        Integer totalorderFreezeCount = 0;
        Integer totalreceived = 0;
        Integer totalcancel = 0;
        Integer totalreceivedBack = 0;
        Integer totalclearStart = 0;
        Integer totalclearStartBack = 0;
        Integer totalclearSucc = 0;
        Integer totalclearSuccBack = 0;
        Integer totalweighing = 0;
        Integer totaldeliver = 0;
        Integer totalwaitDeliver = 0;
        for (Map<String, Object> item : newList) {
            totaltotalOrder = (StringUtil.equals("ERR", item.get("totalOrder").toString()) ? 0 : Integer.valueOf(item.get("totalOrder").toString())) + totaltotalOrder;
            totalallOrder = Integer.valueOf(item.get("allOrder").toString()) + totalallOrder;
            totalalorderreceivedErrCount = Integer.valueOf(item.get("orderreceivedErrCount").toString()) + totalalorderreceivedErrCount;
            totalorderFreezeCount = Integer.valueOf(item.get("orderFreezeCount").toString()) + totalorderFreezeCount;
            totalreceived = Integer.valueOf(item.get("received").toString()) + totalreceived;
            totalcancel = Integer.valueOf(item.get("cancel").toString()) + totalcancel;
            totalreceivedBack = Integer.valueOf(item.get("receivedBack").toString()) + totalreceivedBack;
            totalclearStart = Integer.valueOf(item.get("clearStart").toString()) + totalclearStart;
            totalclearStartBack = Integer.valueOf(item.get("clearStartBack").toString()) + totalclearStartBack;
            totalclearSucc = Integer.valueOf(item.get("clearSucc").toString()) + totalclearSucc;
            totalclearSuccBack = Integer.valueOf(item.get("clearSuccBack").toString()) + totalclearSuccBack;
            totalweighing = Integer.valueOf(item.get("weighing").toString()) + totalweighing;
            totaldeliver = Integer.valueOf(item.get("deliver").toString()) + totaldeliver;
            totalwaitDeliver = Integer.valueOf(item.get("waitDeliver").toString()) + totalwaitDeliver;
        }
        Map<String, Object> totalItem = new HashMap<>();
        totalItem.put("shopId", "汇总");
        totalItem.put("totalOrder", totaltotalOrder);
        totalItem.put("allOrder", totalallOrder);
        totalItem.put("orderreceivedErrCount", totalalorderreceivedErrCount);
        totalItem.put("orderFreezeCount", totalorderFreezeCount);
        totalItem.put("received", totalreceived);
        totalItem.put("cancel", totalcancel);
        totalItem.put("receivedBack", totalreceivedBack);
        totalItem.put("clearStart", totalclearStart);
        totalItem.put("clearStartBack", totalclearStartBack);
        totalItem.put("clearSucc", totalclearSucc);
        totalItem.put("clearSuccBack", totalclearSuccBack);
        totalItem.put("weighing", totalweighing);
        totalItem.put("deliver", totaldeliver);
        totalItem.put("waitDeliver", totalwaitDeliver);
        newList.add(totalItem);
        return newList;

    }

    @Override
    public List<Map<String, Object>> shopOrderCountAll(Map<String, Object> map) {
        List<Map<String, Object>> result;
        Object startTime = map.get("startTime");
        Object endTime = map.get("endTime");
        if (startTime == null || endTime == null)
            throw new BadRequestException("订单创建时间不能为空");
        Object platform = map.get("platform");
        Object shopIdObj = map.get("shopId");
        List<Long> shopIds;
        if (ObjectUtil.isEmpty(shopIdObj)) {
            Long currentUserId = SecurityUtils.getCurrentUserId();
            shopIds = userCustomerService.queryShops(currentUserId);
            if (CollectionUtils.isEmpty(shopIds)) {
                if (ObjectUtil.isNotEmpty(platform))
                    result = crossBorderOrderRepository.shopOrderCountByPlatform(startTime.toString(), endTime.toString(), platform.toString());
                else
                    result = crossBorderOrderRepository.shopOrderCount(startTime.toString(), endTime.toString());
            } else {
                if (ObjectUtil.isNotEmpty(platform))
                    result = crossBorderOrderRepository.shopOrderCountWithCusIdPlatform(startTime.toString(), endTime.toString(), shopIds, platform.toString());
                else
                    result = crossBorderOrderRepository.shopOrderCountWithCusId(startTime.toString(), endTime.toString(), shopIds);
            }
        } else {
            shopIds = new ArrayList<>();
            shopIds.add(Long.parseLong(shopIdObj.toString()));
            if (ObjectUtil.isNotEmpty(platform))
                result = crossBorderOrderRepository.shopOrderCountWithCusIdPlatform(startTime.toString(), endTime.toString(), shopIds, platform.toString());
            else
                result = crossBorderOrderRepository.shopOrderCountWithCusId(startTime.toString(), endTime.toString(), shopIds);
        }
        List<ShopInfo> shopList;
        if (ObjectUtil.isNotEmpty(platform)) {
            shopList = shopInfoService.queryByPlafCodeByWithCusIdAll(shopIds, platform.toString());
        } else
            shopList = shopInfoService.queryByCusIdAll(shopIds);
        List<Map<String, Object>> newList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(shopList)) {
            for (ShopInfo shopInfo : shopList) {
                String totalOrder;
                String tokenFlag;
                try {
                    totalOrder = getTotalOrder(startTime.toString(), endTime.toString(), shopInfo);
                    tokenFlag = "";
                } catch (Exception e) {
                    totalOrder = "0";
                    tokenFlag = "ERR";
                    e.printStackTrace();
                }
                boolean flag = true;
                Map<String, Object> addMapItem = new HashMap<>();
                for (Map<String, Object> resultItem : result) {
                    Map<String, Object> newMapItem = new HashMap<>(resultItem);
                    Long shopId = Long.valueOf(newMapItem.get("shopId").toString());
                    if (shopId.intValue() == shopInfo.getId().intValue()) {
                        // 去查询当前店铺的创建单量
                        newMapItem.put("totalOrder", totalOrder);
                        newMapItem.put("tokenFlag", tokenFlag);
                        newList.add(newMapItem);
                        flag = false;
                    }
                }
                if (flag) {
                    addMapItem.put("shopId", shopInfo.getId());
                    addMapItem.put("totalOrder", totalOrder);
                    addMapItem.put("tokenFlag", tokenFlag);
                    addMapItem.put("allOrder", 0);
                    addMapItem.put("orderreceivedErrCount", 0);
                    addMapItem.put("orderFreezeCount", 0);
                    addMapItem.put("received", 0);
                    addMapItem.put("cancel", 0);
                    addMapItem.put("receivedBack", 0);
                    addMapItem.put("clearStart", 0);
                    addMapItem.put("clearStartBack", 0);
                    addMapItem.put("clearSucc", 0);
                    addMapItem.put("clearSuccBack", 0);
                    addMapItem.put("weighing", 0);
                    addMapItem.put("deliver", 0);
                    addMapItem.put("waitDeliver", 0);
                    newList.add(addMapItem);
                }
            }
        }
        Integer totaltotalOrder = 0;
        Integer totalallOrder = 0;
        Integer totalorderreceivedErrCount = 0;
        Integer totalorderFreezeCount = 0;
        Integer totalreceived = 0;
        Integer totalcancel = 0;
        Integer totalreceivedBack = 0;
        Integer totalclearStart = 0;
        Integer totalclearStartBack = 0;
        Integer totalclearSucc = 0;
        Integer totalclearSuccBack = 0;
        Integer totalweighing = 0;
        Integer totaldeliver = 0;
        Integer totalwaitDeliver = 0;
        for (Map<String, Object> item : newList) {
            totaltotalOrder = (StringUtil.equals("ERR", item.get("totalOrder").toString()) ? 0 : Integer.valueOf(item.get("totalOrder").toString())) + totaltotalOrder;
            totalallOrder = Integer.valueOf(item.get("allOrder").toString()) + totalallOrder;
            totalorderreceivedErrCount = Integer.valueOf(item.get("orderreceivedErrCount").toString()) + totalorderreceivedErrCount;
            totalorderFreezeCount = Integer.valueOf(item.get("orderFreezeCount").toString()) + totalorderFreezeCount;
            totalreceived = Integer.valueOf(item.get("received").toString()) + totalreceived;
            totalcancel = Integer.valueOf(item.get("cancel").toString()) + totalcancel;
            totalreceivedBack = Integer.valueOf(item.get("receivedBack").toString()) + totalreceivedBack;
            totalclearStart = Integer.valueOf(item.get("clearStart").toString()) + totalclearStart;
            totalclearStartBack = Integer.valueOf(item.get("clearStartBack").toString()) + totalclearStartBack;
            totalclearSucc = Integer.valueOf(item.get("clearSucc").toString()) + totalclearSucc;
            totalclearSuccBack = Integer.valueOf(item.get("clearSuccBack").toString()) + totalclearSuccBack;
            totalweighing = Integer.valueOf(item.get("weighing").toString()) + totalweighing;
            totaldeliver = Integer.valueOf(item.get("deliver").toString()) + totaldeliver;
            totalwaitDeliver = Integer.valueOf(item.get("waitDeliver").toString()) + totalwaitDeliver;
        }
        Map<String, Object> totalItem = new HashMap<>();
        totalItem.put("shopId", "汇总");
        totalItem.put("totalOrder", totaltotalOrder);
        totalItem.put("allOrder", totalallOrder);
        totalItem.put("orderreceivedErrCount", totalorderreceivedErrCount);
        totalItem.put("orderFreezeCount", totalorderFreezeCount);
        totalItem.put("received", totalreceived);
        totalItem.put("cancel", totalcancel);
        totalItem.put("receivedBack", totalreceivedBack);
        totalItem.put("clearStart", totalclearStart);
        totalItem.put("clearStartBack", totalclearStartBack);
        totalItem.put("clearSucc", totalclearSucc);
        totalItem.put("clearSuccBack", totalclearSuccBack);
        totalItem.put("weighing", totalweighing);
        totalItem.put("deliver", totaldeliver);
        totalItem.put("waitDeliver", totalwaitDeliver);
        newList.add(totalItem);
        return newList;
        //return null;
    }

    private String getTotalOrder(String startTime, String endTime, ShopInfo shopInfo) throws Exception {
        long totalOrder = 0;
        if (StringUtil.equals("DY", shopInfo.getPlatformCode())) {
            totalOrder = douyinService.queryShopTotalOrder(startTime, endTime, shopInfo.getId());
        } else if (StringUtil.equals("PDD", shopInfo.getPlatformCode())) {
            totalOrder = pddOrderService.queryShopTotalOrder(startTime, endTime, shopInfo.getId());
        } else if (StringUtil.equals("YZ", shopInfo.getPlatformCode())) {
            totalOrder = youZanOrderService.queryShopTotalOrder(startTime, endTime, shopInfo.getId());
        } else if (StringUtil.equals("MGJ", shopInfo.getPlatformCode())) {
            totalOrder = moGuJieService.queryShopTotalOrder(startTime, endTime, shopInfo.getId());
        } else if (StringUtil.equals("Ymatou", shopInfo.getPlatformCode())) {
            totalOrder = ymatouService.queryShopTotalOrder(startTime, endTime, shopInfo.getId());
        }
        return String.valueOf(totalOrder);
    }


    /**
     * 接收订单推送信息
     * @param orderMain
     */
    @Override
    public void pushOrder(@Valid OrderMain orderMain) {
        System.out.println(orderMain);
    }

    /**
     * 小时单量播报
     * @return
     */
    @Override
    public LinkedList<Map<String, Object>> getOrderHour(String startCountTime, String endCountTime, String startOrderCreateTime, String endOrderCreateTime, String preSell) {
        if (startCountTime == null)
            throw new BadRequestException("请选择统计时间");
        if (endCountTime == null)
            throw new BadRequestException("请选择统计时间");
        if (startOrderCreateTime == null)
            throw new BadRequestException("请选择订单创建时间");
        if (endOrderCreateTime == null)
            throw new BadRequestException("请选择订单创建时间");
        List<Date> dates = DateUtils.betweenHours(DateUtils.parse(startCountTime), DateUtils.parse(endCountTime));
        LinkedList<Map<String, Object>> resultList = new LinkedList<>();
        for (Date date : dates) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("date", DateUtils.format(date, "MM月dd日 HH时"));
            // 查询当前总创建、总件单比、当前出库、当前剩余
            crossBorderOrderRepository.queryHourOrderCount(startCountTime, endCountTime, startOrderCreateTime, endOrderCreateTime, preSell);


            resultList.add(resultMap);
        }
        return resultList;
    }

    /**
     * 订单生产进度
     * @param startCreateTime
     * @param endCreateTime
     * @param platformCode
     * @return
     */
    @Override
    public JSONObject orderProcess(String startCreateTime, String endCreateTime, String platformCode) {
        if (StringUtils.isBlank(startCreateTime) || StringUtils.equals("undefined", startCreateTime))
            throw new BadRequestException("请选择拉单时间");
        if (StringUtils.isBlank(endCreateTime) || StringUtils.equals("undefined", endCreateTime))
            throw new BadRequestException("请选择拉单时间");
        if (StringUtils.isBlank(platformCode) || StringUtils.equals("undefined", platformCode))
            throw new BadRequestException("请选择电商平台");

        Timestamp today = DateUtils.parseDate(endCreateTime);
        String todayStr = DateUtils.format(today, DatePattern.NORM_DATE_PATTERN);
        String startOutTime =  todayStr + " 00:00:00";
        String endOutTime =  todayStr + " 23:59:59";

        // 1.查询分小时进单
        List<Map<String,Object>> resultIn = crossBorderOrderRepository.queryProcessIn(startCreateTime, endCreateTime, platformCode);
        // 2.查询分小时出库
        List<Map<String,Object>> resultOut = crossBorderOrderRepository.queryProcessOut(startOutTime, endOutTime, platformCode);
        // 3.查询当前未出库
        List<Map<String, Object>> resultNoDeliver = crossBorderOrderRepository.queryCurrentNoDeliver(startCreateTime, endCreateTime, platformCode);
        List<Date> dates = DateUtils.betweenHours(DateUtils.parse(startCreateTime, DatePattern.NORM_DATETIME_PATTERN), DateUtils.parse(endOutTime, DatePattern.NORM_DATETIME_PATTERN));
        List<Map<String,Object>> result = new ArrayList<>();
        for (Date dateHour : dates) {
            String dateHourStr = DateUtils.format(dateHour, "yyyy-MM-dd HH");

            Map<String,Object> mapResult = new HashMap<>();
            mapResult.put("time", dateHourStr);
            for (Map<String,Object> mapIn : resultIn) {
                // 组装进单
                String timeIn = String.valueOf(mapIn.get("time"));
                if (StringUtils.equals(dateHourStr, timeIn)) {
                    mapResult.put("createOrder", mapIn.get("createOrder"));
                    mapResult.put("createPLOrder", mapIn.get("createPLOrder"));
                    mapResult.put("createSDOrder", mapIn.get("createSDOrder"));
                }
            }
            for (Map<String,Object> mapOut : resultOut) {
                // 组装出库
                String timeOut = String.valueOf(mapOut.get("time"));
                if (StringUtils.equals(dateHourStr, timeOut)) {
                    mapResult.put("deliverAllOrder", mapOut.get("deliverAllOrder"));
                    mapResult.put("deliverPLOrder", mapOut.get("deliverPLOrder"));
                    mapResult.put("deliverSDOrder", mapOut.get("deliverSDOrder"));
                }
            }
            result.add(mapResult);
        }
        JSONObject resultObj = new JSONObject();
        resultObj.putOnce("hourOrder", result);
        resultObj.putOnce("allOrder", resultNoDeliver);
        // 3.查询当前汇总数据
        return resultObj;
    }

    @Override
    public List<CrossBorderOrder> queryByCNDeliverOrder() {
        return crossBorderOrderRepository.queryByCNDeliverOrder();
    }

    /**
     * 创建o2o订单
     * @param data
     */
    @Override
    public void createO2OOrder(String data) {
        JSONObject orderObject = JSONUtil.parseObj(data);
        String orderNo = orderObject.getStr("order_no");
        CrossBorderOrder exist = queryByOrderNo(orderNo);
        if (exist != null)
            return;
        CrossBorderOrder order = new CrossBorderOrder();
        String order_amount = orderObject.getStr("order_amount");
        String pay_amount = orderObject.getStr("pay_amount");
        String payment_mode = orderObject.getStr("payment_mode");
        String payment_no = orderObject.getStr("payment_no");
        String order_seq_no = orderObject.getStr("order_seq_no");
        String name = orderObject.getStr("name");
        String id_num = orderObject.getStr("id_num");
        Date add_time = orderObject.getDate("add_time");
        Date pay_time = orderObject.getDate("pay_time");
        String buyer_account = orderObject.getStr("buyer_account");
        String consignee = orderObject.getStr("consignee");
        String consignee_mobile = orderObject.getStr("consignee_mobile");
        String province = orderObject.getStr("province");
        String city = orderObject.getStr("city");
        String district = orderObject.getStr("district");
        String consignee_addr = orderObject.getStr("consignee_addr");
        String logistics_name = orderObject.getStr("consignee_addr");

        ShopInfo shopInfo = shopInfoService.queryByShopCode("FBO2O");
        order.setCustomersId(shopInfo.getCustId());
        order.setShopId(shopInfo.getId());
        order.setPlatformCode(PlatformConstant.O2O);
        order.setOrderNo(orderNo);
        order.setPayment(pay_amount);
        order.setBuyerName(name);
        order.setBuyerIdNum(id_num);
        order.setOrderCreateTime(new Timestamp(add_time.getTime()));
        order.setPayTime(new Timestamp(pay_time.getTime()));
        order.setBuyerAccount(buyer_account);
        order.setConsigneeName(consignee);
        order.setConsigneeTel(consignee_mobile);
        order.setProvince(province);
        order.setCity(city);
        order.setDistrict(district);
        order.setConsigneeAddr(consignee_addr);

        order.setStatus(CBOrderStatusEnum.STATUS_200.getCode());
        order.setCreateTime(new Timestamp(System.currentTimeMillis()));
        order.setCreateBy("SYSTEM");

        JSONArray goodsArry = orderObject.getJSONArray("goods");
        List<CrossBorderOrderDetails> list = new ArrayList<>();
        for (int i = 0; i < goodsArry.size(); i++) {
            CrossBorderOrderDetails details = new CrossBorderOrderDetails();
            String product_name = goodsArry.getJSONObject(i).getStr("product_name");
            String product_no = goodsArry.getJSONObject(i).getStr("product_no");
            String price = goodsArry.getJSONObject(i).getStr("price");
            int qty = goodsArry.getJSONObject(i).getInt("qty");
            BaseSku baseSku = baseSkuService.queryByOutGoodsNo(product_no);
            details.setOrderNo(orderNo);
            details.setGoodsNo(product_no);
            details.setPayment(price);
            details.setQty(String.valueOf(qty));
            details.setFontGoodsName(product_name);
            if (baseSku != null) {
                details.setGoodsId(baseSku.getId());
                details.setGoodsNo(baseSku.getGoodsNo());
                details.setGoodsCode(baseSku.getGoodsCode());
                details.setBarCode(baseSku.getBarCode());
                details.setGoodsNo(baseSku.getGoodsNo());
            }
            list.add(details);
        }
        order.setItemList(list);
        createWithDetail(order);


    }


}
