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

import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.OutboundOrderDto;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.DailyCrossBorderOrderRepository;
import me.zhengjie.service.dto.DailyCrossBorderOrderDto;
import me.zhengjie.service.dto.DailyCrossBorderOrderQueryCriteria;
import me.zhengjie.service.mapstruct.DailyCrossBorderOrderMapper;
import me.zhengjie.utils.enums.CBOrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author leningzhou
* @date 2022-07-04
**/
@Service
@RequiredArgsConstructor
public class DailyCrossBorderOrderServiceImpl implements DailyCrossBorderOrderService {

    @Autowired
    private DailyCrossBorderOrderDetailsService dailyCrossBorderOrderDetailsService;

    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

    @Autowired
    private CrossBorderOrderDetailsService crossBorderOrderDetailsService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private CustomerInfoService customerInfoService;


    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private PlatformService platformService;

    private final DailyCrossBorderOrderRepository dailyCrossBorderOrderRepository;
    private final DailyCrossBorderOrderMapper dailyCrossBorderOrderMapper;

    @Override
    public Map<String,Object> queryAll(DailyCrossBorderOrderQueryCriteria criteria, Pageable pageable){
        Page<DailyCrossBorderOrder> page = dailyCrossBorderOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(dailyCrossBorderOrderMapper::toDto));
    }

    @Override
    public List<DailyCrossBorderOrderDto> queryAll(DailyCrossBorderOrderQueryCriteria criteria){
        return dailyCrossBorderOrderMapper.toDto(dailyCrossBorderOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public DailyCrossBorderOrderDto findById(Long id) {
        DailyCrossBorderOrder dailyCrossBorderOrder = dailyCrossBorderOrderRepository.findById(id).orElseGet(DailyCrossBorderOrder::new);
        ValidationUtil.isNull(dailyCrossBorderOrder.getId(),"DailyCrossBorderOrder","id",id);
        return dailyCrossBorderOrderMapper.toDto(dailyCrossBorderOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DailyCrossBorderOrderDto create(DailyCrossBorderOrder resources) {
        return dailyCrossBorderOrderMapper.toDto(dailyCrossBorderOrderRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DailyCrossBorderOrder resources) {
        DailyCrossBorderOrder dailyCrossBorderOrder = dailyCrossBorderOrderRepository.findById(resources.getId()).orElseGet(DailyCrossBorderOrder::new);
        ValidationUtil.isNull( dailyCrossBorderOrder.getId(),"DailyCrossBorderOrder","id",resources.getId());
        dailyCrossBorderOrder.copy(resources);
        dailyCrossBorderOrderRepository.save(dailyCrossBorderOrder);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            dailyCrossBorderOrderRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<DailyCrossBorderOrderDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DailyCrossBorderOrderDto dailyCrossBorderOrder : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单号", dailyCrossBorderOrder.getOrderNo());
            map.put("交易号", dailyCrossBorderOrder.getCrossBorderNo());
            map.put("状态", dailyCrossBorderOrder.getStatus());
            map.put("客户", dailyCrossBorderOrder.getCustomersName());
            map.put("店铺", dailyCrossBorderOrder.getShopName());
            map.put("是否预售", dailyCrossBorderOrder.getPreSell());
            map.put("申报单号", dailyCrossBorderOrder.getDeclareNo());
            map.put("总署清单编号", dailyCrossBorderOrder.getInvtNo());
            map.put("波次号", dailyCrossBorderOrder.getWaveNo());
            map.put("SO单号", dailyCrossBorderOrder.getSoNo());
            map.put("申报状态", dailyCrossBorderOrder.getDeclareStatus());
            map.put("申报信息", dailyCrossBorderOrder.getDeclareMsg());
            map.put("冻结原因", dailyCrossBorderOrder.getFreezeReason());
            map.put("运费", dailyCrossBorderOrder.getPostFee());
            map.put("实付金额", dailyCrossBorderOrder.getPayment());
            map.put("预估税费", dailyCrossBorderOrder.getTaxAmount());
            map.put("运单号", dailyCrossBorderOrder.getLogisticsNo());
            map.put("省", dailyCrossBorderOrder.getProvince());
            map.put("市", dailyCrossBorderOrder.getCity());
            map.put("区", dailyCrossBorderOrder.getDistrict());
            map.put("收货地址", dailyCrossBorderOrder.getConsigneeAddr());
            map.put("收货电话", dailyCrossBorderOrder.getConsigneeTel());
            map.put("支付时间", dailyCrossBorderOrder.getPayTime());
            map.put("订单创建时间", dailyCrossBorderOrder.getOrderCreateTime());
            map.put("接单时间", dailyCrossBorderOrder.getCreateTime());
            map.put("接单回传时间", dailyCrossBorderOrder.getReceivedBackTime());
            map.put("清关开始时间", dailyCrossBorderOrder.getClearStartTime());
            map.put("清关开始回传时间", dailyCrossBorderOrder.getClearStartBackTime());
            map.put("清关完成时间", dailyCrossBorderOrder.getClearSuccessTime());
            map.put("清关完成回传时间", dailyCrossBorderOrder.getClearSuccessBackTime());
            map.put("出库时间", dailyCrossBorderOrder.getDeliverTime());
            map.put("是否揽收", dailyCrossBorderOrder.getIsCollect());
            map.put("揽收时间", dailyCrossBorderOrder.getLogisticsCollectTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void downloadDetails(List<DailyCrossBorderOrderDto> queryAll, HttpServletResponse response) throws IOException{
        List<Map<String, Object>> list = new ArrayList<>();
        for (DailyCrossBorderOrderDto dailyCrossBorderOrder : queryAll) {
            List<DailyCrossBorderOrderDetails> dailyCrossBorderOrderDetails = dailyCrossBorderOrderDetailsService.findByOrderId(dailyCrossBorderOrder.getId());
            for (DailyCrossBorderOrderDetails details : dailyCrossBorderOrderDetails) {
                Map<String,Object> map = new LinkedHashMap<>();
                map.put("订单号", details.getOrderNo());
                map.put("交易单号", details.getCrossBorderNo());
                map.put("状态", details.getStatus());
                map.put("客户", details.getCustomersName());
                map.put("店铺", details.getShopName());
                map.put("运单号", details.getLogisticsNo());
                map.put("省", details.getProvince());
                map.put("市", details.getCity());
                map.put("区", details.getDistrict());
                map.put("收货地址", details.getConsigneeAddr());
                map.put("支付时间", details.getPayTime());
                map.put("订单创建时间", details.getOrderCreateTime());
                map.put("出库时间", details.getDeliverTime());
                map.put("是否揽收", details.getIsCollect());
                map.put("揽收时间", details.getLogisticsCollectTime());
                map.put("商品编码", details.getGoodsCode());
                map.put("商品名称", details.getGoodsName());
                map.put("商品海关备案名", details.getFontGoodsName());
                map.put("海关货号", details.getGoodsNo());
                map.put("条码", details.getBarCode());
                map.put("数量", details.getQty());
                map.put("实付总价", details.getPayment());
                map.put("总税额", details.getTaxAmount());
                list.add(map);
            }

        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addOrderCache(Long orderId) {
        CrossBorderOrder crossBorderOrder=crossBorderOrderService.queryByIdWithDetails(orderId);
        if (crossBorderOrder==null)
            throw new BadRequestException("订单不存在");
        DailyCrossBorderOrder order = dailyCrossBorderOrderRepository.findByOrderId(orderId);
        if (order!=null)
            return;
        try {
            crossBorderOrderService.decryptMask(crossBorderOrder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        order=new DailyCrossBorderOrder();
        order.setOrderId(orderId);
        order.setOrderNo(crossBorderOrder.getOrderNo());
        order.setCrossBorderNo(crossBorderOrder.getCrossBorderNo());
        order.setStatus(CBOrderStatusEnum.getDesc(crossBorderOrder.getStatus()));
        order.setCustomersName(customerInfoService.queryById(crossBorderOrder.getCustomersId()).getCustNickName());
        order.setCustomersId(crossBorderOrder.getCustomersId());
        order.setShopName(shopInfoService.queryById(crossBorderOrder.getShopId()).getName());
        order.setShopId(crossBorderOrder.getShopId()+"");
        order.setPreSell(StringUtils.equals("1", crossBorderOrder.getPreSell()) ? "是" : "否");
        order.setDeclareNo(crossBorderOrder.getDeclareNo());
        order.setInvtNo(crossBorderOrder.getInvtNo());
        order.setWaveNo(crossBorderOrder.getWaveNo());
        order.setSoNo(crossBorderOrder.getSoNo());
        order.setDeclareStatus(crossBorderOrder.getDeclareStatus());
        order.setDeclareMsg(crossBorderOrder.getDeclareMsg());
        order.setFreezeReason(crossBorderOrder.getFreezeReason());
        order.setPostFee(crossBorderOrder.getPostFee());
        order.setPayment(crossBorderOrder.getPayment());
        order.setTaxAmount(crossBorderOrder.getTaxAmount());
        order.setLogisticsNo(crossBorderOrder.getLogisticsNo());
        order.setProvince(crossBorderOrder.getProvince());
        order.setCity(crossBorderOrder.getCity());
        order.setDistrict(crossBorderOrder.getDistrict());
        order.setConsigneeAddr(PrivacyUtil.maskAddressData(crossBorderOrder.getConsigneeAddr()));
        order.setConsigneeTel(PrivacyUtil.maskPhoneData(crossBorderOrder.getConsigneeTel()));
        order.setPayTime(crossBorderOrder.getPayTime());
        order.setOrderCreateTime(crossBorderOrder.getOrderCreateTime());
        order.setCreateTime(crossBorderOrder.getCreateTime());
        order.setReceivedBackTime(crossBorderOrder.getReceivedBackTime());
        order.setClearStartTime(crossBorderOrder.getClearStartTime());
        order.setClearStartBackTime(crossBorderOrder.getClearStartBackTime());
        order.setClearSuccessTime(crossBorderOrder.getClearSuccessTime());
        order.setClearSuccessBackTime(crossBorderOrder.getClearSuccessBackTime());
        order.setDeliverTime(crossBorderOrder.getDeliverTime());
        order.setLogisticsCollectTime(crossBorderOrder.getLogisticsCollectTime());
        order.setIsCollect(crossBorderOrder.getLogisticsCollectTime()==null?"否":"是");
        order.setCreateBy(crossBorderOrder.getCreateBy());
        order.setCancelTime(crossBorderOrder.getCancelTime());
        create(order);
        if (CollectionUtils.isEmpty(crossBorderOrder.getItemList()))
            crossBorderOrder.setItemList(crossBorderOrderDetailsService.queryByOrderId(orderId));
        if (CollectionUtils.isNotEmpty(crossBorderOrder.getItemList())){
            for (CrossBorderOrderDetails orderDetails : crossBorderOrder.getItemList()) {
                DailyCrossBorderOrderDetails detail = dailyCrossBorderOrderDetailsService.findDetailId(orderDetails.getId());
                if (detail!=null)
                    continue;
                detail = new DailyCrossBorderOrderDetails();
                detail.setOrderId(orderId);
                detail.setDetailId(orderDetails.getId());
                detail.setOrderNo(order.getOrderNo());
                detail.setStatus(order.getStatus());
                detail.setCustomersId(order.getCustomersId());
                detail.setCustomersName(order.getCustomersName());
                detail.setShopId(order.getShopId());
                detail.setShopName(order.getShopName());
                detail.setLogisticsNo(order.getLogisticsNo());
                detail.setProvince(order.getProvince());
                detail.setCity(order.getCity());
                detail.setDistrict(order.getDistrict());
                detail.setConsigneeAddr(order.getConsigneeAddr());
                detail.setPayTime(order.getPayTime());
                detail.setOrderCreateTime(order.getOrderCreateTime());
                detail.setDeliverTime(order.getDeliverTime());
                detail.setIsCollect(order.getIsCollect());
                detail.setLogisticsCollectTime(order.getLogisticsCollectTime());
                BaseSku baseSku;
                if (orderDetails.getGoodsId()==null)
                    baseSku=baseSkuService.queryByGoodsNo(orderDetails.getGoodsNo());
                else
                    baseSku=baseSkuService.queryById(orderDetails.getGoodsId());
                detail.setGoodsCode(baseSku.getGoodsCode());
                detail.setGoodsName(baseSku.getGoodsName());
                detail.setFontGoodsName(detail.getFontGoodsName());
                detail.setGoodsNo(baseSku.getGoodsNo());
                detail.setBarCode(baseSku.getBarCode());
                detail.setQty(orderDetails.getQty());
                detail.setPayment(orderDetails.getPayment());
                detail.setTaxAmount(orderDetails.getTaxAmount());
                dailyCrossBorderOrderDetailsService.create(detail);
            }
        }
    }
}