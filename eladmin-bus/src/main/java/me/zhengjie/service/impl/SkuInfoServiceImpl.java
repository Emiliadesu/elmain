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

import me.zhengjie.domain.SkuInfo;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.SkuInfoRepository;
import me.zhengjie.service.SkuInfoService;
import me.zhengjie.service.dto.SkuInfoDto;
import me.zhengjie.service.dto.SkuInfoQueryCriteria;
import me.zhengjie.service.mapstruct.SkuInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;
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
* @date 2021-04-15
**/
@Service
@RequiredArgsConstructor
public class SkuInfoServiceImpl implements SkuInfoService {

    private final SkuInfoRepository skuInfoRepository;
    private final SkuInfoMapper skuInfoMapper;

    @Override
    public Map<String,Object> queryAll(SkuInfoQueryCriteria criteria, Pageable pageable){
        Page<SkuInfo> page = skuInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(skuInfoMapper::toDto));
    }

    @Override
    public List<SkuInfoDto> queryAll(SkuInfoQueryCriteria criteria){
        return skuInfoMapper.toDto(skuInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public SkuInfoDto findById(Long id) {
        SkuInfo skuInfo = skuInfoRepository.findById(id).orElseGet(SkuInfo::new);
        ValidationUtil.isNull(skuInfo.getId(),"SkuInfo","id",id);
        return skuInfoMapper.toDto(skuInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SkuInfoDto create(SkuInfo resources) {
        return skuInfoMapper.toDto(skuInfoRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SkuInfo resources) {
        SkuInfo skuInfo = skuInfoRepository.findById(resources.getId()).orElseGet(SkuInfo::new);
        ValidationUtil.isNull( skuInfo.getId(),"SkuInfo","id",resources.getId());
        skuInfo.copy(resources);
        skuInfoRepository.save(skuInfo);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            skuInfoRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<SkuInfoDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (SkuInfoDto skuInfo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("添加时间", skuInfo.getCreateTime());
            map.put("客户ID", skuInfo.getCustomersId());
            map.put("店铺ID", skuInfo.getShopId());
            map.put("商品编码", skuInfo.getGoodsCode());
            map.put("条形码", skuInfo.getBarCode());
            map.put("商品名称", skuInfo.getGoodsName());
            map.put("是否SN管理", skuInfo.getSnControl());
            map.put("长", skuInfo.getSaleL());
            map.put("宽", skuInfo.getSaleW());
            map.put("高", skuInfo.getSaleH());
            map.put("重量", skuInfo.getSaleWeight());
            map.put("箱长", skuInfo.getPackL());
            map.put("箱宽", skuInfo.getPackW());
            map.put("箱高", skuInfo.getPackH());
            map.put("箱重", skuInfo.getPackWeight());
            map.put("箱规", skuInfo.getPackNum());
            map.put("创建人", skuInfo.getCreateBy());
            map.put("修改人", skuInfo.getUpdateBy());
            map.put("修改时间", skuInfo.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}