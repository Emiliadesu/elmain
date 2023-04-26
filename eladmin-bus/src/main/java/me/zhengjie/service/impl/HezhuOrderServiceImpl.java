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

import me.zhengjie.domain.HezhuOrder;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.HezhuOrderRepository;
import me.zhengjie.service.HezhuOrderService;
import me.zhengjie.service.dto.HezhuOrderDto;
import me.zhengjie.service.dto.HezhuOrderQueryCriteria;
import me.zhengjie.service.mapstruct.HezhuOrderMapper;
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
* @date 2021-08-26
**/
@Service
@RequiredArgsConstructor
public class HezhuOrderServiceImpl implements HezhuOrderService {

    private final HezhuOrderRepository hezhuOrderRepository;
    private final HezhuOrderMapper hezhuOrderMapper;

    @Override
    public Map<String,Object> queryAll(HezhuOrderQueryCriteria criteria, Pageable pageable){
        Page<HezhuOrder> page = hezhuOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(hezhuOrderMapper::toDto));
    }

    @Override
    public List<HezhuOrderDto> queryAll(HezhuOrderQueryCriteria criteria){
        return hezhuOrderMapper.toDto(hezhuOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public HezhuOrderDto findById(Long id) {
        HezhuOrder hezhuOrder = hezhuOrderRepository.findById(id).orElseGet(HezhuOrder::new);
        ValidationUtil.isNull(hezhuOrder.getId(),"HezhuOrder","id",id);
        return hezhuOrderMapper.toDto(hezhuOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HezhuOrderDto create(HezhuOrder resources) {
        return hezhuOrderMapper.toDto(hezhuOrderRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(HezhuOrder resources) {
        HezhuOrder hezhuOrder = hezhuOrderRepository.findById(resources.getId()).orElseGet(HezhuOrder::new);
        ValidationUtil.isNull( hezhuOrder.getId(),"HezhuOrder","id",resources.getId());
        hezhuOrder.copy(resources);
        hezhuOrderRepository.save(hezhuOrder);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            hezhuOrderRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<HezhuOrderDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (HezhuOrderDto hezhuOrder : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单ID", hezhuOrder.getOrderId());
            map.put("单据编号", hezhuOrder.getOrderNo());
            map.put("关联单据号", hezhuOrder.getRefOrderNo());
            map.put("关联单据类型", hezhuOrder.getRefOrderType());
            map.put("创建者", hezhuOrder.getCreateBy());
            map.put("创建时间", hezhuOrder.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}