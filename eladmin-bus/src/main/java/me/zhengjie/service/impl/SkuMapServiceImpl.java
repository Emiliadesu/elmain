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

import me.zhengjie.domain.SkuMap;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.SkuMapRepository;
import me.zhengjie.service.SkuMapService;
import me.zhengjie.service.dto.SkuMapDto;
import me.zhengjie.service.dto.SkuMapQueryCriteria;
import me.zhengjie.service.mapstruct.SkuMapMapper;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;

import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author wangm
* @date 2021-04-05
**/
@Service
@RequiredArgsConstructor
public class SkuMapServiceImpl implements SkuMapService {

    private final SkuMapRepository skuMapRepository;
    private final SkuMapMapper skuMapMapper;

    @Override
    public Map<String,Object> queryAll(SkuMapQueryCriteria criteria, Pageable pageable){
        Page<SkuMap> page = skuMapRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(skuMapMapper::toDto));
    }

    @Override
    public List<SkuMapDto> queryAll(SkuMapQueryCriteria criteria){
        return skuMapMapper.toDto(skuMapRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public SkuMapDto findById(Long id) {
        SkuMap skuMap = skuMapRepository.findById(id).orElseGet(SkuMap::new);
        ValidationUtil.isNull(skuMap.getId(),"SkuMap","id",id);
        return skuMapMapper.toDto(skuMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SkuMapDto create(SkuMap resources) {
        return skuMapMapper.toDto(skuMapRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SkuMap resources) {
        SkuMap skuMap = skuMapRepository.findById(resources.getId()).orElseGet(SkuMap::new);
        ValidationUtil.isNull( skuMap.getId(),"SkuMap","id",resources.getId());
        skuMap.copy(resources);
        skuMapRepository.save(skuMap);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            skuMapRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<SkuMapDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (SkuMapDto skuMap : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("商家sku", skuMap.getOwnerSku());
            map.put("仓库sku", skuMap.getWarhouseSku());
            map.put("商家", skuMap.getCustomer().getCustName());
            map.put("创建时间", skuMap.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public SkuMap queryByOwnerSkuAndChannel(String skuNo, String channel) {
        SkuMap skuMap=new SkuMap();
        skuMap.setOwnerSku(skuNo);
        skuMap.setChannel(channel);
        Optional<SkuMap> skuMapOpt = skuMapRepository.findOne(Example.of(skuMap));
        if (skuMapOpt.isPresent())
            return skuMapOpt.get();
        return null;
    }
}
