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

import me.zhengjie.domain.DewuDeclarePushItem;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.DewuDeclarePushItemRepository;
import me.zhengjie.service.DewuDeclarePushItemService;
import me.zhengjie.service.dto.DewuDeclarePushItemDto;
import me.zhengjie.service.dto.DewuDeclarePushItemQueryCriteria;
import me.zhengjie.service.mapstruct.DewuDeclarePushItemMapper;
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
* @date 2023-03-21
**/
@Service
@RequiredArgsConstructor
public class DewuDeclarePushItemServiceImpl implements DewuDeclarePushItemService {

    private final DewuDeclarePushItemRepository dewuDeclarePushItemRepository;
    private final DewuDeclarePushItemMapper dewuDeclarePushItemMapper;

    @Override
    public Map<String,Object> queryAll(DewuDeclarePushItemQueryCriteria criteria, Pageable pageable){
        Page<DewuDeclarePushItem> page = dewuDeclarePushItemRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(dewuDeclarePushItemMapper::toDto));
    }

    @Override
    public List<DewuDeclarePushItemDto> queryAll(DewuDeclarePushItemQueryCriteria criteria){
        return dewuDeclarePushItemMapper.toDto(dewuDeclarePushItemRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public DewuDeclarePushItemDto findById(Long id) {
        DewuDeclarePushItem dewuDeclarePushItem = dewuDeclarePushItemRepository.findById(id).orElseGet(DewuDeclarePushItem::new);
        ValidationUtil.isNull(dewuDeclarePushItem.getId(),"DewuDeclarePushItem","id",id);
        return dewuDeclarePushItemMapper.toDto(dewuDeclarePushItem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DewuDeclarePushItemDto create(DewuDeclarePushItem resources) {
        return dewuDeclarePushItemMapper.toDto(dewuDeclarePushItemRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DewuDeclarePushItem resources) {
        DewuDeclarePushItem dewuDeclarePushItem = dewuDeclarePushItemRepository.findById(resources.getId()).orElseGet(DewuDeclarePushItem::new);
        ValidationUtil.isNull( dewuDeclarePushItem.getId(),"DewuDeclarePushItem","id",resources.getId());
        dewuDeclarePushItem.copy(resources);
        dewuDeclarePushItemRepository.save(dewuDeclarePushItem);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            dewuDeclarePushItemRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<DewuDeclarePushItemDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DewuDeclarePushItemDto dewuDeclarePushItem : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单ID", dewuDeclarePushItem.getOrderId());
            map.put("订单号", dewuDeclarePushItem.getOrderNo());
            map.put("货号", dewuDeclarePushItem.getGoodsNo());
            map.put("商品名称", dewuDeclarePushItem.getGoodsName());
            map.put("数量", dewuDeclarePushItem.getQty());
            map.put("申报单价", dewuDeclarePushItem.getDeclarePrice());
            map.put("申报总价", dewuDeclarePushItem.getDeclareAmount());
            map.put("商品条码", dewuDeclarePushItem.getBarCode());
            map.put("计量单位", dewuDeclarePushItem.getUnit());
            map.put("税费(单个商品税费)", dewuDeclarePushItem.getTaxAmount());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<DewuDeclarePushItem> queryByOrderId(Long orderId) {
        return dewuDeclarePushItemRepository.queryAllByOrderId(orderId);
    }
}