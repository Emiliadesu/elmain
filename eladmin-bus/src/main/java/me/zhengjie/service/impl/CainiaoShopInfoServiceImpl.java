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
import me.zhengjie.support.cainiao.CaiNiaoSupport;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.CainiaoShopInfoRepository;
import me.zhengjie.service.dto.CainiaoShopInfoDto;
import me.zhengjie.service.dto.CainiaoShopInfoQueryCriteria;
import me.zhengjie.service.mapstruct.CainiaoShopInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
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
* @author wangm
* @date 2022-11-21
**/
@Service
@RequiredArgsConstructor
public class CainiaoShopInfoServiceImpl implements CainiaoShopInfoService {

    private final CainiaoShopInfoRepository cainiaoShopInfoRepository;
    private final CainiaoShopInfoMapper cainiaoShopInfoMapper;

    @Autowired
    private ConfigService configService;

    @Override
    public Map<String,Object> queryAll(CainiaoShopInfoQueryCriteria criteria, Pageable pageable){
        Page<CainiaoShopInfo> page = cainiaoShopInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(cainiaoShopInfoMapper::toDto));
    }

    @Override
    public List<CainiaoShopInfoDto> queryAll(CainiaoShopInfoQueryCriteria criteria){
        return cainiaoShopInfoMapper.toDto(cainiaoShopInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public CainiaoShopInfoDto findById(Long id) {
        CainiaoShopInfo cainiaoShopInfo = cainiaoShopInfoRepository.findById(id).orElseGet(CainiaoShopInfo::new);
        ValidationUtil.isNull(cainiaoShopInfo.getId(),"CainiaoShopInfo","id",id);
        return cainiaoShopInfoMapper.toDto(cainiaoShopInfo);
    }

    @Override
    public CainiaoShopInfo queryById(Long id) {
        return cainiaoShopInfoRepository.findById(id).orElse(null);
    }

    @Override
    public CainiaoShopInfo queryByShopId(Long shopId) {
        return cainiaoShopInfoRepository.findByShopId(shopId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CainiaoShopInfoDto create(CainiaoShopInfo resources) {
        if (StringUtil.isBlank(resources.getCpCode())){
            Config cfg = configService.queryByK("CN_CP_CODE");
            if (cfg == null)
                throw new BadRequestException("CN_CP_CODE默认配置项为空");
            resources.setCpCode(cfg.getV());
        }
        return cainiaoShopInfoMapper.toDto(cainiaoShopInfoRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CainiaoShopInfo resources) {
        CainiaoShopInfo cainiaoShopInfo = cainiaoShopInfoRepository.findById(resources.getId()).orElseGet(CainiaoShopInfo::new);
        ValidationUtil.isNull( cainiaoShopInfo.getId(),"CainiaoShopInfo","id",resources.getId());
        cainiaoShopInfo.copy(resources);
        cainiaoShopInfoRepository.save(cainiaoShopInfo);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            cainiaoShopInfoRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<CainiaoShopInfoDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (CainiaoShopInfoDto cainiaoShopInfo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("店铺", cainiaoShopInfo.getShopId());
            map.put("菜鸟货主id", cainiaoShopInfo.getCnOwnerId());
            map.put("物流云对ISV的授权token", cainiaoShopInfo.getCpCode());
            map.put("gos平台的授权token", cainiaoShopInfo.getGosToken());
            map.put("所属BU Id", cainiaoShopInfo.getBusinessUnitId());
            map.put("HSCode", cainiaoShopInfo.getHsCode());
            map.put("库存分组", cainiaoShopInfo.getChannelCode());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Autowired
    private CrossBorderOrderService crossBorderOrderService;
    @Autowired
    private ShopInfoService shopInfoService;
    @Autowired
    private CaiNiaoSupport caiNiaoSupport;
    @Autowired
    private PddCloudPrintDataService pddCloudPrintDataService;

    @Override
    public void sendOrderTest(String orderNo) {
        CrossBorderOrder order = crossBorderOrderService.queryByOrderNoWithDetails(orderNo);
        ShopInfo shopInfo = shopInfoService.findById(order.getShopId());
        CainiaoShopInfo cainiaoShopInfo=cainiaoShopInfoRepository.findByShopId(order.getShopId());
        PddCloudPrintData printData = pddCloudPrintDataService.findByOrderNo(order.getOrderNo());
        caiNiaoSupport.sendOrderToWms(order,shopInfo,cainiaoShopInfo,printData);
    }

    @Override
    public void declareResultCallBackTest(String orderNo) throws Exception{
        CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(orderNo);
        caiNiaoSupport.decalreResultCallBackGA(order);
    }

    @Override
    public void cancelDeclareTest(String orderNo){
        CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(orderNo);
        CainiaoShopInfo cainiaoShopInfo=cainiaoShopInfoRepository.findByShopId(order.getShopId());
        caiNiaoSupport.cancelDeclare(order,cainiaoShopInfo);
    }

    @Override
    public void lastmineHoinCallbackTest(String orderNo){
        CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(orderNo);
        CainiaoShopInfo cainiaoShopInfo=cainiaoShopInfoRepository.findByShopId(order.getShopId());
        caiNiaoSupport.lastmineHoinCallback(order,cainiaoShopInfo);
    }

    @Override
    public void queryWmsStatusTest(String orderNo) {
        CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(orderNo);
        CainiaoShopInfo cainiaoShopInfo=cainiaoShopInfoRepository.findByShopId(order.getShopId());
        caiNiaoSupport.queryWmsStatus(order.getLpCode(),cainiaoShopInfo);
    }

    @Override
    public void queryLogisticsDetailTest(String orderNo) {
        CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(orderNo);
        CainiaoShopInfo cainiaoShopInfo=cainiaoShopInfoRepository.findByShopId(order.getShopId());
        caiNiaoSupport.queryLogisticsDetail(order,cainiaoShopInfo);
    }
}