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

import me.zhengjie.domain.OrderHold;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.OrderHoldRepository;
import me.zhengjie.service.OrderHoldService;
import me.zhengjie.service.dto.OrderHoldDto;
import me.zhengjie.service.dto.OrderHoldQueryCriteria;
import me.zhengjie.service.mapstruct.OrderHoldMapper;
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
* @author luob
* @date 2021-12-30
**/
@CacheConfig(cacheNames = "orderHold")
@Service
@RequiredArgsConstructor
public class OrderHoldServiceImpl implements OrderHoldService {

    private final OrderHoldRepository orderHoldRepository;
    private final OrderHoldMapper orderHoldMapper;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Map<String,Object> queryAll(OrderHoldQueryCriteria criteria, Pageable pageable){
        Page<OrderHold> page = orderHoldRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(orderHoldMapper::toDto));
    }

    @Override
    public List<OrderHoldDto> queryAll(OrderHoldQueryCriteria criteria){
        return orderHoldMapper.toDto(orderHoldRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public OrderHoldDto findById(Long id) {
        OrderHold orderHold = orderHoldRepository.findById(id).orElseGet(OrderHold::new);
        ValidationUtil.isNull(orderHold.getId(),"OrderHold","id",id);
        return orderHoldMapper.toDto(orderHold);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderHoldDto create(OrderHold resources) {
        redisUtils.del("orderHold::code:" + resources.getShopId() + resources.getPlatformCode());
        return orderHoldMapper.toDto(orderHoldRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(OrderHold resources) {
        OrderHold orderHold = orderHoldRepository.findById(resources.getId()).orElseGet(OrderHold::new);
        ValidationUtil.isNull( orderHold.getId(),"OrderHold","id",resources.getId());
        orderHold.copy(resources);
        redisUtils.del("orderHold::code:" + resources.getShopId() + resources.getPlatformCode());
        orderHoldRepository.save(orderHold);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            OrderHoldDto byId = findById(id);
            redisUtils.del("orderHold::code:" + byId.getShopId() + byId.getPlatformCode());
            orderHoldRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<OrderHoldDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (OrderHoldDto orderHold : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("名称", orderHold.getName());
            map.put("状态", orderHold.getStatus());
            map.put("类型", orderHold.getType());
            map.put("客户ID", orderHold.getCustomersId());
            map.put("店铺ID", orderHold.getShopId());
            map.put("电商平台代码", orderHold.getPlatformCode());
            map.put("开始时间", orderHold.getStartTime());
            map.put("结束时间", orderHold.getEndTime());
            map.put("创建人", orderHold.getCreateBy());
            map.put("创建时间", orderHold.getCreateTime());
            map.put("更新者", orderHold.getUpdateBy());
            map.put("更新时间", orderHold.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Cacheable(key = "'code:' + #p0 + #p1")
    @Override
    public OrderHold queryOrderHold(Long shopId, String platformCode) {
        return orderHoldRepository.findOrderHold(shopId, platformCode);
    }
}