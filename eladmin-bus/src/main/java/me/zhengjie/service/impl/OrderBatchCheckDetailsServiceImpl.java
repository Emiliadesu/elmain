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

import me.zhengjie.domain.OrderBatchCheckDetails;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.OrderBatchCheckDetailsRepository;
import me.zhengjie.service.OrderBatchCheckDetailsService;
import me.zhengjie.service.dto.OrderBatchCheckDetailsDto;
import me.zhengjie.service.dto.OrderBatchCheckDetailsQueryCriteria;
import me.zhengjie.service.mapstruct.OrderBatchCheckDetailsMapper;
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
public class OrderBatchCheckDetailsServiceImpl implements OrderBatchCheckDetailsService {

    private final OrderBatchCheckDetailsRepository orderBatchCheckDetailsRepository;
    private final OrderBatchCheckDetailsMapper orderBatchCheckDetailsMapper;

    @Override
    public Map<String,Object> queryAll(OrderBatchCheckDetailsQueryCriteria criteria, Pageable pageable){
        Page<OrderBatchCheckDetails> page = orderBatchCheckDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(orderBatchCheckDetailsMapper::toDto));
    }

    @Override
    public List<OrderBatchCheckDetailsDto> queryAll(OrderBatchCheckDetailsQueryCriteria criteria){
        return orderBatchCheckDetailsMapper.toDto(orderBatchCheckDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public OrderBatchCheckDetailsDto findById(Long id) {
        OrderBatchCheckDetails orderBatchCheckDetails = orderBatchCheckDetailsRepository.findById(id).orElseGet(OrderBatchCheckDetails::new);
        ValidationUtil.isNull(orderBatchCheckDetails.getId(),"OrderBatchCheckDetails","id",id);
        return orderBatchCheckDetailsMapper.toDto(orderBatchCheckDetails);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderBatchCheckDetailsDto create(OrderBatchCheckDetails resources) {
        return orderBatchCheckDetailsMapper.toDto(orderBatchCheckDetailsRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(OrderBatchCheckDetails resources) {
        OrderBatchCheckDetails orderBatchCheckDetails = orderBatchCheckDetailsRepository.findById(resources.getId()).orElseGet(OrderBatchCheckDetails::new);
        ValidationUtil.isNull( orderBatchCheckDetails.getId(),"OrderBatchCheckDetails","id",resources.getId());
        orderBatchCheckDetails.copy(resources);
        orderBatchCheckDetailsRepository.save(orderBatchCheckDetails);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            orderBatchCheckDetailsRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<OrderBatchCheckDetailsDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (OrderBatchCheckDetailsDto orderBatchCheckDetails : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("波次编号", orderBatchCheckDetails.getWaveNo());
            map.put("WMS单号", orderBatchCheckDetails.getWmsNo());
            map.put("订单号", orderBatchCheckDetails.getOrderNo());
            map.put("运单号", orderBatchCheckDetails.getLogisticsNo());
            map.put("波次单状态", orderBatchCheckDetails.getLineStatus());
            map.put("状态", orderBatchCheckDetails.getOrderStatus());
            map.put("订单序号", orderBatchCheckDetails.getSeqNo());
            map.put("批量质检ID", orderBatchCheckDetails.getCheckId());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}