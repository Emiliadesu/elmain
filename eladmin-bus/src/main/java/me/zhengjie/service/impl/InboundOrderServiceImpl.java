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

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.rest.model.douyin.TallyOrderReview;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.DocAsnHeader;
import me.zhengjie.support.WmsSupport;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.InboundOrderRepository;
import me.zhengjie.service.dto.InboundOrderDto;
import me.zhengjie.service.dto.InboundOrderQueryCriteria;
import me.zhengjie.service.mapstruct.InboundOrderMapper;
import me.zhengjie.utils.constant.PlatformConstant;
import me.zhengjie.utils.enums.BooleanEnum;
import me.zhengjie.utils.enums.InBoundStatusEnum;
import me.zhengjie.utils.enums.InBoundTallyStatusEnum;
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
 * @date 2021-05-13
 **/
@Service
@RequiredArgsConstructor
public class InboundOrderServiceImpl implements InboundOrderService {

    private final InboundOrderRepository inboundOrderRepository;
    private final InboundOrderMapper inboundOrderMapper;

    @Autowired
    private InboundOrderDetailsService inboundOrderDetailsService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private InboundOrderLogService inboundOrderLogService;

    @Autowired
    private WmsSupport wmsSupport;

    @Autowired
    private InboundTallyService inboundTallyService;

    @Autowired
    private InboundTallyDetailsService inboundTallyDetailsService;

    @Autowired
    private DouyinService douyinService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private RuoYuChenService ruoYuChenService;

    @Autowired
    private KJGService kjgService;

    @Autowired
    private JackYunService jackYunService;

    @Autowired
    private CustomerInfoService customerInfoService;


