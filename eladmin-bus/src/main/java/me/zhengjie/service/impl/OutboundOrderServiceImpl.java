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

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.exception.EntityExistException;
import me.zhengjie.rest.model.Flux.DocOrderHeader;
import me.zhengjie.rest.model.douyin.TallyOrderReview;
import me.zhengjie.service.*;
import me.zhengjie.support.WmsSupport;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.OutboundOrderRepository;
import me.zhengjie.service.dto.OutboundOrderDto;
import me.zhengjie.service.dto.OutboundOrderQueryCriteria;
import me.zhengjie.service.mapstruct.OutboundOrderMapper;
import me.zhengjie.utils.constant.PlatformConstant;
import me.zhengjie.utils.enums.BooleanEnum;
import me.zhengjie.utils.enums.OutBoundStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
 * @author luob
 * @website https://el-admin.vip
 * @description 服务实现
 * @date 2021-07-13
 **/
@Service
@RequiredArgsConstructor
public class OutboundOrderServiceImpl implements OutboundOrderService {

    private final OutboundOrderRepository outboundOrderRepository;
    private final OutboundOrderMapper outboundOrderMapper;

    @Autowired
    private WmsSupport wmsSupport;

    @Autowired
    private OutboundOrderDetailsService outboundOrderDetailsService;

    @Autowired
    private OutboundOrderLogService outboundOrderLogService;

    @Autowired
    ShopInfoService shopInfoService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private DouyinService douyinService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private RuoYuChenService ruoYuChenService;

    @Autowired
    private JackYunService jackYunService;

    @Autowired
    private CustomerInfoService customerInfoService;

