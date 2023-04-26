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

import me.zhengjie.domain.OrderBatchCheck;
import me.zhengjie.exception.EntityExistException;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.OrderBatchCheckRepository;
import me.zhengjie.service.OrderBatchCheckService;
import me.zhengjie.service.dto.OrderBatchCheckDto;
import me.zhengjie.service.dto.OrderBatchCheckQueryCriteria;
import me.zhengjie.service.mapstruct.OrderBatchCheckMapper;
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
* @date 2022-05-04
**/
@Service
@RequiredArgsConstructor
public class OrderBatchCheckServiceImpl implements OrderBatchCheckService {

    private final OrderBatchCheckRepository orderBatchCheckRepository;
    private final OrderBatchCheckMapper orderBatchCheckMapper;

    @Override
    public Map<String,Object> queryAll(OrderBatchCheckQueryCriteria criteria, Pageable pageable){
        Page<OrderBatchCheck> page = orderBatchCheckRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(orderBatchCheckMapper::toDto));
    }

    @Override
    public List<OrderBatchCheckDto> queryAll(OrderBatchCheckQueryCriteria criteria){
        return orderBatchCheckMapper.toDto(orderBatchCheckRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public OrderBatchCheckDto findById(Long id) {
        OrderBatchCheck orderBatchCheck = orderBatchCheckRepository.findById(id).orElseGet(OrderBatchCheck::new);
        ValidationUtil.isNull(orderBatchCheck.getId(),"OrderBatchCheck","id",id);
        return orderBatchCheckMapper.toDto(orderBatchCheck);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderBatchCheckDto create(OrderBatchCheck resources) {
        if(orderBatchCheckRepository.findByWaveNo(resources.getWaveNo()) != null){
            throw new EntityExistException(OrderBatchCheck.class,"wave_no",resources.getWaveNo());
        }
        return orderBatchCheckMapper.toDto(orderBatchCheckRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(OrderBatchCheck resources) {
        OrderBatchCheck orderBatchCheck = orderBatchCheckRepository.findById(resources.getId()).orElseGet(OrderBatchCheck::new);
        ValidationUtil.isNull( orderBatchCheck.getId(),"OrderBatchCheck","id",resources.getId());
        OrderBatchCheck orderBatchCheck1 = null;
        orderBatchCheck1 = orderBatchCheckRepository.findByWaveNo(resources.getWaveNo());
        if(orderBatchCheck1 != null && !orderBatchCheck1.getId().equals(orderBatchCheck.getId())){
            throw new EntityExistException(OrderBatchCheck.class,"wave_no",resources.getWaveNo());
        }
        orderBatchCheck.copy(resources);
        orderBatchCheckRepository.save(orderBatchCheck);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            orderBatchCheckRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<OrderBatchCheckDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (OrderBatchCheckDto orderBatchCheck : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("波次编号", orderBatchCheck.getWaveNo());
            map.put("波次名称", orderBatchCheck.getWaveName());
            map.put("波次单状态", orderBatchCheck.getWaveStatus());
            map.put("状态", orderBatchCheck.getStatus());
            map.put("订单数量", orderBatchCheck.getOrderNum());
            map.put("包材编码", orderBatchCheck.getMaterialCode());
            map.put("包裹重量", orderBatchCheck.getPackWeight());
            map.put("波次创建时间", orderBatchCheck.getWaveCreateTime());
            map.put("波次创建人", orderBatchCheck.getWaveCreateBy());
            map.put("创建人", orderBatchCheck.getCreateBy());
            map.put("创建时间", orderBatchCheck.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}