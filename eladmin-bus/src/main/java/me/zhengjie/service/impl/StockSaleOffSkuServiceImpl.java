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

import me.zhengjie.domain.StockSaleOffSku;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.CustomerInfoService;
import me.zhengjie.service.ShopInfoService;
import me.zhengjie.service.dto.*;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.StockSaleOffSkuRepository;
import me.zhengjie.service.StockSaleOffSkuService;
import me.zhengjie.service.mapstruct.StockSaleOffSkuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author leningzhou
* @date 2022-12-15
**/
@Service
@RequiredArgsConstructor
public class StockSaleOffSkuServiceImpl implements StockSaleOffSkuService {

    private final StockSaleOffSkuRepository stockSaleOffSkuRepository;
    private final StockSaleOffSkuMapper stockSaleOffSkuMapper;

    @Autowired
    private CustomerInfoService customerInfoService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Override
    public Map<String,Object> queryAll(StockSaleOffSkuQueryCriteria criteria, Pageable pageable){
        Page<StockSaleOffSku> page = stockSaleOffSkuRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(stockSaleOffSkuMapper::toDto));
    }

    @Override
    public List<StockSaleOffSkuDto> queryAll(StockSaleOffSkuQueryCriteria criteria){
        return stockSaleOffSkuMapper.toDto(stockSaleOffSkuRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public StockSaleOffSkuDto findById(Long id) {
        StockSaleOffSku stockSaleOffSku = stockSaleOffSkuRepository.findById(id).orElseGet(StockSaleOffSku::new);
        ValidationUtil.isNull(stockSaleOffSku.getId(),"StockSaleOffSku","id",id);
        return stockSaleOffSkuMapper.toDto(stockSaleOffSku);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StockSaleOffSkuDto create(StockSaleOffSku resources) {
        return stockSaleOffSkuMapper.toDto(stockSaleOffSkuRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(StockSaleOffSku resources) {
        StockSaleOffSku stockSaleOffSku = stockSaleOffSkuRepository.findById(resources.getId()).orElseGet(StockSaleOffSku::new);
        ValidationUtil.isNull( stockSaleOffSku.getId(),"StockSaleOffSku","id",resources.getId());
        stockSaleOffSku.copy(resources);
        stockSaleOffSkuRepository.save(stockSaleOffSku);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            stockSaleOffSkuRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<StockSaleOffSkuDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (StockSaleOffSkuDto stockSaleOffSku : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("客户", stockSaleOffSku.getCustomersId());
            map.put("店铺", stockSaleOffSku.getShopId());
            map.put("海关货号", stockSaleOffSku.getGoodsNo());
            map.put("外部货号", stockSaleOffSku.getOuterGoodsNo());
            map.put("条形码", stockSaleOffSku.getBarCode());
            map.put("商品名称", stockSaleOffSku.getGoodsName());
            map.put("库位", stockSaleOffSku.getWarehouse());
            map.put("总库存", stockSaleOffSku.getSumStock());
            map.put("占用库存", stockSaleOffSku.getHoldStock());
            map.put("可用库存", stockSaleOffSku.getUseStock());
            map.put("生产日期", stockSaleOffSku.getProductionTime());
            map.put("失效日期", stockSaleOffSku.getInvalidTime());
            map.put("入库日期", stockSaleOffSku.getPutinTime());
            map.put("产品批次号", stockSaleOffSku.getBatchCode());
            map.put("库存属性", stockSaleOffSku.getStockAttribute());
            map.put("禁售天数", stockSaleOffSku.getSaleOffDay());
            map.put("是否禁售", stockSaleOffSku.getIsSaleFail());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void downloads(List<StockSaleOffSkuDto> queryAll, HttpServletResponse response) {
        List<Map<String, Object>> list = new ArrayList<>();
        CustomerInfoDto customerInfoDto = null;
        ShopInfoDto shopInfoDto = null;
        for (StockSaleOffSkuDto stockSaleOffSku : queryAll) {
            Map<String,Object> map = new LinkedHashMap<>();
            if (customerInfoDto==null|| !customerInfoDto.getId().equals(stockSaleOffSku.getCustomersId()))
                customerInfoDto = customerInfoService.queryById(stockSaleOffSku.getCustomersId());
            if (shopInfoDto == null || !shopInfoDto.getId().equals(stockSaleOffSku.getShopId()))
                shopInfoDto = shopInfoService.queryById(stockSaleOffSku.getShopId());
            map.put("客户", customerInfoDto.getCustNickName());
            map.put("店铺", shopInfoDto.getName());
            map.put("海关货号", stockSaleOffSku.getGoodsNo());
            map.put("外部货号", stockSaleOffSku.getOuterGoodsNo());
            map.put("条形码", stockSaleOffSku.getBarCode());
            map.put("商品名称", stockSaleOffSku.getGoodsName());
            map.put("库位", stockSaleOffSku.getWarehouse());
            map.put("总库存", stockSaleOffSku.getSumStock());
            map.put("占用库存", stockSaleOffSku.getHoldStock());
            map.put("可用库存", stockSaleOffSku.getUseStock());
            map.put("生产日期", stockSaleOffSku.getProductionTime());
            map.put("失效日期", stockSaleOffSku.getInvalidTime());
            map.put("入库日期", stockSaleOffSku.getPutinTime());
            map.put("产品批次号", stockSaleOffSku.getBatchCode());
            map.put("库存属性", stockSaleOffSku.getStockAttribute());
            map.put("禁售天数", stockSaleOffSku.getSaleOffDay());
            //判断当前日期+禁售天数>失效日期那么就生成一条预警
            if (stockSaleOffSku.getSaleOffDay() != null) {
                Date saleDay =new Date(System.currentTimeMillis() + stockSaleOffSku.getSaleOffDay()*1000*3600*24L);
                if (DateUtils.gt(saleDay,stockSaleOffSku.getInvalidTime())){
                    //此时是需要添加预警
                    map.put("禁售信息", "已超失效日期");
                    stockSaleOffSku.setIsSaleFail("是");
                    map.put("是否禁售", stockSaleOffSku.getIsSaleFail());
                }else
                    map.put("禁售信息","无");
                    map.put("是否禁售", "否");
            }else {
                //因为没有禁售天数，无法生成预警
                map.put("禁售信息", "因为没有禁售天数，无法生成预警");
            }
            list.add(map);
        }
        try {
            FileUtil.downloadExcel(list, response);
        } catch (IOException e) {
            throw new BadRequestException("导出错误： " + e.getMessage());
        }
    }
}

