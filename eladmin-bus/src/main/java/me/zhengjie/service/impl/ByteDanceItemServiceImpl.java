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

import me.zhengjie.domain.ByteDanceItem;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.ByteDanceItemRepository;
import me.zhengjie.service.ByteDanceItemService;
import me.zhengjie.service.dto.ByteDanceItemDto;
import me.zhengjie.service.dto.ByteDanceItemQueryCriteria;
import me.zhengjie.service.mapstruct.ByteDanceItemMapper;
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
* @author wangm
* @date 2021-03-21
**/
@Service
@RequiredArgsConstructor
public class ByteDanceItemServiceImpl implements ByteDanceItemService {

    private final ByteDanceItemRepository byteDanceItemRepository;
    private final ByteDanceItemMapper byteDanceItemMapper;

    @Override
    public Map<String,Object> queryAll(ByteDanceItemQueryCriteria criteria, Pageable pageable){
        Page<ByteDanceItem> page = byteDanceItemRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(byteDanceItemMapper::toDto));
    }

    @Override
    public List<ByteDanceItemDto> queryAll(ByteDanceItemQueryCriteria criteria){
        return byteDanceItemMapper.toDto(byteDanceItemRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public ByteDanceItemDto findById(Long id) {
        ByteDanceItem byteDanceItem = byteDanceItemRepository.findById(id).orElseGet(ByteDanceItem::new);
        ValidationUtil.isNull(byteDanceItem.getId(),"ByteDanceItem","id",id);
        return byteDanceItemMapper.toDto(byteDanceItem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ByteDanceItemDto create(ByteDanceItem resources) {
        return byteDanceItemMapper.toDto(byteDanceItemRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ByteDanceItem resources) {
        ByteDanceItem byteDanceItem = byteDanceItemRepository.findById(resources.getId()).orElseGet(ByteDanceItem::new);
        ValidationUtil.isNull( byteDanceItem.getId(),"ByteDanceItem","id",resources.getId());
        byteDanceItem.copy(resources);
        byteDanceItemRepository.save(byteDanceItem);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            byteDanceItemRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<ByteDanceItemDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ByteDanceItemDto byteDanceItem : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("父订单", byteDanceItem.getOrder());
            map.put("订单明细序号", byteDanceItem.getGnum());
            map.put("电商平台自己的sku id", byteDanceItem.getEcpSkuId());
            map.put("商家货号", byteDanceItem.getItemNo());
            map.put("商品规格型号", byteDanceItem.getGModel());
            map.put("条码", byteDanceItem.getBarCode());
            map.put("商品数量", byteDanceItem.getQty());
            map.put("单价（商品不含税价，不含运费保费）", byteDanceItem.getPrice());
            map.put("总价", byteDanceItem.getTotalPrice());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
