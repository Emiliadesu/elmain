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

import me.zhengjie.domain.OrderHoldDetail;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.OrderHoldDetailRepository;
import me.zhengjie.service.OrderHoldDetailService;
import me.zhengjie.service.dto.OrderHoldDetailDto;
import me.zhengjie.service.dto.OrderHoldDetailQueryCriteria;
import me.zhengjie.service.mapstruct.OrderHoldDetailMapper;
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
* @date 2021-12-30
**/
@Service
@RequiredArgsConstructor
public class OrderHoldDetailServiceImpl implements OrderHoldDetailService {

    private final OrderHoldDetailRepository orderHoldDetailRepository;
    private final OrderHoldDetailMapper orderHoldDetailMapper;

    @Override
    public Map<String,Object> queryAll(OrderHoldDetailQueryCriteria criteria, Pageable pageable){
        Page<OrderHoldDetail> page = orderHoldDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(orderHoldDetailMapper::toDto));
    }

    @Override
    public List<OrderHoldDetailDto> queryAll(OrderHoldDetailQueryCriteria criteria){
        return orderHoldDetailMapper.toDto(orderHoldDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public OrderHoldDetailDto findById(Long id) {
        OrderHoldDetail orderHoldDetail = orderHoldDetailRepository.findById(id).orElseGet(OrderHoldDetail::new);
        ValidationUtil.isNull(orderHoldDetail.getId(),"OrderHoldDetail","id",id);
        return orderHoldDetailMapper.toDto(orderHoldDetail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderHoldDetailDto create(OrderHoldDetail resources) {
        return orderHoldDetailMapper.toDto(orderHoldDetailRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(OrderHoldDetail resources) {
        OrderHoldDetail orderHoldDetail = orderHoldDetailRepository.findById(resources.getId()).orElseGet(OrderHoldDetail::new);
        ValidationUtil.isNull( orderHoldDetail.getId(),"OrderHoldDetail","id",resources.getId());
        orderHoldDetail.copy(resources);
        orderHoldDetailRepository.save(orderHoldDetail);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            orderHoldDetailRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<OrderHoldDetailDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (OrderHoldDetailDto orderHoldDetail : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("holdID", orderHoldDetail.getHoldId());
            map.put("货号", orderHoldDetail.getHoldItem());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}