    @Override
    public Map<String, Object> queryAll(InboundOrderQueryCriteria criteria, Pageable pageable) {
        Page<InboundOrder> page = inboundOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(inboundOrderMapper::toDto));
    }

    @Override
    public List<InboundOrderDto> queryAll(InboundOrderQueryCriteria criteria) {
        return inboundOrderMapper.toDto(inboundOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
    }

    @Override
    @Transactional
    public InboundOrderDto findById(Long id) {
        InboundOrder inboundOrder = inboundOrderRepository.findById(id).orElseGet(InboundOrder::new);
        ValidationUtil.isNull(inboundOrder.getId(), "InboundOrder", "id", id);
        return inboundOrderMapper.toDto(inboundOrder);
    }

    @Override
    public InboundOrder queryById(Long id) {
        return inboundOrderRepository.findById(id).orElseGet(InboundOrder::new);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InboundOrderDto create(InboundOrder resources) {
        ShopInfo shopInfo = shopInfoService.findById(resources.getShopId());
        if (!StringUtils.equals("10", resources.getOrderType()) && StringUtils.equals(PlatformConstant.DY, shopInfo.getPlatformCode())
                && !StringUtil.equals(resources.getIsOnline(), "1")) {
            throw new BadRequestException("抖音店铺请联系商家从抖音后台下单");
        }
        if (resources.getCustomersId().intValue() != shopInfo.getCustId().intValue()) {
            throw new BadRequestException("当前选择店铺不属于选择的客户");
        }
        List<InboundOrderDetails> details = resources.getDetails();
        if (!CollectionUtils.isNotEmpty(details))
            throw new BadRequestException("请添加商品明细！");
        resources.setPlatformCode(shopInfo.getPlatformCode());
        resources.setOrderNo(genOrderNo());
        resources.setStatus(InBoundStatusEnum.STATUS_600.getCode());
        resources.setExpectSkuNum(details.size());
        Integer expectTotalNum = 0;
        for (InboundOrderDetails detail : details) {
            expectTotalNum = expectTotalNum + detail.getExpectNum();
        }
        resources.setExpectTotalNum(expectTotalNum);
        String creator = resources.getCreateBy();
        if (StringUtil.isBlank(resources.getCreateBy())) {
            resources.setCreateBy(SecurityUtils.getCurrentUsername());
            creator = resources.getCreateBy();
        }
        resources.setCreateTime(new Timestamp(System.currentTimeMillis()));
        InboundOrder save = inboundOrderRepository.save(resources);
        for (InboundOrderDetails detail : details) {
            BaseSku baseSku = baseSkuService.queryByGoodsNoAndShop(detail.getGoodsNo(), resources.getShopId());
            if (baseSku != null) {
                detail.setGoodsId(baseSku.getId());
                detail.setGoodsName(baseSku.getGoodsName());
            }
            detail.setOrderId(save.getId());
            detail.setOrderNo(save.getOrderNo());
        }
        inboundOrderDetailsService.saveBatch(details);
        InboundOrderLog log = new InboundOrderLog(
                save.getId(),
                save.getOrderNo(),
                InBoundStatusEnum.STATUS_600.getCode().toString(),
                "",
                "",
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription(),
                creator
        );
        inboundOrderLogService.create(log);
        return inboundOrderMapper.toDto(save);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(InboundOrder resources) {
        InboundOrder inboundOrder = inboundOrderRepository.findById(resources.getId()).orElseGet(InboundOrder::new);
        ValidationUtil.isNull(inboundOrder.getId(), "InboundOrder", "id", resources.getId());
        inboundOrder.copy(resources);
        inboundOrderRepository.save(inboundOrder);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            inboundOrderRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<InboundOrderDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (InboundOrderDto inboundOrder : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("客户", customerInfoService.queryById(inboundOrder.getCustomersId()).getCustNickName() );
            map.put("店铺", shopInfoService.queryById(inboundOrder.getShopId()).getName());
            map.put("入库单号", inboundOrder.getOrderNo());
            map.put("外部单号", inboundOrder.getOutNo());
            map.put("wms单号", inboundOrder.getWmsNo());
            map.put("单据类型", inboundOrder.getOrderType());
            map.put("状态", InBoundStatusEnum.getDesc(inboundOrder.getStatus()));
            map.put("是否4PL", StringUtils.equals("1", inboundOrder.getIsFourPl()) ? "是" : "否");
            map.put("预期sku", inboundOrder.getExpectSkuNum());
            map.put("预期件数", inboundOrder.getExpectTotalNum());
            map.put("实际sku", inboundOrder.getExpectSkuNum());
            map.put("实际件数", inboundOrder.getExpectTotalNum());
            map.put("原单号", inboundOrder.getOriginalNo());
            map.put("报关单号", inboundOrder.getDeclareNo());
            map.put("报检单号", inboundOrder.getInspectNo());
            map.put("预期到货时间", inboundOrder.getExpectArriveTime());
            map.put("理货维度", inboundOrder.getTallyWay());
            map.put("接单确认人", inboundOrder.getConfirmBy());
            map.put("接单确认时间", inboundOrder.getConfirmTime());
            map.put("到货登记人", inboundOrder.getArriveBy());
            map.put("车牌号", inboundOrder.getCarNumber());
            map.put("到货时间", inboundOrder.getArriveTime());
            map.put("到货回传时间", inboundOrder.getArriveBackTime());
            map.put("理货人", inboundOrder.getTallyBy());
            map.put("理货开始时间", inboundOrder.getTallyStartTime());
            map.put("理货开始回传时间", inboundOrder.getTallyStartBackTime());
            map.put("理货结束时间", inboundOrder.getTallyEndTime());
            map.put("理货结束回传时间", inboundOrder.getTallyEndBackTime());
            map.put("收货人", inboundOrder.getTakeBy());
            map.put("收货完成时间", inboundOrder.getTakeTime());
            map.put("收货完成回传时间", inboundOrder.getTakeBackTime());
            map.put("取消时间", inboundOrder.getCancelTime());
            map.put("创建人", inboundOrder.getCreateBy());
            map.put("创建时间", inboundOrder.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void doExportDetails(List<InboundOrderDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (InboundOrderDto inboundOrder : all) {
            List<InboundOrderDetails> inboundOrderDetails = inboundOrderDetailsService.queryByOrderId(inboundOrder.getId());
            for (InboundOrderDetails details : inboundOrderDetails) {
                BaseSku baseSku = baseSkuService.queryByGoodsNo(details.getGoodsNo());
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("客户ID", customerInfoService.queryById(inboundOrder.getCustomersId()).getCustNickName());
                map.put("店铺ID", shopInfoService.queryById(inboundOrder.getShopId()).getName());
                map.put("入库单号", inboundOrder.getOrderNo());
                map.put("外部单号", inboundOrder.getOutNo());
                map.put("WMS单号", inboundOrder.getWmsNo());
                map.put("单据类型", inboundOrder.getOrderType());
                map.put("状态", inboundOrder.getStatus());
                map.put("外部货号", baseSku.getOuterGoodsNo());
                map.put("海关货号", baseSku.getGoodsNo());
                map.put("预期数量", details.getExpectNum());
                map.put("收货数量", details.getTakeNum());
                list.add(map);
            }

        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public InboundOrder queryByOrderNo(String orderNo) {
        return inboundOrderRepository.findByOrderNo(orderNo);
    }

    @Override
    public InboundOrder queryByIdWithDetails(Long id) {
        InboundOrder order = inboundOrderRepository.findById(id).orElseGet(InboundOrder::new);
        List<InboundOrderDetails> details = inboundOrderDetailsService.queryByOrderId(id);
        order.setDetails(details);
        return order;
    }

    @Override
    public InboundOrder queryTallyById(Long id) {
        InboundOrder order = queryByIdWithDetails(id);
        InboundTally tally = inboundTallyService.queryByOrderNo(order.getOrderNo());
        order.setTally(tally);
        List<InboundOrderDetails> details = order.getDetails();
        for (InboundOrderDetails item : details) {
            List<InboundTallyDetails> tallyDetails = inboundTallyDetailsService.queryByTallyIdAndGoodsId(tally.getId(), item.getGoodsId());
            if (order.getStatus().intValue() <= InBoundStatusEnum.STATUS_635.getCode().intValue()) {
                Integer takeNum = 0;
                Integer normalNum = 0;
                Integer damagedNum = 0;
                for (InboundTallyDetails inboundTallyDetails : tallyDetails) {
                    takeNum = takeNum + inboundTallyDetails.getTallyNum();
                    if (inboundTallyDetails.getGoodsQuality().intValue() == 1) {
                        normalNum = normalNum + inboundTallyDetails.getTallyNum();
                    } else {
                        damagedNum = damagedNum + inboundTallyDetails.getTallyNum();
                    }
                }
                item.setTakeNum(takeNum);
                item.setNormalNum(normalNum);
                item.setDamagedNum(damagedNum);
            }
            item.setTallyDetails(tallyDetails);
        }
        return order;
    }

    @Override
    public void refreshStatus() {
        InboundOrderQueryCriteria criteria = new InboundOrderQueryCriteria();
        List<Integer> status = new ArrayList<>();
        status.add(InBoundStatusEnum.STATUS_615.getCode());
        status.add(InBoundStatusEnum.STATUS_620.getCode());
        status.add(InBoundStatusEnum.STATUS_625.getCode());
        status.add(InBoundStatusEnum.STATUS_630.getCode());
        status.add(InBoundStatusEnum.STATUS_635.getCode());
        status.add(InBoundStatusEnum.STATUS_640.getCode());
        status.add(InBoundStatusEnum.STATUS_645.getCode());
        criteria.setStatus(status);
        List<InboundOrder> orders = inboundOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        if (CollectionUtils.isNotEmpty(orders)) {
            for (InboundOrder order : orders) {
                if (StringUtil.isBlank(order.getOnlineSrc()))
                    refreshStatus(order.getId());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshStatus(Long id) {
        InboundOrder order = queryByIdWithDetails(id);
        JSONObject wmsOrder = wmsSupport.queryAsn(order.getOrderNo());
        if (wmsOrder == null||wmsOrder.size()==0)
            throw new BadRequestException("获取WMS状态失败:" + order.getOrderNo());
        if (StringUtil.isEmpty(order.getWmsNo())) {
            order.setWmsNo(wmsOrder.getJSONObject("header").getStr("asnno"));
        }
        if (StringUtil.equals(wmsOrder.getJSONObject("header").getStr("asnstatus"), "99")) {
            // 已收货完成
            order.setStatus(InBoundStatusEnum.STATUS_655.getCode());
            order.setTakeTime(new Timestamp(System.currentTimeMillis()));
            //order.setTakeBackTime(new Timestamp(System.currentTimeMillis()));
            order.setTakeBy("ADMIN");

            JSONArray wmsDetails = wmsOrder.getJSONArray("details");
            if (wmsDetails == null)
                throw new BadRequestException("获取WMS状态失败:" + order.getOrderNo());
            Integer totalTakeNum = 0;
            List<InboundOrderDetails> details = order.getDetails();
            for (int i = 0; i < wmsDetails.size(); i++) {
                for (InboundOrderDetails orderDetails : details) {
                    if (StringUtil.equals(wmsDetails.getJSONObject(i).getStr("sku"),
                            orderDetails.getGoodsNo())) {
                        Integer takeNum = wmsDetails.getJSONObject(i).getInt("receivedqtyEach");
                        orderDetails.setTakeNum(takeNum);
                        totalTakeNum = totalTakeNum + takeNum;
                    }
                }
            }
            order.setGroundingTotalNum(totalTakeNum);
            update(order);
            inboundOrderDetailsService.saveBatch(details);

            InboundOrderLog log = new InboundOrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    InBoundStatusEnum.STATUS_655.getCode().toString(),
                    "",
                    "",
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription(),
                    "ADMIN"
            );
            inboundOrderLogService.create(log);

            if (StringUtil.equals(order.getIsOnline(), "1")
                    && StringUtil.equals(order.getOnlineSrc(), "KJG")) {
                // 回传跨境购收货完后
                kjgService.confirmInSucc(order);
            }

        } else if (StringUtil.equals(wmsOrder.getJSONObject("header").getStr("asnstatus"), "90")) {
            // 已取消
            order.setStatus(InBoundStatusEnum.STATUS_888.getCode());
            update(order);
            InboundOrderLog log = new InboundOrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    InBoundStatusEnum.STATUS_888.getCode().toString(),
                    "",
                    "",
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription(),
                    "ADMIN"
            );
            inboundOrderLogService.create(log);
        } else {
            update(order);
        }

    }

    @Override
    public List<Map<String, Object>> uploadSku(List<Map<String, Object>> list) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> map : list) {
            int sNo = Integer.valueOf(map.get("序号").toString());
            String goodsNo = map.get("货号*") != null ? map.get("货号*").toString() : null;
            String num = map.get("数量*") != null ? map.get("数量*").toString() : null;
            if (StringUtil.isBlank(goodsNo))
                throw new BadRequestException("第" + sNo + "行，货号不能为空");
            if (StringUtil.isBlank(num))
                throw new BadRequestException("第" + sNo + "行，数量不能为空");
            Map<String, Object> resultItem = new HashMap<>();
            boolean flag = true;//没有需要合并的入库详情
            if (CollectionUtil.isNotEmpty(result)) {
                for (Map<String, Object> item : result) {
                    if (StringUtil.equalsIgnoreCase(item.get("goodsNo") + "", goodsNo)) {
                        int itemNum = Integer.parseInt(item.get("num") + "");
                        item.put("num", itemNum + Integer.parseInt(num));
                        flag = false;
                        break;
                    }
                }
            }
            if (flag) {
                resultItem.put("goodsNo", goodsNo);
                resultItem.put("num", num);
                result.add(resultItem);
            }
        }
        return result;
    }

    /**
     * 到货登记
     *
     * @param orderId
     * @param carNumber
     * @param arriveTime
     */
    @Override
    public void arriveSign(Long orderId, String carNumber, Timestamp arriveTime) {
        InboundOrder order = inboundOrderMapper.toEntity(findById(orderId));
        if (order.getStatus().intValue() != InBoundStatusEnum.STATUS_615.getCode().intValue())
            throw new BadRequestException("当前状态不可到货登记");
        order.setCarNumber(carNumber);
        order.setArriveTime(arriveTime);
        order.setArriveBy(SecurityUtils.getCurrentUsername());
        order.setStatus(InBoundStatusEnum.STATUS_620.getCode());
        update(order);
        InboundOrderLog log = new InboundOrderLog(
                order.getId(),
                order.getOrderNo(),
                InBoundStatusEnum.STATUS_620.getCode().toString(),
                "",
                "",
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription(),
                SecurityUtils.getCurrentUsername()
        );
        inboundOrderLogService.create(log);

        // 回传到货
        ShopInfo shopInfo = shopInfoService.findById(order.getShopId());
        if (StringUtil.equals(shopInfo.getPlatformCode(), "DY")) {
            //抖音到货回传
            dyConfirmArrive(orderId);
        } else {
            order.setArriveBackTime(new Timestamp(System.currentTimeMillis()));
            order.setStatus(InBoundStatusEnum.STATUS_625.getCode());
            update(order);
            InboundOrderLog log1 = new InboundOrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    InBoundStatusEnum.STATUS_625.getCode().toString(),
                    "",
                    "",
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription(),
                    SecurityUtils.getCurrentUsername()
            );
            inboundOrderLogService.create(log1);
        }
    }

    /**
     * 审核通过
     *
     * @param orderId
     * @throws Exception
     */
    @Override
    public void verifyPass(Long orderId) throws Exception {
        InboundOrder order = queryByIdWithDetails(orderId);
        if (order.getStatus().intValue() != InBoundStatusEnum.STATUS_600.getCode().intValue())
            throw new BadRequestException("当前状态不可审核通过");
        try {
            wmsSupport.pushInboundOrder(order);// 推送WMS
            order.setStatus(InBoundStatusEnum.STATUS_615.getCode());
            update(order);
            InboundOrderLog log = new InboundOrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    InBoundStatusEnum.STATUS_615.getCode().toString(),
                    "",
                    "",
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription(),
                    SecurityUtils.getCurrentUsername()
            );
            inboundOrderLogService.create(log);
        } catch (Exception e) {
            InboundOrderLog log = new InboundOrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    InBoundStatusEnum.STATUS_615.getCode().toString(),
                    "",
                    "",
                    BooleanEnum.FAIL.getCode(),
                    e.getMessage(),
                    SecurityUtils.getCurrentUsername()
            );
            inboundOrderLogService.create(log);
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void startTally(Long orderId) {
        InboundOrder order = inboundOrderMapper.toEntity(findById(orderId));
        if (order.getStatus().intValue() < InBoundStatusEnum.STATUS_625.getCode().intValue())
            throw new BadRequestException("当前状态不可开始理货");
        // 先查询理货单是否生成
        InboundTally tally = inboundTallyService.queryByOrderNo(order.getOrderNo());
        if (tally != null)
            return;
        //新增入库理货单
        tally = new InboundTally();
        tally.setOrderId(order.getId());
        tally.setTallyNo(genTallyNo());
        tally.setOrderNo(order.getOrderNo());
        tally.setStatus(InBoundTallyStatusEnum.STATUS_700.getCode());
        tally.setExpectSkuNum(order.getExpectSkuNum());
        tally.setExpectTotalNum(order.getExpectTotalNum());
        inboundTallyService.create(tally);

        // 更改主单状态
        order.setStatus(InBoundStatusEnum.STATUS_630.getCode());
        order.setTallyStartTime(new Timestamp(System.currentTimeMillis()));
        order.setTallyBy(SecurityUtils.getCurrentUsername());
        update(order);
    }

    public synchronized String genOrderNo() {
        int tryCount = 0;
        String qz = "RK";
        String time = String.valueOf(System.currentTimeMillis());
        String result = qz + time;
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            result += random.nextInt(10);
        }
        InboundOrder order = queryByOrderNo(result);
        if (order != null && tryCount < 4) {
            genOrderNo();
            tryCount++;
        }
        return result;
    }

    public synchronized String genTallyNo() {
        String qz = "TL";
        String time = DateUtils.format(new Date(), DatePattern.PURE_DATE_FORMAT);
        String result = qz + time;
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            result += random.nextInt(10);
        }
        InboundTally tally = inboundTallyService.queryByTallyNo(result);
        if (tally != null) {
            genTallyNo();
        }
        return result;
    }

    @Override
    public void dyConfirmOrder(Long id) {
        InboundOrder inboundOrder = inboundOrderRepository.findById(id).orElse(null);
        if (inboundOrder == null)
            throw new BadRequestException("入库单不存在");
        if (StringUtil.equals(inboundOrder.getOnlineSrc(), "DY"))
            confirmOrder(inboundOrder);
        else if (StringUtil.equals(inboundOrder.getOnlineSrc(), "RuoYuChen"))
            rycConfirmOrder(inboundOrder);

    }

    private void confirmOrder(InboundOrder inboundOrder) {
        if (inboundOrder.getStatus()>=InBoundStatusEnum.STATUS_615.getCode()&&inboundOrder.getConfirmTime()!=null)
            throw new BadRequestException("当前入库单状态为"+InBoundStatusEnum.getDesc(inboundOrder.getStatus()));
        List<InboundOrderDetails> details = inboundOrderDetailsService.queryByOrderId(inboundOrder.getId());
        inboundOrder.setDetails(details);
        String name;
        try {
            name = SecurityUtils.getCurrentUsername();
        } catch (Exception e) {
            name = "System";
        }
        InboundOrderLog log = new InboundOrderLog(
                inboundOrder.getId(),
                inboundOrder.getOrderNo(),
                InBoundStatusEnum.STATUS_615.getCode().toString(),
                "",
                "",
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription(),
                name
        );
        try {
            if (StringUtil.equals(inboundOrder.getOnlineSrc(), "DY"))
                douyinService.dyConfirmOrder(inboundOrder, log);
        } catch (Exception e) {
            e.printStackTrace();
            inboundOrderLogService.create(log);
            throw new BadRequestException(e.getMessage() == null ? "null" : e.getMessage());
        }
        inboundOrder.setConfirmTime(new Timestamp(System.currentTimeMillis()));
        inboundOrder.setConfirmBy(name);
        update(inboundOrder);
        inboundOrderLogService.create(log);
    }

    @Override
    public void dyConfirmArrive(Long id) {
        InboundOrder inboundOrder = inboundOrderRepository.findById(id).orElse(null);
        if (inboundOrder == null)
            throw new BadRequestException("入库单不存在");
        confirmArrive(inboundOrder);
    }

    private void confirmArrive(InboundOrder inboundOrder) {
        if (inboundOrder.getStatus()>=InBoundStatusEnum.STATUS_625.getCode())
            throw new BadRequestException("当前入库单状态为"+InBoundStatusEnum.getDesc(inboundOrder.getStatus()));
        List<InboundOrderDetails> details = inboundOrderDetailsService.queryByOrderId(inboundOrder.getId());
        inboundOrder.setDetails(details);
        String name;
        try {
            name = SecurityUtils.getCurrentUsername();
        } catch (Exception e) {
            name = "System";
        }
        InboundOrderLog log = new InboundOrderLog(
                inboundOrder.getId(),
                inboundOrder.getOrderNo(),
                InBoundStatusEnum.STATUS_625.getCode().toString(),
                "",
                "",
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription(),
                name
        );
        try {
            if (StringUtil.equals("DY", inboundOrder.getOnlineSrc()))
                douyinService.dyConfirmArrive(inboundOrder, log);
        } catch (Exception e) {
            e.printStackTrace();
            inboundOrderLogService.create(log);
            throw new BadRequestException(e.getMessage() == null ? "null" : e.getMessage());
        }
        inboundOrder.setArriveBackTime(new Timestamp(System.currentTimeMillis()));
        inboundOrder.setArriveBy(name);
        inboundOrder.setArriveTime(inboundOrder.getArriveBackTime());
        inboundOrder.setStatus(InBoundStatusEnum.STATUS_625.getCode());
        update(inboundOrder);
        inboundOrderLogService.create(log);
    }

    @Override
    public void dyConfirmStockTally(Long id) {
        InboundOrder inboundOrder = inboundOrderRepository.findById(id).orElse(null);
        if (inboundOrder == null)
            throw new BadRequestException("入库单不存在");
        confirmStockTally(inboundOrder);
    }

    private void confirmStockTally(InboundOrder inboundOrder) {
        if (!(inboundOrder.getStatus().equals(InBoundStatusEnum.STATUS_625.getCode())||inboundOrder.getStatus().equals(InBoundStatusEnum.STATUS_648.getCode())))
            throw new BadRequestException("当前入库单状态为"+InBoundStatusEnum.getDesc(inboundOrder.getStatus()));
        List<InboundOrderDetails> details = inboundOrderDetailsService.queryByOrderId(inboundOrder.getId());
        inboundOrder.setDetails(details);
        String name;
        try {
            name = SecurityUtils.getCurrentUsername();
        } catch (Exception e) {
            name = "System";
        }
        InboundOrderLog log = new InboundOrderLog(
                inboundOrder.getId(),
                inboundOrder.getOrderNo(),
                InBoundStatusEnum.STATUS_635.getCode().toString(),
                "",
                "",
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription(),
                name
        );
        try {
            if (StringUtil.equals("DY", inboundOrder.getOnlineSrc()))
                douyinService.dyConfirmStockTally(inboundOrder, log);
        } catch (Exception e) {
            e.printStackTrace();
            inboundOrderLogService.create(log);
            throw new BadRequestException(e.getMessage() == null ? "null" : e.getMessage());
        }
        inboundOrder.setTallyStartBackTime(new Timestamp(System.currentTimeMillis()));
        inboundOrder.setTallyBy(name);
        inboundOrder.setTallyStartTime(inboundOrder.getTallyStartTime());
        inboundOrder.setStatus(InBoundStatusEnum.STATUS_635.getCode());
        update(inboundOrder);
        inboundOrderLogService.create(log);
    }

    @Override
    public void dyConfirmStockedTally(Long id) {
        InboundOrder inboundOrder = inboundOrderRepository.findById(id).orElse(null);
        if (inboundOrder == null)
            throw new BadRequestException("入库单不存在");
        confirmStockedTally(inboundOrder);
        if (StringUtil.equals(inboundOrder.getIsFourPl(), "1")) {
            //4pl单再次回传理货完成后待审核
            inboundOrder.setStatus(InBoundStatusEnum.STATUS_646.getCode());
        }
    }

    private void confirmStockedTally(InboundOrder inboundOrder) {
        if (inboundOrder.getStatus()>=InBoundStatusEnum.STATUS_645.getCode())
            throw new BadRequestException("当前入库单状态为"+InBoundStatusEnum.getDesc(inboundOrder.getStatus()));
        List<InboundOrderDetails> details = inboundOrderDetailsService.queryByOrderId(inboundOrder.getId());
        inboundOrder.setDetails(details);
        String name;
        try {
            name = SecurityUtils.getCurrentUsername();
        } catch (Exception e) {
            name = "System";
        }
        InboundOrderLog log = new InboundOrderLog(
                inboundOrder.getId(),
                inboundOrder.getOrderNo(),
                InBoundStatusEnum.STATUS_645.getCode().toString(),
                "",
                "",
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription(),
                name
        );
        try {
            if (StringUtil.equals(inboundOrder.getOnlineSrc(), "DY"))
                douyinService.dyConfirmStockedTally(inboundOrder, log);
            else if (StringUtil.equals(inboundOrder.getOnlineSrc(), "RuoYuChen") && StringUtil.equals(inboundOrder.getDefault01(), "CGRK")) {
                ruoYuChenService.rycConfirmStockedTally(inboundOrder, log);
            }
        } catch (Exception e) {
            e.printStackTrace();
            inboundOrderLogService.create(log);
            throw new BadRequestException(e.getMessage() == null ? "null" : e.getMessage());
        }
        inboundOrder.setTallyEndBackTime(new Timestamp(System.currentTimeMillis()));
        inboundOrder.setTallyEndTime(inboundOrder.getTallyEndBackTime());
        if (StringUtil.equals(inboundOrder.getIsFourPl(), "1")) {
            //4pl单需要等待理货审核
            inboundOrder.setStatus(InBoundStatusEnum.STATUS_646.getCode());
        } else {
            //非4pl单是理货完成回传
            inboundOrder.setStatus(InBoundStatusEnum.STATUS_645.getCode());
        }
        update(inboundOrder);
        inboundOrderLogService.create(log);
    }

    @Override
    public void dyConfirmUp(Long id) {
        InboundOrder inboundOrder = inboundOrderRepository.findById(id).orElse(null);
        if (inboundOrder == null)
            throw new BadRequestException("入库单不存在");
        if (!inboundOrder.getStatus().equals(InBoundStatusEnum.STATUS_647.getCode()) && StringUtil.equals(inboundOrder.getIsFourPl(), "1")) {
            //4pl单需要等待理货审核通过才能回传上架
            throw new BadRequestException("非理货审核通过不可回传上架完成");
        }
        confirmUp(inboundOrder);
    }

    private void confirmUp(InboundOrder inboundOrder) {
        if (inboundOrder.getStatus()>=InBoundStatusEnum.STATUS_655.getCode())
            throw new BadRequestException("当前入库单状态为"+InBoundStatusEnum.getDesc(inboundOrder.getStatus()));
        List<InboundOrderDetails> details = inboundOrderDetailsService.queryByOrderId(inboundOrder.getId());
        inboundOrder.setDetails(details);
        String name;
        boolean isTask=false;
        try {
            name = SecurityUtils.getCurrentUsername();
        } catch (Exception e) {
            name = "System";
            isTask=true;
        }
        InboundOrderLog log = new InboundOrderLog(
                inboundOrder.getId(),
                inboundOrder.getOrderNo(),
                InBoundStatusEnum.STATUS_655.getCode().toString(),
                "",
                "",
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription(),
                name
        );
        try {
            if (StringUtil.equals(inboundOrder.getOnlineSrc(), "DY")) {
                if (StringUtil.equals(inboundOrder.getIsFourPl(), "1")) {
                    //抖音4PL
                    if (inboundOrder.getStatus() == 647)
                        douyinService.dyConfirmUp(inboundOrder, log);
                    else{
                        if (isTask)
                            //定时器的不抛异常
                            return;
                        else
                            throw new BadRequestException("理货审核未通过");
                    }
                }
                else
                    douyinService.dyConfirmUp(inboundOrder, log);
            }
            else if (StringUtil.equals(inboundOrder.getOnlineSrc(), "RuoYuChen"))
                ruoYuChenService.rycConfirmUp(inboundOrder, log);
            else if (StringUtil.equals(inboundOrder.getOnlineSrc(), "JackYun"))
                jackYunService.confirmUp(inboundOrder, log);
        } catch (Exception e) {
            e.printStackTrace();
            inboundOrderLogService.create(log);
            if (!("1".equals(inboundOrder.getIsFourPl()))) {
                inboundOrder.setStatus(645);
            }
            inboundOrder.setFreezeReason(e.getMessage());
            throw new BadRequestException(e.getMessage() == null ? "null" : e.getMessage());
        }
        if (StringUtil.equals(log.getSuccess(), "1")) {
            inboundOrder.setTakeBy(name);
            inboundOrder.setTakeTime(inboundOrder.getTakeBackTime());
            inboundOrder.setTakeBackTime(new Timestamp(System.currentTimeMillis()));
            inboundOrder.setFreezeReason("-");
            inboundOrder.setStatus(InBoundStatusEnum.STATUS_655.getCode());
        }
        update(inboundOrder);
        inboundOrderLogService.create(log);
    }

    @Override
    public void dyConfirmCancel(Long id) {
        InboundOrder inboundOrder = inboundOrderRepository.findById(id).orElse(null);
        if (inboundOrder == null)
            throw new BadRequestException("入库单不存在");
        List<InboundOrderDetails> details = inboundOrderDetailsService.queryByOrderId(id);
        inboundOrder.setDetails(details);
        String name;
        try {
            name=SecurityUtils.getCurrentUsername();
        }catch (Exception e){
            name="System";
        }
        InboundOrderLog log = new InboundOrderLog(
                inboundOrder.getId(),
                inboundOrder.getOrderNo(),
                InBoundStatusEnum.STATUS_888.getCode().toString(),
                "",
                "",
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription(),
                name
        );
        try {
            if (StringUtil.equals("DY",inboundOrder.getOnlineSrc()))
                douyinService.dyConfirmCancel(inboundOrder, log);
        } catch (Exception e) {
            e.printStackTrace();
            inboundOrderLogService.create(log);
            throw new BadRequestException(e.getMessage() == null ? "null" : e.getMessage());
        }
        inboundOrder.setStatus(InBoundStatusEnum.STATUS_888.getCode());
        inboundOrder.setCancelTime(new Timestamp(System.currentTimeMillis()));
        update(inboundOrder);
        inboundOrderLogService.create(log);
    }

    @Override
    public InboundOrder queryByOutNo(String outNo) {
        InboundOrder inboundOrder = new InboundOrder();
        inboundOrder.setOutNo(outNo);
        List<InboundOrder> list = inboundOrderRepository.findAll(Example.of(inboundOrder));
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    /**
     * 监听富勒入库单状态
     */
    @Override
    public void listenFluxInStatus() {
        // TODO: 2021/12/19 监听富勒入库单状态并根据对应的状态去回传相应渠道
        List<InboundOrder> list = inboundOrderRepository.findAll1();
        for (InboundOrder inboundOrder : list) {
            try {
                DocAsnHeader asnOrder;
                JSONObject wmsOrder = wmsSupport.queryAsn(inboundOrder.getOrderNo());
                asnOrder=wmsOrder.getBean("header",DocAsnHeader.class);
                if (asnOrder == null||asnOrder.getAsnno()==null)
                    continue;
                if (StringUtil.isBlank(inboundOrder.getWmsNo()))
                    inboundOrder.setWmsNo(asnOrder.getAsnno());
                inboundOrder.setWmsStatus(asnOrder.getAsnstatus());
                update(inboundOrder);
                if (StringUtil.equals(asnOrder.getAsnstatus(), "40") || StringUtil.equals(asnOrder.getAsnstatus(), "99")) {
                    //asn收货完成/asn关闭
                    if (StringUtil.equals(inboundOrder.getIsOnline(), "1")) {
                        //抖音入库
                        if (inboundOrder.getConfirmTime() == null) {
                            //回传接单
                            confirmOrder(inboundOrder);
                        } else if (inboundOrder.getConfirmTime() != null && inboundOrder.getArriveBackTime() == null) {
                            //回传到货
                            confirmArrive(inboundOrder);
                        } else if (inboundOrder.getArriveBackTime() != null && inboundOrder.getTallyStartBackTime() == null) {
                            //回传理货开始
                            confirmStockTally(inboundOrder);
                        } else if (inboundOrder.getTallyStartBackTime() != null && inboundOrder.getTallyEndBackTime() == null) {
                            //回传理货结果
                            if (!StringUtil.equals("RuoYuChen",inboundOrder.getOnlineSrc())){
                                inboundOrder.setStockRecordUrl("about:blank");
                                inboundOrder.setStockRecordName("undefined");
                            }
                            confirmStockedTally(inboundOrder);
                        } else if (inboundOrder.getTallyEndBackTime() != null && inboundOrder.getTakeBackTime() == null) {
                            //回传上架
                            confirmUp(inboundOrder);
                        }
                    } else {
                        if (StringUtil.equals(asnOrder.getAsnstatus(), "99")) {
                            inboundOrder.setStatus(InBoundStatusEnum.STATUS_655.getCode());
                            update(inboundOrder);
                        }
                    }
                }
            } catch (Exception e) {
                InboundOrderLog log = new InboundOrderLog(
                        inboundOrder.getId(),
                        inboundOrder.getOrderNo(),
                        inboundOrder.getStatus() + "",
                        "",
                        "",
                        BooleanEnum.FAIL.getCode(),
                        BooleanEnum.FAIL.getDescription(),
                        "Task"
                );
                inboundOrderLogService.create(log);
                e.printStackTrace();
            }
        }
    }

    private void rycConfirmUp(InboundOrder inboundOrder) {
        List<InboundOrderDetails> details = inboundOrderDetailsService.queryByOrderId(inboundOrder.getId());
        inboundOrder.setDetails(details);
        String name;
        try {
            name = SecurityUtils.getCurrentUsername();
        } catch (Exception e) {
            name = "System";
        }
        InboundOrderLog log = new InboundOrderLog(
                inboundOrder.getId(),
                inboundOrder.getOrderNo(),
                InBoundStatusEnum.STATUS_655.getCode().toString(),
                "",
                "",
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription(),
                name
        );
        try {
            ruoYuChenService.rycConfirmUp(inboundOrder, log);
        } catch (Exception e) {
            e.printStackTrace();
            inboundOrderLogService.create(log);
            throw new BadRequestException(e.getMessage() == null ? "null" : e.getMessage());
        }
        inboundOrder.setTakeBackTime(new Timestamp(System.currentTimeMillis()));
        inboundOrder.setTakeBy(name);
        inboundOrder.setTakeTime(inboundOrder.getTakeBackTime());
        if (inboundOrder.getStatus() < InBoundStatusEnum.STATUS_655.getCode())
            inboundOrder.setStatus(InBoundStatusEnum.STATUS_655.getCode());
        update(inboundOrder);
        inboundOrderLogService.create(log);
    }

    private void rycConfirmStockedTally(InboundOrder inboundOrder) {
        List<InboundOrderDetails> details = inboundOrderDetailsService.queryByOrderId(inboundOrder.getId());
        inboundOrder.setDetails(details);
        String name;
        try {
            name = SecurityUtils.getCurrentUsername();
        } catch (Exception e) {
            name = "System";
        }
        InboundOrderLog log = new InboundOrderLog(
                inboundOrder.getId(),
                inboundOrder.getOrderNo(),
                InBoundStatusEnum.STATUS_645.getCode().toString(),
                "",
                "",
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription(),
                name
        );
        if (StringUtil.equals(inboundOrder.getDefault01(), "CGRK")) {
            try {
                ruoYuChenService.rycConfirmStockedTally(inboundOrder, log);
            } catch (Exception e) {
                e.printStackTrace();
                inboundOrderLogService.create(log);
                throw new BadRequestException(e.getMessage() == null ? "null" : e.getMessage());
            }
        }
        inboundOrder.setTallyEndBackTime(new Timestamp(System.currentTimeMillis()));
        inboundOrder.setTallyEndTime(inboundOrder.getTallyEndBackTime());
        inboundOrder.setStatus(InBoundStatusEnum.STATUS_646.getCode());
        update(inboundOrder);
        inboundOrderLogService.create(log);
    }

    private void rycConfirmStockTally(InboundOrder inboundOrder) {
        String name;
        try {
            name = SecurityUtils.getCurrentUsername();
        } catch (Exception e) {
            name = "System";
        }
        InboundOrderLog log = new InboundOrderLog(
                inboundOrder.getId(),
                inboundOrder.getOrderNo(),
                InBoundStatusEnum.STATUS_635.getCode().toString(),
                "",
                "",
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription(),
                name
        );
        inboundOrder.setTallyStartBackTime(new Timestamp(System.currentTimeMillis()));
        inboundOrder.setTallyBy(name);
        inboundOrder.setTallyStartTime(inboundOrder.getTallyStartTime());
        if (inboundOrder.getStatus() < InBoundStatusEnum.STATUS_635.getCode())
            inboundOrder.setStatus(InBoundStatusEnum.STATUS_635.getCode());
        update(inboundOrder);
        inboundOrderLogService.create(log);
    }

    private void rycConfirmArrive(InboundOrder inboundOrder) {
        String name;
        try {
            name = SecurityUtils.getCurrentUsername();
        } catch (Exception e) {
            name = "System";
        }
        InboundOrderLog log = new InboundOrderLog(
                inboundOrder.getId(),
                inboundOrder.getOrderNo(),
                InBoundStatusEnum.STATUS_625.getCode().toString(),
                "",
                "",
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription(),
                name
        );
        inboundOrder.setArriveBackTime(new Timestamp(System.currentTimeMillis()));
        inboundOrder.setArriveBy(name);
        inboundOrder.setArriveTime(inboundOrder.getArriveBackTime());
        if (inboundOrder.getStatus() < InBoundStatusEnum.STATUS_625.getCode())
            inboundOrder.setStatus(InBoundStatusEnum.STATUS_625.getCode());
        update(inboundOrder);
        inboundOrderLogService.create(log);
    }

    private void rycConfirmOrder(InboundOrder inboundOrder) {
        List<InboundOrderDetails> details = inboundOrderDetailsService.queryByOrderId(inboundOrder.getId());
        inboundOrder.setDetails(details);
        String name;
        try {
            name = SecurityUtils.getCurrentUsername();
        } catch (Exception e) {
            name = "System";
        }
        InboundOrderLog log = new InboundOrderLog(
                inboundOrder.getId(),
                inboundOrder.getOrderNo(),
                InBoundStatusEnum.STATUS_615.getCode().toString(),
                "",
                "",
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription(),
                name
        );
        try {
            ruoYuChenService.rycConfirmOrder(inboundOrder, log);
        } catch (Exception e) {
            e.printStackTrace();
            inboundOrderLogService.create(log);
            throw new BadRequestException(e.getMessage() == null ? "null" : e.getMessage());
        }
        if (StringUtil.equals(log.getSuccess(), "1")) {
            inboundOrder.setConfirmTime(new Timestamp(System.currentTimeMillis()));
            inboundOrder.setConfirmBy(name);
        }
        update(inboundOrder);
        inboundOrderLogService.create(log);
    }

    @Override
    public void tallyReview(TallyOrderReview tallyOrderReview) {
        InboundOrder inboundOrder = queryByOutNo(tallyOrderReview.getSourceOrderNo());
        if (tallyOrderReview.getRes() == 1) {
            //审核通过
            inboundOrder.setStatus(InBoundStatusEnum.STATUS_647.getCode());
            inboundOrder.setFreezeReason("-");
            update(inboundOrder);
            inboundOrderLogService.create(new InboundOrderLog(
                    inboundOrder.getId(),
                    inboundOrder.getOrderNo(),
                    InBoundStatusEnum.STATUS_647.getCode().toString(),
                    tallyOrderReview.toString(),
                    "",
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription(),
                    "SYSTEM_DY"
            ));
            try {
                confirmUp(inboundOrder);//回传上架
            } catch (Exception e) {
                e.printStackTrace();
                update(inboundOrder);
            }
        } else {
            //审核不过
            inboundOrder.setStatus(InBoundStatusEnum.STATUS_648.getCode());
            inboundOrder.setFreezeReason(tallyOrderReview.getRemark());
            update(inboundOrder);
            inboundOrderLogService.create(new InboundOrderLog(
                    inboundOrder.getId(),
                    inboundOrder.getOrderNo(),
                    InBoundStatusEnum.STATUS_648.getCode().toString(),
                    tallyOrderReview.toString(),
                    "",
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription(),
                    "SYSTEM_DY"
            ));
        }
    }

    @Override
    public void cancel(Long id) {
        InboundOrder inboundOrder = queryById(id);
        if (inboundOrder.getStatus().intValue() >= 620)
            throw new BadRequestException("该状态不能取消");
        inboundOrder.setStatus(InBoundStatusEnum.STATUS_888.getCode());
        inboundOrder.setCancelTime(new Timestamp(System.currentTimeMillis()));
        update(inboundOrder);
        inboundOrderLogService.create(new InboundOrderLog(
                inboundOrder.getId(),
                inboundOrder.getOrderNo(),
                InBoundStatusEnum.STATUS_888.getCode().toString(),
                "",
                "",
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription(),
                "SYSTEM"
        ));

    }

    @Override
    public void freezeOrder(Long id, String freezeReason) {
        InboundOrder inboundOrder = queryById(id);
        if (inboundOrder == null)
            throw new BadRequestException("入库单id不存在");
        freezeOrder(inboundOrder, freezeReason);
    }

    @Override
    public void freezeOrder(InboundOrder order, String freezeReason) {
        order.setStatus(InBoundStatusEnum.STATUS_999.getCode());
        order.setFreezeReason(freezeReason);
        update(order);
        inboundOrderLogService.create(new InboundOrderLog(
                order.getId(),
                order.getOrderNo(),
                InBoundStatusEnum.STATUS_999.getCode().toString(),
                "",
                "",
                BooleanEnum.SUCCESS.getCode(),
                freezeReason,
                "SYSTEM"
        ));
    }


}