    @Override
    public Map<String, Object> queryAll(OutboundOrderQueryCriteria criteria, Pageable pageable) {
        Page<OutboundOrder> page = outboundOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(outboundOrderMapper::toDto));
    }

    @Override
    public List<OutboundOrderDto> queryAll(OutboundOrderQueryCriteria criteria) {
        return outboundOrderMapper.toDto(outboundOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
    }

    @Override
    @Transactional
    public OutboundOrderDto findById(Long id) {
        OutboundOrder outboundOrder = outboundOrderRepository.findById(id).orElseGet(OutboundOrder::new);
        ValidationUtil.isNull(outboundOrder.getId(), "OutboundOrder", "id", id);
        return outboundOrderMapper.toDto(outboundOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OutboundOrderDto create(OutboundOrder resources) {
        if (outboundOrderRepository.findByOrderNo(resources.getOrderNo()) != null) {
            throw new EntityExistException(OutboundOrder.class, "order_no", resources.getOrderNo());
        }
        ShopInfo shopInfo = shopInfoService.findById(resources.getShopId());
        if (!StringUtils.equals("10", resources.getOrderType()) && StringUtils.equals(PlatformConstant.DY, shopInfo.getPlatformCode())
                && !StringUtil.equals(resources.getIsOnline(), "1")) {
            throw new BadRequestException("抖音店铺请联系商家从抖音后台下单");
        }
        if (resources.getCustomersId().intValue() != shopInfo.getCustId().intValue()) {
            throw new BadRequestException("当前选择店铺不属于选择的客户");
        }
        List<OutboundOrderDetails> details = resources.getDetails();
        if (!CollectionUtils.isNotEmpty(details))
            throw new BadRequestException("请添加商品明细！");
        resources.setPlatformCode(shopInfo.getPlatformCode());
        resources.setOrderNo(genOrderNo());
        resources.setStatus(OutBoundStatusEnum.STATUS_700.getCode());
        resources.setExpectSkuNum(details.size());
        Integer expectTotalNum = 0;
        for (OutboundOrderDetails detail : details) {
            expectTotalNum = expectTotalNum + detail.getExpectNum();
        }
        resources.setExpectTotalNum(expectTotalNum);
        String creator = resources.getCreateBy();
        if (StringUtil.isBlank(resources.getCreateBy())) {
            resources.setCreateBy(SecurityUtils.getCurrentUsername());
            creator = resources.getCreateBy();
        }
        resources.setCreateTime(new Timestamp(System.currentTimeMillis()));
        OutboundOrder order = outboundOrderRepository.save(resources);
        for (OutboundOrderDetails item : details) {
            BaseSku baseSku = baseSkuService.queryByGoodsNoAndShop(item.getGoodsNo(), resources.getShopId());
            if (baseSku != null) {
                item.setGoodsId(baseSku.getId());
                item.setGoodsName(baseSku.getGoodsName());
            }
            item.setOrderId(order.getId());
            item.setOrderNo(order.getOrderNo());
        }
        outboundOrderDetailsService.saveBatch(details);
        OutboundOrderLog log = new OutboundOrderLog(
                order.getId(),
                order.getOrderNo(),
                OutBoundStatusEnum.STATUS_700.getCode().toString(),
                "",
                "",
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription(),
                creator
        );
        outboundOrderLogService.create(log);
        return outboundOrderMapper.toDto(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(OutboundOrder resources) {
        OutboundOrder outboundOrder = outboundOrderRepository.findById(resources.getId()).orElseGet(OutboundOrder::new);
        ValidationUtil.isNull(outboundOrder.getId(), "OutboundOrder", "id", resources.getId());
        outboundOrder.copy(resources);
        outboundOrderRepository.save(outboundOrder);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            outboundOrderRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<OutboundOrderDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (OutboundOrderDto outboundOrder : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("客户", customerInfoService.queryById(outboundOrder.getCustomersId()).getCustNickName());
            map.put("店铺", shopInfoService.queryById(outboundOrder.getShopId()).getName());
            map.put("出库单号", outboundOrder.getOrderNo());
            map.put("外部单号", outboundOrder.getOutNo());
            map.put("WMS单号", outboundOrder.getWmsNo());
            map.put("单据类型", outboundOrder.getOrderType());
            map.put("状态", OutBoundStatusEnum.getDesc(outboundOrder.getStatus()));
            map.put("是否4PL", StringUtils.equals("1", outboundOrder.getIsFourPl()) ? "是" : "否");
            map.put("原单号", outboundOrder.getOriginalNo());
            map.put("预期发货时间", outboundOrder.getExpectDeliverTime());
            map.put("理货维度", outboundOrder.getTallyWay());
            map.put("托数", outboundOrder.getPalletNum());
            map.put("箱数", outboundOrder.getBoxNum());
            map.put("预期SKU数", outboundOrder.getExpectSkuNum());
            map.put("出库SKU数", outboundOrder.getDeliverSkuNum());
            map.put("预期总件数", outboundOrder.getExpectTotalNum());
            map.put("出库总件数", outboundOrder.getDeliverTotalNum());
            map.put("出库正品件数", outboundOrder.getDeliverNormalNum());
            map.put("出库残品件数", outboundOrder.getDeliverDamagedNum());
            map.put("接单确认人", outboundOrder.getConfirmBy());
            map.put("接单确认时间", outboundOrder.getConfirmTime());
            map.put("理货人", outboundOrder.getTallyBy());
            map.put("理货开始时间", outboundOrder.getTallyStartTime());
            map.put("理货开始回传时间", outboundOrder.getTallyStartBackTime());
            map.put("理货结束时间", outboundOrder.getTallyEndTime());
            map.put("理货结束回传时间", outboundOrder.getTallyEndBackTime());
            map.put("收货人", outboundOrder.getDeliverBy());
            map.put("收货完成时间", outboundOrder.getDeliverTime());
            map.put("收货完成回传时间", outboundOrder.getDeliverBackTime());
            map.put("取消时间", outboundOrder.getCancelTime());
            map.put("创建人", outboundOrder.getCreateBy());
            map.put("创建时间", outboundOrder.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void doExportDetails(List<OutboundOrderDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (OutboundOrderDto outboundOrder : all) {
            List<OutboundOrderDetails> outboundOrderDetails = outboundOrderDetailsService.findbyOutId(outboundOrder.getId());
            for (OutboundOrderDetails details : outboundOrderDetails) {
                BaseSku baseSku = baseSkuService.queryByGoodsNo(details.getGoodsNo());
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("客户ID", customerInfoService.queryById(outboundOrder.getCustomersId()).getCustNickName());
                map.put("店铺ID", shopInfoService.queryById(outboundOrder.getShopId()).getName());
                map.put("出库单号", outboundOrder.getOrderNo());
                map.put("外部单号", outboundOrder.getOutNo());
                map.put("WMS单号", outboundOrder.getWmsNo());
                map.put("单据类型", outboundOrder.getOrderType());

                map.put("外部货号", baseSku.getOuterGoodsNo());
                map.put("海关货号", baseSku.getGoodsNo());
                map.put("预期数量", details.getExpectNum());
                list.add(map);
            }

        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void refreshStatus() {
        OutboundOrderQueryCriteria criteria = new OutboundOrderQueryCriteria();
        List<Integer> status = new ArrayList<>();
        status.add(OutBoundStatusEnum.STATUS_715.getCode());
        status.add(OutBoundStatusEnum.STATUS_730.getCode());
        status.add(OutBoundStatusEnum.STATUS_735.getCode());
        status.add(OutBoundStatusEnum.STATUS_740.getCode());
        status.add(OutBoundStatusEnum.STATUS_745.getCode());
        criteria.setStatus(status);
        List<OutboundOrder> orders = outboundOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        if (CollectionUtils.isNotEmpty(orders)) {
            for (OutboundOrder order : orders) {
                refreshStatus(order.getId());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshStatus(Long id) {
        OutboundOrder order = queryByIdWithDetails(id);
        JSONObject wmsOrder = wmsSupport.querySo(order.getOrderNo(), order.getOrderNo());
        if (wmsOrder == null)
            throw new BadRequestException("获取WMS状态失败:" + order.getOrderNo());
        if (StringUtil.isBlank(order.getOnlineSrc())){
            if (StringUtil.isEmpty(order.getWmsNo())) {
                order.setWmsNo(wmsOrder.getJSONObject("header").getStr("orderno"));
            }
            if (StringUtil.equals(wmsOrder.getJSONObject("header").getStr("sostatus"), "99")
                    || StringUtil.equals(wmsOrder.getJSONObject("header").getStr("sostatus"), "70")) {
                // 已收货完成
                order.setStatus(OutBoundStatusEnum.STATUS_755.getCode());
                order.setDeliverTime(new Timestamp(System.currentTimeMillis()));
                //order.setDeliverBackTime(new Timestamp(System.currentTimeMillis()));
                order.setDeliverBy("ADMIN");

                JSONArray wmsDetails = wmsOrder.getJSONArray("details");
                if (wmsDetails == null)
                    throw new BadRequestException("获取WMS状态失败:" + order.getOrderNo());
                Integer totalTakeNum = 0;
                List<OutboundOrderDetails> details = order.getDetails();
                for (int i = 0; i < wmsDetails.size(); i++) {
                    for (OutboundOrderDetails orderDetails : details) {
                        if (StringUtil.equals(wmsDetails.getJSONObject(i).getStr("sku"),
                                orderDetails.getGoodsNo())) {
                            Integer takeNum = wmsDetails.getJSONObject(i).getInt("qtyshippedEach");
                            orderDetails.setDeliverNum(takeNum);
                            totalTakeNum = totalTakeNum + takeNum;
                        }
                    }
                }
                order.setDeliverTotalNum(totalTakeNum);
                update(order);
                outboundOrderDetailsService.saveBatch(details);

                OutboundOrderLog log = new OutboundOrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        OutBoundStatusEnum.STATUS_755.getCode().toString(),
                        "",
                        "",
                        BooleanEnum.SUCCESS.getCode(),
                        BooleanEnum.SUCCESS.getDescription(),
                        "ADMIN"
                );
                outboundOrderLogService.create(log);
            } else if (StringUtil.equals(wmsOrder.getJSONObject("header").getStr("sostatus"), "90")) {
                // 已取消
                order.setStatus(OutBoundStatusEnum.STATUS_888.getCode());
                update(order);
                OutboundOrderLog log = new OutboundOrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        OutBoundStatusEnum.STATUS_888.getCode().toString(),
                        "",
                        "",
                        BooleanEnum.SUCCESS.getCode(),
                        BooleanEnum.SUCCESS.getDescription(),
                        "ADMIN"
                );
                outboundOrderLogService.create(log);
            } else {
                update(order);
            }
        }else {
            if (StringUtil.isEmpty(order.getWmsNo())&&!wmsOrder.isEmpty()) {
                order.setWmsNo(wmsOrder.getJSONObject("header").getStr("orderno"));
                update(order);
            }
        }

    }

    @Override
    public OutboundOrder queryByIdWithDetails(Long id) {
        OutboundOrder outboundOrder = outboundOrderRepository.findById(id).orElseGet(OutboundOrder::new);
        List<OutboundOrderDetails> details = outboundOrderDetailsService.findbyOutId(id);
        outboundOrder.setDetails(details);
        return outboundOrder;
    }

    @Override
    public OutboundOrder queryByOrderNo(String orderNo) {
        return outboundOrderRepository.findByOrderNo(orderNo);
    }

    @Override
    public void verifyPass(Long orderId) throws Exception {
        OutboundOrder order = queryByIdWithDetails(orderId);
        if (order.getStatus().intValue() != OutBoundStatusEnum.STATUS_700.getCode().intValue())
            throw new BadRequestException("当前状态不可审核通过");
        try {
            wmsSupport.pushOutBoundOrder(order);// 推送WMS

            order.setStatus(OutBoundStatusEnum.STATUS_715.getCode());
            update(order);
            OutboundOrderLog log = new OutboundOrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    OutBoundStatusEnum.STATUS_715.getCode().toString(),
                    "",
                    "",
                    BooleanEnum.SUCCESS.getCode(),
                    "审核"+BooleanEnum.SUCCESS.getDescription(),
                    SecurityUtils.getCurrentUsername()
            );
            outboundOrderLogService.create(log);
        } catch (Exception e) {
            OutboundOrderLog log = new OutboundOrderLog(order.getId(),
                    order.getOrderNo(),
                    OutBoundStatusEnum.STATUS_715.getCode().toString(),
                    "",
                    "",
                    BooleanEnum.FAIL.getCode(),
                    e.getMessage(),
                    SecurityUtils.getCurrentUsername()
            );
            outboundOrderLogService.create(log);
            throw e;
        }
    }

    @Override
    public List<Map<String, Object>> uploadSku(List<Map<String, Object>> maps) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            int sNo = Integer.valueOf(map.get("序号").toString());
            String goodsNo = map.get("货号*") != null ? map.get("货号*").toString() : null;
            String num = map.get("数量*") != null ? map.get("数量*").toString() : null;
            if (StringUtil.isBlank(goodsNo))
                throw new BadRequestException("第" + sNo + "行，货号不能为空");
            if (StringUtil.isBlank(num))
                throw new BadRequestException("第" + sNo + "行，数量不能为空");
            Map<String, Object> resultItem = new HashMap<>();
            resultItem.put("goodsNo", goodsNo);
            resultItem.put("num", num);
            result.add(resultItem);
        }
        return result;
    }

    @Override
    public void dyConfirmCancel(Long id) {
        OutboundOrder outboundOrder = queryByIdWithDetails(id);
        String name;
        try {
            name = SecurityUtils.getCurrentUsername();
        } catch (Exception e) {
            name = "System";
        }
        OutboundOrderLog log = new OutboundOrderLog(
                outboundOrder.getId(),
                outboundOrder.getOrderNo(),
                OutBoundStatusEnum.STATUS_888.getCode().toString(),
                "",
                "",
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription(),
                name
        );
        try {
            if (StringUtil.equals("DY", outboundOrder.getOnlineSrc()))
                douyinService.dyConfirmCancel(outboundOrder, log);
        } catch (Exception e) {
            e.printStackTrace();
            outboundOrderLogService.create(log);
            throw new BadRequestException(e.getMessage() == null ? "null" : e.getMessage());
        }
        outboundOrder.setStatus(OutBoundStatusEnum.STATUS_888.getCode());
        outboundOrder.setCancelTime(new Timestamp(System.currentTimeMillis()));
        update(outboundOrder);
        outboundOrderLogService.create(log);
    }

    @Override
    public void dyConfirmOrder(Long id) {
        OutboundOrder outboundOrder = queryByIdWithDetails(id);
        confirmOrder(outboundOrder);
    }

    private void confirmOrder(OutboundOrder outboundOrder) {
        if (outboundOrder.getStatus()>=OutBoundStatusEnum.STATUS_715.getCode()&&outboundOrder.getConfirmTime()!=null)
            throw new BadRequestException("当前出库单状态为"+OutBoundStatusEnum.getDesc(outboundOrder.getStatus()));
        if (CollectionUtils.isEmpty(outboundOrder.getDetails())) {
            outboundOrder.setDetails(outboundOrderDetailsService.findbyOutId(outboundOrder.getId()));
        }
        String name;
        try {
            name = SecurityUtils.getCurrentUsername();
        } catch (Exception e) {
            name = "System";
        }
        OutboundOrderLog log = new OutboundOrderLog(
                outboundOrder.getId(),
                outboundOrder.getOrderNo(),
                OutBoundStatusEnum.STATUS_715.getCode().toString(),
                "",
                "",
                BooleanEnum.SUCCESS.getCode(),
                "回传"+BooleanEnum.SUCCESS.getDescription(),
                name
        );
        try {
            if (StringUtil.equals("DY", outboundOrder.getOnlineSrc()))
                douyinService.dyConfirmOrder(outboundOrder, log);
        } catch (Exception e) {
            e.printStackTrace();
            outboundOrderLogService.create(log);
            throw new BadRequestException(e.getMessage() == null ? "null" : e.getMessage());
        }
        if (StringUtil.equals("1", log.getSuccess())) {
            outboundOrder.setConfirmTime(new Timestamp(System.currentTimeMillis()));
            outboundOrder.setConfirmBy(name);
        }
        update(outboundOrder);
        outboundOrderLogService.create(log);
    }

    @Override
    public void dyConfirmStockTally(Long id) {
        OutboundOrder outboundOrder = outboundOrderRepository.findById(id).orElse(null);
        if (outboundOrder == null)
            throw new BadRequestException("出库单不存在");
        if (StringUtil.equals("DY", outboundOrder.getOnlineSrc()) && !StringUtil.equals("1", outboundOrder.getIsFourPl()))
            throw new BadRequestException("抖音非4PL出库单不可回传理货开始");
        if (!outboundOrder.getStatus().equals(OutBoundStatusEnum.STATUS_715.getCode()))
            throw new BadRequestException("当前状态不是接单确认");
        JSONObject wmsOrder = wmsSupport.querySo(outboundOrder.getOrderNo(), outboundOrder.getOrderNo());
        DocOrderHeader orderHead = wmsOrder.getBean("header", DocOrderHeader.class);
        if (orderHead != null && orderHead.getOrderno() != null) {
            int soStatus = Integer.parseInt(orderHead.getSostatus());
            if (!(soStatus >= 40 && soStatus != 90)) {
                throw new BadRequestException("富勒出库单状态不是分配完成以上的状态");
            }
        }
        confirmStockTally(outboundOrder);
    }

    private void confirmStockTally(OutboundOrder outboundOrder) {
        if (!(outboundOrder.getStatus().equals(OutBoundStatusEnum.STATUS_715.getCode()) || outboundOrder.getStatus().equals(OutBoundStatusEnum.STATUS_748.getCode())))
            throw new BadRequestException("当前出库单状态为"+OutBoundStatusEnum.getDesc(outboundOrder.getStatus()));
        List<OutboundOrderDetails> details = outboundOrderDetailsService.findbyOutId(outboundOrder.getId());
        outboundOrder.setDetails(details);
        String name;
        try {
            name = SecurityUtils.getCurrentUsername();
        } catch (Exception e) {
            name = "System";
        }
        OutboundOrderLog log = new OutboundOrderLog(
                outboundOrder.getId(),
                outboundOrder.getOrderNo(),
                OutBoundStatusEnum.STATUS_735.getCode().toString(),
                "",
                "",
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription(),
                name
        );
        try {
            if (StringUtil.equals(outboundOrder.getOnlineSrc(), "DY") && StringUtil.equals(outboundOrder.getIsFourPl(), "1"))
                douyinService.dyConfirmStockTally(outboundOrder, log);
        } catch (Exception e) {
            e.printStackTrace();
            outboundOrderLogService.create(log);
            throw new BadRequestException(e.getMessage() == null ? "null" : e.getMessage());
        }
        if (StringUtil.equals("1", log.getSuccess())) {
            outboundOrder.setTallyStartBackTime(new Timestamp(System.currentTimeMillis()));
            outboundOrder.setTallyBy(name);
            outboundOrder.setTallyStartTime(outboundOrder.getTallyStartTime());
        }
        outboundOrder.setStatus(OutBoundStatusEnum.STATUS_735.getCode());
        update(outboundOrder);
        outboundOrderLogService.create(log);
    }

    @Override
    public void dyConfirmStockedTally(Long id) {
        OutboundOrder outboundOrder = outboundOrderRepository.findById(id).orElse(null);
        if (outboundOrder == null)
            throw new BadRequestException("出库单不存在");
        if (StringUtil.equals("DY", outboundOrder.getOnlineSrc()) && !StringUtil.equals("1", outboundOrder.getIsFourPl()))
            throw new BadRequestException("抖音非4PL出库单不可回传理货结束");
        if (!outboundOrder.getStatus().equals(OutBoundStatusEnum.STATUS_735.getCode()))
            throw new BadRequestException("当前状态不是理货开始回传");
        JSONObject wmsOrder = wmsSupport.querySo(outboundOrder.getOrderNo(), outboundOrder.getOrderNo());
        DocOrderHeader orderHead = wmsOrder.getBean("header", DocOrderHeader.class);
        if (orderHead != null && orderHead.getOrderno() != null) {
            int soStatus = Integer.parseInt(orderHead.getSostatus());
            if (!(soStatus >= 50 && soStatus != 90)) {
                throw new BadRequestException("富勒出库单状态不是部分拣货完成以上的状态");
            }
        }
        confirmStockedTally(outboundOrder);
        if (StringUtil.equals(outboundOrder.getIsFourPl(), "1")) {
            //4pl单再次回传理货完成后待审核
            outboundOrder.setStatus(OutBoundStatusEnum.STATUS_746.getCode());
        }
    }

    private void confirmStockedTally(OutboundOrder outboundOrder) {
        if (outboundOrder.getStatus()>=OutBoundStatusEnum.STATUS_745.getCode())
            throw new BadRequestException("当前出库单状态为"+OutBoundStatusEnum.getDesc(outboundOrder.getStatus()));
        List<OutboundOrderDetails> details = outboundOrderDetailsService.findbyOutId(outboundOrder.getId());
        outboundOrder.setDetails(details);
        String name;
        try {
            name = SecurityUtils.getCurrentUsername();
        } catch (Exception e) {
            name = "System";
        }
        OutboundOrderLog log = new OutboundOrderLog(
                outboundOrder.getId(),
                outboundOrder.getOrderNo(),
                OutBoundStatusEnum.STATUS_745.getCode().toString(),
                "",
                "",
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription(),
                name
        );
        try {
            if (StringUtil.equals(outboundOrder.getOnlineSrc(), "DY")) {
                if (StringUtil.equals(outboundOrder.getIsFourPl(), "1"))
                    douyinService.dyConfirmStockedTally(outboundOrder, log);
            }
        } catch (Exception e) {
            e.printStackTrace();
            outboundOrderLogService.create(log);
            throw new BadRequestException(e.getMessage() == null ? "null" : e.getMessage());
        }
        if (StringUtil.equals("1", log.getSuccess())) {
            outboundOrder.setTallyEndBackTime(new Timestamp(System.currentTimeMillis()));
            outboundOrder.setTallyEndTime(outboundOrder.getTallyEndBackTime());
        }
        if (StringUtil.equals(outboundOrder.getIsFourPl(), "1")) {
            //4pl单需要等待理货审核
            outboundOrder.setStatus(OutBoundStatusEnum.STATUS_746.getCode());
        } else {
            //非4pl单是理货完成回传
            outboundOrder.setStatus(OutBoundStatusEnum.STATUS_745.getCode());
        }
        update(outboundOrder);
        outboundOrderLogService.create(log);
    }

    @Override
    public void dyConfirmOut(Long id) {
        OutboundOrder outboundOrder = queryByIdWithDetails(id);
        if (StringUtil.equals(outboundOrder.getIsFourPl(), "1") && !outboundOrder.getStatus().equals(OutBoundStatusEnum.STATUS_747.getCode()))
            throw new BadRequestException("4pl出库单出库需要商家同意理货审核");
        if (StringUtil.equals(outboundOrder.getPlatformCode(),"DY")){
            if (!outboundOrder.getStatus().equals(OutBoundStatusEnum.STATUS_715.getCode())&&!StringUtil.equals(outboundOrder.getIsFourPl(), "1"))
                throw new BadRequestException("当前状态不是接单确认");
        }else
        {
            if (!outboundOrder.getStatus().equals(OutBoundStatusEnum.STATUS_745.getCode()))
                throw new BadRequestException("当前状态不是理货完成");
        }

        JSONObject wmsOrder = wmsSupport.querySo(outboundOrder.getOrderNo(), outboundOrder.getOrderNo());



        DocOrderHeader orderHead = wmsOrder.getBean("header", DocOrderHeader.class);
        if (orderHead != null && orderHead.getOrderno() != null) {
            int soStatus = Integer.parseInt(orderHead.getSostatus());
            if (!(soStatus >= 50 && soStatus != 90)) {
                throw new BadRequestException("富勒出库单状态不是部分拣货完成以上的状态");
            }
            try {
                wmsSupport.deliver(outboundOrder.getWmsNo());
            } catch (Exception e) {
                e.printStackTrace();
                throw new BadRequestException(e.getMessage());
            }
        }
        confirmOut(outboundOrder);
    }

    private void confirmOut(OutboundOrder outboundOrder) {
        if (outboundOrder.getStatus()>=OutBoundStatusEnum.STATUS_755.getCode())
            throw new BadRequestException("当前出库单状态为"+OutBoundStatusEnum.getDesc(outboundOrder.getStatus()));
        if (CollectionUtils.isEmpty(outboundOrder.getDetails())) {
            outboundOrder.setDetails(outboundOrderDetailsService.findbyOutId(outboundOrder.getId()));
        }
        String name;
        boolean isTask = false;
        try {
            name = SecurityUtils.getCurrentUsername();
        } catch (Exception e) {
            name = "System";
            isTask = true;
        }
        OutboundOrderLog log = new OutboundOrderLog(
                outboundOrder.getId(),
                outboundOrder.getOrderNo(),
                OutBoundStatusEnum.STATUS_755.getCode().toString(),
                "",
                "",
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription(),
                name
        );
        try {
            if (StringUtil.equals(outboundOrder.getOnlineSrc(), "DY")) {
                if (StringUtil.equals("1", outboundOrder.getIsFourPl())) {
                    if (outboundOrder.getStatus() == 747)
                        douyinService.dyConfirmOut(outboundOrder, log);
                    else {
                        if (isTask)
                            return;
                        else
                            throw new BadRequestException("理货审核未通过");
                    }
                } else
                    douyinService.dyConfirmOut(outboundOrder, log);
            } else if (StringUtil.equals(outboundOrder.getOnlineSrc(), "RuoYuChen"))
                ruoYuChenService.rycConfirmOrderDeliver(outboundOrder, log);
            else if (StringUtil.equals(outboundOrder.getOnlineSrc(), "JackYun"))
                jackYunService.confirmOut(outboundOrder, log);
        } catch (Exception e) {
            e.printStackTrace();
            log.setSuccess(BooleanEnum.FAIL.getCode());
            log.setMsg(StringUtil.isBlank(e.getMessage()) ? "null" : e.getMessage());
            outboundOrderLogService.create(log);
            if (!StringUtil.equals(outboundOrder.getIsFourPl(), "1"))
                outboundOrder.setStatus(745);
            outboundOrder.setFreezeReason(e.getMessage());
            update(outboundOrder);
            return;
        }
        if (StringUtil.equals("1", log.getSuccess())) {
            outboundOrder.setDeliverBackTime(new Timestamp(System.currentTimeMillis()));
            outboundOrder.setDeliverBy(name);
            outboundOrder.setStatus(OutBoundStatusEnum.STATUS_755.getCode());
        }
        update(outboundOrder);
        outboundOrderLogService.create(log);
        if (StringUtil.equals("0",log.getSuccess()))
            throw new BadRequestException(log.getMsg());
    }

    @Override
    public OutboundOrder queryByOutNo(String outNo) {
        OutboundOrder outboundOrder = new OutboundOrder();
        outboundOrder.setOutNo(outNo);
        List<OutboundOrder> list = outboundOrderRepository.findAll(Example.of(outboundOrder));
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }


    private synchronized String genOrderNo() {
        int tryCount = 0;
        String qz = "CK";
        String time = String.valueOf(System.currentTimeMillis());
        String result = qz + time;
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            result += random.nextInt(10);
        }
        OutboundOrder order = queryByOrderNo(result);
        if (order != null && tryCount < 4) {
            genOrderNo();
            tryCount++;
        }
        return result;
    }

    /**
     * 监听富勒出库单状态
     */
    @Override
    public void listenFluxOutStatus() {
        // TODO: 2021/12/19 监听富勒出库单状态并根据对应的状态去回传相应渠道
        List<OutboundOrder> list = outboundOrderRepository.findAll1();
        for (OutboundOrder outboundOrder : list) {
            try {
                DocOrderHeader orderHead;
                JSONObject wmsOrder = wmsSupport.querySo(outboundOrder.getOrderNo(), outboundOrder.getOrderNo());
                orderHead = wmsOrder.getBean("header", DocOrderHeader.class);
                if (orderHead == null || orderHead.getOrderno() == null)
                    continue;
                outboundOrder.setWmsStatus(orderHead.getSostatus());
                if (StringUtil.isBlank(outboundOrder.getWmsNo()))
                    outboundOrder.setWmsNo(orderHead.getOrderno());
                update(outboundOrder);
                if (StringUtil.equals(orderHead.getSostatus(), "40") || StringUtil.equals(orderHead.getSostatus(), "99")) {
                    //分配完成/订单完成
                    if (StringUtil.equals(outboundOrder.getIsOnline(), "1")) {
                        if (outboundOrder.getConfirmTime() == null) {
                            //回传接单
                            confirmOrder(outboundOrder);
                        } else if (outboundOrder.getConfirmTime() != null
                                && outboundOrder.getTallyStartBackTime() == null
                                && StringUtil.equals(orderHead.getSostatus(), "99")) {
                            //回传理货开始
                            confirmStockTally(outboundOrder);
                        } else if (outboundOrder.getTallyStartBackTime() != null
                                && outboundOrder.getTallyEndBackTime() == null
                                && StringUtil.equals(orderHead.getSostatus(), "99")) {
                            //回传理货结果
                            confirmStockedTally(outboundOrder);
                        } else if (outboundOrder.getTallyEndBackTime() != null
                                && outboundOrder.getDeliverBackTime() == null
                                && StringUtil.equals(orderHead.getSostatus(), "99")) {
                            //回传出库
                            confirmOut(outboundOrder);
                        }
                    } else {
                        if (StringUtil.equals(orderHead.getSostatus(), "99")) {
                            outboundOrder.setConfirmTime(new Timestamp(System.currentTimeMillis()));
                            outboundOrder.setTallyStartBackTime(new Timestamp(System.currentTimeMillis() + 10000));
                            outboundOrder.setTallyEndBackTime(new Timestamp(System.currentTimeMillis() + 20000));
                            outboundOrder.setDeliverBackTime(new Timestamp(System.currentTimeMillis() + 30000));
                            outboundOrder.setStatus(OutBoundStatusEnum.STATUS_755.getCode());
                            update(outboundOrder);
                        }
                    }
                }
            } catch (Exception e) {
                OutboundOrderLog log = new OutboundOrderLog(
                        outboundOrder.getId(),
                        outboundOrder.getOrderNo(),
                        outboundOrder.getStatus() + "",
                        "",
                        "",
                        BooleanEnum.FAIL.getCode(),
                        e.getMessage(),
                        "Task"
                );
                outboundOrderLogService.create(log);
                update(outboundOrder);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void tallyReview(TallyOrderReview tallyOrderReview) {
        OutboundOrder outboundOrder = outboundOrderRepository.findOutboundOrderByWmsNo(
                tallyOrderReview.getTallyOrderId().substring(0, tallyOrderReview.getTallyOrderId().lastIndexOf("-")));
        if (tallyOrderReview.getRes() == 1) {
            //审核通过
            outboundOrder.setStatus(OutBoundStatusEnum.STATUS_747.getCode());
            outboundOrder.setFreezeReason("-");
            update(outboundOrder);
            outboundOrderLogService.create(new OutboundOrderLog(
                    outboundOrder.getId(),
                    outboundOrder.getOrderNo(),
                    OutBoundStatusEnum.STATUS_747.getCode().toString(),
                    tallyOrderReview.toString(),
                    "",
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription(),
                    "SYSTEM_DY"
            ));
            /*try {
                confirmOut(outboundOrder);
            }catch (Exception e){
                e.printStackTrace();
            }*/
        } else {
            //审核不过
            outboundOrder.setStatus(OutBoundStatusEnum.STATUS_748.getCode());
            update(outboundOrder);
            outboundOrderLogService.create(new OutboundOrderLog(
                    outboundOrder.getId(),
                    outboundOrder.getOrderNo(),
                    OutBoundStatusEnum.STATUS_748.getCode().toString(),
                    tallyOrderReview.toString(),
                    "",
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription(),
                    "SYSTEM_DY"
            ));
        }
    }

    @Override
    public void freezeOrder(Long id, String freezeReason) {
        OutboundOrder order = outboundOrderMapper.toEntity(findById(id));
        if (order == null)
            throw new BadRequestException("出库单id不存在");
        freezeOrder(order, freezeReason);
    }

    @Override
    public void freezeOrder(OutboundOrder outboundOrder, String freezeReason) {
        outboundOrder.setStatus(OutBoundStatusEnum.STATUS_999.getCode());
        outboundOrder.setFreezeReason(freezeReason);
        outboundOrderLogService.create(new OutboundOrderLog(
                outboundOrder.getId(),
                outboundOrder.getOrderNo(),
                OutBoundStatusEnum.STATUS_999.getCode().toString(),
                "",
                "",
                BooleanEnum.SUCCESS.getCode(),
                freezeReason,
                "SYSTEM_DY"
        ));
    }


}
