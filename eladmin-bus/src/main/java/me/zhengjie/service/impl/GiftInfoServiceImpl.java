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
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.repository.GiftDetailsRepository;
import me.zhengjie.service.BaseSkuService;
import me.zhengjie.service.GiftDetailsService;
import me.zhengjie.service.GiftLogService;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.GiftInfoRepository;
import me.zhengjie.service.GiftInfoService;
import me.zhengjie.service.dto.GiftInfoDto;
import me.zhengjie.service.dto.GiftInfoQueryCriteria;
import me.zhengjie.service.mapstruct.GiftInfoMapper;
import me.zhengjie.utils.enums.BooleanEnum;
import me.zhengjie.utils.enums.CBGoodsStatusEnum;
import me.zhengjie.utils.enums.InBoundStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
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
* @date 2021-12-27
**/
@CacheConfig(cacheNames = "gift")
@Service
@RequiredArgsConstructor
public class GiftInfoServiceImpl implements GiftInfoService {

    private final GiftInfoRepository giftInfoRepository;
    private final GiftInfoMapper giftInfoMapper;
    private final GiftDetailsRepository giftDetailsRepository;

    @Autowired
    private GiftDetailsService giftDetailsService;

    @Autowired
    private GiftLogService giftLogService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Map<String,Object> queryAll(GiftInfoQueryCriteria criteria, Pageable pageable){
        Page<GiftInfo> page = giftInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(giftInfoMapper::toDto));
    }

    @Override
    public List<GiftInfoDto> queryAll(GiftInfoQueryCriteria criteria){
        return giftInfoMapper.toDto(giftInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public GiftInfoDto findById(Long id) {
        GiftInfo giftInfo = giftInfoRepository.findById(id).orElseGet(GiftInfo::new);
        ValidationUtil.isNull(giftInfo.getId(),"GiftInfo","id",id);
        return giftInfoMapper.toDto(giftInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GiftInfoDto create(GiftInfo resources) {
        // 全部赠品，随单只能有一个，有了随单之后，就不能有随SKU，有了随SKU就不能有随单
        List<GiftInfo> exist1 = queryByShopIdAndGiftIdWithOrder(resources.getShopId(), resources.getGiftId());
        if (CollectionUtils.isNotEmpty(exist1))
            throw new BadRequestException("该赠品已存在一个随单赠品，不能绑定其他");
        List<GiftInfo> exist2 = queryByShopIdAndGiftIdWithSKU(resources.getShopId(), resources.getGiftId());
        if (CollectionUtils.isNotEmpty(exist2) && StringUtils.equals("1", resources.getBindingType()))
            throw new BadRequestException("该赠品已存在一个随SKU,无法再绑定随单");
        GiftInfo exist3 = queryByShopIdAndGiftIdAndSkuIdWithSKU(resources.getShopId(), resources.getGiftId(), resources.getSkuId());
        if (exist3 != null)
            throw new BadRequestException("该赠品已存在相同主品SKU");
        //添加启动状态
        BaseSku giftSku = baseSkuService.queryById(resources.getGiftId());
        resources.setGiftNo(giftSku.getGoodsNo());
        resources.setGiftCode(giftSku.getBarCode());
        resources.setGiftName(giftSku.getGoodsName());

        if (resources.getSkuId() != null) {
            BaseSku mainSku = baseSkuService.queryById(resources.getSkuId());
            resources.setMainNo(mainSku.getGoodsNo());
            resources.setMainCode(mainSku.getBarCode());
            resources.setMainName(mainSku.getGoodsName());
        }

        GiftInfo giftInfo=giftInfoRepository.save(resources);
        redisUtils.del("gift::sgId:" + resources.getShopId() + resources.getSkuId());
        return giftInfoMapper.toDto(giftInfo);
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(GiftInfo resources) {
        GiftInfo giftInfo = giftInfoRepository.findById(resources.getId()).orElseGet(GiftInfo::new);
        ValidationUtil.isNull( giftInfo.getId(),"GiftInfo","id",resources.getId());
        giftInfo.copy(resources);
        giftInfoRepository.save(giftInfo);
        redisUtils.del("gift::sgId:" + giftInfo.getShopId() + giftInfo.getSkuId());
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            GiftInfoDto byId = findById(id);
            redisUtils.del("gift::sgId:" + byId.getShopId() + byId.getSkuId());
            giftInfoRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<GiftInfoDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (GiftInfoDto giftInfo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("绑定类型", giftInfo.getBindingType());
            map.put("生效时间", giftInfo.getOpenTime());
            map.put("赠品条码", giftInfo.getGiftCode());
            map.put("赠品名称", giftInfo.getGiftName());
            map.put("绑定SKU", giftInfo.getSkuId());
//            map.put("创建人", giftInfo.getCreateBy());
//            map.put("创建时间", giftInfo.getCreateTime());
//            map.put("修改人", giftInfo.getUpdateBy());
//            map.put("修改时间", giftInfo.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public Object queryByIdWithDetails(Long id) {
        GiftInfo giftInfo = giftInfoRepository.findById(id).orElseGet(GiftInfo::new);
        return giftInfo;
    }

    @Cacheable(key = "'sgId:' + #p0 + #p1")
    @Override
    public List<GiftInfo> queryByShopIdAndSkuId(Long shopId, Long goodsId) {
        return giftInfoRepository.findByShopIdAndSkuIdAndBindingType(shopId, goodsId, "2");// 随SKU
    }

    @Cacheable(key = "'sId:' + #p0")
    @Override
    public List<GiftInfo> queryByShopId(Long shopId) {
        return giftInfoRepository.findByShopIdAndBindingType(shopId, "1");// 随单
    }

    @Override
    public List<GiftInfo> queryByShopIdAndGiftIdWithOrder(Long shopId, Long giftId) {
        return giftInfoRepository.findByShopIdAndGiftIdAndBindingType(shopId, giftId, "1");// 随单
    }

    @Override
    public List<GiftInfo> queryByShopIdAndGiftIdWithSKU(Long shopId, Long giftId) {
        return giftInfoRepository.findByShopIdAndGiftIdAndBindingType(shopId, giftId, "2");// 随SKU
    }

    @Override
    public GiftInfo queryByShopIdAndGiftIdAndSkuIdWithSKU(Long shopId, Long giftId, Long skuId) {
        return giftInfoRepository.findByShopIdAndGiftIdAndSkuIdAndBindingType(shopId, giftId, skuId, "2");// 随SKU
    }
}