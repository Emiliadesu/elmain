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
import cn.hutool.json.JSONUtil;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.mq.DomesticOrderProducer;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.ShopInfoDto;
import me.zhengjie.support.WmsSupport;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.DomesticOrderRepository;
import me.zhengjie.service.dto.DomesticOrderDto;
import me.zhengjie.service.dto.DomesticOrderQueryCriteria;
import me.zhengjie.service.mapstruct.DomesticOrderMapper;
import me.zhengjie.utils.constant.MsgType;
import me.zhengjie.utils.enums.BooleanEnum;
import me.zhengjie.utils.enums.CBOrderStatusEnum;
import me.zhengjie.utils.enums.DomesticOrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author luob
* @date 2022-04-11
**/
@Service
@RequiredArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DomesticOrderServiceImpl implements DomesticOrderService {

    private final DomesticOrderRepository domesticOrderRepository;
    private final DomesticOrderMapper domesticOrderMapper;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private DomesticOrderrDetailsService domesticOrderrDetailsService;

    @Autowired
    private DomesticOrderLogService domesticOrderLogService;

    @Autowired
    private DomesticOrderProducer domesticOrderProducer;

    @Autowired
    private LogisticsInfoService logisticsInfoService;

    @Autowired
    private CustomerInfoService customerInfoService;

    @Autowired
    private WmsSupport wmsSupport;

    @Autowired
    private ClearCompanyInfoService clearCompanyInfoService;

    // 接单回传
    @Override
    public void confirmOrder(Long id) {
        DomesticOrder order = queryById(id);
        if (order.getStatus().intValue() != DomesticOrderStatusEnum.STATUS_200.getCode())
            throw new BadRequestException("当前状态不能回传接单");
        order.setStatus(DomesticOrderStatusEnum.STATUS_215.getCode());
        order.setReceivedBackTime(new Timestamp(System.currentTimeMillis()));
        update(order);
        DomesticOrderLog orderLog = new DomesticOrderLog(
                order.getId(),
                order.getOrderNo(),
                String.valueOf(order.getStatus()),
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription()
        );
        domesticOrderLogService.create(orderLog);

        domesticOrderProducer.delaySend(
                MsgType.DM_ORDER_220,
                String.valueOf(order.getId()),
                order.getOrderNo(),
                1000
        );
    }

    // 审核通过
    @Override
    public void checkPass(Long id) throws Exception {
        DomesticOrder order = queryById(id);
        if (order.getStatus().intValue() != DomesticOrderStatusEnum.STATUS_215.getCode())
            throw new BadRequestException("当前状态不能审核通过");
        if (StringUtils.equals("2C", order.getOrderType())) {
            // 获取运单
            try {
                logisticsInfoService.getDMLogistics(order);
            }catch (Exception e) {
                order.setFreezeReason(e.getMessage());
                if (StringUtils.contains(e.getMessage(), "停发")) {
                    order.setStatus(DomesticOrderStatusEnum.STATUS_999.getCode());
                    update(order);
                    DomesticOrderLog orderLog = new DomesticOrderLog(
                            order.getId(),
                            order.getOrderNo(),
                            String.valueOf(order.getStatus()),
                            BooleanEnum.SUCCESS.getCode(),
                            BooleanEnum.SUCCESS.getDescription()
                    );
                    domesticOrderLogService.create(orderLog);
                }
                throw e;
            }

        }
        order.setCheckPackTime(new Timestamp(System.currentTimeMillis()));
        order.setStatus(DomesticOrderStatusEnum.STATUS_220.getCode());
        update(order);
        DomesticOrderLog orderLog = new DomesticOrderLog(
                order.getId(),
                order.getOrderNo(),
                String.valueOf(order.getStatus()),
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription()
        );
        domesticOrderLogService.create(orderLog);

        domesticOrderProducer.delaySend(
                MsgType.DM_ORDER_235,
                String.valueOf(order.getId()),
                order.getOrderNo(),
                1000
        );
    }

    // 预处理
    @Override
    public void preHandle(Long id) throws Exception {
        DomesticOrder order = queryByIdWithDetails(id);
        if (order.getStatus().intValue() != DomesticOrderStatusEnum.STATUS_220.getCode())
            throw new BadRequestException("当前状态不能预处理完成");
        wmsSupport.pushDmOrder(order);

        order.setPreHandelTime(new Timestamp(System.currentTimeMillis()));
        order.setStatus(DomesticOrderStatusEnum.STATUS_235.getCode());
        update(order);
        DomesticOrderLog orderLog = new DomesticOrderLog(
                order.getId(),
                order.getOrderNo(),
                String.valueOf(order.getStatus()),
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription()
        );
        domesticOrderLogService.create(orderLog);
    }

    // 出库
    @Override
    public void deliver(String weight, String mailNo) throws Exception {
        DomesticOrder order = queryByLogisticsNo(mailNo);
        if (order == null)
            throw new BadRequestException("运单号不存在本系统");
        if (order.getStatus().intValue() == DomesticOrderStatusEnum.STATUS_240.getCode())
            throw new BadRequestException("订单已称重，请确认是否重复扫描发货");
        if (order.getStatus().intValue() == DomesticOrderStatusEnum.STATUS_240.getCode())
            throw new BadRequestException("订单已发货，请确认是否重复扫描发货");
        if (order.getStatus().intValue() == DomesticOrderStatusEnum.STATUS_888.getCode())
            throw new BadRequestException("订单已取消");
        if (order.getStatus().intValue() != DomesticOrderStatusEnum.STATUS_235.getCode())
            throw new BadRequestException("当前状态不能发货");
        if (StringUtils.isBlank(order.getWmsStatus()))
            refreshWmsStatus(order.getId());
        wmsSupport.deliver(order.getWmsNo());
        order.setWeighingTime(new Timestamp(System.currentTimeMillis()));
        order.setStatus(DomesticOrderStatusEnum.STATUS_240.getCode());
        update(order);
        DomesticOrderLog orderLog = new DomesticOrderLog(
                order.getId(),
                order.getOrderNo(),
                String.valueOf(order.getStatus()),
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription()
        );
        domesticOrderLogService.create(orderLog);

        domesticOrderProducer.send(
                MsgType.DM_ORDER_245,
                String.valueOf(order.getId()),
                order.getOrderNo()
        );
    }

    @Override
    public void confirmDeliver(Long id) {
        DomesticOrder order = queryByIdWithDetails(id);
        if (order.getStatus().intValue() != DomesticOrderStatusEnum.STATUS_240.getCode())
            throw new BadRequestException("当前状态不能回传出库");
        order.setDeliverTime(new Timestamp(System.currentTimeMillis()));
        order.setStatus(DomesticOrderStatusEnum.STATUS_245.getCode());
        update(order);
        DomesticOrderLog orderLog = new DomesticOrderLog(
                order.getId(),
                order.getOrderNo(),
                String.valueOf(order.getStatus()),
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription()
        );
        domesticOrderLogService.create(orderLog);

    }

    // 取消订单
    @Override
    public void cancel(Long id) {
        DomesticOrder order = queryById(id);
        if (order.getStatus().intValue() >= DomesticOrderStatusEnum.STATUS_240.getCode())
            throw new BadRequestException("当前状态不能取消订单");
        String wmsResult = "";
        if (order.getStatus().intValue() == DomesticOrderStatusEnum.STATUS_235.getCode()) {
            // 已推送富勒
            // 发起富勒订单取消
            ShopInfoDto shopInfoDto = shopInfoService.queryById(order.getShopId());
            ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.findById(shopInfoDto.getServiceId());
            wmsResult = wmsSupport.cancelOrder(order.getOrderNo(), clearCompanyInfo.getCustomsCode());
        }
        order.setStatus(DomesticOrderStatusEnum.STATUS_888.getCode());
        order.setCancelTime(new Timestamp(System.currentTimeMillis()));
        update(order);
        DomesticOrderLog orderLog = new DomesticOrderLog(
                order.getId(),
                order.getOrderNo(),
                String.valueOf(order.getStatus()),
                BooleanEnum.SUCCESS.getCode(),
                "订单退款" + wmsResult
        );
        domesticOrderLogService.create(orderLog);
    }

    @Override
    public void refreshWmsStatus(Long orderId) {
        DomesticOrder order = queryById(orderId);
        JSONObject wmsOrder = wmsSupport.querySo(order.getOrderNo(), order.getOrderNo());
        if (wmsOrder == null)
            throw new BadRequestException("获取WMS状态失败:" + order.getOrderNo());
        order.setWmsStatus(wmsOrder.getJSONObject("header").getStr("sostatus"));
        String soNo = wmsOrder.getJSONObject("header").getStr("orderno");
        if (!StringUtils.equals(order.getWmsNo(), soNo)) {
            order.setWmsNo(soNo);
        }
        update(order);
    }

    @Override
    public DomesticOrder queryByLogisticsNo(String logisticsNo) {
        return domesticOrderRepository.findByLogisticsNo(logisticsNo);
    }

    @Override
    public Map<String,Object> queryAll(DomesticOrderQueryCriteria criteria, Pageable pageable){
        Page<DomesticOrder> page = domesticOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(domesticOrderMapper::toDto));
    }

    @Override
    public List<DomesticOrderDto> queryAll(DomesticOrderQueryCriteria criteria){
        return domesticOrderMapper.toDto(domesticOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public DomesticOrderDto findById(Long id) {
        DomesticOrder domesticOrder = domesticOrderRepository.findById(id).orElseGet(DomesticOrder::new);
        ValidationUtil.isNull(domesticOrder.getId(),"DomesticOrder","id",id);
        return domesticOrderMapper.toDto(domesticOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DomesticOrderDto create(DomesticOrder resources) {
        return domesticOrderMapper.toDto(domesticOrderRepository.save(resources));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createWithDetails(DomesticOrder order) {
        DomesticOrderDto domesticOrderDto = create(order);
        List<DomesticOrderDetails> items = order.getItems();
        for (DomesticOrderDetails details : items) {
            details.setOrderId(domesticOrderDto.getId());
        }
        domesticOrderrDetailsService.createAll(items);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DomesticOrder resources) {
        domesticOrderRepository.save(resources);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            domesticOrderRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<DomesticOrderDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DomesticOrderDto domesticOrder : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单号", domesticOrder.getOrderNo());
            map.put("状态", DomesticOrderStatusEnum.getDesc(domesticOrder.getStatus()));
            map.put("客户", customerInfoService.queryById(domesticOrder.getCustomersId()).getCustNickName());
            map.put("店铺", shopInfoService.queryById(domesticOrder.getShopId()).getName());
            map.put("订单创建时间", domesticOrder.getOrderCreateTime());
            map.put("冻结原因", domesticOrder.getFreezeReason());
            map.put("运单号", domesticOrder.getLogisticsNo());
            map.put("省", domesticOrder.getProvince());
            map.put("市", domesticOrder.getCity());
            map.put("区", domesticOrder.getDistrict());
            map.put("收货人", domesticOrder.getConsigneeName());
            map.put("收货地址", domesticOrder.getConsigneeAddr());
            map.put("收货电话", domesticOrder.getConsigneeTel());
            map.put("接单回传时间", domesticOrder.getReceivedBackTime());
            map.put("审核通过时间", domesticOrder.getCheckPackTime());
            map.put("预处理完成时间", domesticOrder.getPreHandelTime());
            map.put("打包时间", domesticOrder.getPackTime());
            map.put("打包完成回传时间", domesticOrder.getPackBackTime());
            map.put("称重时间", domesticOrder.getWeighingTime());
            map.put("出库时间", domesticOrder.getDeliverTime());
            map.put("取消时间", domesticOrder.getCancelTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    // 导入订单
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadOrder(List<Map<String, Object>> list, String name) {
       if (StringUtils.isEmpty(name))
           throw new BadRequestException("名称为空");
        if (CollectionUtils.isEmpty(list))
            throw new BadRequestException("数据为空");
        List<DomesticOrder> orders = new ArrayList<>();
        for (Map<String, Object> map : list) {
            checkUploadOrder(orders, map, name);
        }
        for (DomesticOrder order : orders) {
            createWithDetails(order);
            DomesticOrderLog orderLog = new DomesticOrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(order.getStatus()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            domesticOrderLogService.create(orderLog);

            domesticOrderProducer.delaySend(
                    MsgType.DM_ORDER_215,
                    String.valueOf(order.getId()),
                    order.getOrderNo(),
                    2000
            );
        }
    }


    private void checkUploadOrder(List<DomesticOrder> orders, Map<String, Object> map, String name) {
        String shopCode = map.get("店铺CODE") != null ? map.get("店铺CODE").toString() : null;
        String orderNo = map.get("订单号") != null ?map.get("订单号").toString() : null;
        String consigneeName = map.get("收货人") != null ?map.get("收货人").toString() : null;
        String consigneeTel = map.get("收货人电话") != null ?map.get("收货人电话").toString() : null;
        String province = map.get("省") != null ?map.get("省").toString() : null;
        String city = map.get("市") != null ?map.get("市").toString() : null;
        String district = map.get("区") != null ?map.get("区").toString() : null;
        String consigneeAddr = map.get("详细地址") != null ?map.get("详细地址").toString() : null;
        String barcode = map.get("商品条码") != null ?map.get("商品条码").toString() : null;// 作为海关备案名
        String qty = map.get("商品数量") != null ?map.get("商品数量").toString() : null;

        if (StringUtil.isBlank(shopCode))
            throw new BadRequestException(orderNo + "，店铺CODE不能为空");
        if (StringUtil.isBlank(orderNo))
            throw new BadRequestException(orderNo + "，订单号不能为空");
        if (StringUtil.isBlank(consigneeName))
            throw new BadRequestException(orderNo + "，收货人不能为空");
        if (StringUtil.isBlank(consigneeTel))
            throw new BadRequestException(orderNo + "，收货人电话不能为空");
        if (StringUtil.isBlank(province))
            throw new BadRequestException(orderNo + "，省不能为空");
        if (StringUtil.isBlank(city))
            throw new BadRequestException(orderNo + "，市不能为空");
        if (StringUtil.isBlank(district))
            throw new BadRequestException(orderNo + "，区不能为空");
        if (StringUtil.isBlank(consigneeAddr))
            throw new BadRequestException(orderNo + "，详细地址不能为空");
        if (StringUtil.isBlank(barcode))
            throw new BadRequestException(orderNo + "，商品条码不能为空");
        if (StringUtil.isBlank(qty))
            throw new BadRequestException(orderNo + "，商品数量不能为空");
        ShopInfo shopInfo = shopInfoService.queryByShopCode(shopCode);
        if (shopInfo == null)
            throw new BadRequestException(orderNo + "，店铺CODE不存在");
        DomesticOrder exist = queryByOrderNo(orderNo);
        if (exist != null)
            throw new BadRequestException(orderNo + "，订单号已存在");
        BaseSku baseSku = baseSkuService.queryByGoodsNo(barcode);
        if (baseSku == null)
            throw new BadRequestException(orderNo + "，商品条码不存在");

        DomesticOrder order = new DomesticOrder();
        order.setCustomersId(shopInfo.getCustId());
        order.setShopId(shopInfo.getId());
        order.setOrderNo(orderNo);
        order.setStatus(DomesticOrderStatusEnum.STATUS_200.getCode());
        order.setOrderType(name);
        order.setOrderCreateTime(new Timestamp(System.currentTimeMillis()));
        order.setProvince(province);
        order.setCity(city);
        order.setDistrict(district);
        order.setConsigneeAddr(consigneeAddr);
        order.setConsigneeName(consigneeName);
        order.setConsigneeTel(consigneeTel);
        order.setCreateTime(new Timestamp(System.currentTimeMillis()));
        order.setCreateBy("SYSTEM");

        if (orders.contains(order)) {
            order = orders.get(orders.indexOf(order));
        }else {
            orders.add(order);
        }
        List<DomesticOrderDetails> items = order.getItems();
        if (CollectionUtils.isEmpty(items))
            items = new ArrayList<>();
        DomesticOrderDetails details = new DomesticOrderDetails();
        details.setOrderNo(orderNo);
        details.setGoodsId(baseSku.getId());
        details.setGoodsNo(baseSku.getGoodsNo());
        details.setGoodsCode(baseSku.getGoodsCode());
        details.setFontGoodsName(baseSku.getGoodsName());
        details.setGoodsName(baseSku.getGoodsName());
        details.setQty(new BigDecimal(qty).toString());
        items.add(details);
        order.setItems(items);
    }

    @Override
    public DomesticOrder queryByOrderNo(String orderNo) {
        return  domesticOrderRepository.findByOrderNo(orderNo);
    }

    @Override
    public DomesticOrder queryByIdWithDetails(Long id) {
        DomesticOrder domesticOrder = domesticOrderRepository.findById(id).orElseGet(DomesticOrder::new);
        List<DomesticOrderDetails> items = domesticOrderrDetailsService.queryByOrderId(id);
        domesticOrder.setItems(items);
        return domesticOrder;
    }



    @Override
    public DomesticOrder queryById(Long id) {
        return domesticOrderRepository.findById(id).orElseGet(DomesticOrder::new);
    }


}