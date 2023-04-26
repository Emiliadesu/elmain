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
import cn.hutool.json.JSONObject;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.exception.EntityExistException;
import me.zhengjie.service.*;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.CombinationOrderRepository;
import me.zhengjie.service.dto.CombinationOrderDto;
import me.zhengjie.service.dto.CombinationOrderQueryCriteria;
import me.zhengjie.service.mapstruct.CombinationOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author wangm
* @date 2021-06-21
**/
@Service
@RequiredArgsConstructor
public class CombinationOrderServiceImpl implements CombinationOrderService {

    private final CombinationOrderRepository combinationOrderRepository;
    private final CombinationOrderMapper combinationOrderMapper;

    @Autowired
    private CombSplitService combSplitService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private ShopTokenService shopTokenService;

    @Autowired
    private PlatformService platformService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Override
    public Map<String,Object> queryAll(CombinationOrderQueryCriteria criteria, Pageable pageable){
        Page<CombinationOrder> page = combinationOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(combinationOrderMapper::toDto));
    }

    @Override
    public List<CombinationOrderDto> queryAll(CombinationOrderQueryCriteria criteria){
        return combinationOrderMapper.toDto(combinationOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public CombinationOrderDto findById(Long id) {
        CombinationOrder combinationOrder = combinationOrderRepository.findById(id).orElseGet(CombinationOrder::new);
        ValidationUtil.isNull(combinationOrder.getId(),"CombinationOrder","id",id);
        return combinationOrderMapper.toDto(combinationOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CombinationOrderDto create(CombinationOrder resources) {
        if(combinationOrderRepository.findByCombSkuId(resources.getCombSkuId()) != null){
            throw new EntityExistException(CombinationOrder.class,"comb_sku_id",resources.getCombSkuId());
        }
        if (CollectionUtil.isEmpty(resources.getSplitList()))
            throw new BadRequestException("没有拆分明细");
        resources.setSplitQty(0);
        for (CombSplit combSplit : resources.getSplitList()) {
            resources.setSplitQty(resources.getSplitQty()+combSplit.getQty());
        }
        ShopInfo shopInfo=shopInfoService.findById(resources.getShopId());
        resources.setShopCode(shopInfo.getCode());
        resources=combinationOrderRepository.save(resources);
        for (CombSplit combSplit : resources.getSplitList()) {
            combSplit.setCombId(resources.getId());
            combSplit.setCombSkuId(resources.getCombSkuId());
            combSplitService.create(combSplit);
        }
        return combinationOrderMapper.toDto(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CombinationOrder resources) {
        CombinationOrder combinationOrder = combinationOrderRepository.findById(resources.getId()).orElseGet(CombinationOrder::new);
        ValidationUtil.isNull( combinationOrder.getId(),"CombinationOrder","id",resources.getId());
        CombinationOrder combinationOrder1 = null;
        combinationOrder1 = combinationOrderRepository.findByCombSkuId(resources.getCombSkuId());
        if(combinationOrder1 != null && !combinationOrder1.getId().equals(combinationOrder.getId())){
            throw new EntityExistException(CombinationOrder.class,"comb_sku_id",resources.getCombSkuId());
        }
        combinationOrder.copy(resources);
        combinationOrderRepository.save(combinationOrder);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            combinationOrderRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<CombinationOrderDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (CombinationOrderDto combinationOrder : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("组合包的skuId", combinationOrder.getCombSkuId());
            map.put("组合包的名称", combinationOrder.getCombName());
            map.put("组合包来源平台", combinationOrder.getPlatform());
            map.put(" splitQty",  combinationOrder.getSplitQty());
            map.put("组合包所属系统店铺id", combinationOrder.getShopId());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public CombinationOrder queryByCombSku(String combSkuId) {
        return combinationOrderRepository.findByCombSkuId(combSkuId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadComb(List<Map<String, Object>> list) {
        if (CollectionUtils.isEmpty(list))
            throw new BadRequestException("数据为空");
        List<CombinationOrder> combs = new ArrayList<>();
        ShopToken shopToken = null;
        ShopInfo shopInfo = null;
        Platform platform = null;
        for (Map<String, Object> map : list) {
            CombinationOrder comb = checkComb(map);
            if (combs.contains(comb)){
                List<CombSplit>splitList=comb.getSplitList();
                for (CombSplit combSplit : splitList) {
                    if (StringUtil.equals(shopInfo.getPushTo(),"1")){
                        BaseSku baseSku = baseSkuService.queryByOutGoodsNo(combSplit.getSplitSkuId());
                        if (baseSku==null)
                            throw new BadRequestException("菜鸟货品id"+combSplit.getSplitSkuId()+"不存在");
                        combSplit.setSplitSkuId(baseSku.getGoodsNo());
                    }
                }
                combs.get(combs.indexOf(comb)).getSplitList().addAll(splitList);
            }else {
                if (shopToken==null||!StringUtil.equals(shopToken.getPlatformShopId(),comb.getShopCode())){
                    shopToken = shopTokenService.queryByPaltShopId(comb.getShopCode());
                    shopInfo = shopInfoService.findById(shopToken.getShopId());
                }
                if (platform==null || !StringUtil.equals(platform.getPlafCode(),comb.getPlatformCode()))
                    platform=platformService.findByCode(comb.getPlatformCode());
                if (platform==null)
                    throw new BadRequestException("平台编码为"+comb.getPlatformCode()+"不存在");
                List<CombSplit>splitList=comb.getSplitList();
                for (CombSplit combSplit : splitList) {
                    if (StringUtil.equals(shopInfo.getPushTo(),"1")){
                        BaseSku baseSku = baseSkuService.queryByOutGoodsNo(combSplit.getSplitSkuId());
                        if (baseSku==null)
                            throw new BadRequestException("菜鸟货品id"+combSplit.getSplitSkuId()+"不存在");
                        combSplit.setSplitSkuId(baseSku.getGoodsNo());
                    }
                }
                comb.setShopId(shopToken.getShopId());
                comb.setPlatform(platform.getId());
                combs.add(comb);
            }
        }
        for (CombinationOrder comb : combs) {
            CombinationOrder exist = queryByCombSku(comb.getCombSkuId());
            if (exist==null)
                create(comb);
        }
    }

    private CombinationOrder checkComb(Map<String, Object> map) {
        CombinationOrder comb=new CombinationOrder();
        JSONObject obj=new JSONObject(map);
        String combSkuName = obj.getStr("comb_sku_name");
        String splitSkuName = obj.getStr("split_sku_name");
        String combSkuId = obj.getStr("comb_sku_id");
        String splitSkuId = obj.getStr("split_sku_id");
        BigDecimal payment = obj.getBigDecimal("payment");
        Integer splitQty = obj.getInt("split_qty");
        String shopCode = obj.getStr("shop_code");
        String platformCode = obj.getStr("platform");
        comb.setCombSkuId(combSkuId);
        comb.setPlatformCode(platformCode);
        Integer qty = obj.getInt("qty");
        comb.setShopCode(shopCode);
        comb.setSplitQty(splitQty);
        comb.setCombName(combSkuName);
        List<CombSplit>splitList=new ArrayList<>();
        CombSplit split=new CombSplit();
        split.setCombSkuId(combSkuId);
        split.setPayment(payment);
        split.setQty(qty);
        split.setSplitSkuId(splitSkuId);
        split.setSplitSkuName(splitSkuName);
        splitList.add(split);
        comb.setSplitList(splitList);
        return comb;
    }
}
