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

import me.zhengjie.domain.OrderDeliverLog;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.OrderDeliverLogRepository;
import me.zhengjie.service.OrderDeliverLogService;
import me.zhengjie.service.dto.OrderDeliverLogDto;
import me.zhengjie.service.dto.OrderDeliverLogQueryCriteria;
import me.zhengjie.service.mapstruct.OrderDeliverLogMapper;
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
* @date 2021-04-03
**/
@Service
@RequiredArgsConstructor
public class OrderDeliverLogServiceImpl implements OrderDeliverLogService {

    private final OrderDeliverLogRepository orderDeliverLogRepository;
    private final OrderDeliverLogMapper orderDeliverLogMapper;

    @Override
    public Map<String,Object> queryAll(OrderDeliverLogQueryCriteria criteria, Pageable pageable){
        Page<OrderDeliverLog> page = orderDeliverLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(orderDeliverLogMapper::toDto));
    }

    @Override
    public List<OrderDeliverLogDto> queryAll(OrderDeliverLogQueryCriteria criteria){
        return orderDeliverLogMapper.toDto(orderDeliverLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public OrderDeliverLogDto findById(Long id) {
        OrderDeliverLog orderDeliverLog = orderDeliverLogRepository.findById(id).orElseGet(OrderDeliverLog::new);
        ValidationUtil.isNull(orderDeliverLog.getId(),"OrderDeliverLog","id",id);
        return orderDeliverLogMapper.toDto(orderDeliverLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDeliverLogDto create(OrderDeliverLog resources) {
        return orderDeliverLogMapper.toDto(orderDeliverLogRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(OrderDeliverLog resources) {
        OrderDeliverLog orderDeliverLog = orderDeliverLogRepository.findById(resources.getId()).orElseGet(OrderDeliverLog::new);
        ValidationUtil.isNull( orderDeliverLog.getId(),"OrderDeliverLog","id",resources.getId());
        orderDeliverLog.copy(resources);
        orderDeliverLogRepository.save(orderDeliverLog);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            orderDeliverLogRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<OrderDeliverLogDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (OrderDeliverLogDto orderDeliverLog : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("店铺ID", orderDeliverLog.getShopId());
            map.put("扫描人", orderDeliverLog.getUserName());
            map.put("订单号", orderDeliverLog.getOrderNo());
            map.put("运单号", orderDeliverLog.getMailNo());
            map.put("请求报文", orderDeliverLog.getReqMsg());
            map.put("返回报文", orderDeliverLog.getResMsg());
            map.put("执行机器", orderDeliverLog.getHost());
            map.put("花费时间", orderDeliverLog.getCostTime());
            map.put("创建时间", orderDeliverLog.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}