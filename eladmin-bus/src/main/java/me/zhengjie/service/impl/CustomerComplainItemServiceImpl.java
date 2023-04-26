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

import me.zhengjie.domain.CustomerComplainItem;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.CustomerComplainItemRepository;
import me.zhengjie.service.CustomerComplainItemService;
import me.zhengjie.service.dto.CustomerComplainItemDto;
import me.zhengjie.service.dto.CustomerComplainItemQueryCriteria;
import me.zhengjie.service.mapstruct.CustomerComplainItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
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
* @date 2021-12-15
**/
@Service
@RequiredArgsConstructor
public class CustomerComplainItemServiceImpl implements CustomerComplainItemService {

    private final CustomerComplainItemRepository customerComplainItemRepository;
    private final CustomerComplainItemMapper customerComplainItemMapper;

    @Override
    public Map<String,Object> queryAll(CustomerComplainItemQueryCriteria criteria, Pageable pageable){
        Page<CustomerComplainItem> page = customerComplainItemRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(customerComplainItemMapper::toDto));
    }

    @Override
    public List<CustomerComplainItemDto> queryAll(CustomerComplainItemQueryCriteria criteria){
        return customerComplainItemMapper.toDto(customerComplainItemRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public CustomerComplainItemDto findById(Long id) {
        CustomerComplainItem customerComplainItem = customerComplainItemRepository.findById(id).orElseGet(CustomerComplainItem::new);
        ValidationUtil.isNull(customerComplainItem.getId(),"CustomerComplainItem","id",id);
        return customerComplainItemMapper.toDto(customerComplainItem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerComplainItemDto create(CustomerComplainItem resources) {
        Snowflake snowflake = IdUtil.createSnowflake(1, 1);
        resources.setId(snowflake.nextId());
        return customerComplainItemMapper.toDto(customerComplainItemRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CustomerComplainItem resources) {
        CustomerComplainItem customerComplainItem = customerComplainItemRepository.findById(resources.getId()).orElseGet(CustomerComplainItem::new);
        ValidationUtil.isNull( customerComplainItem.getId(),"CustomerComplainItem","id",resources.getId());
        customerComplainItem.copy(resources);
        customerComplainItemRepository.save(customerComplainItem);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            customerComplainItemRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<CustomerComplainItemDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (CustomerComplainItemDto customerComplainItem : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("客诉ID", customerComplainItem.getComplainId());
            map.put("条码", customerComplainItem.getBarCode());
            map.put(" goodsName",  customerComplainItem.getGoodsName());
            map.put("商品状态", customerComplainItem.getGoodsStatus());
            map.put("商品数量", customerComplainItem.getQty());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<CustomerComplainItem> findByCompId(Long compId) {
        return customerComplainItemRepository.findByComplainId(compId);
    }
}
