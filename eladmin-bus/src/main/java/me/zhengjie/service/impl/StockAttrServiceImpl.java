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
import me.zhengjie.domain.BaseSku;
import me.zhengjie.domain.StockAttr;
import me.zhengjie.domain.WmsInstock;
import me.zhengjie.service.BaseSkuService;
import me.zhengjie.service.WmsInstockService;
import me.zhengjie.service.dto.InvLotLocIdAtt;
import me.zhengjie.support.WmsSupport;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.StockAttrRepository;
import me.zhengjie.service.StockAttrService;
import me.zhengjie.service.dto.StockAttrDto;
import me.zhengjie.service.dto.StockAttrQueryCriteria;
import me.zhengjie.service.mapstruct.StockAttrMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * @author wangm
 * @website https://el-admin.vip
 * @description 服务实现
 * @date 2021-06-08
 **/
@Service
@RequiredArgsConstructor
public class StockAttrServiceImpl implements StockAttrService {

    private final StockAttrRepository stockAttrRepository;
    private final StockAttrMapper stockAttrMapper;

    @Autowired
    private WmsSupport wmsSupport;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private WmsInstockService wmsInstockService;

    @Override
    public Map<String, Object> queryAll(StockAttrQueryCriteria criteria, Pageable pageable) {
        Page<StockAttr> page = stockAttrRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(stockAttrMapper::toDto));
    }

    @Override
    public List<StockAttrDto> queryAll(StockAttrQueryCriteria criteria) {
        return stockAttrMapper.toDto(stockAttrRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
    }

    @Override
    @Transactional
    public StockAttrDto findById(Long id) {
        StockAttr stockAttr = stockAttrRepository.findById(id).orElseGet(StockAttr::new);
        ValidationUtil.isNull(stockAttr.getId(), "StockAttr", "id", id);
        return stockAttrMapper.toDto(stockAttr);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StockAttrDto create(StockAttr resources) {
        return stockAttrMapper.toDto(stockAttrRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(StockAttr resources) {
        StockAttr stockAttr = stockAttrRepository.findById(resources.getId()).orElseGet(StockAttr::new);
        ValidationUtil.isNull(stockAttr.getId(), "StockAttr", "id", resources.getId());
        stockAttr.copy(resources);
        stockAttrRepository.save(stockAttr);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            stockAttrRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<StockAttrDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (StockAttrDto stockAttr : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("仓库批次号", stockAttr.getWmsBatchNo());
            map.put("入库单号", stockAttr.getInOrderSn());
            map.put("细分类型", stockAttr.getSubType());
            map.put("账册号", stockAttr.getBookNo());
            map.put("供应商编码", stockAttr.getSuperviseCode());
            map.put("客户批次号", stockAttr.getCustomerBatchNo());
            map.put("最近修改时间", stockAttr.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public StockAttr queryByWmsBatch(String wmsBatch) {
        StockAttr stockAttr = new StockAttr();
        stockAttr.setWmsBatchNo(wmsBatch);
        List<StockAttr> attrList = stockAttrRepository.findAll(Example.of(stockAttr));
        return CollectionUtils.isNotEmpty(attrList) ? attrList.get(0) : null;
    }

    @Override
    public List<StockAttr> findByCustomerBatchNoAndSubType(String customerBatchNo, String subType) {
        StockAttr stockAttr = new StockAttr();
        stockAttr.setCustomerBatchNo(customerBatchNo);
        stockAttr.setSubType(subType);
        return stockAttrRepository.findAll(Example.of(stockAttr));
    }

    @Override
    public boolean exists(StockAttr stockAttr) {
        return stockAttrRepository.findOne(Example.of(stockAttr)).orElse(null) != null;
    }

    @Override
    public void recordNewLotNum() {
        List<BaseSku> baseSkuList = baseSkuService.queryByCustomerId(32L);
        if (CollectionUtil.isEmpty(baseSkuList))
            return;
        List<String> skuList = new ArrayList<>();
        for (BaseSku baseSku : baseSkuList) {
            skuList.add(baseSku.getGoodsNo());
        }
        List<InvLotLocIdAtt> attList = wmsSupport.getLotNum(skuList);
        if (CollectionUtil.isEmpty(attList))
            return;
        for (InvLotLocIdAtt invLotLocIdAtt : attList) {
            StockAttr stockAttr = queryByWmsBatch(invLotLocIdAtt.getLotNum());
            if (stockAttr != null)
                continue;
            WmsInstock wmsInstock = wmsInstockService.queryByPoNo(invLotLocIdAtt.getLotAtt04());
            if (wmsInstock != null && StringUtil.equals(wmsInstock.getInStatus(), "60")) {
                stockAttr = new StockAttr();
                stockAttr.setWmsBatchNo(invLotLocIdAtt.getLotNum());
                stockAttr.setBookNo("T3105W000060");
                stockAttr.setInOrderSn(invLotLocIdAtt.getLotAtt04());
                stockAttr.setSuperviseCode("1210");
                stockAttr.setSubType("0102");
                stockAttr.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                stockAttr.setCustomerBatchNo(invLotLocIdAtt.getLotAtt09());
                create(stockAttr);
            }
        }
    }
}
