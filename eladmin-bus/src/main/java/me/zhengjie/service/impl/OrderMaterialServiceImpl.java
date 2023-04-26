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

import me.zhengjie.domain.OrderMaterial;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.OrderMaterialRepository;
import me.zhengjie.service.OrderMaterialService;
import me.zhengjie.service.dto.OrderMaterialDto;
import me.zhengjie.service.dto.OrderMaterialQueryCriteria;
import me.zhengjie.service.mapstruct.OrderMaterialMapper;
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
* @date 2022-06-22
**/
@Service
@RequiredArgsConstructor
public class OrderMaterialServiceImpl implements OrderMaterialService {

    private final OrderMaterialRepository orderMaterialRepository;
    private final OrderMaterialMapper orderMaterialMapper;

    @Override
    public Map<String,Object> queryAll(OrderMaterialQueryCriteria criteria, Pageable pageable){
        Page<OrderMaterial> page = orderMaterialRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(orderMaterialMapper::toDto));
    }

    @Override
    public List<OrderMaterialDto> queryAll(OrderMaterialQueryCriteria criteria){
        return orderMaterialMapper.toDto(orderMaterialRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public OrderMaterialDto findById(Long id) {
        OrderMaterial orderMaterial = orderMaterialRepository.findById(id).orElseGet(OrderMaterial::new);
        ValidationUtil.isNull(orderMaterial.getId(),"OrderMaterial","id",id);
        return orderMaterialMapper.toDto(orderMaterial);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderMaterialDto create(OrderMaterial resources) {
        return orderMaterialMapper.toDto(orderMaterialRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(OrderMaterial resources) {
        OrderMaterial orderMaterial = orderMaterialRepository.findById(resources.getId()).orElseGet(OrderMaterial::new);
        ValidationUtil.isNull( orderMaterial.getId(),"OrderMaterial","id",resources.getId());
        orderMaterial.copy(resources);
        orderMaterialRepository.save(orderMaterial);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            orderMaterialRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<OrderMaterialDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (OrderMaterialDto orderMaterial : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单ID", orderMaterial.getOrderId());
            map.put("客户ID", orderMaterial.getCustomersId());
            map.put("店铺ID", orderMaterial.getShopId());
            map.put("客户名称", orderMaterial.getCustomersName());
            map.put("店铺名称", orderMaterial.getShopName());
            map.put("订单号", orderMaterial.getOrderNo());
            map.put("耗材编码", orderMaterial.getMaterialCode());
            map.put("耗材名称", orderMaterial.getMaterialName());
            map.put("使用数量", orderMaterial.getQty());
            map.put("创建人", orderMaterial.getCreateBy());
            map.put("创建时间", orderMaterial.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void createAll(List<OrderMaterial> orderMaterialList) {
        if (CollectionUtils.isNotEmpty(orderMaterialList)) {
            orderMaterialRepository.saveAll(orderMaterialList);
        }
    }

    @Override
    public List<OrderMaterial> queryByOrderId(Long orderId) {
        return orderMaterialRepository.findByOrderId(orderId);
    }

    @Override
    public OrderMaterial queryByMailNo(String mailNo) {
        return orderMaterialRepository.findByLogisticsNo(mailNo);
    }
}